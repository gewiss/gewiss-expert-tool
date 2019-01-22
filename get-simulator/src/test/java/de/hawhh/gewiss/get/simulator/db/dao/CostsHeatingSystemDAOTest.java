package de.hawhh.gewiss.get.simulator.db.dao;

import de.hawhh.gewiss.get.core.model.CostsHeatingSystem;
import de.hawhh.gewiss.get.core.model.HeatingType;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class CostsHeatingSystemDAOTest {

    private CostsHeatingSystemDAO dao;

    @Before
    public void setUp() {
        dao = new CostsHeatingSystemDAO();
    }

    @Test
    public void findAll() {
        Map<HeatingType, Map<Integer, Double>> map = dao.findAll();

        assertNotNull(map);
        assertFalse(map.isEmpty());
    }

    @Test
    public void findBy() {
        CostsHeatingSystem costs = dao.findBy(15, HeatingType.DISTRICT_HEAT);
        assertNotNull(costs);
    }
}