package de.hawhh.gewiss.get.simulator.db.dao;

import de.hawhh.gewiss.get.core.model.HeatingType;
import de.hawhh.gewiss.get.core.model.PrimaryEnergyFactors;
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
        Map<HeatingType, PrimaryEnergyFactors> results = dao.findAll();

        assertNotNull(results);
        assertFalse(results.isEmpty());
    }

    @Test
    public void findBy() {
        PrimaryEnergyFactors pef = dao.findBy(HeatingType.CONDENSING_BOILER);
        assertNotNull(pef);
    }
}