package de.hawhh.gewiss.get.core.model;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/**
 * Class containing information about the heat demand of a building. It consists of the heating demand and the warm water demand for different renovation level.
 *
 * @author Thomas Preisler
 */
@Data
public class HeatDemand {

    private String buildingType;
    private Map<RenovationLevel, Double> heatingDemand = new HashMap<>();
    private Map<RenovationLevel, Double> warmWaterDemand = new HashMap<>();

    /**
     * Returns the heating demand for the given renovation level
     *
     * @param renovationLevel
     * @return
     */
    public Double getHeatingDemand(RenovationLevel renovationLevel) {
        return heatingDemand.get(renovationLevel);
    }

    /**
     * Returns the warm water demand for the given renovation level
     *
     * @param renovationLevel
     * @return
     */
    public Double getWarmWaterDemand(RenovationLevel renovationLevel) {
        return warmWaterDemand.get(renovationLevel);
    }
}
