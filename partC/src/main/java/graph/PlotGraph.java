package graph;

import com.opencsv.CSVReader;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.renderer.xy.*;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.plot.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class PlotGraph {

    public void plotCsvMetrics(String endpoint) {
        // Paths to CSV files
        String createCsvPath = Paths.get(System.getProperty("user.dir"), "partC", "results", "create" + endpoint + ".csv").toString();
        String updateCsvPath = Paths.get(System.getProperty("user.dir"), "partC", "results", "update" + endpoint + ".csv").toString();
        String deleteCsvPath = Paths.get(System.getProperty("user.dir"), "partC", "results", "delete" + endpoint + ".csv").toString();

        // Parse CSV files
        List<String[]> createData = parseCSV(createCsvPath);
        List<String[]> updateData = parseCSV(updateCsvPath);
        List<String[]> deleteData = parseCSV(deleteCsvPath);

        // Create paths for graphs
        String timeTakenGraphPath = Paths.get(System.getProperty("user.dir"), "partC", "graphs", "time_taken_" + endpoint + ".png").toString();
        String cpuUsageGraphPath = Paths.get(System.getProperty("user.dir"), "partC", "graphs", "cpu_usage_" + endpoint + ".png").toString();
        String freeMemoryGraphPath = Paths.get(System.getProperty("user.dir"), "partC", "graphs", "free_memory_" + endpoint + ".png").toString();

        // Plot time taken graph
        plotGraph(createData, updateData, deleteData, 1, "Time Taken (ms)", "Number of " + endpoint, "Time Taken vs Number of " + endpoint, timeTakenGraphPath, false);

        // Plot cpu usage graph
        plotGraph(createData, updateData, deleteData, 2, "CPU Usage (%)", "Number of " + endpoint, "CPU Usage vs Number of " + endpoint, cpuUsageGraphPath, false);

        // Plot free memory graph
        plotGraph(createData, updateData, deleteData, 3, "Free Available Memory (MB)", "Number of " + endpoint, "Free Available Memory vs Number of " + endpoint, freeMemoryGraphPath, true);
    }

    private List<String[]> parseCSV(String csvFilePath) {
        try (CSVReader csvReader = new CSVReader(new FileReader(csvFilePath))) {
            return csvReader.readAll();
        } catch (IOException e) {
            throw new RuntimeException("Error reading CSV file: " + csvFilePath, e);
        }
    }

    private void plotGraph(List<String[]> createData, List<String[]> updateData, List<String[]> deleteData, int columnIndex,
                            String yAxisLabel, String xAxisLabel, String chartTitle, String outputFileName, boolean scale) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries seriesCreate = new XYSeries("Created");
        XYSeries seriesUpdate = new XYSeries("Updated");
        XYSeries seriesDelete = new XYSeries("Deleted");

        // Populate series
        for (int i = 1; i < createData.size(); i++) {
            String[] row = createData.get(i);
            try {
                double x = Double.parseDouble(row[0]);
                double y = Double.parseDouble(row[columnIndex]);
                seriesCreate.add(x, y);
            } catch (NumberFormatException e) {
                System.err.println("Skipping row in createData: " + String.join(",", row));
            }
        }

        for (int i = 1; i < updateData.size(); i++) {
            String[] row = updateData.get(i);
            try {
                double x = Double.parseDouble(row[0]);
                double y = Double.parseDouble(row[columnIndex]);
                seriesUpdate.add(x, y);
            } catch (NumberFormatException e) {
                System.err.println("Skipping row in updateData: " + String.join(",", row));
            }
        }

        for (int i = 1; i < deleteData.size(); i++) {
            String[] row = deleteData.get(i);
            try {
                double x = Double.parseDouble(row[0]);
                double y = Double.parseDouble(row[columnIndex]);
                seriesDelete.add(x, y);
            } catch (NumberFormatException e) {
                System.err.println("Skipping row in deleteData: " + String.join(",", row));
            }
        }

        // Add series to dataset
        dataset.addSeries(seriesCreate);
        dataset.addSeries(seriesUpdate);
        dataset.addSeries(seriesDelete);

        // Create chart
        JFreeChart chart = ChartFactory.createXYLineChart(
                chartTitle,
                xAxisLabel,
                yAxisLabel,
                dataset
        );

        chart.setBackgroundPaint(Color.WHITE);
        chart.getPlot().setBackgroundPaint(Color.WHITE);

        XYPlot plot = chart.getXYPlot();
        if (scale) {
            NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
            yAxis.setRange(7500, 8500);  // Only display values between 7500 and 8500 to highlight differences
        }

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setDefaultShapesVisible(true);
        renderer.setSeriesStroke(0, new BasicStroke(3.0f)); // Created
        renderer.setSeriesStroke(1, new BasicStroke(3.0f)); // Updated
        renderer.setSeriesStroke(2, new BasicStroke(3.0f)); // Deleted

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        // Save as PNG
        try {
            ChartUtils.saveChartAsPNG(new File(outputFileName), chart, 800, 600);
            System.out.println("Saved chart: " + outputFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Display chart
        JFrame frame = new JFrame(chartTitle);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(new ChartPanel(chart));
        frame.pack();
        frame.setVisible(true);
    }
}