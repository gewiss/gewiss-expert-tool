package de.hawhh.gewiss.get.core.model;

import lombok.Data;
import java.util.Map;

@Data
public class PrimaryEnergyFactors {

    private HeatingType heatingSystem;
    private EnergySourceType energySourceType;
    private Double primaryEnergyFactor;
    private Double co2Start;
    private Double co2Mid;
    private Double co2Final;
    private Map<Integer, Double> yearlyCO2Emissions; // <year, CO2_emissions>

    public Double getCO2DataForYear(Integer year) {
        return yearlyCO2Emissions.get(year);
    }
}
