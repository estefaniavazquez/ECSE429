package api;

import java.net.HttpURLConnection;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.*;
import org.junit.runners.MethodSorters;
import static org.junit.Assert.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import general.Api;
import models.Category;
import static general.CommonConstants.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CategoriesApiTest extends Api {
    private List<String> testCategoriesStrings = new ArrayList<>();

    /* Helpers */

    private void populateTestCategories() {
        for (int i = 0; i<MAX_NUM_OBJECTS_FOR_PERFORMANCE_TESTING; i++) {
            String title = generateRandomString(1, 50, false);
            String description = generateRandomString(0, 200, true);
            Category category = new Category(title, description);

            Map<String, Object> payloadMap = category.toPayloadMap();
            String jsonBody = toJson(payloadMap);
            testCategoriesStrings.add(jsonBody);
        }
    }

    private void createCategory(String categoryJsonBody) throws Exception {
        HttpURLConnection connection = request(CATEGORIES_ENDPOINT, POST_METHOD, categoryJsonBody);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);

        ObjectMapper objectMapper = new ObjectMapper();
        Category createdCategory = objectMapper.readValue(responseBody, Category.class);

        String createdCategoryJsonBody = toJson(createdCategory.toPayloadMap());

        assertEquals(201, responseCode);
        assertEquals("Created", responseMessage);
        assertEquals(createdCategoryJsonBody, categoryJsonBody);

        connection.getInputStream().close();

        latestCreatedCategoryId = Integer.parseInt(createdCategory.getId());
    }

    private void changeCategory(String id, String categoryJsonBody) throws Exception {
        HttpURLConnection connection = requestWithId(CATEGORIES_ENDPOINT, PUT_METHOD, id, categoryJsonBody);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);

        ObjectMapper objectMapper = new ObjectMapper();
        Category updatedCategory = objectMapper.readValue(responseBody, Category.class);

        String updatedCategoryJsonBody = toJson(updatedCategory.toPayloadMap());

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertEquals(id, updatedCategory.getId());
        assertEquals(updatedCategoryJsonBody, categoryJsonBody);

        connection.getInputStream().close();
    }

    private void deleteCategory(String id) throws Exception {
        HttpURLConnection connection = requestWithId(CATEGORIES_ENDPOINT, DELETE_METHOD, id, null);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertEquals("", responseBody);

        connection.getInputStream().close();
    }


    /*   /categories endpoint tests    */

    // Test POST /categories

    @Test
    public void testPostCategoriesJson() throws Exception {
        System.out.println("\n----------------------Creating categories performance tests");

        populateTestCategories();

        // Store <number of objects, <time taken to create all objects, CPU usage, memory usage>>
        Map<Integer, List<String>> performanceMetrics = new HashMap<>();

        for (int numObjects : NUM_OBJECTS_FOR_PERFORMANCE_TESTING) {
            System.out.println("\n############# Testing with " + numObjects + " categories");
            List<String> metrics = measurePerformanceMetrics(() -> {
                for (int i = 0; i < numObjects; i++) {
                    try {
                        createCategory(testCategoriesStrings.get(i));
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to create a random category", e);
                    }
                }
            });

            performanceMetrics.put(numObjects, metrics);

            // Sleep for a short duration to allow server to stabilize
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        String filePath = Paths.get(System.getProperty("user.dir"), "results", "createCategories.csv").toString();
        savePerformanceMetricsToCSV(filePath, performanceMetrics);

        System.out.println("\nSaved categories performance tests----------------------\n");
    }


    /*   /categories/:id endpoint tests   */

    // Test PUT /categories/:id

    @Test
    public void testPutCategoriesIdJson() throws Exception {
        System.out.println("\n----------------------Updating categories performance tests");

        populateTestCategories();

        // Store <number of objects, <time taken to update all objects, CPU usage, memory usage>>
        Map<Integer, List<String>> performanceMetrics = new HashMap<>();

        for (int numObjects : NUM_OBJECTS_FOR_PERFORMANCE_TESTING) {
            System.out.println("\n############# Testing with " + numObjects + " categories to update");

            // First, create the required number of categories
            for (int i = 0; i < numObjects; i++) {
                createCategory(testCategoriesStrings.get(i));
            }

            // Get fresh random data for updates
            populateTestCategories();

            // Now, update the created categories and measure performance
            List<String> metrics = measurePerformanceMetrics(() -> {
                for (int i = 0; i < numObjects; i++) {
                    String currentCategoryId = String.valueOf(latestCreatedCategoryId - numObjects + 1 + i);
                    try {
                        changeCategory(currentCategoryId, testCategoriesStrings.get(i));
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to update category", e);
                    }
                }
            });

            performanceMetrics.put(numObjects, metrics);

            // Sleep for a short duration to allow server to stabilize
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        String filePath = Paths.get(System.getProperty("user.dir"), "results", "updateCategories.csv").toString();
        savePerformanceMetricsToCSV(filePath, performanceMetrics);

        System.out.println("\nSaved updating categories performance tests----------------------\n");
    }

    // Test DELETE /categories/:id

    @Test
    public void testDeleteCategoriesIdJson() throws Exception {
        System.out.println("\n----------------------Deleting categories performance tests");

        populateTestCategories();

        // Store <number of objects, <time taken to delete all objects, CPU usage, memory usage>>
        Map<Integer, List<String>> performanceMetrics = new HashMap<>();

        for (int numObjects : NUM_OBJECTS_FOR_PERFORMANCE_TESTING) {
            System.out.println("\n############# Testing with " + numObjects + " categories to delete");

            // First, create the required number of categories
            for (int i = 0; i < numObjects; i++) {
                createCategory(testCategoriesStrings.get(i));
            }

            // Now, delete the created categories and measure performance
            List<String> metrics = measurePerformanceMetrics(() -> {
                for (int i = 0; i < numObjects; i++) {
                    String currentCategoryId = String.valueOf(latestCreatedCategoryId - numObjects + 1 + i);
                    try {
                        deleteCategory(currentCategoryId);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to delete category", e);
                    }
                }
            });

            performanceMetrics.put(numObjects, metrics);

            // Sleep for a short duration to allow server to stabilize
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        String filePath = Paths.get(System.getProperty("user.dir"), "results", "deleteCategories.csv").toString();
        savePerformanceMetricsToCSV(filePath, performanceMetrics);

        System.out.println("\nSaved deleting categories performance tests----------------------\n");
    }
}
