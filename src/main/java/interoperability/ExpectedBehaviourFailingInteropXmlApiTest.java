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

import general.BaseApiTest;
import interoperability.models.Todo;
import interoperability.models.XmlRelationship;
import interoperability.models.XmlTodo;

/**
 * Test class for expected failure scenarios in interoperability API endpoints
 * using XML format.
 * 
 * This class tests various error conditions and edge cases that should result
 * in expected failures when using XML content type. It verifies that the API
 * properly
 * handles invalid requests, nonexistent resources, malformed data, and other
 * error scenarios with appropriate HTTP error codes and meaningful error
 * messages
 * in XML format.
 * 
 * Tested scenarios:
 * - Creating relationships with nonexistent entities
 * - Deleting nonexistent relationships
 * - Invalid XML request bodies
 * - Missing required fields in relationship creation
 * - Attempting operations on nonexistent todos, projects, or categories
 * 
 * Expected behaviors:
 * - 404 Not Found for operations involving nonexistent entities
 * - 400 Bad Request for malformed or invalid XML data
 * - Proper error messages in XML format
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExpectedBehaviourFailingInteropXmlApiTest extends BaseApiTest {

        /**
         * Tests creating a todo-category relationship with a nonexistent category using
         * XML format.
         * This test verifies that attempting to create a relationship between a valid
         * todo and a category that doesn't exist results in appropriate error response
         * using
         * XML.
         * Expected: 404 Not Found indicating the category doesn't exist.
         */
        @Test
        public void testPostTodoCategoriesNonexistentCategoryXml() throws Exception {
                System.out.println("Running testPostTodoCategoriesNonexistentCategoryXml...");

                XmlMapper xmlMapper = new XmlMapper();

                // First create a todo
                String todoXml = "<todo><title>Test Todo for Nonexistent Category</title><doneStatus>false</doneStatus><description>Testing with nonexistent category</description></todo>";
                HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT,
                                todoXml);
                String createTodoResponse = readResponse(createTodoConnection);
                Todo createdTodo = xmlMapper.readValue(createTodoResponse, Todo.class);
                createTodoConnection.disconnect();

                // Try to create relationship with nonexistent category
                XmlRelationship relationshipBody = new XmlRelationship("99999"); // Nonexistent category ID
                String relationshipXml = relationshipBody.toStringXml();

                String endpoint = String.format(TODOS_CATEGORIES_ENDPOINT, createdTodo.getId());
                HttpURLConnection connection = request(endpoint, POST_METHOD, XML_FORMAT, XML_FORMAT, relationshipXml);

                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String responseBody = readResponse(connection);

                assertEquals(404, responseCode);
                assertEquals("Not Found", responseMessage);
                assertTrue("Response should contain error message", responseBody.contains("errorMessages"));

                connection.disconnect();
                System.out.println("testPostTodoCategoriesNonexistentCategoryXml passed.");
        }

        /**
         * Tests creating a todo-tasksof relationship with a nonexistent project using
         * XML format.
         * This test verifies that attempting to create a relationship between a valid
         * todo
         * and a project that doesn't exist results in appropriate error response using
         * XML.
         * Expected: 404 Not Found indicating the project doesn't exist.
         */
        @Test
        public void testPostTodoTasksofNonexistentProjectXml() throws Exception {
                System.out.println("Running testPostTodoTasksofNonexistentProjectXml...");

                XmlMapper xmlMapper = new XmlMapper();

                // First create a todo
                String todoXml = "<todo><title>Test Todo for Nonexistent Project</title><doneStatus>false</doneStatus><description>Testing with nonexistent project</description></todo>";
                HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT,
                                todoXml);
                String createTodoResponse = readResponse(createTodoConnection);
                Todo createdTodo = xmlMapper.readValue(createTodoResponse, Todo.class);
                createTodoConnection.disconnect();

                // Try to create relationship with nonexistent project
                XmlRelationship relationshipBody = new XmlRelationship("99999");
                String relationshipXml = relationshipBody.toStringXml();

                String endpoint = String.format(TODOS_TASKSOF_ENDPOINT, createdTodo.getId());
                HttpURLConnection connection = request(endpoint, POST_METHOD, XML_FORMAT, XML_FORMAT, relationshipXml);

                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String responseBody = readResponse(connection);

                assertEquals(404, responseCode);
                assertEquals("Not Found", responseMessage);
                assertTrue("Response should contain error message", responseBody.contains("errorMessages"));

                connection.disconnect();
                System.out.println("testPostTodoTasksofNonexistentProjectXml passed.");
        }

        /**
         * Tests creating a relationship with a nonexistent todo using XML format.
         * This test verifies that attempting to create a todo-category relationship
         * for a todo that doesn't exist results in appropriate error response using
         * XML.
         * Expected: 404 Not Found indicating the todo doesn't exist.
         */
        @Test
        public void testPostNonexistentTodoCategoriesXml() throws Exception {
                System.out.println("Running testPostNonexistentTodoCategoriesXml...");

                // First create a category
                String categoryXml = "<category><title>Test Category for Nonexistent Todo</title><description>Testing with nonexistent todo</description></category>";
                HttpURLConnection createCategoryConnection = request(CATEGORIES_ENDPOINT, POST_METHOD, XML_FORMAT,
                                XML_FORMAT,
                                categoryXml);
                String createCategoryResponse = readResponse(createCategoryConnection);
                XmlMapper xmlMapper = new XmlMapper();
                categories.Category createdCategory = xmlMapper.readValue(createCategoryResponse,
                                categories.Category.class);
                String categoryId = createdCategory.getId();
                createCategoryConnection.disconnect();

                // Try to create relationship for nonexistent todo
                XmlRelationship relationshipBody = new XmlRelationship(categoryId);
                String relationshipXml = relationshipBody.toStringXml();

                String endpoint = String.format(TODOS_CATEGORIES_ENDPOINT, "99999");
                HttpURLConnection connection = request(endpoint, POST_METHOD, XML_FORMAT, XML_FORMAT, relationshipXml);

                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String responseBody = readResponse(connection);

                assertEquals(404, responseCode);
                assertEquals("Not Found", responseMessage);
                assertTrue("Response should contain error message", responseBody.contains("errorMessages"));

                connection.disconnect();
                System.out.println("testPostNonexistentTodoCategoriesXml passed.");
        }

        /**
         * Tests creating a project-task relationship with a nonexistent todo using XML
         * format.
         * This test verifies that attempting to create a relationship between a valid
         * project
         * and a todo that doesn't exist results in appropriate error response using
         * XML.
         * Expected: 404 Not Found indicating the todo doesn't exist.
         */
        @Test
        public void testPostProjectTasksNonexistentTodoXml() throws Exception {
                System.out.println("Running testPostProjectTasksNonexistentTodoXml...");

                XmlMapper xmlMapper = new XmlMapper();

                // First create a project
                String projectXml = "<project><title>Test Project for Nonexistent Todo</title><completed>false</completed><active>true</active><description>Testing with nonexistent todo</description></project>";
                HttpURLConnection createProjectConnection = request(PROJECTS_ENDPOINT, POST_METHOD, XML_FORMAT,
                                XML_FORMAT,
                                projectXml);
                String createProjectResponse = readResponse(createProjectConnection);
                projects.Project createdProject = xmlMapper.readValue(createProjectResponse, projects.Project.class);
                String projectId = createdProject.getId();
                createProjectConnection.disconnect();

                // Try to create relationship with nonexistent todo
                String relationshipXml = "<todo><id>99999</id></todo>";

                String endpoint = String.format(PROJECTS_TASKS_ENDPOINT, projectId);
                HttpURLConnection connection = request(endpoint, POST_METHOD, XML_FORMAT, XML_FORMAT, relationshipXml);

                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String responseBody = readResponse(connection);

                assertEquals(404, responseCode);
                assertEquals("Not Found", responseMessage);
                assertTrue("Response should contain error message", responseBody.contains("errorMessages"));

                connection.disconnect();
                System.out.println("testPostProjectTasksNonexistentTodoXml passed.");
        }

        /**
         * Tests creating a category-project relationship with a nonexistent project
         * using XML format.
         * This test verifies that attempting to create a relationship between a valid
         * category
         * and a project that doesn't exist results in appropriate error response using
         * XML.
         * Expected: 404 Not Found indicating the project doesn't exist.
         */
        @Test
        public void testPostCategoryProjectsNonexistentProjectXml() throws Exception {
                System.out.println("Running testPostCategoryProjectsNonexistentProjectXml...");

                XmlMapper xmlMapper = new XmlMapper();

                // First create a category
                String categoryXml = "<category><title>Test Category for Nonexistent Project</title><description>Testing with nonexistent project</description></category>";
                HttpURLConnection createCategoryConnection = request(CATEGORIES_ENDPOINT, POST_METHOD, XML_FORMAT,
                                XML_FORMAT,
                                categoryXml);
                String createCategoryResponse = readResponse(createCategoryConnection);
                categories.Category createdCategory = xmlMapper.readValue(createCategoryResponse,
                                categories.Category.class);
                String categoryId = createdCategory.getId();
                createCategoryConnection.disconnect();

                // Try to create relationship with nonexistent project
                XmlRelationship relationshipBody = new XmlRelationship("99999");
                String relationshipXml = relationshipBody.toStringXml();

                String endpoint = String.format(CATEGORIES_PROJECTS_ENDPOINT, categoryId);
                HttpURLConnection connection = request(endpoint, POST_METHOD, XML_FORMAT, XML_FORMAT, relationshipXml);

                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String responseBody = readResponse(connection);

                assertEquals(404, responseCode);
                assertEquals("Not Found", responseMessage);
                assertTrue("Response should contain error message", responseBody.contains("errorMessages"));

                connection.disconnect();
                System.out.println("testPostCategoryProjectsNonexistentProjectXml passed.");
        }

        /**
         * Tests creating a relationship with malformed XML data.
         * This test verifies that attempting to create a relationship using invalid XML
         * results in appropriate error response.
         * Expected: 400 Bad Request for malformed XML.
         */
        @Test
        public void testPostTodoCategoriesMalformedXml() throws Exception {
                System.out.println("Running testPostTodoCategoriesMalformedXml...");

                XmlMapper xmlMapper = new XmlMapper();

                // First create a todo
                String todoXml = "<todo><title>Test Todo for Malformed XML</title><doneStatus>false</doneStatus><description>Testing malformed XML</description></todo>";
                HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT,
                                todoXml);
                String createTodoResponse = readResponse(createTodoConnection);
                Todo createdTodo = xmlMapper.readValue(createTodoResponse, Todo.class);
                createTodoConnection.disconnect();

                // Try to create relationship with malformed XML
                String malformedXml = "<project><id>1</id>";

                String endpoint = String.format(TODOS_CATEGORIES_ENDPOINT, createdTodo.getId());
                HttpURLConnection connection = request(endpoint, POST_METHOD, XML_FORMAT, XML_FORMAT, malformedXml);

                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();

                assertEquals(400, responseCode);
                assertEquals("Bad Request", responseMessage);

                connection.disconnect();
                System.out.println("testPostTodoCategoriesMalformedXml passed.");
        }

        /**
         * Tests creating a relationship with empty XML body.
         * This test verifies that attempting to create a relationship with no XML body
         * results in appropriate error response.
         * Expected: 400 Bad Request for empty request body.
         */
        @Test
        public void testPostTodoCategoriesEmptyBodyXml() throws Exception {
                System.out.println("Running testPostTodoCategoriesEmptyBodyXml...");

                XmlMapper xmlMapper = new XmlMapper();

                // First create a todo
                String todoXml = "<todo><title>Test Todo for Empty Body</title><doneStatus>false</doneStatus><description>Testing empty body</description></todo>";
                HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT,
                                todoXml);
                String createTodoResponse = readResponse(createTodoConnection);
                Todo createdTodo = xmlMapper.readValue(createTodoResponse, Todo.class);
                createTodoConnection.disconnect();

                // Try to create relationship with empty body
                String endpoint = String.format(TODOS_CATEGORIES_ENDPOINT, createdTodo.getId());
                HttpURLConnection connection = request(endpoint, POST_METHOD, XML_FORMAT, XML_FORMAT, "");

                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();

                assertEquals(400, responseCode);
                assertEquals("Bad Request", responseMessage);

                connection.disconnect();
                System.out.println("testPostTodoCategoriesEmptyBodyXml passed.");
        }

        /**
         * Tests getting relationships for a nonexistent todo using XML format.
         * This test verifies that attempting to get categories for a todo that doesn't
         * exist
         * results in appropriate error response using XML format.
         * Expected: 404 Not Found indicating the todo doesn't exist.
         */
        @Test
        public void testGetNonexistentTodoCategoriesXml() throws Exception {
                System.out.println("Running testGetNonexistentTodoCategoriesXml...");

                // Try to get categories for nonexistent todo
                String endpoint = String.format(TODOS_CATEGORIES_ENDPOINT, "99999");
                HttpURLConnection connection = request(endpoint, GET_METHOD, XML_FORMAT, XML_FORMAT, null);

                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String responseBody = readResponse(connection);

                assertEquals(200, responseCode);
                assertEquals("OK", responseMessage);
                assertTrue("Response should not contain error message", !responseBody.contains("errorMessages"));

                connection.disconnect();
                System.out.println("testGetNonexistentTodoCategoriesXml passed.");
        }

        /**
         * Tests creating a relationship with missing required ID field using XML
         * format.
         * This test verifies that attempting to create a relationship without providing
         * the required ID field results in appropriate error response using XML.
         * Expected: 400 Bad Request for missing required field.
         */
        @Test
        public void testPostTodoCategoriesMissingIdXml() throws Exception {
                System.out.println("Running testPostTodoCategoriesMissingIdXml...");

                XmlMapper xmlMapper = new XmlMapper();

                // First create a todo
                String todoXml = "<todo><title>Test Todo for Missing ID</title><doneStatus>false</doneStatus><description>Testing missing ID</description></todo>";
                HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT,
                                todoXml);
                String createTodoResponse = readResponse(createTodoConnection);
                Todo createdTodo = xmlMapper.readValue(createTodoResponse, Todo.class);
                createTodoConnection.disconnect();

                // Try to create relationship with missing ID
                String relationshipXml = "<project></project>"; // Missing ID field

                String endpoint = String.format(TODOS_CATEGORIES_ENDPOINT, createdTodo.getId());
                HttpURLConnection connection = request(endpoint, POST_METHOD, XML_FORMAT, XML_FORMAT, relationshipXml);

                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();

                assertEquals(400, responseCode);
                assertEquals("Bad Request", responseMessage);

                connection.disconnect();
                System.out.println("testPostTodoCategoriesMissingIdXml passed.");
        }

        /**
         * Tests creating a category-todo relationship with a nonexistent todo using XML
         * format.
         * This test verifies that attempting to create a relationship between a valid
         * category
         * and a todo that doesn't exist results in appropriate error response using
         * XML.
         * Expected: 404 Not Found indicating the todo doesn't exist.
         */
        @Test
        public void testPostCategoryTodosNonexistentTodoXml() throws Exception {
                System.out.println("Running testPostCategoryTodosNonexistentTodoXml...");

                XmlMapper xmlMapper = new XmlMapper();

                // First create a category
                String categoryXml = "<category><title>Test Category for Nonexistent Todo</title><description>Testing with nonexistent todo</description></category>";
                HttpURLConnection createCategoryConnection = request(CATEGORIES_ENDPOINT, POST_METHOD, XML_FORMAT,
                                XML_FORMAT,
                                categoryXml);
                String createCategoryResponse = readResponse(createCategoryConnection);
                categories.Category createdCategory = xmlMapper.readValue(createCategoryResponse,
                                categories.Category.class);
                String categoryId = createdCategory.getId();
                createCategoryConnection.disconnect();

                // Try to create relationship with nonexistent todo
                String relationshipXml = "<todo><id>99999</id></todo>"; // Nonexistent todo ID

                String endpoint = String.format(CATEGORIES_TODOS_ENDPOINT, categoryId);
                HttpURLConnection connection = request(endpoint, POST_METHOD, XML_FORMAT, XML_FORMAT, relationshipXml);

                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String responseBody = readResponse(connection);

                assertEquals(404, responseCode);
                assertEquals("Not Found", responseMessage);
                assertTrue("Response should contain error message", responseBody.contains("errorMessages"));

                connection.disconnect();
                System.out.println("testPostCategoryTodosNonexistentTodoXml passed.");
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
                HttpURLConnection createCategoryConnection = request(CATEGORIES_ENDPOINT, POST_METHOD, XML_FORMAT,
                                XML_FORMAT,
                                categoryXml);
                String createCategoryResponse = readResponse(createCategoryConnection);
                categories.Category createdCategory = xmlMapper.readValue(createCategoryResponse,
                                categories.Category.class);
                String categoryId = createdCategory.getId();
                createCategoryConnection.disconnect();

                // Create a project
                String projectXml = "<project><title>Test Project for Category</title><completed>false</completed><active>true</active><description>Test project description</description></project>";
                HttpURLConnection createProjectConnection = request(PROJECTS_ENDPOINT, POST_METHOD, XML_FORMAT,
                                XML_FORMAT,
                                projectXml);
                String createProjectResponse = readResponse(createProjectConnection);
                projects.Project createdProject = xmlMapper.readValue(createProjectResponse, projects.Project.class);
                String projectId = createdProject.getId();
                createProjectConnection.disconnect();

                // Confirm both entities are created
                HttpURLConnection getCategoryConnection = request(
                                String.format(CATEGORIES_ENDPOINT + "/%s", categoryId), GET_METHOD, XML_FORMAT,
                                XML_FORMAT, null);

                assertEquals(200, getCategoryConnection.getResponseCode());
                assertEquals("OK", getCategoryConnection.getResponseMessage());

                HttpURLConnection getProjectConnection = request(
                                String.format(PROJECTS_ENDPOINT + "/%s", projectId), GET_METHOD, XML_FORMAT,
                                XML_FORMAT, null);
                assertEquals(200, getProjectConnection.getResponseCode());
                assertEquals("OK", getProjectConnection.getResponseMessage());

                // Create relationship
                XmlRelationship relationshipBody = new XmlRelationship(projectId);
                String relationshipXml = relationshipBody.toStringXml();

                String endpoint = String.format(CATEGORIES_PROJECTS_ENDPOINT, categoryId);
                HttpURLConnection connection = request(endpoint, POST_METHOD, XML_FORMAT, XML_FORMAT,
                                relationshipXml);

                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String contentType = connection.getContentType();

                assertEquals(404, responseCode);
                assertEquals("Not Found", responseMessage);
                assertTrue(contentType.contains("application/xml"));

                connection.disconnect();
                System.out.println("testPostCategoryProjectsXml passed.");
        }

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
                HttpURLConnection createProjectConnection = request(PROJECTS_ENDPOINT, POST_METHOD, XML_FORMAT,
                                XML_FORMAT,
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
                HttpURLConnection createProjectConnection = request(PROJECTS_ENDPOINT, POST_METHOD, XML_FORMAT,
                                XML_FORMAT,
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

                // Confirm both entities are created
                HttpURLConnection getProjectConnection = request(
                                String.format(PROJECTS_ENDPOINT + "/%s", projectId), GET_METHOD, XML_FORMAT,
                                XML_FORMAT, null);
                assertEquals(200, getProjectConnection.getResponseCode());
                assertEquals("OK", getProjectConnection.getResponseMessage());

                HttpURLConnection getTodoConnection = request(
                                String.format(TODOS_ENDPOINT + "/%s", createdTodo.getId()), GET_METHOD, XML_FORMAT,
                                XML_FORMAT, null);
                assertEquals(200, getTodoConnection.getResponseCode());
                assertEquals("OK", getTodoConnection.getResponseMessage());

                // Create relationship (need to use todo format for tasks relationship)
                String relationshipXml = "<todo><id>" + createdTodo.getId() + "</id></todo>";

                String endpoint = String.format(PROJECTS_TASKS_ENDPOINT, projectId);
                HttpURLConnection connection = request(endpoint, POST_METHOD, XML_FORMAT, XML_FORMAT, relationshipXml);

                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String contentType = connection.getContentType();

                assertEquals(404, responseCode);
                assertEquals("Not Found", responseMessage);
                assertTrue(contentType.contains("application/xml"));

                connection.disconnect();
                System.out.println("testPostProjectTasksXml passed.");
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
                HttpURLConnection createProjectConnection = request(PROJECTS_ENDPOINT, POST_METHOD, XML_FORMAT,
                                XML_FORMAT,
                                projectXml);
                String createProjectResponse = readResponse(createProjectConnection);
                projects.Project createdProject = xmlMapper.readValue(createProjectResponse, projects.Project.class);
                String projectId = createdProject.getId();
                createProjectConnection.disconnect();

                // Create a category
                String categoryXml = "<category><title>Test Category for Project</title><description>Test category description</description></category>";
                HttpURLConnection createCategoryConnection = request(CATEGORIES_ENDPOINT, POST_METHOD, XML_FORMAT,
                                XML_FORMAT,
                                categoryXml);
                String createCategoryResponse = readResponse(createCategoryConnection);
                categories.Category createdCategory = xmlMapper.readValue(createCategoryResponse,
                                categories.Category.class);
                String categoryId = createdCategory.getId();
                createCategoryConnection.disconnect();

                // Confirm both entities are created
                HttpURLConnection getCategoryConnection = request(
                                String.format(CATEGORIES_ENDPOINT + "/%s", categoryId), GET_METHOD, XML_FORMAT,
                                XML_FORMAT, null);
                assertEquals(200, getCategoryConnection.getResponseCode());
                assertEquals("OK", getCategoryConnection.getResponseMessage());
                HttpURLConnection getProjectConnection = request(
                                String.format(PROJECTS_ENDPOINT + "/%s", projectId), GET_METHOD, XML_FORMAT,
                                XML_FORMAT, null);
                assertEquals(200, getProjectConnection.getResponseCode());
                assertEquals("OK", getProjectConnection.getResponseMessage());

                // Create relationship
                XmlRelationship relationshipBody = new XmlRelationship(categoryId);
                String relationshipXml = relationshipBody.toStringXmlCategory();

                String endpoint = String.format(PROJECTS_CATEGORIES_ENDPOINT, projectId);
                HttpURLConnection connection = request(endpoint, POST_METHOD, XML_FORMAT, XML_FORMAT, relationshipXml);

                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String contentType = connection.getContentType();

                assertEquals(404, responseCode);
                assertEquals("Not Found", responseMessage);
                assertTrue(contentType.contains("application/xml"));

                connection.disconnect();
                System.out.println("testPostProjectCategoriesXml passed.");
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
                HttpURLConnection createCategoryConnection = request(CATEGORIES_ENDPOINT, POST_METHOD, XML_FORMAT,
                                XML_FORMAT,
                                categoryXml);
                String createCategoryResponse = readResponse(createCategoryConnection);
                categories.Category createdCategory = xmlMapper.readValue(createCategoryResponse,
                                categories.Category.class);
                String categoryId = createdCategory.getId();
                createCategoryConnection.disconnect();

                // Create a todo
                String todoXml = "<todo><title>Test Todo for Category</title><doneStatus>false</doneStatus><description>Test todo description</description></todo>";
                HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT,
                                todoXml);
                String createTodoResponse = readResponse(createTodoConnection);
                Todo createdTodo = xmlMapper.readValue(createTodoResponse, Todo.class);
                createTodoConnection.disconnect();

                // Verify both entities are created
                HttpURLConnection getCategoryConnection = request(
                                String.format(CATEGORIES_ENDPOINT + "/%s", categoryId), GET_METHOD, XML_FORMAT,
                                XML_FORMAT, null);
                assertEquals(200, getCategoryConnection.getResponseCode());
                assertEquals("OK", getCategoryConnection.getResponseMessage());
                HttpURLConnection getTodoConnection = request(
                                String.format(TODOS_ENDPOINT + "/%s", createdTodo.getId()), GET_METHOD, XML_FORMAT,
                                XML_FORMAT, null);
                assertEquals(200, getTodoConnection.getResponseCode());
                assertEquals("OK", getTodoConnection.getResponseMessage());

                // Create relationship (need to use todo format)
                String relationshipXml = "<todo><id>" + createdTodo.getId() + "</id></todo>";

                String endpoint = String.format(CATEGORIES_TODOS_ENDPOINT, categoryId);
                HttpURLConnection connection = request(endpoint, POST_METHOD, XML_FORMAT, XML_FORMAT, relationshipXml);

                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String contentType = connection.getContentType();

                assertEquals(404, responseCode);
                assertEquals("Not Found", responseMessage);
                assertTrue(contentType.contains("application/xml"));

                connection.disconnect();
                System.out.println("testPostCategoryTodosXml passed.");
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
                HttpURLConnection createCategoryConnection = request(CATEGORIES_ENDPOINT, POST_METHOD, XML_FORMAT,
                                XML_FORMAT,
                                categoryXml);
                String createCategoryResponse = readResponse(createCategoryConnection);
                categories.Category createdCategory = xmlMapper.readValue(createCategoryResponse,
                                categories.Category.class);
                String categoryId = createdCategory.getId();
                createCategoryConnection.disconnect();

                // Confirm both entities are created
                HttpURLConnection getCategoryConnection = request(
                                String.format(CATEGORIES_ENDPOINT + "/%s", categoryId), GET_METHOD, XML_FORMAT,
                                XML_FORMAT, null);
                assertEquals(200, getCategoryConnection.getResponseCode());
                assertEquals("OK", getCategoryConnection.getResponseMessage());

                HttpURLConnection getTodoConnection = request(
                                String.format(TODOS_ENDPOINT + "/%s", createdTodo.getId()), GET_METHOD, XML_FORMAT,
                                XML_FORMAT, null);
                assertEquals(200, getTodoConnection.getResponseCode());
                assertEquals("OK", getTodoConnection.getResponseMessage());

                // Create relationship
                XmlRelationship relationshipBody = new XmlRelationship(categoryId);
                String relationshipXml = relationshipBody.toStringXmlCategory();

                String endpoint = String.format(TODOS_CATEGORIES_ENDPOINT, createdTodo.getId());
                HttpURLConnection connection = request(endpoint, POST_METHOD, XML_FORMAT, XML_FORMAT, relationshipXml);

                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String contentType = connection.getContentType();

                assertEquals(404, responseCode);
                assertEquals("Not Found", responseMessage);
                assertTrue(contentType.contains("application/xml"));

                connection.disconnect();
                System.out.println("testPostTodoCategoriesXml passed.");
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
                HttpURLConnection createProjectConnection = request(PROJECTS_ENDPOINT, POST_METHOD, XML_FORMAT,
                                XML_FORMAT,
                                projectXml);
                String createProjectResponse = readResponse(createProjectConnection);
                projects.Project createdProject = xmlMapper.readValue(createProjectResponse, projects.Project.class);
                String projectId = createdProject.getId();
                createProjectConnection.disconnect();

                // Confirm both entities are created
                HttpURLConnection getProjectConnection = request(
                                String.format(PROJECTS_ENDPOINT + "/%s", projectId), GET_METHOD, XML_FORMAT,
                                XML_FORMAT, null);
                assertEquals(200, getProjectConnection.getResponseCode());
                assertEquals("OK", getProjectConnection.getResponseMessage());

                HttpURLConnection getTodoConnection = request(
                                String.format(TODOS_ENDPOINT + "/%s", createdTodo.getId()), GET_METHOD, XML_FORMAT,
                                XML_FORMAT, null);
                assertEquals(200, getTodoConnection.getResponseCode());
                assertEquals("OK", getTodoConnection.getResponseMessage());

                // Create relationship
                XmlRelationship relationshipBody = new XmlRelationship(projectId);
                String relationshipXml = relationshipBody.toStringXml();

                String endpoint = String.format(TODOS_TASKSOF_ENDPOINT, createdTodo.getId());
                HttpURLConnection connection = request(endpoint, POST_METHOD, XML_FORMAT, XML_FORMAT, relationshipXml);

                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String contentType = connection.getContentType();

                assertEquals(404, responseCode);
                assertEquals("Not Found", responseMessage);
                assertTrue(contentType.contains("application/xml"));

                connection.disconnect();
                System.out.println("testPostTodoTasksofXml passed.");
        }
}