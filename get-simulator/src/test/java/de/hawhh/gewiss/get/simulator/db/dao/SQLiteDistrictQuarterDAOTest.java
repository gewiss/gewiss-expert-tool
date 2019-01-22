package de.hawhh.gewiss.get.simulator.db.dao;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

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
        Assert.assertEquals(104, result.size());
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
        Assert.assertEquals(14, result.size());
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
        Assert.assertEquals(7, result.size());
    }
}