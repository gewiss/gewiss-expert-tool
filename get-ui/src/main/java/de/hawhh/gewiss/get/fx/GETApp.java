package de.hawhh.gewiss.get.fx;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.logging.Logger;

/**
 * Main entry point for the GET JavaFX App.
 * 
 * @author Thomas Preisler
 */
public class GETApp extends Application {

    private final static Logger LOGGER = Logger.getLogger(GETApp.class.getName());

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/GET.fxml"));
        
        Scene scene = new Scene(root);
        //scene.getStylesheets().add("/styles/Styles.css");
        //scene.getStylesheets().add("/styles/bootstrap3.css");
        
        stage.setTitle("GEWISS Expert Tool (GET v0.3)");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon/cityscape.png")));
        
        stage.setScene(scene);
        // start the application maximized
        //stage.setMaximized(true);
        stage.show();
        
        // Set on close to make sure all child threads are interrupted as well
        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
    }

    /**
     * Opens and runs application.
     *
     * @param args arguments passed to this application
     */
    public static void main(String[] args) {
        LOGGER.info("Application started");
        
        // set the ArcGIS license
        //ArcGISRuntimeEnvironment.setLicense("YOUR-LICENCE-KEY");

        // Start the actual application
        Application.launch(args);
    }
}
