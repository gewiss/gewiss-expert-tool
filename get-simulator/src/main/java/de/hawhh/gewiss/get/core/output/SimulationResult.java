package de.hawhh.gewiss.get.core.output;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import de.hawhh.gewiss.get.core.model.RenovationLevel;
import lombok.Data;
import org.javatuples.Triplet;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Data class for storing the results of a simulation scenario.
 *
 * @author Thomas Preisler
 */
@Data
public class SimulationResult {

    private Long seed;
    private String name;
    private String parameter;
    private Multimap<Integer, SimulationOutput> output = MultimapBuilder.treeKeys().arrayListValues().build();
    private Map<String, BuildingInformation> buildings = new HashMap<>();
    private Long runTime;

    @Override
    public String toString() {
        return "Simulation scenario: " + this.name;
    }

    /**
     * Returns the min overall yel
     *
     * @return
     */
    public Double getMinHeatHemand() {
        return output.values().stream().min((o1, o2) -> Double.compare(o1.getHeatDemand(), o2.getHeatDemand())).get().getHeatDemand();
    }

    public Double getMaxHeatHemand() {
        return output.values().stream().max((o1, o2) -> Double.compare(o1.getHeatDemand(), o2.getHeatDemand())).get().getHeatDemand();
    }

    /**
     * Returns a map where years of the simulations are the keys and the overall yearly heat demand is the value.
     *
     * @param quarter specify a quarter if just results for this quarter should be considered, otherwise provide <code>null</code>
     * @return
     */
    public Map<Integer, Double> getYearlyHeatDemand(String quarter) {
        Map<Integer, Double> heatDemandMap = new TreeMap<>();

        final Set<String> quarterBuildings = new HashSet<>();

        if (quarter != null) {
            // if set filter just the matching results
            quarterBuildings.addAll(buildings.values().stream().filter(building -> quarter.equals(building.getQuarter())).map(BuildingInformation::getBuildingId).collect(Collectors.toSet()));
        }

        output.keySet().forEach(year -> {
            Double heatDemand = 0d;

            if (quarter == null) {
                // if quarter is not set consider all results
                heatDemand = output.get(year).stream().mapToDouble(SimulationOutput::getHeatDemand).sum();
            } else {
                // if set filter just the matching results
                heatDemand = output.get(year).stream().filter(building -> quarterBuildings.contains(building.getBuildingId())).mapToDouble(SimulationOutput::getHeatDemand).sum();
            }

            heatDemandMap.put(year, heatDemand);
        });

        return heatDemandMap;
    }

    /**
     * Returns a map where years of the simulations are the keys and a triplet containing the number of buildings in the three different renovation levels as value.
     *
     * @param quarter specify a quarter if just results for this quarter should be considered, otherwise provide <code>null</code>
     * @return
     */
    public Map<Integer, Triplet<Long, Long, Long>> getRenovationLevels(String quarter) {
        Map<Integer, Triplet<Long, Long, Long>> renovationLevelMap = new TreeMap<>();

        final Set<String> quarterBuildings = new HashSet<>();

        if (quarter != null) {
            // if set filter just the matching results
            quarterBuildings.addAll(buildings.values().stream().filter(building -> quarter.equals(building.getQuarter())).map(BuildingInformation::getBuildingId).collect(Collectors.toSet()));
        }

        output.keySet().forEach(year -> {
            Long noRenovation = 0L, basicRenovation = 0L, goodRenovation = 0L;

            if (quarter == null) {
                // if quarter is not set consider all results
                noRenovation = output.get(year).stream().filter(so -> RenovationLevel.NO_RENOVATION.equals(so.getRenovationLevel())).count();
                basicRenovation = output.get(year).stream().filter(so -> RenovationLevel.BASIC_RENOVATION.equals(so.getRenovationLevel())).count();
                goodRenovation = output.get(year).stream().filter(so -> RenovationLevel.GOOD_RENOVATION.equals(so.getRenovationLevel())).count();
            } else {

                noRenovation = output.get(year).stream().filter(building -> quarterBuildings.contains(building.getBuildingId()) && RenovationLevel.NO_RENOVATION.equals(building.getRenovationLevel())).count();
                basicRenovation = output.get(year).stream().filter(building -> quarterBuildings.contains(building.getBuildingId()) && RenovationLevel.BASIC_RENOVATION.equals(building.getRenovationLevel())).count();
                goodRenovation = output.get(year).stream().filter(building -> quarterBuildings.contains(building.getBuildingId()) && RenovationLevel.GOOD_RENOVATION.equals(building.getRenovationLevel())).count();
            }

            Triplet<Long, Long, Long> data = new Triplet(noRenovation, basicRenovation, goodRenovation);
            renovationLevelMap.put(year, data);
        });

        return renovationLevelMap;
    }

    /**
     * Returns a map where years of the simulations are the keys and the overall yearly co2 emission is the value.
     *
     * @param quarter specify a quarter if just results for this quarter should be considered, otherwise provide <code>null</code>
     * @return
     */
    public Map<Integer, Double> getCO2Emissions(String quarter) {
        Map<Integer, Double> emissionsMap = new TreeMap<>();

        final Set<String> quarterBuildings = new HashSet<>();

        if (quarter != null) {
            // if set filter just the matching results
            quarterBuildings.addAll(buildings.values().stream().filter(building -> quarter.equals(building.getQuarter())).map(BuildingInformation::getBuildingId).collect(Collectors.toSet()));
        }

        output.keySet().forEach(year -> {
            Double emission = 0d;

            if (quarter == null) {
                emission = output.get(year).stream().mapToDouble(SimulationOutput::getCo2Emission).sum();
            } else {
                emission = output.get(year).stream().filter(building -> quarterBuildings.contains(building.getBuildingId())).mapToDouble(SimulationOutput::getCo2Emission).sum();
            }

            // transformation from g/year to tons/year.
            emission /= 1000000;

            emissionsMap.put(year, emission);
        });

        return emissionsMap;
    }

    /**
     * Returns a map where years of the simulations are the keys and the overall yearly renovation cost is the value.
     *
     * @param quarter specify a quarter if just results for this quarter should be considered, otherwise provide <code>null</code>
     * @return
     */
    public Map<Integer, Double> getRenovationCosts(String quarter) {
        Map<Integer, Double> costsMap = new TreeMap<>();

        final Set<String> quarterBuildings = new HashSet<>();

        if (quarter != null) {
            // if set filter just the matching results
            quarterBuildings.addAll(buildings.values().stream().filter(building -> quarter.equals(building.getQuarter())).map(BuildingInformation::getBuildingId).collect(Collectors.toSet()));
        }

        output.keySet().forEach(year -> {
            Double cost = 0d;

            if (quarter == null) {
                cost = output.get(year).stream().mapToDouble(SimulationOutput::getRenovationCost).sum();
            } else {
                cost = output.get(year).stream().filter(building -> quarterBuildings.contains(building.getBuildingId())).mapToDouble(SimulationOutput::getRenovationCost).sum();
            }

            costsMap.put(year, cost);
        });

        return costsMap;
    }

    /**
     * Print the overall heat demand for each year of the simulation to the console.
     */
    public void printHeatDemand() {
        System.out.println("Heat demand for simulation: " + name);
        System.out.println("Year\tHeat Demand (kWh/m^2)");

        output.keySet().forEach((year) -> {
            Collection<SimulationOutput> outputs = output.get(year);
            Double heatDemand = outputs.stream().mapToDouble(SimulationOutput::getHeatDemand).sum();
            System.out.print(year + "\t");
            System.out.printf("%f\n", heatDemand);
        });
    }

    /**
     * Print the number of renovation levels for each year of the simulation to the console.
     */
    public void printRenovationLevelDemand() {
        System.out.println("Renovation level for simulation: " + name);
        System.out.println("Year\tLevel0\tLevel1\tLevel2");

        output.keySet().forEach((year) -> {
            Collection<SimulationOutput> outputs = output.get(year);
            Long ren0 = outputs.stream().filter(so -> so.getRenovationLevel().equals(RenovationLevel.NO_RENOVATION)).count();
            Long ren1 = outputs.stream().filter(so -> so.getRenovationLevel().equals(RenovationLevel.BASIC_RENOVATION)).count();
            Long ren2 = outputs.stream().filter(so -> so.getRenovationLevel().equals(RenovationLevel.GOOD_RENOVATION)).count();
            System.out.print(year + "\t");
            System.out.printf("%d\t%d\t%d\n", ren0, ren1, ren2);
        });
    }
}
