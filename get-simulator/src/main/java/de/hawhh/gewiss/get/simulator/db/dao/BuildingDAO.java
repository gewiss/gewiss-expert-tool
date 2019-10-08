package de.hawhh.gewiss.get.simulator.db.dao;

import de.hawhh.gewiss.get.core.model.Building;

import java.util.List;

/**
 * DAO interface for the {@link Building} class.
 *
 * @author Thomas Preisler, Antony Sotirov
 */
public interface BuildingDAO {

    /**
     * Finds all the {@link Building}s located at the data source.
     *
     * @return a {@link List} of {@link Building}s
     */
    List<Building> findAll();

    /**
     * Find and return the {@link Building} with the given Id.
     *
     * @param buildingId the given building id
     * @return the found parcel or null if no parcel matches the given id
     */
    Building findById(String buildingId);
    
    /**
     * Returns a List of alle the *different* residential building types.
     * @return 
     */
    List<String> getResidentialBuildingTypes();
    
    /**
     * Returns a List of all the *different* types of non-residential buildings.
     * @return 
     */
    List<String> getNonResidentialBuildingTypes();

    /**
     * Returns a list of all the *different* building owners.
     * @return
     */
    // @TODO: add tests
    List<String> getOwnershipTypes();
}
