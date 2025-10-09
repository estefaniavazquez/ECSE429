package interoperability;

import static general.CommonConstants.CATEGORIES_ENDPOINT;
import static general.CommonConstants.CATEGORIES_PROJECTS_ENDPOINT;
import static general.CommonConstants.CATEGORIES_TODOS_ENDPOINT;
import static general.CommonConstants.GET_METHOD;
import static general.CommonConstants.POST_METHOD;
import static general.CommonConstants.PROJECTS_ENDPOINT;
import static general.CommonConstants.PROJECTS_TASKS_ENDPOINT;
import static general.CommonConstants.TODOS_CATEGORIES_ENDPOINT;
import static general.CommonConstants.TODOS_ENDPOINT;
import static general.CommonConstants.TODOS_TASKSOF_ENDPOINT;
import static general.CommonConstants.XML_FORMAT;
import static general.Utils.readResponse;
import static general.Utils.request;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.HttpURLConnection;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import general.BaseApiTest;
import interoperability.models.Todo;
import interoperability.models.XmlRelationship;

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

                assertEquals(404, responseCode);
                assertEquals("Not Found", responseMessage);
                assertTrue("Response should contain error message", responseBody.contains("errorMessages"));

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
}