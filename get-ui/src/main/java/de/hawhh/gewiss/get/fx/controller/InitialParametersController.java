package de.hawhh.gewiss.get.fx.controller;

import de.hawhh.gewiss.get.core.input.CO2FactorsData;
import de.hawhh.gewiss.get.core.input.HeatingSystemExchangeRate;
import de.hawhh.gewiss.get.core.input.SimulationParameter;
import de.hawhh.gewiss.get.core.model.HeatingType;
import de.hawhh.gewiss.get.core.model.RenovationType;
import de.hawhh.gewiss.get.simulator.db.dao.PrimaryEnergyFactorsDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;
import org.controlsfx.control.CheckComboBox;
import java.util.List;

/**
 * Controller for the InitialParameter.fxml view.
 *
 * @author Thomas Preisler, Antony Sotirov
 */
public class InitialParametersController {
    
    public static final String BUILDING_AGE = "Building Age/Last Renovation";
    public static final String CO2_EMISSION = "CO2 Emission";
    public static final String CO2_EMISSION_SQUARE_METER = "CO2 Emission (m^2)";
    public static final Integer START_CO2_YEAR = SimulationParameter.FIRST_YEAR;
    public static final Integer MID_CO2_YEAR = 2030;
    public static final Integer FINAL_CO2_YEAR = 2050;
    private PrimaryEnergyFactorsDAO energyFactorsDAO;

    // UI elements
    @FXML
    private Accordion accordion;
    @FXML
    private TitledPane co2Pane;
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
    @FXML
    private CheckComboBox<String> rankingMethods;
    // Heating System Exchange Table
    @FXML
    private TableView<HeatingSystemExchangeRate> heatingSystemExchangeTable;
    @FXML
    private TableColumn<HeatingSystemExchangeRate, String> renType;
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
    // Co2 Emissions Factors Table
    @FXML
    private TableView<CO2FactorsData> cO2FactorsTable;
    @FXML
    private TableColumn<CO2FactorsData, String> heatingSystem;
    @FXML
    private TableColumn<CO2FactorsData, Double> startEmissions;
    @FXML
    private TableColumn<CO2FactorsData, Double> midEmissions;
    @FXML
    private TableColumn<CO2FactorsData, Double> finalEmissions;

    /**
     * Initializes the heating system exchanges table and connects the view to the underlying data model.
     */
    private void initHeatExchangeTable() {
        // Define the connection between data model and table columns
        oldType.setCellValueFactory(new PropertyValueFactory<>("oldType"));
        renType.setCellValueFactory(new PropertyValueFactory<>("renType"));

        lowTempBoilerRate.setCellValueFactory(new PropertyValueFactory<>("lowTempBoilerRate"));
        lowTempBoilerRate.setEditable(true);
        lowTempBoilerRate.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        lowTempBoilerRate.setOnEditCommit((TableColumn.CellEditEvent<HeatingSystemExchangeRate, Double> event) -> {
            TablePosition<HeatingSystemExchangeRate, Double> pos = event.getTablePosition();
            Double newValue = event.getNewValue();

            int row = pos.getRow();
            HeatingSystemExchangeRate hser = event.getTableView().getItems().get(row);
            hser.setLowTempBoilerRate(newValue);
        });

        districtHeatRate.setCellValueFactory(new PropertyValueFactory<>("districtHeatRate"));
        districtHeatRate.setEditable(true);
        districtHeatRate.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        districtHeatRate.setOnEditCommit((TableColumn.CellEditEvent<HeatingSystemExchangeRate, Double> event) -> {
            TablePosition<HeatingSystemExchangeRate, Double> pos = event.getTablePosition();
            Double newValue = event.getNewValue();

            int row = pos.getRow();
            HeatingSystemExchangeRate hser = event.getTableView().getItems().get(row);
            hser.setDistrictHeatRate(newValue);
        });

        condensingBoilerRate.setCellValueFactory(new PropertyValueFactory<>("condensingBoilerRate"));
        condensingBoilerRate.setEditable(true);
        condensingBoilerRate.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        condensingBoilerRate.setOnEditCommit((TableColumn.CellEditEvent<HeatingSystemExchangeRate, Double> event) -> {
            TablePosition<HeatingSystemExchangeRate, Double> pos = event.getTablePosition();
            Double newValue = event.getNewValue();

            int row = pos.getRow();
            HeatingSystemExchangeRate hser = event.getTableView().getItems().get(row);
            hser.setCondensingBoilerRate(newValue);
        });

        condBoilerSolarRate.setCellValueFactory(new PropertyValueFactory<>("condBoilerSolarRate"));
        condBoilerSolarRate.setEditable(true);
        condBoilerSolarRate.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        condBoilerSolarRate.setOnEditCommit((TableColumn.CellEditEvent<HeatingSystemExchangeRate, Double> event) -> {
            TablePosition<HeatingSystemExchangeRate, Double> pos = event.getTablePosition();
            Double newValue = event.getNewValue();

            int row = pos.getRow();
            HeatingSystemExchangeRate hser = event.getTableView().getItems().get(row);
            hser.setCondBoilerSolarRate(newValue);
        });

        pelletsRate.setCellValueFactory(new PropertyValueFactory<>("pelletsRate"));
        pelletsRate.setEditable(true);
        pelletsRate.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        pelletsRate.setOnEditCommit((TableColumn.CellEditEvent<HeatingSystemExchangeRate, Double> event) -> {
            TablePosition<HeatingSystemExchangeRate, Double> pos = event.getTablePosition();
            Double newValue = event.getNewValue();

            int row = pos.getRow();
            HeatingSystemExchangeRate hser = event.getTableView().getItems().get(row);
            hser.setPelletsRate(newValue);
        });

        heatPumpHRRate.setCellValueFactory(new PropertyValueFactory<>("heatPumpHRRate"));
        heatPumpHRRate.setEditable(true);
        heatPumpHRRate.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        heatPumpHRRate.setOnEditCommit((TableColumn.CellEditEvent<HeatingSystemExchangeRate, Double> event) -> {
            TablePosition<HeatingSystemExchangeRate, Double> pos = event.getTablePosition();
            Double newValue = event.getNewValue();

            int row = pos.getRow();
            HeatingSystemExchangeRate hser = event.getTableView().getItems().get(row);
            hser.setHeatPumpHRRate(newValue);
        });

        pelletsSolarHRRate.setCellValueFactory(new PropertyValueFactory<>("pelletsSolarHRRate"));
        pelletsSolarHRRate.setEditable(true);
        pelletsSolarHRRate.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        pelletsSolarHRRate.setOnEditCommit((TableColumn.CellEditEvent<HeatingSystemExchangeRate, Double> event) -> {
            TablePosition<HeatingSystemExchangeRate, Double> pos = event.getTablePosition();
            Double newValue = event.getNewValue();

            int row = pos.getRow();
            HeatingSystemExchangeRate hser = event.getTableView().getItems().get(row);
            hser.setPelletsSolarHRRate(newValue);
        });

        districtHeatHRRate.setCellValueFactory(new PropertyValueFactory<>("districtHeatHRRate"));
        districtHeatHRRate.setEditable(true);
        districtHeatHRRate.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        districtHeatHRRate.setOnEditCommit((TableColumn.CellEditEvent<HeatingSystemExchangeRate, Double> event) -> {
            TablePosition<HeatingSystemExchangeRate, Double> pos = event.getTablePosition();
            Double newValue = event.getNewValue();

            int row = pos.getRow();
            HeatingSystemExchangeRate hser = event.getTableView().getItems().get(row);
            hser.setDistrictHeatHRRate(newValue);
        });

        condBoilerSolarHRRate.setCellValueFactory(new PropertyValueFactory<>("condBoilerSolarHRRate"));
        condBoilerSolarHRRate.setEditable(true);
        condBoilerSolarHRRate.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
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
                // residential Basic Renovation matrix
                new HeatingSystemExchangeRate(RenovationType.RES_ENEV, HeatingType.LOW_TEMPERATURE_BOILER, 0d, 44d, 42d, 0d, 9d, 0d, 0d, 6d, 0d),
                new HeatingSystemExchangeRate(RenovationType.RES_ENEV, HeatingType.DISTRICT_HEAT, 0d, 0d, 0d, 0d, 0d, 0d, 10d, 90d, 0d),
                new HeatingSystemExchangeRate(RenovationType.RES_ENEV, HeatingType.CONDENSING_BOILER, 0d, 44d, 42d, 0d, 9d, 0d, 0d, 6d, 0d),
                // residential Good Renovation matrix
                new HeatingSystemExchangeRate(RenovationType.RES_PASSIVE, HeatingType.LOW_TEMPERATURE_BOILER, 0d, 0d, 0d, 32d, 0d, 32d, 6d, 0d, 30d),
                new HeatingSystemExchangeRate(RenovationType.RES_PASSIVE, HeatingType.DISTRICT_HEAT, 0d, 0d, 0d, 0d, 0d, 0d, 50d, 50d, 0d),
                new HeatingSystemExchangeRate(RenovationType.RES_PASSIVE, HeatingType.CONDENSING_BOILER, 0d, 0d, 0d, 32d, 0d, 32d, 6d, 0d, 30d),
                // non-residential Basic Renovation matrix
                new HeatingSystemExchangeRate(RenovationType.NRES_ENEV, HeatingType.LOW_TEMPERATURE_BOILER, 0d, 44d, 42d, 0d, 9d, 0d, 0d, 6d, 0d),
                new HeatingSystemExchangeRate(RenovationType.NRES_ENEV, HeatingType.DISTRICT_HEAT, 0d, 0d, 0d, 0d, 0d, 0d, 10d, 90d, 0d),
                new HeatingSystemExchangeRate(RenovationType.NRES_ENEV, HeatingType.CONDENSING_BOILER, 0d, 44d, 42d, 0d, 9d, 0d, 0d, 6d, 0d),
                // non-residential Good Renovation matrix
                new HeatingSystemExchangeRate(RenovationType.NRES_PASSIVE, HeatingType.LOW_TEMPERATURE_BOILER, 0d, 0d, 0d, 32d, 0d, 32d, 6d, 0d, 30d),
                new HeatingSystemExchangeRate(RenovationType.NRES_PASSIVE, HeatingType.DISTRICT_HEAT, 0d, 0d, 0d, 0d, 0d, 0d, 50d, 50d, 0d),
                new HeatingSystemExchangeRate(RenovationType.NRES_PASSIVE, HeatingType.CONDENSING_BOILER, 0d, 0d, 0d, 32d, 0d, 32d, 6d, 0d, 30d)
        );
    }

    /**
     * Initializes the CO2 Factors Data table and connects the view to the underlying data model.
     */
    private void initCO2FactorsTable() {
        // instantiate energyFactorsDAO, needed to get start year CO2 Factors
        energyFactorsDAO = new PrimaryEnergyFactorsDAO();

        // Define the connection between data model and table columns
        heatingSystem.setCellValueFactory(new PropertyValueFactory<>("heatingSystem"));
        heatingSystem.setText("Heating System");

        startEmissions.setCellValueFactory(new PropertyValueFactory<>("startEmissions"));
        startEmissions.setText(START_CO2_YEAR.toString() + " (current)");
        startEmissions.setEditable(true);
        startEmissions.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        startEmissions.setOnEditCommit((TableColumn.CellEditEvent<CO2FactorsData, Double> event) -> {
            TablePosition<CO2FactorsData, Double> pos = event.getTablePosition();
            Double newValue = event.getNewValue();

            int row = pos.getRow();
            CO2FactorsData hser = event.getTableView().getItems().get(row);
            hser.setStartEmissions(newValue);
        });

        midEmissions.setCellValueFactory(new PropertyValueFactory<>("midEmissions"));
        midEmissions.setText(MID_CO2_YEAR.toString());
        midEmissions.setEditable(true);
        midEmissions.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        midEmissions.setOnEditCommit((TableColumn.CellEditEvent<CO2FactorsData, Double> event) -> {
            TablePosition<CO2FactorsData, Double> pos = event.getTablePosition();
            Double newValue = event.getNewValue();

            int row = pos.getRow();
            CO2FactorsData hser = event.getTableView().getItems().get(row);
            hser.setMidEmissions(newValue);
        });

        finalEmissions.setCellValueFactory(new PropertyValueFactory<>("finalEmissions"));
        finalEmissions.setText(FINAL_CO2_YEAR.toString());
        finalEmissions.setEditable(true);
        finalEmissions.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        finalEmissions.setOnEditCommit((TableColumn.CellEditEvent<CO2FactorsData, Double> event) -> {
            TablePosition<CO2FactorsData, Double> pos = event.getTablePosition();
            Double newValue = event.getNewValue();

            int row = pos.getRow();
            CO2FactorsData hser = event.getTableView().getItems().get(row);
            hser.setFinalEmissions(newValue);
        });

        cO2FactorsTable.setItems(getCO2FactorsDataList());
        cO2FactorsTable.setEditable(true);
        accordion.setExpandedPane(co2Pane);
    }
    /**
     * Creates and returns the default list for {@link CO2FactorsData}.
     */
    private ObservableList<CO2FactorsData> getCO2FactorsDataList() {
        ObservableList<CO2FactorsData> co2FactorsDataList = FXCollections.observableArrayList();

        for (HeatingType type: HeatingType.values()) {
            co2FactorsDataList.add(new CO2FactorsData(
                type,
                energyFactorsDAO.findBy(type).getCo2Start(),
                energyFactorsDAO.findBy(type).getCo2Mid(),
                energyFactorsDAO.findBy(type).getCo2Final()
            ));
        }

        return co2FactorsDataList;
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
                    } else if (year < SimulationParameter.FIRST_YEAR) {
                        globalUntil.setText(SimulationParameter.FIRST_YEAR.toString());
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
                    Long seed = System.nanoTime();
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
        initCO2FactorsTable();
        initTextFieldValidators();
        
        rankingMethods.getItems().addAll(BUILDING_AGE, CO2_EMISSION, CO2_EMISSION_SQUARE_METER);
        rankingMethods.getCheckModel().check(0);
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

    public List<CO2FactorsData> getCO2FactorTableData() {
        return cO2FactorsTable.getItems();
    }

    /**
     *  If no seed is present (empty-string ""), then generate one automatically and set the corresponding field.
     *
     * @return the seed used in the simulation
     */
    public Long getSeed() {
        String text = globalSeed.getText();
        Long seed = null;
        try {
            seed = Long.parseLong(text);
        } catch (NumberFormatException e) {
            seed = System.nanoTime();
            globalSeed.setText(String.valueOf(seed));
        } finally {
            return seed;
        }
    }
    
    public List<String> getSelectedRankingMethods() {
        return rankingMethods.getCheckModel().getCheckedItems();
    }
}
