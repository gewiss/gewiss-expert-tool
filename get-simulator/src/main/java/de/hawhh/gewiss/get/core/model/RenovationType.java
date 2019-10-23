package de.hawhh.gewiss.get.core.model;

/**
 * Enumeration for different renovation types.
 * A combination of Residential / Non-Residential buildings and renovation level
 *
 * @author Antony Sotirov
 */
public enum RenovationType {
    //@TODO: ask Team Peters whether Level 1 and 2 match BASIC and GOOD; also naming
    RES_BASIC,   // Residential Renovation Level 1
    RES_GOOD,   // Residential Renovation Level 2
    NRES_BASIC,  // Non-Residential Renovation Level 1
    NRES_GOOD   // Non-Residential Renovation Level 2
}
