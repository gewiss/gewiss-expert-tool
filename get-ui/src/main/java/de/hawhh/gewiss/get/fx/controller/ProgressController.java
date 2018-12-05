package de.hawhh.gewiss.get.fx.controller;

import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

/**
 * FXML Controller for the Progress.fxml
 *
 * @author Thomas Preisler
 */
public class ProgressController {
    
    private final static Logger LOGGER = Logger.getLogger(ProgressController.class.getName());
    
    private MainController mainController;
    private String initTitle;
    
    @FXML
    private ProgressBar progressBar;
    @FXML
    private TextArea progressLog;
    @FXML
    private Text title;

    /**
     * Custom initialization of this controller.
     *
     * @param mc Reference to the main controller
     */
    public void init(MainController mc) {
        mainController = mc;
        
        progressBar.setProgress(0d);
        this.initTitle = title.getText();
    }
    
    public void startProgressBar() {
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
    }
    
    public void stopProgressBar() {
        progressBar.setProgress(1d);
    }
    
    public void setProgress(Double progress) {
       progressBar.setProgress(progress);
    }
    
    public void appendLog(String msg) {
        this.progressLog.appendText(msg + "\n");
    }
    
    public void setSimName(String simName) {
        title.setText(initTitle + " " + simName);
    }
    
    @FXML
    private void abortSimulation() {
        mainController.abortSimulation();
    }
}
