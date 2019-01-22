package de.hawhh.gewiss.get.core.model;

import lombok.Data;

@Data
public class CostsBuildingShell {

    private String buildingType;
    private RenovationLevel renovationLevel;
    private Double costPerSQM;
}
