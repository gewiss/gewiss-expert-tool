package de.hawhh.gewiss.get.core.input;

import lombok.Data;

/**
 * Holder class for receving simulation request from a web client.
 * 
 * @author Thomas Preisler
 */
@Data
public class SimulationRequest {
    
    private SimulationParameter parameter;
    private RenovationStrategy strategy;
}
