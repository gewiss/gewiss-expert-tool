package de.hawhh.gewiss.get.fx.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.collect.Range;
import de.hawhh.gewiss.get.core.input.Modifier;
import de.hawhh.gewiss.get.core.model.HeatingType;
import de.hawhh.gewiss.get.core.model.RenovationLevel;
import de.hawhh.gewiss.get.simulator.db.dao.SQLiteBuildingDAO;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.CheckTreeView;

/**
 * Controller for the Modifier.fxml view.
 *
 * @author Thomas Preisler
 */
public class ModifierController {

    @FXML
    private TextField modifierName;
    @FXML
    private TextField modifierStart;
    @FXML
    private TextField modifierEnd;
    @FXML
    private TextField districtHeatingDistance;
    @FXML
    private TextField constructionYearStart;
    @FXML
    private TextField constructionYearEnd;
    @FXML
    private CheckTreeView<String> locationCheckTree;
    @FXML
    private CheckTreeView<String> typeCheckTree;
    @FXML
    private Slider modifierImpact;
    @FXML
    private CheckBox renLevelPassiveHouse;
    @FXML
    private CheckBox renLevelEnEV2014;
    @FXML
    private CheckBox renLevelUnrenovated;
    @FXML
    private CheckBox heatingSystemDistrictHeating;
    @FXML
    private CheckBox heatingSystemBoiler;
    @FXML
    private CheckBox heatingSystemCondensingBoiler;
    @FXML
    private VBox modifierPane;

    private InputController inputController;
    private SQLiteBuildingDAO buildingDAO;

    public void init(InputController parentController) {
        inputController = parentController;
        buildingDAO = new SQLiteBuildingDAO();

        modifierName.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) { //when focus lost
                String text = modifierName.getText();
                inputController.setTabTitle(text);
            }
        });

        initTextFieldValidators();
        initLocationCheckTree();
        initTypeCheckTree();
    }

    private void initTextFieldValidators() {
        modifierStart.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) { //when focus lost
                String text = modifierStart.getText();
                try {
                    Integer year = Integer.parseInt(text);
                } catch (NumberFormatException e) {
                    modifierStart.setText("2020");
                }
            }
        });

        modifierEnd.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) { //when focus lost
                String text = modifierEnd.getText();
                try {
                    Integer year = Integer.parseInt(text);
                } catch (NumberFormatException e) {
                    modifierEnd.setText("2030");
                }
            }
        });

        districtHeatingDistance.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) { //when focus lost
                String text = districtHeatingDistance.getText();
                if (text.length() > 0) {
                    try {
                        Double distance = Double.parseDouble(text);
                        // convert double to int, as we dont want double values here
                        districtHeatingDistance.setText(String.valueOf(Math.round(distance)));
                    } catch (NumberFormatException e) {
                        districtHeatingDistance.setText("100");
                    }
                }
            }
        });

        constructionYearStart.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) { //when focus lost
                String text = constructionYearStart.getText();
                if (text.length() > 0) {
                    try {
                        Integer year = Integer.parseInt(text);
                    } catch (NumberFormatException e) {
                        constructionYearStart.setText("1960");
                    }
                }
            }
        });

        constructionYearEnd.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) { //when focus lost
                String text = constructionYearEnd.getText();
                if (text.length() > 0) {
                    try {
                        Integer year = Integer.parseInt(text);
                    } catch (NumberFormatException e) {
                        constructionYearEnd.setText("1980");
                    }
                }
            }
        });
    }

    private void initLocationCheckTree() {
        // Hamburg as root
        CheckBoxTreeItem<String> root = new CheckBoxTreeItem<>("Hamburg");
        root.setExpanded(true);

        // Get the districts
        inputController.getLocationMap().keySet().forEach(district -> {
            CheckBoxTreeItem<String> districtItem = new CheckBoxTreeItem(district);

            inputController.getLocationMap().get(district).forEach(quarter -> {
                CheckBoxTreeItem<String> quarterItem = new CheckBoxTreeItem<>(quarter);
                districtItem.getChildren().add(quarterItem);
            });

            districtItem.setExpanded(false);
            root.getChildren().add(districtItem);
        });

        locationCheckTree.setRoot(root);
    }

    private void initTypeCheckTree() {
        CheckBoxTreeItem<String> root = new CheckBoxTreeItem<>("All");
        root.setExpanded(true);

        // Get the residential types
        CheckBoxTreeItem<String> residential = new CheckBoxTreeItem<>(InputController.RESIDENTIAL_BUILDINGS);
        residential.setExpanded(false);
        root.getChildren().add(residential);
        inputController.getBuildingTypeMap().get(InputController.RESIDENTIAL_BUILDINGS).forEach(type -> {
            CheckBoxTreeItem<String> bt = new CheckBoxTreeItem<>(type);
            residential.getChildren().add(bt);
        });

        // Get the non residential types
        CheckBoxTreeItem<String> nonResidential = new CheckBoxTreeItem<>(InputController.NON_RESIDENTIAL_BUILDINGS);
        nonResidential.setExpanded(false);
        root.getChildren().add(nonResidential);
        inputController.getBuildingTypeMap().get(InputController.NON_RESIDENTIAL_BUILDINGS).forEach(type -> {
            CheckBoxTreeItem<String> bt = new CheckBoxTreeItem<>(type);
            nonResidential.getChildren().add(bt);
        });

        typeCheckTree.setRoot(root);
    }

    /**
     * Fetches the input from the UI and builds a {@link Modifier} by creating the according conditions for the filled input fields.
     *
     * @return the created Modifier or <code>null</code> in case of an exception
     */
    public Modifier getModifier() {
        try {
            String name = modifierName.getText();
            if (name == null || name.isEmpty()) {
                name = "Default";
            }

            Double impact = modifierImpact.getValue();
            Integer from = Integer.parseInt(modifierStart.getText());
            Integer to = Integer.parseInt(modifierEnd.getText());

            Modifier modifier = new Modifier(name, from, to, impact);

            // Location
            List<String> quarters = getSelectedQuarters();
            if (!quarters.isEmpty()) {
                modifier.setTargetQuarters(quarters);
            }

            // Building Types
            List<String> buildingTypes = new ArrayList<>();
            List<String> residentialBuildingTypes = getSelectedResidentialBuildingTypes();
            if (!residentialBuildingTypes.isEmpty()) {
                buildingTypes.addAll(residentialBuildingTypes);
            }
            List<String> nonResidentialBTs = getSelectedNonResidentialBuildingTypes();
            if (!nonResidentialBTs.isEmpty()) {
                buildingTypes.addAll(nonResidentialBTs);
            }
            if (!buildingTypes.isEmpty()) {
                modifier.setTargetBuildingsTypes(buildingTypes);
            }

            // Construction year
            Integer constructionFrom = 0;
            Integer constructionTo = 2018;
            if (!constructionYearStart.getText().isEmpty()) {
                constructionFrom = Integer.parseInt(constructionYearStart.getText());
            }
            if (!constructionYearEnd.getText().isEmpty()) {
                constructionTo = Integer.parseInt(constructionYearEnd.getText());
            }
            // just add the condition if one of the fields is filled with a meaningful value
            if (!constructionYearStart.getText().isEmpty() || !constructionYearEnd.getText().isEmpty()) {
                modifier.setYearOfConstructionRange(Range.closed(constructionFrom, constructionTo));
            }

            // Renovation Level
            List<RenovationLevel> renovationLevels = new ArrayList<>();
            if (renLevelUnrenovated.isSelected()) {
                renovationLevels.add(RenovationLevel.NO_RENOVATION);
            }
            if (renLevelEnEV2014.isSelected()) {
                renovationLevels.add(RenovationLevel.BASIC_RENOVATION);
            }
            if (renLevelPassiveHouse.isSelected()) {
                renovationLevels.add(RenovationLevel.GOOD_RENOVATION);
            }
            // just add the condition if one of the three checkboxes is selected
            if (renLevelUnrenovated.isSelected() || renLevelEnEV2014.isSelected() || renLevelPassiveHouse.isSelected()) {
                modifier.setTargetRenovationLevels(renovationLevels);
            }

            // Heating System
            List<HeatingType> heatingTypes = new ArrayList<>();
            if (heatingSystemBoiler.isSelected()) {
                heatingTypes.add(HeatingType.LOW_TEMPERATURE_BOILER);
            }
            if (heatingSystemDistrictHeating.isSelected()) {
                heatingTypes.add(HeatingType.DISTRICT_HEAT);
            }
            if (heatingSystemCondensingBoiler.isSelected()) {
                heatingTypes.add(HeatingType.CONDENSING_BOILER);
            }
            // just add the condition if at least one checkbox is selected
            if (heatingSystemBoiler.isSelected() || heatingSystemDistrictHeating.isSelected() || heatingSystemCondensingBoiler.isSelected()) {
                modifier.setTargetHeatingSystems(heatingTypes);
            }

            // District Heating Outlet Distance
            if (!districtHeatingDistance.getText().isEmpty()) {
                Double distance = Double.parseDouble(districtHeatingDistance.getText());
                modifier.setMaxDistrictHeatingDistance(distance);
            }

            return modifier;
        } catch (NumberFormatException ex) {
            Logger.getLogger(ModifierController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private List<String> getSelectedQuarters() {
        List<String> quarters = new ArrayList<>();
        locationCheckTree.getCheckModel().getCheckedItems().stream().filter((item) -> (item.getChildren().isEmpty())).forEachOrdered((item) -> {
            quarters.add(item.getValue());
        });

        return quarters;
    }

    private List<String> getSelectedResidentialBuildingTypes() {
        List<String> types = new ArrayList<>();
        typeCheckTree.getCheckModel().getCheckedItems().stream().filter((item) -> (item.getChildren().isEmpty() && item.getParent().getValue().equals(InputController.RESIDENTIAL_BUILDINGS))).forEachOrdered((item) -> {
            types.add(item.getValue());
        });

        return types;
    }

    private List<String> getSelectedNonResidentialBuildingTypes() {
        List<String> types = new ArrayList<>();
        typeCheckTree.getCheckModel().getCheckedItems().stream().filter((item) -> (item.getChildren().isEmpty() && item.getParent().getValue().equals(InputController.NON_RESIDENTIAL_BUILDINGS))).forEachOrdered((item) -> {
            types.add(item.getValue());
        });

        return types;
    }

    @FXML
    private void export() {
        Modifier modifier = getModifier();
        if (modifier != null) {
            try {
                // convert to JSON
                // Add special support for Guava (Google) datatype for Jackson
                ObjectMapper mapper = new ObjectMapper().registerModule(new GuavaModule());
                // enable toString method of enums to return the value to be mapped
                mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
                String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(modifier);

                // set the file chooser
                Stage stage = (Stage) modifierPane.getScene().getWindow();
                final FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON file (*.json)", "*.json"));
                fileChooser.setInitialFileName("Modifier-" + modifier.getName());

                // write the file
                File file = fileChooser.showSaveDialog(stage);
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8));
                bufferedWriter.write(jsonString);
                bufferedWriter.flush();
            } catch (JsonProcessingException ex) {
                Logger.getLogger(ModifierController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ModifierController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error during Export of Modifier");
            alert.setHeaderText("Modifier Export Error");
            alert.setContentText("An error occured during modifier export. Please check if the required fields (duration) are filled out and if the other text fields are filled with meaningful input.");

            alert.showAndWait();
        }
    }

    public void init(InputController parentController, Modifier modifier) {
        init(parentController);

        // base information
        modifierName.setText(modifier.getName());
        modifierStart.setText(String.valueOf(modifier.getDuration().lowerEndpoint()));
        modifierEnd.setText(String.valueOf(modifier.getDuration().upperEndpoint()));
        modifierImpact.setValue(modifier.getImpactFactor());

        // renovation level
        if (modifier.getTargetRenovationLevels() != null) {
            modifier.getTargetRenovationLevels().forEach(renLevel -> {
                switch (renLevel) {
                    case NO_RENOVATION:
                        renLevelUnrenovated.setSelected(true);
                        break;
                    case BASIC_RENOVATION:
                        renLevelEnEV2014.setSelected(true);
                        break;
                    case GOOD_RENOVATION:
                        renLevelPassiveHouse.setSelected(true);
                        break;
                    default:
                        break;
                }
            });
        }

        // heating system
        if (modifier.getTargetHeatingSystems() != null) {
            modifier.getTargetHeatingSystems().forEach(heatingSystem -> {
                if (heatingSystem.equals(HeatingType.LOW_TEMPERATURE_BOILER)) {
                    heatingSystemBoiler.setSelected(true);
                } if (heatingSystem.equals(HeatingType.DISTRICT_HEAT)) {
                    heatingSystemDistrictHeating.setSelected(true);
                } if (heatingSystem.equals(HeatingType.CONDENSING_BOILER)) {
                    heatingSystemCondensingBoiler.setSelected(true);
                }
            });
        }

        // district heating outlet distance
        if (modifier.getMaxDistrictHeatingDistance() != null) {
            districtHeatingDistance.setText(String.valueOf(modifier.getMaxDistrictHeatingDistance()));
        }

        // Construction year
        if (modifier.getYearOfConstructionRange() != null) {
            constructionYearStart.setText(String.valueOf(modifier.getYearOfConstructionRange().lowerEndpoint()));
            constructionYearEnd.setText(String.valueOf(modifier.getYearOfConstructionRange().upperEndpoint()));
        }

        // Location
        if (modifier.getTargetQuarters() != null) {
            selectCheckTreeItem(locationCheckTree.getRoot(), modifier.getTargetQuarters());
        }

        // Building types
        if (modifier.getTargetBuildingsTypes() != null) {
            selectCheckTreeItem(typeCheckTree.getRoot(), modifier.getTargetBuildingsTypes());
        }
    }

    private void selectCheckTreeItem(TreeItem<String> root, Collection<String> values) {
        root.getChildren().forEach((child) -> {
            if (child.getChildren().isEmpty()) {
                if (values.contains(child.getValue())) {
                    ((CheckBoxTreeItem) child).setSelected(true);
                }
            } else {
                selectCheckTreeItem(child, values);
            }
        });
    }

    @FXML
    private void duplicate() {
        Modifier modifier = this.getModifier();
        if (modifier != null) {
            inputController.createModifierTab(modifier);
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error during Duplication of Modifier");
            alert.setHeaderText("Modifier Duplication Error");
            alert.setContentText("An error occured during modifier duplication. Please check if the required fields (duration) are filled out and if the other text fields are filled with meaningful input.");

            alert.showAndWait();
        }
    }
}
