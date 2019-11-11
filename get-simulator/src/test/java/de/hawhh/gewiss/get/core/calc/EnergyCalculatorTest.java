package de.hawhh.gewiss.get.core.calc;

import de.hawhh.gewiss.get.core.input.CO2FactorsData;
import de.hawhh.gewiss.get.core.input.InputValidationException;
import de.hawhh.gewiss.get.core.input.SimulationParameter;
import de.hawhh.gewiss.get.core.model.Building;
import de.hawhh.gewiss.get.core.model.HeatingType;
import de.hawhh.gewiss.get.core.model.RenovationLevel;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class EnergyCalculatorTest {

    private EnergyCalculator energyCalculator;
    private Building testBuilding;

    @Before
    public void setUp() {
        this.energyCalculator = EnergyCalculator.getInstance();

        this.testBuilding = new Building();
        this.testBuilding.setQuarter("Altona");
        this.testBuilding.setDistrictHeatingOutletDistance(1);
        this.testBuilding.setDistrict("Altona");
        this.testBuilding.setHeatingType(HeatingType.DISTRICT_HEAT);
        this.testBuilding.setResidentialType("EFH_A");
        this.testBuilding.setYearOfConstruction(1990);
        this.testBuilding.setAlkisID("Test_ID");
        this.testBuilding.setCityBlock("Test Block");
        this.testBuilding.setClusterID("Test Cluster");
        this.testBuilding.setRenovationLevel(RenovationLevel.BASIC_RENOVATION);
        this.testBuilding.setYearOfRenovation(2000);
        this.testBuilding.setResidentialFloorSpace(200D);
    }

    @Test
    public void getInstance() {
        assertNotNull(this.energyCalculator);
    }

    @Test
    public void calcHeatDemand() {
        Double heatDemand = energyCalculator.calcHeatDemand(testBuilding);

        System.out.println("Heat demand: " + heatDemand);
        assertNotNull(heatDemand);
        assertTrue(heatDemand > 0);
    }

    @Test
    public void calcCO2EmissionNoInterpolation() {
        Double co2 = energyCalculator.calcCO2Emission(testBuilding, SimulationParameter.FIRST_YEAR);

        System.out.println("CO2 Emission: " + co2 + " (default rate for " + SimulationParameter.FIRST_YEAR +")");
        assertNotNull(co2);
        assertTrue(co2 > 0);
    }

    @Test
    public void calcCO2EmissionInterpolation() throws InputValidationException {
        // CO2 Factors for List for interpolation prep
        CO2FactorsData fa1 = new CO2FactorsData(HeatingType.CONDENSING_BOILER, 201d, 201d, 201d);
        CO2FactorsData fa2 = new CO2FactorsData(HeatingType.CONDENSING_BOILER_SOLAR, 201d, 201d, 201d);
        CO2FactorsData fa3 = new CO2FactorsData(HeatingType.CONDENSING_BOILER_SOLAR_HEAT_RECOVERY, 201d, 201d, 201d);
        CO2FactorsData fa4 = new CO2FactorsData(HeatingType.DISTRICT_HEAT, 291.6d, 215d, 160d);
        CO2FactorsData fa5 = new CO2FactorsData(HeatingType.DISTRICT_HEAT_HEAT_RECOVERY, 291.6d, 215d, 160d);
        CO2FactorsData fa6 = new CO2FactorsData(HeatingType.HEAT_PUMP_HEAT_RECOVERY, 617d, 402d, 231d);
        CO2FactorsData fa7 = new CO2FactorsData(HeatingType.LOW_TEMPERATURE_BOILER, 201d, 201d, 201d);
        CO2FactorsData fa8 = new CO2FactorsData(HeatingType.PELLETS, 23d, 23d, 23d);
        CO2FactorsData fa9 = new CO2FactorsData(HeatingType.PELLETS_SOLAR_HEAT_RECOVERY, 23d, 23d, 23d);

        List<CO2FactorsData> yearlyCO2Factors = new ArrayList<>(Arrays.asList(fa1, fa2, fa3, fa4, fa5, fa6, fa7, fa8, fa9));
        Integer midCO2Year = 2030;
        Integer finalCO2Year = 2050;

        energyCalculator.prepCO2YearlyRates(yearlyCO2Factors, midCO2Year, finalCO2Year);

        for (int year = SimulationParameter.FIRST_YEAR; year<=finalCO2Year; year++) {
            Double co2 = energyCalculator.calcCO2Emission(testBuilding, SimulationParameter.FIRST_YEAR);

            System.out.println("CO2 Emission: " + co2 + " for year: " + year);
            assertNotNull(co2);
            assertTrue(co2 > 0);
        }
    }

    @Test
    public void calcShellRenovationCosts() {
        Double shellRenovationCosts = energyCalculator.calcShellRenovationCosts(testBuilding);

        System.out.println("Shell renovation costs: " + shellRenovationCosts);
        assertNotNull(shellRenovationCosts);
        assertTrue(shellRenovationCosts > 0);
    }

    @Test
    public void calcHeatingExchangeRenovationCosts() {
        Double heatingExchangeCosts = energyCalculator.calcHeatingExchangeRenovationCosts(testBuilding);

        System.out.println("Heating Exchange Costs: " + heatingExchangeCosts);
        assertNotNull(heatingExchangeCosts);
        assertTrue(heatingExchangeCosts > 0);
    }
}