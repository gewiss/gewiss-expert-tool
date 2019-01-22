package de.hawhh.gewiss.get.simulator.renovation;

import com.google.common.collect.Range;
import de.hawhh.gewiss.get.core.input.HeatingSystemExchangeRate;
import de.hawhh.gewiss.get.core.model.Building;
import de.hawhh.gewiss.get.core.model.HeatingType;
import de.hawhh.gewiss.get.core.model.RenovationLevel;
import de.hawhh.gewiss.get.simulator.model.ScoredBuilding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Renovation strategy where a fixed yearly renovation rate as well as heat exchange rates for certain heating system types are set. Thereby, the n% buildings with the
 * highest score are selected and renovated, thereby their heating system might also be exchanged w.r.t. the heating exchange rates. Additionally the chance for a passive
 * house renovation can also be defined. The strategy performs a random check against this rate and based on the outcome decides whether the building is renovated to the
 * basic or the good renovation level.
 *
 * @author Thomas Preisler
 */
public class RenovationHeatExchangeRateStrategy implements IRenovationStrategy {

    private final static Logger LOGGER = Logger.getLogger(RenovationHeatExchangeRateStrategy.class.getName());

    private final Double renovationRate;
    private final Double passiveHouseRate;
    private Map<HeatingType, Map<Range<Double>, HeatingType>> heatingExchangeClasses;

    /**
     * Default constructor. Here the yearly renovation rate and the chance for a basic renovation rate can be set. In both cases a value of 50.0 equals 50%.
     *
     * @param renovationRate
     * @param passiveHouseRate
     * @param heatingSystemExchangeRates
     */
    public RenovationHeatExchangeRateStrategy(Double renovationRate, Double passiveHouseRate, List<HeatingSystemExchangeRate> heatingSystemExchangeRates) {
        this.renovationRate = renovationRate;
        this.passiveHouseRate = passiveHouseRate;
        
        createHeatingExchangeClasses(heatingSystemExchangeRates);

        LOGGER.log(Level.INFO, "Initialized RenovationRateStrategy with a renovationRate of {0}% and a passive house renovation chance of {1}%", new Object[]{renovationRate, passiveHouseRate});
    }

    @Override
    public void performRenovation(List<ScoredBuilding> scoredBuildings, Integer currentYear, Random pseudoRandomGenerator) {
        LOGGER.info("Performing renovation of given List of scored buildings");

        long noRenovatedBuildings = (long) (scoredBuildings.size() * (renovationRate / 100d));
        // Limit the stream of scored buildings so that only renovate rate percentage buildings are selected for a renovation
        scoredBuildings.stream().limit(noRenovatedBuildings).forEachOrdered(sb -> {
            //LOGGER.log(Level.INFO, "Renvating building {0}\twith score {1}", new Object[]{sb.getBuilding().getAlkisID(), sb.getScore()});
            Building building = sb.getBuilding();

            // renovation of the building hull
            if (building.getRenovationLevel().equals(RenovationLevel.NO_RENOVATION)) {
                // Perform a "normal" or passive house standard renovation depending on the passive house rate
                if (pseudoRandomGenerator.nextDouble() * 100 <= passiveHouseRate) {
                    building.renovate(RenovationLevel.GOOD_RENOVATION, currentYear);
                } else {
                    building.renovate(RenovationLevel.BASIC_RENOVATION, currentYear);
                }
            } else if (building.getRenovationLevel().equals(RenovationLevel.BASIC_RENOVATION)) {
                building.renovate(RenovationLevel.GOOD_RENOVATION, currentYear);
            }
            
            // renovation/exchange of heating system
            for (HeatingType heatingType : this.heatingExchangeClasses.keySet()) {
                if (heatingType.equals(building.getHeatingType())) {
                   Double coinToss = pseudoRandomGenerator.nextDouble() * 100;
                   Map<Range<Double>, HeatingType> classes = this.heatingExchangeClasses.get(heatingType);
                   
                   for (Range<Double> classRange : classes.keySet()) {
                       if (classRange.contains(coinToss)) {
                           building.exchangeHeatingSystem(classes.get(classRange));
                           //LOGGER.log(Level.INFO, "Changed heating systemn in building {0} from {1} to {2}", new Object[]{building.getAlkisID(), heatingType, building.getHeatingType()});
                       }
                   }
                }
            }
        });
    }

    private void createHeatingExchangeClasses(List<HeatingSystemExchangeRate> heatingSystemExchangeRates) {
        this.heatingExchangeClasses = new HashMap<>();
       
        heatingSystemExchangeRates.forEach((exchangeRate) -> {
            exchangeRate.normalize();
            HeatingType originType = exchangeRate.getOldType();
            Map<Range<Double>, HeatingType> transitions = new HashMap<>();
            
            Double a = 0d;
            Double b = a + exchangeRate.getLowTempBoilerRate();
            Range<Double> ltb = Range.closed(a, b);
            transitions.put(ltb, HeatingType.LOW_TEMPERATURE_BOILER);
            
            a = b;
            b = a + exchangeRate.getDistrictHeatRate();
            Range<Double> dh = Range.openClosed(a, b);
            transitions.put(dh, HeatingType.DISTRICT_HEAT);
            
            a = b;
            b = a + exchangeRate.getCondensingBoilerRate();
            Range<Double> cb = Range.openClosed(a, b);
            transitions.put(cb, HeatingType.CONDENSING_BOILER);
            
            a = b;
            b = a + exchangeRate.getCondBoilerSolarRate();
            Range<Double> cbs = Range.openClosed(a,b);
            transitions.put(cbs, HeatingType.CONDENSING_BOILER_SOLAR);
            
            a = b;
            b = a + exchangeRate.getPelletsRate();
            Range<Double> p = Range.openClosed(a, b);
            transitions.put(p, HeatingType.PELLETS);
            
            a = b;
            b = a + exchangeRate.getHeatPumpHRRate();
            Range<Double> hr = Range.openClosed(a, b);
            transitions.put(hr, HeatingType.HEAT_PUMP_HEAT_RECOVERY);
            
            a = b;
            b = a + exchangeRate.getPelletsSolarHRRate();
            Range<Double> phr = Range.openClosed(a, b);
            transitions.put(phr, HeatingType.PELLETS_SOLAR_HEAT_RECOVERY);
            
            a = b;
            b = a + exchangeRate.getDistrictHeatHRRate();
            Range<Double> dhhr = Range.openClosed(a, b);
            transitions.put(dhhr, HeatingType.DISTRICT_HEAT_HEAT_RECOVERY);
            
            a = b;
            b = a + exchangeRate.getCondBoilerSolarHRRate();
            Range<Double> cbshr = Range.openClosed(a, b);
            transitions.put(cbshr, HeatingType.CONDENSING_BOILER_SOLAR_HEAT_RECOVERY);
            
            this.heatingExchangeClasses.put(originType, transitions);
        });
        
        printHeatingExchangeClasses();
    }

    private void printHeatingExchangeClasses() {
        this.heatingExchangeClasses.keySet().stream().peek((originType) -> LOGGER.log(Level.INFO, "Heating Exchange Classes for: {0}", originType)).forEachOrdered((originType) -> {
            this.heatingExchangeClasses.get(originType).keySet().forEach((range) -> {
                HeatingType toType = this.heatingExchangeClasses.get(originType).get(range);
                
                LOGGER.log(Level.INFO, "{0}\t-->\t{1}", new Object[]{range, toType});
            });
        });
    }
}
