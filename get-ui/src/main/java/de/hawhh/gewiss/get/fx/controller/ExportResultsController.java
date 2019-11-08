package de.hawhh.gewiss.get.fx.controller;

import de.hawhh.gewiss.get.core.output.SimulationResult;
import de.hawhh.gewiss.get.fx.SimulationResultHolder;
import de.hawhh.gewiss.get.simulator.db.dao.PostgresConnection;
import de.hawhh.gewiss.get.simulator.db.dao.SimulationResultDAO;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for ExportResults.fxml
 *
 * @author Thomas Preisler
 */
public class ExportResultsController {

    @FXML
    private TextField hostField;
    @FXML
    private TextField portField;
    @FXML
    private TextField databaseField;
    @FXML
    private TextField userField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button connectButton;
    @FXML
    private Button disconnectButton;
    @FXML
    private Button exportButton;
    @FXML
    private TextArea logArea;
    @FXML
    private ProgressBar exportProgress;

    private Connection connection;

    private final static Logger LOGGER = Logger.getLogger(ExportResultsController.class.getName());

    public void init() {
        LOGGER.info("Initializing ExportResultsController");
        
        logArea.setWrapText(true);
    }

    @FXML
    private void connect() {
        String host = hostField.getText();
        Integer port = Integer.valueOf(portField.getText());
        String database = databaseField.getText();
        String user = userField.getText();
        String pwd = passwordField.getText();

        Connection conn = PostgresConnection.getConnection(host, database, port, user, pwd);
        if (conn != null) {
            this.connection = conn;
            disconnectButton.setDisable(false);
            exportButton.setDisable(false);
            connectButton.setDisable(true);
            logArea.appendText("Successfully connected to GeHIT Postgres database.\n");
        } else {
            logArea.appendText("Error connecting to GeHIT Postgres database, please check input parameters.\n");
        }
    }

    @FXML
    private void disconnect() {
        if (connection != null) {
            try {
                connection.close();
                disconnectButton.setDisable(true);
                exportButton.setDisable(true);
                connectButton.setDisable(false);

                logArea.appendText("Closed connection to GeHIT database.\n");
            } catch (SQLException ex) {
                Logger.getLogger(ExportResultsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    private void export() {
        SimulationResult result = SimulationResultHolder.getInstance().getResult();
        if (result != null) {
            exportProgress.setProgress(-1d);
            exportButton.setDisable(true);
            disconnectButton.setDisable(true);
            logArea.appendText("Export started (may take a long time).\n");

            Task<Integer> exportTask = new Task<Integer>() {
                @Override
                protected Integer call() {
                    SimulationResultDAO dao = new SimulationResultDAO(connection);

                    return dao.save(result);
                }
            };
            exportTask.setOnSucceeded(event -> {
                try {
                    Integer simId = exportTask.get();
                    exportButton.setDisable(false);
                    disconnectButton.setDisable(false);
                    exportProgress.setProgress(1d);
                    logArea.appendText("Simulation results successfully exported to GeHIT database (sim_id = " + simId + ")\n");
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(ExportResultsController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            new Thread(exportTask).start();
        } else {
            logArea.appendText("No simulation was performed, therefore no results to export.\n");
        }
    }
}
