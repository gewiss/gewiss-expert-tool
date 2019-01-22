package de.hawhh.gewiss.get.simulator.scoring;

import de.hawhh.gewiss.get.core.calc.EnergyCalculator;
import de.hawhh.gewiss.get.core.model.Building;
import de.hawhh.gewiss.get.core.model.RenovationLevel;

/**
 * Score a building according to it's CO2 emission.
 *
 * @author Thomas Preisler
 */
public class CO2EmissionFactor implements ScoringMethod {

    final EnergyCalculator energyCalculator;

    public CO2EmissionFactor() {
        this.energyCalculator = EnergyCalculator.getInstance();
    }

    @Override
    public Double calcBaseScore(Building building, Integer simYear) {
        if (!building.getRenovationLevel().equals(RenovationLevel.GOOD_RENOVATION)) {
            return energyCalculator.calcCO2Emission(building);
        } else {
            return -1d;
        }
    }
}