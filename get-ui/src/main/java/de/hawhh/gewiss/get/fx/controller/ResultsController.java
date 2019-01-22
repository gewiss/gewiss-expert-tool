package de.hawhh.gewiss.get.fx.controller;

import de.hawhh.gewiss.get.simulator.db.dao.DistrictQuarterDAO;
import de.hawhh.gewiss.get.simulator.db.dao.SQLiteDistrictQuarterDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * FXML Controller class for the Results.fxml
 *
 * @author Thomas Preisler
 */
public class ResultsController {

    private final static Logger LOGGER = Logger.getLogger(ResultsController.class.getName());

    @FXML
    private ChartResultsController chartResultsController;
    @FXML
    private MapResultsController mapResultsController;
    @FXML
    private ExportResultsController exportResultsController;
    @FXML
    private ExportFilesController exportFilesController;
    
    @FXML
    private Accordion accordion;
    @FXML
    private TitledPane tpChartResults;

    private List<String> quarters;

    public void init(MainController mc) {
        LOGGER.info("Initializing ResultsController");
        DistrictQuarterDAO districtQuarterDAO = new SQLiteDistrictQuarterDAO();

        this.quarters = districtQuarterDAO.getQuarters();

        chartResultsController.init(this);
        mapResultsController.init();
        exportResultsController.init();
        exportFilesController.init();
        
        //accordion.setExpandedPane(tpChartResults);
    }

    public List<String> getQuarters() {
        return quarters;
    }

    /**
     * Update the year combobox in the map results controller with the given set of years.
     *
     * @param years
     */
    public void updateYearBox(Set<Integer> years) {
        mapResultsController.updateYearBox(years);
    }
}