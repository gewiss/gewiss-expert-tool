package de.hawhh.gewiss.get.simulator.db.dao;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class implementing the CityquarterDAO interface for a SQLite database.
 *
 * @author Arjun Jamil, Thomas Preisler
 */
public class SQLiteDistrictQuarterDAO extends SQLiteDAO implements DistrictQuarterDAO {

    private final static Logger LOGGER = Logger.getLogger(SQLiteDistrictQuarterDAO.class.getName());

    public SQLiteDistrictQuarterDAO() {
        super();

    }

    /**
     * Return a List of all distinct quarters in the city.
     *
     * @return the List of quarters as String or <code>null</code> in case of an exception
     */
    @Override
    public List<String> getQuarters() {
        try {
            LOGGER.info("Getting quarters from SQLite DB");

            List<String> quarters = new ArrayList<>();

            String sql = "select quarter from districts_and_quarters order by quarter";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String quarter = rs.getString("quarter");
                quarters.add(quarter);
            }

            return quarters;
        } catch (SQLException ex) {
            Logger.getLogger(SQLiteDistrictQuarterDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * Return a List of all distinct quarters for the given district.
     *
     * @param district the name of the given district
     * @return the List of quarters as String or <code>null</code> in case of an exception
     */
    @Override
    public List<String> getQuarters(String district) {
        try {
            LOGGER.log(Level.INFO, "Getting quarters from SQLite DB for given district: {0}", district);

            List<String> quarters = new ArrayList<>();
            String sql = "select quarter from districts_and_quarters where district = ? order by quarter";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, district);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String quarter = rs.getString("quarter");
                quarters.add(quarter);
            }

            return quarters;
        } catch (SQLException ex) {
            Logger.getLogger(SQLiteDistrictQuarterDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * Return a List of all distinct districts in the city.
     *
     * @return the List of districts as String or <code>null</code> in case of an exception
     */
    @Override
    public List<String> getDistricts() {
        try {
            LOGGER.info("Getting districts from SQLite DB");

            List<String> districts = new ArrayList<>();

            String sql = "select distinct(district) from districts_and_quarters order by district";
            PreparedStatement stmt
                    = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String district = rs.getString("district");
                districts.add(district);
            }

            return districts;
        } catch (SQLException ex) {
            Logger.getLogger(SQLiteDistrictQuarterDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
}