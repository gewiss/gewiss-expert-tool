package de.hawhh.gewiss.get.simulator.db.dao;

import de.hawhh.gewiss.get.core.model.EnergySource;
import de.hawhh.gewiss.get.core.model.EnergySource.Type;
import java.util.Map;

/**
 * DAO interface for the {@link EnergySource} class.
 *
 * @author Nils Weiss
 */
public interface EnergySourceDAO {

    /**
     * Finds all the {@link EnergySource}s located at the data source.
     *
     * @return a {@link Map} of {@link EnergySource}s
     */
    public Map<EnergySource.Type, EnergySource> findAll();
    
    /**
     * Find and return the {@link EnergySource} with the given Id.
     *
     * @param sourceType the given heating source type
     * @return the found energy source or null if no source matches the given type
     */
    public EnergySource findByType(Type sourceType);   
}