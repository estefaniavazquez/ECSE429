package projects;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.net.HttpURLConnection;

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

        HttpURLConnection connection = requestWithId(PROJECTS_ENDPOINT, GET_METHOD,
                JSON_FORMAT, JSON_FORMAT, defaultProject.getId(), null);
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

        ObjectMapper objectMapper = new ObjectMapper();
        Project project = objectMapper.readValue(responseBody, Project.class);
        assertEquals(defaultProject.getId(), project.getId());
        assertEquals(defaultProject.getTitle(), project.getTitle());
        assertEquals(defaultProject.isCompleted(), project.isCompleted());
        assertEquals(defaultProject.isActive(), project.isActive());
        assertEquals(defaultProject.getDescription(), project.getDescription());

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
        Project project = xmlMapper.readValue(responseBody, Project.class);
        assertEquals(defaultProject.getId(), project.getId());
        assertEquals(defaultProject.getTitle(), project.getTitle());
        assertEquals(defaultProject.isCompleted(), project.isCompleted());
        assertEquals(defaultProject.isActive(), project.isActive());
        assertEquals(defaultProject.getDescription(), project.getDescription());

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

        String projectsId = defaultProject.getId();
        ProjectBody newProjectBody = new ProjectBody("new project", false, true, "A new project");
        ObjectMapper objectMapper = new ObjectMapper();
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

        HttpURLConnection connectionAll = request(PROJECTS_ENDPOINT, GET_METHOD, JSON_FORMAT, JSON_FORMAT, null);
        String responseBodyAll = readResponse(connectionAll);

        JsonProject allProjects = objectMapper.readValue(responseBodyAll, JsonProject.class);

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertEquals(JSON_FORMAT, contentType);
        assertEquals(newProject, returnedProject);
        assertTrue(allProjects.contains(newProject));

        connection.disconnect();
        connectionAll.disconnect();

        System.out.println("testPostProjectsIdJson passed.");
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

        HttpURLConnection connectionAll = request(PROJECTS_ENDPOINT, GET_METHOD, JSON_FORMAT, JSON_FORMAT, null);
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

        String projectsId = defaultProject.getId();

        HttpURLConnection connection = requestWithId(PROJECTS_ENDPOINT, DELETE_METHOD, JSON_FORMAT, JSON_FORMAT,
                projectsId, null);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String contentType = connection.getContentType();
        String responseBody = readResponse(connection);

        HttpURLConnection connectionAll = request(PROJECTS_ENDPOINT, GET_METHOD, JSON_FORMAT, JSON_FORMAT, null);
        String responseBodyAll = readResponse(connectionAll);

        ObjectMapper objectMapper = new ObjectMapper();
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

        connection.disconnect();
        connectionAll.disconnect();

        System.out.println("testDeleteProjectsIdJson passed.");
    }

    @Test
    public void testDeleteProjectsIdXml() throws Exception {
        System.out.println("Running testDeleteProjectsIdXml...");

        String projectsId = defaultProject.getId();

        HttpURLConnection connection = requestWithId(PROJECTS_ENDPOINT, DELETE_METHOD, XML_FORMAT, XML_FORMAT,
                projectsId, null);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String contentType = connection.getContentType();
        String responseBody = readResponse(connection);

        HttpURLConnection connectionAll = request(PROJECTS_ENDPOINT, GET_METHOD, XML_FORMAT, XML_FORMAT, null);
        String responseBodyAll = readResponse(connectionAll);

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
