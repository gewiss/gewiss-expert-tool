package de.hawhh.gewiss.get.simulator.db.dao;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Connection to a SQLite database.
 *
 * @author Thomas Preisler
 */
public class SQLiteConnection {

    public final static String DB_FILE = "/GEWISS_buildings_1.16_IST_anonymized_for_testing.sqlite";
    
    private final static Logger LOGGER = Logger.getLogger(SQLiteConnection.class.getName());

    /**
     * The {@link Connection} to the Database
     */
    private static Connection connection = null;

    /**
     * Return the {@link Connection} to the Database, if the connection is <code>null</code> or closed a new connection will be opened.
     *
     * @return
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                try {
                    // create a connection to the database
                    Class.forName("org.sqlite.JDBC");
                    String url;
                    
                    // check if the program is running in a jar or from IDE
                    if (SQLiteConnection.class.getResource("SQLiteConnection.class").getProtocol().equals("jar")) {
                        LOGGER.info("Running from JAR");
                        url = "jdbc:sqlite::resource:jar:file:lib/get-simulator-0.1.1.jar!" + DB_FILE;
                    } else {
                        LOGGER.info("Running from IDE");

                        URL resource = SQLiteConnection.class.getResource(DB_FILE);
                        String path = resource.getPath();
                        url = "jdbc:sqlite:" + path;
                    }

                    connection = DriverManager.getConnection(url);
                    LOGGER.info("Connection to SQLite has been established");
                } catch (SQLException | ClassNotFoundException ex) {
                    Logger.getLogger(SQLiteConnection.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(SQLiteConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        return connection;
    }
}
