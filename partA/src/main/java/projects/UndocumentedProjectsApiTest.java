package projects;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.*;
import org.junit.runners.MethodSorters;

import general.BaseApiTest;

import java.net.HttpURLConnection;

import static org.junit.Assert.*;

import static general.Utils.*;
import static general.CommonConstants.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UndocumentedProjectsApiTest extends BaseApiTest {

    @Test
    public void testPutProjectsJson() throws Exception {
        System.out.println("Running testPutProjectsJson...");

        Project existingProject = new Project(defaultProject.getId(), "new title", true, false, "new description");
        ObjectMapper objectMapper = new ObjectMapper();
        String projectJson = objectMapper.writeValueAsString(existingProject);

        HttpURLConnection connection = request(PROJECTS_ENDPOINT, PUT_METHOD, JSON_FORMAT, JSON_FORMAT, projectJson);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);

        assertEquals(405, responseCode);
        assertEquals("Method Not Allowed", responseMessage);
        assertEquals("", responseBody);

        connection.disconnect();

        System.out.println("testPutProjectsJson passed.");
    }

    @Test
    public void testPutProjectsXml() throws Exception {
        System.out.println("Running testPutProjectsXml...");

        Project existingProject = new Project(defaultProject.getId(), "new title", true, false, "new description");
        String projectXml = existingProject.toStringXml();

        HttpURLConnection connection = request(PROJECTS_ENDPOINT, PUT_METHOD, XML_FORMAT, XML_FORMAT, projectXml);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);

        assertEquals(405, responseCode);
        assertEquals("Method Not Allowed", responseMessage);
        assertEquals("", responseBody);

        connection.disconnect();

        System.out.println("testPutProjectsXml passed.");
    }

    @Test
    public void testDeleteProjectsJson() throws Exception {
        System.out.println("Running testDeleteProjectsJson...");

        String idBody = "{\"id\":\"" + defaultProject.getId() + "\"}";

        HttpURLConnection connection = request(PROJECTS_ENDPOINT, DELETE_METHOD, JSON_FORMAT, JSON_FORMAT, idBody);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);

        assertEquals(405, responseCode);
        assertEquals("Method Not Allowed", responseMessage);
        assertEquals("", responseBody);

        connection.disconnect();

        System.out.println("testDeleteProjectsJson passed.");
    }

    @Test
    public void testDeleteProjectsXml() throws Exception {
        System.out.println("Running testDeleteProjectsXml...");

        String idBody = "<project><id>" + defaultProject.getId() + "</id></project>";

        HttpURLConnection connection = request(PROJECTS_ENDPOINT, DELETE_METHOD, XML_FORMAT, XML_FORMAT, idBody);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);

        assertEquals(405, responseCode);
        assertEquals("Method Not Allowed", responseMessage);
        assertEquals("", responseBody);

        connection.disconnect();

        System.out.println("testDeleteProjectsXml passed.");
    }

    @Test
    public void testPatchProjectsJson() throws Exception {
        System.out.println("Running testPatchProjectsJson...");

        String patchBody = "{\"title\":\"patched title\"}";

        HttpURLConnection connection = requestPATCH(PROJECTS_ENDPOINT, JSON_FORMAT, JSON_FORMAT);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);

        assertEquals(405, responseCode);
        assertEquals("Method Not Allowed", responseMessage);
        assertEquals("", responseBody);

        connection.disconnect();

        System.out.println("testPatchProjectsJson passed.");
    }

    @Test
    public void testPatchProjectsXml() throws Exception {
        System.out.println("Running testPatchProjectsXml...");

        String patchBody = "<project><title>patched title</title></project>";

        HttpURLConnection connection = requestPATCH(PROJECTS_ENDPOINT, XML_FORMAT, XML_FORMAT);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);

        assertEquals(405, responseCode);
        assertEquals("Method Not Allowed", responseMessage);
        assertEquals("", responseBody);

        connection.disconnect();

        System.out.println("testPatchProjectsXml passed.");
    }

    @Test
    public void testOptionsProjectsJson() throws Exception {
        System.out.println("Running testOptionsProjectsJson...");

        HttpURLConnection connection = request(PROJECTS_ENDPOINT, OPTIONS_METHOD, JSON_FORMAT, JSON_FORMAT, null);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String allowHeader = connection.getHeaderField("Allow");
        String responseBody = readResponse(connection);

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertEquals(PROJECT_OPTIONS, allowHeader);
        assertEquals("", responseBody);

        connection.disconnect();

        System.out.println("testOptionsProjectsJson passed.");
    }

    @Test
    public void testOptionsProjectsXml() throws Exception {
        System.out.println("Running testOptionsProjectsXml...");

        HttpURLConnection connection = request(PROJECTS_ENDPOINT, OPTIONS_METHOD, XML_FORMAT, XML_FORMAT, null);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String allowHeader = connection.getHeaderField("Allow");
        String responseBody = readResponse(connection);

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertEquals(PROJECT_OPTIONS, allowHeader);
        assertEquals("", responseBody);

        connection.disconnect();

        System.out.println("testOptionsProjectsXml passed.");
    }

}
