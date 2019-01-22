package de.hawhh.gewiss.get.simulator.db.dao;

import de.hawhh.gewiss.get.core.model.EnergySourceType;
import de.hawhh.gewiss.get.core.model.HeatingType;
import de.hawhh.gewiss.get.core.model.PrimaryEnergyFactor;

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
     * Find all {@link PrimaryEnergyFactor}s as a {@link Map} where the {@link HeatingType} is the key and the {@link PrimaryEnergyFactor} the value.
     *
     * @return
     */
    public Map<HeatingType, PrimaryEnergyFactor> findAll() {
        Map<HeatingType, PrimaryEnergyFactor> results = new HashMap<>();
        String sql = "SELECT HEATING_SYSTEM, ENERGY_SOURCE_TYPE, PRIMARY_ENERGY_FACTOR, CO2 FROM primary_energy_factors";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                HeatingType heatingType = HeatingType.valueOf(rs.getString(1));
                EnergySourceType energySourceType = EnergySourceType.valueOf(rs.getString(2));
                Double pef = rs.getDouble(3);
                Double co2 = rs.getDouble(4);

                PrimaryEnergyFactor primaryEnergyFactor = new PrimaryEnergyFactor();
                primaryEnergyFactor.setHeatingSystem(heatingType);
                primaryEnergyFactor.setEnergySourceType(energySourceType);
                primaryEnergyFactor.setPrimaryEnergyFactor(pef);
                primaryEnergyFactor.setCo2(co2);

                results.put(heatingType, primaryEnergyFactor);
            }
        } catch (SQLException e) {
            LOGGER.severe(e.getMessage());
        }

        return results;
    }

    /**
     * Find the {@link PrimaryEnergyFactor} for the given {@link HeatingType}.
     *
     * @param heatingSystem
     * @return
     */
    public PrimaryEnergyFactor findBy(HeatingType heatingSystem) {
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

                PrimaryEnergyFactor primaryEnergyFactor = new PrimaryEnergyFactor();
                primaryEnergyFactor.setHeatingSystem(heatingSystem);
                primaryEnergyFactor.setEnergySourceType(energySource);
                primaryEnergyFactor.setPrimaryEnergyFactor(pef);
                primaryEnergyFactor.setCo2(co2);

                return primaryEnergyFactor;
            }
        } catch (SQLException e) {
            LOGGER.severe(e.getMessage());
        }

        return null;
    }
}