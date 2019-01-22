package de.hawhh.gewiss.get.simulator.scoring;

import de.hawhh.gewiss.get.core.model.Building;

/**
 * Interface for calculating a buildings base score. The interface is used to be able to use different scoring methods/implementations
 * for the calculation of this score.
 *
 * @author Nils Weiss
 */

public interface ScoringMethod {

    /**
     * Calculates the buildings score depending on the provided scoring method and the current year of the simulation.
     *
     * @param building the given building
     * @param simYear the current year of the simulation
     * @return the calculated building base score
     */
    Double calcBaseScore(Building building, Integer simYear);
}
