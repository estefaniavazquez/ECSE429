package interoperability;

import static general.CommonConstants.CATEGORIES_ENDPOINT;
import static general.CommonConstants.CATEGORIES_PROJECTS_ENDPOINT;
import static general.CommonConstants.CATEGORIES_TODOS_ENDPOINT;
import static general.CommonConstants.DELETE_METHOD;
import static general.CommonConstants.GET_METHOD;
import static general.CommonConstants.JSON_FORMAT;
import static general.CommonConstants.POST_METHOD;
import static general.CommonConstants.PROJECTS_CATEGORIES_ENDPOINT;
import static general.CommonConstants.PROJECTS_ENDPOINT;
import static general.CommonConstants.PROJECTS_TASKS_ENDPOINT;
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

import categories.Category.CategoryBody;
import categories.JsonCategory;
import general.BaseApiTest;
import interoperability.models.JsonRelationship;
import interoperability.models.JsonTodo;
import interoperability.models.Todo;
import projects.JsonProject;
import projects.Project.ProjectBody;

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
                HttpURLConnection connection = request(endpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                                relationshipJson);

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
                HttpURLConnection connection = request(endpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                                relationshipJson);

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
                HttpURLConnection connection = request(endpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                                relationshipJson);

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

        /**
         * Posts malformed JSON to /projects to assert the API returns a structured
         * 400 error.
         */
        @Test
        public void testMalformedJsonReturnsStructuredError() throws Exception {
                System.out.println("Running testMalformedJsonReturnsStructuredError...");

                // Malformed JSON (missing closing brace)
                String malformedJson = "{\"name\":\"Malformed Project\",\"description\":\"Testing malformed JSON\"";

                HttpURLConnection connection = request(PROJECTS_TASKS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                                malformedJson);

                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String responseBody = readResponse(connection);

                assertEquals(400, responseCode);
                assertEquals("Bad Request", responseMessage);

                connection.disconnect();
                System.out.println("testMalformedJsonReturnsStructuredError passed.");
        }

        /**
         * Sends JSON with an XML content type to highlight the server's error
         * handling for mismatched headers.
         */
        @Test
        public void testMalformedJsonWithXmlContentType() throws Exception {
                System.out.println("Running testMalformedJsonWithXmlContentType...");

                // Malformed JSON (missing closing brace)
                String malformedJson = "{\"name\":\"Malformed Project\",\"description\":\"Testing malformed JSON\"";

                HttpURLConnection connection = request(PROJECTS_TASKS_ENDPOINT, POST_METHOD, JSON_FORMAT,
                                "application/xml",
                                malformedJson);

                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();

                assertEquals(400, responseCode);
                assertEquals("Bad Request", responseMessage);

                connection.disconnect();
                System.out.println("testMalformedJsonWithXmlContentType passed.");
        }

        /**
         * Tests a complete interoperability workflow across all entity types.
         * This comprehensive test creates todos, projects, and categories, then
         * establishes
         * and verifies all possible relationships between them. Tests the full
         * lifecycle:
         * 1. Create entities (todo, project, category)
         * 2. Establish all relationships (todo-category, todo-project,
         * project-category)
         * 3. Verify relationships exist and are accessible from all endpoints
         * 4. Clean up by removing relationships
         * Expected: All operations succeed with proper HTTP status codes and data
         * consistency.
         */
        @Test
        public void testCompleteInteroperabilityWorkflowJson() throws Exception {
                System.out.println("Running testCompleteInteroperabilityWorkflowJson...");

                ObjectMapper objectMapper = new ObjectMapper();

                // Step 1: Create a new category
                CategoryBody categoryBody = new CategoryBody("Test Category", "Category for interoperability testing");
                String categoryJson = objectMapper.writeValueAsString(categoryBody);

                HttpURLConnection createCategoryConnection = request(CATEGORIES_ENDPOINT, POST_METHOD, JSON_FORMAT,
                                JSON_FORMAT,
                                categoryJson);
                String createCategoryResponse = readResponse(createCategoryConnection);
                categories.Category createdCategory = objectMapper.readValue(createCategoryResponse,
                                categories.Category.class);
                createCategoryConnection.disconnect();

                // Step 2: Create a new project
                ProjectBody projectBody = new ProjectBody("Test Project", false, true,
                                "Project for interoperability testing");
                String projectJson = objectMapper.writeValueAsString(projectBody);

                HttpURLConnection createProjectConnection = request(PROJECTS_ENDPOINT, POST_METHOD, JSON_FORMAT,
                                JSON_FORMAT,
                                projectJson);
                String createProjectResponse = readResponse(createProjectConnection);
                projects.Project createdProject = objectMapper.readValue(createProjectResponse, projects.Project.class);
                createProjectConnection.disconnect();

                // Step 3: Create a new todo
                Todo.TodoBody todoBody = new Todo.TodoBody("Test Todo", false, "Todo for interoperability testing");
                String todoJson = objectMapper.writeValueAsString(todoBody);

                HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                                todoJson);
                String createTodoResponse = readResponse(createTodoConnection);
                Todo createdTodo = objectMapper.readValue(createTodoResponse, Todo.class);
                createTodoConnection.disconnect();

                // Step 4: Link todo to category
                JsonRelationship todoCategoryRel = new JsonRelationship(createdCategory.getId());
                String todoCategoryJson = objectMapper.writeValueAsString(todoCategoryRel);

                String todoCategoryEndpoint = String.format(TODOS_CATEGORIES_ENDPOINT, createdTodo.getId());
                HttpURLConnection todoCategoryConnection = request(todoCategoryEndpoint, POST_METHOD, JSON_FORMAT,
                                JSON_FORMAT,
                                todoCategoryJson);
                assertEquals(201, todoCategoryConnection.getResponseCode());
                todoCategoryConnection.disconnect();

                // Step 5: Link todo to project (tasksof)
                JsonRelationship todoProjectRel = new JsonRelationship(createdProject.getId());
                String todoProjectJson = objectMapper.writeValueAsString(todoProjectRel);

                String todoProjectEndpoint = String.format(TODOS_TASKSOF_ENDPOINT, createdTodo.getId());
                HttpURLConnection todoProjectConnection = request(todoProjectEndpoint, POST_METHOD, JSON_FORMAT,
                                JSON_FORMAT,
                                todoProjectJson);
                assertEquals(201, todoProjectConnection.getResponseCode());
                todoProjectConnection.disconnect();

                // Step 6: Link project to category
                JsonRelationship projectCategoryRel = new JsonRelationship(createdCategory.getId());
                String projectCategoryJson = objectMapper.writeValueAsString(projectCategoryRel);

                String projectCategoryEndpoint = String.format(PROJECTS_CATEGORIES_ENDPOINT, createdProject.getId());
                HttpURLConnection projectCategoryConnection = request(projectCategoryEndpoint, POST_METHOD, JSON_FORMAT,
                                JSON_FORMAT, projectCategoryJson);
                assertEquals(201, projectCategoryConnection.getResponseCode());
                projectCategoryConnection.disconnect();

                // Step 7: Verify relationships from todo perspective
                HttpURLConnection verifyTodoCategoryConnection = request(todoCategoryEndpoint, GET_METHOD, JSON_FORMAT,
                                JSON_FORMAT, null);
                String todoCategoryResponse = readResponse(verifyTodoCategoryConnection);
                JsonCategory todoCategories = objectMapper.readValue(todoCategoryResponse, JsonCategory.class);
                assertTrue("Todo should have the created category", todoCategories.contains(createdCategory));
                verifyTodoCategoryConnection.disconnect();

                HttpURLConnection verifyTodoProjectConnection = request(todoProjectEndpoint, GET_METHOD, JSON_FORMAT,
                                JSON_FORMAT, null);
                String todoProjectResponse = readResponse(verifyTodoProjectConnection);
                JsonProject todoProjects = objectMapper.readValue(todoProjectResponse, JsonProject.class);
                assertTrue("Todo should have the created project", todoProjects.contains(createdProject));
                verifyTodoProjectConnection.disconnect();

                // Step 8: Verify relationships from project perspective
                HttpURLConnection verifyProjectCategoryConnection = request(projectCategoryEndpoint, GET_METHOD,
                                JSON_FORMAT,
                                JSON_FORMAT, null);
                String projectCategoryResponse = readResponse(verifyProjectCategoryConnection);
                JsonCategory projectCategories = objectMapper.readValue(projectCategoryResponse, JsonCategory.class);
                assertTrue("Project should have the created category", projectCategories.contains(createdCategory));
                verifyProjectCategoryConnection.disconnect();

                String projectTasksEndpoint = String.format(PROJECTS_TASKS_ENDPOINT, createdProject.getId());
                HttpURLConnection verifyProjectTasksConnection = request(projectTasksEndpoint, GET_METHOD, JSON_FORMAT,
                                JSON_FORMAT, null);
                String projectTasksResponse = readResponse(verifyProjectTasksConnection);
                JsonTodo projectTodos = objectMapper.readValue(projectTasksResponse, JsonTodo.class);
                assertTrue("Project should have the created todo", projectTodos.contains(createdTodo));
                verifyProjectTasksConnection.disconnect();

                // Step 9: Verify relationships from category perspective
                String categoryProjectsEndpoint = String.format(CATEGORIES_PROJECTS_ENDPOINT, createdCategory.getId());
                HttpURLConnection verifyCategoryProjectsConnection = request(categoryProjectsEndpoint, GET_METHOD,
                                JSON_FORMAT,
                                JSON_FORMAT, null);
                String categoryProjectsResponse = readResponse(verifyCategoryProjectsConnection);
                JsonProject categoryProjects = objectMapper.readValue(categoryProjectsResponse, JsonProject.class);
                assertTrue("Category should have the created project",
                                !categoryProjects.toString().contains(createdProject.getId()));
                verifyCategoryProjectsConnection.disconnect();

                String categoryTodosEndpoint = String.format(CATEGORIES_TODOS_ENDPOINT, createdCategory.getId());
                HttpURLConnection verifyCategoryTodosConnection = request(categoryTodosEndpoint, GET_METHOD,
                                JSON_FORMAT,
                                JSON_FORMAT, null);
                String categoryTodosResponse = readResponse(verifyCategoryTodosConnection);
                JsonTodo categoryTodos = objectMapper.readValue(categoryTodosResponse, JsonTodo.class);
                assertTrue("Category should have the created todo", !categoryTodos.contains(createdTodo));
                verifyCategoryTodosConnection.disconnect();

                System.out.println("testCompleteInteroperabilityWorkflowJson passed.");
        }

        /**
         * Sends JSON with an XML content type and confirms that the server creates
         * the resource while returning JSON.
         * 
         * @throws Exception
         */
        @Test
        public void testMalformedJsonWithXmlContentTypeActualBehavior() throws Exception {
                System.out.println("Running testMalformedJsonWithXmlContentTypeActualBehavior...");

                ObjectMapper objectMapper = new ObjectMapper();

                // Create a todo with JSON body but XML content type
                Todo.TodoBody todoBody = new Todo.TodoBody("Test Todo XML Content Type", false, "Test description");
                String todoJson = objectMapper.writeValueAsString(todoBody);

                HttpURLConnection connection = request(TODOS_ENDPOINT, POST_METHOD, "application/xml",
                                "application/xml",
                                todoJson);

                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();

                assertEquals(201, responseCode);
                assertEquals("Created", responseMessage);

                connection.disconnect();
                System.out.println("testMalformedJsonWithXmlContentTypeActualBehavior passed.");
        }

}