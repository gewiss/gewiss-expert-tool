package de.hawhh.gewiss.get.core.model;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/**
 *
 * Class holding the renovation costs for a specific type of building for different renovation levels.
 *
 * @author Thomas
 */
@Data
public class RenovationCost {

    private String buildingType;
    private Map<RenovationLevel, Double> regressionCosts = new HashMap<>();

    /**
     * Returns the renovation cost for the given renovation level
     *
     * @param renovationLevel
     * @return
     */
    public Double getRenovationCost(RenovationLevel renovationLevel) {
        return regressionCosts.get(renovationLevel);
    }
}