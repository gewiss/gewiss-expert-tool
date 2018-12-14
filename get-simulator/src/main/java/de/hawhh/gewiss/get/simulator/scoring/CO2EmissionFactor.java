package de.hawhh.gewiss.get.simulator.scoring;

import de.hawhh.gewiss.get.core.model.Building;
import de.hawhh.gewiss.get.core.model.EnergySource;
import de.hawhh.gewiss.get.core.model.HeatingType;
import de.hawhh.gewiss.get.core.model.RenovationLevel;
import de.hawhh.gewiss.get.simulator.db.dao.EnergySourceDAO;
import de.hawhh.gewiss.get.simulator.db.dao.SQLiteEnergySourceDAO;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Thomas Preisler
 */
public class CO2EmissionFactor implements ScoringMethod {

    private final EnergySourceDAO energySourceDAO;

    Map<EnergySource.Type, EnergySource> energySources;
    Map<HeatingType, EnergySource> heatingToFuelMap;

    public CO2EmissionFactor() {
        this.energySourceDAO = new SQLiteEnergySourceDAO();

        // Fetch the energy sources from the DB and match them to heating types
        energySources = fetchEnergySources();
        heatingToFuelMap = mapEnergySourcesToHeatingTypes(energySources);
    }

    @Override
    public Double calcBaseScore(Building building, Integer simYear) {
        if (!building.getRenovationLevel().equals(RenovationLevel.GOOD_RENOVATION)) {
            return building.calcCo2Emission(building.calcHeatDemand(), heatingToFuelMap);
        } else {
            return -1d;
        }
    }

    /**
     * Fetch all the energy sources from the database and return them as a map.
     *
     * @return List of buildings
     */
    private Map<EnergySource.Type, EnergySource> fetchEnergySources() {
        return energySourceDAO.findAll();
    }

    /**
     * Map the EnergySources to the corresponding HeatingTypes.
     *
     * @param sources the Map of EnergySources.
     *
     * @return map with heating types as key and energy source as value.
     */
    private Map<HeatingType, EnergySource> mapEnergySourcesToHeatingTypes(Map<EnergySource.Type, EnergySource> sources) {
        Map<HeatingType, EnergySource> heatingTypeToSourceMap = new HashMap<>();

        for (HeatingType type : HeatingType.values()) {

            switch (type) {
                case LOW_TEMPERATURE_BOILER:
                case CONDENSING_BOILER:
                case CONDENSING_BOILER_SOLAR:
                case CONDENSING_BOILER_SOLAR_HEAT_RECOVERY:
                    heatingTypeToSourceMap.put(type, sources.get(EnergySource.Type.NATURAL_GAS));
                    break;

                case DISTRICT_HEAT:
                case DISTRICT_HEAT_HEAT_RECOVERY:
                    heatingTypeToSourceMap.put(type, sources.get(EnergySource.Type.DISTRICT_HEAT));
                    break;

                case PELLETS:
                case PELLETS_SOLAR_HEAT_RECOVERY:
                    heatingTypeToSourceMap.put(type, sources.get(EnergySource.Type.PELLETS));
                    break;

                case HEAT_PUMP_HEAT_RECOVERY:
                    heatingTypeToSourceMap.put(type, sources.get(EnergySource.Type.ELECTRICITY));
                    break;

                default:
                    break;
            }
        }

        return heatingTypeToSourceMap;
    }
}
