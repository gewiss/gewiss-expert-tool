package de.hawhh.gewiss.get.simulator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import de.hawhh.gewiss.get.core.input.HeatingSystemExchangeRate;
import de.hawhh.gewiss.get.core.input.InputValidationException;
import de.hawhh.gewiss.get.core.input.Modifier;
import de.hawhh.gewiss.get.core.input.SimulationParameter;
import de.hawhh.gewiss.get.core.model.Building;
import de.hawhh.gewiss.get.core.model.EnergySource;
import de.hawhh.gewiss.get.core.model.EnergySource.Type;
import de.hawhh.gewiss.get.core.model.HeatingType;
import de.hawhh.gewiss.get.core.model.RenovationLevel;
import de.hawhh.gewiss.get.core.output.BuildingInformation;
import de.hawhh.gewiss.get.core.output.SimulationOutput;
import de.hawhh.gewiss.get.core.output.SimulationResult;
import de.hawhh.gewiss.get.simulator.baf.IBuildingAgeFactor;
import de.hawhh.gewiss.get.simulator.baf.SimpleBuildingAgeFactor;
import de.hawhh.gewiss.get.simulator.db.dao.BuildingDAO;
import de.hawhh.gewiss.get.simulator.db.dao.EnergySourceDAO;
import de.hawhh.gewiss.get.simulator.db.dao.SQLiteBuildingDAO;
import de.hawhh.gewiss.get.simulator.db.dao.SQLiteEnergySourceDAO;
import de.hawhh.gewiss.get.simulator.model.ScoredBuilding;
import de.hawhh.gewiss.get.simulator.renovation.IRenovationStrategy;
import de.hawhh.gewiss.get.simulator.renovation.RenovationHeatExchangeRateStrategy;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * The main simulator class.
 *
 * @author Thomas Preisler
 */
public class Simulator extends Observable {

    private final static Logger LOGGER = Logger.getLogger(Simulator.class.getName());

    private final BuildingDAO buildingDAO;
    private final EnergySourceDAO energySourceDAO;
    private final Random randomGenerator;

    public Simulator() {
        this.buildingDAO = new SQLiteBuildingDAO();
        this.energySourceDAO = new SQLiteEnergySourceDAO();
        this.randomGenerator = new Random();
    }

    /**
     * Main simulation method. Performs the actual simulation which is carried
     * out in a discrete manner (one simulation step equals one year). For each
     * simulation step (year) the renovation score is calculated for each
     * building. Based on these scores the renovation strategy is applied and
     * the selected buildings are renovated. Afterwards the current heat demand
     * of each building is calculated.
     *
     * @param parameter the encapusalted simulation parameters
     * @param ageFactor the age factor calculation interface/function
     * @param renovationStrategy the selected renovation strategy
     * @param rgSeed seed for the pseudorandom number generator
     * @return
     * @throws de.hawhh.gewiss.get.core.input.InputValidationException
     */
    public SimulationResult simulate(SimulationParameter parameter, IBuildingAgeFactor ageFactor, IRenovationStrategy renovationStrategy, Long rgSeed) throws InputValidationException {
        if (rgSeed != null) {
            // set the seed for the random generator
            this.randomGenerator.setSeed(rgSeed);
        } else {
            // if the seed is not explicitly set, use the system time in nano second to create new "random" seed for each run.
            this.randomGenerator.setSeed(System.nanoTime());
        }

        // Validate the input factors
        parameter.validate();

        // Fetch the building from the DB
        List<Building> buildings = fetchBuildings();

        // Fetch the energy sources from the DB and match them to heating types
        Map<EnergySource.Type, EnergySource> energySources = fetchEnergySources();
        //System.out.println("fetching is DONE!");
        Map<HeatingType, EnergySource> heatingToFuelMap = mapEnergySourcesToHeatingTypes(energySources);

        SimulationResult result = new SimulationResult();
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

            // Don't perform a simulation in the first year, here we just calculate the status quo
            if (i > SimulationParameter.FIRST_YEAR) {
                // Use the stream api to calc the scores in parallel and store the building and scores in a new List
                List<ScoredBuilding> scoredBuildings = buildings.stream().parallel().map(building -> {
                    Double score = calcScore(building, simYear, parameter.getModifiers(), ageFactor);
                    ScoredBuilding scoredBuilding = new ScoredBuilding(building, score);
                    return scoredBuilding;
                }).collect(Collectors.toList());

                // Sort the List of scored buildings in a descending (reverse) order of scores
                Collections.sort(scoredBuildings, (ScoredBuilding o1, ScoredBuilding o2) -> -o1.getScore().compareTo(o2.getScore()));

                // Apply renovation strategy
                renovationStrategy.performRenovation(scoredBuildings, i, this.randomGenerator);
            }
            // Calc heat demand and store results
            List<SimulationOutput> outputs = buildings.stream().parallel().map(building -> {
                SimulationOutput output = new SimulationOutput();

                Double heatDemand = building.calcHeatDemand();

                // Calc co2 emmission for buildings
                Double co2Emission = building.calcCo2Emission(heatDemand, heatingToFuelMap);
//                System.out.println(co2Emission.toString());

                output.setBuildingId(building.getAlkisID());
//                output.setQuarter(building.getQuarter());
//                output.setClusterId(building.getClusterID());
//                output.setGeometry(building.getGeometry());
                output.setHeatDemand(heatDemand);
                output.setHeatDemandM2(heatDemand / (building.getResidentialFloorSpace() + building.getNonResidentialFloorSpace()));
                output.setRenovationLevel(building.getRenovationLevel());

                output.setYear(simYear);
                output.setCo2Emission(co2Emission);

                // TODO: Fill with meaningful values
                output.setHeatingType(building.getHeatingType());
                output.setRenovationCost(0d);

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
        
        return result;
    }

    /**
     * Fetch all the building from the database and return them as a List.
     *
     * @return List of buildings
     */
    private List<Building> fetchBuildings() {
        return buildingDAO.findAll();
    }

    /**
     * Fetch all the energy sources from the database and return them as a map.
     *
     * @return List of buildings
     */
    private Map<EnergySource.Type, EnergySource> fetchEnergySources() {
        return energySourceDAO.findAll();
    }

    /**
     * Map the EnergySources to the corresponding HeatingTypes.
     *
     * @param sources the Map of EnergySources.
     *
     * @return map with heating types as key and energy source as value.
     */
    private Map<HeatingType, EnergySource> mapEnergySourcesToHeatingTypes(Map<EnergySource.Type, EnergySource> sources) {
        Map<HeatingType, EnergySource> heatingTypeToSourceMap = new HashMap<>();

        for (HeatingType type : HeatingType.values()) {

            switch (type) {
                case LOW_TEMPERATURE_BOILER:
                case CONDENSING_BOILER:
                case CONDENSING_BOILER_SOLAR:
                case CONDENSING_BOILER_SOLAR_HEAT_RECOVERY:
                    heatingTypeToSourceMap.put(type, sources.get(Type.NATURAL_GAS));
                    break;

                case DISTRICT_HEAT:
                case DISTRICT_HEAT_HEAT_RECOVERY:
                    heatingTypeToSourceMap.put(type, sources.get(Type.DISTRICT_HEAT));
                    break;

                case PELLETS:
                case PELLETS_SOLAR_HEAT_RECOVERY:
                    heatingTypeToSourceMap.put(type, sources.get(Type.PELLETS));
                    break;

                case HEAT_PUMP_HEAT_RECOVERY:
                    heatingTypeToSourceMap.put(type, sources.get(Type.ELECTRICITY));
                    break;

                default:
                    break;
            }
        }

        return heatingTypeToSourceMap;
    }

    /**
     * Calculate the renovation score for the given building by considering the
     * given modifiers.
     *
     * @param building the given building
     * @param simYear the current year of the simulation
     * @param modifiers the List of Modifiers
     * @param ageFactor interface for the building age factor calculation
     * @return the calculated score
     */
    private Double calcScore(Building building, Integer simYear, List<Modifier> modifiers, IBuildingAgeFactor ageFactor) {
        // if the building has already reached the maximum renovation level, return a zero score
        if (building.getRenovationLevel().equals(RenovationLevel.GOOD_RENOVATION)) {
            return 0d;
        } else {
            // calc the building age factor as initial score
            Double score = ageFactor.calcFactor(building, simYear);

            if (modifiers != null) {
                for (Modifier modifier : modifiers) {
                    if (modifier.isActive(simYear) && modifier.checkConditions(building)) {
                        //LOGGER.log(Level.INFO, "Modifier {0} is active in sim year {1} for building {2}", new Object[]{modifier.getName(), simYear, building.getAlkisID()});
                        score *= modifier.getImpactFactor();
                    }
                }
            }

            //LOGGER.log(Level.INFO, "Return scoring value for {0}/{1}:\t{2}", new Object[]{simYear, building.getAlkisID(), score});
            return score;
        }
    }

    public static void main(String[] args) throws InputValidationException, IOException {
        Integer simStop = 2050;

        String name = "ConsoleRun-" + LocalDateTime.now();

        List<Modifier> modifiers = new ArrayList<>();
        //Modifier mod1 = new Modifier("OneFamilyHousesInAltona", 2020, 2030, 2d);
        //mod1.setTargetQuarters(Arrays.asList("Nienstedten", "Othmarschen", "Ottensen", "Altona-Altstadt", "Rissen", "Blankenese", "Osdorf", "Iserbrook", "Groß Flottbek",
        //        "Bahrenfeld", "Altona-Nord", "Sternschanze", "Sülldorf", "Lurup"));
        //mod1.setTargetBuildingsTypes(Arrays.asList("EFH_C", "EFH_I", "EFH_B", "EFH_G", "EFH_A", "EFH_F", "EFH_J", "EFH_K", "EFH_L", "EFH_H", "EFH_E", "EFH_D"));
        //modifiers.add(mod1);

        SimulationParameter parameter = new SimulationParameter(name, simStop, modifiers);

        // Add special support for Guava (Google) datatype for Jackson
        //ObjectMapper mapper = new ObjectMapper().registerModule(new GuavaModule());
        //ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        //writer.writeValue(new File("simparams.json"), parameter);
        IBuildingAgeFactor buildingAgeFactor = new SimpleBuildingAgeFactor();

        List<HeatingSystemExchangeRate> rates = new ArrayList<>();
        HeatingSystemExchangeRate rate1 = new HeatingSystemExchangeRate(HeatingType.LOW_TEMPERATURE_BOILER, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0,
                100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0);
        HeatingSystemExchangeRate rate2 = new HeatingSystemExchangeRate(HeatingType.DISTRICT_HEAT, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0,
                100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0);
        rates.addAll(Arrays.asList(rate1, rate2));

        IRenovationStrategy renovationStrategy = new RenovationHeatExchangeRateStrategy(1.5, 0.0, rates);

        long startTime = System.currentTimeMillis();
        Simulator simulator = new Simulator();
        SimulationResult result = simulator.simulate(parameter, buildingAgeFactor, renovationStrategy, (long) 821985);
        long endTime = System.currentTimeMillis();

        System.out.println("Simulated scenario " + result.getName() + " in " + (endTime - startTime) + " ms");
        result.printHeatDemand();
        result.printRenovationLevelDemand();
    }
}
