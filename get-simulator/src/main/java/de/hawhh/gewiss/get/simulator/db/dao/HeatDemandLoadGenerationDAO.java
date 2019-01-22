package de.hawhh.gewiss.get.simulator.db.dao;

import de.hawhh.gewiss.get.core.model.HeatDemandLoadGeneration;
import de.hawhh.gewiss.get.core.model.RenovationLevel;
import de.hawhh.gewiss.get.core.util.RenovationLevelMapper;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * DAO class for the "heat_demand_and_load_at_generation" table.
 *
 * @author Thomas Preisler
 */
public class HeatDemandLoadGenerationDAO extends SQLiteDAO {

    private final static Logger LOGGER = Logger.getLogger(HeatDemandLoadGenerationDAO.class.getName());

    public HeatDemandLoadGenerationDAO() {
        super();
    }

    /**
     * Return all {@link HeatDemandLoadGeneration}s from the database.
     *
     * @return MultiKeyMap with building type ({@link String}) and {@link RenovationLevel} as keys
     */
    public MultiKeyMap<MultiKey<?>, HeatDemandLoadGeneration> findAll() {
        MultiKeyMap map = new MultiKeyMap();

        String sql = "SELECT BUILD_TYPE, RENOVATION_LEVEL, SPACE_HEATING_DEMAND, WARMWATER_DEMAND, SPACE_HEATING_LOAD, WARMWATER_LOAD " +
                "FROM heat_demand_and_load_at_generation";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String buildingType = rs.getString(1);
                RenovationLevel renovationLevel = RenovationLevelMapper.fromInteger(rs.getInt(2));
                Double spaceHeatingDemand = rs.getDouble(3);
                Double warmwaterDemand = rs.getDouble(4);
                Double spaceHeatingLoad = rs.getDouble(5);
                Double warmwaterLoad = rs.getDouble(6);

                HeatDemandLoadGeneration heatDemandLoadGeneration = new HeatDemandLoadGeneration();
                heatDemandLoadGeneration.setBuildingType(buildingType);
                heatDemandLoadGeneration.setSpaceHeatingDemand(spaceHeatingDemand);
                heatDemandLoadGeneration.setWarmwaterDemand(warmwaterDemand);
                heatDemandLoadGeneration.setSpaceHeatingLoad(spaceHeatingLoad);
                heatDemandLoadGeneration.setWarmwaterLoad(warmwaterLoad);

                map.put(buildingType, renovationLevel, heatDemandLoadGeneration);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return map;
    }

    /**
     * Return the {@link HeatDemandLoadGeneration} for the given building type ({@link String}) and {@link RenovationLevel}.
     *
     * @param buildingType
     * @param renovationLevel
     * @return
     */
    HeatDemandLoadGeneration findBy(String buildingType, RenovationLevel renovationLevel) {
        LOGGER.info("Getting heat demand and load at generation for building type "
                + buildingType + " and renovation level " + renovationLevel);

        String sql = "SELECT BUILD_TYPE, RENOVATION_LEVEL, SPACE_HEATING_DEMAND, WARMWATER_DEMAND, SPACE_HEATING_LOAD," +
                " WARMWATER_LOAD FROM heat_demand_and_load_at_generation where BUILD_TYPE = ? AND RENOVATION_LEVEL = ?";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, buildingType);
            stmt.setInt(2, RenovationLevelMapper.fromRenovationLevel(renovationLevel));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Double spaceHeatingDemand = rs.getDouble("SPACE_HEATING_DEMAND");
                Double warmwaterDemand = rs.getDouble("WARMWATER_DEMAND");
                Double spaceHeatingLoad = rs.getDouble("SPACE_HEATING_LOAD");
                Double warmwaterLoad = rs.getDouble("WARMWATER_LOAD");

                HeatDemandLoadGeneration heatDemandLoadGeneration = new HeatDemandLoadGeneration();
                heatDemandLoadGeneration.setBuildingType(buildingType);
                heatDemandLoadGeneration.setRenovationLevel(renovationLevel);
                heatDemandLoadGeneration.setSpaceHeatingDemand(spaceHeatingDemand);
                heatDemandLoadGeneration.setWarmwaterDemand(warmwaterDemand);
                heatDemandLoadGeneration.setSpaceHeatingLoad(spaceHeatingLoad);
                heatDemandLoadGeneration.setWarmwaterLoad(warmwaterLoad);

                return heatDemandLoadGeneration;
            }
        } catch (SQLException e) {
            LOGGER.severe(e.getMessage());
        }

        return null;
    }
}
