package de.hawhh.gewiss.get.simulator.model;

import de.hawhh.gewiss.get.core.model.Building;
import de.hawhh.gewiss.get.simulator.scoring.ScoringMethod;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/**
 * Tuple for storing a building and its calculated renovation scoring value together.
 *
 * @author Thomas Preisler
 */
@Data
public class ScoredBuilding {

    private Building building;
    private Map<ScoringMethod, Double> scores;
    private Double combinedScore;

    /**
     * Default constructor creating the object by the given building and score.
     *
     * @param building
     */
    public ScoredBuilding(Building building) {
        this.building = building;
        this.scores = new HashMap<>();
    }
}
