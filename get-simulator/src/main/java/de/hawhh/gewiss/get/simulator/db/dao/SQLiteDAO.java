package de.hawhh.gewiss.get.simulator.db.dao;

import java.sql.Connection;

/**
  Abstract super class for DAOs based on the SQLite DB connection.
 * 
 * @author Thomas Preisler
 */
public abstract class SQLiteDAO {
    
    protected final Connection connection;
    
    public SQLiteDAO() {
        connection = SQLiteConnection.getConnection();
    }
}
