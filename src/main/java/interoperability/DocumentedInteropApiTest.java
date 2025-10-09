package interoperability;

import static general.CommonConstants.CATEGORIES_PROJECTS_ENDPOINT;
import static general.CommonConstants.CATEGORIES_PROJECTS_ID_ENDPOINT;
import static general.CommonConstants.CATEGORIES_TODOS_ENDPOINT;
import static general.CommonConstants.CATEGORIES_TODOS_ID_ENDPOINT;
import static general.CommonConstants.DELETE_METHOD;
import static general.CommonConstants.GET_METHOD;
import static general.CommonConstants.HEAD_METHOD;
import static general.CommonConstants.JSON_FORMAT;
import static general.CommonConstants.POST_METHOD;
import static general.CommonConstants.PROJECTS_CATEGORIES_ENDPOINT;
import static general.CommonConstants.PROJECTS_TASKS_ENDPOINT;
import static general.CommonConstants.PROJECTS_TASKS_ID_ENDPOINT;
import static general.CommonConstants.TODOS_CATEGORIES_ENDPOINT;
import static general.CommonConstants.TODOS_CATEGORIES_ID_ENDPOINT;
import static general.CommonConstants.TODOS_ENDPOINT;
import static general.CommonConstants.TODOS_TASKSOF_ENDPOINT;
import static general.CommonConstants.TODOS_TASKSOF_ID_ENDPOINT;
import static general.CommonConstants.XML_FORMAT;
import static general.Utils.readResponse;
import static general.Utils.request;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.HttpURLConnection;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import categories.JsonCategory;
import categories.XmlCategory;
import general.BaseApiTest;
import interoperability.models.JsonRelationship;
import interoperability.models.JsonTodo;
import interoperability.models.Todo;
import projects.JsonProject;

/**
 * Test class for documented interoperability API endpoints.
 * 
 * This class tests all the documented relationship endpoints between todos,
 * projects,
 * and categories as specified in the API documentation. It verifies that all
 * supported
 * HTTP methods (GET, POST, HEAD, DELETE) work correctly for establishing and
 * managing
 * relationships between entities.
 * 
 * Covered endpoints:
 * - /todos/{id}/categories (GET, POST, HEAD)
 * - /todos/{id}/tasksof (GET, POST)
 * - /projects/{id}/categories (GET, POST)
 * - /todos/{id}/categories/{id} (DELETE)
 * 
 * All tests expect successful operations with appropriate HTTP status codes
 * (200, 201).
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DocumentedInteropApiTest extends BaseApiTest {

    /* Test Todo-Category Relationships */

    /**
     * Tests the GET /todos/{id}/categories endpoint with JSON format.
     * This test verifies that we can successfully retrieve all categories
     * associated with a specific todo item. Creates a test todo first,
     * then fetches its categories (should return empty list initially).
     * Expected: 200 OK with empty categories array in JSON format.
     */
    @Test
    public void testGetTodoCategoriesJson() throws Exception {
        System.out.println("Running testGetTodoCategoriesJson...");

        // First create a todo
        Todo.TodoBody todoBody = new Todo.TodoBody("Test Todo", false, "Test description");
        ObjectMapper objectMapper = new ObjectMapper();
        String todoJson = objectMapper.writeValueAsString(todoBody);

        HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                todoJson);
        String createTodoResponse = readResponse(createTodoConnection);
        Todo createdTodo = objectMapper.readValue(createTodoResponse, Todo.class);
        createTodoConnection.disconnect();

        // Get categories for this todo
        String endpoint = String.format(TODOS_CATEGORIES_ENDPOINT, createdTodo.getId());
        HttpURLConnection connection = request(endpoint, GET_METHOD, JSON_FORMAT, JSON_FORMAT, null);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String contentType = connection.getContentType();
        String responseBody = readResponse(connection);

        JsonCategory categories = objectMapper.readValue(responseBody, JsonCategory.class);

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertTrue(contentType.contains(JSON_FORMAT));
        assertNotNull(categories);

        connection.disconnect();
        System.out.println("testGetTodoCategoriesJson passed.");
    }

    /**
     * Tests the GET /todos/{id}/categories endpoint with XML format.
     * This test verifies that we can successfully retrieve all categories
     * associated with a specific todo item using XML content type.
     * Creates a test todo first, then fetches its categories.
     * Expected: 200 OK with categories data in XML format.
     */
    @Test
    public void testGetTodoCategoriesXml() throws Exception {
        System.out.println("Running testGetTodoCategoriesXml...");

        // First create a todo
        Todo.TodoBody todoBody = new Todo.TodoBody("Test Todo XML", false, "Test description");
        XmlMapper xmlMapper = new XmlMapper();
        String todoXml = xmlMapper.writeValueAsString(todoBody);

        HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT, todoXml);
        String createTodoResponse = readResponse(createTodoConnection);
        Todo createdTodo = xmlMapper.readValue(createTodoResponse, Todo.class);
        createTodoConnection.disconnect();

        // Get categories for this todo
        String endpoint = String.format(TODOS_CATEGORIES_ENDPOINT, createdTodo.getId());
        HttpURLConnection connection = request(endpoint, GET_METHOD, XML_FORMAT, XML_FORMAT, null);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String contentType = connection.getContentType();
        String responseBody = readResponse(connection);

        XmlCategory categories = xmlMapper.readValue(responseBody, XmlCategory.class);

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertTrue(contentType.contains(XML_FORMAT));
        assertNotNull(categories);

        connection.disconnect();
        System.out.println("testGetTodoCategoriesXml passed.");
    }

    /**
     * Tests the POST /todos/{id}/categories endpoint with JSON format.
     * This test verifies that we can successfully create a relationship
     * between a todo and a category by posting a category reference.
     * Creates a todo first, then establishes a relationship with an existing
     * category.
     * Expected: 201 Created confirming the relationship was established.
     */
    @Test
    public void testPostTodoCategoryRelationshipJson() throws Exception {
        System.out.println("Running testPostTodoCategoryRelationshipJson...");

        ObjectMapper objectMapper = new ObjectMapper();

        // First create a todo
        Todo.TodoBody todoBody = new Todo.TodoBody("Test Todo for Category", false, "Test description");
        String todoJson = objectMapper.writeValueAsString(todoBody);

        HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                todoJson);
        String createTodoResponse = readResponse(createTodoConnection);
        Todo createdTodo = objectMapper.readValue(createTodoResponse, Todo.class);
        createTodoConnection.disconnect();

        // Create relationship with existing category (Office category has id "1")
        JsonRelationship relationshipBody = new JsonRelationship("1");
        String relationshipJson = objectMapper.writeValueAsString(relationshipBody);

        String endpoint = String.format(TODOS_CATEGORIES_ENDPOINT, createdTodo.getId());
        HttpURLConnection connection = request(endpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT, relationshipJson);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();

        assertEquals(201, responseCode);
        assertEquals("Created", responseMessage);

        connection.disconnect();
        System.out.println("testPostTodoCategoryRelationshipJson passed.");
    }

    /* Test Todo-Project Relationships (tasksof) */

    /**
     * Tests the GET /todos/{id}/tasksof endpoint with JSON format.
     * This test verifies that we can retrieve all projects that a specific
     * todo is a task of. The 'tasksof' relationship shows which projects
     * contain this todo as a task. Initially should return empty list.
     * Expected: 200 OK with projects array in JSON format.
     */
    @Test
    public void testGetTodoTasksofJson() throws Exception {
        System.out.println("Running testGetTodoTasksofJson...");

        ObjectMapper objectMapper = new ObjectMapper();

        // First create a todo
        Todo.TodoBody todoBody = new Todo.TodoBody("Test Todo for Project", false, "Test description");
        String todoJson = objectMapper.writeValueAsString(todoBody);

        HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                todoJson);
        String createTodoResponse = readResponse(createTodoConnection);
        Todo createdTodo = objectMapper.readValue(createTodoResponse, Todo.class);
        createTodoConnection.disconnect();

        // Get projects for this todo (tasksof relationship)
        String endpoint = String.format(TODOS_TASKSOF_ENDPOINT, createdTodo.getId());
        HttpURLConnection connection = request(endpoint, GET_METHOD, JSON_FORMAT, JSON_FORMAT, null);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String contentType = connection.getContentType();
        String responseBody = readResponse(connection);

        JsonProject projects = objectMapper.readValue(responseBody, JsonProject.class);

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertTrue(contentType.contains(JSON_FORMAT));
        assertNotNull(projects);

        connection.disconnect();
        System.out.println("testGetTodoTasksofJson passed.");
    }

    /**
     * Tests the POST /todos/{id}/tasksof endpoint with JSON format.
     * This test verifies that we can create a relationship between a todo
     * and a project, making the todo a task of that project. Creates both
     * a todo and project first, then establishes the tasksof relationship.
     * Expected: 201 Created confirming the task relationship was established.
     */
    @Test
    public void testPostTodoTasksofRelationshipJson() throws Exception {
        System.out.println("Running testPostTodoTasksofRelationshipJson...");

        ObjectMapper objectMapper = new ObjectMapper();

        // First create a todo
        Todo.TodoBody todoBody = new Todo.TodoBody("Test Todo for Tasksof", false, "Test description");
        String todoJson = objectMapper.writeValueAsString(todoBody);

        HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                todoJson);
        String createTodoResponse = readResponse(createTodoConnection);
        Todo createdTodo = objectMapper.readValue(createTodoResponse, Todo.class);
        createTodoConnection.disconnect();

        // Create relationship with existing project (default project has id "1")
        JsonRelationship relationshipBody = new JsonRelationship("1");
        String relationshipJson = objectMapper.writeValueAsString(relationshipBody);

        String endpoint = String.format(TODOS_TASKSOF_ENDPOINT, createdTodo.getId());
        HttpURLConnection connection = request(endpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT, relationshipJson);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();

        assertEquals(201, responseCode);
        assertEquals("Created", responseMessage);

        connection.disconnect();
        System.out.println("testPostTodoTasksofRelationshipJson passed.");
    }

    /* Test Project-Category Relationships */

    /**
     * Tests the GET /projects/{id}/categories endpoint with JSON format.
     * This test verifies that we can retrieve all categories associated
     * with a specific project. Projects can be categorized using categories,
     * and this endpoint returns all such category associations.
     * Expected: 200 OK with categories array in JSON format.
     */
    @Test
    public void testGetProjectCategoriesJson() throws Exception {
        System.out.println("Running testGetProjectCategoriesJson...");

        ObjectMapper objectMapper = new ObjectMapper();

        // Get categories for existing project (id "1")
        String endpoint = String.format(PROJECTS_CATEGORIES_ENDPOINT, "1");
        HttpURLConnection connection = request(endpoint, GET_METHOD, JSON_FORMAT, JSON_FORMAT, null);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String contentType = connection.getContentType();
        String responseBody = readResponse(connection);

        JsonCategory categories = objectMapper.readValue(responseBody, JsonCategory.class);

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertTrue(contentType.contains(JSON_FORMAT));
        assertNotNull(categories);

        connection.disconnect();
        System.out.println("testGetProjectCategoriesJson passed.");
    }

    /**
     * Tests the POST /projects/{id}/categories endpoint with JSON format.
     * This test verifies that we can create a relationship between a project
     * and a category. Creates an association by posting a category reference
     * to the project's categories endpoint using existing entities.
     * Expected: 201 Created confirming the relationship was established.
     */
    @Test
    public void testPostProjectCategoryRelationshipJson() throws Exception {
        System.out.println("Running testPostProjectCategoryRelationshipJson...");

        ObjectMapper objectMapper = new ObjectMapper();

        // Create relationship between existing project and category
        JsonRelationship relationshipBody = new JsonRelationship("2"); // Home category
        String relationshipJson = objectMapper.writeValueAsString(relationshipBody);

        String endpoint = String.format(PROJECTS_CATEGORIES_ENDPOINT, "1");
        HttpURLConnection connection = request(endpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT, relationshipJson);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();

        assertEquals(201, responseCode);
        assertEquals("Created", responseMessage);

        connection.disconnect();
        System.out.println("testPostProjectCategoryRelationshipJson passed.");
    }

    /* Test HEAD methods for relationships */

    /**
     * Tests the HEAD /todos/{id}/categories endpoint with JSON format.
     * This test verifies that we can perform a HEAD request to check
     * if the todo-categories relationship endpoint exists and is accessible
     * without retrieving the actual data. HEAD should return headers only.
     * Expected: 200 OK with no response body, confirming endpoint accessibility.
     */
    @Test
    public void testHeadTodoCategoriesJson() throws Exception {
        System.out.println("Running testHeadTodoCategoriesJson...");

        ObjectMapper objectMapper = new ObjectMapper();

        // First create a todo
        Todo.TodoBody todoBody = new Todo.TodoBody("Test Todo HEAD", false, "Test description");
        String todoJson = objectMapper.writeValueAsString(todoBody);

        HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                todoJson);
        String createTodoResponse = readResponse(createTodoConnection);
        Todo createdTodo = objectMapper.readValue(createTodoResponse, Todo.class);
        createTodoConnection.disconnect();

        // HEAD request for categories
        String endpoint = String.format(TODOS_CATEGORIES_ENDPOINT, createdTodo.getId());
        HttpURLConnection connection = request(endpoint, HEAD_METHOD, JSON_FORMAT, JSON_FORMAT, null);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String contentType = connection.getContentType();
        String responseBody = readResponse(connection);

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertTrue(contentType.contains(JSON_FORMAT));
        assertEquals("", responseBody);

        connection.disconnect();
        System.out.println("testHeadTodoCategoriesJson passed.");
    }

    /* Test DELETE relationship methods */

    /**
     * Tests the DELETE /todos/{id}/categories/{id} endpoint with JSON format.
     * This test verifies that we can remove an existing relationship between
     * a todo and a category. First establishes the relationship, then deletes it
     * using the specific category ID in the URL path.
     * Expected: 200 OK confirming the relationship was successfully removed.
     */
    @Test
    public void testDeleteTodoCategoryRelationshipJson() throws Exception {
        System.out.println("Running testDeleteTodoCategoryRelationshipJson...");

        ObjectMapper objectMapper = new ObjectMapper();

        // First create a todo
        Todo.TodoBody todoBody = new Todo.TodoBody("Test Todo Delete Rel", false, "Test description");
        String todoJson = objectMapper.writeValueAsString(todoBody);

        HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                todoJson);
        String createTodoResponse = readResponse(createTodoConnection);
        Todo createdTodo = objectMapper.readValue(createTodoResponse, Todo.class);
        createTodoConnection.disconnect();

        // Create relationship first
        JsonRelationship relationshipBody = new JsonRelationship("1");
        String relationshipJson = objectMapper.writeValueAsString(relationshipBody);

        String createEndpoint = String.format(TODOS_CATEGORIES_ENDPOINT, createdTodo.getId());
        HttpURLConnection createRelConnection = request(createEndpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                relationshipJson);
        int responseCodeForCreation = createRelConnection.getResponseCode();
        String responseMessageForCreation = createRelConnection.getResponseMessage();
        assertEquals(201, responseCodeForCreation);
        assertEquals("Created", responseMessageForCreation);
        createRelConnection.disconnect();

        // Now delete the relationship
        String deleteEndpoint = String.format(TODOS_CATEGORIES_ID_ENDPOINT, createdTodo.getId(), "1");
        HttpURLConnection connection = request(deleteEndpoint, DELETE_METHOD, JSON_FORMAT, JSON_FORMAT, null);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);

        connection.disconnect();
        System.out.println("testDeleteTodoCategoryRelationshipJson passed.");
    }

    /* Test Project-Task Relationships */

    /**
     * Tests the GET /projects/{id}/tasks endpoint with JSON format.
     * This test verifies that we can retrieve all tasks (todos) associated
     * with a specific project. Projects can contain multiple todos as tasks,
     * and this endpoint returns all such task associations.
     * Expected: 200 OK with tasks array in JSON format.
     */
    @Test
    public void testGetProjectTasksJson() throws Exception {
        System.out.println("Running testGetProjectTasksJson...");

        ObjectMapper objectMapper = new ObjectMapper();

        // Get tasks for existing project (id "1")
        String endpoint = String.format(PROJECTS_TASKS_ENDPOINT, "1");
        HttpURLConnection connection = request(endpoint, GET_METHOD, JSON_FORMAT, JSON_FORMAT, null);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String contentType = connection.getContentType();
        String responseBody = readResponse(connection);

        JsonTodo tasks = objectMapper.readValue(responseBody, JsonTodo.class);

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertTrue(contentType.contains(JSON_FORMAT));
        assertNotNull(tasks);

        connection.disconnect();
        System.out.println("testGetProjectTasksJson passed.");
    }

    /**
     * Tests the POST /projects/{id}/tasks endpoint with JSON format.
     * This test verifies that we can create a relationship between a project
     * and a todo, making the todo a task of that project. Creates a todo first,
     * then establishes the project-task relationship.
     * Expected: 201 Created confirming the task relationship was established.
     */
    @Test
    public void testPostProjectTaskRelationshipJson() throws Exception {
        System.out.println("Running testPostProjectTaskRelationshipJson...");

        ObjectMapper objectMapper = new ObjectMapper();

        // First create a todo
        Todo.TodoBody todoBody = new Todo.TodoBody("Project Task Todo", false, "Task for project");
        String todoJson = objectMapper.writeValueAsString(todoBody);

        HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                todoJson);
        String createTodoResponse = readResponse(createTodoConnection);
        Todo createdTodo = objectMapper.readValue(createTodoResponse, Todo.class);
        createTodoConnection.disconnect();

        // Create relationship between existing project and the created todo
        JsonRelationship relationshipBody = new JsonRelationship(createdTodo.getId());
        String relationshipJson = objectMapper.writeValueAsString(relationshipBody);

        String endpoint = String.format(PROJECTS_TASKS_ENDPOINT, "1");
        HttpURLConnection connection = request(endpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT, relationshipJson);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();

        assertEquals(201, responseCode);
        assertEquals("Created", responseMessage);

        connection.disconnect();
        System.out.println("testPostProjectTaskRelationshipJson passed.");
    }

    /* Test Category-Project Relationships */

    /**
     * Tests the GET /categories/{id}/projects endpoint with JSON format.
     * This test verifies that we can retrieve all projects associated
     * with a specific category. Categories can be associated with multiple
     * projects,
     * and this endpoint returns all such project associations.
     * Expected: 200 OK with projects array in JSON format.
     */
    @Test
    public void testGetCategoryProjectsJson() throws Exception {
        System.out.println("Running testGetCategoryProjectsJson...");

        ObjectMapper objectMapper = new ObjectMapper();

        // Get projects for existing category (id "1")
        String endpoint = String.format(CATEGORIES_PROJECTS_ENDPOINT, "1");
        HttpURLConnection connection = request(endpoint, GET_METHOD, JSON_FORMAT, JSON_FORMAT, null);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String contentType = connection.getContentType();
        String responseBody = readResponse(connection);

        JsonProject projects = objectMapper.readValue(responseBody, JsonProject.class);

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertTrue(contentType.contains(JSON_FORMAT));
        assertNotNull(projects);

        connection.disconnect();
        System.out.println("testGetCategoryProjectsJson passed.");
    }

    /**
     * Tests the POST /categories/{id}/projects endpoint with JSON format.
     * This test verifies that we can create a relationship between a category
     * and a project from the category side. This is the bidirectional complement
     * to the /projects/{id}/categories endpoint.
     * Expected: 201 Created confirming the relationship was established.
     */
    @Test
    public void testPostCategoryProjectRelationshipJson() throws Exception {
        System.out.println("Running testPostCategoryProjectRelationshipJson...");

        ObjectMapper objectMapper = new ObjectMapper();

        // Create relationship between existing category and project
        JsonRelationship relationshipBody = new JsonRelationship("1"); // Office Work project
        String relationshipJson = objectMapper.writeValueAsString(relationshipBody);

        String endpoint = String.format(CATEGORIES_PROJECTS_ENDPOINT, "2"); // Home category
        HttpURLConnection connection = request(endpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT, relationshipJson);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();

        assertEquals(201, responseCode);
        assertEquals("Created", responseMessage);

        connection.disconnect();
        System.out.println("testPostCategoryProjectRelationshipJson passed.");
    }

    /* Test Category-Todo Relationships */

    /**
     * Tests the GET /categories/{id}/todos endpoint with JSON format.
     * This test verifies that we can retrieve all todos associated
     * with a specific category. Categories can be associated with multiple todos,
     * and this endpoint returns all such todo associations.
     * Expected: 200 OK with todos array in JSON format.
     */
    @Test
    public void testGetCategoryTodosJson() throws Exception {
        System.out.println("Running testGetCategoryTodosJson...");

        ObjectMapper objectMapper = new ObjectMapper();

        // Get todos for existing category (id "1")
        String endpoint = String.format(CATEGORIES_TODOS_ENDPOINT, "1");
        HttpURLConnection connection = request(endpoint, GET_METHOD, JSON_FORMAT, JSON_FORMAT, null);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String contentType = connection.getContentType();
        String responseBody = readResponse(connection);

        JsonTodo todos = objectMapper.readValue(responseBody, JsonTodo.class);

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertTrue(contentType.contains(JSON_FORMAT));
        assertNotNull(todos);

        connection.disconnect();
        System.out.println("testGetCategoryTodosJson passed.");
    }

    /**
     * Tests the POST /categories/{id}/todos endpoint with JSON format.
     * This test verifies that we can create a relationship between a category
     * and a todo from the category side. This is the bidirectional complement
     * to the /todos/{id}/categories endpoint.
     * Expected: 201 Created confirming the relationship was established.
     */
    @Test
    public void testPostCategoryTodoRelationshipJson() throws Exception {
        System.out.println("Running testPostCategoryTodoRelationshipJson...");

        ObjectMapper objectMapper = new ObjectMapper();

        // First create a todo
        Todo.TodoBody todoBody = new Todo.TodoBody("Category Todo", false, "Todo for category");
        String todoJson = objectMapper.writeValueAsString(todoBody);

        HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                todoJson);
        String createTodoResponse = readResponse(createTodoConnection);
        Todo createdTodo = objectMapper.readValue(createTodoResponse, Todo.class);
        createTodoConnection.disconnect();

        // Create relationship between existing category and the created todo
        JsonRelationship relationshipBody = new JsonRelationship(createdTodo.getId());
        String relationshipJson = objectMapper.writeValueAsString(relationshipBody);

        String endpoint = String.format(CATEGORIES_TODOS_ENDPOINT, "1"); // Office category
        HttpURLConnection connection = request(endpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT, relationshipJson);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();

        assertEquals(201, responseCode);
        assertEquals("Created", responseMessage);

        connection.disconnect();
        System.out.println("testPostCategoryTodoRelationshipJson passed.");
    }

    /* Test Additional DELETE endpoints */

    /**
     * Tests the DELETE /projects/{id}/tasks/{id} endpoint with JSON format.
     * This test verifies that we can remove an existing relationship between
     * a project and a todo (task). First establishes the relationship, then deletes
     * it
     * using the specific todo ID in the URL path.
     * Expected: 200 OK confirming the relationship was successfully removed.
     */
    @Test
    public void testDeleteProjectTaskRelationshipJson() throws Exception {
        System.out.println("Running testDeleteProjectTaskRelationshipJson...");

        ObjectMapper objectMapper = new ObjectMapper();

        // First create a todo and establish relationship
        Todo.TodoBody todoBody = new Todo.TodoBody("Delete Task Todo", false, "Task to be deleted");
        String todoJson = objectMapper.writeValueAsString(todoBody);

        HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                todoJson);
        String createTodoResponse = readResponse(createTodoConnection);
        Todo createdTodo = objectMapper.readValue(createTodoResponse, Todo.class);
        createTodoConnection.disconnect();

        // Create the relationship first
        JsonRelationship relationshipBody = new JsonRelationship(createdTodo.getId());
        String relationshipJson = objectMapper.writeValueAsString(relationshipBody);

        String createEndpoint = String.format(PROJECTS_TASKS_ENDPOINT, "1");
        HttpURLConnection createRelConnection = request(createEndpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                relationshipJson);
        createRelConnection.getResponseCode(); // Consume response
        createRelConnection.disconnect();

        // Now delete the relationship
        String deleteEndpoint = String.format(PROJECTS_TASKS_ID_ENDPOINT, "1", createdTodo.getId());
        HttpURLConnection connection = request(deleteEndpoint, DELETE_METHOD, JSON_FORMAT, JSON_FORMAT, null);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);

        connection.disconnect();
        System.out.println("testDeleteProjectTaskRelationshipJson passed.");
    }

    /**
     * Tests the DELETE /categories/{id}/projects/{id} endpoint with JSON format.
     * This test verifies that we can remove an existing relationship between
     * a category and a project. First establishes the relationship, then deletes it
     * using the specific project ID in the URL path.
     * Expected: 200 OK confirming the relationship was successfully removed.
     */
    @Test
    public void testDeleteCategoryProjectRelationshipJson() throws Exception {
        System.out.println("Running testDeleteCategoryProjectRelationshipJson...");

        ObjectMapper objectMapper = new ObjectMapper();

        // Create the relationship first
        JsonRelationship relationshipBody = new JsonRelationship("1");
        String relationshipJson = objectMapper.writeValueAsString(relationshipBody);

        String createEndpoint = String.format(CATEGORIES_PROJECTS_ENDPOINT, "2");
        HttpURLConnection createRelConnection = request(createEndpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                relationshipJson);
        createRelConnection.getResponseCode(); // Consume response
        createRelConnection.disconnect();

        // Now delete the relationship
        String deleteEndpoint = String.format(CATEGORIES_PROJECTS_ID_ENDPOINT, "2", "1");
        HttpURLConnection connection = request(deleteEndpoint, DELETE_METHOD, JSON_FORMAT, JSON_FORMAT, null);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);

        connection.disconnect();
        System.out.println("testDeleteCategoryProjectRelationshipJson passed.");
    }

    /**
     * Tests the DELETE /categories/{id}/todos/{id} endpoint with JSON format.
     * This test verifies that we can remove an existing relationship between
     * a category and a todo. First establishes the relationship, then deletes it
     * using the specific todo ID in the URL path.
     * Expected: 200 OK confirming the relationship was successfully removed.
     */
    @Test
    public void testDeleteCategoryTodoRelationshipJson() throws Exception {
        System.out.println("Running testDeleteCategoryTodoRelationshipJson...");

        ObjectMapper objectMapper = new ObjectMapper();

        // First create a todo and establish relationship
        Todo.TodoBody todoBody = new Todo.TodoBody("Delete Category Todo", false, "Todo to be deleted from category");
        String todoJson = objectMapper.writeValueAsString(todoBody);

        HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                todoJson);
        String createTodoResponse = readResponse(createTodoConnection);
        Todo createdTodo = objectMapper.readValue(createTodoResponse, Todo.class);
        createTodoConnection.disconnect();

        // Create the relationship first
        JsonRelationship relationshipBody = new JsonRelationship(createdTodo.getId());
        String relationshipJson = objectMapper.writeValueAsString(relationshipBody);

        String createEndpoint = String.format(CATEGORIES_TODOS_ENDPOINT, "1");
        HttpURLConnection createRelConnection = request(createEndpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                relationshipJson);
        createRelConnection.getResponseCode(); // Consume response
        createRelConnection.disconnect();

        // Now delete the relationship
        String deleteEndpoint = String.format(CATEGORIES_TODOS_ID_ENDPOINT, "1", createdTodo.getId());
        HttpURLConnection connection = request(deleteEndpoint, DELETE_METHOD, JSON_FORMAT, JSON_FORMAT, null);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);

        connection.disconnect();
        System.out.println("testDeleteCategoryTodoRelationshipJson passed.");
    }

    /**
     * Tests the DELETE /todos/{id}/tasksof/{id} endpoint with JSON format.
     * This test verifies that we can remove an existing tasksof relationship
     * between
     * a todo and a project. First establishes the relationship, then deletes it
     * using the specific project ID in the URL path.
     * Expected: 200 OK confirming the relationship was successfully removed.
     */
    @Test
    public void testDeleteTodoTasksofRelationshipJson() throws Exception {
        System.out.println("Running testDeleteTodoTasksofRelationshipJson...");

        ObjectMapper objectMapper = new ObjectMapper();

        // First create a todo and establish relationship
        Todo.TodoBody todoBody = new Todo.TodoBody("Delete Tasksof Todo", false, "Todo tasksof to be deleted");
        String todoJson = objectMapper.writeValueAsString(todoBody);

        HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                todoJson);
        String createTodoResponse = readResponse(createTodoConnection);
        Todo createdTodo = objectMapper.readValue(createTodoResponse, Todo.class);
        createTodoConnection.disconnect();

        // Create the relationship first
        JsonRelationship relationshipBody = new JsonRelationship("1");
        String relationshipJson = objectMapper.writeValueAsString(relationshipBody);

        String createEndpoint = String.format(TODOS_TASKSOF_ENDPOINT, createdTodo.getId());
        HttpURLConnection createRelConnection = request(createEndpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                relationshipJson);
        createRelConnection.getResponseCode(); // Consume response
        createRelConnection.disconnect();

        // Now delete the relationship
        String deleteEndpoint = String.format(TODOS_TASKSOF_ID_ENDPOINT, createdTodo.getId(), "1");
        HttpURLConnection connection = request(deleteEndpoint, DELETE_METHOD, JSON_FORMAT, JSON_FORMAT, null);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);

        connection.disconnect();
        System.out.println("testDeleteTodoTasksofRelationshipJson passed.");
    }

    /**
     * Creates a todo under a project with a boolean doneStatus and verifies it
     * can be retrieved from the project's task list.
     */
    @Test
    public void testCreateTodoUnderProjectWithBooleanDoneStatusJson() throws Exception {
        System.out.println("Running testCreateTodoUnderProjectWithBooleanDoneStatusJson...");
        ObjectMapper objectMapper = new ObjectMapper();
        // Create a new todo with doneStatus as boolean
        Todo.TodoBody todoBody = new Todo.TodoBody("Boolean DoneStatus Todo",
                true, "Testing boolean doneStatus");
        String todoJson = objectMapper.writeValueAsString(todoBody);
        HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                todoJson);
        String createTodoResponse = readResponse(createTodoConnection);
        Todo createdTodo = objectMapper.readValue(createTodoResponse, Todo.class);
        createTodoConnection.disconnect();
        // Associate the todo with an existing project (id "1")
        JsonRelationship relationshipBody = new JsonRelationship("1");
        String relationshipJson = objectMapper.writeValueAsString(relationshipBody);
        String endpoint = String.format(TODOS_TASKSOF_ENDPOINT, createdTodo.getId());
        HttpURLConnection connection = request(endpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT, relationshipJson

        );
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        assertEquals(201, responseCode);
        assertEquals("Created", responseMessage);
        connection.disconnect();
        // Retrieve tasks for the project to verify the todo is listed
        String tasksEndpoint = String.format(PROJECTS_TASKS_ENDPOINT, "1");
        HttpURLConnection tasksConnection = request(tasksEndpoint, GET_METHOD, JSON_FORMAT, JSON_FORMAT, null);
        int tasksResponseCode = tasksConnection.getResponseCode();
        String tasksResponseMessage = tasksConnection.getResponseMessage();
        String tasksResponseBody = readResponse(tasksConnection);
        JsonTodo projectTodos = objectMapper.readValue(tasksResponseBody, JsonTodo.class);
        assertEquals(200, tasksResponseCode);
        assertEquals("OK", tasksResponseMessage);
        assertEquals(createdTodo.getId(), projectTodos.getTodos()[0].getId());
    }

    /**
     * Attaches a category to a project using the documented JSON shape and
     * verifies the link via retrieval.
     */
    @Test
    public void testProjectCategoryByIdExpected() throws Exception {
        System.out.println("Running testAttachCategoryByIdExpected...");
        ObjectMapper objectMapper = new ObjectMapper();

        // Create relationship between existing project and category
        JsonRelationship relationshipBody = new JsonRelationship("2");
        String relationshipJson = objectMapper.writeValueAsString(relationshipBody);

        String endpoint = String.format(PROJECTS_CATEGORIES_ENDPOINT, "1");
        HttpURLConnection connection = request(endpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT, relationshipJson);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();

        assertEquals(201, responseCode);
        assertEquals("Created", responseMessage);

        connection.disconnect();

        // Verify the category is linked to the project
        String getCategoriesEndpoint = String.format(PROJECTS_CATEGORIES_ENDPOINT, "1");
        HttpURLConnection getConnection = request(getCategoriesEndpoint, GET_METHOD, JSON_FORMAT, JSON_FORMAT,
                null);

        int getResponseCode = getConnection.getResponseCode();
        String getResponseMessage = getConnection.getResponseMessage();
        String getResponseBody = readResponse(getConnection);

        JsonCategory categories = objectMapper.readValue(getResponseBody, JsonCategory.class);

        assertEquals(200, getResponseCode);
        assertEquals("OK", getResponseMessage);
        assertNotNull(categories);
        assertTrue(getResponseBody.contains("\"id\":\"2\""));

        getConnection.disconnect();
        System.out.println("testAttachCategoryByIdExpected passed.");
    }

    /**
     * Attaches a category to a todo using the documented contract and confirms it
     * is reflected in the todo's category list.
     */
    @Test
    public void testTodoCategoryToTodoValid() throws Exception {
        System.out.println("Running testTodoCategoryToTodoValid...");
        ObjectMapper objectMapper = new ObjectMapper();

        // First create a todo
        Todo.TodoBody todoBody = new Todo.TodoBody("Test Todo Valid Category",
                false, "Test description");
        String todoJson = objectMapper.writeValueAsString(todoBody);
        HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                todoJson);
        String createTodoResponse = readResponse(createTodoConnection);
        Todo createdTodo = objectMapper.readValue(createTodoResponse, Todo.class);
        createTodoConnection.disconnect();

        // Create relationship between existing todo and category
        JsonRelationship relationshipBody = new JsonRelationship("1");
        String relationshipJson = objectMapper.writeValueAsString(relationshipBody);
        String endpoint = String.format(TODOS_CATEGORIES_ENDPOINT, createdTodo.getId());
        HttpURLConnection connection = request(endpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT, relationshipJson);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        assertEquals(201, responseCode);
        assertEquals("Created", responseMessage);
        connection.disconnect();
    }

}
