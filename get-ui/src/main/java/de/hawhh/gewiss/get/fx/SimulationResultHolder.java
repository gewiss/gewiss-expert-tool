package de.hawhh.gewiss.get.fx;

import de.hawhh.gewiss.get.core.output.SimulationResult;

/**
 * Singleton class for storing the simulation result application wide.
 *
 * @author Thomas Preisler
 */
public class SimulationResultHolder {

    private static SimulationResultHolder instance = null;
    private SimulationResult result = null;

    private SimulationResultHolder() {
    }

    /**
     * Get the instance of the SimulationResultHolder
     *
     * @return
     */
    public static SimulationResultHolder getInstance() {
        if (instance == null) {
            instance = new SimulationResultHolder();
        }

        return instance;
    }

    /**
     * Set the simulation result
     *
     * @param result
     */
    public void setResult(SimulationResult result) {
        this.result = result;
    }

    /**
     * Get the simulation result
     *
     * @return
     */
    public SimulationResult getResult() {
        return result;
    }
}
