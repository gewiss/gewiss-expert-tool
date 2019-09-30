package de.hawhh.gewiss.get.simulator.db.dao;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import de.hawhh.gewiss.get.core.model.Building;
import de.hawhh.gewiss.get.core.model.ConstructionAgeClass;
import de.hawhh.gewiss.get.core.model.HeatingType;
import de.hawhh.gewiss.get.core.util.GeometryHelper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class implementing the BuildingDAO interface for a SQLite database.
 *
 * @author Thomas Preisler
 */
public class SQLiteBuildingDAO extends SQLiteDAO implements BuildingDAO {

    private final static Logger LOGGER = Logger.getLogger(SQLiteBuildingDAO.class.getName());

    private final Map<String, ConstructionAgeClass> constructionAgeClasses;

    public SQLiteBuildingDAO() {
        super();

        // related DAOs
        ConstructionAgeClassDAO constructionAgeClassDAO = new ConstructionAgeClassDAO();
        constructionAgeClasses = constructionAgeClassDAO.findAll();
    }

    @Override
    public List<Building> findAll() {
        LOGGER.log(Level.INFO, "Parsing buildings from SQLite DB");

        List<Building> buildings = new ArrayList<>();

        // WG + NWG
        String sql = "SELECT alkis_id, geomwkt, wohnfl, nwg_ngf, bezirk, stadtteil, stat_gebiet, baublock, bj_alk_dt, dt_san_year, dt_heiztyp,iwu_typ, nwg_typ, bak_fin, cluster, dt_fw_dist"
                + " FROM gewiss_buildings_v_1 WHERE nwg_typ NOT LIKE 'kein_Bedarf' AND NOT(iwu_typ LIKE '_' AND nwg_typ LIKE '_')";

        // just WG
        //String sql = "SELECT alkis_id, geomwkt, wohnfl, nwg_ngf, bezirk, stadtteil, stat_gebiet, baublock, bj_alk_dt, dt_san_year, dt_heiztyp,iwu_typ, nwg_typ, bak_fin, cluster, dt_fw_dist FROM gewiss_buildings_v_1 WHERE iwu_typ NOT LIKE '_'";
        
        // just NWG
        //String sql = "SELECT alkis_id, geomwkt, wohnfl, nwg_ngf, bezirk, stadtteil, stat_gebiet, baublock, bj_alk_dt, dt_san_year, dt_heiztyp,iwu_typ, nwg_typ, bak_fin, cluster, dt_fw_dist FROM gewiss_buildings_v_1 WHERE nwg_typ NOT LIKE '_' AND nwg_typ NOT LIKE 'kein_Bedarf'";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);

            int count = 0;
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String alkisID = rs.getString("alkis_id");
                String geomWkt = rs.getString("geomwkt");
                Double residentialFloorSpace = rs.getDouble("wohnfl");
                Double nonResidentialFloorSpace = rs.getDouble("nwg_ngf");
                String district = rs.getString("bezirk");
                String quarter = rs.getString("stadtteil");
                String statisticalArea = rs.getString("stat_gebiet");
                String cityBlock = rs.getString("baublock");
                Integer yearOfConstruction = rs.getInt("bj_alk_dt");
                Integer yearOfRenovation = rs.getInt("dt_san_year");
                String residentialType = rs.getString("iwu_typ");
                String nonResidentialType = rs.getString("nwg_typ");
                String clusterID = rs.getString("cluster");
                Integer districtHeatingOutletDistance = rs.getInt("dt_fw_dist");
                ConstructionAgeClass constructionAgeClass = constructionAgeClasses.get(rs.getString("bak_fin"));
                String heatingTypeString = rs.getString("dt_heiztyp");
                HeatingType heatingType = HeatingType.valueOf(heatingTypeString);

                Building building = createBuilding(alkisID, geomWkt, residentialFloorSpace, nonResidentialFloorSpace, district, quarter, statisticalArea, cityBlock,
                        yearOfConstruction, yearOfRenovation, residentialType, nonResidentialType, constructionAgeClass, clusterID, heatingType, districtHeatingOutletDistance);
                if (building != null) {
                    buildings.add(building);
                }
                
                if ((count % 10000) == 0) {
                    LOGGER.log(Level.INFO, "Parsed {0} buildings", count);
                }
                count++;
            }
        } catch (SQLException e) {
            Logger.getLogger(SQLiteBuildingDAO.class.getName()).log(Level.SEVERE, null, e);
        }

        LOGGER.log(Level.INFO, "Finished parsing buildings from PostGIS DB");
        return buildings;
    }

    /**
     * Creates a buidling from the given data.
     *
     * @param alkisID ALKIS Id
     * @param geomWkt WKT String representation of the geometry
     * @param residentialFloorSpace residential floor space (Wohnfläche)
     * @param nonResidentialFloorSpace non residential floor space (Nutzfläche)
     * @param district district (Bezirk)
     * @param quarter district (Stadtteil)
     * @param statisticalArea statistical area (statistisches Gebiet)
     * @param cityBlock city block (Baublock)
     * @param yearOfConstruction year of construction
     * @param yearOfRenovation year of (last) renovation
     * @param residentialType residential building type (IWU)
     * @param nonResidentialType non residential building type (Ecofys)
     * @param constructionAgeClass construction age class (Baualtersklasse)
     * @param clusterID
     * @param heatingType 
     * @param districtHeatingOutletDistance distance in meter to the nearest district heating outlet
     * @return
     */
    private Building createBuilding(String alkisID, String geomWkt, Double residentialFloorSpace, Double nonResidentialFloorSpace, String district, String quarter,
            String statisticalArea, String cityBlock, Integer yearOfConstruction, Integer yearOfRenovation, String residentialType, String nonResidentialType,
            ConstructionAgeClass constructionAgeClass, String clusterID, HeatingType heatingType, Integer districtHeatingOutletDistance) {
        Geometry geometry = null;
        try {
            geometry = GeometryHelper.convertToJTSPolygon(geomWkt);
        } catch (ParseException e) {
            LOGGER.severe(e.getMessage());
        }

        Building building = new Building();
        building.setAlkisID(alkisID);
        building.setGeometry(geometry);
        building.setResidentialFloorSpace(residentialFloorSpace);
        building.setNonResidentialFloorSpace(nonResidentialFloorSpace);
        building.setDistrict(district);
        building.setQuarter(quarter);
        building.setStatisticalArea(statisticalArea);
        building.setCityBlock(cityBlock);
        building.setYearOfConstruction(yearOfConstruction);
        building.setYearOfRenovation(yearOfRenovation);
        building.setResidentialType(residentialType);
        building.setNonResidentialType(nonResidentialType);
        building.setConstructionAgeClass(constructionAgeClass);
        building.setClusterID(clusterID);
        building.setHeatingType(heatingType);
        building.setDistrictHeatingOutletDistance(districtHeatingOutletDistance);

        return building;
    }

    /**
     * Find and return the {@link Building} with the given Id.
     *
     * @param buildingId the given building id
     * @return the found parcel or null if no parcel matches the given id
     */
    @Override
    public Building findById(String buildingId) {
        String sql = "SELECT alkis_id, geomwkt, wohnfl, nwg_ngf, bezirk, stadtteil, stat_gebiet, baublock, bj_alk_dt, dt_san_year, dt_heiztyp, iwu_typ, nwg_typ, bak_fin, cluster, dt_fw_dist"
                + " FROM gewiss_buildings_v_1 WHERE alkis_id = ?";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, buildingId);

            ResultSet rs = stmt.executeQuery();
            // @TODO: add building ownership information: "property" (Bezitz)
            while (rs.next()) {
                String alkisID = rs.getString("alkis_id");
                String geomWkt = rs.getString("geomwkt");
                Double residentialFloorSpace = rs.getDouble("wohnfl");
                Double nonResidentialFloorSpace = rs.getDouble("nwg_ngf");
                String district = rs.getString("bezirk");
                String quarter = rs.getString("stadtteil");
                String statisticalArea = rs.getString("stat_gebiet");
                String cityBlock = rs.getString("baublock");
                Integer yearOfConstruction = rs.getInt("bj_alk_dt");
                Integer yearOfRenovation = rs.getInt("dt_san_year");
                String residentialType = rs.getString("iwu_typ");
                String nonResidentialType = rs.getString("nwg_typ");
                String clusterID = rs.getString("cluster");
                Integer districtHeatingOutletDistance = rs.getInt("dt_fw_dist");
                ConstructionAgeClass constructionAgeClass = constructionAgeClasses.get(rs.getString("bak_fin"));
                String heatingTypeString = rs.getString("dt_heiztyp");
                HeatingType heatingType = HeatingType.valueOf(heatingTypeString);

                return createBuilding(alkisID, geomWkt, residentialFloorSpace, nonResidentialFloorSpace, district, quarter, statisticalArea, cityBlock,
                        yearOfConstruction, yearOfRenovation, residentialType, nonResidentialType, constructionAgeClass, clusterID, heatingType, districtHeatingOutletDistance);
            }
        } catch (SQLException e) {
            Logger.getLogger(SQLiteBuildingDAO.class.getName()).log(Level.SEVERE, null, e);
        }

        return null;
    }

    @Override
    public List<String> getResidentialBuildingTypes() {
        try {
            LOGGER.info("Getting residential building types from SQLite DB");

            List<String> types = new ArrayList<>();

            String sql = "select distinct(iwu_typ) from gewiss_buildings_v_1 where iwu_typ not like '_' order by iwu_typ";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String type = rs.getString("iwu_typ");
                types.add(type);
            }

            return types;
        } catch (SQLException ex) {
            Logger.getLogger(SQLiteBuildingDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    @Override
    public List<String> getNonResidentialBuildingTypes() {
        try {
            LOGGER.info("Getting non residential building types from SQLite DB");

            List<String> types = new ArrayList<>();

            String sql = "select distinct(nwg_typ) from gewiss_buildings_v_1 where nwg_typ not like '_' order by nwg_typ";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String type = rs.getString("nwg_typ");
                types.add(type);
            }

            return types;
        } catch (SQLException ex) {
            Logger.getLogger(SQLiteBuildingDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
}