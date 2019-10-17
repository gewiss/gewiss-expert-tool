package de.hawhh.gewiss.get.core.model;

import lombok.Data;

@Data
public class PrimaryEnergyFactors {

    private HeatingType heatingSystem;
    private EnergySourceType energySourceType;
    private Double primaryEnergyFactor;
    private Double co2;
}
