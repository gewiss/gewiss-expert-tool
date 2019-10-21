package de.hawhh.gewiss.get.core.input;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * Class containing all the input parameter of a simulation run.
 *
 * @author Thomas Preisler
 */
@Data
public class SimulationParameter {

    public static final int FIRST_YEAR = 2019; //@TODO: maybe grab from new DB field (discuss with Team Peters)?

    private String name;
    private Integer stopYear;
    private List<Modifier> modifiers = new ArrayList<>();
    private List<CO2FactorsData> yearlyCO2Factors = new ArrayList<>();

    /**
     * Constructor setting all simulation parameter.
     *
     * @param name the name of the simulation
     * @param stopYear the simulation stop year (inclusive)
     * @param modifiers
     */
    public SimulationParameter(String name, Integer stopYear, List<Modifier> modifiers, List<CO2FactorsData> yearlyCO2Factors) {
        this.name = name;
        this.stopYear = stopYear;
        this.modifiers = modifiers;
        this.yearlyCO2Factors = yearlyCO2Factors;
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
     * @return a boolean whether yearly CO2 factors data is present
     */
    public Boolean hasCO2FactorsData() {
        if(yearlyCO2Factors != null && !yearlyCO2Factors.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Validates the simulation input parameters and throws an {@link InputValidationException} is one of the parameters is invalid.
     *
     * @throws InputValidationException
     */
    public void validate() throws InputValidationException {
        try {
            if (stopYear < FIRST_YEAR) {
                throw new InputValidationException("Stop year shouldn't be before " + FIRST_YEAR + " (status quo)");
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
