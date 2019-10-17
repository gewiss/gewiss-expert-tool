package de.hawhh.gewiss.get.fx.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import de.hawhh.gewiss.get.core.input.HeatingSystemExchangeRate;
import de.hawhh.gewiss.get.core.input.Modifier;
import de.hawhh.gewiss.get.core.input.SimulationParameter;
import de.hawhh.gewiss.get.core.output.SimulationResult;
import de.hawhh.gewiss.get.fx.ExceptionDialog;
import de.hawhh.gewiss.get.fx.SimulationResultHolder;
import de.hawhh.gewiss.get.simulator.Simulator;
import de.hawhh.gewiss.get.simulator.db.dao.BuildingDAO;
import de.hawhh.gewiss.get.simulator.db.dao.DistrictQuarterDAO;
import de.hawhh.gewiss.get.simulator.db.dao.SQLiteBuildingDAO;
import de.hawhh.gewiss.get.simulator.db.dao.SQLiteDistrictQuarterDAO;
import de.hawhh.gewiss.get.simulator.renovation.IRenovationStrategy;
import de.hawhh.gewiss.get.simulator.renovation.RenovationHeatExchangeRateStrategy;
import de.hawhh.gewiss.get.simulator.scoring.BuildingAgeFactor;
import de.hawhh.gewiss.get.simulator.scoring.CO2EmissionFactor;
import de.hawhh.gewiss.get.simulator.scoring.CO2EmissionSquareMeterFactor;
import de.hawhh.gewiss.get.simulator.scoring.ScoringMethod;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FXML Controller class for the Input.fxml.
 *
 * @author Thomas Preisler, Antony Sotirov
 */
@SuppressWarnings("ALL")
public class InputController implements Observer {

    public static final String RESIDENTIAL_BUILDINGS = "Residential";
    public static final String NON_RESIDENTIAL_BUILDINGS = "Non-Residential";

    @FXML
    private InitialParametersController initialParametersController;
    @FXML
    private TabPane modifierTabPane;
    @FXML
    private ModifierController firstModifierController;
    @FXML
    private BorderPane inputPane;

    private Simulator simulator;
    private MainController mainController;
    private BuildingDAO buildingDAO;
    private DistrictQuarterDAO districtQuarterDAO;
    private Thread simThread;

    private List<ModifierController> modifierControllers;

    // Multimap containing all Hamburger Bezirke as keys and the according Stadtteile as values
    private Multimap<String, String> locationMap;
    // Multimap containing IWU and Ecofys as keys and the according building types as values
    private Multimap<String, String> buildingTypeMap;
    // Ordered set of unique building owners
    private List<String> buildingOwnershipTypes;

    private final static Logger LOGGER = Logger.getLogger(InputController.class.getName());

    /**
     * Custom initialization of this controller.
     *
     * @param mc Reference to the main controller
     */
    public void init(MainController mc) {
        mainController = mc;
        simulator = new Simulator();
        buildingDAO = new SQLiteBuildingDAO();
        districtQuarterDAO = new SQLiteDistrictQuarterDAO();
        modifierControllers = new ArrayList<>();

        simulator.addObserver(this);

        locationMap = TreeMultimap.create();
        initLocationMap();
        buildingTypeMap = TreeMultimap.create();
        initBuildingTypeMap();
        buildingOwnershipTypes = new ArrayList<>();
        initBuildingOwnershipTypes();

        initialParametersController.init();
        firstModifierController.init(this);
        modifierControllers.add(firstModifierController);
    }

    @FXML
    private void addModifier() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Modifier.fxml"));
            VBox modifierPane = loader.load();

            //<VBox alignment="CENTER" style="-fx-background-color: WhiteSmoke;">
            VBox container = new VBox();
            container.setAlignment(Pos.CENTER);
            container.setStyle("-fx-background-color: WhiteSmoke;");
            container.getChildren().add(modifierPane);

            Tab tab = new Tab("Modifier X", container);
            modifierTabPane.getTabs().add(tab);
            modifierTabPane.getSelectionModel().select(tab);

            ModifierController controller = loader.<ModifierController>getController();
            controller.init(this);
            modifierControllers.add(controller);

            tab.setOnClosed((Event event) -> {
                modifierControllers.remove(controller);
                LOGGER.log(Level.INFO, "Tab {0} closed", tab.getText());
                LOGGER.log(Level.INFO, "Removed controller from controllers list, remaining controllers: {0}", modifierControllers.size());
            });
        } catch (IOException ex) {
            Logger.getLogger(InputController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void closedFirstModifierPanel() {
        modifierControllers.remove(firstModifierController);
        LOGGER.log(Level.INFO, "Initial modifier tab closed, deleted first modifier controller from controllers list, remaining controllers: {0}", modifierControllers.size());
    }

    public void setTabTitle(String text) {
        Tab tab = modifierTabPane.getSelectionModel().getSelectedItem();
        if (!text.startsWith("Modifier")) {
            tab.setText("Modifier-" + text);
        } else {
            tab.setText(text);
        }
    }

    @FXML
    private void loadModifier() {
        // set the file chooser
        Stage stage = (Stage) inputPane.getScene().getWindow();
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON file (*.json)", "*.json"));

        // Add special support for Guava (Google) datatype for Jackson
        ObjectMapper mapper = new ObjectMapper().registerModule(new GuavaModule());
        // enable toString method of enums to return the value to be mapped
        mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);

        List<File> files = fileChooser.showOpenMultipleDialog(stage);
        files.forEach((file) -> {
            try {
                Modifier modifier = mapper.readValue(file, Modifier.class);
                LOGGER.log(Level.INFO, "Loading Modifier {0} from {1}", new Object[]{modifier.getName(), file.getAbsolutePath()});

                createModifierTab(modifier);
            } catch (IOException ex) {
                Logger.getLogger(InputController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    public void createModifierTab(Modifier modifier) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Modifier.fxml"));
            VBox modifierPane = loader.load();

            //<VBox alignment="CENTER" style="-fx-background-color: WhiteSmoke;">
            VBox container = new VBox();
            container.setAlignment(Pos.CENTER);
            container.setStyle("-fx-background-color: WhiteSmoke;");
            container.getChildren().add(modifierPane);

            String title = modifier.getName().startsWith("Modifier") ? modifier.getName() : "Modifier-" + modifier.getName();
            Tab tab = new Tab(title, container);
            modifierTabPane.getTabs().add(tab);
            modifierTabPane.getSelectionModel().select(tab);

            ModifierController controller = loader.<ModifierController>getController();
            controller.init(this, modifier);
            modifierControllers.add(controller);
        } catch (IOException ex) {
            Logger.getLogger(InputController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initLocationMap() {
        districtQuarterDAO.getDistricts().forEach(district -> {
            districtQuarterDAO.getQuarters(district).forEach(quarter -> {
                locationMap.put(district, quarter);
            });
        });
    }

    private void initBuildingTypeMap() {
        buildingTypeMap.putAll(RESIDENTIAL_BUILDINGS, buildingDAO.getResidentialBuildingTypes());
        buildingTypeMap.putAll(NON_RESIDENTIAL_BUILDINGS, buildingDAO.getNonResidentialBuildingTypes());
    }

    private void initBuildingOwnershipTypes() {
        buildingOwnershipTypes.addAll(buildingDAO.getOwnershipTypes());
    }

    public Multimap<String, String> getLocationMap() {
        return locationMap;
    }

    public Multimap<String, String> getBuildingTypeMap() {
        return buildingTypeMap;
    }

    public List<String> getBuildingOwnershipTypes() {
        return buildingOwnershipTypes;
    }

    @FXML
    public void startSimulation() {
        String name = initialParametersController.getSimulationName();

        SimTask simTask = new SimTask();
        simTask.setOnSucceeded(event -> {
            try {
                SimulationResult result = simTask.get();
                SimulationResultHolder.getInstance().setResult(result);

                mainController.simulationStop(simTask.get().getRunTime());
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(InputController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        simThread = new Thread(simTask);
        simThread.start();
    }

    public void abortRunningSim() {
        LOGGER.info("Stopping Sim Thread");
        simThread.stop();
        LOGGER.info("Sim Thread stopped");

        mainController.simulationAbort();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof Simulator && arg instanceof Integer) {
            Integer year = (Integer) arg;
            mainController.simulationProgess(year, initialParametersController.getSimulationUntil());
        }
    }

    /**
     * A simulation task to perform the simulation in an async extra thread to the applications/UIs main thread.
     */
    private class SimTask extends Task<SimulationResult> {

        @Override
        protected SimulationResult call() {
            try {
                // Simulation parameters
                Integer lastYear = initialParametersController.getSimulationUntil();
                String name = initialParametersController.getSimulationName();
                List<Modifier> modifiers = new ArrayList<>();
                modifierControllers.forEach(mc -> {
                    Modifier modifier = mc.getModifier();
                    if (modifier != null) {
                        modifiers.add(mc.getModifier());
                    }
                });
                SimulationParameter parameters = new SimulationParameter(name, lastYear, modifiers);

                // Ranking/scoring methods
                List<ScoringMethod> scoringMethods = new ArrayList<>();
                if (initialParametersController.getSelectedRankingMethods().contains(InitialParametersController.BUILDING_AGE)) {
                    scoringMethods.add(new BuildingAgeFactor());
                } else if (initialParametersController.getSelectedRankingMethods().contains(InitialParametersController.CO2_EMISSION)) {
                    scoringMethods.add(new CO2EmissionFactor());
                } else if (initialParametersController.getSelectedRankingMethods().contains(InitialParametersController.CO2_EMISSION_SQUARE_METER)) {
                    scoringMethods.add(new CO2EmissionSquareMeterFactor());
                }
                
                // Renovation strategy (hard-coded to RenovationHeatExchangeRateStrategy!)
                Double renovationRate = initialParametersController.getRenovationRate();
                Double passiveHouseRate = initialParametersController.getPassiveHouseRate();
                List<HeatingSystemExchangeRate> heatingSystemExchangeRates = initialParametersController.getHeatingSystemExchangeRates();
                IRenovationStrategy renovationStrategy = new RenovationHeatExchangeRateStrategy(renovationRate, passiveHouseRate, heatingSystemExchangeRates);

                // Random seed
                Long seed = initialParametersController.getSeed();

                // Tell the main controller that the simulation is going to start, so the panel is switched
                mainController.simulationStart(name);

                // Clear the old sim result to free memory
                SimulationResultHolder.getInstance().setResult(null);
                
                long startTime = System.currentTimeMillis();
                SimulationResult result = simulator.simulate(parameters, scoringMethods, renovationStrategy, seed);
                long endTime = System.currentTimeMillis();
                long runTime = endTime - startTime;

                LOGGER.log(Level.INFO, "Simulated scenario {0} in {1} ms", new Object[]{result.getName(), runTime});
                result.setRunTime(runTime);

                return result;
            } catch (Exception ex) {
                //Logger.getLogger(InputController.class.getName()).log(Level.SEVERE, null, e);

                Platform.runLater(() -> {
                    ExceptionDialog exDialog = new ExceptionDialog();
                    exDialog.setTitle("Input validation error");
                    exDialog.setHeaderText("An exception occured during validation of simulation input.\nPlease make sure that all required input fields are filled with valid values.");
                    exDialog.setContentText(ex.getClass().getName() + ": "+ ex.getMessage());
                    exDialog.setException(ex);
                    exDialog.showAndWait();
                });
            }
            return null;
        }
    }
}
