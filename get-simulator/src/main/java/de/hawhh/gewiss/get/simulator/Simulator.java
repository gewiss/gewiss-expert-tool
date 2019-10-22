package de.hawhh.gewiss.get.simulator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import de.hawhh.gewiss.get.core.calc.EnergyCalculator;
import de.hawhh.gewiss.get.core.input.HeatingSystemExchangeRate;
import de.hawhh.gewiss.get.core.input.InputValidationException;
import de.hawhh.gewiss.get.core.input.Modifier;
import de.hawhh.gewiss.get.core.input.SimulationParameter;
import de.hawhh.gewiss.get.core.model.Building;
import de.hawhh.gewiss.get.core.model.HeatingType;
import de.hawhh.gewiss.get.core.output.BuildingInformation;
import de.hawhh.gewiss.get.core.output.SimulationOutput;
import de.hawhh.gewiss.get.core.output.SimulationResult;
import de.hawhh.gewiss.get.simulator.db.dao.BuildingDAO;
import de.hawhh.gewiss.get.simulator.db.dao.SQLiteBuildingDAO;
import de.hawhh.gewiss.get.simulator.model.ScoredBuilding;
import de.hawhh.gewiss.get.simulator.renovation.IRenovationStrategy;
import de.hawhh.gewiss.get.simulator.renovation.RenovationHeatExchangeRateStrategy;
import de.hawhh.gewiss.get.simulator.scoring.BuildingAgeFactor;
import de.hawhh.gewiss.get.simulator.scoring.CO2EmissionFactor;
import de.hawhh.gewiss.get.simulator.scoring.CO2EmissionSquareMeterFactor;
import de.hawhh.gewiss.get.simulator.scoring.ScoringMethod;

import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * The main simulator class.
 *
 * @author Thomas Preisler, Antony Sotirov
 */
public class Simulator extends Observable {

    private final static Logger LOGGER = Logger.getLogger(Simulator.class.getName());

    private final BuildingDAO buildingDAO;
    private final EnergyCalculator energyCalculator;
    private final Random randomGenerator;

    public Simulator() {
        this.buildingDAO = new SQLiteBuildingDAO();
        this.energyCalculator = EnergyCalculator.getInstance();
        this.randomGenerator = new Random();
    }

    /**
     * Main simulation method. Performs the actual simulation which is carried out in a discrete manner (one simulation step equals one year). For each simulation step
     * (year) the renovation score is calculated for each building. Based on these scores the renovation strategy is applied and the selected buildings are renovated.
     * Afterwards the current heat demand of each building is calculated.
     *
     * @param parameter the encapusalted simulation parameters
     * @param scoringMethods List of different scoring methods to be applied
     * @param renovationStrategy the selected renovation strategy
     * @param rgSeed seed for the pseudorandom number generator
     * @return
     * @throws de.hawhh.gewiss.get.core.input.InputValidationException
     */
    public SimulationResult simulate(SimulationParameter parameter, List<ScoringMethod> scoringMethods, IRenovationStrategy renovationStrategy, Long rgSeed) throws InputValidationException {
        long startTime = System.currentTimeMillis();

        // if seed is not explicitly set, use system time in nano second to create new "random" seed for each run.
        if (rgSeed == null) {
            rgSeed = System.nanoTime();
        }
        // set the seed for the random generator
        LOGGER.log(Level.INFO, "The seed for the simulation is {0}", rgSeed);
        this.randomGenerator.setSeed(rgSeed);

        // Validate the input factors
        parameter.validate();

        // check if yearly CO2 Factors Data is present and populate list for linear interpolation
        if(parameter.hasCO2FactorsData()) {
            this.energyCalculator.prepCO2YearlyRates(
                parameter.getYearlyCO2Factors(), parameter.getMidCO2Year(), parameter.getFinalCO2Year());
        }

        // Fetch the building from the DB
        List<Building> buildings = fetchBuildings();
        
        // Limit the number of buildings, only for debug purposes!
        //buildings = buildings.subList(0, 20);

        SimulationResult result = new SimulationResult();
        result.setSeed(rgSeed);
        result.setName(parameter.getName());
        
        // Convert parameter to string representation for storage in db
        // Add special support for Guava (Google) datatype for Jackson
        ObjectMapper mapper = new ObjectMapper().registerModule(new GuavaModule());
        // enable toString method of enums to return the value to be mapped
        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        try {
            result.setParameter(mapper.writeValueAsString(parameter));
        } catch (JsonProcessingException ex) {
            Logger.getLogger(Simulator.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Simulation main loop
        for (Integer i = SimulationParameter.FIRST_YEAR; i <= parameter.getStopYear(); i++) {
            final Integer simYear = i;
            LOGGER.log(Level.INFO, "Simulating year {0}", simYear);

            // Don't perform a simulation in the first year; just calculate the status quo (List<SimulationOutput> loop)
            if (i > SimulationParameter.FIRST_YEAR) {
                // Use the stream api to calc the scores in parallel and store the building and scores in a new List
                LOGGER.log(Level.INFO, "Calculating initial scoring values for year {0}", simYear);
                List<ScoredBuilding> scoredBuildings = buildings.stream().parallel().map(building -> {
                    ScoredBuilding scoredBuilding = new ScoredBuilding(building);

                    // Calc and store the scores for all scoring methods
                    scoringMethods.forEach(method -> {
                        Double score = method.calcBaseScore(building, simYear);
                        scoredBuilding.getScores().put(method, score);
                    });

                    return scoredBuilding;
                }).collect(Collectors.toList());

                //LOGGER.info("Scoring Values:");
                //printScoredBuilding(scoredBuildings);

                // Normalize the scoring values
                LOGGER.info("Normalizing initial scoring values");
                normalizeScores(scoredBuildings, scoringMethods);
                //LOGGER.info("Normalized Scoring Values:");
                //printScoredBuilding(scoredBuildings);

                // Combine scores
                LOGGER.info("Combining scoring values");
                combineScores(scoredBuildings);
                //LOGGER.info("Combined Normalized Scoring Values");
                //printScoredBuilding(scoredBuildings);

                // Apply modifiers
                LOGGER.info("Applying modifiers to scoring values");
                if (parameter.getModifiers() != null) {
                    scoredBuildings.parallelStream().forEach(sb -> {
                        parameter.getModifiers().stream().filter(modifier -> modifier.isActive(simYear) && modifier.checkConditions(sb.getBuilding())).forEach(modifier -> {
                            sb.setCombinedScore(sb.getCombinedScore() * modifier.getImpactFactor());
                        });
                    });
                }
                //LOGGER.info("Combined Normalized Scoring Values with Modifiers");
                //printScoredBuilding(scoredBuildings);

                // Sort the List of scored buildings in a descending (reverse) order of scores
                LOGGER.info("Sorting building (desc) according to scoring values");
                scoredBuildings.sort((ScoredBuilding o1, ScoredBuilding o2) -> -o1.getCombinedScore().compareTo(o2.getCombinedScore()));

                // Apply renovation strategy
                renovationStrategy.performRenovation(scoredBuildings, i, this.randomGenerator);
            }


            // Calc heat demand and store results
            List<SimulationOutput> outputs = buildings.stream().parallel().map(building -> {
                SimulationOutput output = new SimulationOutput();
                Double heatDemand = energyCalculator.calcHeatDemand(building);

                Double co2Emission;
                // if requested: grab CO2 factors: use calcCO2Emission(building, year) instead of regular method
                if (energyCalculator.hasYearlyCO2RatesInterpolated()) {
                    co2Emission = energyCalculator.calcCO2Emission(building, simYear);
                } else {
                    co2Emission = energyCalculator.calcCO2Emission(building);
                }

                Double combinedArea = building.getResidentialFloorSpace() + building.getNonResidentialFloorSpace();

                output.setBuildingId(building.getAlkisID());
                output.setHeatDemand(heatDemand);
                if (combinedArea != 0) {
                    output.setHeatDemandM2(heatDemand / combinedArea);
                } else {
                    output.setHeatDemandM2(0.0);
                }
                output.setRenovationLevel(building.getRenovationLevel());

                output.setYear(simYear);
                output.setCo2Emission(co2Emission);

                output.setHeatingType(building.getHeatingType());
                output.setRenovationCost(building.getAccumulatedRenovationCosts());

                output.setResidentialArea(building.getResidentialFloorSpace());
                output.setCombinedArea(combinedArea);

                return output;
            }).collect(Collectors.toList());
            Multimap<Integer, SimulationOutput> mapOutput = ArrayListMultimap.create();
            mapOutput.putAll(simYear, outputs);
            result.getOutput().putAll(mapOutput);

            // Notify observers that the simulation of the year is finished
            setChanged();
            notifyObservers(i);
        }

        // Map Buildings to BuildingInformation and store them in the result object.
        Map<String, BuildingInformation> resultBuildings = buildings.stream().collect(Collectors.toMap(Building::getAlkisID, building -> BuildingInformation.create(building.getAlkisID(), building.getClusterID(), building.getQuarter(), building.getGeometry())));
        result.setBuildings(resultBuildings);

        long endTime = System.currentTimeMillis();
        result.setRunTime(endTime - startTime);

        return result;
    }

    /**
     * Fetch all the building from the database and return them as a List.
     *
     * @return List of buildings
     */
    List<Building> fetchBuildings() {
        return buildingDAO.findAll();
    }

    public static void main(String[] args) throws InputValidationException {
        Integer simStop = 3000;

        String name = "ConsoleRun-" + LocalDateTime.now();

        List<Modifier> modifiers = new ArrayList<>();
        //Modifier mod1 = new Modifier("OneFamilyHousesInAltona", 2020, 2030, 2d);
        //mod1.setTargetQuarters(Arrays.asList("Nienstedten", "Othmarschen", "Ottensen", "Altona-Altstadt", "Rissen", "Blankenese", "Osdorf", "Iserbrook", "Groß Flottbek",
        //        "Bahrenfeld", "Altona-Nord", "Sternschanze", "Sülldorf", "Lurup"));
        //mod1.setTargetBuildingsTypes(Arrays.asList("EFH_C", "EFH_I", "EFH_B", "EFH_G", "EFH_A", "EFH_F", "EFH_J", "EFH_K", "EFH_L", "EFH_H", "EFH_E", "EFH_D"));
        //modifiers.add(mod1);

        // yearlyCO2Factors, midYearCO2, finalYearCO2 == null: use constant CO2 factors data instead
        SimulationParameter parameter = new SimulationParameter(name, simStop, modifiers, null, null, null);

        // Add special support for Guava (Google) datatype for Jackson
        //ObjectMapper mapper = new ObjectMapper().registerModule(new GuavaModule());
        //ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        //writer.writeValue(new File("simparams.json"), parameter);
        List<ScoringMethod> scoringMethods = new ArrayList<>();
        scoringMethods.add(new BuildingAgeFactor());
        scoringMethods.add(new CO2EmissionFactor());
        scoringMethods.add(new CO2EmissionSquareMeterFactor());

        HeatingSystemExchangeRate rate1 = new HeatingSystemExchangeRate(HeatingType.LOW_TEMPERATURE_BOILER, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0,
                100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0);
        HeatingSystemExchangeRate rate2 = new HeatingSystemExchangeRate(HeatingType.DISTRICT_HEAT, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0,
                100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0);
        List<HeatingSystemExchangeRate> rates = new ArrayList<>(Arrays.asList(rate1, rate2));

        IRenovationStrategy renovationStrategy = new RenovationHeatExchangeRateStrategy(2.0, 0.0, rates);

        Simulator simulator = new Simulator();
        SimulationResult result = simulator.simulate(parameter, scoringMethods, renovationStrategy, (long) 821985);

        System.out.println("Simulated scenario " + result.getName() + " in " + result.getRunTime() + " ms");
        result.printHeatDemand();
        result.printRenovationLevelDemand();
    }

    /**
     * Normalizes the scores for all given scored buildings and all applied scoring methods.
     *
     * @param scoredBuildings
     */
    void normalizeScores(List<ScoredBuilding> scoredBuildings, List<ScoringMethod> scoringMethods) {
        scoringMethods.forEach(method -> {
            Double min = scoredBuildings.parallelStream().mapToDouble(sb -> sb.getScores().get(method)).min().getAsDouble();
            Double max = scoredBuildings.parallelStream().mapToDouble(sb -> sb.getScores().get(method)).max().getAsDouble();

            scoredBuildings.parallelStream().forEach(sb -> {
                Double score = sb.getScores().get(method);
                Double nValue = (score - min) / (max - min);

                sb.getScores().put(method, nValue);
            });
        });
    }

    /**
     * Combines the different scoring values for the different scoring methods to one combined value: 1/N * (x1 + x2 + ... xi)
     *
     * @param scoredBuildings
     */
    void combineScores(List<ScoredBuilding> scoredBuildings) {
        scoredBuildings.parallelStream().forEach(sb -> {
            Double value = (1 / ((double) sb.getScores().size())) * (sb.getScores().values().stream().mapToDouble(Double::doubleValue).sum());
            sb.setCombinedScore(value);
        });
    }

    /**
     * Print the given list of scored buildings to the console.
     * 
     * @param scoredBuildings 
     */
    private void printScoredBuilding(List<ScoredBuilding> scoredBuildings) {
        scoredBuildings.parallelStream().forEachOrdered(building -> {
            StringBuilder sb = new StringBuilder();
            sb.append(building.getBuilding().getAlkisID()).append("\t");
            building.getScores().values().forEach(value -> sb.append(value).append("\t"));
            sb.append(building.getCombinedScore());
            System.out.println(sb.toString());
        });
    }
}
