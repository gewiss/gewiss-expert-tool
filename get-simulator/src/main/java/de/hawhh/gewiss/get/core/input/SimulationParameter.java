package de.hawhh.gewiss.get.core.input;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * Class containing all the input parameter of a simulation run.
 *
 * @author Thomas Preisler, Antony Sotirov
 */
@Data
public class SimulationParameter {
    public static final Integer FIRST_YEAR = 2019; // also used as 'startCO2Year'

    private String name;
    private Integer stopYear;
    private List<Modifier> modifiers = new ArrayList<>();
    // used for the calculation of the yearly CO2 Emission Rate factors:
    private List<CO2FactorsData> yearlyCO2Factors = new ArrayList<>();
    private Integer midCO2Year;
    private Integer finalCO2Year;

    /**
     * Constructor setting all simulation parameters.
     *
     * @param name the name of the simulation
     * @param stopYear the simulation stop year (inclusive)
     * @param modifiers
     * @param yearlyCO2Factors list of CO2Factors per HeatingType
     * @param midCO2Year the middle year for CO2 Target Emissions Rate
     * @param finalCO2Year the final year for CO2 Target Emissions Rate
     */
    public SimulationParameter(String name, Integer stopYear, List<Modifier> modifiers,
                               List<CO2FactorsData> yearlyCO2Factors, Integer midCO2Year, Integer finalCO2Year) {
        this.name = name;
        this.stopYear = stopYear;
        this.modifiers = modifiers;
        this.yearlyCO2Factors = yearlyCO2Factors;
        this.midCO2Year = midCO2Year;
        this.finalCO2Year = finalCO2Year;
    }

    /**
     * Standard constructor.
     */
    public SimulationParameter() {
        super();
    }

    /**
     * @return the duration of the simulation in years.
     */
    public int getSimDuration() {
        return stopYear - FIRST_YEAR + 1;
    }

    /**
     * Validates the simulation input parameters and throws an {@link InputValidationException} is one of the parameters is invalid.
     *
     * @throws InputValidationException
     */
    public void validate() throws InputValidationException {
        try {
            if(midCO2Year == null || finalCO2Year == null || yearlyCO2Factors == null || yearlyCO2Factors.isEmpty()) {
                throw new InputValidationException("Missing yearly CO2 Factors Data for different time periods!");
            }
            if (stopYear < FIRST_YEAR) {
                throw new InputValidationException("Stop year shouldn't be before " + FIRST_YEAR + " (status quo)");
            }
            if(midCO2Year < FIRST_YEAR) {
                    throw new InputValidationException("Middle year for CO2 Emissions Rate shouldn't be before " + FIRST_YEAR + " (status quo)");
            }
            if(finalCO2Year < midCO2Year) {
                    throw new InputValidationException("Final year for CO2 Emissions Rate shouldn't be before " + midCO2Year + " (status quo)");
            }

            for (Modifier modifier : modifiers) {
                if (!modifier.isValid()) {
                    throw new InputValidationException(modifier.getName() + " is invalid");
                }
            }
            
            if (name == null) {
                throw new InputValidationException("Name is null");
            }
        } catch (NullPointerException e) {
            throw new InputValidationException((e.getMessage()));
        }
    }
}
