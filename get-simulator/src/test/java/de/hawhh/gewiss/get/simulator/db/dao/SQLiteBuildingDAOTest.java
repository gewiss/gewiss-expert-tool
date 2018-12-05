package de.hawhh.gewiss.get.simulator.db.dao;

import de.hawhh.gewiss.get.core.model.Building;
import java.util.List;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link SQLiteBuildingDAO}.
 *
 * @author Thomas Preisler
 */
public class SQLiteBuildingDAOTest {

    private SQLiteBuildingDAO dao;

    @Before
    public void setUp() {
        dao = new SQLiteBuildingDAO();
    }

    /**
     * Test of findAll method, of class SQLiteBuildingDAO.
     */
    @Test
    public void testFindAll() {
        System.out.println("findAll");

        List<Building> result = dao.findAll();
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void testFindById() throws Exception {
        String buildingID;
        if (SQLiteConnection.DB_FILE.contains("anonymized")) {
            buildingID = "1";
        } else {
            buildingID = "DEHHALKAz0000Opt";
        }

        Building building = dao.findById(buildingID);

        assertNotNull(building);
        assertNotNull(building.getConstructionAgeClass());
        assertNotNull(building.getDistrictHeatingOutletDistance());
    }

    @Test
    public void testGetResidentialBuildingTypes() {
        List<String> types = dao.getResidentialBuildingTypes();
        assertNotNull(types);

        if (SQLiteConnection.DB_FILE.contains("anonymized")) {
            assertTrue(types.size() == 30);
        } else {
            assertTrue(types.size() == 43);
        }
    }

    @Test
    public void testGetNonResidentialBuildingTypes() {
        List<String> types = dao.getNonResidentialBuildingTypes();
        assertNotNull(types);

        if (SQLiteConnection.DB_FILE.contains("anonymized")) {
            assertTrue(types.size() == 11);
        } else {
            assertTrue(types.size() == 19);
        }
    }
}
