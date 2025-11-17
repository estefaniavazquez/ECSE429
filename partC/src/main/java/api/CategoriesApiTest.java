package api;

import java.net.HttpURLConnection;
import java.nio.file.Paths;
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
    /* Helpers */

    private void createCategory(String title, String description) throws Exception {
        Category category = new Category(title, description);
        Map<String, Object> payloadMap = category.toPayloadMap();
        String jsonBody = toJson(payloadMap);

        HttpURLConnection connection = request(CATEGORIES_ENDPOINT, POST_METHOD, jsonBody);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);

        System.out.println("Response code: " + responseCode);
        System.out.println("Response message: " + responseMessage);
        System.out.println("Response body: " + responseBody);

        ObjectMapper objectMapper = new ObjectMapper();
        Category createdCategory = objectMapper.readValue(responseBody, Category.class);

        assertEquals(201, responseCode);
        assertEquals("Created", responseMessage);
        assertEquals(title, createdCategory.getTitle());
        assertEquals(description, createdCategory.getDescription());

        connection.getInputStream().close();

        latestCreatedCategoryId = Integer.parseInt(createdCategory.getId());
    }

    private void changeCategory(String id, String newTitle, String newDescription) throws Exception {
        Category category = new Category(newTitle, newDescription);
        Map<String, Object> payloadMap = category.toPayloadMap();
        String jsonBody = toJson(payloadMap);

        HttpURLConnection connection = requestWithId(CATEGORIES_ENDPOINT, PUT_METHOD, id, jsonBody);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);

        ObjectMapper objectMapper = new ObjectMapper();
        Category updatedCategory = objectMapper.readValue(responseBody, Category.class);

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertEquals(id, updatedCategory.getId());
        assertEquals(newTitle, updatedCategory.getTitle());
        assertEquals(newDescription, updatedCategory.getDescription());

        connection.disconnect();
    }

    private void deleteCategory(String id) throws Exception {
        HttpURLConnection connection = requestWithId(CATEGORIES_ENDPOINT, DELETE_METHOD, id, null);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertEquals("", responseBody);

        connection.disconnect();
    }


    /*   /categories endpoint tests    */

    // Test POST /categories

    @Test
    public void testPostCategoriesJson() throws Exception {
        System.out.println("\n----------------------Creating categories performance tests");

        // Store <number of objects, <time taken to create all objects, CPU usage, memory usage>>
        Map<Integer, List<String>> performanceMetrics = new HashMap<>();

        for (int numObjects : NUM_OBJECTS_FOR_PERFORMANCE_TESTING) {
            List<String> metrics = measurePerformanceMetrics(() -> {
                for (int i = 0; i < numObjects; i++) {
                    System.out.println("\n############# Creating category " + (i + 1) + " of " + numObjects);
                    String randomTitle = generateRandomString(1, 50, false);
                    String randomDescription = generateRandomString(0, 200, true);
                    try {
                        createCategory(randomTitle, randomDescription);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to create a random category", e);
                    }
                }
            });

            performanceMetrics.put(numObjects, metrics);
        }

        String filePath = Paths.get(System.getProperty("user.dir"), "results", "createCategories.csv").toString();
        savePerformanceMetricsToCSV(filePath, performanceMetrics);

        System.out.println("\nSaved categories performance tests----------------------\n");
    }


    /*   /categories/:id endpoint tests   */

    // Test PUT /categories/:id

    @Test
    public void testPutCategoriesIdJson() throws Exception {

    }

    // Test DELETE /categories/:id

    @Test
    public void testDeleteCategoriesIdJson() throws Exception {

    }
}
