package de.hawhh.gewiss.get.simulator.db.dao;

import de.bytefish.pgbulkinsert.PgBulkInsert;
import de.hawhh.gewiss.get.core.output.SimulationOutput;
import de.hawhh.gewiss.get.core.output.SimulationResult;
import org.postgresql.PGConnection;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PostgresDAO for saving {@link SimulationResult}s and the according {@link SimulationOutput}s in the PostGIS DB.
 *
 * @author Thomas Preisler
 */
public class SimulationResultDAO {

    private final static Logger LOGGER = Logger.getLogger(SimulationResultDAO.class.getName());

    private final Connection connection;

    public SimulationResultDAO(Connection connection) {
        this.connection = connection;
        try {
            this.connection.setAutoCommit(false);
        } catch (SQLException ex) {
            Logger.getLogger(SimulationResultDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Save the given {@link SimulationResult} and return the created ID (key).
     *
     * @param sr SimulationResult Object
     * @return created ID key
     */
    public Integer save(SimulationResult sr) {
        Integer key = null;

        try {
            LOGGER.log(Level.INFO, "Saving given simulation result {0} to database", sr);

            String insertResult = "INSERT INTO simulation_scenario(description, parameter) VALUES(?, ?::JSON)";

            PreparedStatement stmt = connection.prepareStatement(insertResult, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, sr.getName());
            stmt.setObject(2, sr.getParameter());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                key = rs.getInt(1);
                LOGGER.log(Level.INFO, "Inserted row into simulation_scenario, primary key: {0}", key);

                LOGGER.log(Level.INFO, "Bulk inserting into table simulation_output for scenario id {0}", key);
                SimulationOutputBulkInserter bulkInserter = new SimulationOutputBulkInserter(key);
                bulkInserter.saveAll((PGConnection) connection, sr.getOutput().values().stream());
                LOGGER.log(Level.INFO, "Finished bulk insert into table simulation_output for scenario id {0}", key);
            }

            connection.commit();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(SimulationResultDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return key;
    }

    /**
     * A special bulk inserting for PostgreSQL Databases, based on the Postgres COPY command. Way faster than using standard JDBC INSERT BATCH.
     */
    private class SimulationOutputBulkInserter extends PgBulkInsert<SimulationOutput> {

        SimulationOutputBulkInserter(Integer simId) {
            super("public", "simulation_output");

            mapInteger("sim_id", (SimulationOutput so) -> simId);
            mapString("building_id", SimulationOutput::getBuildingId);
            mapInteger("year", SimulationOutput::getYear);
            mapDouble("heat_demand_m2", SimulationOutput::getHeatDemandM2);
            mapDouble("heat_demand", SimulationOutput::getHeatDemand);
            mapString("renovation_level", SimulationOutput::getRenovationLevelString);
            mapString("heating_system", SimulationOutput::getHeatingTypeString);
            mapDouble("renovation_cost", SimulationOutput::getRenovationCost);
            mapDouble("co2_emission", SimulationOutput::getCo2Emission);
            mapDouble("residential_area", SimulationOutput:: getResidentialArea);
            mapDouble("combined_area", SimulationOutput::getCombinedArea);
        }
    }
}
