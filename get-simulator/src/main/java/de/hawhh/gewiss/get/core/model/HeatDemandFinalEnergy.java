package de.hawhh.gewiss.get.core.model;

import lombok.Data;

@Data
public class HeatDemandFinalEnergy {

    private String buildingType;
    private RenovationLevel renovationLevel;
    private HeatingType heatingSystem;
    private Double finalEnergy;
}
