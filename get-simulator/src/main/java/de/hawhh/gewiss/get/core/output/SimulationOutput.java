package de.hawhh.gewiss.get.core.output;

import de.hawhh.gewiss.get.core.model.HeatingType;
import de.hawhh.gewiss.get.core.model.RenovationLevel;
import lombok.Data;

/**
 * Data class for storing the building specific simulation results for one year.
 * 
 * @author Thomas Preisler, Antony Sotirov
 */
@Data
public class SimulationOutput {

    private String buildingId;
    private Integer year;
    private Double heatDemandM2;
    private Double heatDemand;
    private RenovationLevel renovationLevel;
    private HeatingType heatingType;
    private Double renovationCost;
    private Double co2Emission;
    private Double residentialArea;
    private Double combinedArea;

    public String getRenovationLevelString() {
        return this.renovationLevel.toString();
    }
    
    public String getHeatingTypeString() {
        return this.heatingType.toString();
    }
}
