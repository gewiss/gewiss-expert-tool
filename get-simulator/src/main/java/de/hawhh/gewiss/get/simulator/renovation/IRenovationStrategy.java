package de.hawhh.gewiss.get.simulator.renovation;

import de.hawhh.gewiss.get.simulator.model.ScoredBuilding;

import java.util.List;
import java.util.Random;

/**
 * Interface for renovation strategies.
 *
 * @author Thomas Preisler
 */
public interface IRenovationStrategy {

    /**
     * Perform the renovation strategy for the given list of {@link ScoredBuilding}s
     *
     * @param scoredBuildings
     * @param currentYear           the current year of the simulation
     * @param pseudoRandomGenerator the pseudo random generator used for the simulation
     */
    void performRenovation(List<ScoredBuilding> scoredBuildings, Integer currentYear, Random pseudoRandomGenerator);
}
