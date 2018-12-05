package de.hawhh.gewiss.get.simulator.db.dao;

import de.hawhh.gewiss.get.core.model.ConstructionAgeClass;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SQLiteDAO Class for {@link ConstructionAgeClass}.
 *
 * @author Thomas Preisler
 */
public class ConstructionAgeClassDAO extends SQLiteDAO {

    private final static Logger LOGGER = Logger.getLogger(ConstructionAgeClassDAO.class.getName());

    public ConstructionAgeClassDAO() {
        super();
    }

    /**
     * Find and return the {@link ConstructionAgeClass} with the given name.
     *
     * @param className the given construction age class name
     * @return the found construction age class or <code>null</code> if none was found
     */
    public ConstructionAgeClass findByName(String className) {
        try {
            LOGGER.log(Level.INFO, "Getting construction age class from SQLite DB for class {0}", className);

            String sql = "SELECT klasse, von, bis FROM baualtersklassen where klasse = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, className);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String klasse = rs.getString("klasse");
                Integer von = rs.getInt("von");
                Integer bis = rs.getInt("bis");

                ConstructionAgeClass ageClass = new ConstructionAgeClass();
                ageClass.setAgeClass(klasse);
                ageClass.setFromYear(von);
                ageClass.setToYear(bis);

                return ageClass;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConstructionAgeClassDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * @return a map with all construction age classes from the db. The key is the name of the construction age class and the value the actual class.
     */
    public Map<String, ConstructionAgeClass> findAll() {
        try {
            LOGGER.log(Level.INFO, "Getting all construction age classes from SQLite DB");
            Map<String, ConstructionAgeClass> results = new HashMap<>();

            String sql = "SELECT klasse, von, bis FROM baualtersklassen";
            PreparedStatement stmt = connection.prepareStatement(sql);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String klasse = rs.getString("klasse");
                Integer von = rs.getInt("von");
                Integer bis = rs.getInt("bis");

                ConstructionAgeClass ageClass = new ConstructionAgeClass();
                ageClass.setAgeClass(klasse);
                ageClass.setFromYear(von);
                ageClass.setToYear(bis);

                results.put(klasse, ageClass);
            }

            return results;
        } catch (SQLException ex) {
            Logger.getLogger(ConstructionAgeClassDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
}
