package general;

import java.io.*;

import java.net.HttpURLConnection;
import java.net.URL;

import java.nio.charset.StandardCharsets;

import java.util.List;
import java.util.Map;

import java.lang.management.ManagementFactory;

import com.sun.management.OperatingSystemMXBean;


import org.junit.After;
import org.junit.Before;

import com.google.gson.Gson;

import static general.CommonConstants.*;

public abstract class Api {
    private static Process serverProcess;
    private static final Gson GSON = new Gson();
    protected int latestCreatedCategoryId;

    public Api() {}

    /* UTILS */

    // Generate a random string of varied length for testing purposes
    public String generateRandomString(int minLength, int maxLength, boolean isAllowEmpty) {
        String charactersWithWhitespace = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 ";
        String charactersWithoutWhitespace = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        String characters = isAllowEmpty ? charactersWithWhitespace : charactersWithoutWhitespace;

        int length = minLength + (int) (Math.random() * (maxLength - minLength + 1));
        StringBuilder randomString = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * characters.length());
            randomString.append(characters.charAt(index));
        }

        return randomString.toString().trim();
    }

    // Converts a Map (payload) into a JSON string.
    public String toJson(Map<String, Object> payloadMap) {
        return GSON.toJson(payloadMap);
    }

    public static HttpURLConnection request(String endpoint, String method, String body) throws Exception {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(method);
        connection.setRequestProperty("Accept", JSON_FORMAT);
        connection.setRequestProperty("Content-Type", JSON_FORMAT);
        connection.setDoOutput(true);
        if (body != null && !body.isEmpty()) {
            connection.setDoInput(true);
            connection.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
        }

        return connection;
    }

    public static HttpURLConnection requestWithId(String endpoint, String method, String id, String body) throws Exception {
        String newEndpoint = endpoint + "/" + id;

        return request(newEndpoint, method, body);
    }

    public static String readResponse(HttpURLConnection connection) throws Exception {
        StringBuilder response = new StringBuilder();
        BufferedReader reader;

        if (connection.getErrorStream() != null) {
            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
        }
        else {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        }

        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        return response.toString();
    }


    /* JUNIT */

    @Before
    public void startServer() throws Exception {
        serverProcess = new ProcessBuilder("java", "-jar", PATH_TO_SERVER_JAR).start();

        String endpointToTestConnection = BASE_URL + "docs";
        for (int attempt = 0; attempt < 10; attempt++) {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(endpointToTestConnection).openConnection();
                Thread.sleep(500);
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    connection.disconnect();
                    System.out.println("=======================Server is up and running.");
                    break;
                }
            } catch (Exception e) {
                System.out.println("Waiting for server to start...");
                Thread.sleep(1000);
            }
        }
    }

    @After
    public void stopServer() throws Exception {
        if (serverProcess != null) {
            serverProcess.destroy();
            serverProcess.waitFor();
            System.out.println("Server stopped.=======================\n");
        }
    }


    /* PERFORMANCE */

    private double getCPUUsage() {
        OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        double cupUsage = operatingSystemMXBean.getProcessCpuLoad();

        return cupUsage * 100; // Convert to percentage
    }

    private double getMemoryUsageInMB() {
        long memoryUsageBytes = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        return memoryUsageBytes / (1024.0 * 1024.0);    // Convert to MB
    }

    // To measure performance metrics
    public List<String> measurePerformanceMetrics(Runnable operation) {
        double initialCPU = getCPUUsage();
        double initialMemory = getMemoryUsageInMB();
        long startTime = System.currentTimeMillis();

        operation.run();

        long endTime = System.currentTimeMillis();
        double finalMemory = getMemoryUsageInMB();
        double finalCPU = getCPUUsage();

        long timeTaken = endTime - startTime; // in milliseconds
        double cpuUsed = finalCPU - initialCPU; // in percentage
        double memoryUsed = finalMemory - initialMemory; // in MB

        return List.of(
                String.valueOf(timeTaken),
                String.format("%.2f", cpuUsed),
                String.format("%.2f", memoryUsed)
        );
    }

    // To save performance metrics to CSV file
    public void savePerformanceMetricsToCSV(String filePath, Map<Integer, List<String>> performanceMetrics) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("Number of Objects,Time Taken (ms),CPU Usage (%),Memory Usage (MB)");

            performanceMetrics.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
                Integer numObjects = entry.getKey();
                List<String> metrics = entry.getValue();
                writer.println(numObjects + "," + metrics.get(0) + "," + metrics.get(1) + "," + metrics.get(2));
                System.out.println("\n- Performance metrics for " + numObjects + " objects: Time Taken = " + metrics.get(0) + " ms, CPU Usage = " + metrics.get(1) + " %, Memory Usage = " + metrics.get(2) + " MB");
            });
        } catch (IOException e) {
            throw new RuntimeException("Error writing CSV file", e);
        }
    }
}
