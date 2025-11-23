package general;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.MemoryMXBean;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.ArrayList;

public class ResourceMonitor implements Runnable {
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final List<Double> cpuUsages = new ArrayList<>();
    private final List<Double> memoryUsages = new ArrayList<>();
    
    private final OperatingSystemMXBean osBean;
    private final MemoryMXBean memoryBean;
    private final com.sun.management.OperatingSystemMXBean sunOsBean;
    // Initial usages
    private double initialCpuUsage = 0.0;
    private double initialMemoryUsage = 0.0;

    public ResourceMonitor() {
        this.osBean = ManagementFactory.getOperatingSystemMXBean();
        this.memoryBean = ManagementFactory.getMemoryMXBean();
        this.sunOsBean = (com.sun.management.OperatingSystemMXBean) osBean;
    }
    public void stop() {
        running.set(false);
    }

    public String getAverageCpu() {
        return calculateAverage(cpuUsages, initialCpuUsage);
    }

    public String getAverageMemory() {
        return calculateAverage(memoryUsages, initialMemoryUsage);
    }

    public double getInitialCpuUsage() {
        return initialCpuUsage;
    }

    public double getInitialMemoryUsage() {
        return initialMemoryUsage;
    }

    public void captureNewBaseline() {
        try {
            // Take multiple samples and average for more stable baseline
            double cpuSum = 0;
            double memSum = 0;
            int samples = 5; // Fewer samples for faster response
            
            for (int i = 0; i < samples; i++) {
                cpuSum += getCurrentCpuUsage();
                memSum += getCurrentMemoryUsage();
                if (i < samples - 1) Thread.sleep(10); // Small delay between samples
            }
            
            initialCpuUsage = cpuSum / samples;
            initialMemoryUsage = memSum / samples;
            
            System.out.println("Baseline - CPU: " + String.format("%.2f", initialCpuUsage) + "%, Memory: " + String.format("%.2f", initialMemoryUsage) + " MB");
        } catch (Exception e) {
            System.err.println("Failed to capture baseline: " + e.getMessage());
            initialCpuUsage = 0.0;
            initialMemoryUsage = 0.0;
        }
    }


    private double getCurrentCpuUsage() {
        // Process CPU load as percentage
        double cpuLoad = sunOsBean.getProcessCpuLoad();
        if (cpuLoad < 0) {
            // Sometimes returns -1 if not available, use system CPU instead
            cpuLoad = osBean.getSystemLoadAverage() / osBean.getAvailableProcessors();
            if (cpuLoad < 0) cpuLoad = 0; // If still not available
        }
        return cpuLoad * 100.0;
    }

    private double getCurrentMemoryUsage() {
        // Heap memory usage in MB
        long usedMemory = memoryBean.getHeapMemoryUsage().getUsed();
        return usedMemory / (1024.0 * 1024.0);
    }

    private String calculateAverage(List<Double> usages, double initialUsage) {
        if (usages.isEmpty()) {
            return "0.00";
        }
        double sum = 0.0;
        for (Double usage : usages) {
            sum += usage;
        }
        double average = sum / usages.size();
        return String.format("%.2f", average - initialUsage);
    }

    @Override
    public void run() {

        while (running.get()) {
            try {
                double currentCpu = getCurrentCpuUsage();
                double currentMemory = getCurrentMemoryUsage();
                
                cpuUsages.add(currentCpu);
                memoryUsages.add(currentMemory);
                
                Thread.sleep(50); // Sample every 50 milliseconds for better granularity
            } catch (Exception e) {
                // Ignore the sample if failure
                System.err.println("Error during monitoring: " + e.getMessage());
            }
        }
    }

}