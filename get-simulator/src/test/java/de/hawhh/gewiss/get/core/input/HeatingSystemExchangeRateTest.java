package de.hawhh.gewiss.get.core.input;

import de.hawhh.gewiss.get.core.input.HeatingSystemExchangeRate;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link HeatingSystemExchangeRate}.
 *
 * @author Thomas Preisler
 */
public class HeatingSystemExchangeRateTest {

    /**
     * Test of normalize method, of class HeatingSystemExchangeRate.
     */
    @Test
    public void testNormalize() {
        System.out.println("normalize");
        HeatingSystemExchangeRate instance = new HeatingSystemExchangeRate();

        // > 100%
        instance.setCondBoilerSolarHRRate(25d);
        instance.setCondBoilerSolarRate(25d);
        instance.setCondensingBoilerRate(25d);
        instance.setDistrictHeatHRRate(25d);
        instance.setDistrictHeatRate(25d);
        instance.setHeatPumpHRRate(25d);
        instance.setLowTempBoilerRate(25d);
        instance.setPelletsRate(25d);
        instance.setPelletsSolarHRRate(25d);
        instance.normalize();
        Assert.assertEquals(100d, instance.getCondBoilerSolarHRRate() + instance.getCondBoilerSolarRate() + instance.getCondensingBoilerRate()
                + instance.getDistrictHeatHRRate() + instance.getDistrictHeatRate() + instance.getHeatPumpHRRate() + instance.getLowTempBoilerRate() + instance.getPelletsRate() + instance.getPelletsSolarHRRate(), 0.01);

        // < 100%
        instance.setCondBoilerSolarHRRate(10d);
        instance.setCondBoilerSolarRate(10d);
        instance.setCondensingBoilerRate(10d);
        instance.setDistrictHeatHRRate(10d);
        instance.setDistrictHeatRate(10d);
        instance.setHeatPumpHRRate(10d);
        instance.setLowTempBoilerRate(10d);
        instance.setPelletsRate(10d);
        instance.setPelletsSolarHRRate(10d);
        instance.normalize();
        Assert.assertEquals(100d, instance.getCondBoilerSolarHRRate() + instance.getCondBoilerSolarRate() + instance.getCondensingBoilerRate()
                + instance.getDistrictHeatHRRate() + instance.getDistrictHeatRate() + instance.getHeatPumpHRRate() + instance.getLowTempBoilerRate() + instance.getPelletsRate() + instance.getPelletsSolarHRRate(), 0.01);

        // == 100%
        instance.setCondBoilerSolarHRRate(100.0 / 9.0);
        instance.setCondBoilerSolarRate(100.0 / 9.0);
        instance.setCondensingBoilerRate(100.0 / 9.0);
        instance.setDistrictHeatHRRate(100.0 / 9.0);
        instance.setDistrictHeatRate(100.0 / 9.0);
        instance.setHeatPumpHRRate(100.0 / 9.0);
        instance.setLowTempBoilerRate(100.0 / 9.0);
        instance.setPelletsRate(100.0 / 9.0);
        instance.setPelletsSolarHRRate(100.0 / 9.0);
        instance.normalize();
        Assert.assertEquals(100d, instance.getCondBoilerSolarHRRate() + instance.getCondBoilerSolarRate() + instance.getCondensingBoilerRate()
                + instance.getDistrictHeatHRRate() + instance.getDistrictHeatRate() + instance.getHeatPumpHRRate() + instance.getLowTempBoilerRate() + instance.getPelletsRate() + instance.getPelletsSolarHRRate(), 0.01);
    }
}
