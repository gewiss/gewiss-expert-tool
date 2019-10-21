package de.hawhh.gewiss.get.core.input;

import de.hawhh.gewiss.get.core.model.HeatingType;
import de.hawhh.gewiss.get.core.model.PrimaryEnergyFactors;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Data model class for Co2 Emission Data per {@link HeatingType} with starting, mid-range and final emission rates.
 * A list of CO2FactorsData objects is used to fill the yearlyCO2Emissions map in {@link PrimaryEnergyFactors}
 * using linear interpolation.
 *
 * @author Antony Sotirov
 */
public class CO2FactorsData {

    private final SimpleStringProperty heatingSystem;
    private final SimpleDoubleProperty startEmissions;
    private final SimpleDoubleProperty midEmissions;
    private final SimpleDoubleProperty finalEmissions;

    public CO2FactorsData(HeatingType heatingSystem, Double startEmissions, Double midEmissions, Double finalEmissions) {
        this.heatingSystem = new SimpleStringProperty(heatingSystem.toString());
        this.startEmissions = new SimpleDoubleProperty(startEmissions);
        this.midEmissions = new SimpleDoubleProperty(midEmissions);
        this.finalEmissions = new SimpleDoubleProperty(finalEmissions);
    }

    // heating type (getter and setter based around HeatingType parameters, not strings!)
    public HeatingType getHeatingSystem() {
        return HeatingType.valueOf(this.heatingSystem.get());
    }

    public String getHeatSystemString() {
        return heatingSystem.get();
    }

    public void setHeatingSystem(HeatingType heatingSystem) {
        this.heatingSystem.set(heatingSystem.toString());
    }

    // start year
    public Double getStartEmissions() {
        return startEmissions.get();
    }

    public SimpleDoubleProperty startEmissionsProperty() {
        return startEmissions;
    }

    public void setStartEmissions(double startEmissions) {
        this.startEmissions.set(startEmissions);
    }

    // mid year
    public Double getMidEmissions() {
        return midEmissions.get();
    }

    public SimpleDoubleProperty midEmissionsProperty() {
        return midEmissions;
    }

    public void setMidEmissions(double midEmissions) {
        this.midEmissions.set(midEmissions);
    }

    // final year
    public Double getFinalEmissions() {
        return finalEmissions.get();
    }

    public SimpleDoubleProperty finalEmissionsProperty() {
        return finalEmissions;
    }

    public void setFinalEmissions(double finalEmissions) {
        this.finalEmissions.set(finalEmissions);
    }
}
