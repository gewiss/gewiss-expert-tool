package de.hawhh.gewiss.get.core.model;

import lombok.Data;

@Data
public class CostsHeatingSystem {

    private Integer heatLoadKW;
    private HeatingType heatingSystem;
    private Double costs;
}
