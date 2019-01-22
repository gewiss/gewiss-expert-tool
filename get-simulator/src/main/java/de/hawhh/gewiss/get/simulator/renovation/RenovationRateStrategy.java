package de.hawhh.gewiss.get.simulator.renovation;

import de.hawhh.gewiss.get.core.model.Building;
import de.hawhh.gewiss.get.core.model.RenovationLevel;
import de.hawhh.gewiss.get.simulator.model.ScoredBuilding;

import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple renovation strategy where a yearly renovation rate is set. Thereby, the n% buildings with the highest score are selected and renovated. Additionally the chance
 * for a basic renovation can also be defined. The strategy performs a random check against this rate and based on the outcome decides whether the building is renovated
 * to the basic or the good renovation level.
 *
 * @author Thomas Preisler
 */
public class RenovationRateStrategy implements IRenovationStrategy {

    private final static Logger LOGGER = Logger.getLogger(RenovationRateStrategy.class.getName());

    private final Double renovationRate;
    private final Double basicRenovationChance;

    /**
     * Default constructor. Here the yearly renovation rate and the chance for a basic renovation rate can be set. In both cases a value of 0.5 equals 50%.
     *
     * @param renovationRate
     * @param basicRenovationChance
     */
    private RenovationRateStrategy(Double renovationRate, Double basicRenovationChance) {
        this.renovationRate = renovationRate;
        this.basicRenovationChance = basicRenovationChance;

        LOGGER.log(Level.INFO, "Initialized RenovationRateStrategy with a renovationRate of {0} and a basic renovation chance of {1}", new Object[]{renovationRate, basicRenovationChance});
    }

    @Override
    public void performRenovation(List<ScoredBuilding> scoredBuildings, Integer currentYear, Random pseudoRandomGenerator) {
        LOGGER.info("Performing renovation of given List of scored buildings");

        long noRenovatedBuildings = (long) (scoredBuildings.size() * renovationRate);
        // Limit the stream of scored buildings so that only renovate rate percentage buildings are selected for a renovation
        scoredBuildings.stream().limit(noRenovatedBuildings).forEachOrdered(sb -> {
            //LOGGER.log(Level.INFO, "Renvating building {0}\twith score {1}", new Object[]{sb.getBuilding().getAlkisID(), sb.getScore()});
            Building building = sb.getBuilding();

            if (building.getRenovationLevel().equals(RenovationLevel.NO_RENOVATION)) {
                // Perform a basic or a good renovation depending on the basic renovation chance
                if (pseudoRandomGenerator.nextDouble() <= basicRenovationChance) {
                    building.renovate(RenovationLevel.BASIC_RENOVATION, currentYear);
                } else {
                    building.renovate(RenovationLevel.GOOD_RENOVATION, currentYear);
                }
            } else if (building.getRenovationLevel().equals(RenovationLevel.BASIC_RENOVATION)) {
                building.renovate(RenovationLevel.GOOD_RENOVATION, currentYear);
            }
        });

        LOGGER.log(Level.INFO, "Renovated {0} of {1} buildings with a renovation rate of {2}", new Object[]{noRenovatedBuildings, scoredBuildings.size(), renovationRate});
    }
}
