package de.hawhh.gewiss.get.fx.controller;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyGraphicsOverlayResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.sun.management.OperatingSystemMXBean;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import de.hawhh.gewiss.get.core.output.BuildingInformation;
import de.hawhh.gewiss.get.core.output.SimulationOutput;
import de.hawhh.gewiss.get.core.output.SimulationResult;
import de.hawhh.gewiss.get.fx.SimulationResultHolder;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.management.ManagementFactory;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.wololo.geojson.Feature;
import org.wololo.geojson.FeatureCollection;
import org.wololo.jts2geojson.LngLatGeoJSONWriter;

/**
 * Controller for MapResults.fxml
 *
 * @author Thomas Preisler
 */
public class MapResultsController {

    private final static Logger LOGGER = Logger.getLogger(MapResultsController.class.getName());

    @FXML
    private StackPane mapResultsPane;
    @FXML
    private MapView mapView;
    @FXML
    private ComboBox<Integer> yearBox;
    @FXML
    private VBox loadingBox;
    @FXML
    private VBox ramBox;
    @FXML
    private Button exportButton;
    @FXML
    private VBox legendBox;

    private ObservableList<Integer> years;

    private GraphicsOverlay buildingOverlay;
    private SpatialReference spatialReference;

    public void init() {
        LOGGER.info("Initializing MapResultsController");

        years = FXCollections.observableArrayList();
        yearBox.setItems(years);

        // Check if the used PC has enough RAM (<= 16GB)
        if (ramCheck()) {
            createMap();
            yearBox.setOnAction(event -> {
                // show the encapuslating pane of the loading progess indicator
                loadingBox.setVisible(true);
                // disable all other UI elements on the pane
                exportButton.setDisable(true);
                yearBox.setDisable(true);
                mapView.setDisable(true);
                legendBox.setDisable(true);

                // async loading task for displaying the buildings
                Task displayTask = new Task() {
                    @Override
                    protected Object call() throws Exception {
                        showBuildings();
                        return null;
                    }
                };
                // when task is finished, hide pane of progress indicator and enable all other UI elements
                displayTask.setOnSucceeded(e -> {
                    loadingBox.setVisible(false);
                    exportButton.setDisable(false);
                    yearBox.setDisable(false);
                    mapView.setDisable(false);
                    legendBox.setDisable(true);
                });

                // start the task as an extra thread
                new Thread(displayTask).start();
            });
        } else {
            // show the encapuslating pane of the not enough RAM message
            ramBox.setVisible(true);
            // disable all other UI elements on the pane
            exportButton.setDisable(true);
            yearBox.setDisable(true);
            mapView.setDisable(true);
            legendBox.setDisable(true);
        }
    }

    /**
     * Create and initialize the ArcGIS map.
     */
    private void createMap() {
        // create a ArcGISMap with the a Basemap instance with an Imagery base layer
        ArcGISMap map = new ArcGISMap(Basemap.Type.STREETS_VECTOR, 53.551085, 9.993682, 14);
        // Create spatial reference for EPSG:25832
        spatialReference = SpatialReference.create(25832);
        // set the map to be displayed in this view
        mapView.setMap(map);

        // create a graphics overlay for the buildings
        buildingOverlay = new GraphicsOverlay();
        // add graphics overlay to the map view
        mapView.getGraphicsOverlays().add(buildingOverlay);

        // click event to display the callout
        mapView.setOnMouseClicked(e -> {
            // check that the primary mouse button was clicked and user is not panning
            if (e.isStillSincePress() && e.getButton() == MouseButton.PRIMARY) {
                // create a point from where the user clicked
                Point2D point = new Point2D(e.getX(), e.getY());
                // identify graphics on the graphics overlay
                ListenableFuture<IdentifyGraphicsOverlayResult> identifyGraphics = mapView.identifyGraphicsOverlayAsync(buildingOverlay, point, 10, false);
                identifyGraphics.addDoneListener(() -> Platform.runLater(() -> {
                    try {
                        // get the list of graphics returned by identify
                        IdentifyGraphicsOverlayResult result = identifyGraphics.get();
                        List<Graphic> graphics = result.getGraphics();

                        Callout callout = mapView.getCallout();
                        if (!graphics.isEmpty()) {
                            // get the map view's callout

                            if (callout.isVisible()) {
                                // hide the callout
                                callout.dismiss();
                            }

                            Graphic graphic = graphics.get(0);
                            String buildingId = (String) graphic.getAttributes().get("building_id");
                            Double heatDemandM2 = (Double) graphic.getAttributes().get("heat_demand_m2");

                            // set the callout's details
                            callout.setTitle("Building: " + buildingId);
                            callout.setDetail("Heat demand (m^2): " + heatDemandM2);

                            // show the callout where the user clicked
                            callout.showCalloutAt(mapView.screenToLocation(point));
                        } else {
                            callout.dismiss();
                        }
                    } catch (InterruptedException | ExecutionException ex) {
                        Logger.getLogger(MapResultsController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }));
            }

        });
    }

    /**
     * Create a fill symbol for the given parameters. A fill symbol allows to display polygons on the map.
     *
     * @param outlineColor
     * @param outlineWidth
     * @param fillColor
     * @param fillStyle
     * @return
     */
    private SimpleFillSymbol createFillSymbol(int outlineColor, float outlineWidth, int fillColor, SimpleFillSymbol.Style fillStyle) {
        SimpleLineSymbol outlineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, outlineColor, outlineWidth);
        SimpleFillSymbol fillSymbol = new SimpleFillSymbol(fillStyle, fillColor, outlineSymbol);

        return fillSymbol;
    }

    /**
     * Create a ArcGIS compatible polygon from the given geometry.
     */
    private Polygon polygonFromGeometry(Geometry geometry) {
        // create a new point collection for polyline
        PointCollection points = new PointCollection(spatialReference);
        for (Coordinate coordinate : geometry.getCoordinates()) {
            points.add(new Point(coordinate.x, coordinate.y));
        }

        Polygon polygon = new Polygon(points);
        return polygon;
    }

    /**
     * Display all the buildings with their heat demand per square meter for the selected year from the simulation results.
     */
    @FXML
    public void showBuildings() {
        Integer year = yearBox.getValue();
        if (SimulationResultHolder.getInstance().getResult() != null && year != null) {
            LOGGER.info("Showing buildings for year " + year);
            buildingOverlay.getGraphics().clear();

            // Retreive building information from SimulationResultHolder
            Map<String, BuildingInformation> buildings = SimulationResultHolder.getInstance().getResult().getBuildings();

            Collection<SimulationOutput> buildingResults = SimulationResultHolder.getInstance().getResult().getOutput().get(year);
            buildingResults.parallelStream().forEach((SimulationOutput buildingResult) -> {
                Geometry geometry = buildings.get(buildingResult.getBuildingId()).getGeometry();
                Polygon polygon = polygonFromGeometry(geometry);

                Double hdm2 = buildingResult.getHeatDemandM2();
                int color = 0;
                if (hdm2 < 25d) {
                    color = 0xFF1A9641;
                } else if (hdm2 < 50d) {
                    color = 0xFF52b151;
                } else if (hdm2 < 75d) {
                    color = 0xFF8acc62;
                } else if (hdm2 < 100d) {
                    color = 0xFFb8e17b;
                } else if (hdm2 < 125d) {
                    color = 0xFFdcf09e;
                } else if (hdm2 < 150d) {
                    color = 0xFFffffc0;
                } else if (hdm2 < 175d) {
                    color = 0xFFffdf9a;
                } else if (hdm2 < 200d) {
                    color = 0xFFfebe74;
                } else if (hdm2 < 225d) {
                    color = 0xFFf69053;
                } else if (hdm2 < 250d) {
                    color = 0xFFe75437;
                } else {
                    color = 0xFFd7191c;
                }

                SimpleFillSymbol fillSymbol = createFillSymbol(color, 1, color, SimpleFillSymbol.Style.SOLID);
                Graphic graphic = new Graphic(polygon, fillSymbol);
                graphic.getAttributes().put("building_id", buildingResult.getBuildingId());
                graphic.getAttributes().put("heat_demand_m2", buildingResult.getHeatDemandM2());
                buildingOverlay.getGraphics().add(graphic);
            });

            LOGGER.info("Finished adding buildings to map");
        }
    }

    /**
     * Update the year combobox with the given set of years.
     *
     * @param years
     */
    public void updateYearBox(Set<Integer> years) {
        this.years.clear();
        this.years.addAll(years);
        Collections.sort(this.years);
    }

    @FXML
    private void export() {
        SimulationResult result = SimulationResultHolder.getInstance().getResult();
        if (result != null) {
            Integer year = yearBox.getValue();
            LOGGER.info("Exporting buildings to GeoJSON for year " + year);
            Collection<SimulationOutput> buildingResults = SimulationResultHolder.getInstance().getResult().getOutput().get(year);

            if (buildingResults != null && !buildingResults.isEmpty()) {
                try {
                    CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:25832");
                    CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:4326");

                    MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);

                    List<Feature> features = new ArrayList<>();
                    LngLatGeoJSONWriter writer = new LngLatGeoJSONWriter();

                    // Retreive building information from SimulationResultHolder
                    Map<String, BuildingInformation> buildings = SimulationResultHolder.getInstance().getResult().getBuildings();

                    buildingResults.forEach(so -> {
                        try {
                            Map<String, Object> properties = new HashMap<>();
                            properties.put("building_id", so.getBuildingId());
                            properties.put("cluster_id", buildings.get(so.getBuildingId()).getClusterId());
                            properties.put("heat_demand", so.getHeatDemand());
                            properties.put("heat_demand_m2", so.getHeatDemandM2());
                            properties.put("renovation_level", so.getRenovationLevelString());
                            properties.put("renovation_costs", so.getRenovationCost());
                            properties.put("heating_system", so.getHeatingTypeString());
                            properties.put("co2_emission", so.getCo2Emission());

                            Geometry geometry = JTS.transform(buildings.get(so.getBuildingId()).getGeometry(), transform);
                            org.wololo.geojson.Geometry geoJsonGeometry = writer.write(geometry);

                            features.add(new Feature(so.getBuildingId(), geoJsonGeometry, properties));
                        } catch (MismatchedDimensionException | TransformException ex) {
                            Logger.getLogger(MapResultsController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });

                    FeatureCollection featureCollection = writer.write(features);

                    Stage stage = (Stage) mapResultsPane.getScene().getWindow();
                    final FileChooser fileChooser = new FileChooser();
                    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("GeoJSON file (*.geojson)", "*.geojson"));
                    fileChooser.setInitialFileName("export-buildings-" + year);

                    File file = fileChooser.showSaveDialog(stage);

                    ObjectMapper mapper = new ObjectMapper();
                    ObjectWriter objectWriter = mapper.writerWithDefaultPrettyPrinter();
                    LOGGER.info("Writing GeoJSON Feature Collection to byte[]");
                    byte[] jsonBytes = objectWriter.writeValueAsBytes(featureCollection);

                    FileChannel rwChannel = new RandomAccessFile(file, "rw").getChannel();
                    ByteBuffer wrBuf = rwChannel.map(FileChannel.MapMode.READ_WRITE, 0, jsonBytes.length);
                    LOGGER.info("Writing byte[] data to file " + file.getAbsolutePath());
                    wrBuf.put(jsonBytes);
                    rwChannel.close();
                } catch (FactoryException | IOException ex) {
                    Logger.getLogger(MapResultsController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * Checks if the used PC has at least 16 GB of total physical RAM.
     *
     * @return
     */
    private boolean ramCheck() {
        OperatingSystemMXBean mxbean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        long mbRam = mxbean.getTotalPhysicalMemorySize() / (1000 * 1000);

        return mbRam >= 16000;
    }
}
