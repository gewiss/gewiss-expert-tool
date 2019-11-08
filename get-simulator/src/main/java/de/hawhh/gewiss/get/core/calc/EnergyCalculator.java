package de.hawhh.gewiss.get.core.calc;

import de.hawhh.gewiss.get.core.input.CO2FactorsData;
import de.hawhh.gewiss.get.core.input.InputValidationException;
import de.hawhh.gewiss.get.core.input.SimulationParameter;
import de.hawhh.gewiss.get.core.model.*;
import de.hawhh.gewiss.get.simulator.db.dao.*;
import org.apache.commons.collections4.map.MultiKeyMap;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper class (singleton) for different energy based calculations (like heat demand, CO2 emissions). It uses maps to
 * cache the values in the database to significantly speed up the look up operations.
 *
 * @author Thomas Preisler, Antony Sotirov
 */
public class EnergyCalculator {
    private final static Logger LOGGER = Logger.getLogger(EnergyCalculator.class.getName());
    private static final EnergyCalculator ourInstance = new EnergyCalculator();
    private Boolean areYearlyCO2RatesInterpolated = false;
    private Integer midCO2Year = null;
    private Integer finalCO2Year = null;

    /**
     * MultiKeyMap for the heat demand at load generation with building type ({@link String}) and {@link RenovationLevel} as key.
     */
    private final MultiKeyMap heatDemandLoadGenerationMap;

    /**
     * MultiKeyMap for the heat demand final energy with building type ({@link String}), {@link RenovationLevel} and {@link HeatingType} as key.
     */
    private final MultiKeyMap heatDemandFinalEnergyMap;

    /**
     * MultiKeyMap for building shell renovation costs with building type ({@link String}) and {@link RenovationLevel} as key.
     */
    private final MultiKeyMap costsBuildingShellMap;

    /**
     * Map containing the exchange costs for heating system. The {@link HeatingType} is the key and the value is a map
     * with bins containing the renovation costs as value in Euro and the heat load in kW as key.
     */
    private final Map<HeatingType, Map<Integer, Double>> costsHeatingSystemMap;

    /**
     * Map mapping {@link HeatingType} to {@link PrimaryEnergyFactors}.
     */
    private final Map<HeatingType, PrimaryEnergyFactors> primaryEnergyFactorsMap;

    public static EnergyCalculator getInstance() {
        return ourInstance;
    }

    private EnergyCalculator() {
        // Init the DAOs for filling the caching maps.
        HeatDemandLoadGenerationDAO heatDemandLoadGenerationDAO = new HeatDemandLoadGenerationDAO();
        PrimaryEnergyFactorsDAO primaryEnergyFactorsDAO = new PrimaryEnergyFactorsDAO();
        HeatDemandFinalEnergyDAO heatDemandFinalEnergyDAO = new HeatDemandFinalEnergyDAO();
        CostsBuildingShellDAO costsBuildingShellDAO = new CostsBuildingShellDAO();
        CostsHeatingSystemDAO costsHeatingSystemDAO = new CostsHeatingSystemDAO();

        // Fill all the maps from the DB
        this.heatDemandLoadGenerationMap = heatDemandLoadGenerationDAO.findAll();
        this.heatDemandFinalEnergyMap = heatDemandFinalEnergyDAO.findAll();
        this.costsBuildingShellMap = costsBuildingShellDAO.findAll();
        this.costsHeatingSystemMap = costsHeatingSystemDAO.findAll();
        this.primaryEnergyFactorsMap = primaryEnergyFactorsDAO.findAll();

    }

    /**
     * Calculate the heat demand for the given {@link Building} using the {@link HeatDemandLoadGenerationDAO}.
     * @param building
     * @return
     */
    public Double calcHeatDemand(Building building) {
        Double heatDemand = 0d;

        // Residential buildings
        if (building.getResidentialType() != null) {
            HeatDemandLoadGeneration heatDemandLoadGeneration = (HeatDemandLoadGeneration) this.heatDemandLoadGenerationMap.get(building.getResidentialType(), building.getRenovationLevel());

            Double spaceHeatingDemand = building.getResidentialFloorSpace() * heatDemandLoadGeneration.getSpaceHeatingDemand();
            Double warmwaterDemand = building.getResidentialFloorSpace() * heatDemandLoadGeneration.getWarmwaterDemand();

            heatDemand += spaceHeatingDemand + warmwaterDemand;
        }

        // Non-residential buildings
        if (building.getNonResidentialType() != null) {
            HeatDemandLoadGeneration heatDemandLoadGeneration = (HeatDemandLoadGeneration) this.heatDemandLoadGenerationMap.get(building.getNonResidentialType(), building.getRenovationLevel());

            Double spaceHeatingDemand = building.getNonResidentialFloorSpace() * heatDemandLoadGeneration.getSpaceHeatingDemand();
            Double warmwaterDemand = building.getNonResidentialFloorSpace() * heatDemandLoadGeneration.getWarmwaterDemand();

            heatDemand += spaceHeatingDemand + warmwaterDemand;
        }

        return heatDemand;
    }

    /**
     * Prepare and store the yearly CO2 data in the primaryEnergyFactorsMap based on {@link HeatingType}.
     *
     * @param co2FactorsData
     * @throws de.hawhh.gewiss.get.core.input.InputValidationException
     */
    public void prepCO2YearlyRates(List<CO2FactorsData> co2FactorsData, Integer midCO2Year, Integer finalCO2Year) throws InputValidationException {
        LOGGER.log(Level.INFO, "Preparing yearly CO2 Emission Rates ");
        this.midCO2Year = midCO2Year;
        this.finalCO2Year = finalCO2Year;
        try {
            for (CO2FactorsData data: co2FactorsData) {
                linInterpolation(data); // can throw NullPointerException
            }
            areYearlyCO2RatesInterpolated = true;
        } catch (NullPointerException e) {
            throw new InputValidationException((e.getMessage()));
        }

    }

    /**
     * Linear interpolation method that creates CO2 yearly emissions for two data ranges:
     * (start) FIRST_YEAR - midCO2Year; midCO2Year - finalCO2Year (end)
     * based on given CO2 values for the start, mid and final year (present in CO2Factors data).
     * Result is stored in the {@link PrimaryEnergyFactors} object associated with the given {@link HeatingType}.
     *
     * @param data CO2FactorsData for a given {@link HeatingType}
     */
    private void linInterpolation(CO2FactorsData data) {
        HeatingType type = data.getHeatingSystem();
        Double startEmissions = data.getStartEmissions();
        Double midEmissions = data.getMidEmissions();
        Double finalEmissions = data.getFinalEmissions();

        Map<Integer, Double> yearlyCO2Rates = new HashMap<>();; // local map
        Double tempEmission;
        Integer year = SimulationParameter.FIRST_YEAR;
        Integer numOfSteps;

        // first interval: start year to mid year
        // *less-than* comparison: only up to midYear (included in second interval loop)
        numOfSteps = midCO2Year - year;
        for (int step = 0; step < numOfSteps; step++) {
            tempEmission = startEmissions + step * (midEmissions - startEmissions) / numOfSteps;
            yearlyCO2Rates.put(year, tempEmission);
            // LOGGER.log(Level.INFO, "{0} CO2 emissions {1} for year {2}", new Object[] {type, tempEmission, year});
            year++;
        }

        // second interval: mid year (*year* variable post-increment from first for loop) to final year
        numOfSteps = finalCO2Year - year;
        for (int step = 0; step <= numOfSteps; step++) {
            tempEmission = midEmissions + step * (finalEmissions - midEmissions) / numOfSteps;
            yearlyCO2Rates.put(year, tempEmission);
            // LOGGER.log(Level.INFO, "{0} CO2 emissions {1} for year {2}", new Object[] {type, tempEmission, year});
            year++;
        }

        this.primaryEnergyFactorsMap.get(type).setYearlyCO2Emissions(yearlyCO2Rates);
        LOGGER.log(Level.INFO, "Finished setting yearlyCO2Rates in primaryEnergyFactorsMap for {0}", type);
    }

    /**
     * Calculate the CO2 Emissions based on the given year after linear interpolation has finished.
     * If CO2 yearly rates do NOT exist or year before range, use overloaded method with base CO2 data from DB.
     *
     * @param building
     * @param year year for the CO2 Yearly Data
     * @return calculated CO2 emissions for given year (and {@link HeatingType } for specific {@link Building})
     */
    public Double calcCO2Emission(Building building, Integer year) {
        Double co2Emission = 0d;
        if (!areYearlyCO2RatesInterpolated) {
            LOGGER.log(Level.INFO, "Yearly interpolated CO2 rates not calculated! Using base CO2 Emissions from DB instead");
            return calcCO2EmissionInternal(building);
        }
        if(year < SimulationParameter.FIRST_YEAR) {
            LOGGER.log(Level.INFO, "No interpolated CO2 data for year before {0}: using base CO2 Emissions from DB instead", SimulationParameter.FIRST_YEAR);
            return calcCO2EmissionInternal(building);
        } else {
            // If year exceeds data range: all calculations will be based on finalYear!
            if (year > finalCO2Year) {
                // LOGGER.log(Level.INFO, "Year exceeds data range: all calculations will be based on finalYear {0}", 2050);
                year = finalCO2Year;
            }

            if (building.getResidentialType() != null) {
                Double co2 = primaryEnergyFactorsMap.get(building.getHeatingType()).getCO2DataForYear(year);
                HeatDemandFinalEnergy heatDemandFinalEnergy = (HeatDemandFinalEnergy) heatDemandFinalEnergyMap.get(
                    building.getResidentialType(), building.getRenovationLevel(), building.getHeatingType()
                );
                Double finalEnergy = heatDemandFinalEnergy.getFinalEnergy();
                co2Emission += (co2 * finalEnergy * building.getResidentialFloorSpace());
            }

            if (building.getNonResidentialType() != null) {
                Double co2 = primaryEnergyFactorsMap.get(building.getHeatingType()).getCO2DataForYear(year);
                HeatDemandFinalEnergy heatDemandFinalEnergy = (HeatDemandFinalEnergy) heatDemandFinalEnergyMap.get(
                    building.getNonResidentialType(), building.getRenovationLevel(), building.getHeatingType()
                );
                Double finalEnergy = heatDemandFinalEnergy.getFinalEnergy();
                co2Emission += (co2 * finalEnergy * building.getNonResidentialFloorSpace());
            }
        }

        return co2Emission;
    }

    /**
     * Calculate the CO2 emissions for the given {@link Building}
     * using {@link HeatDemandFinalEnergyDAO} and only the starting CO2 emissions from {@link PrimaryEnergyFactors}!
     *
     * @param building
     * @return calculated CO2 emissions for {@link HeatingType } from specific {@link Building}
     */
    private Double calcCO2EmissionInternal(Building building) {
        Double co2Emission = 0d;

        if (building.getResidentialType() != null) {
            Double co2 = primaryEnergyFactorsMap.get(building.getHeatingType()).getCo2Start();
            HeatDemandFinalEnergy heatDemandFinalEnergy = (HeatDemandFinalEnergy) heatDemandFinalEnergyMap.get(
                building.getResidentialType(), building.getRenovationLevel(), building.getHeatingType()
            );
            Double finalEnergy = heatDemandFinalEnergy.getFinalEnergy();
            co2Emission += (co2 * finalEnergy * building.getResidentialFloorSpace());
        }

        if (building.getNonResidentialType() != null) {
            Double co2 = primaryEnergyFactorsMap.get(building.getHeatingType()).getCo2Start();
            HeatDemandFinalEnergy heatDemandFinalEnergy = (HeatDemandFinalEnergy) heatDemandFinalEnergyMap.get(
                building.getNonResidentialType(), building.getRenovationLevel(), building.getHeatingType()
            );
            Double finalEnergy = heatDemandFinalEnergy.getFinalEnergy();
            co2Emission += (co2 * finalEnergy * building.getNonResidentialFloorSpace());
        }

        return co2Emission;
    }

    /**
     * Calculate the building shell renovation costs using the {@link CostsBuildingShellDAO}.
     * @param building
     * @return
     */
    public Double calcShellRenovationCosts(Building building) {
        Double costsShell = 0d;

        if (building.getResidentialType() != null) {
            CostsBuildingShell costsBuildingShell = (CostsBuildingShell) costsBuildingShellMap.get(building.getResidentialType(), building.getRenovationLevel());
            costsShell += costsBuildingShell.getCostPerSQM() * building.getResidentialFloorSpace();
        }

        if (building.getNonResidentialType() != null) {
            CostsBuildingShell costsBuildingShell = (CostsBuildingShell) costsBuildingShellMap.get(building.getNonResidentialType(), building.getRenovationLevel());
            costsShell +=  costsBuildingShell.getCostPerSQM() * building.getNonResidentialFloorSpace();
        }

        return costsShell;
    }

    /**
     * Calculate the heat load for the given {@link Building} using the {@link HeatDemandLoadGenerationDAO}.
     *
     * @param building
     * @return
     */
    private Double calcHeatLoad(Building building) {
        Double heatLoad = 0d;

        // Residential buildings
        if (building.getResidentialType() != null) {
            HeatDemandLoadGeneration heatDemandLoadGeneration = (HeatDemandLoadGeneration) heatDemandLoadGenerationMap.get(building.getResidentialType(), building.getRenovationLevel());

            Double spaceHeatingLoad = building.getResidentialFloorSpace() * heatDemandLoadGeneration.getSpaceHeatingLoad();
            Double warmwaterLoad = building.getResidentialFloorSpace() * heatDemandLoadGeneration.getWarmwaterLoad();

            heatLoad += spaceHeatingLoad + warmwaterLoad;
        }

        // Non-residential buildings
        if (building.getNonResidentialType() != null) {
            HeatDemandLoadGeneration heatDemandLoadGeneration = (HeatDemandLoadGeneration) heatDemandLoadGenerationMap.get(building.getNonResidentialType(), building.getRenovationLevel());

            Double spaceHeatingLoad = building.getNonResidentialFloorSpace() * heatDemandLoadGeneration.getSpaceHeatingLoad();
            Double warmwaterLoad = building.getNonResidentialFloorSpace() * heatDemandLoadGeneration.getWarmwaterLoad();

            heatLoad += spaceHeatingLoad + warmwaterLoad;
        }

        return heatLoad;
    }

    /**
     * Calculate the heating exchange renovation costs for the given {@link Building} using the {@link CostsHeatingSystemDAO}.
     *
     * @param building
     * @return
     */
    public Double calcHeatingExchangeRenovationCosts(Building building) {
        Double totalLoad = calcHeatLoad(building);
        return getHeatingExchangeCosts(totalLoad.intValue(), building.getHeatingType());
    }

    /**
     * Getter for Boolean yearlyCO2RatesInterpolated.
     * @return
     */
    public Boolean hasYearlyCO2RatesInterpolated() {
        return areYearlyCO2RatesInterpolated;
    }

    /**
     * Return the heating exchange costs for the given {@link HeatingType} and heat load in kW from the map of heating
     * system exchange costs. As the map represents different bins, the nearest matching value is returned, so that the
     * given heat load is >= the lower bound of the bin but smaller than the next bin entry.
     *
     * @param heatLoadKW the given heat load in kW.
     * @param heatingSystem
     * @return
     */
    private Double getHeatingExchangeCosts(Integer heatLoadKW, HeatingType heatingSystem) {
        Map<Integer, Double> costs = this.costsHeatingSystemMap.get(heatingSystem);
        Optional<Integer> matchingResult = costs.keySet().stream().filter(loadKey -> heatLoadKW >= loadKey).min(Comparator.comparing(Integer::valueOf));
        if (matchingResult.isPresent()) {
            Integer maxMatchingLoad = matchingResult.get();
            return costs.get(maxMatchingLoad);
        }
        return 0d;
    }
}