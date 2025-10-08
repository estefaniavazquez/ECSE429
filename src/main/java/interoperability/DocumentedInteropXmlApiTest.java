package interoperability;

import static general.CommonConstants.CATEGORIES_ENDPOINT;
import static general.CommonConstants.CATEGORIES_PROJECTS_ENDPOINT;
import static general.CommonConstants.CATEGORIES_PROJECTS_ID_ENDPOINT;
import static general.CommonConstants.CATEGORIES_TODOS_ENDPOINT;
import static general.CommonConstants.CATEGORIES_TODOS_ID_ENDPOINT;
import static general.CommonConstants.DELETE_METHOD;
import static general.CommonConstants.GET_METHOD;
import static general.CommonConstants.HEAD_METHOD;
import static general.CommonConstants.POST_METHOD;
import static general.CommonConstants.PROJECTS_CATEGORIES_ENDPOINT;
import static general.CommonConstants.PROJECTS_ENDPOINT;
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

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import categories.XmlCategory;
import general.BaseApiTest;
import projects.XmlProject;

/**
 * Test class for documented interoperability API endpoints using XML format.
 * 
 * This class tests all the documented relationship endpoints between todos,
 * projects,
 * and categories as specified in the API documentation using XML content type.
 * It verifies that all supported HTTP methods (GET, POST, HEAD, DELETE) work
 * correctly for establishing and managing relationships between entities in XML
 * format.
 * 
 * Each test method focuses on a specific relationship endpoint and HTTP method
 * combination,
 * expecting successful responses (200 OK for GET/HEAD, 201 Created for POST,
 * 200 OK for DELETE)
 * when interacting with documented API functionality using XML content type.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DocumentedInteropXmlApiTest extends BaseApiTest {

    /* Test Todo-Category Relationships */

    /**
     * Tests the GET /todos/{id}/categories endpoint with XML format.
     * This test verifies that we can successfully retrieve all categories
     * associated with a specific todo item using XML format. Creates a test todo
     * first,
     * then fetches its categories (should return empty list initially).
     * Expected: 200 OK with empty categories array in XML format.
     */
    @Test
    public void testGetTodoCategoriesXml() throws Exception {
        System.out.println("Running testGetTodoCategoriesXml...");

        // First create a todo
        String todoXml = "<todo><title>Test Todo</title><doneStatus>false</doneStatus><description>Test description</description></todo>";

        HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT,
                todoXml);
        String createTodoResponse = readResponse(createTodoConnection);
        XmlMapper xmlMapper = new XmlMapper();
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
        assertTrue(contentType.contains("application/xml"));
        assertNotNull(categories);
        assertEquals(0, categories.getCategories().length);

        connection.disconnect();
        System.out.println("testGetTodoCategoriesXml passed.");
    }

    /**
     * Tests the POST /todos/{id}/categories endpoint with XML format.
     * This test verifies that we can successfully create a relationship between
     * a todo and a category using XML format. Creates both a todo and category
     * first, then establishes the relationship.
     * Expected: 201 Created when successfully linking category to todo.
     */
    @Test
    public void testPostTodoCategoriesXml() throws Exception {
        System.out.println("Running testPostTodoCategoriesXml...");

        XmlMapper xmlMapper = new XmlMapper();

        // First create a todo
        String todoXml = "<todo><title>Test Todo for Category Link</title><doneStatus>false</doneStatus><description>Testing category association</description></todo>";
        HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT,
                todoXml);
        String createTodoResponse = readResponse(createTodoConnection);
        Todo createdTodo = xmlMapper.readValue(createTodoResponse, Todo.class);
        createTodoConnection.disconnect();

        // Create a category
        String categoryXml = "<category><title>Test Category</title><description>Test category description</description></category>";
        HttpURLConnection createCategoryConnection = request(CATEGORIES_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT,
                categoryXml);
        String createCategoryResponse = readResponse(createCategoryConnection);
        categories.Category createdCategory = xmlMapper.readValue(createCategoryResponse, categories.Category.class);
        String categoryId = createdCategory.getId();
        createCategoryConnection.disconnect();

        // Create relationship
        XmlRelationship relationshipBody = new XmlRelationship(categoryId);
        String relationshipXml = relationshipBody.toStringXmlCategory();

        String endpoint = String.format(TODOS_CATEGORIES_ENDPOINT, createdTodo.getId());
        HttpURLConnection connection = request(endpoint, POST_METHOD, XML_FORMAT, XML_FORMAT, relationshipXml);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String contentType = connection.getContentType();

        assertEquals(201, responseCode);
        assertEquals("Created", responseMessage);
        assertTrue(contentType.contains("application/xml"));

        connection.disconnect();
        System.out.println("testPostTodoCategoriesXml passed.");
    }

    /**
     * Tests the HEAD /todos/{id}/categories endpoint with XML format.
     * This test verifies that we can successfully retrieve headers for categories
     * associated with a specific todo item using XML format. Creates a test todo
     * first,
     * then fetches headers for its categories.
     * Expected: 200 OK with appropriate headers.
     */
    @Test
    public void testHeadTodoCategoriesXml() throws Exception {
        System.out.println("Running testHeadTodoCategoriesXml...");

        XmlMapper xmlMapper = new XmlMapper();

        // First create a todo
        String todoXml = "<todo><title>Test Todo for HEAD</title><doneStatus>false</doneStatus><description>Test HEAD request</description></todo>";
        HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT,
                todoXml);
        String createTodoResponse = readResponse(createTodoConnection);
        Todo createdTodo = xmlMapper.readValue(createTodoResponse, Todo.class);
        createTodoConnection.disconnect();

        // HEAD request for categories
        String endpoint = String.format(TODOS_CATEGORIES_ENDPOINT, createdTodo.getId());
        HttpURLConnection connection = request(endpoint, HEAD_METHOD, XML_FORMAT, XML_FORMAT, null);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String contentType = connection.getContentType();

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertTrue(contentType.contains("application/xml"));

        connection.disconnect();
        System.out.println("testHeadTodoCategoriesXml passed.");
    }

    /**
     * Tests the DELETE /todos/{id}/categories/{id} endpoint with XML format.
     * This test verifies that we can successfully delete a relationship between
     * a todo and a category using XML format. Creates both entities and
     * relationship first, then deletes the relationship.
     * Expected: 200 OK when successfully deleting the relationship.
     */
    @Test
    public void testDeleteTodoCategoriesIdXml() throws Exception {
        System.out.println("Running testDeleteTodoCategoriesIdXml...");

        XmlMapper xmlMapper = new XmlMapper();

        // First create a todo
        String todoXml = "<todo><title>Test Todo for Delete</title><doneStatus>false</doneStatus><description>Test delete relationship</description></todo>";
        HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT,
                todoXml);
        String createTodoResponse = readResponse(createTodoConnection);
        Todo createdTodo = xmlMapper.readValue(createTodoResponse, Todo.class);
        createTodoConnection.disconnect();

        // Create a category
        String categoryXml = "<category><title>Test Category for Delete</title><description>Test category for deletion</description></category>";
        HttpURLConnection createCategoryConnection = request(CATEGORIES_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT,
                categoryXml);
        String createCategoryResponse = readResponse(createCategoryConnection);
        categories.Category createdCategory = xmlMapper.readValue(createCategoryResponse, categories.Category.class);
        String categoryId = createdCategory.getId();
        createCategoryConnection.disconnect();

        // Create relationship
        XmlRelationship relationshipBody = new XmlRelationship(categoryId);
        String relationshipXml = relationshipBody.toStringXmlCategory();

        String createRelationshipEndpoint = String.format(TODOS_CATEGORIES_ENDPOINT, createdTodo.getId());
        HttpURLConnection createRelationshipConnection = request(createRelationshipEndpoint, POST_METHOD, XML_FORMAT,
                XML_FORMAT, relationshipXml);
        createRelationshipConnection.disconnect();

        // Delete relationship
        String deleteEndpoint = String.format(TODOS_CATEGORIES_ID_ENDPOINT, createdTodo.getId(), categoryId);
        HttpURLConnection connection = request(deleteEndpoint, DELETE_METHOD, XML_FORMAT, XML_FORMAT, null);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);

        connection.disconnect();
        System.out.println("testDeleteTodoCategoriesIdXml passed.");
    }

    /* Test Todo-Project Relationships (tasksof) */

    /**
     * Tests the GET /todos/{id}/tasksof endpoint with XML format.
     * This test verifies that we can successfully retrieve all projects
     * associated with a specific todo item via the tasksof relationship using XML
     * format.
     * Expected: 200 OK with empty projects array in XML format.
     */
    @Test
    public void testGetTodoTasksofXml() throws Exception {
        System.out.println("Running testGetTodoTasksofXml...");

        XmlMapper xmlMapper = new XmlMapper();

        // First create a todo
        String todoXml = "<todo><title>Test Todo for Tasksof</title><doneStatus>false</doneStatus><description>Test tasksof relationship</description></todo>";
        HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT,
                todoXml);
        String createTodoResponse = readResponse(createTodoConnection);
        Todo createdTodo = xmlMapper.readValue(createTodoResponse, Todo.class);
        createTodoConnection.disconnect();

        // Get projects for this todo via tasksof
        String endpoint = String.format(TODOS_TASKSOF_ENDPOINT, createdTodo.getId());
        HttpURLConnection connection = request(endpoint, GET_METHOD, XML_FORMAT, XML_FORMAT, null);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String contentType = connection.getContentType();
        String responseBody = readResponse(connection);

        XmlProject projects = xmlMapper.readValue(responseBody, XmlProject.class);

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertTrue(contentType.contains("application/xml"));
        assertNotNull(projects);
        assertEquals(0, projects.getProjects().length);

        connection.disconnect();
        System.out.println("testGetTodoTasksofXml passed.");
    }

    /**
     * Tests the POST /todos/{id}/tasksof endpoint with XML format.
     * This test verifies that we can successfully create a tasksof relationship
     * between a todo and a project using XML format. Creates both entities first,
     * then establishes the relationship.
     * Expected: 201 Created when successfully linking project to todo.
     */
    @Test
    public void testPostTodoTasksofXml() throws Exception {
        System.out.println("Running testPostTodoTasksofXml...");

        XmlMapper xmlMapper = new XmlMapper();

        // First create a todo
        String todoXml = "<todo><title>Test Todo for Tasksof Link</title><doneStatus>false</doneStatus><description>Testing tasksof association</description></todo>";
        HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT,
                todoXml);
        String createTodoResponse = readResponse(createTodoConnection);
        Todo createdTodo = xmlMapper.readValue(createTodoResponse, Todo.class);
        createTodoConnection.disconnect();

        // Create a project
        String projectXml = "<project><title>Test Project</title><completed>false</completed><active>true</active><description>Test project description</description></project>";
        HttpURLConnection createProjectConnection = request(PROJECTS_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT,
                projectXml);
        String createProjectResponse = readResponse(createProjectConnection);
        projects.Project createdProject = xmlMapper.readValue(createProjectResponse, projects.Project.class);
        String projectId = createdProject.getId();
        createProjectConnection.disconnect();

        // Create relationship
        XmlRelationship relationshipBody = new XmlRelationship(projectId);
        String relationshipXml = relationshipBody.toStringXml();

        String endpoint = String.format(TODOS_TASKSOF_ENDPOINT, createdTodo.getId());
        HttpURLConnection connection = request(endpoint, POST_METHOD, XML_FORMAT, XML_FORMAT, relationshipXml);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String contentType = connection.getContentType();

        assertEquals(201, responseCode);
        assertEquals("Created", responseMessage);
        assertTrue(contentType.contains("application/xml"));

        connection.disconnect();
        System.out.println("testPostTodoTasksofXml passed.");
    }

    /**
     * Tests the DELETE /todos/{id}/tasksof/{id} endpoint with XML format.
     * This test verifies that we can successfully delete a tasksof relationship
     * between a todo and a project using XML format. Creates both entities and
     * relationship first, then deletes the relationship.
     * Expected: 200 OK when successfully deleting the relationship.
     */
    @Test
    public void testDeleteTodoTasksofIdXml() throws Exception {
        System.out.println("Running testDeleteTodoTasksofIdXml...");

        XmlMapper xmlMapper = new XmlMapper();

        // First create a todo
        String todoXml = "<todo><title>Test Todo for Tasksof Delete</title><doneStatus>false</doneStatus><description>Test delete tasksof relationship</description></todo>";
        HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT,
                todoXml);
        String createTodoResponse = readResponse(createTodoConnection);
        Todo createdTodo = xmlMapper.readValue(createTodoResponse, Todo.class);
        createTodoConnection.disconnect();

        // Create a project
        String projectXml = "<project><title>Test Project for Delete</title><completed>false</completed><active>true</active><description>Test project for deletion</description></project>";
        HttpURLConnection createProjectConnection = request(PROJECTS_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT,
                projectXml);
        String createProjectResponse = readResponse(createProjectConnection);
        projects.Project createdProject = xmlMapper.readValue(createProjectResponse, projects.Project.class);
        String projectId = createdProject.getId();
        createProjectConnection.disconnect();

        // Create relationship
        XmlRelationship relationshipBody = new XmlRelationship(projectId);
        String relationshipXml = relationshipBody.toStringXml();

        String createRelationshipEndpoint = String.format(TODOS_TASKSOF_ENDPOINT, createdTodo.getId());
        HttpURLConnection createRelationshipConnection = request(createRelationshipEndpoint, POST_METHOD, XML_FORMAT,
                XML_FORMAT, relationshipXml);
        createRelationshipConnection.disconnect();

        // Delete relationship
        String deleteEndpoint = String.format(TODOS_TASKSOF_ID_ENDPOINT, createdTodo.getId(), projectId);
        HttpURLConnection connection = request(deleteEndpoint, DELETE_METHOD, XML_FORMAT, XML_FORMAT, null);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);

        connection.disconnect();
        System.out.println("testDeleteTodoTasksofIdXml passed.");
    }

    /* Test Project-Category Relationships */

    /**
     * Tests the GET /projects/{id}/categories endpoint with XML format.
     * This test verifies that we can successfully retrieve all categories
     * associated with a specific project using XML format.
     * Expected: 200 OK with empty categories array in XML format.
     */
    @Test
    public void testGetProjectCategoriesXml() throws Exception {
        System.out.println("Running testGetProjectCategoriesXml...");

        XmlMapper xmlMapper = new XmlMapper();

        // First create a project
        String projectXml = "<project><title>Test Project for Categories</title><completed>false</completed><active>true</active><description>Test project description</description></project>";
        HttpURLConnection createProjectConnection = request(PROJECTS_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT,
                projectXml);
        String createProjectResponse = readResponse(createProjectConnection);
        projects.Project createdProject = xmlMapper.readValue(createProjectResponse, projects.Project.class);
        String projectId = createdProject.getId();
        createProjectConnection.disconnect();

        // Get categories for this project
        String endpoint = String.format(PROJECTS_CATEGORIES_ENDPOINT, projectId);
        HttpURLConnection connection = request(endpoint, GET_METHOD, XML_FORMAT, XML_FORMAT, null);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String contentType = connection.getContentType();
        String responseBody = readResponse(connection);

        XmlCategory categories = xmlMapper.readValue(responseBody, XmlCategory.class);

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertTrue(contentType.contains("application/xml"));
        assertNotNull(categories);
        assertEquals(0, categories.getCategories().length);

        connection.disconnect();
        System.out.println("testGetProjectCategoriesXml passed.");
    }

    /**
     * Tests the POST /projects/{id}/categories endpoint with XML format.
     * This test verifies that we can successfully create a relationship between
     * a project and a category using XML format.
     * Expected: 201 Created when successfully linking category to project.
     */
    @Test
    public void testPostProjectCategoriesXml() throws Exception {
        System.out.println("Running testPostProjectCategoriesXml...");

        XmlMapper xmlMapper = new XmlMapper();

        // First create a project
        String projectXml = "<project><title>Test Project for Category Link</title><completed>false</completed><active>true</active><description>Testing category association</description></project>";
        HttpURLConnection createProjectConnection = request(PROJECTS_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT,
                projectXml);
        String createProjectResponse = readResponse(createProjectConnection);
        projects.Project createdProject = xmlMapper.readValue(createProjectResponse, projects.Project.class);
        String projectId = createdProject.getId();
        createProjectConnection.disconnect();

        // Create a category
        String categoryXml = "<category><title>Test Category for Project</title><description>Test category description</description></category>";
        HttpURLConnection createCategoryConnection = request(CATEGORIES_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT,
                categoryXml);
        String createCategoryResponse = readResponse(createCategoryConnection);
        categories.Category createdCategory = xmlMapper.readValue(createCategoryResponse, categories.Category.class);
        String categoryId = createdCategory.getId();
        createCategoryConnection.disconnect();

        // Create relationship
        XmlRelationship relationshipBody = new XmlRelationship(categoryId);
        String relationshipXml = relationshipBody.toStringXmlCategory();

        String endpoint = String.format(PROJECTS_CATEGORIES_ENDPOINT, projectId);
        HttpURLConnection connection = request(endpoint, POST_METHOD, XML_FORMAT, XML_FORMAT, relationshipXml);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String contentType = connection.getContentType();

        assertEquals(201, responseCode);
        assertEquals("Created", responseMessage);
        assertTrue(contentType.contains("application/xml"));

        connection.disconnect();
        System.out.println("testPostProjectCategoriesXml passed.");
    }

    /* Test Project-Todo Relationships (tasks) */

    /**
     * Tests the GET /projects/{id}/tasks endpoint with XML format.
     * This test verifies that we can successfully retrieve all todos
     * associated with a specific project via the tasks relationship using XML
     * format.
     * Expected: 200 OK with empty todos array in XML format.
     */
    @Test
    public void testGetProjectTasksXml() throws Exception {
        System.out.println("Running testGetProjectTasksXml...");

        XmlMapper xmlMapper = new XmlMapper();

        // First create a project
        String projectXml = "<project><title>Test Project for Tasks</title><completed>false</completed><active>true</active><description>Test tasks relationship</description></project>";
        HttpURLConnection createProjectConnection = request(PROJECTS_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT,
                projectXml);
        String createProjectResponse = readResponse(createProjectConnection);
        projects.Project createdProject = xmlMapper.readValue(createProjectResponse, projects.Project.class);
        String projectId = createdProject.getId();
        createProjectConnection.disconnect();

        // Get tasks for this project
        String endpoint = String.format(PROJECTS_TASKS_ENDPOINT, projectId);
        HttpURLConnection connection = request(endpoint, GET_METHOD, XML_FORMAT, XML_FORMAT, null);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String contentType = connection.getContentType();
        String responseBody = readResponse(connection);

        XmlTodo todos = xmlMapper.readValue(responseBody, XmlTodo.class);

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertTrue(contentType.contains("application/xml"));
        assertNotNull(todos);
        assertEquals(0, todos.getTodos().length);

        connection.disconnect();
        System.out.println("testGetProjectTasksXml passed.");
    }

    /**
     * Tests the POST /projects/{id}/tasks endpoint with XML format.
     * This test verifies that we can successfully create a tasks relationship
     * between a project and a todo using XML format.
     * Expected: 201 Created when successfully linking todo to project.
     */
    @Test
    public void testPostProjectTasksXml() throws Exception {
        System.out.println("Running testPostProjectTasksXml...");

        XmlMapper xmlMapper = new XmlMapper();

        // First create a project
        String projectXml = "<project><title>Test Project for Task Link</title><completed>false</completed><active>true</active><description>Testing task association</description></project>";
        HttpURLConnection createProjectConnection = request(PROJECTS_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT,
                projectXml);
        String createProjectResponse = readResponse(createProjectConnection);
        projects.Project createdProject = xmlMapper.readValue(createProjectResponse, projects.Project.class);
        String projectId = createdProject.getId();
        createProjectConnection.disconnect();

        // Create a todo
        String todoXml = "<todo><title>Test Todo for Project</title><doneStatus>false</doneStatus><description>Test todo description</description></todo>";
        HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT,
                todoXml);
        String createTodoResponse = readResponse(createTodoConnection);
        Todo createdTodo = xmlMapper.readValue(createTodoResponse, Todo.class);
        createTodoConnection.disconnect();

        // Create relationship (need to use todo format for tasks relationship)
        String relationshipXml = "<todo><id>" + createdTodo.getId() + "</id></todo>";

        String endpoint = String.format(PROJECTS_TASKS_ENDPOINT, projectId);
        HttpURLConnection connection = request(endpoint, POST_METHOD, XML_FORMAT, XML_FORMAT, relationshipXml);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String contentType = connection.getContentType();

        assertEquals(201, responseCode);
        assertEquals("Created", responseMessage);
        assertTrue(contentType.contains("application/xml"));

        connection.disconnect();
        System.out.println("testPostProjectTasksXml passed.");
    }

    /**
     * Tests the DELETE /projects/{id}/tasks/{id} endpoint with XML format.
     * This test verifies that we can successfully delete a tasks relationship
     * between a project and a todo using XML format.
     * Expected: 200 OK when successfully deleting the relationship.
     */
    @Test
    public void testDeleteProjectTasksIdXml() throws Exception {
        System.out.println("Running testDeleteProjectTasksIdXml...");

        XmlMapper xmlMapper = new XmlMapper();

        // First create a project
        String projectXml = "<project><title>Test Project for Task Delete</title><completed>false</completed><active>true</active><description>Test delete task relationship</description></project>";
        HttpURLConnection createProjectConnection = request(PROJECTS_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT,
                projectXml);
        String createProjectResponse = readResponse(createProjectConnection);
        projects.Project createdProject = xmlMapper.readValue(createProjectResponse, projects.Project.class);
        String projectId = createdProject.getId();
        createProjectConnection.disconnect();

        // Create a todo
        String todoXml = "<todo><title>Test Todo for Project Delete</title><doneStatus>false</doneStatus><description>Test todo for deletion</description></todo>";
        HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT,
                todoXml);
        String createTodoResponse = readResponse(createTodoConnection);
        Todo createdTodo = xmlMapper.readValue(createTodoResponse, Todo.class);
        createTodoConnection.disconnect();

        // Create relationship
        String relationshipXml = "<todo><id>" + createdTodo.getId() + "</id></todo>";

        String createRelationshipEndpoint = String.format(PROJECTS_TASKS_ENDPOINT, projectId);
        HttpURLConnection createRelationshipConnection = request(createRelationshipEndpoint, POST_METHOD, XML_FORMAT,
                XML_FORMAT, relationshipXml);
        createRelationshipConnection.disconnect();

        // Delete relationship
        String deleteEndpoint = String.format(PROJECTS_TASKS_ID_ENDPOINT, projectId, createdTodo.getId());
        HttpURLConnection connection = request(deleteEndpoint, DELETE_METHOD, XML_FORMAT, XML_FORMAT, null);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);

        connection.disconnect();
        System.out.println("testDeleteProjectTasksIdXml passed.");
    }

    /* Test Category-Project Relationships */

    /**
     * Tests the GET /categories/{id}/projects endpoint with XML format.
     * This test verifies that we can successfully retrieve all projects
     * associated with a specific category using XML format.
     * Expected: 200 OK with empty projects array in XML format.
     */
    @Test
    public void testGetCategoryProjectsXml() throws Exception {
        System.out.println("Running testGetCategoryProjectsXml...");

        XmlMapper xmlMapper = new XmlMapper();

        // First create a category
        String categoryXml = "<category><title>Test Category for Projects</title><description>Test category description</description></category>";
        HttpURLConnection createCategoryConnection = request(CATEGORIES_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT,
                categoryXml);
        String createCategoryResponse = readResponse(createCategoryConnection);
        categories.Category createdCategory = xmlMapper.readValue(createCategoryResponse, categories.Category.class);
        String categoryId = createdCategory.getId();
        createCategoryConnection.disconnect();

        // Get projects for this category
        String endpoint = String.format(CATEGORIES_PROJECTS_ENDPOINT, categoryId);
        HttpURLConnection connection = request(endpoint, GET_METHOD, XML_FORMAT, XML_FORMAT, null);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String contentType = connection.getContentType();
        String responseBody = readResponse(connection);

        XmlProject projects = xmlMapper.readValue(responseBody, XmlProject.class);

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertTrue(contentType.contains("application/xml"));
        assertNotNull(projects);
        assertEquals(0, projects.getProjects().length);

        connection.disconnect();
        System.out.println("testGetCategoryProjectsXml passed.");
    }

    /**
     * Tests the POST /categories/{id}/projects endpoint with XML format.
     * This test verifies that we can successfully create a relationship between
     * a category and a project using XML format.
     * Expected: 201 Created when successfully linking project to category.
     */
    @Test
    public void testPostCategoryProjectsXml() throws Exception {
        System.out.println("Running testPostCategoryProjectsXml...");

        XmlMapper xmlMapper = new XmlMapper();

        // First create a category
        String categoryXml = "<category><title>Test Category for Project Link</title><description>Testing project association</description></category>";
        HttpURLConnection createCategoryConnection = request(CATEGORIES_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT,
                categoryXml);
        String createCategoryResponse = readResponse(createCategoryConnection);
        categories.Category createdCategory = xmlMapper.readValue(createCategoryResponse, categories.Category.class);
        String categoryId = createdCategory.getId();
        createCategoryConnection.disconnect();

        // Create a project
        String projectXml = "<project><title>Test Project for Category</title><completed>false</completed><active>true</active><description>Test project description</description></project>";
        HttpURLConnection createProjectConnection = request(PROJECTS_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT,
                projectXml);
        String createProjectResponse = readResponse(createProjectConnection);
        projects.Project createdProject = xmlMapper.readValue(createProjectResponse, projects.Project.class);
        String projectId = createdProject.getId();
        createProjectConnection.disconnect();

        // Create relationship
        XmlRelationship relationshipBody = new XmlRelationship(projectId);
        String relationshipXml = relationshipBody.toStringXml();

        String endpoint = String.format(CATEGORIES_PROJECTS_ENDPOINT, categoryId);
        HttpURLConnection connection = request(endpoint, POST_METHOD, XML_FORMAT, XML_FORMAT, relationshipXml);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String contentType = connection.getContentType();

        assertEquals(201, responseCode);
        assertEquals("Created", responseMessage);
        assertTrue(contentType.contains("application/xml"));

        connection.disconnect();
        System.out.println("testPostCategoryProjectsXml passed.");
    }

    /**
     * Tests the DELETE /categories/{id}/projects/{id} endpoint with XML format.
     * This test verifies that we can successfully delete a relationship between
     * a category and a project using XML format.
     * Expected: 200 OK when successfully deleting the relationship.
     */
    @Test
    public void testDeleteCategoryProjectsIdXml() throws Exception {
        System.out.println("Running testDeleteCategoryProjectsIdXml...");

        XmlMapper xmlMapper = new XmlMapper();

        // First create a category
        String categoryXml = "<category><title>Test Category for Project Delete</title><description>Test delete project relationship</description></category>";
        HttpURLConnection createCategoryConnection = request(CATEGORIES_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT,
                categoryXml);
        String createCategoryResponse = readResponse(createCategoryConnection);
        categories.Category createdCategory = xmlMapper.readValue(createCategoryResponse, categories.Category.class);
        String categoryId = createdCategory.getId();
        createCategoryConnection.disconnect();

        // Create a project
        String projectXml = "<project><title>Test Project for Category Delete</title><completed>false</completed><active>true</active><description>Test project for deletion</description></project>";
        HttpURLConnection createProjectConnection = request(PROJECTS_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT,
                projectXml);
        String createProjectResponse = readResponse(createProjectConnection);
        projects.Project createdProject = xmlMapper.readValue(createProjectResponse, projects.Project.class);
        String projectId = createdProject.getId();
        createProjectConnection.disconnect();

        // Create relationship
        XmlRelationship relationshipBody = new XmlRelationship(projectId);
        String relationshipXml = relationshipBody.toStringXml();

        String createRelationshipEndpoint = String.format(CATEGORIES_PROJECTS_ENDPOINT, categoryId);
        HttpURLConnection createRelationshipConnection = request(createRelationshipEndpoint, POST_METHOD, XML_FORMAT,
                XML_FORMAT, relationshipXml);
        createRelationshipConnection.disconnect();

        // Delete relationship
        String deleteEndpoint = String.format(CATEGORIES_PROJECTS_ID_ENDPOINT, categoryId, projectId);
        HttpURLConnection connection = request(deleteEndpoint, DELETE_METHOD, XML_FORMAT, XML_FORMAT, null);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);

        connection.disconnect();
        System.out.println("testDeleteCategoryProjectsIdXml passed.");
    }

    /* Test Category-Todo Relationships */

    /**
     * Tests the GET /categories/{id}/todos endpoint with XML format.
     * This test verifies that we can successfully retrieve all todos
     * associated with a specific category using XML format.
     * Expected: 200 OK with empty todos array in XML format.
     */
    @Test
    public void testGetCategoryTodosXml() throws Exception {
        System.out.println("Running testGetCategoryTodosXml...");

        XmlMapper xmlMapper = new XmlMapper();

        // First create a category
        String categoryXml = "<category><title>Test Category for Todos</title><description>Test category description</description></category>";
        HttpURLConnection createCategoryConnection = request(CATEGORIES_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT,
                categoryXml);
        String createCategoryResponse = readResponse(createCategoryConnection);
        categories.Category createdCategory = xmlMapper.readValue(createCategoryResponse, categories.Category.class);
        String categoryId = createdCategory.getId();
        createCategoryConnection.disconnect();

        // Get todos for this category
        String endpoint = String.format(CATEGORIES_TODOS_ENDPOINT, categoryId);
        HttpURLConnection connection = request(endpoint, GET_METHOD, XML_FORMAT, XML_FORMAT, null);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String contentType = connection.getContentType();
        String responseBody = readResponse(connection);

        XmlTodo todos = xmlMapper.readValue(responseBody, XmlTodo.class);

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertTrue(contentType.contains("application/xml"));
        assertNotNull(todos);
        assertEquals(0, todos.getTodos().length);

        connection.disconnect();
        System.out.println("testGetCategoryTodosXml passed.");
    }

    /**
     * Tests the POST /categories/{id}/todos endpoint with XML format.
     * This test verifies that we can successfully create a relationship between
     * a category and a todo using XML format.
     * Expected: 201 Created when successfully linking todo to category.
     */
    @Test
    public void testPostCategoryTodosXml() throws Exception {
        System.out.println("Running testPostCategoryTodosXml...");

        XmlMapper xmlMapper = new XmlMapper();

        // First create a category
        String categoryXml = "<category><title>Test Category for Todo Link</title><description>Testing todo association</description></category>";
        HttpURLConnection createCategoryConnection = request(CATEGORIES_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT,
                categoryXml);
        String createCategoryResponse = readResponse(createCategoryConnection);
        categories.Category createdCategory = xmlMapper.readValue(createCategoryResponse, categories.Category.class);
        String categoryId = createdCategory.getId();
        createCategoryConnection.disconnect();

        // Create a todo
        String todoXml = "<todo><title>Test Todo for Category</title><doneStatus>false</doneStatus><description>Test todo description</description></todo>";
        HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT,
                todoXml);
        String createTodoResponse = readResponse(createTodoConnection);
        Todo createdTodo = xmlMapper.readValue(createTodoResponse, Todo.class);
        createTodoConnection.disconnect();

        // Create relationship (need to use todo format)
        String relationshipXml = "<todo><id>" + createdTodo.getId() + "</id></todo>";

        String endpoint = String.format(CATEGORIES_TODOS_ENDPOINT, categoryId);
        HttpURLConnection connection = request(endpoint, POST_METHOD, XML_FORMAT, XML_FORMAT, relationshipXml);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String contentType = connection.getContentType();

        assertEquals(201, responseCode);
        assertEquals("Created", responseMessage);
        assertTrue(contentType.contains("application/xml"));

        connection.disconnect();
        System.out.println("testPostCategoryTodosXml passed.");
    }

    /**
     * Tests the DELETE /categories/{id}/todos/{id} endpoint with XML format.
     * This test verifies that we can successfully delete a relationship between
     * a category and a todo using XML format.
     * Expected: 200 OK when successfully deleting the relationship.
     */
    @Test
    public void testDeleteCategoryTodosIdXml() throws Exception {
        System.out.println("Running testDeleteCategoryTodosIdXml...");

        XmlMapper xmlMapper = new XmlMapper();

        // First create a category
        String categoryXml = "<category><title>Test Category for Todo Delete</title><description>Test delete todo relationship</description></category>";
        HttpURLConnection createCategoryConnection = request(CATEGORIES_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT,
                categoryXml);
        String createCategoryResponse = readResponse(createCategoryConnection);
        categories.Category createdCategory = xmlMapper.readValue(createCategoryResponse, categories.Category.class);
        String categoryId = createdCategory.getId();
        createCategoryConnection.disconnect();

        // Create a todo
        String todoXml = "<todo><title>Test Todo for Category Delete</title><doneStatus>false</doneStatus><description>Test todo for deletion</description></todo>";
        HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT,
                todoXml);
        String createTodoResponse = readResponse(createTodoConnection);
        Todo createdTodo = xmlMapper.readValue(createTodoResponse, Todo.class);
        createTodoConnection.disconnect();

        // Create relationship
        String relationshipXml = "<todo><id>" + createdTodo.getId() + "</id></todo>";

        String createRelationshipEndpoint = String.format(CATEGORIES_TODOS_ENDPOINT, categoryId);
        HttpURLConnection createRelationshipConnection = request(createRelationshipEndpoint, POST_METHOD, XML_FORMAT,
                XML_FORMAT, relationshipXml);
        createRelationshipConnection.disconnect();

        // Delete relationship
        String deleteEndpoint = String.format(CATEGORIES_TODOS_ID_ENDPOINT, categoryId, createdTodo.getId());
        HttpURLConnection connection = request(deleteEndpoint, DELETE_METHOD, XML_FORMAT, XML_FORMAT, null);

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);

        connection.disconnect();
        System.out.println("testDeleteCategoryTodosIdXml passed.");
    }
}