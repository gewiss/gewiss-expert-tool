package de.hawhh.gewiss.get.simulator.db.dao;

import de.hawhh.gewiss.get.core.model.EnergySource;
import de.hawhh.gewiss.get.core.model.EnergySource.Type;
import java.util.Map;
import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link SQLiteEnergySourceDAO}.
 * 
 * @author Nils Weiss
 */
public class SQLiteEnergySourceDAOTest {
    
    SQLiteEnergySourceDAO dao;
    
    @Before
    public void setUp() {
        dao = new SQLiteEnergySourceDAO();
    }

    /**
     * Test of findByType method, of class SQLiteEnergySourceDAO.
     */
    @Test
    public void testFindByType() {
        System.out.println("findByType");
        EnergySource.Type type =Type.HEATING_OIL;
        
        EnergySource result = dao.findByType(type);
        assertEquals(type, result.getType());
        assertNotNull(result.getPrimaryEnergyFactor());
    }

    /**
     * Test of findAll method, of class SQLiteEnergySourceDAO.
     */
    @Test
    public void testFindAll() {
        System.out.println("findAll");
        
        Map<Type, EnergySource> results = dao.findAll();
        
        Assert.assertNotNull(results);
        Assert.assertEquals(7, results.size());
    }
    
}
