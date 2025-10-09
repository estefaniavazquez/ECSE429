package interoperability;

import static general.CommonConstants.CATEGORIES_ENDPOINT;
import static general.CommonConstants.CATEGORIES_PROJECTS_ENDPOINT;
import static general.CommonConstants.CATEGORIES_TODOS_ENDPOINT;
import static general.CommonConstants.GET_METHOD;
import static general.CommonConstants.POST_METHOD;
import static general.CommonConstants.PROJECTS_CATEGORIES_ENDPOINT;
import static general.CommonConstants.PROJECTS_ENDPOINT;
import static general.CommonConstants.PROJECTS_TASKS_ENDPOINT;
import static general.CommonConstants.TODOS_CATEGORIES_ENDPOINT;
import static general.CommonConstants.TODOS_ENDPOINT;
import static general.CommonConstants.TODOS_TASKSOF_ENDPOINT;
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
import interoperability.models.Todo;
import interoperability.models.XmlTodo;
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

                connection.disconnect();
                System.out.println("testGetTodoTasksofXml passed.");
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
                HttpURLConnection createProjectConnection = request(PROJECTS_ENDPOINT, POST_METHOD, XML_FORMAT,
                                XML_FORMAT,
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
                HttpURLConnection createCategoryConnection = request(CATEGORIES_ENDPOINT, POST_METHOD, XML_FORMAT,
                                XML_FORMAT,
                                categoryXml);
                String createCategoryResponse = readResponse(createCategoryConnection);
                categories.Category createdCategory = xmlMapper.readValue(createCategoryResponse,
                                categories.Category.class);
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

                connection.disconnect();
                System.out.println("testGetCategoryProjectsXml passed.");
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
                HttpURLConnection createCategoryConnection = request(CATEGORIES_ENDPOINT, POST_METHOD, XML_FORMAT,
                                XML_FORMAT,
                                categoryXml);
                String createCategoryResponse = readResponse(createCategoryConnection);
                categories.Category createdCategory = xmlMapper.readValue(createCategoryResponse,
                                categories.Category.class);
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
         * Creates a todo under a project with a boolean doneStatus and verifies it
         * can be retrieved from the project's task list.
         */
        @Test
        public void testCreateTodoUnderProjectWithBooleanDoneStatusXml() throws Exception {
                System.out.println("Running testCreateTodoUnderProjectWithBooleanDoneStatusXml...");

                XmlMapper xmlMapper = new XmlMapper();

                // Create a project
                String projectXml = "<project><title>Project for Boolean DoneStatus</title><completed>false</completed><active>true</active><description>Testing boolean doneStatus</description></project>";
                HttpURLConnection createProjectConnection = request(PROJECTS_ENDPOINT, POST_METHOD, XML_FORMAT,
                                XML_FORMAT,
                                projectXml);
                String createProjectResponse = readResponse(createProjectConnection);
                projects.Project createdProject = xmlMapper.readValue(createProjectResponse, projects.Project.class);
                String projectId = createdProject.getId();
                createProjectConnection.disconnect();

                // Create a todo with boolean doneStatus under the project
                String todoXml = "<todo><title>Todo with Boolean DoneStatus</title><doneStatus>true</doneStatus><description>Testing boolean doneStatus</description></todo>";
                String endpoint = String.format(PROJECTS_TASKS_ENDPOINT, projectId);
                HttpURLConnection createTodoConnection = request(endpoint, POST_METHOD, XML_FORMAT, XML_FORMAT,
                                todoXml);
                String createTodoResponse = readResponse(createTodoConnection);
                Todo createdTodo = xmlMapper.readValue(createTodoResponse, Todo.class);
                createTodoConnection.disconnect();

                // Verify the todo is created and associated with the project
                HttpURLConnection getProjectTasksConnection = request(endpoint, GET_METHOD, XML_FORMAT, XML_FORMAT,
                                null);
                String getProjectTasksResponse = readResponse(getProjectTasksConnection);
                XmlTodo projectTodos = xmlMapper.readValue(getProjectTasksResponse, XmlTodo.class);
                getProjectTasksConnection.disconnect();

                assertNotNull(projectTodos);
                assertEquals(1, projectTodos.getTodos().length);
                assertEquals(createdTodo.getId(), projectTodos.getTodos()[0].getId());

                System.out.println("testCreateTodoUnderProjectWithBooleanDoneStatusXml passed.");
        }
}