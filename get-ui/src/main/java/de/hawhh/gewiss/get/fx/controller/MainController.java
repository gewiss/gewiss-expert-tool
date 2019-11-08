package de.hawhh.gewiss.get.fx.controller;

import de.hawhh.gewiss.get.fx.SimulationResultHolder;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.util.Objects;
import java.util.logging.Logger;

/**
 * Main controller of the Simbar application. Used for inter-controller communication, in case UI elements have to be update from another controller.
 *
 * @author Thomas Preisler
 */
public class MainController {

    private final static Logger LOGGER = Logger.getLogger(MainController.class.getName());

    @FXML
    private InputController inputController;
    @FXML
    private ProgressController progressController;
    @FXML
    private ResultsController resultsController;

    @FXML
    private TabPane tabPane;
    @FXML
    private Tab progressTab;
    @FXML
    private ScrollPane mainScrollPane;

    private Integer duration;

    @FXML
    public void initialize() {
        LOGGER.info("Initializing MainController");

        inputController.init(this);
        progressController.init(this);
        resultsController.init(this);
    }

    /**
     * Method is called via menu - close and closes the application.
     */
    @FXML
    private void menuClose() {
        LOGGER.info("Closing application via menu - close");
        Platform.exit();
    }

    @FXML
    private void menuAbout() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText(null);
        alert.setContentText(""
                + "The GEWISS Simulation Tool was developed by:\n"
                + "- Thomas Preisler (HAW Hamburg)\n"
                + "- Nils Weiss (HAW Hamburg)\n"
                + "- Antony Sotirov (HAW Hamburg)\n\n"
                + "The data model as well as the actual data was provided by Ivan Dochev (HCU Hamburg).\n\n"
                + "Additional contributors: Arjun Jamil (HAW Hamburg), Ev Köhler (HAW Hamburg), Hannes Seller (HCU Hamburg).\n\n"
                + "Project lead: Wolfgang Renz (HAW Hamburg), Irene Peters (HCU Hamburg).\n\n"
                + "All participants were part of the GEWISS Project (http://gewiss.haw-hamburg.de/) funded by the German Federal Ministry for Economic Affairs and Energy.\n\n"
                + "Icon made by Freepik (www.freepik.com) from www.flaticon.com.");

        alert.showAndWait();
    }

    /**
     * Method is called upon simulation start to switch to the progress panel and start the progress bar.
     *
     * @param simName
     */
    public void simulationStart(String simName) {
        Platform.runLater(() -> {
            this.progressController.setSimName(simName);

            // switch to the progress pane and scroll up
            tabPane.getSelectionModel().select(progressTab);
            mainScrollPane.setVvalue(0d);

            this.progressController.startProgressBar();
            this.progressController.appendLog("Simulation " + simName + " started");
            this.progressController.appendLog("Loading buildings");
        });
    }

    /**
     * Method is called when the simulation finishes.
     *
     * @param runTime the simulation run time in ms
     */
    public void simulationStop(long runTime) {
        this.progressController.stopProgressBar();
        double timeInS = runTime / 1000d;
        this.progressController.appendLog("Simulation finished in " + timeInS + "s");
        this.resultsController.updateYearBox(SimulationResultHolder.getInstance().getResult().getOutput().keySet());
    }

    public void simulationProgess(Integer currentYear, Integer lastYear) {
        if (duration == null) {
            duration = (lastYear - currentYear) + 1;
        }

        Double progress = (duration.doubleValue() - (lastYear.doubleValue() - currentYear.doubleValue())) / duration.doubleValue();

        // Make sure that the changes to the UI are performed thread-safe
        Platform.runLater(() -> {
            this.progressController.appendLog("Simulated year " + currentYear + "/" + lastYear);
            this.progressController.setProgress(progress);
        });

        if (Objects.equals(currentYear, lastYear)) {
            duration = null;
        }
    }

    public void abortSimulation() {
        this.inputController.abortRunningSim();
    }

    public void simulationAbort() {
        Platform.runLater(() -> {
            this.progressController.stopProgressBar();
            this.progressController.appendLog("Simulation aborted");
        });
    }
}
