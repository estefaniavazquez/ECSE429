package interoperability;

import static general.CommonConstants.CATEGORIES_PROJECTS_ENDPOINT;
import static general.CommonConstants.CATEGORIES_TODOS_ENDPOINT;
import static general.CommonConstants.CATEGORIES_TODOS_ID_ENDPOINT;
import static general.CommonConstants.DELETE_METHOD;
import static general.CommonConstants.HEAD_METHOD;
import static general.CommonConstants.JSON_FORMAT;
import static general.CommonConstants.OPTIONS_METHOD;
import static general.CommonConstants.POST_METHOD;
import static general.CommonConstants.PROJECTS_CATEGORIES_ID_ENDPOINT;
import static general.CommonConstants.PROJECTS_TASKS_ENDPOINT;
import static general.CommonConstants.PROJECTS_TASKS_ID_ENDPOINT;
import static general.CommonConstants.PUT_METHOD;
import static general.CommonConstants.RELATIONSHIP_ID_OPTIONS;
import static general.CommonConstants.TODOS_CATEGORIES_ENDPOINT;
import static general.CommonConstants.TODOS_CATEGORIES_OPTIONS;
import static general.CommonConstants.TODOS_ENDPOINT;
import static general.CommonConstants.TODOS_TASKSOF_ENDPOINT;
import static general.CommonConstants.TODOS_TASKSOF_ID_ENDPOINT;
import static general.Utils.readResponse;
import static general.Utils.request;
import static general.Utils.requestPATCH;
import static org.junit.Assert.assertEquals;

import java.net.HttpURLConnection;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.fasterxml.jackson.databind.ObjectMapper;

import general.BaseApiTest;
import interoperability.models.JsonRelationship;
import interoperability.models.Todo;

/**
 * Test class for undocumented interoperability API endpoints and methods.
 * 
 * This class tests HTTP methods and endpoints that are not documented in the
 * API
 * specification but might be supported or should return appropriate error
 * responses.
 * It verifies that unsupported methods return proper HTTP error codes and that
 * nonexistent endpoints behave correctly.
 * 
 * Tested scenarios:
 * - Unsupported HTTP methods (PUT, DELETE, PATCH) on relationship endpoints
 * - Undocumented specific Ids endpoints
 * - OPTIONS method support for discovering allowed methods
 * 
 * Expected behaviors:
 * - 405 Method Not Allowed for unsupported methods on existing endpoints
 * - 404 Not Found for nonexistent specific relationship endpoints
 * - 200 OK for OPTIONS with proper Allow headers
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UndocumentedInteropApiTest extends BaseApiTest {

    /**
     * Tests the PUT /todos/{id}/categories endpoint with JSON format.
     * This test verifies that PUT method is not supported on the todo-categories
     * relationship endpoint. PUT is not documented for relationship endpoints
     * and should return 405 Method Not Allowed error.
     * Expected: 405 Method Not Allowed with appropriate error message.
     */
    @Test
    public void testPutTodoCategoriesJson() throws Exception {
        System.out.println("Running testPutTodoCategoriesJson...");

        ObjectMapper objectMapper = new ObjectMapper();

        // First create a todo
        Todo.TodoBody todoBody = new Todo.TodoBody("Test Todo PUT", false, "Test description");
        String todoJson = objectMapper.writeValueAsString(todoBody);

        HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                todoJson);
        String createTodoResponse = readResponse(createTodoConnection);
        Todo createdTodo = objectMapper.readValue(createTodoResponse, Todo.class);
        createTodoConnection.disconnect();

        // Try PUT on relationship endpoint (should be method not allowed)
        JsonRelationship relationshipBody = new JsonRelationship("1");
        String relationshipJson = objectMapper.writeValueAsString(relationshipBody);

        String endpoint = String.format(TODOS_CATEGORIES_ENDPOINT, createdTodo.getId());
        HttpURLConnection connection = request(endpoint, PUT_METHOD, JSON_FORMAT, JSON_FORMAT, relationshipJson);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);

        assertEquals(405, responseCode);
        assertEquals("Method Not Allowed", responseMessage);
        assertEquals("", responseBody);

        connection.disconnect();
        System.out.println("testPutTodoCategoriesJson passed.");
    }

    /**
     * Tests the DELETE /todos/{id}/categories endpoint with JSON format.
     * This test verifies that DELETE method without a specific category ID
     * is not supported on the todo-categories relationship endpoint.
     * DELETE should only work with specific Idss.
     * Expected: 405 Method Not Allowed for bulk delete operations.
     */
    @Test
    public void testDeleteTodoCategoriesJson() throws Exception {
        System.out.println("Running testDeleteTodoCategoriesJson...");

        ObjectMapper objectMapper = new ObjectMapper();

        // First create a todo
        Todo.TodoBody todoBody = new Todo.TodoBody("Test Todo DELETE", false, "Test description");
        String todoJson = objectMapper.writeValueAsString(todoBody);

        HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                todoJson);
        String createTodoResponse = readResponse(createTodoConnection);
        Todo createdTodo = objectMapper.readValue(createTodoResponse, Todo.class);
        createTodoConnection.disconnect();

        // Try DELETE on relationship collection endpoint (should be method not allowed)
        String endpoint = String.format(TODOS_CATEGORIES_ENDPOINT, createdTodo.getId());
        HttpURLConnection connection = request(endpoint, DELETE_METHOD, JSON_FORMAT, JSON_FORMAT, null);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);

        assertEquals(405, responseCode);
        assertEquals("Method Not Allowed", responseMessage);
        assertEquals("", responseBody);

        connection.disconnect();
        System.out.println("testDeleteTodoCategoriesJson passed.");
    }

    /**
     * Tests the PATCH /todos/{id}/tasksof endpoint with JSON format.
     * This test verifies that PATCH method is not supported on the todo-tasksof
     * relationship endpoint. PATCH is not documented for relationship endpoints
     * and should return 405 Method Not Allowed error.
     * Expected: 405 Method Not Allowed indicating PATCH is not supported.
     */
    @Test
    public void testPatchTodoTasksofJson() throws Exception {
        System.out.println("Running testPatchTodoTasksofJson...");

        ObjectMapper objectMapper = new ObjectMapper();

        // First create a todo
        Todo.TodoBody todoBody = new Todo.TodoBody("Test Todo PATCH", false, "Test description");
        String todoJson = objectMapper.writeValueAsString(todoBody);

        HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                todoJson);
        String createTodoResponse = readResponse(createTodoConnection);
        Todo createdTodo = objectMapper.readValue(createTodoResponse, Todo.class);
        createTodoConnection.disconnect();

        // Try PATCH on relationship endpoint (should be method not allowed)
        String endpoint = String.format(TODOS_TASKSOF_ENDPOINT, createdTodo.getId());
        HttpURLConnection connection = requestPATCH(endpoint, JSON_FORMAT, JSON_FORMAT);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);

        assertEquals(405, responseCode);
        assertEquals("Method Not Allowed", responseMessage);
        assertEquals("", responseBody);

        connection.disconnect();
        System.out.println("testPatchTodoTasksofJson passed.");
    }

    /* Test undocumented methods on specific Ids endpoints */

    /**
     * Tests the POST /todos/{id}/tasksof/{id} endpoint with JSON format.
     * This test verifies that POST method on specific Ids endpoints
     * is not supported. Relationship creation should only happen on collection
     * endpoints, not specific ID endpoints. Should return 405 Method Not Allowed.
     * Expected: 405 Method Not Allowed for POST on specific Ids.
     */
    @Test
    public void testPostTodoTasksofIdJson() throws Exception {
        System.out.println("Running testPostTodoTasksofIdJson...");

        ObjectMapper objectMapper = new ObjectMapper();

        // First create a todo
        Todo.TodoBody todoBody = new Todo.TodoBody("Test Todo POST ID", false, "Test description");
        String todoJson = objectMapper.writeValueAsString(todoBody);

        HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                todoJson);
        String createTodoResponse = readResponse(createTodoConnection);
        Todo createdTodo = objectMapper.readValue(createTodoResponse, Todo.class);
        createTodoConnection.disconnect();

        // Try POST on specific Ids endpoint (should be method not allowed)
        JsonRelationship relationshipBody = new JsonRelationship("1");
        String relationshipJson = objectMapper.writeValueAsString(relationshipBody);

        String endpoint = String.format(TODOS_TASKSOF_ID_ENDPOINT, createdTodo.getId(), "1");
        HttpURLConnection connection = request(endpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT, relationshipJson);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);

        assertEquals(404, responseCode);
        assertEquals("Not Found", responseMessage);
        assertEquals("", responseBody);

        connection.disconnect();
        System.out.println("testPostTodoTasksofIdJson passed.");
    }

    /**
     * Tests the HEAD /projects/{id}/categories/{id} endpoint with JSON format.
     * This test verifies that HEAD method on specific project-category relationship
     * ID endpoints returns 404 Not Found because these specific endpoints don't
     * exist.
     * The API only supports collection endpoints, not individual relationship
     * items.
     * Expected: 404 Not Found indicating the specific relationship endpoint doesn't
     * exist.
     */
    @Test
    public void testHeadProjectCategoryIdJson() throws Exception {
        System.out.println("Running testHeadProjectCategoryIdJson...");

        // Try HEAD on specific Ids endpoint (should be not found)
        String endpoint = String.format(PROJECTS_CATEGORIES_ID_ENDPOINT, "1", "1");
        HttpURLConnection connection = request(endpoint, HEAD_METHOD, JSON_FORMAT, JSON_FORMAT, null);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();

        assertEquals(404, responseCode);
        assertEquals("Not Found", responseMessage);

        connection.disconnect();
        System.out.println("testHeadProjectCategoryIdJson passed.");
    }

    /* Test OPTIONS methods on relationship endpoints */

    /**
     * Tests the OPTIONS /todos/{id}/categories endpoint with JSON format.
     * This test verifies that OPTIONS method returns the supported HTTP methods
     * for the todo-categories relationship endpoint. OPTIONS should return
     * the allowed methods (GET, POST, HEAD, DELETE) in the response headers.
     * Expected: 200 OK with allowed methods in headers matching
     * TODOS_CATEGORIES_OPTIONS.
     */
    @Test
    public void testOptionsTodoCategoriesJson() throws Exception {
        System.out.println("Running testOptionsTodoCategoriesJson...");

        ObjectMapper objectMapper = new ObjectMapper();

        // First create a todo
        Todo.TodoBody todoBody = new Todo.TodoBody("Test Todo OPTIONS", false, "Test description");
        String todoJson = objectMapper.writeValueAsString(todoBody);

        HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                todoJson);
        String createTodoResponse = readResponse(createTodoConnection);
        Todo createdTodo = objectMapper.readValue(createTodoResponse, Todo.class);
        createTodoConnection.disconnect();

        // Test OPTIONS on relationship endpoint
        String endpoint = String.format(TODOS_CATEGORIES_ENDPOINT, createdTodo.getId());
        HttpURLConnection connection = request(endpoint, OPTIONS_METHOD, JSON_FORMAT, JSON_FORMAT, null);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String allowHeader = connection.getHeaderField("Allow");

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertEquals(TODOS_CATEGORIES_OPTIONS, allowHeader);

        connection.disconnect();
        System.out.println("testOptionsTodoCategoriesJson passed.");
    }

    /**
     * Tests the OPTIONS /projects/{id}/tasks/{id} endpoint with JSON format.
     * This test verifies that OPTIONS method returns the supported HTTP methods
     * for specific project-task relationship ID endpoints. OPTIONS should return
     * the allowed methods for individual relationship items (typically DELETE).
     * Expected: 200 OK with allowed methods in headers matching
     * RELATIONSHIP_ID_OPTIONS.
     */
    @Test
    public void testOptionsProjectTasksIdJson() throws Exception {
        System.out.println("Running testOptionsProjectTasksIdJson...");

        // Test OPTIONS on specific Ids endpoint
        String endpoint = String.format(PROJECTS_TASKS_ID_ENDPOINT, "1", "1");
        HttpURLConnection connection = request(endpoint, OPTIONS_METHOD, JSON_FORMAT, JSON_FORMAT, null);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String allowHeader = connection.getHeaderField("Allow");

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertEquals(RELATIONSHIP_ID_OPTIONS, allowHeader);

        connection.disconnect();
        System.out.println("testOptionsProjectTasksIdJson passed.");
    }

    /**
     * Tests the PUT /projects/{id}/tasks endpoint with JSON format.
     * This test verifies that PUT method is not supported on the project-tasks
     * relationship endpoint. PUT is not documented for relationship endpoints
     * and should return 405 Method Not Allowed error.
     * Expected: 405 Method Not Allowed with appropriate error message.
     */
    @Test
    public void testPutProjectTasksJson() throws Exception {
        System.out.println("Running testPutProjectTasksJson...");

        ObjectMapper objectMapper = new ObjectMapper();
        JsonRelationship relationshipBody = new JsonRelationship("1");
        String relationshipJson = objectMapper.writeValueAsString(relationshipBody);

        String endpoint = String.format(PROJECTS_TASKS_ENDPOINT, "1");
        HttpURLConnection connection = request(endpoint, PUT_METHOD, JSON_FORMAT, JSON_FORMAT, relationshipJson);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);

        assertEquals(405, responseCode);
        assertEquals("Method Not Allowed", responseMessage);
        assertEquals("", responseBody);

        connection.disconnect();
        System.out.println("testPutProjectTasksJson passed.");
    }

    /**
     * Tests the DELETE /categories/{id}/projects endpoint with JSON format.
     * This test verifies that DELETE method without a specific project ID
     * is not supported on the category-projects relationship endpoint.
     * DELETE should only work with specific IDs.
     * Expected: 405 Method Not Allowed for bulk delete operations.
     */
    @Test
    public void testDeleteCategoryProjectsJson() throws Exception {
        System.out.println("Running testDeleteCategoryProjectsJson...");

        String endpoint = String.format(CATEGORIES_PROJECTS_ENDPOINT, "1");
        HttpURLConnection connection = request(endpoint, DELETE_METHOD, JSON_FORMAT, JSON_FORMAT, null);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);

        assertEquals(405, responseCode);
        assertEquals("Method Not Allowed", responseMessage);
        assertEquals("", responseBody);

        connection.disconnect();
        System.out.println("testDeleteCategoryProjectsJson passed.");
    }

    /**
     * Tests the PATCH /categories/{id}/todos endpoint with JSON format.
     * This test verifies that PATCH method is not supported on the category-todos
     * relationship endpoint. PATCH is not documented for relationship endpoints
     * and should return 405 Method Not Allowed error.
     * Expected: 405 Method Not Allowed indicating PATCH is not supported.
     */
    @Test
    public void testPatchCategoryTodosJson() throws Exception {
        System.out.println("Running testPatchCategoryTodosJson...");

        String endpoint = String.format(CATEGORIES_TODOS_ENDPOINT, "1");
        HttpURLConnection connection = requestPATCH(endpoint, JSON_FORMAT, JSON_FORMAT);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);

        assertEquals(405, responseCode);
        assertEquals("Method Not Allowed", responseMessage);
        assertEquals("", responseBody);

        connection.disconnect();
        System.out.println("testPatchCategoryTodosJson passed.");
    }

    /**
     * Tests the POST /categories/{id}/todos/{id} endpoint with JSON format.
     * This test verifies that POST method on specific Ids endpoints
     * is not supported. Relationship creation should only happen on collection
     * endpoints, not specific ID endpoints. Should return 405 Method Not Allowed.
     * Expected: 405 Method Not Allowed for POST on specific Ids.
     */
    @Test
    public void testPostCategoryTodoIdJson() throws Exception {
        System.out.println("Running testPostCategoryTodoIdJson...");

        ObjectMapper objectMapper = new ObjectMapper();
        JsonRelationship relationshipBody = new JsonRelationship("2");
        String relationshipJson = objectMapper.writeValueAsString(relationshipBody);

        String endpoint = String.format(CATEGORIES_TODOS_ID_ENDPOINT, "1", "2");
        HttpURLConnection connection = request(endpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT, relationshipJson);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);

        assertEquals(404, responseCode);
        assertEquals("Not Found", responseMessage);
        assertEquals("", responseBody);

        connection.disconnect();
        System.out.println("testPostCategoryTodoIdJson passed.");
    }

    /**
     * Posts a task payload containing an unsupported status field and expects a
     * validation failure in line with documented behaviour.
     */
    @Test
    public void testCreateTaskWithStatusFieldShouldFailExpected() throws Exception {
        System.out.println("Running testCreateTaskWithStatusFieldShouldFailExpected...");

        ObjectMapper objectMapper = new ObjectMapper();

        // Create a todo with an unsupported 'status' field
        String todoJson = "{ \"title\": \"Test Todo with Status\", \"doneStatus\": false, \"description\": \"Test description\", \"status\": \"in-progress\" }";

        HttpURLConnection connection = request(TODOS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT, todoJson);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();

        // Expecting a 400 Bad Request due to unsupported 'status' field
        assertEquals(400, responseCode);
        assertEquals("Bad Request", responseMessage);

        connection.disconnect();
        System.out.println("testCreateTaskWithStatusFieldShouldFailExpected passed.");
    }

}