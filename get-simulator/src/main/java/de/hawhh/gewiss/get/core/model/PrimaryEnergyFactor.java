package de.hawhh.gewiss.get.core.model;

import lombok.Data;

@Data
public class PrimaryEnergyFactor {

    private HeatingType heatingSystem;
    private EnergySourceType energySourceType;
    private Double primaryEnergyFactor;
    private Double co2;
}
