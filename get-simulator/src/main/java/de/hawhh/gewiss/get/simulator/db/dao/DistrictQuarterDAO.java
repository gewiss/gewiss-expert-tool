package de.hawhh.gewiss.get.simulator.db.dao;

import java.util.List;

/**
 * DAO interface for the mapping of districts and quarters in the city of Hamburg.
 *
 * @author Arjun Jamil, Thomas Preisler
 */
public interface DistrictQuarterDAO {

  
    /**
     * Return a List of all district districts in the city.
     * 
     * @return the List of districts as String or <code>null</code> in case of an exception
     */
    List<String> getDistricts();

    /**
     * Return a List of all distinct quarters in the city.
     *
     * @return the List of quarters as String or <code>null</code> in case of an exception
     */
    List<String> getQuarters();

    /**
     * Return a List of all distinct quarters for the given district.
     *
     * @param district the name of the given district
     * @return the List of quarters as String or <code>null</code> in case of an exception
     */
    List<String> getQuarters(String district);
    
}
