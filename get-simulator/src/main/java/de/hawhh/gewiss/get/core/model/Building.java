package de.hawhh.gewiss.get.core.model;

import com.vividsolutions.jts.geom.Geometry;
import java.util.Map;
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
    private RenovationCost residentialRenovationCost;
    private RenovationCost nonResidentialRenovationCost;
    private HeatDemand residentialHeatDemand;
    private HeatDemand nonResidentialHeatDemand;
    private RenovationLevel renovationLevel;
    private String clusterID;
    private String heatingTypeString;
    private HeatingType heatingType;
    private Integer districtHeatingOutletDistance;
    
    public Building() {
        this.renovationLevel = RenovationLevel.NO_RENOVATION;
    }

    /**
     * Calculates the heat demand for this building based on it specific heating and warm water demand (IWU for residential and ecofys for non-residential buildings) by
     * multiplying it with the used floor space. For residential and non-residential building as well as buildings that are used in both ways.
     *
     * @return the calculated heat demand
     */
    public Double calcHeatDemand() {
        Double residentialDemand = 0d, nonResidentialDemand = 0d;

        if (residentialHeatDemand != null) {
            residentialDemand = (residentialHeatDemand.getHeatingDemand(renovationLevel) * residentialFloorSpace) + (residentialHeatDemand.getWarmWaterDemand(renovationLevel) * residentialFloorSpace);
        }
        if (nonResidentialHeatDemand != null) {
            nonResidentialDemand = (nonResidentialHeatDemand.getHeatingDemand(renovationLevel) * nonResidentialFloorSpace) + (nonResidentialHeatDemand.getWarmWaterDemand(renovationLevel) * nonResidentialFloorSpace);
        }

        return residentialDemand + nonResidentialDemand;
    }

    /**
     * Calculates the co2 emissions for this building based on it's heat demand
     * @param heatDemand
     * @param heatingToFuelMap
     * @return the calculated co2 emissions
     */    
    public Double calcCo2Emission(Double heatDemand, Map<HeatingType, EnergySource> heatingToFuelMap) {
        Double co2Emission = heatDemand * heatingToFuelMap.get(heatingType).getCo2Emission();
        return co2Emission;
    }

    /**
     * Renovates the building by applying the given renovation level.
     *
     * @param renovationLevel
     */
    public void renovate(RenovationLevel renovationLevel) {
        this.renovationLevel = renovationLevel;
    }
}
