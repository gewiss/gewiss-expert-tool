package de.hawhh.gewiss.get.fx.controller;

import com.sun.management.OperatingSystemMXBean;
import javafx.application.Platform;
import javafx.collections.ModifiableObservableListBase;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

import java.lang.management.ManagementFactory;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FXML Controller for the Progress.fxml
 *
 * @author Thomas Preisler
 */
public class ProgressController {

    private final static Logger LOGGER = Logger.getLogger(ProgressController.class.getName());
    private final static int NO_MEASUREMENTS = 20;

    private MainController mainController;
    private String initTitle;

    @FXML
    private ProgressBar progressBar;
    @FXML
    private TextArea progressLog;
    @FXML
    private Text title;
    @FXML
    private AreaChart<Number, Number> cpuUsageChart;
    @FXML
    private AreaChart<Number, Number> memoryUsageChart;

    private ObservableList<XYChart.Data<Number, Number>> systemCpuData;
    private ObservableList<XYChart.Data<Number, Number>> processCpuData;

    private ObservableList<XYChart.Data<Number, Number>> memorySizeData;
    private ObservableList<XYChart.Data<Number, Number>> memoryUsedData;

    private NumberAxis cpuXAxis;

    private NumberAxis memoryXAxis;

    /**
     * Custom initialization of this controller.
     *
     * @param mc Reference to the main controller
     */
    public void init(MainController mc) {
        mainController = mc;

        progressBar.setProgress(0d);
        this.initTitle = title.getText();

        systemCpuData = new ObservableLimitedList(NO_MEASUREMENTS);
        processCpuData = new ObservableLimitedList(NO_MEASUREMENTS);
        memorySizeData = new ObservableLimitedList(NO_MEASUREMENTS);
        memoryUsedData = new ObservableLimitedList(NO_MEASUREMENTS);

        cpuUsageChart.getData().add(new XYChart.Series<>("System CPU Usage", systemCpuData));
        cpuUsageChart.getData().add(new XYChart.Series<>("GET CPU Usage", processCpuData));
        memoryUsageChart.getData().add(new XYChart.Series<>("Total memory size (GB)", memorySizeData));
        memoryUsageChart.getData().add(new XYChart.Series<>("Used memory (GB)", memoryUsedData));

        cpuXAxis = (NumberAxis) cpuUsageChart.getXAxis();
        cpuXAxis.setLowerBound(0);
        cpuXAxis.setUpperBound(NO_MEASUREMENTS - 1);
        cpuXAxis.setAutoRanging(false);

        NumberAxis cpuYAxis = (NumberAxis) cpuUsageChart.getYAxis();
        cpuYAxis.setLowerBound(0);
        cpuYAxis.setUpperBound(100);
        cpuYAxis.setAutoRanging(false);

        memoryXAxis = (NumberAxis) memoryUsageChart.getXAxis();
        memoryXAxis.setLowerBound(0);
        memoryXAxis.setUpperBound(NO_MEASUREMENTS - 1);
        memoryXAxis.setAutoRanging(false);

        MonitorTask monitorTask = new MonitorTask();
        new Thread(monitorTask).start();
    }

    public void startProgressBar() {
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
    }

    public void stopProgressBar() {
        progressBar.setProgress(1d);
    }

    public void setProgress(Double progress) {
        progressBar.setProgress(progress);
    }

    public void appendLog(String msg) {
        this.progressLog.appendText(msg + "\n");
    }

    public void setSimName(String simName) {
        title.setText(initTitle + " " + simName);
    }

    @FXML
    private void abortSimulation() {
        mainController.abortSimulation();
    }

    private class MonitorTask extends Task<Void> {

        @Override
        protected Void call() {
            int cycle = 0;

            OperatingSystemMXBean bean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

            while (!Thread.interrupted()) {
                double processCpuLoad = bean.getProcessCpuLoad() * 100;
                double systemCpuLoad = bean.getSystemCpuLoad() * 100;
                // Transform from byte to GB
                double memorySize = bean.getTotalPhysicalMemorySize() / (1000 * 1000 * 1000);
                double memoryUsed = memorySize - (bean.getFreePhysicalMemorySize() / (1000 * 1000 * 1000));

                final int xValue = cycle;

                //System.out.println(processCpuLoad + " / " + systemCpuLoad);
                Platform.runLater(() -> {
                    systemCpuData.add(new XYChart.Data<>(xValue, systemCpuLoad));
                    processCpuData.add(new XYChart.Data<>(xValue, processCpuLoad));
                    memorySizeData.add(new XYChart.Data<>(xValue, memorySize));
                    memoryUsedData.add(new XYChart.Data<>(xValue, memoryUsed));

                    if (xValue >= NO_MEASUREMENTS) {
                        double oldLowerBound = cpuXAxis.getLowerBound();
                        double oldUpperBound = cpuXAxis.getUpperBound();

                        cpuXAxis.setLowerBound(oldLowerBound + 1);
                        cpuXAxis.setUpperBound(oldUpperBound + 1);

                        memoryXAxis.setLowerBound(oldLowerBound + 1);
                        memoryXAxis.setUpperBound(oldUpperBound + 1);
                    }
                });
                cycle++;

                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            return null;
        }
    }

    class ObservableLimitedList<T> extends ModifiableObservableListBase<T> {

        private final LinkedList<T> list;
        private final int maxSize;

        public ObservableLimitedList(int maxSize) {
            this.maxSize = maxSize;
            list = new LinkedList<>();
        }

        @Override
        public boolean add(T element) {
            boolean result = super.add(element);
            if (size() > maxSize) {
                remove(0);
            }
            return result;
        }

        // delegate overrides:
        @Override
        public T get(int index) {
            return list.get(index);
        }

        @Override
        public int size() {
            return list.size();
        }

        @Override
        protected void doAdd(int index, T element) {
            list.add(index, element);
        }

        @Override
        protected T doSet(int index, T element) {
            return list.set(index, element);
        }

        @Override
        protected T doRemove(int index) {
            return list.remove(index);
        }
    }
}
