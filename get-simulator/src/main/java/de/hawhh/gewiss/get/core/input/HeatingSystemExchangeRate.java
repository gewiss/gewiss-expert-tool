package de.hawhh.gewiss.get.core.input;

import de.hawhh.gewiss.get.core.model.HeatingType;
import de.hawhh.gewiss.get.core.model.RenovationType;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Data model class for exchange rates from a given/old heating system to potential new ones.
 *
 * @author Thomas Preisler, Antony Sotirov
 */
public class HeatingSystemExchangeRate {
    
    private final static Logger LOGGER = Logger.getLogger(HeatingSystemExchangeRate.class.getName());

    private final SimpleStringProperty renType;
    private final SimpleStringProperty oldType;
    private final SimpleDoubleProperty lowTempBoilerRate;
    private final SimpleDoubleProperty districtHeatRate;
    private final SimpleDoubleProperty condensingBoilerRate;
    private final SimpleDoubleProperty condBoilerSolarRate;
    private final SimpleDoubleProperty pelletsRate;
    private final SimpleDoubleProperty heatPumpHRRate;
    private final SimpleDoubleProperty pelletsSolarHRRate;
    private final SimpleDoubleProperty districtHeatHRRate;
    private final SimpleDoubleProperty condBoilerSolarHRRate;

    public HeatingSystemExchangeRate() {
        this(RenovationType.RES_ENEV, HeatingType.DISTRICT_HEAT, 0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d);
    }

    public HeatingSystemExchangeRate(RenovationType renType, HeatingType oldType, Double lowTempBoilerRate, Double districtHeatRate, Double condensingBoilerRate, Double condBoilerSolarRate,
            Double pelletsRate, Double heatPumpHRRate, Double pelletsSolarHRRate, Double districtHeatHRRate, Double condBoilerSolarHRRate) {
        this.renType = new SimpleStringProperty(renType.toString());
        this.oldType = new SimpleStringProperty(oldType.toString());
        this.lowTempBoilerRate = new SimpleDoubleProperty(lowTempBoilerRate);
        this.districtHeatRate = new SimpleDoubleProperty(districtHeatRate);
        this.condensingBoilerRate = new SimpleDoubleProperty(condensingBoilerRate);
        this.condBoilerSolarRate = new SimpleDoubleProperty(condBoilerSolarRate);
        this.pelletsRate = new SimpleDoubleProperty(pelletsRate);
        this.heatPumpHRRate = new SimpleDoubleProperty(heatPumpHRRate);
        this.pelletsSolarHRRate = new SimpleDoubleProperty(pelletsSolarHRRate);
        this.districtHeatHRRate = new SimpleDoubleProperty(districtHeatHRRate);
        this.condBoilerSolarHRRate = new SimpleDoubleProperty(condBoilerSolarHRRate);
    }

    // Renovation Type Getters and Setters
    public RenovationType getRenType() {
        return RenovationType.valueOf(renType.get());
    }

    public void setRenType(RenovationType renType) {
        this.renType.set(renType.toString());
    }

    // Heating Type Getters and Setters
    public HeatingType getOldType() {
        return HeatingType.valueOf(oldType.get());
    }

    public void setOldType(HeatingType oldType) {
        this.oldType.set(oldType.toString());
    }

    // Rates Getters and Setters
    public Double getLowTempBoilerRate() {
        return lowTempBoilerRate.get();
    }

    public void setLowTempBoilerRate(Double rate) {
        lowTempBoilerRate.set(rate);
    }

    public Double getDistrictHeatRate() {
        return districtHeatRate.get();
    }

    public void setDistrictHeatRate(Double rate) {
        districtHeatRate.set(rate);
    }

    public Double getCondensingBoilerRate() {
        return condensingBoilerRate.get();
    }

    public void setCondensingBoilerRate(Double rate) {
        condensingBoilerRate.set(rate);
    }

    public Double getCondBoilerSolarRate() {
        return condBoilerSolarRate.get();
    }

    public void setCondBoilerSolarRate(Double rate) {
        condBoilerSolarRate.set(rate);
    }

    public Double getPelletsRate() {
        return pelletsRate.get();
    }

    public void setPelletsRate(Double rate) {
        pelletsRate.set(rate);
    }

    public Double getHeatPumpHRRate() {
        return heatPumpHRRate.get();
    }

    public void setHeatPumpHRRate(Double rate) {
        heatPumpHRRate.set(rate);
    }

    public Double getPelletsSolarHRRate() {
        return pelletsSolarHRRate.get();
    }

    public void setPelletsSolarHRRate(Double rate) {
        pelletsSolarHRRate.set(rate);
    }

    public Double getDistrictHeatHRRate() {
        return districtHeatHRRate.get();
    }

    public void setDistrictHeatHRRate(Double rate) {
        districtHeatHRRate.set(rate);
    }

    public Double getCondBoilerSolarHRRate() {
        return condBoilerSolarHRRate.get();
    }

    public void setCondBoilerSolarHRRate(Double rate) {
        condBoilerSolarHRRate.set(rate);
    }

    /**
     * Normalize the exchange rate, so that they give 100% in sum.
     */
    public void normalize() {
        Double sum = getCondBoilerSolarHRRate() + getCondBoilerSolarRate() + getCondensingBoilerRate() + getDistrictHeatHRRate() + getDistrictHeatRate()
                + getHeatPumpHRRate() + getLowTempBoilerRate() + getPelletsRate() + getPelletsSolarHRRate();
        LOGGER.log(Level.INFO, "Normalizing HeatingSystemExchangeRate for {0} from {1}% to 100%", new Object[]{getOldType(), sum});

        setCondBoilerSolarHRRate(getCondBoilerSolarHRRate() * 100d / sum);
        setCondBoilerSolarRate(getCondBoilerSolarRate() * 100d / sum);
        setCondensingBoilerRate(getCondensingBoilerRate() * 100d / sum);
        setDistrictHeatHRRate(getDistrictHeatHRRate() * 100d / sum);
        setDistrictHeatRate(getDistrictHeatRate() * 100d / sum);
        setHeatPumpHRRate(getHeatPumpHRRate() * 100d / sum);
        setLowTempBoilerRate(getLowTempBoilerRate() * 100d / sum);
        setPelletsRate(getPelletsRate() * 100d / sum);
        setPelletsSolarHRRate(getPelletsSolarHRRate() * 100d / sum);
    }
}
