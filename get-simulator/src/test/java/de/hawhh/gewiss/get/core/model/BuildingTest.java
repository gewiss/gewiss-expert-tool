/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hawhh.gewiss.get.core.model;

import de.hawhh.gewiss.get.core.model.HeatingType;
import de.hawhh.gewiss.get.core.model.EnergySource;
import de.hawhh.gewiss.get.core.model.Building;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Nils Weiss
 */
public class BuildingTest {
    
    public BuildingTest() {
    }
    
    /**
     * Test of calcCo2Emission method, of class Building.
     */
    @Test
    public void testCalcCo2Emission() {
        System.out.println("calcCo2Emission");
        Building instance = new Building();
        HeatingType ht = HeatingType.PELLETS;
        instance.setHeatingType(ht);
        
        Double heatDemand = 600.0;
        Map<HeatingType, EnergySource> map = new HashMap<>();
        EnergySource source = new EnergySource();
        source.setCo2Emission(100.0);
        map.put(HeatingType.PELLETS, source);
      
        Double expResult = 600.0 * 100.0;
        Double result = instance.calcCo2Emission(heatDemand, map);
        assertEquals(expResult, result);
    }
   
}
