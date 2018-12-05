package de.hawhh.gewiss.get.simulator.db.dao;

import de.hawhh.gewiss.get.core.model.EnergySource;
import de.hawhh.gewiss.get.core.model.EnergySource.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SQLiteDAO class for {@link EnergySource}.
 *
 * @author Nils Weiss
 */
public class SQLiteEnergySourceDAO extends SQLiteDAO implements EnergySourceDAO {

    private final static Logger LOGGER = Logger.getLogger(SQLiteEnergySourceDAO.class.getName());

    public SQLiteEnergySourceDAO() {
        super();
    }

    /**
     * Find and return the {@link EnergySource} for the given source type.
     *
     * @param type the given source type
     * @return the found EnergySource factors or <code>null</code> if none was
     * found
     */
    @Override
    public EnergySource findByType(Type type) {
        try {
            LOGGER.log(Level.INFO, "Getting primary energy factor from SQLite DB for type {0}", type);

            String sql = "SELECT typ, primaerenergiefaktor, CO2 FROM primary_energy_factor_co2_factor where typ = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, type.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Double peFactor = rs.getDouble("Primaerenergiefaktor");
                Double co2Factor = rs.getDouble("CO2");

                EnergySource energySource = new EnergySource();
                energySource.setType(type);
                energySource.setPrimaryEnergyFactor(peFactor);
                energySource.setCo2Emission(co2Factor);

                return energySource;

            }
        } catch (SQLException ex) {
            Logger.getLogger(SQLiteEnergySourceDAO.class.getName()).log(Level.SEVERE, null, ex);

        }

        return null;
    }

    /**
     * @return a map with all energy sources from the db. The key is the source
     * type and the value the actual class.
     */
    @Override
    public Map<EnergySource.Type, EnergySource> findAll() {
        Map<EnergySource.Type, EnergySource> results = new HashMap<>();
        try {
            
            LOGGER.log(Level.INFO, "Getting all energy sources from SQLite DB");

            String sql = "SELECT typ, primaerenergiefaktor, CO2 FROM primary_energy_factor_co2_factor";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {

                String typ = rs.getString("typ");
                Double peFactor = rs.getDouble("Primaerenergiefaktor");
                Double co2Factor = rs.getDouble("CO2");

                EnergySource.Type esType = Type.valueOf(typ);
                EnergySource energySource = new EnergySource();
                energySource.setType(esType);
                energySource.setPrimaryEnergyFactor(peFactor);
                energySource.setCo2Emission(co2Factor);

                results.put(esType, energySource);
            }
            return results;
        } catch (SQLException ex) {
            Logger.getLogger(SQLiteEnergySourceDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        LOGGER.log(Level.INFO, "Finished parsing energy sources from SQLite DB");
        return results;
    }

}
