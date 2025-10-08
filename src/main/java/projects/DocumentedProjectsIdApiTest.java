package projects;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.net.HttpURLConnection;
import java.util.List;

import static org.junit.Assert.*;

import general.BaseApiTest;
import categories.Category.*;

import static general.CommonConstants.*;
import static general.Utils.*;
import projects.Project.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DocumentedProjectsIdApiTest extends BaseApiTest {

        @Test
        public void testGetProjectsIdJson() throws Exception {
                System.out.println("Running testGetProjectsIdJson...");

                // check initial state
                HttpURLConnection initialAllConnection = request(PROJECTS_ENDPOINT, GET_METHOD, JSON_FORMAT, JSON_FORMAT, null);
                String initialAllResponse = readResponse(initialAllConnection);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonProject initialAllProjects = objectMapper.readValue(initialAllResponse, JsonProject.class);
                initialAllConnection.disconnect();

                HttpURLConnection connection = requestWithId(PROJECTS_ENDPOINT, GET_METHOD,
                                JSON_FORMAT, JSON_FORMAT, defaultProject.getId(), null);
                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String contentType = connection.getHeaderField("Content-Type");
                String responseBody = readResponse(connection);

                JsonProject project = objectMapper.readValue(responseBody, JsonProject.class);

                assertEquals(200, responseCode);
                assertEquals("OK", responseMessage);
                assertEquals(JSON_FORMAT, contentType);
                assertTrue(project.contains(defaultProject));

                // verify GET operation didn't modify any data
                HttpURLConnection finalAllConnection = request(PROJECTS_ENDPOINT, GET_METHOD, JSON_FORMAT, JSON_FORMAT, null);
                String finalAllResponse = readResponse(finalAllConnection);
                JsonProject finalAllProjects = objectMapper.readValue(finalAllResponse, JsonProject.class);
                finalAllConnection.disconnect();
                
                assertEquals("GET operation should not modify project count", initialAllProjects.getProjects().length, finalAllProjects.getProjects().length);
                assertEquals("GET operation should not modify any project data", initialAllResponse, finalAllResponse);

                connection.disconnect();

                System.out.println("testGetProjectsIdJson passed.");
        }

        @Test
        public void testGetProjectsIdXml() throws Exception {
                System.out.println("Running testGetProjectsIdXml...");

                HttpURLConnection connection = requestWithId(PROJECTS_ENDPOINT, GET_METHOD,
                                XML_FORMAT, XML_FORMAT, defaultProject.getId(), null);
                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String contentType = connection.getHeaderField("Content-Type");
                String responseBody = readResponse(connection);

                System.out.println("Response code: " + responseCode);
                System.out.println("Response message: " + responseMessage);
                System.out.println("Content-Type: " + contentType);
                System.out.println("Response body: " + responseBody);

                assertEquals(200, responseCode);
                assertEquals("OK", responseMessage);
                assertEquals(XML_FORMAT, contentType);

                XmlMapper xmlMapper = new XmlMapper();
                XmlProject project = xmlMapper.readValue(responseBody, XmlProject.class);
                assertTrue(project.contains(defaultProject));

                connection.disconnect();

                System.out.println("testGetProjectsIdXml passed.");
        }

        @Test
        public void testGetProjectsIdInexistentJson() throws Exception {
                System.out.println("Running testGetProjectsIdInexistentJson...");

                HttpURLConnection connection = requestWithId(PROJECTS_ENDPOINT, GET_METHOD, JSON_FORMAT, JSON_FORMAT,
                                "inexistent", null);
                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String contentType = connection.getHeaderField("Content-Type");
                String responseBody = readResponse(connection);

                System.out.println("Response code: " + responseCode);
                System.out.println("Response message: " + responseMessage);
                System.out.println("Content-Type: " + contentType);
                System.out.println("Response body: " + responseBody);

                assertEquals(404, responseCode);
                assertEquals("Not Found", responseMessage);
                assertEquals(JSON_FORMAT, contentType);

                connection.disconnect();

                System.out.println("testGetProjectsIdInexistentJson passed.");
        }

        @Test
        public void testGetProjectsIdInexistentXml() throws Exception {
                System.out.println("Running testGetProjectsIdInexistentXml...");

                HttpURLConnection connection = requestWithId(PROJECTS_ENDPOINT, GET_METHOD, XML_FORMAT, XML_FORMAT,
                                "inexistent", null);
                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String contentType = connection.getHeaderField("Content-Type");
                String responseBody = readResponse(connection);

                System.out.println("Response code: " + responseCode);
                System.out.println("Response message: " + responseMessage);
                System.out.println("Content-Type: " + contentType);
                System.out.println("Response body: " + responseBody);

                assertEquals(404, responseCode);
                assertEquals("Not Found", responseMessage);
                assertEquals(XML_FORMAT, contentType);

                connection.disconnect();

                System.out.println("testGetProjectsIdInexistentXml passed.");
        }

        @Test
        public void testHeadProjectsIdJson() throws Exception {
                System.out.println("Running testHeadProjectsIdJson...");

                HttpURLConnection connection = requestWithId(PROJECTS_ENDPOINT, HEAD_METHOD, JSON_FORMAT, JSON_FORMAT,
                                defaultProject.getId(), null);
                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String contentType = connection.getHeaderField("Content-Type");
                String responseBody = readResponse(connection);

                System.out.println("Response code: " + responseCode);
                System.out.println("Response message: " + responseMessage);
                System.out.println("Content-Type: " + contentType);
                System.out.println("Response body: " + responseBody);

                assertEquals(200, responseCode);
                assertEquals("OK", responseMessage);
                assertEquals(JSON_FORMAT, contentType);
                assertEquals("", responseBody);

                connection.disconnect();

                System.out.println("testHeadProjectsIdJson passed.");
        }

        @Test
        public void testHeadProjectsIdXml() throws Exception {
                System.out.println("Running testHeadProjectsIdXml...");

                HttpURLConnection connection = requestWithId(PROJECTS_ENDPOINT, HEAD_METHOD, XML_FORMAT, XML_FORMAT,
                                defaultProject.getId(), null);
                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String contentType = connection.getHeaderField("Content-Type");
                String responseBody = readResponse(connection);

                System.out.println("Response code: " + responseCode);
                System.out.println("Response message: " + responseMessage);
                System.out.println("Content-Type: " + contentType);
                System.out.println("Response body: " + responseBody);

                assertEquals(200, responseCode);
                assertEquals("OK", responseMessage);
                assertEquals(XML_FORMAT, contentType);
                assertEquals("", responseBody);

                connection.disconnect();

                System.out.println("testHeadProjectsIdXml passed.");
        }

        @Test
        public void testPostProjectsIdJson() throws Exception {
                System.out.println("Running testPostProjectsIdJson...");

                // check initial state of all projects
                HttpURLConnection initialConnection = request(PROJECTS_ENDPOINT, GET_METHOD, JSON_FORMAT, JSON_FORMAT, null);
                String initialResponseBody = readResponse(initialConnection);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonProject initialProjects = objectMapper.readValue(initialResponseBody, JsonProject.class);
                int initialProjectCount = initialProjects.getProjects().length;
                initialConnection.disconnect();

                String projectsId = defaultProject.getId();
                ProjectBody newProjectBody = new ProjectBody("new project", false, true, "A new project");
                String newProjectBodyJson = objectMapper.writeValueAsString(newProjectBody);

                Project newProject = new Project(projectsId, newProjectBody.getTitle(), newProjectBody.isCompleted(),
                                newProjectBody.isActive(), newProjectBody.getDescription());

                HttpURLConnection connection = requestWithId(PROJECTS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                                projectsId, newProjectBodyJson);
                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String contentType = connection.getContentType();
                String responseBody = readResponse(connection);

                System.out.println("Response code: " + responseCode);
                System.out.println("Response message: " + responseMessage);
                System.out.println("Content-Type: " + contentType);
                System.out.println("Response body: " + responseBody);

                Project returnedProject = objectMapper.readValue(responseBody, Project.class);

                HttpURLConnection connectionAll = request(PROJECTS_ENDPOINT, GET_METHOD, JSON_FORMAT, JSON_FORMAT,
                                null);
                String responseBodyAll = readResponse(connectionAll);

                JsonProject allProjects = objectMapper.readValue(responseBodyAll, JsonProject.class);

                assertEquals(200, responseCode);
                assertEquals("OK", responseMessage);
                assertEquals(JSON_FORMAT, contentType);
                assertEquals(newProject, returnedProject);
                assertTrue(allProjects.contains(newProject));
                
                // verify project count remained the same (update, not create)
                assertEquals("Project count should remain the same for update operation", initialProjectCount, allProjects.getProjects().length);
                
                // verify other projects weren't affected
                for (Project initialProject : initialProjects.getProjects()) {
                    if (!initialProject.getId().equals(projectsId)) {
                        boolean foundUnchanged = false;
                        for (Project finalProject : allProjects.getProjects()) {
                            if (initialProject.getId().equals(finalProject.getId()) &&
                                initialProject.getTitle().equals(finalProject.getTitle()) &&
                                initialProject.getDescription().equals(finalProject.getDescription()) &&
                                initialProject.isCompleted() == finalProject.isCompleted() &&
                                initialProject.isActive() == finalProject.isActive()) {
                                foundUnchanged = true;
                                break;
                            }
                        }
                        assertTrue("Project " + initialProject.getId() + " should remain unchanged", foundUnchanged);
                    }
                }

                connection.disconnect();
                connectionAll.disconnect();

                System.out.println("testPostProjectsIdJson passed.");
        }

        @Test
        public void testPostNoBodyProjectsIdJson() throws Exception {
                System.out.println("Running testPostNoBodyProjectsIdJson...");

                String projectsId = defaultProject.getId();

                HttpURLConnection connection = requestWithId(PROJECTS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                                projectsId, null);
                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String contentType = connection.getContentType();
                String responseBody = readResponse(connection);

                System.out.println("Response code: " + responseCode);
                System.out.println("Response message: " + responseMessage);
                System.out.println("Content-Type: " + contentType);
                System.out.println("Response body: " + responseBody);

                assertEquals(200, responseCode);
                assertEquals("OK", responseMessage);
                assertEquals(JSON_FORMAT, contentType);
                assertEquals("{\"id\":\"1\",\"title\":\"Office Work\",\"completed\":\"false\",\"active\":\"false\",\"description\":\"\",\"tasks\":[{\"id\":\"1\"},{\"id\":\"2\"}]}", // Could
                                                                                                                                                                                     // be
                                                                                                                                                                                     // SUS
                                responseBody);

                connection.disconnect();

                System.out.println("testPostNoBodyProjectsIdJson passed.");
        }

        @Test
        public void testPostRandomFieldProjectsIdJson() throws Exception {
                System.out.println("Running testPostRandomFieldProjectsIdJson...");

                // check initial state
                HttpURLConnection initialConnection = request(PROJECTS_ENDPOINT, GET_METHOD, JSON_FORMAT, JSON_FORMAT, null);
                String initialResponseBody = readResponse(initialConnection);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonProject initialProjects = objectMapper.readValue(initialResponseBody, JsonProject.class);
                initialConnection.disconnect();

                // check initial state of the target project
                HttpURLConnection initialTargetConnection = requestWithId(PROJECTS_ENDPOINT, GET_METHOD, JSON_FORMAT, JSON_FORMAT, defaultProject.getId(), null);
                String initialTargetResponse = readResponse(initialTargetConnection);
                Project initialTargetProject = objectMapper.readValue(initialTargetResponse, Project.class);
                initialTargetConnection.disconnect();

                String projectsId = defaultProject.getId();
                String newProjectBodyJson = "{\"randomField\":\"randomValue\"}";

                HttpURLConnection connection = requestWithId(PROJECTS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                                projectsId, newProjectBodyJson);
                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String contentType = connection.getContentType();
                String responseBody = readResponse(connection);

                System.out.println("Response code: " + responseCode);
                System.out.println("Response message: " + responseMessage);
                System.out.println("Content-Type: " + contentType);
                System.out.println("Response body: " + responseBody);

                assertEquals(400, responseCode);
                assertEquals("Bad Request", responseMessage);
                assertEquals(JSON_FORMAT, contentType);
                assertEquals("{\"errorMessages\":[\"Could not find field: randomField\"]}", responseBody);

                // verify target project was not modified by failed request
                HttpURLConnection finalTargetConnection = requestWithId(PROJECTS_ENDPOINT, GET_METHOD, JSON_FORMAT, JSON_FORMAT, defaultProject.getId(), null);
                String finalTargetResponse = readResponse(finalTargetConnection);
                JsonProject finalTargetJsonProject = objectMapper.readValue(finalTargetResponse, JsonProject.class);
                Project finalTargetProject = finalTargetJsonProject.getProjects()[0]; // Get the first (and only) project from the array
                finalTargetConnection.disconnect();
                
                assertEquals("Target project should remain unchanged after failed update", initialTargetProject, finalTargetProject);

                // verify overall project state unchanged by error
                HttpURLConnection finalConnection = request(PROJECTS_ENDPOINT, GET_METHOD, JSON_FORMAT, JSON_FORMAT, null);
                String finalResponseBody = readResponse(finalConnection);
                JsonProject finalProjects = objectMapper.readValue(finalResponseBody, JsonProject.class);
                finalConnection.disconnect();
                
                assertEquals("Project count should remain unchanged after error", initialProjects.getProjects().length, finalProjects.getProjects().length);

                connection.disconnect();

                System.out.println("testPostRandomFieldProjectsIdJson passed.");
        }

        @Test
        public void testPostProjectsIdXml() throws Exception {
                System.out.println("Running testPostProjectsIdXml...");

                String projectsId = defaultProject.getId();
                ProjectBody newProjectBody = new ProjectBody("new project", false, true, "A new project");
                XmlMapper xmlMapper = new XmlMapper();
                String newProjectBodyXml = xmlMapper.writeValueAsString(newProjectBody);

                Project newProject = new Project(projectsId, newProjectBody.getTitle(), newProjectBody.isCompleted(),
                                newProjectBody.isActive(), newProjectBody.getDescription());

                HttpURLConnection connection = requestWithId(PROJECTS_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT,
                                projectsId, newProjectBodyXml);
                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String contentType = connection.getContentType();
                String responseBody = readResponse(connection);

                System.out.println("Response code: " + responseCode);
                System.out.println("Response message: " + responseMessage);
                System.out.println("Content-Type: " + contentType);
                System.out.println("Response body: " + responseBody);

                Project returnedProject = xmlMapper.readValue(responseBody, Project.class);

                HttpURLConnection connectionAll = request(PROJECTS_ENDPOINT, GET_METHOD, XML_FORMAT, XML_FORMAT, null);
                String responseBodyAll = readResponse(connectionAll);

                XmlProject allProjects = xmlMapper.readValue(responseBodyAll, XmlProject.class);

                assertEquals(200, responseCode);
                assertEquals("OK", responseMessage);
                assertEquals(XML_FORMAT, contentType);
                assertEquals(newProject, returnedProject);
                assertTrue(allProjects.contains(newProject));

                connection.disconnect();
                connectionAll.disconnect();

                System.out.println("testPostProjectsIdXml passed.");
        }

        @Test
        public void testPutProjectsIdJson() throws Exception {
                System.out.println("Running testPutProjectsIdJson...");

                String projectsId = defaultProject.getId();
                ProjectBody newProjectBody = new ProjectBody("new project", false, true, "A new project");
                XmlMapper xmlMapper = new XmlMapper();
                String newProjectBodyXml = xmlMapper.writeValueAsString(newProjectBody);

                Project newProject = new Project(projectsId, newProjectBody.getTitle(), newProjectBody.isCompleted(),
                                newProjectBody.isActive(), newProjectBody.getDescription());

                HttpURLConnection connection = requestWithId(PROJECTS_ENDPOINT, PUT_METHOD, XML_FORMAT, XML_FORMAT,
                                projectsId, newProjectBodyXml);
                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String contentType = connection.getContentType();
                String responseBody = readResponse(connection);

                System.out.println("Response code: " + responseCode);
                System.out.println("Response message: " + responseMessage);
                System.out.println("Content-Type: " + contentType);
                System.out.println("Response body: " + responseBody);

                Project returnedProject = xmlMapper.readValue(responseBody, Project.class);

                HttpURLConnection connectionAll = request(PROJECTS_ENDPOINT, GET_METHOD, XML_FORMAT, XML_FORMAT, null);
                String responseBodyAll = readResponse(connectionAll);

                XmlProject allProjects = xmlMapper.readValue(responseBodyAll, XmlProject.class);

                assertEquals(200, responseCode);
                assertEquals("OK", responseMessage);
                assertEquals(XML_FORMAT, contentType);
                assertEquals(newProject, returnedProject);
                assertTrue(allProjects.contains(newProject));

                connection.disconnect();
                connectionAll.disconnect();

                System.out.println("testPutProjectsIdJson passed.");
        }

        @Test
        public void testPutNoBodyProjectsIdJson() throws Exception {
                System.out.println("Running testPutNoBodyProjectsIdJson...");

                String projectsId = defaultProject.getId();

                HttpURLConnection connection = requestWithId(PROJECTS_ENDPOINT, PUT_METHOD, JSON_FORMAT, JSON_FORMAT,
                                projectsId, null);
                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String contentType = connection.getContentType();
                String responseBody = readResponse(connection);

                System.out.println("Response code: " + responseCode);
                System.out.println("Response message: " + responseMessage);
                System.out.println("Content-Type: " + contentType);
                System.out.println("Response body: " + responseBody);

                assertEquals(200, responseCode);
                assertEquals("OK", responseMessage);
                assertEquals(JSON_FORMAT, contentType);
                assertEquals("{\"id\":\"1\",\"title\":\"\",\"completed\":\"false\",\"active\":\"false\",\"description\":\"\"}",
                                responseBody);

                connection.disconnect();

                System.out.println("testPutNoBodyProjectsIdJson passed.");
        }

        @Test
        public void testPutRandomFieldProjectsIdJson() throws Exception {
                System.out.println("Running testPutRandomFieldProjectsIdJson...");

                String projectsId = defaultProject.getId();
                String newProjectBodyJson = "{\"randomField\":\"randomValue\"}";

                HttpURLConnection connection = requestWithId(PROJECTS_ENDPOINT, PUT_METHOD, JSON_FORMAT, JSON_FORMAT,
                                projectsId, newProjectBodyJson);
                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String contentType = connection.getContentType();
                String responseBody = readResponse(connection);

                System.out.println("Response code: " + responseCode);
                System.out.println("Response message: " + responseMessage);
                System.out.println("Content-Type: " + contentType);
                System.out.println("Response body: " + responseBody);

                assertEquals(400, responseCode);
                assertEquals("Bad Request", responseMessage);
                assertEquals(JSON_FORMAT, contentType);
                assertEquals("{\"errorMessages\":[\"Could not find field: randomField\"]}", responseBody);

                connection.disconnect();

                System.out.println("testPutRandomFieldProjectsIdJson passed.");
        }

        @Test
        public void testPutProjectsIdXml() throws Exception {
                System.out.println("Running testPutProjectsIdXml...");

                String projectsId = defaultProject.getId();
                ProjectBody newProjectBody = new ProjectBody("new project", false, true, "A new project");
                ObjectMapper objectMapper = new ObjectMapper();
                String newProjectBodyJson = objectMapper.writeValueAsString(newProjectBody);

                Project newProject = new Project(projectsId, newProjectBody.getTitle(), newProjectBody.isCompleted(),
                                newProjectBody.isActive(), newProjectBody.getDescription());

                HttpURLConnection connection = requestWithId(PROJECTS_ENDPOINT, PUT_METHOD, JSON_FORMAT, JSON_FORMAT,
                                projectsId, newProjectBodyJson);
                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String contentType = connection.getContentType();
                String responseBody = readResponse(connection);

                System.out.println("Response code: " + responseCode);
                System.out.println("Response message: " + responseMessage);
                System.out.println("Content-Type: " + contentType);
                System.out.println("Response body: " + responseBody);

                Project returnedProject = objectMapper.readValue(responseBody, Project.class);

                HttpURLConnection connectionAll = request(PROJECTS_ENDPOINT, GET_METHOD, JSON_FORMAT, JSON_FORMAT,
                                null);
                String responseBodyAll = readResponse(connectionAll);

                JsonProject allProjects = objectMapper.readValue(responseBodyAll, JsonProject.class);

                assertEquals(200, responseCode);
                assertEquals("OK", responseMessage);
                assertEquals(JSON_FORMAT, contentType);
                assertEquals(newProject, returnedProject);
                assertTrue(allProjects.contains(newProject));

                connection.disconnect();
                connectionAll.disconnect();

                System.out.println("testPutProjectsIdXml passed.");
        }

        @Test
        public void testDeleteProjectsIdJson() throws Exception {
                System.out.println("Running testDeleteProjectsIdJson...");

                // check initial project count
                HttpURLConnection initialProjectsConnection = request(PROJECTS_ENDPOINT, GET_METHOD, JSON_FORMAT, JSON_FORMAT, null);
                String initialProjectsResponse = readResponse(initialProjectsConnection);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonProject initialProjects = objectMapper.readValue(initialProjectsResponse, JsonProject.class);
                int initialProjectCount = initialProjects.getProjects().length;
                initialProjectsConnection.disconnect();

                String projectsId = defaultProject.getId();

                HttpURLConnection connection = requestWithId(PROJECTS_ENDPOINT, DELETE_METHOD, JSON_FORMAT, JSON_FORMAT,
                                projectsId, null);
                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String contentType = connection.getContentType();
                String responseBody = readResponse(connection);

                HttpURLConnection connectionAll = request(PROJECTS_ENDPOINT, GET_METHOD, JSON_FORMAT, JSON_FORMAT,
                                null);
                String responseBodyAll = readResponse(connectionAll);

                JsonProject allProjects = objectMapper.readValue(responseBodyAll, JsonProject.class);

                System.out.println("Response code: " + responseCode);
                System.out.println("Response message: " + responseMessage);
                System.out.println("Content-Type: " + contentType);
                System.out.println("Response body: " + responseBody);

                assertEquals(200, responseCode);
                assertEquals("OK", responseMessage);
                assertEquals(JSON_FORMAT, contentType);
                assertEquals("", responseBody);
                assertFalse(allProjects.contains(defaultProject));
                
                // verify project count decreased by 1
                assertEquals("Project count should decrease by 1", initialProjectCount - 1, allProjects.getProjects().length);

                connection.disconnect();
                connectionAll.disconnect();

                System.out.println("testDeleteProjectsIdJson passed.");
        }

        @Test
        // Test deleting the same object again
        public void testDeleteProjectsIdJsonTwice() throws Exception {
                System.out.println("Running testDeleteProjectsIdJsonTwice...");
                
                // check initial state
                HttpURLConnection initialConnection = request(PROJECTS_ENDPOINT, GET_METHOD, JSON_FORMAT, JSON_FORMAT, null);
                String initialResponse = readResponse(initialConnection);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonProject initialProjects = objectMapper.readValue(initialResponse, JsonProject.class);
                int initialCount = initialProjects.getProjects().length;
                initialConnection.disconnect();
                
                String projectsId = defaultProject.getId();

                // first delete
                HttpURLConnection connectionFirst = requestWithId(PROJECTS_ENDPOINT,
                                DELETE_METHOD, JSON_FORMAT, JSON_FORMAT,
                                projectsId, null);
                int responseCodeFirst = connectionFirst.getResponseCode();
                String responseMessageFirst = connectionFirst.getResponseMessage();
                String contentTypeFirst = connectionFirst.getContentType();
                String responseBodyFirst = readResponse(connectionFirst);
                assertEquals(200, responseCodeFirst);
                assertEquals("OK", responseMessageFirst);
                assertEquals(JSON_FORMAT, contentTypeFirst);
                assertEquals("", responseBodyFirst);
                connectionFirst.disconnect();

                // verify state after first delete
                HttpURLConnection afterFirstConnection = request(PROJECTS_ENDPOINT, GET_METHOD, JSON_FORMAT, JSON_FORMAT, null);
                String afterFirstResponse = readResponse(afterFirstConnection);
                JsonProject afterFirstProjects = objectMapper.readValue(afterFirstResponse, JsonProject.class);
                int afterFirstCount = afterFirstProjects.getProjects().length;
                afterFirstConnection.disconnect();
                assertEquals("First delete should decrease count by 1", initialCount - 1, afterFirstCount);

                // second delete
                HttpURLConnection connection = requestWithId(PROJECTS_ENDPOINT,
                                DELETE_METHOD, JSON_FORMAT, JSON_FORMAT,
                                projectsId, null);
                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String contentType = connection.getContentType();
                String responseBody = readResponse(connection);

                System.out.println("Response code: " + responseCode);
                System.out.println("Response message: " + responseMessage);
                System.out.println("Content-Type: " + contentType);
                System.out.println("Response body: " + responseBody);
                assertEquals(404, responseCode);
                assertEquals("Not Found", responseMessage);
                assertEquals(JSON_FORMAT, contentType);
                assertEquals("{\"errorMessages\":[\"Could not find any instances with projects/" + projectsId + "\"]}",
                                responseBody);
                
                // verify second delete doesn't affect remaining projects
                HttpURLConnection finalConnection = request(PROJECTS_ENDPOINT, GET_METHOD, JSON_FORMAT, JSON_FORMAT, null);
                String finalResponse = readResponse(finalConnection);
                JsonProject finalProjects = objectMapper.readValue(finalResponse, JsonProject.class);
                int finalCount = finalProjects.getProjects().length;
                finalConnection.disconnect();
                assertEquals("Second delete should not change project count", afterFirstCount, finalCount);
                assertEquals("Remaining projects should be unchanged", afterFirstResponse, finalResponse);
                
                connection.disconnect();
                System.out.println("testDeleteProjectsIdJsonTwice passed.");
        }

        // no need
        // @Test
        // public void testDeleteAllProjectsIdJson() throws Exception {
        // System.out.println("Running testDeleteAllProjectsIdJson...");

        // HttpURLConnection connectionGetAll = request(PROJECTS_ENDPOINT, GET_METHOD,
        // JSON_FORMAT, JSON_FORMAT, null);
        // String responseBodyGetAll = readResponse(connectionGetAll);
        // ObjectMapper objectMapperGetAll = new ObjectMapper();
        // JsonProject allProjectsGetAll =
        // objectMapperGetAll.readValue(responseBodyGetAll, JsonProject.class);
        // List<Project> projectsList = List.of(allProjectsGetAll.getProjects());
        // for (Project project : projectsList) {
        // if (!project.equals(defaultProject)) {
        // String projectsId = project.getId();
        // HttpURLConnection connectionDelete = requestWithId(PROJECTS_ENDPOINT,
        // DELETE_METHOD, JSON_FORMAT,
        // JSON_FORMAT,
        // projectsId, null);
        // int responseCodeDelete = connectionDelete.getResponseCode();
        // String responseMessageDelete = connectionDelete.getResponseMessage();
        // String contentTypeDelete = connectionDelete.getContentType();
        // String responseBodyDelete = readResponse(connectionDelete);

        // System.out.println("Deleted project ID: " + projectsId);
        // System.out.println("Response code: " + responseCodeDelete);
        // System.out.println("Response message: " + responseMessageDelete);
        // System.out.println("Content-Type: " + contentTypeDelete);
        // System.out.println("Response body: " + responseBodyDelete);

        // assertEquals(200, responseCodeDelete);
        // assertEquals("OK", responseMessageDelete);
        // assertEquals(JSON_FORMAT, contentTypeDelete);
        // assertEquals("", responseBodyDelete);

        // connectionDelete.disconnect();
        // }
        // }

        // // Create the default project again to ensure it exists
        // HttpURLConnection connectionPost = request(PROJECTS_ENDPOINT, POST_METHOD,
        // JSON_FORMAT, JSON_FORMAT, defaultProject.toStringJson());
        // int responseCodePost = connectionPost.getResponseCode();
        // String responseMessagePost = connectionPost.getResponseMessage();
        // String contentTypePost = connectionPost.getContentType();
        // String responseBodyPost = readResponse(connectionPost);

        // // Extract the new ID from the response body
        // JsonProject createdProject = objectMapperGetAll.readValue(responseBodyPost,
        // JsonProject.class);
        // String newId = createdProject.getProjects()[0].getId();

        // defaultProject.setId(newId);
        // System.out.println("Recreated default project.");
        // assertEquals(201, responseCodePost);
        // assertEquals("Created", responseMessagePost);
        // assertEquals(JSON_FORMAT, contentTypePost);
        // assertFalse(responseBodyPost.isEmpty());
        // connectionPost.disconnect();
        // System.out.println("testDeleteAllProjectsIdJson passed.");
        // }

        @Test
        public void testDeleteProjectsIdXml() throws Exception {
                System.out.println("Running testDeleteProjectsIdXml...");

                // // Check if the default project exists, if not create it
                // HttpURLConnection connectionPost = request(PROJECTS_ENDPOINT, POST_METHOD,
                // JSON_FORMAT, JSON_FORMAT, defaultProject.toStringJson());
                // int responseCodePost = connectionPost.getResponseCode();
                // if (responseCodePost == 201) {
                // String responseBodyPost = readResponse(connectionPost);
                // ObjectMapper objectMapperPost = new ObjectMapper();
                // JsonProject createdProject = objectMapperPost.readValue(responseBodyPost,
                // JsonProject.class);
                // String newId = createdProject.getProjects()[0].getId();
                // defaultProject.setId(newId);
                // System.out.println("Default project did not exist. Created with ID: " +
                // newId);
                // } else {
                // System.out.println("Default project already exists.");
                // }

                String projectsId = defaultProject.getId();

                HttpURLConnection connection = requestWithId(PROJECTS_ENDPOINT, DELETE_METHOD, XML_FORMAT, XML_FORMAT,
                                projectsId, null);
                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String contentType = connection.getContentType();
                String responseBody = readResponse(connection);

                HttpURLConnection connectionAll = request(PROJECTS_ENDPOINT, GET_METHOD, XML_FORMAT, XML_FORMAT, null);
                String responseBodyAll = readResponse(connectionAll);
                System.out.println("Response body all projects: " + responseBodyAll);

                XmlMapper xmlMapper = new XmlMapper();
                XmlProject allProjects = xmlMapper.readValue(responseBodyAll, XmlProject.class);

                System.out.println("Response code: " + responseCode);
                System.out.println("Response message: " + responseMessage);
                System.out.println("Content-Type: " + contentType);
                System.out.println("Response body: " + responseBody);

                assertEquals(200, responseCode);
                assertEquals("OK", responseMessage);
                assertEquals(XML_FORMAT, contentType);
                assertEquals("", responseBody);
                assertFalse(allProjects.contains(defaultProject));

                connection.disconnect();
                connectionAll.disconnect();

                System.out.println("testDeleteProjectsIdXml passed.");
        }
}
