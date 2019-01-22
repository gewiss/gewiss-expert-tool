package de.hawhh.gewiss.get.core.calc;

import de.hawhh.gewiss.get.core.model.Building;
import de.hawhh.gewiss.get.core.model.HeatingType;
import de.hawhh.gewiss.get.core.model.RenovationLevel;
import org.junit.Before;
import org.junit.Test;

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
        this.testBuilding.setDistrictHeatingOutletDistance(10);
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
    public void calcCO2Emission() {
        Double co2 = energyCalculator.calcCO2Emission(testBuilding);

        System.out.println("CO2 Emission: " + co2);
        assertNotNull(co2);
        assertTrue(co2 > 0);
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