package interoperability;

import static general.CommonConstants.CATEGORIES_ENDPOINT;
import static general.CommonConstants.CATEGORIES_PROJECTS_ENDPOINT;
import static general.CommonConstants.CATEGORIES_TODOS_ENDPOINT;
import static general.CommonConstants.DELETE_METHOD;
import static general.CommonConstants.OPTIONS_METHOD;
import static general.CommonConstants.POST_METHOD;
import static general.CommonConstants.PROJECTS_CATEGORIES_ENDPOINT;
import static general.CommonConstants.PROJECTS_ENDPOINT;
import static general.CommonConstants.PROJECTS_TASKS_ENDPOINT;
import static general.CommonConstants.PUT_METHOD;
import static general.CommonConstants.RELATIONSHIP_ID_OPTIONS;
import static general.CommonConstants.TODOS_CATEGORIES_ENDPOINT;
import static general.CommonConstants.TODOS_CATEGORIES_ID_ENDPOINT;
import static general.CommonConstants.TODOS_CATEGORIES_OPTIONS;
import static general.CommonConstants.TODOS_ENDPOINT;
import static general.CommonConstants.TODOS_TASKSOF_ENDPOINT;
import static general.CommonConstants.TODOS_TASKSOF_ID_ENDPOINT;
import static general.CommonConstants.XML_FORMAT;
import static general.Utils.readResponse;
import static general.Utils.request;
import static general.Utils.requestPATCH;
import static org.junit.Assert.assertEquals;

import java.net.HttpURLConnection;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import general.BaseApiTest;

/**
 * Test class for undocumented interoperability API endpoints and methods using
 * XML format.
 * 
 * This class tests HTTP methods and endpoints that are not documented in the
 * API
 * specification but might be supported or should return appropriate error
 * responses
 * using XML content type. It verifies that unsupported methods return proper
 * HTTP
 * error codes and that nonexistent endpoints behave correctly when using XML
 * format.
 * 
 * Tested scenarios:
 * - Unsupported HTTP methods (PUT, DELETE, PATCH) on relationship endpoints
 * - Undocumented specific relationship ID endpoints
 * - OPTIONS method support for discovering allowed methods
 * 
 * Expected behaviors:
 * - 405 Method Not Allowed for unsupported methods on existing endpoints
 * - 404 Not Found for nonexistent specific relationship endpoints
 * - 200 OK for OPTIONS with proper Allow headers
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UndocumentedInteropXmlApiTest extends BaseApiTest {

        /* Test undocumented HTTP methods on relationship endpoints */

        /**
         * Tests the PUT /todos/{id}/categories endpoint with XML format.
         * This test verifies that PUT method is not supported on the todo-categories
         * relationship endpoint when using XML. PUT is not documented for relationship
         * endpoints
         * and should return 405 Method Not Allowed error.
         * Expected: 405 Method Not Allowed with appropriate error message.
         */
        @Test
        public void testPutTodoCategoriesXml() throws Exception {
                System.out.println("Running testPutTodoCategoriesXml...");

                XmlMapper xmlMapper = new XmlMapper();

                // First create a todo
                String todoXml = "<todo><title>Test Todo PUT</title><doneStatus>false</doneStatus><description>Test description</description></todo>";

                HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT,
                                todoXml);
                String createTodoResponse = readResponse(createTodoConnection);
                Todo createdTodo = xmlMapper.readValue(createTodoResponse, Todo.class);
                createTodoConnection.disconnect();

                // Try PUT on relationship endpoint (should be method not allowed)
                XmlRelationship relationshipBody = new XmlRelationship("1");
                String relationshipXml = relationshipBody.toStringXml();

                String endpoint = String.format(TODOS_CATEGORIES_ENDPOINT, createdTodo.getId());
                HttpURLConnection connection = request(endpoint, PUT_METHOD, XML_FORMAT, XML_FORMAT, relationshipXml);

                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String responseBody = readResponse(connection);

                assertEquals(405, responseCode);
                assertEquals("Method Not Allowed", responseMessage);
                assertEquals("", responseBody);

                connection.disconnect();
                System.out.println("testPutTodoCategoriesXml passed.");
        }

        /**
         * Tests the DELETE /todos/{id}/categories endpoint with XML format.
         * This test verifies that DELETE method without a specific category ID
         * is not supported on the todo-categories relationship endpoint when using XML.
         * DELETE should only work with specific relationship IDs.
         * Expected: 405 Method Not Allowed for bulk delete operations.
         */
        @Test
        public void testDeleteTodoCategoriesXml() throws Exception {
                System.out.println("Running testDeleteTodoCategoriesXml...");

                XmlMapper xmlMapper = new XmlMapper();

                // First create a todo
                String todoXml = "<todo><title>Test Todo DELETE</title><doneStatus>false</doneStatus><description>Test description</description></todo>";

                HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT,
                                todoXml);
                String createTodoResponse = readResponse(createTodoConnection);
                Todo createdTodo = xmlMapper.readValue(createTodoResponse, Todo.class);
                createTodoConnection.disconnect();

                // Try DELETE on relationship collection endpoint (should be method not allowed)
                String endpoint = String.format(TODOS_CATEGORIES_ENDPOINT, createdTodo.getId());
                HttpURLConnection connection = request(endpoint, DELETE_METHOD, XML_FORMAT, XML_FORMAT, null);

                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String responseBody = readResponse(connection);

                assertEquals(405, responseCode);
                assertEquals("Method Not Allowed", responseMessage);
                assertEquals("", responseBody);

                connection.disconnect();
                System.out.println("testDeleteTodoCategoriesXml passed.");
        }

        /**
         * Tests the PATCH /todos/{id}/tasksof endpoint with XML format.
         * This test verifies that PATCH method is not supported on the todo-tasksof
         * relationship endpoint when using XML. PATCH is not documented for
         * relationship endpoints
         * and should return 405 Method Not Allowed error.
         * Expected: 405 Method Not Allowed indicating PATCH is not supported.
         */
        @Test
        public void testPatchTodoTasksofXml() throws Exception {
                System.out.println("Running testPatchTodoTasksofXml...");

                XmlMapper xmlMapper = new XmlMapper();

                // First create a todo
                String todoXml = "<todo><title>Test Todo PATCH</title><doneStatus>false</doneStatus><description>Test description</description></todo>";

                HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT,
                                todoXml);
                String createTodoResponse = readResponse(createTodoConnection);
                Todo createdTodo = xmlMapper.readValue(createTodoResponse, Todo.class);
                createTodoConnection.disconnect();

                // Try PATCH on relationship endpoint (should be method not allowed)
                String endpoint = String.format(TODOS_TASKSOF_ENDPOINT, createdTodo.getId());
                HttpURLConnection connection = requestPATCH(endpoint, XML_FORMAT, XML_FORMAT);

                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String responseBody = readResponse(connection);

                assertEquals(405, responseCode);
                assertEquals("Method Not Allowed", responseMessage);
                assertEquals("", responseBody);

                connection.disconnect();
                System.out.println("testPatchTodoTasksofXml passed.");
        }

        /**
         * Tests the PUT /todos/{id}/tasksof/{id} endpoint with XML format.
         * This test verifies that PUT method on specific relationship ID endpoints
         * is not supported when using XML. PUT should not work on specific relationship
         * IDs.
         * Expected: 405 Method Not Allowed for PUT on specific relationship ID.
         */
        @Test
        public void testPutTodoTasksofIdXml() throws Exception {
                System.out.println("Running testPutTodoTasksofIdXml...");

                XmlMapper xmlMapper = new XmlMapper();

                // First create a todo
                String todoXml = "<todo><title>Test Todo for PUT ID</title><doneStatus>false</doneStatus><description>Test description</description></todo>";

                HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT,
                                todoXml);
                String createTodoResponse = readResponse(createTodoConnection);
                Todo createdTodo = xmlMapper.readValue(createTodoResponse, Todo.class);
                createTodoConnection.disconnect();

                // Try PUT on specific relationship ID endpoint (should be method not allowed)
                XmlRelationship relationshipBody = new XmlRelationship("1");
                String relationshipXml = relationshipBody.toStringXml();

                String endpoint = String.format(TODOS_TASKSOF_ID_ENDPOINT, createdTodo.getId(), "1");
                HttpURLConnection connection = request(endpoint, PUT_METHOD, XML_FORMAT, XML_FORMAT, relationshipXml);

                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String responseBody = readResponse(connection);

                assertEquals(405, responseCode);
                assertEquals("Method Not Allowed", responseMessage);
                assertEquals("", responseBody);

                connection.disconnect();
                System.out.println("testPutTodoTasksofIdXml passed.");
        }

        /* Test OPTIONS method support */

        /**
         * Tests the OPTIONS /todos/{id}/categories endpoint with XML format.
         * This test verifies that OPTIONS method is supported on relationship endpoints
         * and returns appropriate allowed methods when using XML format.
         * Expected: 200 OK with proper Allow header listing supported methods.
         */
        @Test
        public void testOptionsTodoCategoriesXml() throws Exception {
                System.out.println("Running testOptionsTodoCategoriesXml...");

                XmlMapper xmlMapper = new XmlMapper();

                // First create a todo
                String todoXml = "<todo><title>Test Todo for OPTIONS</title><doneStatus>false</doneStatus><description>Test description</description></todo>";

                HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT,
                                todoXml);
                String createTodoResponse = readResponse(createTodoConnection);
                Todo createdTodo = xmlMapper.readValue(createTodoResponse, Todo.class);
                createTodoConnection.disconnect();

                // Try OPTIONS on relationship endpoint
                String endpoint = String.format(TODOS_CATEGORIES_ENDPOINT, createdTodo.getId());
                HttpURLConnection connection = request(endpoint, OPTIONS_METHOD, XML_FORMAT, XML_FORMAT, null);

                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String allowHeader = connection.getHeaderField("Allow");

                assertEquals(200, responseCode);
                assertEquals("OK", responseMessage);
                assertEquals(TODOS_CATEGORIES_OPTIONS, allowHeader);

                connection.disconnect();
                System.out.println("testOptionsTodoCategoriesXml passed.");
        }

        /**
         * Tests the OPTIONS /todos/{id}/categories/{id} endpoint with XML format.
         * This test verifies that OPTIONS method is supported on specific relationship
         * ID endpoints
         * and returns appropriate allowed methods when using XML format.
         * Expected: 200 OK with proper Allow header for relationship ID operations.
         */
        @Test
        public void testOptionsTodoCategoriesIdXml() throws Exception {
                System.out.println("Running testOptionsTodoCategoriesIdXml...");

                XmlMapper xmlMapper = new XmlMapper();

                // First create a todo
                String todoXml = "<todo><title>Test Todo for OPTIONS ID</title><doneStatus>false</doneStatus><description>Test description</description></todo>";

                HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT,
                                todoXml);
                String createTodoResponse = readResponse(createTodoConnection);
                Todo createdTodo = xmlMapper.readValue(createTodoResponse, Todo.class);
                createTodoConnection.disconnect();

                // Try OPTIONS on specific relationship ID endpoint
                String endpoint = String.format(TODOS_CATEGORIES_ID_ENDPOINT, createdTodo.getId(), "1");
                HttpURLConnection connection = request(endpoint, OPTIONS_METHOD, XML_FORMAT, XML_FORMAT, null);

                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String allowHeader = connection.getHeaderField("Allow");

                assertEquals(200, responseCode);
                assertEquals("OK", responseMessage);
                assertEquals(RELATIONSHIP_ID_OPTIONS, allowHeader);

                connection.disconnect();
                System.out.println("testOptionsTodoCategoriesIdXml passed.");
        }

        /* Test undocumented project relationship methods */

        /**
         * Tests the PUT /projects/{id}/categories endpoint with XML format.
         * This test verifies that PUT method is not supported on project-categories
         * relationship endpoints when using XML format.
         * Expected: 405 Method Not Allowed.
         */
        @Test
        public void testPutProjectCategoriesXml() throws Exception {
                System.out.println("Running testPutProjectCategoriesXml...");

                XmlMapper xmlMapper = new XmlMapper();

                // First create a project
                String projectXml = "<project><title>Test Project PUT</title><completed>false</completed><active>true</active><description>Test description</description></project>";

                HttpURLConnection createProjectConnection = request(PROJECTS_ENDPOINT, POST_METHOD, XML_FORMAT,
                                XML_FORMAT,
                                projectXml);
                String createProjectResponse = readResponse(createProjectConnection);
                projects.Project createdProject = xmlMapper.readValue(createProjectResponse, projects.Project.class);
                String projectId = createdProject.getId();
                createProjectConnection.disconnect();

                // Try PUT on relationship endpoint (should be method not allowed)
                XmlRelationship relationshipBody = new XmlRelationship("1");
                String relationshipXml = relationshipBody.toStringXml();

                String endpoint = String.format(PROJECTS_CATEGORIES_ENDPOINT, projectId);
                HttpURLConnection connection = request(endpoint, PUT_METHOD, XML_FORMAT, XML_FORMAT, relationshipXml);

                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String responseBody = readResponse(connection);

                assertEquals(405, responseCode);
                assertEquals("Method Not Allowed", responseMessage);
                assertEquals("", responseBody);

                connection.disconnect();
                System.out.println("testPutProjectCategoriesXml passed.");
        }

        /**
         * Tests the DELETE /projects/{id}/tasks endpoint with XML format.
         * This test verifies that DELETE method without a specific task ID
         * is not supported on project-tasks relationship endpoints when using XML
         * format.
         * Expected: 405 Method Not Allowed for bulk delete operations.
         */
        @Test
        public void testDeleteProjectTasksXml() throws Exception {
                System.out.println("Running testDeleteProjectTasksXml...");

                XmlMapper xmlMapper = new XmlMapper();

                // First create a project
                String projectXml = "<project><title>Test Project DELETE</title><completed>false</completed><active>true</active><description>Test description</description></project>";

                HttpURLConnection createProjectConnection = request(PROJECTS_ENDPOINT, POST_METHOD, XML_FORMAT,
                                XML_FORMAT,
                                projectXml);
                String createProjectResponse = readResponse(createProjectConnection);
                projects.Project createdProject = xmlMapper.readValue(createProjectResponse, projects.Project.class);
                String projectId = createdProject.getId();
                createProjectConnection.disconnect();

                // Try DELETE on relationship collection endpoint (should be method not allowed)
                String endpoint = String.format(PROJECTS_TASKS_ENDPOINT, projectId);
                HttpURLConnection connection = request(endpoint, DELETE_METHOD, XML_FORMAT, XML_FORMAT, null);

                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String responseBody = readResponse(connection);

                assertEquals(405, responseCode);
                assertEquals("Method Not Allowed", responseMessage);
                assertEquals("", responseBody);

                connection.disconnect();
                System.out.println("testDeleteProjectTasksXml passed.");
        }

        /* Test undocumented category relationship methods */

        /**
         * Tests the PATCH /categories/{id}/projects endpoint with XML format.
         * This test verifies that PATCH method is not supported on category-projects
         * relationship endpoints when using XML format.
         * Expected: 405 Method Not Allowed.
         */
        @Test
        public void testPatchCategoryProjectsXml() throws Exception {
                System.out.println("Running testPatchCategoryProjectsXml...");

                XmlMapper xmlMapper = new XmlMapper();

                // First create a category
                String categoryXml = "<category><title>Test Category PATCH</title><description>Test description</description></category>";

                HttpURLConnection createCategoryConnection = request(CATEGORIES_ENDPOINT, POST_METHOD, XML_FORMAT,
                                XML_FORMAT,
                                categoryXml);
                String createCategoryResponse = readResponse(createCategoryConnection);
                categories.Category createdCategory = xmlMapper.readValue(createCategoryResponse,
                                categories.Category.class);
                String categoryId = createdCategory.getId();
                createCategoryConnection.disconnect();

                // Try PATCH on relationship endpoint (should be method not allowed)
                String endpoint = String.format(CATEGORIES_PROJECTS_ENDPOINT, categoryId);
                HttpURLConnection connection = requestPATCH(endpoint, XML_FORMAT, XML_FORMAT);

                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String responseBody = readResponse(connection);

                assertEquals(405, responseCode);
                assertEquals("Method Not Allowed", responseMessage);
                assertEquals("", responseBody);

                connection.disconnect();
                System.out.println("testPatchCategoryProjectsXml passed.");
        }

        /**
         * Tests the PUT /categories/{id}/todos endpoint with XML format.
         * This test verifies that PUT method is not supported on category-todos
         * relationship endpoints when using XML format.
         * Expected: 405 Method Not Allowed.
         */
        @Test
        public void testPutCategoryTodosXml() throws Exception {
                System.out.println("Running testPutCategoryTodosXml...");

                XmlMapper xmlMapper = new XmlMapper();

                // First create a category
                String categoryXml = "<category><title>Test Category PUT</title><description>Test description</description></category>";

                HttpURLConnection createCategoryConnection = request(CATEGORIES_ENDPOINT, POST_METHOD, XML_FORMAT,
                                XML_FORMAT,
                                categoryXml);
                String createCategoryResponse = readResponse(createCategoryConnection);
                categories.Category createdCategory = xmlMapper.readValue(createCategoryResponse,
                                categories.Category.class);
                String categoryId = createdCategory.getId();
                createCategoryConnection.disconnect();

                // Try PUT on relationship endpoint (should be method not allowed)
                String relationshipXml = "<todo><id>1</id></todo>";

                String endpoint = String.format(CATEGORIES_TODOS_ENDPOINT, categoryId);
                HttpURLConnection connection = request(endpoint, PUT_METHOD, XML_FORMAT, XML_FORMAT, relationshipXml);

                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String responseBody = readResponse(connection);

                assertEquals(405, responseCode);
                assertEquals("Method Not Allowed", responseMessage);
                assertEquals("", responseBody);

                connection.disconnect();
                System.out.println("testPutCategoryTodosXml passed.");
        }
}