package de.hawhh.gewiss.get.core.model;

import com.vividsolutions.jts.geom.Geometry;
import de.hawhh.gewiss.get.core.calc.EnergyCalculator;
import lombok.Data;

/**
 * Model for the buildings in the simulation.
 *
 * @author Thomas Preisler
 */
@Data
public class Building {

    private String alkisID;
    private Geometry geometry;
    private Double residentialFloorSpace; // Wohnfäche
    private Double nonResidentialFloorSpace; // Nutzfläche
    private String district; //Bezirk
    private String quarter; //Stadtteil
    private String statisticalArea; // statistisches Gebiet
    private String cityBlock; // Baublock
    private Integer yearOfConstruction;
    private Integer yearOfRenovation;
    private String residentialType; // IWU-Typ
    private String nonResidentialType; //NWG-Typ
    private String property; // Besitz
    private Boolean monument; // Denkmal
    private ConstructionAgeClass constructionAgeClass; // Baualtersklasse
    private RenovationLevel renovationLevel;
    private String clusterID;
    private String heatingTypeString;
    private HeatingType heatingType;
    private Integer districtHeatingOutletDistance;
    private Double accumulatedRenovationCosts = 0d;

    public Building() {
        this.renovationLevel = RenovationLevel.NO_RENOVATION;
        //this.energyCalculator = new EnergyCalculator();
    }

    /**
     * Renovates the building by applying the given renovation level, updating the last year of renovation and adding
     * the hull renovation costs to the accumulated renovation costs.
     *
     * @param renovationLevel
     */
    public void renovate(RenovationLevel renovationLevel, Integer yearOfRenovation) {
        this.renovationLevel = renovationLevel;
        this.yearOfRenovation = yearOfRenovation;

        this.accumulatedRenovationCosts += EnergyCalculator.getInstance().calcShellRenovationCosts(this);
    }

    public void exchangeHeatingSystem(HeatingType heatingSystem) {
        this.heatingType = heatingSystem;
        this.heatingTypeString = heatingType.toString();

        this.accumulatedRenovationCosts += EnergyCalculator.getInstance().calcHeatingExchangeRenovationCosts(this);
    }
}
