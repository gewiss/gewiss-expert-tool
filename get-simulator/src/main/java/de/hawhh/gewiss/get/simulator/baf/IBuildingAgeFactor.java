package de.hawhh.gewiss.get.simulator.baf;

import de.hawhh.gewiss.get.core.model.Building;

/**
 * Interface for calculating the building age factor as a base for the renovation score calculation. The interface is used to be able to use different implementations/
 * approaches for the calculation of this factor.
 *
 * @author Thomas Preisler
 */
public interface IBuildingAgeFactor {

    /**
     * Calculates the building age factor depending on the given building and the current year of the simulation.
     *
     * @param building the given building
     * @param simYear the current year of the simulation
     * @return the calculated building age factor
     */
    public Double calcFactor(Building building, Integer simYear);
}
