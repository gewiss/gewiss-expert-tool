package de.hawhh.gewiss.get.simulator.baf;

import de.hawhh.gewiss.get.core.model.Building;
import de.hawhh.gewiss.get.simulator.db.dao.SQLiteBuildingDAO;
import de.hawhh.gewiss.get.simulator.db.dao.SQLiteConnection;
import org.junit.Assert;
import org.junit.Test;

/**
 * Text class for {@link SimpleBuildingAgeFactor}.
 * 
 * @author Thomas Preisler
 */
public class SimpleBuildingAgeFactorTest {
    
    /**
     * Test of calcFactor method, of class SimpleBuildingAgeFactor.
     */
    @Test
    public void testCalcFactor() {
        System.out.println("calcFactor");
        SimpleBuildingAgeFactor instance = new SimpleBuildingAgeFactor();
        
        SQLiteBuildingDAO buildingDAO = new SQLiteBuildingDAO();
        Building building;
        
        if (SQLiteConnection.DB_FILE.contains("anonymized")) {
            building = buildingDAO.findById("9");
        } else {
            building =  buildingDAO.findById("DEHHALKA100002dV");
        }
        
        Integer simYear = 2029;
        
        Assert.assertEquals(0d, instance.calcFactor(building, simYear), 0d);
        Assert.assertTrue(instance.calcFactor(building, 2039) < instance.calcFactor(building, 2049));
    }   
}