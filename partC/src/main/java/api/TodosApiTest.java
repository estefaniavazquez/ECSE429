package api;

import java.net.HttpURLConnection;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
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

    private void populateTestTodos() {
        testTodosStrings.clear();
        for (int i = 0; i < MAX_NUM_OBJECTS_FOR_PERFORMANCE_TESTING; i++) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("title", generateRandomString(1, 50, false));
            payload.put("doneStatus", false);
            payload.put("description", generateRandomString(0, 200, true));
            String jsonBody = toJson(payload);
            testTodosStrings.add(jsonBody);
        }
    }

    private void createTodo(String jsonBody) throws Exception {
        HttpURLConnection connection =
                request(TODOS_ENDPOINT, POST_METHOD, jsonBody);

        assertEquals(201, connection.getResponseCode());

        String response = readResponse(connection);
        ObjectMapper mapper = new ObjectMapper();
        Todo todo = mapper.readValue(response, Todo.class);

        latestCreatedTodoId = Integer.parseInt(todo.getId());
        connection.getInputStream().close();
    }

    private void changeTodo(String id, String jsonBody) throws Exception {
        HttpURLConnection connection =
                requestWithId(TODOS_ENDPOINT, PUT_METHOD, id, jsonBody);

        assertEquals(200, connection.getResponseCode());

        String response = readResponse(connection);
        ObjectMapper mapper = new ObjectMapper();
        Todo updated = mapper.readValue(response, Todo.class);

        assertEquals(id, updated.getId());
        assertEquals(jsonBody, toJson(updated.toPayloadMap()));
        connection.getInputStream().close();
    }

    private void deleteTodo(String id) throws Exception {
        HttpURLConnection connection =
                requestWithId(TODOS_ENDPOINT, DELETE_METHOD, id, null);

        assertEquals(200, connection.getResponseCode());
        assertEquals("", readResponse(connection));
        connection.getInputStream().close();
    }

    @Test
    public void testPostTodosJson() throws Exception {
        System.out.println("\n----------------------Creating todos performance tests");

        Map<Integer, List<String>> performanceMetrics = new HashMap<>();

        populateTestTodos();

        for (int numObjects : NUM_OBJECTS_FOR_PERFORMANCE_TESTING) {

            List<String> metrics = measurePerformanceMetrics(() -> {
                for (int i = 0; i < numObjects; i++) {
                    try {
                        createTodo(testTodosStrings.get(i));
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to create todo", e);
                    }
                }
            });

            performanceMetrics.put(numObjects, metrics);
        }

        String filePath = Paths.get(System.getProperty("user.dir"),
                "results", "createTodos.csv").toString();

        savePerformanceMetricsToCSV(filePath, performanceMetrics);

        System.out.println("\nSaved creating todos performance tests----------------------\n");
    }

    @Test
    public void testPutTodosIdJson() throws Exception {
        System.out.println("\n----------------------Updating todos performance tests");

        Map<Integer, List<String>> performanceMetrics = new HashMap<>();

        for (int numObjects : NUM_OBJECTS_FOR_PERFORMANCE_TESTING) {

            populateTestTodos();

            for (int i = 0; i < numObjects; i++) {
                createTodo(testTodosStrings.get(i));
            }

            int startId = latestCreatedTodoId - numObjects + 1;

            populateTestTodos();

            List<String> metrics = measurePerformanceMetrics(() -> {
                for (int i = 0; i < numObjects; i++) {
                    try {
                        changeTodo(String.valueOf(startId + i), testTodosStrings.get(i));
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to update todo", e);
                    }
                }
            });

            performanceMetrics.put(numObjects, metrics);
        }

        String filePath = Paths.get(System.getProperty("user.dir"),
                "results", "updateTodos.csv").toString();

        savePerformanceMetricsToCSV(filePath, performanceMetrics);

        System.out.println("\nSaved updating todos performance tests----------------------\n");
    }

    @Test
    public void testDeleteTodosIdJson() throws Exception {
        System.out.println("\n----------------------Deleting todos performance tests");

        Map<Integer, List<String>> performanceMetrics = new HashMap<>();

        for (int numObjects : NUM_OBJECTS_FOR_PERFORMANCE_TESTING) {

            populateTestTodos();

            for (int i = 0; i < numObjects; i++) {
                createTodo(testTodosStrings.get(i));
            }

            int startId = latestCreatedTodoId - numObjects + 1;

            List<String> metrics = measurePerformanceMetrics(() -> {
                for (int i = 0; i < numObjects; i++) {
                    try {
                        deleteTodo(String.valueOf(startId + i));
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to delete todo", e);
                    }
                }
            });

            performanceMetrics.put(numObjects, metrics);
        }

        String filePath = Paths.get(System.getProperty("user.dir"),
                "results", "deleteTodos.csv").toString();

        savePerformanceMetricsToCSV(filePath, performanceMetrics);

        System.out.println("\nSaved deleting todos performance tests----------------------\n");
    }
}
