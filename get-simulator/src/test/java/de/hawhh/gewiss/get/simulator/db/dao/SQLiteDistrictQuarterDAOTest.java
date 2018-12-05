package de.hawhh.gewiss.get.simulator.db.dao;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link SQLiteDistrictQuarterDAO}.
 * 
 * @author Thomas Preisler
 */
public class SQLiteDistrictQuarterDAOTest {

    /**
     * Test of getQuarters method, of class SQLiteDistrictQuarterDAO.
     */
    @Test
    public void testGetQuarters_0args() {
        System.out.println("getQuarters");
        SQLiteDistrictQuarterDAO instance = new SQLiteDistrictQuarterDAO();
        List<String> result = instance.getQuarters();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.size() == 104);
    }

    /**
     * Test of getQuarters method, of class SQLiteDistrictQuarterDAO.
     */
    @Test
    public void testGetQuarters_String() {
        System.out.println("getQuarters");
        String district = "Bergedorf";
        SQLiteDistrictQuarterDAO instance = new SQLiteDistrictQuarterDAO();
        List<String> result = instance.getQuarters(district);
        
        Assert.assertNotNull(result);
        Assert.assertTrue(result.size() == 14);
    }

    /**
     * Test of getDistricts method, of class SQLiteDistrictQuarterDAO.
     */
    @Test
    public void testGetDistricts() {
        System.out.println("getDistricts");
        SQLiteDistrictQuarterDAO instance = new SQLiteDistrictQuarterDAO();
        List<String> result = instance.getDistricts();
        
        Assert.assertNotNull(result);
        Assert.assertTrue(result.size() == 7);
    }
}