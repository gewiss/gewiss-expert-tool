package de.hawhh.gewiss.get.core.model;

import lombok.Data;

@Data
public class HeatDemandLoadGeneration {

    private String buildingType;
    private RenovationLevel renovationLevel;
    private Double spaceHeatingDemand;
    private Double warmwaterDemand;
    private Double spaceHeatingLoad;
    private Double warmwaterLoad;
}
