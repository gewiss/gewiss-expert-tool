package de.hawhh.gewiss.get.core.model;

import lombok.Data;

/**
 * Model for construction age classes of buildings.
 *
 * @author Thomas Preisler
 */
@Data
public class ConstructionAgeClass {

    private String ageClass;
    private Integer fromYear;
    private Integer toYear;

    /**
     * @return the mean value of the from and to year, or the to year if the from year is null of the from year if the to year is null.
     */
    public Integer getMean() {
        if (fromYear == null) {
            return toYear;
        } else if (toYear == null) {
            return fromYear;
        } else {
            return (fromYear + toYear) / 2;
        }
    }
}