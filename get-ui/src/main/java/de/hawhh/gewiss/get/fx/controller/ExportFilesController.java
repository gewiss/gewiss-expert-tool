package de.hawhh.gewiss.get.fx.controller;

import de.hawhh.gewiss.get.core.output.BuildingInformation;
import de.hawhh.gewiss.get.core.output.SimulationOutput;
import de.hawhh.gewiss.get.core.output.SimulationResult;
import de.hawhh.gewiss.get.fx.SimulationResultHolder;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for ExportFiles.fxml
 *
 * @author Thomas Preisler
 */
public class ExportFilesController {

    @FXML
    private BorderPane exportFilesPane;
    @FXML
    private Button exportButton;
    @FXML
    private RadioButton excelRadio;
    @FXML
    private ProgressBar exportProgess;

    private final static Logger LOGGER = Logger.getLogger(ExportFilesController.class.getName());

    public void init() {
        LOGGER.info("Initializing ExportFilesController");

        ToggleGroup toggleGroup = new ToggleGroup();
        excelRadio.setToggleGroup(toggleGroup);
        excelRadio.setSelected(true);
    }

    @FXML
    private void export() {
        SimulationResult result = SimulationResultHolder.getInstance().getResult();
        if (result != null) {
            if (excelRadio.isSelected()) {
                Stage stage = (Stage) exportFilesPane.getScene().getWindow();
                final FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Microsoft Excel Open XML Format  (*.xlsx)", "*.xlsx"));
                fileChooser.setInitialFileName("simulation-results");
                File file = fileChooser.showSaveDialog(stage);

                Task<Boolean> exportTask = new Task<Boolean>() {
                    @Override
                    protected Boolean call() {
                        return exportToExcel(result, file);
                    }

                };
                exportTask.setOnSucceeded(event -> exportProgess.setProgress(1d));

                new Thread(exportTask).start();
                exportProgess.setProgress(-1d);
            }
        }
    }

    /**
     * Export the given SimulationResult to the given Excel file. The file is formatted according to the "Microsoft Excel Open XML Format".
     *
     * @param result the given SimulationResult
     * @param file the given (Excel) file
     * @return true if the export was successful, otherwise false
     */
    private boolean exportToExcel(SimulationResult result, File file) {
        LOGGER.info("Exporting Simulation Results to Excel");

        SXSSFWorkbook wb = new SXSSFWorkbook();

        Map<String, BuildingInformation> bi = result.getBuildings();

        result.getOutput().keySet().forEach((year) -> {
            LOGGER.log(Level.INFO, "Creating Excel sheet for {0}", year);
            Sheet sh = wb.createSheet(year.toString());

            Integer rowNum = 0;
            Row topRow = sh.createRow(rowNum++);
            topRow.createCell(0).setCellValue("Year");
            topRow.createCell(1).setCellValue("Building Id");
            topRow.createCell(2).setCellValue("Cluster Id");
            topRow.createCell(3).setCellValue("Quarter");
            topRow.createCell(4).setCellValue("Heat Demand");
            topRow.createCell(5).setCellValue("Heat Demand m^2");
            topRow.createCell(6).setCellValue("CO2 Emission");
            topRow.createCell(7).setCellValue("Renovation Level");
            topRow.createCell(8).setCellValue("Heating Type");
            topRow.createCell(9).setCellValue("Renovation Cost");
            topRow.createCell(10).setCellValue("Combined Floor Space");

            for (SimulationOutput so : result.getOutput().get(year)) {
                Row row = sh.createRow(rowNum++);
                row.createCell(0).setCellValue(so.getYear());
                row.createCell(1).setCellValue(so.getBuildingId());
                row.createCell(2).setCellValue(bi.get(so.getBuildingId()).getClusterId());
                row.createCell(3).setCellValue(bi.get(so.getBuildingId()).getQuarter());
                row.createCell(4).setCellValue(so.getHeatDemand());
                row.createCell(5).setCellValue(so.getHeatDemandM2());
                row.createCell(6).setCellValue(so.getCo2Emission());
                row.createCell(7).setCellValue(so.getRenovationLevelString());
                row.createCell(8).setCellValue(so.getHeatingTypeString());
                row.createCell(9).setCellValue(so.getRenovationCost());
                row.createCell(10).setCellValue(so.getCombinedArea());
            }
        });
        LOGGER.info("Finished creating Excel workbook");

        try {
            LOGGER.log(Level.INFO, "Writing Excel workbook to file: {0}", file);
            FileOutputStream out = new FileOutputStream(file);
            wb.write(out);
            out.close();

            // dispose of temporary files backing this workbook on disk
            wb.dispose();
            LOGGER.info("Finished writing Excel file");

            return true;
        } catch (IOException ex) {
            Logger.getLogger(ExportFilesController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }
}
