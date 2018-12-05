package de.hawhh.gewiss.get.core.input;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Range;
import de.hawhh.gewiss.get.core.model.Building;
import de.hawhh.gewiss.get.core.model.HeatingType;
import de.hawhh.gewiss.get.core.model.RenovationLevel;
import java.util.List;

/**
 * Data model for the Modifier approach. A modifier has a year range where it is active, an impact value and multiple conditions that have to be fulfilled in order for a
 * {@link Building} to be selected by the modifier.
 *
 * @author Thomas Preisler
 */
public class Modifier {

    private List<String> targetQuarters;
    private List<String> targetBuildingsTypes;
    private List<RenovationLevel> targetRenovationLevels;
    private List<HeatingType> targetHeatingSystems;
    private Double maxDistrictHeatingDistance;
    private Range<Integer> yearOfConstructionRange;

    private Double impactFactor;
    private Range<Integer> duration;
    private String name;

    public Modifier() {
        super();
    }

    /**
     * Construct a modifier by specified it's period of validity and the actual impact factor.
     *
     * @param name the name of the modifier
     * @param firstYear the first year of validity (inclusively)
     * @param lastYear the last year of validity (inclusively)
     * @param impactFactor
     */
    public Modifier(String name, Integer firstYear, Integer lastYear, Double impactFactor) {
        this.name = name;
        this.duration = Range.closed(firstYear, lastYear);
        this.impactFactor = impactFactor;
    }

    /**
     * For the given {@link Building} check if all conditions are fullfilled.
     *
     * @param building
     * @return true if all conditions are fullfilled for the building otherwise false
     */
    public boolean checkConditions(Building building) {
        if (targetQuarters != null) {
            if (!targetQuarters.contains(building.getQuarter())) {
                return false;
            }
        }

        if (targetBuildingsTypes != null) {
            if (!(targetBuildingsTypes.contains(building.getResidentialType()) || targetBuildingsTypes.contains(building.getNonResidentialType()))) {
                return false;
            }
        }

        if (targetRenovationLevels != null) {
            if (!targetRenovationLevels.contains(building.getRenovationLevel())) {
                return false;
            }
        }

        if (targetHeatingSystems != null) {
            if (!targetHeatingSystems.contains(building.getHeatingType())) {
                return false;
            }
        }

        if (maxDistrictHeatingDistance != null) {
            if (building.getDistrictHeatingOutletDistance() > maxDistrictHeatingDistance) {
                return false;
            }
        }

        if (yearOfConstructionRange != null) {
            if (!yearOfConstructionRange.contains(building.getYearOfConstruction())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if the modifier is active for the given year.
     *
     * @param year
     * @return
     */
    public boolean isActive(Integer year) {
        return duration.contains(year);
    }

    public List<String> getTargetQuarters() {
        return targetQuarters;
    }

    public void setTargetQuarters(List<String> targetQuarters) {
        this.targetQuarters = targetQuarters;
    }

    public List<String> getTargetBuildingsTypes() {
        return targetBuildingsTypes;
    }

    public void setTargetBuildingsTypes(List<String> targetBuildingsTypes) {
        this.targetBuildingsTypes = targetBuildingsTypes;
    }

    public List<RenovationLevel> getTargetRenovationLevels() {
        return targetRenovationLevels;
    }

    public void setTargetRenovationLevels(List<RenovationLevel> targetRenovationLevels) {
        this.targetRenovationLevels = targetRenovationLevels;
    }

    public List<HeatingType> getTargetHeatingSystems() {
        return targetHeatingSystems;
    }

    public void setTargetHeatingSystems(List<HeatingType> targetHeatingSystems) {
        this.targetHeatingSystems = targetHeatingSystems;
    }

    public Double getMaxDistrictHeatingDistance() {
        return maxDistrictHeatingDistance;
    }

    public void setMaxDistrictHeatingDistance(Double maxDistrictHeatingDistance) {
        this.maxDistrictHeatingDistance = maxDistrictHeatingDistance;
    }

    public Range<Integer> getYearOfConstructionRange() {
        return yearOfConstructionRange;
    }

    public void setYearOfConstructionRange(Range<Integer> yearOfConstructionRange) {
        this.yearOfConstructionRange = yearOfConstructionRange;
    }

    public Double getImpactFactor() {
        return impactFactor;
    }

    public void setImpactFactor(Double impactFactor) {
        this.impactFactor = impactFactor;
    }

    public Range<Integer> getDuration() {
        return duration;
    }

    public void setDuration(Range<Integer> duration) {
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public boolean isValid() {
        if (impactFactor < 0) {
            return false;
        }
        
        if (maxDistrictHeatingDistance != null) {
            if (maxDistrictHeatingDistance < 0) {
                return false;
            }
        }
        
        return true;
    }
}
