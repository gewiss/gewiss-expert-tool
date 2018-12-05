/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hawhh.gewiss.get.simulator.renovation;

import de.hawhh.gewiss.get.simulator.model.ScoredBuilding;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Thomas
 */
public interface IRenovationStrategy {
    
    public void performRenovation(List<ScoredBuilding> scoredBuildings, Integer currentYear, Random pseudoRandomGenerator);    
}
