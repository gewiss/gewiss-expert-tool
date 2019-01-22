package de.hawhh.gewiss.get.core.input;

import com.google.common.collect.Range;
import de.hawhh.gewiss.get.core.model.Building;
import de.hawhh.gewiss.get.core.model.HeatingType;
import de.hawhh.gewiss.get.core.model.RenovationLevel;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * Test class for {@link Modifier}s.
 * 
 * @author Thomas Preisler
 */
public class ModifierTest {
    
    public ModifierTest() {
    }

    /**
     * Test of checkConditions method, of class Modifier.
     *
     */
    @Test
    public void testCheckConditions() {
        System.out.println("checkConditions");
        
        Building building = new Building();
        building.setQuarter("Altona");
        building.setRenovationLevel(RenovationLevel.BASIC_RENOVATION);
        building.setResidentialType("Test");
        building.setHeatingType(HeatingType.CONDENSING_BOILER);
        building.setYearOfConstruction(1995);
        building.setDistrictHeatingOutletDistance(100);
        
        Modifier instance = new Modifier("Test", 2017, 2018, 2d);
        instance.setTargetQuarters(Arrays.asList("Altona", "Bahrenfeld"));
        instance.setTargetRenovationLevels(Collections.singletonList(RenovationLevel.BASIC_RENOVATION));
        instance.setTargetBuildingsTypes(Collections.singletonList("Test"));
        instance.setTargetHeatingSystems(Collections.singletonList(HeatingType.CONDENSING_BOILER));
        instance.setYearOfConstructionRange(Range.closed(1990, 2000));
        
        boolean result = instance.checkConditions(building);
        assertTrue(result);
        
        building.setQuarter("Curslack");
        result = instance.checkConditions(building);
        assertFalse(result);
    }

    /**
     * Test of isActive method, of class Modifier.
     */
    @Test
    public void testIsActive() {
        System.out.println("isActive");
        Integer year = 2019;
        Modifier instance = new Modifier("Test", 2018, 2019, 1d);
        boolean expResult = true;
        boolean result = instance.isActive(year);
        
        assertEquals(expResult, result);
        assertFalse(instance.isActive(2020));
    }

    /**
     * Test of getImpactFactor method, of class Modifier.
     */
    @Test
    public void testGetImpactFactor() {
        System.out.println("getImpactFactor");
        Modifier instance = new Modifier("Test", 2018, 2020, 1d);
        Double expResult = 1d;
        Double result = instance.getImpactFactor();
        assertEquals(expResult, result, 0.01);
    }
}