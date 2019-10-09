package de.hawhh.gewiss.get.simulator;

import de.hawhh.gewiss.get.core.input.HeatingSystemExchangeRate;
import de.hawhh.gewiss.get.core.input.InputValidationException;
import de.hawhh.gewiss.get.core.input.Modifier;
import de.hawhh.gewiss.get.core.input.SimulationParameter;
import de.hawhh.gewiss.get.core.model.Building;
import de.hawhh.gewiss.get.core.model.HeatingType;
import de.hawhh.gewiss.get.core.output.SimulationResult;
import de.hawhh.gewiss.get.simulator.model.ScoredBuilding;
import de.hawhh.gewiss.get.simulator.renovation.IRenovationStrategy;
import de.hawhh.gewiss.get.simulator.renovation.RenovationHeatExchangeRateStrategy;
import de.hawhh.gewiss.get.simulator.scoring.BuildingAgeFactor;
import de.hawhh.gewiss.get.simulator.scoring.CO2EmissionFactor;
import de.hawhh.gewiss.get.simulator.scoring.CO2EmissionSquareMeterFactor;
import de.hawhh.gewiss.get.simulator.scoring.ScoringMethod;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Thomas Preisler
 */

public class SimulatorTest {

    private SimulationParameter simParams;
    private List<ScoringMethod> scoringMethods;
    private IRenovationStrategy renovationStrategy;

    private Simulator simulator;
    private List<Building> buildings;

    @Before
    public void setUp() {
        Integer simStop = 2020;
        String name = "TestRun-" + LocalDateTime.now();

        List<Modifier> modifiers = new ArrayList<>();
        Modifier mod1 = new Modifier("OneFamilyHousesInAltona", 2020, 2030, 2d);
        mod1.setTargetQuarters(Arrays.asList("Nienstedten", "Othmarschen", "Ottensen", "Altona-Altstadt", "Rissen", "Blankenese", "Osdorf", "Iserbrook", "Groß Flottbek",
                "Bahrenfeld", "Altona-Nord", "Sternschanze", "Sülldorf", "Lurup"));
        mod1.setTargetBuildingsTypes(Arrays.asList("EFH_C", "EFH_I", "EFH_B", "EFH_G", "EFH_A", "EFH_F", "EFH_J", "EFH_K", "EFH_L", "EFH_H", "EFH_E", "EFH_D"));
        mod1.setTargetOwnershipTypes(Arrays.asList("PRIVAT", "UNKNOWN"));
        modifiers.add(mod1);

        this.simParams = new SimulationParameter(name, simStop, modifiers);

        this.scoringMethods = new ArrayList<>();
        this.scoringMethods.add(new BuildingAgeFactor());
        this.scoringMethods.add(new CO2EmissionFactor());
        this.scoringMethods.add(new CO2EmissionSquareMeterFactor());

        HeatingSystemExchangeRate rate1 = new HeatingSystemExchangeRate(HeatingType.LOW_TEMPERATURE_BOILER, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0,
                100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0);
        HeatingSystemExchangeRate rate2 = new HeatingSystemExchangeRate(HeatingType.DISTRICT_HEAT, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0,
                100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0);
        List<HeatingSystemExchangeRate> rates = new ArrayList<>(Arrays.asList(rate1, rate2));

        this.renovationStrategy = new RenovationHeatExchangeRateStrategy(2.0, 0.0, rates);

        this.simulator = new Simulator();
        this.buildings = this.simulator.fetchBuildings();
    }

    @Test
    public void simulate() {
        SimulationResult simulationResult = null;
        try {
            simulationResult = this.simulator.simulate(this.simParams, this.scoringMethods, this.renovationStrategy, 815L);
        } catch (InputValidationException e) {
            e.printStackTrace();
        }

        Assert.assertNotNull(simulationResult);
        Assert.assertNotNull(simulationResult.getBuildings());
        Assert.assertNotNull(simulationResult.getOutput());
        Assert.assertNotNull(simulationResult.getName());
        Assert.assertNotNull(simulationResult.getParameter());
        Assert.assertNotNull(simulationResult.getRunTime());
        Assert.assertFalse(simulationResult.getOutput().isEmpty());
        Assert.assertFalse(simulationResult.getBuildings().isEmpty());
    }

    @Test
    public void scoreBuildings() {
        List<ScoredBuilding> scoredBuildings = scoreBuildings(buildings);

        Assert.assertNotNull(scoredBuildings);
        Assert.assertFalse(scoredBuildings.isEmpty());

        scoredBuildings.forEach(sb -> {
            Assert.assertNotNull(sb.getBuilding());
            Assert.assertNull(sb.getCombinedScore());
            Assert.assertNotNull(sb.getScores());
            Assert.assertFalse(sb.getScores().isEmpty());
        });
    }

    @Test
    public void normalizeScore() {
        List<ScoredBuilding> scoredBuildings = scoreBuildings(buildings);

        this.simulator.normalizeScores(scoredBuildings, this.scoringMethods);

        scoredBuildings.forEach(sb -> {
            sb.getScores().values().forEach(score -> {
                Assert.assertNotNull(score);
                Assert.assertTrue(score >= 0D);
                Assert.assertTrue(score <= 1D);
            });
        });
    }

    @Test
    public void combineScores() {
        List<ScoredBuilding> scoredBuildings = scoreBuildings(buildings);

        this.simulator.combineScores(scoredBuildings);

        scoredBuildings.forEach(sb -> Assert.assertNotNull(sb.getCombinedScore()));
    }

    private List<ScoredBuilding> scoreBuildings(List<Building> buildings) {
        return buildings.stream().parallel().map(building -> {
            ScoredBuilding scoredBuilding = new ScoredBuilding(building);

            // Calc and store the scores for all scoring methods
            scoringMethods.forEach(method -> {
                Double score = method.calcBaseScore(building, 2020);
                scoredBuilding.getScores().put(method, score);
            });

            return scoredBuilding;
        }).collect(Collectors.toList());
    }
}
