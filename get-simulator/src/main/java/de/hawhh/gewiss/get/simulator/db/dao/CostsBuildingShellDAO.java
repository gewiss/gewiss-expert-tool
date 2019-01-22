package de.hawhh.gewiss.get.simulator.db.dao;

import de.hawhh.gewiss.get.core.model.CostsBuildingShell;
import de.hawhh.gewiss.get.core.model.RenovationLevel;
import de.hawhh.gewiss.get.core.util.RenovationLevelMapper;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * DAO class for the "costs_building_shell" table.
 *
 * @author Thomas Preisler
 */
public class CostsBuildingShellDAO extends SQLiteDAO {

    private final static Logger LOGGER = Logger.getLogger(CostsBuildingShellDAO.class.getName());

    public CostsBuildingShellDAO() {
        super();
    }

    /**
     * Return all {@link CostsBuildingShell}s from the database.
     *
     * @return MultiKeyMap with building type ({@link String}) and {@link RenovationLevel} as keys
     */
    public MultiKeyMap<MultiKey<?>, CostsBuildingShell> findAll() {
        MultiKeyMap map = new MultiKeyMap();

        String sql = "SELECT BUILD_TYPE, RENOVATION_LEVEL, COST_PER_SQ_METER FROM costs_building_shell";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String buildingType = rs.getString(1);
                RenovationLevel renovationLevel = RenovationLevelMapper.fromInteger(rs.getInt(2));
                Double costs = rs.getDouble(3);

                CostsBuildingShell costsBuildingShell = new CostsBuildingShell();
                costsBuildingShell.setBuildingType(buildingType);
                costsBuildingShell.setRenovationLevel(renovationLevel);
                costsBuildingShell.setCostPerSQM(costs);

                map.put(buildingType, renovationLevel, costsBuildingShell);
            }
        } catch (SQLException e) {
            LOGGER.severe(e.getMessage());
        }

        return map;
    }

    /**
     * Find a {@link CostsBuildingShell} by the given building type and {@link RenovationLevel}.
     *
     * @param buildingType
     * @param renovationLevel
     * @return
     */
    public CostsBuildingShell findBy(String buildingType, RenovationLevel renovationLevel) {
        LOGGER.info("Getting costs building shell for building type " + buildingType + " and renovation level "
                + renovationLevel
        );

        String sql = "SELECT BUILD_TYPE, RENOVATION_LEVEL, COST_PER_SQ_METER FROM costs_building_shell WHERE" +
                " BUILD_TYPE = ? AND RENOVATION_LEVEL = ?";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, buildingType);
            stmt.setInt(2, RenovationLevelMapper.fromRenovationLevel(renovationLevel));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Double costs = rs.getDouble("COST_PER_SQ_METER");

                CostsBuildingShell cbs = new CostsBuildingShell();
                cbs.setBuildingType(buildingType);
                cbs.setRenovationLevel(renovationLevel);
                cbs.setCostPerSQM(costs);

                return cbs;
            }
        } catch (SQLException e) {
            LOGGER.severe(e.getMessage());
        }

        return null;
    }
}
