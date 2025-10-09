package todos;

import java.net.HttpURLConnection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import general.BaseApiTest;
import static general.CommonConstants.JSON_FORMAT;
import static general.CommonConstants.OPTIONS_METHOD;
import static general.CommonConstants.POST_METHOD;
import static general.CommonConstants.XML_FORMAT;
import static general.Utils.readResponse;
import static general.Utils.request;
import static general.Utils.requestWithId;
import static general.Utils.requestWithIdPATCH;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UndocumentedTodosIdApiTest extends BaseApiTest {

    // reuse same todo id for tests
    private static String baseId;

    @BeforeClass
    public static void setupTodo() throws Exception {
        String body = "{\"title\":\"Baseline Undoc ID\",\"description\":\"For undocumented ID tests\"}";
        HttpURLConnection c = request("todos", POST_METHOD, JSON_FORMAT, JSON_FORMAT, body);
        int code = c.getResponseCode();
        String resp = readResponse(c);
        c.disconnect();

        assertEquals(201, code);
        assertTrue(resp.contains("\"id\""));

        int i = resp.indexOf("\"id\"");
        int colon = resp.indexOf(":", i);
        int qs = resp.indexOf("\"", colon+1);
        int qe = resp.indexOf("\"", qs+1);
        baseId = resp.substring(qs+1, qe);
    }

    // patch /todos/{id} json
    @Test
    public void testPatchTodosIdJson() throws Exception {
        HttpURLConnection c = requestWithIdPATCH("todos", JSON_FORMAT, JSON_FORMAT, baseId);
        int code = c.getResponseCode();
        String msg = c.getResponseMessage();
        String resp = readResponse(c);

        assertEquals(405, code);
        assertEquals("Method Not Allowed", msg);
        assertEquals("", resp);
        c.disconnect();
    }

    // patch /todos/{id} xml
    @Test
    public void testPatchTodosIdXml() throws Exception {
        HttpURLConnection c = requestWithIdPATCH("todos", XML_FORMAT, XML_FORMAT, baseId);
        int code = c.getResponseCode();
        String msg = c.getResponseMessage();
        String resp = readResponse(c);

        assertEquals(405, code);
        assertEquals("Method Not Allowed", msg);
        assertEquals("", resp);
        c.disconnect();
    }

    // options /todos/{id} json
    @Test
    public void testOptionsTodosIdJson() throws Exception {
        HttpURLConnection c = requestWithId("todos", OPTIONS_METHOD, JSON_FORMAT, JSON_FORMAT, baseId, null);
        int code = c.getResponseCode();
        String msg = c.getResponseMessage();
        String allow = c.getHeaderField("Allow");
        String resp = readResponse(c);

        assertEquals(200, code);
        assertEquals("OK", msg);
        assertTrue(allow.contains("OPTIONS"));
        assertTrue(allow.contains("GET"));
        assertTrue(allow.contains("HEAD"));
        assertTrue(allow.contains("PUT"));
        assertTrue(allow.contains("DELETE"));
        assertEquals("", resp);
        c.disconnect();
    }

    // options /todos/{id} xml
    @Test
    public void testOptionsTodosIdXml() throws Exception {
        HttpURLConnection c = requestWithId("todos", OPTIONS_METHOD, XML_FORMAT, XML_FORMAT, baseId, null);
        int code = c.getResponseCode();
        String msg = c.getResponseMessage();
        String allow = c.getHeaderField("Allow");
        String resp = readResponse(c);

        assertEquals(200, code);
        assertEquals("OK", msg);
        assertTrue(allow.contains("OPTIONS"));
        assertTrue(allow.contains("GET"));
        assertTrue(allow.contains("HEAD"));
        assertTrue(allow.contains("PUT"));
        assertTrue(allow.contains("DELETE"));
        assertEquals("", resp);
        c.disconnect();
    }
}
