package de.hawhh.gewiss.get.simulator.db.dao;

import de.hawhh.gewiss.get.core.model.EnergySourceType;
import de.hawhh.gewiss.get.core.model.HeatingType;
import de.hawhh.gewiss.get.core.model.PrimaryEnergyFactors;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * DAO for the "primary_energy_factors" table.
 *
 * @author Thomas Preisler
 */
public class PrimaryEnergyFactorsDAO extends SQLiteDAO {

    private final static Logger LOGGER = Logger.getLogger(PrimaryEnergyFactorsDAO.class.getName());

    public PrimaryEnergyFactorsDAO() {
        super();
    }

    /**
     * Find all {@link PrimaryEnergyFactors}s as a {@link Map} where the {@link HeatingType} is the key and the {@link PrimaryEnergyFactors} the value.
     *
     * @return
     */
    public Map<HeatingType, PrimaryEnergyFactors> findAll() {
        Map<HeatingType, PrimaryEnergyFactors> results = new HashMap<>();
        String sql = "SELECT HEATING_SYSTEM, ENERGY_SOURCE_TYPE, PRIMARY_ENERGY_FACTOR, CO2 FROM primary_energy_factors";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                HeatingType heatingType = HeatingType.valueOf(rs.getString(1));
                EnergySourceType energySourceType = EnergySourceType.valueOf(rs.getString(2));
                Double pef = rs.getDouble(3);
                Double co2 = rs.getDouble(4);

                PrimaryEnergyFactors primaryEnergyFactors = new PrimaryEnergyFactors();
                primaryEnergyFactors.setHeatingSystem(heatingType);
                primaryEnergyFactors.setEnergySourceType(energySourceType);
                primaryEnergyFactors.setPrimaryEnergyFactor(pef);
                primaryEnergyFactors.setCo2(co2);

                results.put(heatingType, primaryEnergyFactors);
            }
        } catch (SQLException e) {
            LOGGER.severe(e.getMessage());
        }

        return results;
    }

    /**
     * Find the {@link PrimaryEnergyFactors} for the given {@link HeatingType}.
     *
     * @param heatingSystem
     * @return
     */
    public PrimaryEnergyFactors findBy(HeatingType heatingSystem) {
        LOGGER.info("Getting primary energy factors for heating system " + heatingSystem);

        String sql = "SELECT HEATING_SYSTEM, ENERGY_SOURCE_TYPE, PRIMARY_ENERGY_FACTOR, CO2 FROM primary_energy_factors" +
                " WHERE HEATING_SYSTEM = ?";

        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, heatingSystem.toString());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Double pef = rs.getDouble("PRIMARY_ENERGY_FACTOR");
                Double co2 = rs.getDouble("CO2");
                EnergySourceType energySource = EnergySourceType.valueOf(rs.getString("ENERGY_SOURCE_TYPE"));

                PrimaryEnergyFactors primaryEnergyFactors = new PrimaryEnergyFactors();
                primaryEnergyFactors.setHeatingSystem(heatingSystem);
                primaryEnergyFactors.setEnergySourceType(energySource);
                primaryEnergyFactors.setPrimaryEnergyFactor(pef);
                primaryEnergyFactors.setCo2(co2);

                return primaryEnergyFactors;
            }
        } catch (SQLException e) {
            LOGGER.severe(e.getMessage());
        }

        return null;
    }
}