package de.hawhh.gewiss.get.simulator.model;

import de.hawhh.gewiss.get.core.model.Building;
import lombok.Data;

/**
 * Tuple for storing a building and its calculated renovation scoring value together.
 *
 * @author Thomas Preisler
 */
@Data
public class ScoredBuilding {

    private Building building;
    private Double score;

    /**
     * Default constructor creating the object by the given building and score.
     *
     * @param building
     * @param score
     */
    public ScoredBuilding(Building building, Double score) {
        this.building = building;
        this.score = score;
    }
}
