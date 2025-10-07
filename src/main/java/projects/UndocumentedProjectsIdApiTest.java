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
public class UndocumentedProjectsIdApiTest extends BaseApiTest {
    /* /projects/{id} endpoint tests */

    // Test PATCH /projects/{id} (not documented)

    @Test
    public void testPatchProjectsIdJson() throws Exception {
        System.out.println("Running testPatchProjectsIdJson...");

        String projectId = defaultProject.getId();

        HttpURLConnection connection = requestWithIdPATCH(PROJECTS_ENDPOINT, JSON_FORMAT, JSON_FORMAT, projectId);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);

        assertEquals(405, responseCode);
        assertEquals("Method Not Allowed", responseMessage);
        assertEquals("", responseBody);

        connection.disconnect();

        System.out.println("testPatchProjectsIdJson passed.");
    }

    @Test
    public void testPatchProjectsIdXml() throws Exception {
        System.out.println("Running testPatchProjectsIdXml...");

        String projectId = defaultProject.getId();

        HttpURLConnection connection = requestWithIdPATCH(PROJECTS_ENDPOINT, XML_FORMAT, XML_FORMAT, projectId);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);

        assertEquals(405, responseCode);
        assertEquals("Method Not Allowed", responseMessage);
        assertEquals("", responseBody);

        connection.disconnect();

        System.out.println("testPatchProjectsIdXml passed.");
    }

    @Test
    public void testOptionsProjectsIdJson() throws Exception {
        System.out.println("Running testOptionsProjectsIdJson...");

        String projectId = defaultProject.getId();

        HttpURLConnection connection = requestWithId(PROJECTS_ENDPOINT, OPTIONS_METHOD, JSON_FORMAT, JSON_FORMAT,
                projectId, null);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String allowHeader = connection.getHeaderField("Allow");
        String responseBody = readResponse(connection);

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertEquals(PROJECT_ID_OPTIONS, allowHeader);
        assertEquals("", responseBody);

        connection.disconnect();

        System.out.println("testOptionsProjectsIdJson passed.");
    }

    @Test
    public void testOptionsProjectsIdXml() throws Exception {
        System.out.println("Running testOptionsProjectsIdXml...");

        String projectId = defaultProject.getId();

        HttpURLConnection connection = requestWithId(PROJECTS_ENDPOINT, OPTIONS_METHOD, XML_FORMAT, XML_FORMAT,
                projectId, null);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String allowHeader = connection.getHeaderField("Allow");
        String responseBody = readResponse(connection);

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertEquals(PROJECT_ID_OPTIONS, allowHeader);
        assertEquals("", responseBody);

        connection.disconnect();

        System.out.println("testOptionsProjectsIdXml passed.");
    }
}
