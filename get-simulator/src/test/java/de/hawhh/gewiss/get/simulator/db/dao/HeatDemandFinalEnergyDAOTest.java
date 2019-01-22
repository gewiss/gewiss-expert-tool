package de.hawhh.gewiss.get.simulator.db.dao;

import de.hawhh.gewiss.get.core.model.HeatDemandFinalEnergy;
import de.hawhh.gewiss.get.core.model.HeatingType;
import de.hawhh.gewiss.get.core.model.RenovationLevel;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class HeatDemandFinalEnergyDAOTest {

    private HeatDemandFinalEnergyDAO dao;

    @Before
    public void setUp() {
        dao = new HeatDemandFinalEnergyDAO();
    }

    @Test
    public void findAll() {
        MultiKeyMap map = dao.findAll();

        assertNotNull(map);
        assertFalse(map.isEmpty());
    }

    @Test
    public void findBy() {
        HeatDemandFinalEnergy o = dao.findBy("EFH_A", RenovationLevel.NO_RENOVATION, HeatingType.LOW_TEMPERATURE_BOILER);
        assertNotNull(o);
    }
}