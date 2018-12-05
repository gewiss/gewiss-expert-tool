package de.hawhh.gewiss.get.core.input;

import lombok.Data;

/**
 * Data holder class for receiving renovation strategy configuration from a web-client.
 * 
 * @author Thomas Preisler
 */
@Data
public class RenovationStrategy {
    
    public enum Strategy {
        RENOVATION_RATE
    }
    
    private String strategy;
    private Double renovationRate;
    private Double basicRenovationChance;    
}
