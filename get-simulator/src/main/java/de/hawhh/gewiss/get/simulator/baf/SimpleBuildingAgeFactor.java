package de.hawhh.gewiss.get.simulator.baf;

import de.hawhh.gewiss.get.core.model.Building;

/**
 * Implements a simple function for the calculation of the building age factor.
 * 
 * If a building has been renovated within the last 20 years (relative to the current year of the simulation) 0 is returned.
 * Otherwise the factor is calculated by: (Year of Simulation - Last year of renovation) / Year of simulation.
 * Therefore the factor is higher if the renovation was a longer time ago.
 * 
 * @author Thomas Preisler
 */
public class SimpleBuildingAgeFactor implements IBuildingAgeFactor {

     @Override
    public Double calcFactor(Building building, Integer simYear) {
        Integer lastRenovation = null;
        
        // Determine the last year of renovation.
        // Either the actual last year of renovation, the year of construction if set, or the mean value of the construction age class
        if (building.getYearOfRenovation() != null && building.getYearOfRenovation() > 0) {
            lastRenovation = building.getYearOfRenovation();
        } else {
            if (building.getYearOfConstruction() != null && building.getYearOfConstruction() > 0) {
                lastRenovation = building.getYearOfConstruction();
            } else {
                lastRenovation = building.getConstructionAgeClass().getMean();
            }
        }
        
        if ((simYear - lastRenovation) <= 20) {
            return 0d;
        } else {
            return (simYear.doubleValue() - lastRenovation.doubleValue()) / simYear.doubleValue();
        }
    }  
}