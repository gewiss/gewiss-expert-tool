package de.hawhh.gewiss.get.simulator.db.dao;

import de.hawhh.gewiss.get.core.model.HeatDemand;
import de.hawhh.gewiss.get.core.model.RenovationLevel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SQLiteDAO class for {@link HeatDemand}.
 *
 * @author Thomas Preisler
 */
public class HeatDemandDAO extends SQLiteDAO {

    private final static Logger LOGGER = Logger.getLogger(HeatDemandDAO.class.getName());

    public HeatDemandDAO() {
        super();
    }

    /**
     * Find and return the {@link HeatDemand} for the given building type.
     *
     * @param type the given building type
     * @return the found heat demand or <code>null</code> if none was found
     */
    public HeatDemand findByType(String type) {
        try {
            LOGGER.log(Level.INFO, "Getting heat demand from SQLite DB for type {0}", type);

            String sql = "SELECT typ, warmwasser0, heizwaerme0, warmwasser1, heizwaerme1, warmwasser2, heizwaerme2 FROM waermebedarf WHERE typ = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, type);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String typ = rs.getString("typ");
                Double warmwasser0 = rs.getDouble("warmwasser0");
                Double warmwasser1 = rs.getDouble("warmwasser1");
                Double warmwasser2 = rs.getDouble("warmwasser2");
                Double heizwaerme0 = rs.getDouble("heizwaerme0");
                Double heizwaerme1 = rs.getDouble("heizwaerme1");
                Double heizwaerme2 = rs.getDouble("heizwaerme2");

                HeatDemand hd = new HeatDemand();
                hd.setBuildingType(typ);

                Map<RenovationLevel, Double> water = new HashMap<>();
                water.put(RenovationLevel.NO_RENOVATION, warmwasser0);
                water.put(RenovationLevel.BASIC_RENOVATION, warmwasser1);
                water.put(RenovationLevel.GOOD_RENOVATION, warmwasser2);

                Map<RenovationLevel, Double> heat = new HashMap<>();
                heat.put(RenovationLevel.NO_RENOVATION, heizwaerme0);
                heat.put(RenovationLevel.BASIC_RENOVATION, heizwaerme1);
                heat.put(RenovationLevel.GOOD_RENOVATION, heizwaerme2);

                hd.setHeatingDemand(heat);
                hd.setWarmWaterDemand(water);

                return hd;
            }
        } catch (SQLException ex) {
            Logger.getLogger(HeatDemandDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * @return a map with all heat demands from the db. The key is the building type and the value the actual class.
     */
    public Map<String, HeatDemand> findAll() {
        try {
            LOGGER.log(Level.INFO, "Getting heat demands from SQLite DB");
            Map<String, HeatDemand> results = new HashMap<>();

            String sql = "SELECT typ, warmwasser0, heizwaerme0, warmwasser1, heizwaerme1, warmwasser2, heizwaerme2 FROM waermebedarf";
            PreparedStatement stmt = connection.prepareStatement(sql);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String typ = rs.getString("typ");
                Double warmwasser0 = rs.getDouble("warmwasser0");
                Double warmwasser1 = rs.getDouble("warmwasser1");
                Double warmwasser2 = rs.getDouble("warmwasser2");
                Double heizwaerme0 = rs.getDouble("heizwaerme0");
                Double heizwaerme1 = rs.getDouble("heizwaerme1");
                Double heizwaerme2 = rs.getDouble("heizwaerme2");

                HeatDemand hd = new HeatDemand();
                hd.setBuildingType(typ);

                Map<RenovationLevel, Double> water = new HashMap<>();
                water.put(RenovationLevel.NO_RENOVATION, warmwasser0);
                water.put(RenovationLevel.BASIC_RENOVATION, warmwasser1);
                water.put(RenovationLevel.GOOD_RENOVATION, warmwasser2);

                Map<RenovationLevel, Double> heat = new HashMap<>();
                heat.put(RenovationLevel.NO_RENOVATION, heizwaerme0);
                heat.put(RenovationLevel.BASIC_RENOVATION, heizwaerme1);
                heat.put(RenovationLevel.GOOD_RENOVATION, heizwaerme2);

                hd.setHeatingDemand(heat);
                hd.setWarmWaterDemand(water);

                results.put(typ, hd);
            }

            return results;
        } catch (SQLException ex) {
            Logger.getLogger(HeatDemandDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
}
