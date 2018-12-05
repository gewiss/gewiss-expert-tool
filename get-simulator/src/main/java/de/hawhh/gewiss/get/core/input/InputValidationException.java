package de.hawhh.gewiss.get.core.input;

/**
 * Exception that is thrown if the simulation input parameters are invalid.
 * 
 * @author Thomas Preisler
 */
public class InputValidationException extends Exception {

    public InputValidationException(String message) {
        super(message);
    }
}