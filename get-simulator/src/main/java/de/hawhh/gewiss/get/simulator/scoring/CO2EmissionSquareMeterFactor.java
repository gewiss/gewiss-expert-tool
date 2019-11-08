package de.hawhh.gewiss.get.simulator.scoring;

import de.hawhh.gewiss.get.core.model.Building;
import de.hawhh.gewiss.get.core.model.RenovationLevel;

/**
 * Score a building according to its CO2 emission per square meter.
 *
 * @author Thomas Preisler
 */
public class CO2EmissionSquareMeterFactor extends CO2EmissionFactor {

    @Override
    public Double calcBaseScore(Building building, Integer simYear) {
        if (!building.getRenovationLevel().equals(RenovationLevel.GOOD_RENOVATION)) {
            Double sm = building.getNonResidentialFloorSpace() + building.getResidentialFloorSpace();
            if (sm <= 0d) {
                return -1d;
            }
            return energyCalculator.calcCO2Emission(building, simYear) / sm;
        } else {
            return -1d;
        }
    }
}
