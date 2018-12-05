package de.hawhh.gewiss.get.simulator.db.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;
import org.postgresql.util.PGobject;

/**
 * Utility class holding the {@link Connection} to the database.
 *
 * @author Thomas Preisler
 */
public class PostgresConnection {

    private final static Logger LOGGER = Logger.getLogger(PostgresConnection.class.getName());

    /**
     * The {@link Connection} to the Database
     */
    private static Connection connection = null;

    /**
     * Return the {@link Connection} to the Database, if the connection is <code>null</code> or closed a new connection will be opened.
     *
     * @param host the database host
     * @param database the name of the database
     * @param port the port of the database
     * @param user the database user
     * @param password the user password
     * @return the connection to the Database.
     */
    @SuppressWarnings("unchecked")
    public static Connection getConnection(String host, String database, Integer port, String user, String password) {
        try {
            if (connection == null || connection.isClosed()) {
                String dbUrl = "jdbc:postgresql://" + host + ":" + port + "/" + database;
                Class.forName("org.postgresql.Driver").newInstance();
                connection = DriverManager.getConnection(dbUrl, user, password);

                LOGGER.info("PostgresConnection - getConnection() successfully connected to DB");

                /*
                * Add the geometry types to the connection. Note that you must cast the connection to the pgsql-specific connection implementation before
                * calling the addDataType() method.
                */
                ((org.postgresql.PGConnection) connection).addDataType("geometry", (Class<? extends PGobject>) Class.forName("org.postgis.PGgeometry"));
                ((org.postgresql.PGConnection) connection).addDataType("box3d", (Class<? extends PGobject>) Class.forName("org.postgis.PGbox3d"));
            }
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
            LOGGER.severe(e.getMessage());
        }
        return connection;
    }
}
