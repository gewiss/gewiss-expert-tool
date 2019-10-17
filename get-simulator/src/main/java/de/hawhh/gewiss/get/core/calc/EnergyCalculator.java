package de.hawhh.gewiss.get.core.calc;

import de.hawhh.gewiss.get.core.model.*;
import de.hawhh.gewiss.get.simulator.db.dao.*;
import org.apache.commons.collections4.map.MultiKeyMap;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

/**
 * Helper class (singleton) for different energy based calculations (like heat demand, co2 emission). It uses maps to
 * cache the values in the database to significally speed up the look up operations.
 *
 * @author Thomas Preisler
 */
public class EnergyCalculator {
    private static final EnergyCalculator ourInstance = new EnergyCalculator();

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
     * Calc the heat demand for the given {@link Building} using the {@link HeatDemandLoadGenerationDAO}.
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
     * Calc the co2 emission in g for the given {@link Building} using the {@link PrimaryEnergyFactorsDAO} and {@link HeatDemandFinalEnergyDAO}.
     * @param building
     * @return
     */
    //@TODO: create an overloaded method  with calcCO2Emission(Building building, int year)
    // use original method calcCO2Emission(Bulding building) if decreasing CO2 not requested or if first year(?)
    // Be careful with edge cases: initial year vs DB data; final year sim vs final year CO2
    public Double calcCO2Emission(Building building) {
        Double co2Emission = 0d;

        if (building.getResidentialType() != null) {
            Double co2 = primaryEnergyFactorsMap.get(building.getHeatingType()).getCo2();
            HeatDemandFinalEnergy heatDemandFinalEnergy = (HeatDemandFinalEnergy) heatDemandFinalEnergyMap.get(building.getResidentialType(), building.getRenovationLevel(), building.getHeatingType());
            Double finalEnergy = heatDemandFinalEnergy.getFinalEnergy();
            co2Emission += (co2 * finalEnergy * building.getResidentialFloorSpace());
        }

        if (building.getNonResidentialType() != null) {
            Double co2 = primaryEnergyFactorsMap.get(building.getHeatingType()).getCo2();
            HeatDemandFinalEnergy heatDemandFinalEnergy = (HeatDemandFinalEnergy) heatDemandFinalEnergyMap.get(building.getNonResidentialType(), building.getRenovationLevel(), building.getHeatingType());
            Double finalEnergy = heatDemandFinalEnergy.getFinalEnergy();
            co2Emission += (co2 * finalEnergy * building.getNonResidentialFloorSpace());
        }

        return co2Emission;
    }

    /**
     * Calc the building shell renovation costs using the {@link CostsBuildingShellDAO}.
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
     * Calc the heat load for the given {@link Building} using the {@link HeatDemandLoadGenerationDAO}.
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
     * Calc the heating exchange renovation costs for the given {@link Building} using the {@link CostsHeatingSystemDAO}.
     * @param building
     * @return
     */
    public Double calcHeatingExchangeRenovationCosts(Building building) {
        Double totalLoad = calcHeatLoad(building);
        return getHeatingExchangeCosts(totalLoad.intValue(), building.getHeatingType());
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