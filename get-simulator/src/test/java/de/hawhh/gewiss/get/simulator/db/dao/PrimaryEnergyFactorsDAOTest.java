package de.hawhh.gewiss.get.simulator.db.dao;

import de.hawhh.gewiss.get.core.model.HeatingType;
import de.hawhh.gewiss.get.core.model.PrimaryEnergyFactor;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class PrimaryEnergyFactorsDAOTest {

    private PrimaryEnergyFactorsDAO dao;

    @Before
    public void setUp() {
        dao = new PrimaryEnergyFactorsDAO();
    }

    @Test
    public void findAll() {
        Map<HeatingType, PrimaryEnergyFactor> results = dao.findAll();

        assertNotNull(results);
        assertFalse(results.isEmpty());
    }

    @Test
    public void findBy() {
        PrimaryEnergyFactor pef = dao.findBy(HeatingType.CONDENSING_BOILER);
        assertNotNull(pef);
    }
}