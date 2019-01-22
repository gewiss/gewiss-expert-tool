package de.hawhh.gewiss.get.simulator.db.dao;

import de.hawhh.gewiss.get.core.model.HeatDemandFinalEnergy;
import de.hawhh.gewiss.get.core.model.HeatingType;
import de.hawhh.gewiss.get.core.model.RenovationLevel;
import de.hawhh.gewiss.get.core.util.RenovationLevelMapper;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * DAO for the "heat_demand_final_energy" table.
 *
 * @author Thomas Preisler
 */
public class HeatDemandFinalEnergyDAO extends SQLiteDAO {

    private final static Logger LOGGER = Logger.getLogger(HeatDemandFinalEnergyDAO.class.getName());

    public HeatDemandFinalEnergyDAO() {
        super();
    }

    /**
     * Return all {@link HeatDemandFinalEnergy}s from the database.
     *
     * @return MultiKeyMap with building type ({@link String}), {@link RenovationLevel} and {@link HeatingType }as keys
     */
    public MultiKeyMap<MultiKey<?>, HeatDemandFinalEnergy> findAll() {
        MultiKeyMap map = new MultiKeyMap();
        String sql = "SELECT BUILD_TYPE, RENOVATION_LEVEL, HEATING_SYSTEM, FINAL_ENERGY FROM heat_demand_final_energy";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String buildingType = rs.getString(1);
                RenovationLevel renovationLevel = RenovationLevelMapper.fromInteger(rs.getInt(2));
                HeatingType heatingType = HeatingType.valueOf(rs.getString(3));
                Double finalEnergy = rs.getDouble(4);

                HeatDemandFinalEnergy heatDemandFinalEnergy = new HeatDemandFinalEnergy();
                heatDemandFinalEnergy.setBuildingType(buildingType);
                heatDemandFinalEnergy.setRenovationLevel(renovationLevel);
                heatDemandFinalEnergy.setHeatingSystem(heatingType);
                heatDemandFinalEnergy.setFinalEnergy(finalEnergy);

                map.put(buildingType, renovationLevel, heatingType, heatDemandFinalEnergy);
            }
        } catch (SQLException e) {
            LOGGER.severe(e.getMessage());
        }

        return map;
    }

    /**
     * Return the {@link HeatDemandFinalEnergy} matching the given building type ({@link String}), {@link RenovationLevel and HeatingType}.
     *
     * @param buildingType
     * @param renovationLevel
     * @param heatingSystem
     * @return
     */
    HeatDemandFinalEnergy findBy(String buildingType, RenovationLevel renovationLevel, HeatingType heatingSystem) {
        LOGGER.info("Getting heat demand and final enegry for building type "
                + buildingType + ", renovation level " + renovationLevel + " and heating system " + heatingSystem);

        String sql = "SELECT BUILD_TYPE, RENOVATION_LEVEL, HEATING_SYSTEM, FINAL_ENERGY FROM heat_demand_final_energy" +
                " WHERE BUILD_TYPE = ? AND RENOVATION_LEVEL = ? AND HEATING_SYSTEM = ?";

        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, buildingType);
            stmt.setInt(2, RenovationLevelMapper.fromRenovationLevel(renovationLevel));
            stmt.setString(3, heatingSystem.toString());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Double finalEnergy = rs.getDouble("FINAL_ENERGY");

                HeatDemandFinalEnergy heatDemandFinalEnergy = new HeatDemandFinalEnergy();
                heatDemandFinalEnergy.setBuildingType(buildingType);
                heatDemandFinalEnergy.setRenovationLevel(renovationLevel);
                heatDemandFinalEnergy.setHeatingSystem(heatingSystem);
                heatDemandFinalEnergy.setFinalEnergy(finalEnergy);

                return heatDemandFinalEnergy;
            }
        } catch (SQLException e) {
            LOGGER.severe(e.getMessage());
        }

        return null;
    }
}
