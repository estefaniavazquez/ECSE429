/**
 * Category API Unit Test Suite
 * Author: William Zhang
 * simple junit tests for todo manager categories api
 covers server check, category get/post/head/options, category/:id crud and options/head/delete cases,
 with getters checks for category->projects and category->todos, cleanup after tests
*/

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

public class CategoryApiTest {
    private static final String BASE_URL = "http://localhost:4567";
    private static String newCategoryId;

    // helpers
    private HttpURLConnection makeRequest(String endpoint, String method, String body, String contentType) throws IOException {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", contentType);
        conn.setRequestProperty("Accept", contentType);
        conn.setDoInput(true);
        if (body != null && !body.isEmpty()) {
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }
        }
        return conn;
    }

    private HttpURLConnection makeRequest(String endpoint, String method, String body) throws IOException {
        return makeRequest(endpoint, method, body, "application/json");
    }

    private String readResponse(HttpURLConnection conn) throws IOException {
        InputStream is = (conn.getResponseCode() >= 200 && conn.getResponseCode() < 400)
                ? conn.getInputStream() : conn.getErrorStream();
        if (is == null) return "";
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }

    // Do this because I am testing multiple types of inputs
    private String createCategory(String categoryJson) throws IOException {
        HttpURLConnection conn = makeRequest("/categories", "POST", categoryJson);
        String body = readResponse(conn);
        conn.disconnect();
        return body;
    }

    // setup server + baseline todo
    @BeforeClass
    public static void serviceCheck() throws IOException {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(BASE_URL + "/todos").openConnection();
            conn.setRequestMethod("GET");
            assertEquals("❌ Server is not running.", 200, conn.getResponseCode());
            conn.disconnect();
        } catch (ConnectException e) {
            fail("Start server first with: java -jar runTodoManagerRestAPI-1.5.5.jar");
        }
    }

    @BeforeClass
    public static void initNewCategory() throws IOException {
        CategoryApiTest testInstance = new CategoryApiTest();
        String response = testInstance.createCategory("{\"title\":\"TestCategory\",\"description\":\"For unit testing\"}");
        assertTrue("Failed to create initial category", response.contains("\"id\":"));
    }

    // get all categories json
    @Test
    public void testGetAllCategoriesJSON() throws IOException {
        HttpURLConnection conn = makeRequest("/categories", "GET", null);
        String response = readResponse(conn);
        conn.disconnect();
        assertEquals(200, conn.getResponseCode());
        assertTrue("Failed to retrieve categories", response.contains("{:"));
    }

    // get all categories xml
    @Test
    public void testGetAllCategoriesXML() throws IOException {
        HttpURLConnection conn = makeRequest("/categories", "GET", null, "application/xml");
        String response = readResponse(conn);
        conn.disconnect();
        assertEquals(200, conn.getResponseCode());
        assertTrue("Failed to retrieve categories", response.contains("<categories>"));
    }

}
