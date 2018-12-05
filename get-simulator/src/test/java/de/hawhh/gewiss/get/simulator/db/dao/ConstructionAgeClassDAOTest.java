package de.hawhh.gewiss.get.simulator.db.dao;

import de.hawhh.gewiss.get.simulator.db.dao.ConstructionAgeClassDAO;
import de.hawhh.gewiss.get.core.model.ConstructionAgeClass;
import java.util.Map;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Test class for {@link ConstructionAgeClassDAO}.
 *
 * @author Thomas Preisler
 */
public class ConstructionAgeClassDAOTest {

    public ConstructionAgeClassDAOTest() {
    }

    /**
     * Test of findByName method, of class ConstructionAgeClassDAO.
     */
    @Test
    public void testFindByName() {
        System.out.println("findByName");
        String className = "B";
        ConstructionAgeClassDAO instance = new ConstructionAgeClassDAO();
        ConstructionAgeClass expResult = new ConstructionAgeClass();
        expResult.setAgeClass("B");
        expResult.setFromYear(1860);
        expResult.setToYear(1918);
        ConstructionAgeClass result = instance.findByName(className);

        assertEquals(expResult, result);
    }

    /**
     * Test of findAll method, of class ConstructionAgeClassDAO.
     */
    @Test
    public void testFindAll() {
        System.out.println("findAll");
        ConstructionAgeClassDAO instance = new ConstructionAgeClassDAO();
        Map<String, ConstructionAgeClass> results = instance.findAll();

        Assert.assertNotNull(results);
        Assert.assertEquals(12, results.size());
    }
}
