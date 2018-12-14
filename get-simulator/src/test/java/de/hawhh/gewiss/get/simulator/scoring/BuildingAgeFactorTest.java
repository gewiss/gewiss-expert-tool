package de.hawhh.gewiss.get.simulator.scoring;

import de.hawhh.gewiss.get.core.model.Building;
import de.hawhh.gewiss.get.simulator.db.dao.SQLiteBuildingDAO;
import de.hawhh.gewiss.get.simulator.db.dao.SQLiteConnection;
import org.junit.Assert;
import org.junit.Test;

/**
 * Text class for {@link BuildingAgeFactor}.
 * 
 * @author Thomas Preisler
 */
public class BuildingAgeFactorTest {
    
    /**
     * Test of calcFactor method, of class BuildingAgeFactor.
     */
    @Test
    public void testCalcFactor() {
        System.out.println("calcFactor");
        BuildingAgeFactor instance = new BuildingAgeFactor();
        
        SQLiteBuildingDAO buildingDAO = new SQLiteBuildingDAO();
        Building building;
        
        if (SQLiteConnection.DB_FILE.contains("anonymized")) {
            building = buildingDAO.findById("9");
        } else {
            building =  buildingDAO.findById("DEHHALKA100002dV");
        }
        
        Integer simYear = 2029;
        
        Assert.assertEquals(0d, instance.calcBaseScore(building, simYear), 0d);
        Assert.assertTrue(instance.calcBaseScore(building, 2039) < instance.calcBaseScore(building, 2049));
    }   
}