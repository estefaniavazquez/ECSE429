/**
 * Todo API Unit Test Suite
 * Author: Najib Najib
 * simple junit tests for todo api
 covers server check, todos get/post/head/options, invalid put/delete, bad payloads,
 todos/:id crud and options/head/delete cases, categories+tasksof, cleanup after tests
*/

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

public class TodoApiTest {

    private static final String BASE_URL = "http://localhost:4567";
    private static String baselineTodoId;

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

    private String createTodo(String title) throws IOException {
        String json = "{\"title\":\"" + title + "\",\"description\":\"desc\"}";
        HttpURLConnection conn = makeRequest("/todos", "POST", json);
        String body = readResponse(conn);
        conn.disconnect();
        int idIndex = body.indexOf("\"id\"");
        if (idIndex == -1) return null;
        int colon = body.indexOf(":", idIndex);
        int qs = body.indexOf("\"", colon + 1);
        int qe = body.indexOf("\"", qs + 1);
        return body.substring(qs + 1, qe);
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
    public static void initBaselineTodo() throws IOException {
        TodoApiTest t = new TodoApiTest();
        baselineTodoId = t.createTodo("Baseline");
        assertNotNull("Failed to create baseline todo", baselineTodoId);
    }

    // get all todos json
    @Test
    public void testGetAllTodosJSON() throws IOException {
        HttpURLConnection conn = makeRequest("/todos", "GET", null);
        assertEquals(200, conn.getResponseCode());
        String body = readResponse(conn);
        conn.disconnect();
        assertTrue(body.contains("{"));
    }

    // create todo json
    @Test
    public void testCreateTodoJSON() throws IOException {
        String id = createTodo("JUnit JSON");
        assertNotNull(id);
    }

    // create todo xml
    @Test
    public void testCreateTodoXML() throws IOException {
        String xml = "<todo><title>XML Todo</title><description>desc</description></todo>";
        HttpURLConnection conn = makeRequest("/todos", "POST", xml, "application/xml");
        int code = conn.getResponseCode();
        String body = readResponse(conn);
        conn.disconnect();
        assertTrue(code == 200 || code == 201);
        assertTrue(body.contains("<id>"));
    }

    // get all todos xml
    @Test
    public void testGetAllTodosXML() throws IOException {
        HttpURLConnection conn = makeRequest("/todos", "GET", null, "application/xml");
        assertEquals(200, conn.getResponseCode());
        String body = readResponse(conn);
        conn.disconnect();
        assertTrue(body.contains("<todos>"));
    }

    // head todos
    @Test
    public void testHeadTodos() throws IOException {
        HttpURLConnection conn = makeRequest("/todos", "HEAD", null);
        int code = conn.getResponseCode();
        String body = readResponse(conn);
        conn.disconnect();
        assertTrue(code == 200 || code == 204);
        assertTrue(body.isEmpty());
    }

    // options todos
    @Test
    public void testOptionsTodos() throws IOException {
        HttpURLConnection conn = makeRequest("/todos", "OPTIONS", null);
        int code = conn.getResponseCode();
        String allow = conn.getHeaderField("Allow");
        conn.disconnect();
        assertEquals(200, code);
        assertNotNull(allow);
    }

    // put todos not allowed
    @Test 
    public void testPutNotAllowedOnTodos() throws IOException {
        HttpURLConnection conn = makeRequest("/todos", "PUT", "{}");
        int code = conn.getResponseCode();
        conn.disconnect();
        assertTrue(code == 404 || code == 405);
    }

    // delete todos not allowed
    @Test 
    public void testDeleteNotAllowedOnTodos() throws IOException {
        HttpURLConnection conn = makeRequest("/todos", "DELETE", null);
        int code = conn.getResponseCode();
        conn.disconnect();
        assertTrue(code == 404 || code == 405);
    }

    // malformed json
    @Test
    public void testMalformedJSON() throws IOException {
        HttpURLConnection conn = makeRequest("/todos", "POST", "{\"title\":");
        int code = conn.getResponseCode();
        conn.disconnect();
        assertTrue(code == 400 || code == 500);
    }

    // malformed xml
    @Test
    public void testMalformedXML() throws IOException {
        HttpURLConnection conn = makeRequest("/todos", "POST", "<todo><title>", "application/xml");
        int code = conn.getResponseCode();
        conn.disconnect();
        assertTrue(code == 400 || code == 500);
    }

    // get todo by id
    @Test
    public void testGetTodoById() throws IOException {
        HttpURLConnection conn = makeRequest("/todos/" + baselineTodoId, "GET", null);
        assertEquals(200, conn.getResponseCode());
        conn.disconnect();
    }

    // put todo valid
    @Test
    public void testPutTodoValid() throws IOException {
        String id = createTodo("PutMe");
        HttpURLConnection conn = makeRequest("/todos/" + id, "PUT", "{\"title\":\"Updated\"}");
        int code = conn.getResponseCode();
        String body = readResponse(conn);
        conn.disconnect();
        assertTrue(code == 200 || code == 201);
        assertTrue(body.contains("Updated"));
    }

    // put todo malformed
    @Test
    public void testPutTodoMalformed() throws IOException {
        String id = createTodo("BadPut");
        HttpURLConnection conn = makeRequest("/todos/" + id, "PUT", "{\"title\"");
        int code = conn.getResponseCode();
        conn.disconnect();
        assertTrue(code == 400 || code == 500);
    }

    // head todo id
    @Test
    public void testHeadTodoById() throws IOException {
        HttpURLConnection conn = makeRequest("/todos/" + baselineTodoId, "HEAD", null);
        int code = conn.getResponseCode();
        conn.disconnect();
        assertTrue(code == 200 || code == 204);
    }

    // options todo id
    @Test
    public void testOptionsTodoById() throws IOException {
        HttpURLConnection conn = makeRequest("/todos/" + baselineTodoId, "OPTIONS", null);
        int code = conn.getResponseCode();
        conn.disconnect();
        assertEquals(200, code);
    }

    // delete todo id
    @Test
    public void testDeleteTodoById() throws IOException {
        String id = createTodo("ToDelete");
        HttpURLConnection conn = makeRequest("/todos/" + id, "DELETE", null);
        int code = conn.getResponseCode();
        conn.disconnect();
        assertTrue(code == 200 || code == 204);
    }

    // invalid id
    @Test
    public void testGetInvalidTodo() throws IOException {
        HttpURLConnection conn = makeRequest("/todos/" + UUID.randomUUID(), "GET", null);
        int code = conn.getResponseCode();
        conn.disconnect();
        assertTrue(code == 400 || code == 404);
    }

    // double delete
    @Test
    public void testDoubleDeleteTodo() throws IOException {
        String id = createTodo("DoubleDelete");
        HttpURLConnection del1 = makeRequest("/todos/" + id, "DELETE", null);
        del1.getResponseCode();
        del1.disconnect();
        HttpURLConnection del2 = makeRequest("/todos/" + id, "DELETE", null);
        int code2 = del2.getResponseCode();
        del2.disconnect();
        assertTrue(code2 == 400 || code2 == 404);
    }

    // get categories
    @Test
    public void testGetCategoriesForTodo() throws IOException {
        HttpURLConnection conn = makeRequest("/todos/" + baselineTodoId + "/categories", "GET", null);
        int code = conn.getResponseCode();
        conn.disconnect();
        assertTrue(code == 200 || code == 404);
    }

    // options categories
    @Test
    public void testOptionsCategoriesForTodo() throws IOException {
        HttpURLConnection conn = makeRequest("/todos/" + baselineTodoId + "/categories", "OPTIONS", null);
        assertEquals(200, conn.getResponseCode());
        conn.disconnect();
    }

    // head categories
    @Test
    public void testHeadCategoriesForTodo() throws IOException {
        HttpURLConnection conn = makeRequest("/todos/" + baselineTodoId + "/categories", "HEAD", null);
        int code = conn.getResponseCode();
        conn.disconnect();
        assertTrue(code == 200 || code == 204);
    }

    // get tasksof
    @Test
    public void testGetTasksofForTodo() throws IOException {
        HttpURLConnection conn = makeRequest("/todos/" + baselineTodoId + "/tasksof", "GET", null);
        int code = conn.getResponseCode();
        conn.disconnect();
        assertTrue(code == 200 || code == 404);
    }

    // options tasksof
    @Test
    public void testOptionsTasksofForTodo() throws IOException {
        HttpURLConnection conn = makeRequest("/todos/" + baselineTodoId + "/tasksof", "OPTIONS", null);
        assertEquals(200, conn.getResponseCode());
        conn.disconnect();
    }

    // head tasksof
    @Test
    public void testHeadTasksofForTodo() throws IOException {
        HttpURLConnection conn = makeRequest("/todos/" + baselineTodoId + "/tasksof", "HEAD", null);
        int code = conn.getResponseCode();
        conn.disconnect();
        assertTrue(code == 200 || code == 204);
    }

    // cleanup
    @After
    public void restoreBaselineState() throws IOException {
        HttpURLConnection conn = makeRequest("/todos", "GET", null);
        String body = readResponse(conn);
        conn.disconnect();
        String[] ids = body.split("\"id\":\"");
        for (int i = 1; i < ids.length; i++) {
            String id = ids[i].split("\"")[0];
            if (!id.equals(baselineTodoId)) {
                HttpURLConnection del = makeRequest("/todos/" + id, "DELETE", null);
                del.getResponseCode();
                del.disconnect();
            }
        }
    }
}

