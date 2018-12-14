package de.hawhh.gewiss.get.fx.controller;

import de.hawhh.gewiss.get.core.output.SimulationResult;
import de.hawhh.gewiss.get.fx.SimulationResultHolder;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.javatuples.Triplet;

/**
 * Controller for ChartResults.fxml.
 *
 * @author Thomas Preisler
 */
public class ChartResultsController {

    private final static Logger LOGGER = Logger.getLogger(ChartResultsController.class.getName());

    private final static String HEAT_DEMAND_MODE = "Heat Demand";
    private final static String CO2_EMISSION_MODE = "CO2 Emission";
    private final static String RENOVATION_LEVEL_MODE = "Renovation Level";
    private final static String RENOVATION_COST_MODE = "Renovation Costs";

    private final static String LOCATION_ALL = "---ALL---";

    private ResultsController parentController;

    @FXML
    private ComboBox<String> modeBox;
    @FXML
    private ComboBox<String> locationBox;
    @FXML
    private LineChart<Number, Number> resultChart;
    @FXML
    private BorderPane chartResultsPane;

    private NumberAxis xAxis;
    private NumberAxis yAxis;

    public void init(ResultsController parentController) {
        LOGGER.info("Initializing ChartResultsController");
        this.parentController = parentController;

        modeBox.getItems().addAll(CO2_EMISSION_MODE, HEAT_DEMAND_MODE, RENOVATION_LEVEL_MODE);
        locationBox.getItems().add(LOCATION_ALL);
        locationBox.getItems().addAll(this.parentController.getQuarters());

        modeBox.setOnAction(event -> {
            displayResults();
        });
        locationBox.setOnAction(event -> {
            displayResults();
        });
    }

    @FXML
    private void displayResults() {
        SimulationResult result = SimulationResultHolder.getInstance().getResult();
        if (result != null) {
            String location = locationBox.getValue();

            // Set location to null if option ALL is selected, as null is used for filtering later, if no specific location is given
            if (location != null && location.equals(LOCATION_ALL)) {
                location = null;
            }

            // HEAT_DEMAND_MODE
            if (modeBox != null && modeBox.getValue() != null) {
                if (modeBox.getValue().equals(HEAT_DEMAND_MODE)) {
                    resultChart.getData().clear();

                    xAxis = (NumberAxis) resultChart.getXAxis();
                    xAxis.setAutoRanging(false);
                    yAxis = (NumberAxis) resultChart.getYAxis();
                    yAxis.setAutoRanging(true);
                    //yAxis.setAutoRanging(false);

                    Integer firstYear = Collections.min(result.getOutput().keySet());
                    Integer lastYear = Collections.max(result.getOutput().keySet());

                    xAxis.setLabel("Time (years)");
                    xAxis.setLowerBound(firstYear - 1);
                    xAxis.setUpperBound(lastYear + 1);

                    XYChart.Series<Number, Number> heatDemandSeries = new XYChart.Series<>();
                    heatDemandSeries.setName("Overall yearly heat demand");

                    Map<Integer, Double> heatDemands = result.getYearlyHeatDemand(location);
                    heatDemands.keySet().stream().forEach(year -> {
                        Double heatDemand = heatDemands.get(year);
                        XYChart.Data data = new XYChart.Data(year, heatDemand);
                        heatDemandSeries.getData().add(data);
                    });

                    Double minHeatDemand = heatDemandSeries.getData().stream().min((data1, data2) -> Double.compare(data1.getYValue().doubleValue(), data2.getYValue().doubleValue())).get().getYValue().doubleValue();
                    Double maxHeatDemand = heatDemandSeries.getData().stream().max((data1, data2) -> Double.compare(data1.getYValue().doubleValue(), data2.getYValue().doubleValue())).get().getYValue().doubleValue();

                    yAxis.setLabel("Overall yearly heat demand (kWh/m^2)");
                    yAxis.setLowerBound(minHeatDemand);
                    yAxis.setUpperBound(maxHeatDemand);

                    resultChart.getData().add(heatDemandSeries);

                    heatDemandSeries.getData().stream().forEach(date -> {
                        Tooltip.install(date.getNode(), new Tooltip(
                                "year: " + "\t \t" + date.getXValue().toString() + "\n" + "heat demand: " + "\t" + date.getYValue().toString())
                        );
                    });

                } // RENOVATION_LEVEL_MODE
                else if (modeBox.getValue().equals(RENOVATION_LEVEL_MODE)) {
                    resultChart.getData().clear();

                    xAxis = (NumberAxis) resultChart.getXAxis();
                    xAxis.setAutoRanging(false);
                    yAxis = (NumberAxis) resultChart.getYAxis();
                    yAxis.setAutoRanging(true);
//                yAxis.setAutoRanging(false);

                    Integer firstYear = Collections.min(result.getOutput().keySet());
                    Integer lastYear = Collections.max(result.getOutput().keySet());

                    xAxis.setLabel("Time (years)");
                    xAxis.setLowerBound(firstYear - 1);
                    xAxis.setUpperBound(lastYear + 1);

                    XYChart.Series<Number, Number> noRenovationLevelSeries = new XYChart.Series<>();
                    XYChart.Series<Number, Number> basicRenovationLevelSeries = new XYChart.Series<>();
                    XYChart.Series<Number, Number> goodRenovationLevelSeries = new XYChart.Series<>();
                    noRenovationLevelSeries.setName("No renovation level");
                    basicRenovationLevelSeries.setName("Basic renovation level (EnEV 2014)");
                    goodRenovationLevelSeries.setName("Good renovation level (Passive House)");

                    Map<Integer, Triplet<Long, Long, Long>> renovationLevels = result.getRenovationLevels(location);
                    renovationLevels.keySet().forEach(year -> {
                        XYChart.Data dataNoRenovation = new XYChart.Data(year, renovationLevels.get(year).getValue0());
                        XYChart.Data dataBasicRenovation = new XYChart.Data(year, renovationLevels.get(year).getValue1());
                        XYChart.Data dataGoodRenovation = new XYChart.Data(year, renovationLevels.get(year).getValue2());

                        basicRenovationLevelSeries.getData().add(dataBasicRenovation);
                        noRenovationLevelSeries.getData().add(dataNoRenovation);
                        goodRenovationLevelSeries.getData().add(dataGoodRenovation);
                    });

                    Double minNoRenovationLevel = noRenovationLevelSeries.getData().stream().min((data1, data2) -> Double.compare(data1.getYValue().doubleValue(), data2.getYValue().doubleValue())).get().getYValue().doubleValue();
                    Double maxNoRenovationLevel = noRenovationLevelSeries.getData().stream().max((data1, data2) -> Double.compare(data1.getYValue().doubleValue(), data2.getYValue().doubleValue())).get().getYValue().doubleValue();
                    Double minBasicRenovationLevel = basicRenovationLevelSeries.getData().stream().min((data1, data2) -> Double.compare(data1.getYValue().doubleValue(), data2.getYValue().doubleValue())).get().getYValue().doubleValue();
                    Double maxBasicRenovationLevel = basicRenovationLevelSeries.getData().stream().max((data1, data2) -> Double.compare(data1.getYValue().doubleValue(), data2.getYValue().doubleValue())).get().getYValue().doubleValue();
                    Double minGoodRenovationLevel = goodRenovationLevelSeries.getData().stream().min((data1, data2) -> Double.compare(data1.getYValue().doubleValue(), data2.getYValue().doubleValue())).get().getYValue().doubleValue();
                    Double maxGoodRenovationLevel = goodRenovationLevelSeries.getData().stream().max((data1, data2) -> Double.compare(data1.getYValue().doubleValue(), data2.getYValue().doubleValue())).get().getYValue().doubleValue();

                    Double minRenovationLevel = Math.min(minGoodRenovationLevel, Math.min(minNoRenovationLevel, minBasicRenovationLevel));
                    Double maxRenovationLevel = Math.max(maxGoodRenovationLevel, Math.max(maxNoRenovationLevel, maxBasicRenovationLevel));

                    yAxis.setLabel("Number of buildings");
                    yAxis.setLowerBound(minRenovationLevel);
                    yAxis.setUpperBound(maxRenovationLevel);

                    resultChart.getData().add(noRenovationLevelSeries);
                    resultChart.getData().add(basicRenovationLevelSeries);
                    resultChart.getData().add(goodRenovationLevelSeries);

                    noRenovationLevelSeries.getData().stream().forEach(date -> {
                        Tooltip.install(date.getNode(), new Tooltip(
                                "year:" + "\t \t" + date.getXValue().toString() + "\n" + "buildings: " + "\t" + date.getYValue().toString())
                        );
                    });

                    basicRenovationLevelSeries.getData().stream().forEach(date -> {
                        Tooltip.install(date.getNode(), new Tooltip(
                                "year: " + "\t \t" + date.getXValue().toString() + "\n" + "buildings: " + "\t" + date.getYValue().toString())
                        );
                    });

                    goodRenovationLevelSeries.getData().stream().forEach(date -> {
                        Tooltip.install(date.getNode(), new Tooltip(
                                "year: " + "\t \t" + date.getXValue().toString() + "\n" + "buildings: " + "\t" + date.getYValue().toString())
                        );
                    });

                    // RENOVATION_COST_MODE
                } else if (modeBox.getValue().equals(RENOVATION_COST_MODE)) {

                    resultChart.getData().clear();

                    xAxis = (NumberAxis) resultChart.getXAxis();
                    xAxis.setAutoRanging(false);
                    yAxis = (NumberAxis) resultChart.getYAxis();
                    yAxis.setAutoRanging(true);
                    //yAxis.setAutoRanging(false);

                    Integer firstYear = Collections.min(result.getOutput().keySet());
                    Integer lastYear = Collections.max(result.getOutput().keySet());

                    xAxis.setLabel("Time (years)");
                    xAxis.setLowerBound(firstYear - 1);
                    xAxis.setUpperBound(lastYear + 1);

                    XYChart.Series<Number, Number> renovationCostSeries = new XYChart.Series<>();
                    renovationCostSeries.setName("Overall yearly renovation costs");

                    Map<Integer, Double> costsMap = result.getRenovationCosts(location);
                    costsMap.keySet().forEach(year -> {
                        Double costs = costsMap.get(year);
                        XYChart.Data data = new XYChart.Data(year, costs);
                        renovationCostSeries.getData().add(data);
                    });

                    Double minRenovationCosts = renovationCostSeries.getData().stream().min((data1, data2) -> Double.compare(data1.getYValue().doubleValue(), data2.getYValue().doubleValue())).get().getYValue().doubleValue();
                    Double maxRenovationCosts = renovationCostSeries.getData().stream().max((data1, data2) -> Double.compare(data1.getYValue().doubleValue(), data2.getYValue().doubleValue())).get().getYValue().doubleValue();

                    yAxis.setLabel("Overall yearly renovation costs (EUR)");
                    yAxis.setLowerBound(minRenovationCosts);
                    yAxis.setUpperBound(maxRenovationCosts);

                    resultChart.getData().add(renovationCostSeries);

                    renovationCostSeries.getData().stream().forEach(date -> {
                        Tooltip.install(date.getNode(), new Tooltip(
                                "year: " + "\t" + date.getXValue().toString() + "\n" + "costs: " + "\t" + date.getYValue().toString())
                        );
                    });
                    // CO2_EMISSION_MODE
                } else if (modeBox.getValue().equals(CO2_EMISSION_MODE)) {

                    resultChart.getData().clear();

                    xAxis = (NumberAxis) resultChart.getXAxis();
                    xAxis.setAutoRanging(false);
                    yAxis = (NumberAxis) resultChart.getYAxis();
                    yAxis.setAutoRanging(true);
                    //yAxis.setAutoRanging(false);

                    Integer firstYear = Collections.min(result.getOutput().keySet());
                    Integer lastYear = Collections.max(result.getOutput().keySet());

                    xAxis.setLabel("Time (years)");
                    xAxis.setLowerBound(firstYear - 1);
                    xAxis.setUpperBound(lastYear + 1);

                    XYChart.Series<Number, Number> co2EmissionSeries = new XYChart.Series<>();
                    co2EmissionSeries.setName("Overall yearly CO2 emissions");

                    Map<Integer, Double> emissionsMap = result.getCO2Emissions(location);
                    emissionsMap.keySet().forEach(year -> {
                        Double emission = emissionsMap.get(year);
                        XYChart.Data data = new XYChart.Data(year, emission);
                        co2EmissionSeries.getData().add(data);
                    });

                    Double minCo2Emissions = co2EmissionSeries.getData().stream().min((data1, data2) -> Double.compare(data1.getYValue().doubleValue(), data2.getYValue().doubleValue())).get().getYValue().doubleValue();
                    Double maxCo2Emissions = co2EmissionSeries.getData().stream().max((data1, data2) -> Double.compare(data1.getYValue().doubleValue(), data2.getYValue().doubleValue())).get().getYValue().doubleValue();

                    yAxis.setLabel("Overall yearly CO2 emissions (t)");
                    yAxis.setLowerBound(minCo2Emissions);
                    yAxis.setUpperBound(maxCo2Emissions);

                    resultChart.getData().add(co2EmissionSeries);

                    co2EmissionSeries.getData().stream().forEach(date -> {
                        Tooltip.install(date.getNode(), new Tooltip(
                                "year: " + "\t \t \t" + date.getXValue().toString() + "\n" + "CO2 emissions: " + "\t" + String.format("%.2f", date.getYValue()))
                        );
                    });
                }
            }
        }
    }

    @FXML
    private void exportResults() {
        ObservableList<XYChart.Series<Number, Number>> dataSeries = resultChart.getData();
        if (!dataSeries.isEmpty()) {
            BufferedWriter writer = null;
            try {
                // Configure the file chooser and get the file
                Stage stage = (Stage) chartResultsPane.getScene().getWindow();
                final FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV file (*.csv)", "*.csv"));
                fileChooser.setInitialFileName("export-" + modeBox.getValue().replaceAll("\\s+", "-"));
                File file = fileChooser.showSaveDialog(stage);

                // Get the headers
                List<String> headers = new ArrayList<>();
                headers.add("Year");
                dataSeries.forEach(series -> {
                    headers.add(series.getName());
                });

                writer = new BufferedWriter(new FileWriter(file));
                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(headers.stream().toArray(String[]::new)));

                // get length of longest series
                int length = 0;
                for (XYChart.Series<Number, Number> series : dataSeries) {
                    if (series.getData().size() > length) {
                        length = series.getData().size();
                    }
                }

                // iterate over length of all series
                for (int i = 0; i <= length; i++) {
                    boolean first = true;
                    List<String> record = new ArrayList<>();

                    // iterate over all series
                    for (XYChart.Series<Number, Number> series : dataSeries) {
                        ObservableList<XYChart.Data<Number, Number>> dataList = series.getData();
                        if (i < dataList.size()) {
                            XYChart.Data<Number, Number> data = dataList.get(i);

                            // only add the year (x value) for the first record entry
                            if (first) {
                                record.add(data.getXValue().toString());
                                first = false;
                            }
                            // add the y value, the actual data to the record
                            record.add(data.getYValue().toString());
                        }
                    }

                    csvPrinter.printRecord(record);
                }

                csvPrinter.flush();
            } catch (IOException ex) {
                Logger.getLogger(ChartResultsController.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    writer.close();
                } catch (IOException ex) {
                    Logger.getLogger(ChartResultsController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
