package de.hawhh.gewiss.get.simulator.db.dao;

import java.sql.Connection;

/**
  Abstract super class for DAOs based on the SQLite DB connection.
 * 
 * @author Thomas Preisler
 */
abstract class SQLiteDAO {
    
    final Connection connection;
    
    SQLiteDAO() {
        connection = SQLiteConnection.getConnection();
    }
}
