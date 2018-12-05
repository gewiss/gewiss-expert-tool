package de.hawhh.gewiss.get.simulator.db.dao;

import de.hawhh.gewiss.get.simulator.db.dao.RenovationCostDAO;
import de.hawhh.gewiss.get.core.model.RenovationCost;
import de.hawhh.gewiss.get.core.model.RenovationLevel;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link RenovationCostDAO}.
 * 
 * @author Thomas Preisler
 */
public class RenovationCostDAOTest {
    
    public RenovationCostDAOTest() {
    }

    /**
     * Test of findByType method, of class RenovationCostDAO.
     */
    @Test
    public void testFindByType() {
        System.out.println("findByType");
        String type = "EFH_A";
        
        RenovationCostDAO instance = new RenovationCostDAO();
        RenovationCost result = instance.findByType(type);
        
        Assert.assertEquals(type, result.getBuildingType());
        Assert.assertEquals(321.27, result.getRegressionCosts().get(RenovationLevel.BASIC_RENOVATION), 0.05);
        Assert.assertEquals(421.73, result.getRegressionCosts().get(RenovationLevel.GOOD_RENOVATION), 0.05);
    }
    
    /**
     * Test of findAll method, of class RenovationCostDAO.
     */
    @Test
    public void testFindAll() {
        System.out.println("findAll");
        
        RenovationCostDAO instance = new RenovationCostDAO();
        Map<String, RenovationCost> results = instance.findAll();
        
        Assert.assertNotNull(results);
        Assert.assertEquals(28, results.size());
    }
}