package de.hawhh.gewiss.get.core.output;

import com.vividsolutions.jts.geom.Geometry;
import lombok.Data;

/**
 * Data class
 *
 * @author Nils Weiss
 */

@Data
public class BuildingInformation {

    private String buildingId;
    private String clusterId;
    private String quarter;
    private Geometry geometry;

    public static BuildingInformation create(String buildingId, String clusterId, String quarter, Geometry geometry) {
        
        BuildingInformation building = new BuildingInformation();
        building.setBuildingId(buildingId);
        building.setClusterId(clusterId);
        building.setQuarter(quarter);
        building.setGeometry(geometry);
        
        return building;
    }
}
