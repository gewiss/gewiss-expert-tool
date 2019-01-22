package de.hawhh.gewiss.get.simulator.db.dao;

import de.hawhh.gewiss.get.core.model.CostsBuildingShell;
import de.hawhh.gewiss.get.core.model.RenovationLevel;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class CostsBuildingShellDAOTest {

    private CostsBuildingShellDAO dao;

    @Before
    public void setUp() {
        dao = new CostsBuildingShellDAO();
    }

    @Test
    public void findAll() {
        MultiKeyMap results = dao.findAll();

        assertNotNull(results);
        assertFalse(results.isEmpty());
    }

    @Test
    public void findBy() {
        CostsBuildingShell costs = dao.findBy("EFH_A", RenovationLevel.BASIC_RENOVATION);
        assertNotNull(costs);
    }
}