package de.hawhh.gewiss.get.fx.controller;

import de.hawhh.gewiss.get.core.input.HeatingSystemExchangeRate;
import de.hawhh.gewiss.get.core.model.HeatingType;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;

/**
 * Controller for the InitialParameter.fxml view.
 *
 * @author Thomas Preisler
 */
public class InitialParametersController {

    // Initial simulation parameters
    @FXML
    private TextField globalName;
    @FXML
    private TextField globalRenovationRate;
    @FXML
    private TextField globalUntil;
    @FXML
    private TextField globalPassiveHouse;
    @FXML
    private TextField globalSeed;

    // Heating System Exchange Table
    @FXML
    private TableView<HeatingSystemExchangeRate> heatingSystemExchangeTable;
    @FXML
    private TableColumn<HeatingSystemExchangeRate, String> oldType;
    @FXML
    private TableColumn<HeatingSystemExchangeRate, Double> lowTempBoilerRate;
    @FXML
    private TableColumn<HeatingSystemExchangeRate, Double> districtHeatRate;
    @FXML
    private TableColumn<HeatingSystemExchangeRate, Double> condensingBoilerRate;
    @FXML
    private TableColumn<HeatingSystemExchangeRate, Double> condBoilerSolarRate;
    @FXML
    private TableColumn<HeatingSystemExchangeRate, Double> pelletsRate;
    @FXML
    private TableColumn<HeatingSystemExchangeRate, Double> heatPumpHRRate;
    @FXML
    private TableColumn<HeatingSystemExchangeRate, Double> pelletsSolarHRRate;
    @FXML
    private TableColumn<HeatingSystemExchangeRate, Double> districtHeatHRRate;
    @FXML
    private TableColumn<HeatingSystemExchangeRate, Double> condBoilerSolarHRRate;

    /**
     * Initializes the heating system exchanges table and connects the view to the underlying data model.
     */
    private void initHeatExchangeTable() {
        // Define the connection between data model and table columns
        oldType.setCellValueFactory(new PropertyValueFactory<>("oldType"));

        lowTempBoilerRate.setCellValueFactory(new PropertyValueFactory<>("lowTempBoilerRate"));
        lowTempBoilerRate.setEditable(true);
        lowTempBoilerRate.setCellFactory(TextFieldTableCell.<HeatingSystemExchangeRate, Double>forTableColumn(new DoubleStringConverter()));
        lowTempBoilerRate.setOnEditCommit((TableColumn.CellEditEvent<HeatingSystemExchangeRate, Double> event) -> {
            TablePosition<HeatingSystemExchangeRate, Double> pos = event.getTablePosition();
            Double newValue = event.getNewValue();

            int row = pos.getRow();
            HeatingSystemExchangeRate hser = event.getTableView().getItems().get(row);
            hser.setLowTempBoilerRate(newValue);
        });

        districtHeatRate.setCellValueFactory(new PropertyValueFactory<>("districtHeatRate"));
        districtHeatRate.setEditable(true);
        districtHeatRate.setCellFactory(TextFieldTableCell.<HeatingSystemExchangeRate, Double>forTableColumn(new DoubleStringConverter()));
        districtHeatRate.setOnEditCommit((TableColumn.CellEditEvent<HeatingSystemExchangeRate, Double> event) -> {
            TablePosition<HeatingSystemExchangeRate, Double> pos = event.getTablePosition();
            Double newValue = event.getNewValue();

            int row = pos.getRow();
            HeatingSystemExchangeRate hser = event.getTableView().getItems().get(row);
            hser.setDistrictHeatRate(newValue);
        });

        condensingBoilerRate.setCellValueFactory(new PropertyValueFactory<>("condensingBoilerRate"));
        condensingBoilerRate.setEditable(true);
        condensingBoilerRate.setCellFactory(TextFieldTableCell.<HeatingSystemExchangeRate, Double>forTableColumn(new DoubleStringConverter()));
        condensingBoilerRate.setOnEditCommit((TableColumn.CellEditEvent<HeatingSystemExchangeRate, Double> event) -> {
            TablePosition<HeatingSystemExchangeRate, Double> pos = event.getTablePosition();
            Double newValue = event.getNewValue();

            int row = pos.getRow();
            HeatingSystemExchangeRate hser = event.getTableView().getItems().get(row);
            hser.setCondensingBoilerRate(newValue);
        });

        condBoilerSolarRate.setCellValueFactory(new PropertyValueFactory<>("condBoilerSolarRate"));
        condBoilerSolarRate.setEditable(true);
        condBoilerSolarRate.setCellFactory(TextFieldTableCell.<HeatingSystemExchangeRate, Double>forTableColumn(new DoubleStringConverter()));
        condBoilerSolarRate.setOnEditCommit((TableColumn.CellEditEvent<HeatingSystemExchangeRate, Double> event) -> {
            TablePosition<HeatingSystemExchangeRate, Double> pos = event.getTablePosition();
            Double newValue = event.getNewValue();

            int row = pos.getRow();
            HeatingSystemExchangeRate hser = event.getTableView().getItems().get(row);
            hser.setCondBoilerSolarRate(newValue);
        });

        pelletsRate.setCellValueFactory(new PropertyValueFactory<>("pelletsRate"));
        pelletsRate.setEditable(true);
        pelletsRate.setCellFactory(TextFieldTableCell.<HeatingSystemExchangeRate, Double>forTableColumn(new DoubleStringConverter()));
        pelletsRate.setOnEditCommit((TableColumn.CellEditEvent<HeatingSystemExchangeRate, Double> event) -> {
            TablePosition<HeatingSystemExchangeRate, Double> pos = event.getTablePosition();
            Double newValue = event.getNewValue();

            int row = pos.getRow();
            HeatingSystemExchangeRate hser = event.getTableView().getItems().get(row);
            hser.setPelletsRate(newValue);
        });

        heatPumpHRRate.setCellValueFactory(new PropertyValueFactory<>("heatPumpHRRate"));
        heatPumpHRRate.setEditable(true);
        heatPumpHRRate.setCellFactory(TextFieldTableCell.<HeatingSystemExchangeRate, Double>forTableColumn(new DoubleStringConverter()));
        heatPumpHRRate.setOnEditCommit((TableColumn.CellEditEvent<HeatingSystemExchangeRate, Double> event) -> {
            TablePosition<HeatingSystemExchangeRate, Double> pos = event.getTablePosition();
            Double newValue = event.getNewValue();

            int row = pos.getRow();
            HeatingSystemExchangeRate hser = event.getTableView().getItems().get(row);
            hser.setHeatPumpHRRate(newValue);
        });

        pelletsSolarHRRate.setCellValueFactory(new PropertyValueFactory<>("pelletsSolarHRRate"));
        pelletsSolarHRRate.setEditable(true);
        pelletsSolarHRRate.setCellFactory(TextFieldTableCell.<HeatingSystemExchangeRate, Double>forTableColumn(new DoubleStringConverter()));
        pelletsSolarHRRate.setOnEditCommit((TableColumn.CellEditEvent<HeatingSystemExchangeRate, Double> event) -> {
            TablePosition<HeatingSystemExchangeRate, Double> pos = event.getTablePosition();
            Double newValue = event.getNewValue();

            int row = pos.getRow();
            HeatingSystemExchangeRate hser = event.getTableView().getItems().get(row);
            hser.setPelletsSolarHRRate(newValue);
        });

        districtHeatHRRate.setCellValueFactory(new PropertyValueFactory<>("districtHeatHRRate"));
        districtHeatHRRate.setEditable(true);
        districtHeatHRRate.setCellFactory(TextFieldTableCell.<HeatingSystemExchangeRate, Double>forTableColumn(new DoubleStringConverter()));
        districtHeatHRRate.setOnEditCommit((TableColumn.CellEditEvent<HeatingSystemExchangeRate, Double> event) -> {
            TablePosition<HeatingSystemExchangeRate, Double> pos = event.getTablePosition();
            Double newValue = event.getNewValue();

            int row = pos.getRow();
            HeatingSystemExchangeRate hser = event.getTableView().getItems().get(row);
            hser.setDistrictHeatHRRate(newValue);
        });

        condBoilerSolarHRRate.setCellValueFactory(new PropertyValueFactory<>("condBoilerSolarHRRate"));
        condBoilerSolarHRRate.setEditable(true);
        condBoilerSolarHRRate.setCellFactory(TextFieldTableCell.<HeatingSystemExchangeRate, Double>forTableColumn(new DoubleStringConverter()));
        condBoilerSolarHRRate.setOnEditCommit((TableColumn.CellEditEvent<HeatingSystemExchangeRate, Double> event) -> {
            TablePosition<HeatingSystemExchangeRate, Double> pos = event.getTablePosition();
            Double newValue = event.getNewValue();

            int row = pos.getRow();
            HeatingSystemExchangeRate hser = event.getTableView().getItems().get(row);
            hser.setCondBoilerSolarHRRate(newValue);
        });

        heatingSystemExchangeTable.setItems(getHeatExchangeList());
        heatingSystemExchangeTable.setEditable(true);
    }

    /**
     * Creates and returns the default list for {@link HeatingSystemExchangeRate}s.
     */
    private ObservableList<HeatingSystemExchangeRate> getHeatExchangeList() {
        return FXCollections.observableArrayList(
                new HeatingSystemExchangeRate(HeatingType.LOW_TEMPERATURE_BOILER, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0),
                new HeatingSystemExchangeRate(HeatingType.DISTRICT_HEAT, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0),
                new HeatingSystemExchangeRate(HeatingType.CONDENSING_BOILER, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0, 100.0 / 9.0)
        );
    }

    /**
     * Initializes and configures input validators for the text fields on this view.
     */
    private void initTextFieldValidators() {
        globalUntil.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) { //when focus lost
                String text = globalUntil.getText();
                try {
                    Integer year = Integer.parseInt(text);
                    if (year > 2100) {
                        globalUntil.setText("2100");
                    } else if (year < 2018) {
                        globalUntil.setText("2018");
                    }
                } catch (NumberFormatException e) {
                    globalUntil.setText("2050");
                }
            }
        });

        globalRenovationRate.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) { //when focus lost
                String text = globalRenovationRate.getText();
                try {
                    Double rate = Double.parseDouble(text);
                    if (rate > 100.0) {
                        globalRenovationRate.setText("100.0");
                    } else if (rate < 0.0) {
                        globalRenovationRate.setText("0.0");
                    }
                } catch (NumberFormatException e) {
                    globalRenovationRate.setText("2.0");
                }
            }
        });

        globalPassiveHouse.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) { //when focus lost
                String text = globalPassiveHouse.getText();
                try {
                    Double rate = Double.parseDouble(text);
                    if (rate > 100.0) {
                        globalPassiveHouse.setText("100.0");
                    } else if (rate < 0.0) {
                        globalPassiveHouse.setText("0.0");
                    }
                } catch (NumberFormatException e) {
                    globalPassiveHouse.setText("10.0");
                }
            }
        });

        globalSeed.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) { //when focus lost
                String text = globalSeed.getText();
                try {
                    Long seed = Long.parseLong(text);
                } catch (NumberFormatException e) {
                    Long seed = System.currentTimeMillis();
                    globalSeed.setText(String.valueOf(seed));
                }
            }
        });
    }

    /**
     * Generates a default seed and sets the according text field.
     */
    @FXML
    private void generateSeed() {
        Long seed = System.nanoTime();
        globalSeed.setText(String.valueOf(seed));
    }

    /**
     * Initializes this controller.
     */
    public void init() {
        initHeatExchangeTable();
        initTextFieldValidators();
    }
    
    public Integer getSimulationUntil() {
        return Integer.parseInt(globalUntil.getText());
    }
    
    public String getSimulationName() {
        return globalName.getText();
    }
    
    public Double getRenovationRate() {
        return Double.parseDouble(globalRenovationRate.getText());
    }
    
    public Double getPassiveHouseRate() {
        return Double.parseDouble(globalPassiveHouse.getText());
    }
    
    public List<HeatingSystemExchangeRate> getHeatingSystemExchangeRates() {
        return heatingSystemExchangeTable.getItems();
    }
    
    public Long getSeed() {
        return Long.parseLong(globalSeed.getText());
    }
}
