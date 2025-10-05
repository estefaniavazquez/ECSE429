package todos;

import java.net.HttpURLConnection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.fasterxml.jackson.databind.ObjectMapper;

import static general.CommonConstants.DELETE_METHOD;
import static general.CommonConstants.JSON_FORMAT;
import static general.CommonConstants.OPTIONS_METHOD;
import static general.CommonConstants.PUT_METHOD;
import static general.CommonConstants.XML_FORMAT;
import static general.Utils.readResponse;
import static general.Utils.request;
import static general.Utils.requestPATCH;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UndocumentedTodosApiTest extends general.BaseApiTest {

    // put /todos json (should fail)
    @Test
    public void testPutTodosJson() throws Exception {
        Todo t = new Todo(); // empty body
        ObjectMapper m = new ObjectMapper();
        String body = m.writeValueAsString(t);

        HttpURLConnection c = request("todos", PUT_METHOD, JSON_FORMAT, JSON_FORMAT, body);
        int code = c.getResponseCode();
        String msg = c.getResponseMessage();
        String resp = readResponse(c);

        assertTrue(code == 404 || code == 405);
        assertTrue(msg.equals("Not Found") || msg.equals("Method Not Allowed"));
        assertEquals("", resp);
        c.disconnect();
    }

    // put /todos xml (should fail)
    @Test
    public void testPutTodosXml() throws Exception {
        String xml = "<todo><title>ShouldFail</title></todo>";
        HttpURLConnection c = request("todos", PUT_METHOD, XML_FORMAT, XML_FORMAT, xml);
        int code = c.getResponseCode();
        String msg = c.getResponseMessage();
        String resp = readResponse(c);

        assertTrue(code == 404 || code == 405);
        assertTrue(msg.equals("Not Found") || msg.equals("Method Not Allowed"));
        assertEquals("", resp);
        c.disconnect();
    }

    // delete /todos json
    @Test
    public void testDeleteTodosJson() throws Exception {
        String body = "{\"id\":\"123\"}";
        HttpURLConnection c = request("todos", DELETE_METHOD, JSON_FORMAT, JSON_FORMAT, body);
        int code = c.getResponseCode();
        String msg = c.getResponseMessage();
        String resp = readResponse(c);

        assertTrue(code == 404 || code == 405);
        assertTrue(msg.equals("Not Found") || msg.equals("Method Not Allowed"));
        assertEquals("", resp);
        c.disconnect();
    }

    // delete /todos xml
    @Test
    public void testDeleteTodosXml() throws Exception {
        String xml = "<todo><id>123</id></todo>";
        HttpURLConnection c = request("todos", DELETE_METHOD, XML_FORMAT, XML_FORMAT, xml);
        int code = c.getResponseCode();
        String msg = c.getResponseMessage();
        String resp = readResponse(c);

        assertTrue(code == 404 || code == 405);
        assertTrue(msg.equals("Not Found") || msg.equals("Method Not Allowed"));
        assertEquals("", resp);
        c.disconnect();
    }

    // patch /todos json
    @Test
    public void testPatchTodosJson() throws Exception {
        HttpURLConnection c = requestPATCH("todos", JSON_FORMAT, JSON_FORMAT);
        int code = c.getResponseCode();
        String msg = c.getResponseMessage();
        String resp = readResponse(c);

        assertEquals(405, code);
        assertEquals("Method Not Allowed", msg);
        assertEquals("", resp);
        c.disconnect();
    }

    // patch /todos xml
    @Test
    public void testPatchTodosXml() throws Exception {
        HttpURLConnection c = requestPATCH("todos", XML_FORMAT, XML_FORMAT);
        int code = c.getResponseCode();
        String msg = c.getResponseMessage();
        String resp = readResponse(c);

        assertEquals(405, code);
        assertEquals("Method Not Allowed", msg);
        assertEquals("", resp);
        c.disconnect();
    }

    // options /todos json
    @Test
    public void testOptionsTodosJson() throws Exception {
        HttpURLConnection c = request("todos", OPTIONS_METHOD, JSON_FORMAT, JSON_FORMAT, null);
        int code = c.getResponseCode();
        String msg = c.getResponseMessage();
        String allow = c.getHeaderField("Allow");

        assertEquals(200, code);
        assertEquals("OK", msg);
        assertTrue(allow.contains("OPTIONS"));
        assertTrue(allow.contains("GET"));
        assertTrue(allow.contains("POST"));
        c.disconnect();
    }

    // options /todos xml
    @Test
    public void testOptionsTodosXml() throws Exception {
        HttpURLConnection c = request("todos", OPTIONS_METHOD, XML_FORMAT, XML_FORMAT, null);
        int code = c.getResponseCode();
        String msg = c.getResponseMessage();
        String allow = c.getHeaderField("Allow");

        assertEquals(200, code);
        assertEquals("OK", msg);
        assertTrue(allow.contains("OPTIONS"));
        assertTrue(allow.contains("GET"));
        assertTrue(allow.contains("POST"));
        c.disconnect();
    }
}
