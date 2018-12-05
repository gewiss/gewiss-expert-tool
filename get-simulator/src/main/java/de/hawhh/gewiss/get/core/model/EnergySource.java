package de.hawhh.gewiss.get.core.model;

import lombok.Data;

/**
 * Class implementing a general EnergySource.
 * 
 * @author Nils Weiss
 */

@Data
public class EnergySource {
    
public enum Type { 
    HEATING_OIL, NATURAL_GAS, DISTRICT_HEAT, HEAT_BOILER_STATION, NUCLEAR_POWER, PELLETS, ELECTRICITY
}


private Type type;
private Double primaryEnergyFactor;
private Double co2Emission;

}