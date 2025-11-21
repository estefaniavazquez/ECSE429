package api;

import java.net.HttpURLConnection;
import java.nio.file.Paths;
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
import static general.CommonConstants.NUM_OBJECTS_FOR_PERFORMANCE_TESTING;
import static general.CommonConstants.POST_METHOD;
import static general.CommonConstants.PUT_METHOD;
import static general.CommonConstants.TODOS_ENDPOINT;
import models.Todo;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TodosApiTest extends Api {

    private int latestCreatedTodoId;

    /* --------------------------------------------------------------------
       HELPERS
       -------------------------------------------------------------------- */

    private Todo createTodo() throws Exception {

        Map<String, Object> payload = new HashMap<>();
        payload.put("title", "todo-" + System.nanoTime());
        payload.put("doneStatus", false);
        payload.put("description", "desc-" + System.currentTimeMillis());

        HttpURLConnection connection =
                request(TODOS_ENDPOINT, POST_METHOD, toJson(payload));

        assertEquals(201, connection.getResponseCode());

        String response = readResponse(connection);
        ObjectMapper mapper = new ObjectMapper();
        Todo todo = mapper.readValue(response, Todo.class);

        latestCreatedTodoId = Integer.parseInt(todo.getId());
        return todo;
    }

    private void createTodo(String title, String description) throws Exception {

        Map<String, Object> payload = new HashMap<>();
        payload.put("title", title);
        payload.put("doneStatus", false);
        payload.put("description", description);

        HttpURLConnection connection =
                request(TODOS_ENDPOINT, POST_METHOD, toJson(payload));

        assertEquals(201, connection.getResponseCode());

        String response = readResponse(connection);
        ObjectMapper mapper = new ObjectMapper();
        Todo todo = mapper.readValue(response, Todo.class);

        latestCreatedTodoId = Integer.parseInt(todo.getId());
    }

    private void changeTodo(String id, String newTitle, String newDescription) throws Exception {

        Map<String, Object> payload = new HashMap<>();
        payload.put("title", newTitle);
        payload.put("doneStatus", false);
        payload.put("description", newDescription);

        HttpURLConnection connection =
                requestWithId(TODOS_ENDPOINT, PUT_METHOD, id, toJson(payload));

        assertEquals(200, connection.getResponseCode());

        String response = readResponse(connection);
        ObjectMapper mapper = new ObjectMapper();
        Todo updated = mapper.readValue(response, Todo.class);

        assertEquals(id, updated.getId());
        assertEquals(newTitle, updated.getTitle());
        assertEquals(newDescription, updated.getDescription());
    }

    private void deleteTodo(String id) throws Exception {
        HttpURLConnection connection =
                requestWithId(TODOS_ENDPOINT, DELETE_METHOD, id, null);

        assertEquals(200, connection.getResponseCode());
        assertEquals("", readResponse(connection));
    }

    /* --------------------------------------------------------------------
       TESTS
       -------------------------------------------------------------------- */

    @Test
    public void testPostTodosJson() throws Exception {
        System.out.println("\n----------------------Creating todos performance tests");

        Map<Integer, List<String>> performanceMetrics = new HashMap<>();

        for (int numObjects : NUM_OBJECTS_FOR_PERFORMANCE_TESTING) {

            List<String> metrics = measurePerformanceMetrics(() -> {
                for (int i = 0; i < numObjects; i++) {
                    System.out.println("\n############# Creating todo "
                            + (i + 1) + " of " + numObjects);
                    try {
                        createTodo(
                                "todo-" + System.nanoTime(),
                                "desc-" + System.nanoTime()
                        );
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

            // Create Todos to update
            for (int i = 0; i < numObjects; i++) {
                String t = "todo-" + System.nanoTime();
                String d = "desc-" + System.nanoTime();
                createTodo(t, d);
            }

            int startId = latestCreatedTodoId - numObjects + 1;

            List<String> metrics = measurePerformanceMetrics(() -> {
                for (int i = 0; i < numObjects; i++) {
                    System.out.println("\n############# Updating todo "
                            + (i + 1) + " of " + numObjects);
                    try {
                        changeTodo(
                                String.valueOf(startId + i),
                                "updated-" + System.nanoTime(),
                                "updatedDesc-" + System.nanoTime()
                        );
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

            // Create Todos to delete
            for (int i = 0; i < numObjects; i++) {
                String t = "todo-" + System.nanoTime();
                String d = "desc-" + System.nanoTime();
                createTodo(t, d);
            }

            int startId = latestCreatedTodoId - numObjects + 1;

            List<String> metrics = measurePerformanceMetrics(() -> {
                for (int i = 0; i < numObjects; i++) {
                    System.out.println("\n############# Deleting todo "
                            + (i + 1) + " of " + numObjects);
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
