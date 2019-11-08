package de.hawhh.gewiss.get.core.model;

import lombok.Data;
import java.util.Map;

/**
 * Primary Energy Factors data class.
 *
 * @author Thomas Preisler, Antony Sotirov
 */
@Data
public class PrimaryEnergyFactors {

    // values obtained from sqlite Database (see PrimaryEnergyFactorsDAO)
    private HeatingType heatingSystem;
    private EnergySourceType energySourceType;
    private Double primaryEnergyFactor;
    private Double co2Start;
    private Double co2Mid;
    private Double co2Final;
    // value set in EnergyCalculator class using relevant SimulationParameter variables
    private Map<Integer, Double> yearlyCO2Emissions; // <year, CO2_emissions>

    public Double getCO2DataForYear(Integer year) {
        return yearlyCO2Emissions.get(year);
    }
}
