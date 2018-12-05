package de.hawhh.gewiss.get.simulator.db.dao;

import de.hawhh.gewiss.get.simulator.db.dao.HeatDemandDAO;
import de.hawhh.gewiss.get.core.model.HeatDemand;
import de.hawhh.gewiss.get.core.model.RenovationLevel;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link HeatDemandDAO}
 * 
 * @author Thomas Preisler
 */
public class HeatDemandDAOTest {
    
    public HeatDemandDAOTest() {
    }

    /**
     * Test of findByType method, of class HeatDemandDAO.
     */
    @Test
    public void testFindByType() {
        System.out.println("findByType");
        String type = "EFH_I";
        HeatDemandDAO instance = new HeatDemandDAO();
        HeatDemand result = instance.findByType(type);
        
        Assert.assertEquals(type, result.getBuildingType());
        Assert.assertEquals(9.9, result.getWarmWaterDemand(RenovationLevel.NO_RENOVATION), 0.05);
        Assert.assertEquals(10.8, result.getWarmWaterDemand(RenovationLevel.BASIC_RENOVATION), 0.05);
        Assert.assertEquals(11.6, result.getWarmWaterDemand(RenovationLevel.GOOD_RENOVATION), 0.05);
        Assert.assertEquals(121.5, result.getHeatingDemand(RenovationLevel.NO_RENOVATION), 0.05);
        Assert.assertEquals(107.4, result.getHeatingDemand(RenovationLevel.BASIC_RENOVATION), 0.05);
        Assert.assertEquals(43.3, result.getHeatingDemand(RenovationLevel.GOOD_RENOVATION), 0.05);
    }
    
    /**
     * Test of findAll method, of class HeatDemandDAO.
     */
    @Test
    public void testFindAll() {
        System.out.println("findAll");
        
        HeatDemandDAO instance = new HeatDemandDAO();
        Map<String, HeatDemand> results = instance.findAll();
        
        Assert.assertNotNull(results);
        Assert.assertEquals(71, results.size());
    }
}