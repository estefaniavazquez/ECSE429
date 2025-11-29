package api;

import java.net.HttpURLConnection;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.fasterxml.jackson.databind.ObjectMapper;

import general.Api;
import static general.CommonConstants.DELETE_METHOD;
import static general.CommonConstants.MAX_NUM_OBJECTS_FOR_PERFORMANCE_TESTING;
import static general.CommonConstants.NUM_OBJECTS_FOR_PERFORMANCE_TESTING;
import static general.CommonConstants.POST_METHOD;
import static general.CommonConstants.PUT_METHOD;
import static general.CommonConstants.TODOS_ENDPOINT;
import models.Todo;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TodosApiTest extends Api {

    private List<String> testTodosStrings = new ArrayList<>();

    /* Helpers */

    private void populateTestTodos() {
        testTodosStrings.clear();
        for (int i = 0; i < MAX_NUM_OBJECTS_FOR_PERFORMANCE_TESTING; i++) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("title", generateRandomString(1, 50, false));
            payload.put("doneStatus", generateRandomBoolean());
            payload.put("description", generateRandomString(0, 200, true));
            testTodosStrings.add(toJson(payload));
        }
    }

    private void createTodo(String todoJsonBody) throws Exception {
        HttpURLConnection conn = request(TODOS_ENDPOINT, POST_METHOD, todoJsonBody);
        int code = conn.getResponseCode();
        String body = readResponse(conn);

        assertEquals(201, code);

        Todo created = new ObjectMapper().readValue(body, Todo.class);

        // Minimal checks only (do NOT compare raw JSON)
        assertNotNull(created.getId());
        assertNotNull(created.getTitle());

        conn.getInputStream().close();
        latestCreatedTodoId = Integer.parseInt(created.getId());
    }

    private void changeTodo(String id, String todoJsonBody) throws Exception {
        HttpURLConnection conn = requestWithId(TODOS_ENDPOINT, PUT_METHOD, id, todoJsonBody);
        int code = conn.getResponseCode();
        String body = readResponse(conn);

        assertEquals(200, code);

        Todo updated = new ObjectMapper().readValue(body, Todo.class);
        assertEquals(id, updated.getId());

        conn.getInputStream().close();
    }

    private void deleteTodo(String id) throws Exception {
        HttpURLConnection conn = requestWithId(TODOS_ENDPOINT, DELETE_METHOD, id, null);
        int code = conn.getResponseCode();
        String body = readResponse(conn);

        assertEquals(200, code);
        assertEquals("", body);

        conn.getInputStream().close();
    }

    /* Tests */

    @Test
    public void testPostTodosJson() throws Exception {
        System.out.println("\n----------------------Creating todos performance tests");

        populateTestTodos();
        Map<Integer, List<String>> metricsMap = new HashMap<>();

        for (int numObjects : NUM_OBJECTS_FOR_PERFORMANCE_TESTING) {
            System.out.println("\n############# Testing with " + numObjects + " todos");

            List<String> metrics = measurePerformanceMetrics(() -> {
                for (int i = 0; i < numObjects; i++) {
                    try {
                        createTodo(testTodosStrings.get(i));
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to create todo " + (i + 1)
                                + " of " + numObjects, e);
                    }
                }
            });

            metricsMap.put(numObjects, metrics);

            try { Thread.sleep(2000); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        String path = Paths.get(System.getProperty("user.dir"),
                "results", "createTodos.csv").toString();
        savePerformanceMetricsToCSV(path, metricsMap);

        System.out.println("\nSaved creating todos performance tests----------------------\n");
    }

    @Test
    public void testPutTodosIdJson() throws Exception {
        System.out.println("\n----------------------Updating todos performance tests");

        populateTestTodos();
        Map<Integer, List<String>> metricsMap = new HashMap<>();

        for (int numObjects : NUM_OBJECTS_FOR_PERFORMANCE_TESTING) {
            System.out.println("\n############# Testing with " + numObjects + " todos to update");

            // Create todos
            for (int i = 0; i < numObjects; i++) {
                createTodo(testTodosStrings.get(i));
            }

            int startId = latestCreatedTodoId - numObjects + 1;

            populateTestTodos(); // get new data for updating

            List<String> metrics = measurePerformanceMetrics(() -> {
                for (int i = 0; i < numObjects; i++) {
                    String id = String.valueOf(startId + i);
                    try {
                        changeTodo(id, testTodosStrings.get(i));
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to update todo " + (i + 1)
                                + " of " + numObjects, e);
                    }
                }
            });

            metricsMap.put(numObjects, metrics);

            try { Thread.sleep(2000); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        String path = Paths.get(System.getProperty("user.dir"),
                "results", "updateTodos.csv").toString();
        savePerformanceMetricsToCSV(path, metricsMap);

        System.out.println("\nSaved updating todos performance tests----------------------\n");
    }

    @Test
    public void testDeleteTodosIdJson() throws Exception {
        System.out.println("\n----------------------Deleting todos performance tests");

        populateTestTodos();
        Map<Integer, List<String>> metricsMap = new HashMap<>();

        for (int numObjects : NUM_OBJECTS_FOR_PERFORMANCE_TESTING) {
            System.out.println("\n############# Testing with " + numObjects + " todos to delete");

            // Create todos
            for (int i = 0; i < numObjects; i++) {
                createTodo(testTodosStrings.get(i));
            }

            int startId = latestCreatedTodoId - numObjects + 1;

            List<String> metrics = measurePerformanceMetrics(() -> {
                for (int i = 0; i < numObjects; i++) {
                    String id = String.valueOf(startId + i);
                    try {
                        deleteTodo(id);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to delete todo " + (i + 1)
                                + " of " + numObjects, e);
                    }
                }
            });

            metricsMap.put(numObjects, metrics);

            try { Thread.sleep(2000); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        String path = Paths.get(System.getProperty("user.dir"),
                "results", "deleteTodos.csv").toString();
        savePerformanceMetricsToCSV(path, metricsMap);

        System.out.println("\nSaved deleting todos performance tests----------------------\n");
    }
}
