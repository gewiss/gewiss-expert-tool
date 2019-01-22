package de.hawhh.gewiss.get.simulator.db.dao;

import de.hawhh.gewiss.get.core.model.HeatDemandLoadGeneration;
import de.hawhh.gewiss.get.core.model.RenovationLevel;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class HeatDemandLoadGenerationDAOTest {

    private HeatDemandLoadGenerationDAO dao;

    @Before
    public void setUp() {
        dao = new HeatDemandLoadGenerationDAO();
    }

    @Test
    public void findAll() {
        MultiKeyMap results = dao.findAll();

        assertNotNull(results);
        assertFalse(results.isEmpty());
    }

    @Test
    public void testfindBy() {
        String buildingType = "EFH_A";
        RenovationLevel renovationLevel = RenovationLevel.BASIC_RENOVATION;

        HeatDemandLoadGeneration o = dao.findBy(buildingType, renovationLevel);

        assertNotNull(o);
    }
}