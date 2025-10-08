package interoperability;

import static general.CommonConstants.DELETE_METHOD;
import static general.CommonConstants.JSON_FORMAT;
import static general.CommonConstants.POST_METHOD;
import static general.CommonConstants.TODOS_CATEGORIES_ENDPOINT;
import static general.CommonConstants.TODOS_CATEGORIES_ID_ENDPOINT;
import static general.CommonConstants.TODOS_ENDPOINT;
import static general.CommonConstants.TODOS_TASKSOF_ENDPOINT;
import static general.Utils.readResponse;
import static general.Utils.request;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.HttpURLConnection;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.fasterxml.jackson.databind.ObjectMapper;

import general.BaseApiTest;

/**
 * Test class for expected failure scenarios in interoperability API endpoints.
 * 
 * This class tests various error conditions and edge cases that should result
 * in
 * proper error responses from the interoperability API. It verifies that the
 * API
 * handles invalid inputs, nonexistent resources, and malformed requests
 * appropriately
 * with correct HTTP status codes and error messages.
 * 
 * Tested failure scenarios:
 * - Operations with nonexistent entity IDs (todos, categories, projects)
 * - Invalid request bodies and malformed JSON
 * - Duplicate relationship creation attempts
 * - Deletion of nonexistent relationships
 * 
 * Expected error responses:
 * - 400 Bad Request for invalid data or malformed requests
 * - 404 Not Found for nonexistent resources or relationships
 * - Error messages in response body for debugging
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExpectedBehaviourFailingInteropApiTest extends BaseApiTest {

    /* Test expected failure scenarios for interoperability */

    /**
     * Tests creating a relationship with a nonexistent todo ID.
     * This test verifies that attempting to create a todo-category relationship
     * using a todo ID that doesn't exist (99999) results in a proper error
     * response.
     * The API should return 404 Not Found with appropriate error message.
     * Expected: 404 Not Found with error message indicating todo not found.
     */
    @Test
    public void testCreateRelationshipWithNonexistentTodoJson() throws Exception {
        System.out.println("Running testCreateRelationshipWithNonexistentTodoJson...");

        ObjectMapper objectMapper = new ObjectMapper();

        // Try to create relationship with nonexistent todo ID
        JsonRelationship relationshipBody = new JsonRelationship("1");
        String relationshipJson = objectMapper.writeValueAsString(relationshipBody);

        String endpoint = String.format(TODOS_CATEGORIES_ENDPOINT, "99999");
        HttpURLConnection connection = request(endpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT, relationshipJson);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);

        assertEquals(404, responseCode);
        assertEquals("Not Found", responseMessage);
        assertTrue(responseBody.contains("errorMessages"));

        connection.disconnect();
        System.out.println("testCreateRelationshipWithNonexistentTodoJson passed.");
    }

    /**
     * Tests creating a relationship with a nonexistent category ID.
     * This test verifies that attempting to create a todo-category relationship
     * using a valid todo but with a category ID that doesn't exist (99999) results
     * in a proper error response. The API should return 404 Not Found.
     * Expected: 404 Not Found with error message indicating category not found.
     */
    @Test
    public void testCreateRelationshipWithNonexistentCategoryJson() throws Exception {
        System.out.println("Running testCreateRelationshipWithNonexistentCategoryJson...");

        ObjectMapper objectMapper = new ObjectMapper();

        // First create a todo
        Todo.TodoBody todoBody = new Todo.TodoBody("Test Todo Invalid Cat", false, "Test description");
        String todoJson = objectMapper.writeValueAsString(todoBody);

        HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                todoJson);
        String createTodoResponse = readResponse(createTodoConnection);
        Todo createdTodo = objectMapper.readValue(createTodoResponse, Todo.class);
        createTodoConnection.disconnect();

        // Try to create relationship with nonexistent category
        JsonRelationship relationshipBody = new JsonRelationship("99999");
        String relationshipJson = objectMapper.writeValueAsString(relationshipBody);

        String endpoint = String.format(TODOS_CATEGORIES_ENDPOINT, createdTodo.getId());
        HttpURLConnection connection = request(endpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT, relationshipJson);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);

        assertEquals(404, responseCode);
        assertEquals("Not Found", responseMessage);
        assertTrue(responseBody.contains("errorMessages"));

        connection.disconnect();
        System.out.println("testCreateRelationshipWithNonexistentCategoryJson passed.");
    }

    /**
     * Tests creating a relationship with invalid request body.
     * This test verifies that attempting to create a todo-category relationship
     * with an empty or invalid JSON body results in a proper error response.
     * The API should return 400 Bad Request for malformed relationship data.
     * Expected: 400 Bad Request with error message indicating invalid body format.
     */
    @Test
    public void testCreateRelationshipWithInvalidBodyJson() throws Exception {
        System.out.println("Running testCreateRelationshipWithInvalidBodyJson...");

        ObjectMapper objectMapper = new ObjectMapper();

        // First create a todo
        Todo.TodoBody todoBody = new Todo.TodoBody("Test Todo Invalid Body", false, "Test description");
        String todoJson = objectMapper.writeValueAsString(todoBody);

        HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                todoJson);
        String createTodoResponse = readResponse(createTodoConnection);
        Todo createdTodo = objectMapper.readValue(createTodoResponse, Todo.class);
        createTodoConnection.disconnect();

        // Try to create relationship with invalid body (missing id)
        String invalidJson = "{}";

        String endpoint = String.format(TODOS_CATEGORIES_ENDPOINT, createdTodo.getId());
        HttpURLConnection connection = request(endpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT, invalidJson);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);

        assertEquals(400, responseCode);
        assertEquals("Bad Request", responseMessage);
        assertTrue(responseBody.contains("errorMessages"));

        connection.disconnect();
        System.out.println("testCreateRelationshipWithInvalidBodyJson passed.");
    }

    /**
     * Tests deleting a nonexistent relationship.
     * This test verifies that attempting to delete a todo-category relationship
     * that doesn't exist results in a proper error response. Uses a valid todo
     * but tries to delete a relationship with a category that was never associated.
     * Expected: 404 Not Found with error message indicating relationship not found.
     */
    @Test
    public void testDeleteNonexistentRelationshipJson() throws Exception {
        System.out.println("Running testDeleteNonexistentRelationshipJson...");

        ObjectMapper objectMapper = new ObjectMapper();

        // First create a todo
        Todo.TodoBody todoBody = new Todo.TodoBody("Test Todo Delete Nonexist", false, "Test description");
        String todoJson = objectMapper.writeValueAsString(todoBody);

        HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                todoJson);
        String createTodoResponse = readResponse(createTodoConnection);
        Todo createdTodo = objectMapper.readValue(createTodoResponse, Todo.class);
        createTodoConnection.disconnect();

        // Try to delete a relationship that doesn't exist
        String endpoint = String.format(TODOS_CATEGORIES_ID_ENDPOINT, createdTodo.getId(), "99999");
        HttpURLConnection connection = request(endpoint, DELETE_METHOD, JSON_FORMAT, JSON_FORMAT, null);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);

        assertEquals(404, responseCode);
        assertEquals("Not Found", responseMessage);
        assertTrue(responseBody.contains("errorMessages"));

        connection.disconnect();
        System.out.println("testDeleteNonexistentRelationshipJson passed.");
    }

    /**
     * Tests creating a relationship with a nonexistent project ID.
     * This test verifies that attempting to create a todo-project (tasksof)
     * relationship
     * using a valid todo but with a project ID that doesn't exist (99999) results
     * in a proper error response. The API should return 404 Not Found.
     * Expected: 404 Not Found with error message indicating project not found.
     */
    @Test
    public void testCreateRelationshipWithNonexistentProjectJson() throws Exception {
        System.out.println("Running testCreateRelationshipWithNonexistentProjectJson...");

        ObjectMapper objectMapper = new ObjectMapper();

        // First create a todo
        Todo.TodoBody todoBody = new Todo.TodoBody("Test Todo Invalid Proj", false, "Test description");
        String todoJson = objectMapper.writeValueAsString(todoBody);

        HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                todoJson);
        String createTodoResponse = readResponse(createTodoConnection);
        Todo createdTodo = objectMapper.readValue(createTodoResponse, Todo.class);
        createTodoConnection.disconnect();

        // Try to create relationship with nonexistent project
        JsonRelationship relationshipBody = new JsonRelationship("99999");
        String relationshipJson = objectMapper.writeValueAsString(relationshipBody);

        String endpoint = String.format(TODOS_TASKSOF_ENDPOINT, createdTodo.getId());
        HttpURLConnection connection = request(endpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT, relationshipJson);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);

        assertEquals(404, responseCode);
        assertEquals("Not Found", responseMessage);
        assertTrue(responseBody.contains("errorMessages"));

        connection.disconnect();
        System.out.println("testCreateRelationshipWithNonexistentProjectJson passed.");
    }

    /**
     * Tests creating a relationship with malformed JSON.
     * This test verifies that attempting to create a todo-category relationship
     * with invalid JSON syntax results in a proper error response. Uses
     * syntactically
     * incorrect JSON that cannot be parsed by the server.
     * Expected: 400 Bad Request with error message indicating JSON parsing error.
     */
    @Test
    public void testCreateRelationshipWithMalformedJsonJson() throws Exception {
        System.out.println("Running testCreateRelationshipWithMalformedJsonJson...");

        ObjectMapper objectMapper = new ObjectMapper();

        // First create a todo
        Todo.TodoBody todoBody = new Todo.TodoBody("Test Todo Malformed", false, "Test description");
        String todoJson = objectMapper.writeValueAsString(todoBody);

        HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                todoJson);
        String createTodoResponse = readResponse(createTodoConnection);
        Todo createdTodo = objectMapper.readValue(createTodoResponse, Todo.class);
        createTodoConnection.disconnect();

        // Try to create relationship with malformed JSON
        String malformedJson = "{\"id\":\"1\",";

        String endpoint = String.format(TODOS_CATEGORIES_ENDPOINT, createdTodo.getId());
        HttpURLConnection connection = request(endpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT, malformedJson);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);

        assertEquals(400, responseCode);
        assertEquals("Bad Request", responseMessage);
        assertTrue(responseBody.contains("errorMessages"));

        connection.disconnect();
        System.out.println("testCreateRelationshipWithMalformedJsonJson passed.");
    }
}