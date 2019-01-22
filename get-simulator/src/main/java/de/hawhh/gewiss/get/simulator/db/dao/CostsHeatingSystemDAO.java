package de.hawhh.gewiss.get.simulator.db.dao;

import de.hawhh.gewiss.get.core.model.CostsHeatingSystem;
import de.hawhh.gewiss.get.core.model.HeatingType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * DAO for the "costs_heating_system" table.
 *
 * @author Thomas Preisler
 */
public class CostsHeatingSystemDAO extends SQLiteDAO {

    private final static Logger LOGGER = Logger.getLogger(CostsHeatingSystemDAO.class.getName());

    public CostsHeatingSystemDAO() {
        super();
    }

    /**
     * @return Map containing the exchange costs for heating system. The {@link HeatingType} is the key and the value is a map
     * with bins containing the renovation costs as value in Euro and the heat load in kW as key.
     */
    public Map<HeatingType, Map<Integer, Double>> findAll() {
        Map<HeatingType, Map<Integer, Double>> results = new HashMap<>();
        String sql = "SELECT HEAT_LOAD_kW, HEATING_SYSTEM, COST FROM costs_heating_system";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Integer heatLoad = rs.getInt(1);
                HeatingType heatingType = HeatingType.valueOf(rs.getString(2));
                Double cost = rs.getDouble(3);

                if (!results.containsKey(heatingType)) {
                    results.put(heatingType, new HashMap<>());
                }
                results.get(heatingType).put(heatLoad, cost);
            }
        } catch (SQLException e) {
            LOGGER.severe(e.getMessage());
        }

        return results;
    }

    /**
     * Find the CostsHeatingSystem matching the given heat load in kW and {@link HeatingType}.
     *
     * @param heatLoadKW
     * @param heatingSystem
     * @return
     */
    public CostsHeatingSystem findBy(Integer heatLoadKW, HeatingType heatingSystem) {
        LOGGER.info("Getting heating system renovation costs for heating system " + heatingSystem +
                " with heat load (KW) " + heatLoadKW);

        String sql = "SELECT MAX(HEAT_LOAD_KW), cost from costs_heating_system " +
                "WHERE HEAT_LOAD_KW <= ? AND HEATING_SYSTEM = ?";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, heatLoadKW);
            stmt.setString(2, heatingSystem.toString());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Integer heatLoadUpper = rs.getInt(1);
                Double costsValue = rs.getDouble(2);

                CostsHeatingSystem costs = new CostsHeatingSystem();
                costs.setHeatingSystem(heatingSystem);
                costs.setHeatLoadKW(heatLoadUpper);
                costs.setCosts(costsValue);

                return costs;
            }
        } catch (SQLException e) {
            LOGGER.severe(e.getMessage());
        }

        return null;
    }
}
