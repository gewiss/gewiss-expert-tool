package de.hawhh.gewiss.get.simulator.db.dao;

import de.hawhh.gewiss.get.core.model.RenovationCost;
import de.hawhh.gewiss.get.core.model.RenovationLevel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SQLiteDAO class for {@link RenovationCost}.
 *
 * @author Thomas Preisler
 */
public class RenovationCostDAO extends SQLiteDAO {

    private final static Logger LOGGER = Logger.getLogger(ConstructionAgeClassDAO.class.getName());

    public RenovationCostDAO() {
        super();
    }

    /**
     * Find and return the {@link RenovationCost} for the given building type.
     *
     * @param type the given building type
     * @return the found renovation cost or <code>null</code> if none was found
     */
    public RenovationCost findByType(String type) {
        try {
            LOGGER.log(Level.INFO, "Getting renovation cost from SQLite DB for type {0}", type);

            String sql = "SELECT typ, regressions_kosten1, regressions_kosten2 FROM sanierungskosten where typ = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, type);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String typ = rs.getString("typ");
                Double kosten1 = rs.getDouble("regressions_kosten1");
                Double kosten2 = rs.getDouble("regressions_kosten2");

                RenovationCost cost = new RenovationCost();
                cost.setBuildingType(type);
                Map<RenovationLevel, Double> costs = new HashMap<>();
                costs.put(RenovationLevel.BASIC_RENOVATION, kosten1);
                costs.put(RenovationLevel.GOOD_RENOVATION, kosten2);
                cost.setRegressionCosts(costs);

                return cost;
            }
        } catch (SQLException ex) {
            Logger.getLogger(RenovationCostDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * @return a map with all renovation costs from the db. The key is the building type and the value the actual class.
     */
    public Map<String, RenovationCost> findAll() {
        try {
            Map<String, RenovationCost> results = new HashMap<>();
            LOGGER.log(Level.INFO, "Getting all renovation cost from SQLite DB");

            String sql = "SELECT typ, regressions_kosten1, regressions_kosten2 FROM sanierungskosten";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String typ = rs.getString("typ");
                Double kosten1 = rs.getDouble("regressions_kosten1");
                Double kosten2 = rs.getDouble("regressions_kosten2");

                RenovationCost cost = new RenovationCost();
                cost.setBuildingType(typ);
                Map<RenovationLevel, Double> costs = new HashMap<>();
                costs.put(RenovationLevel.BASIC_RENOVATION, kosten1);
                costs.put(RenovationLevel.GOOD_RENOVATION, kosten2);
                cost.setRegressionCosts(costs);

                results.put(typ, cost);
            }
            return results;
        } catch (SQLException ex) {
            Logger.getLogger(RenovationCostDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
}
