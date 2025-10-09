package todos;

import java.net.HttpURLConnection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import general.BaseApiTest;
import static general.CommonConstants.GET_METHOD;
import static general.CommonConstants.HEAD_METHOD;
import static general.CommonConstants.JSON_FORMAT;
import static general.CommonConstants.OPTIONS_METHOD;
import static general.CommonConstants.POST_METHOD;
import static general.CommonConstants.TODOS_EMPTY_TITLE_JSON;
import static general.CommonConstants.TODOS_ENDPOINT;
import static general.CommonConstants.TODOS_MISSING_TITLE_JSON;
import static general.CommonConstants.TODOS_OPTIONS;
import static general.CommonConstants.XML_FORMAT;
import static general.Utils.readResponse;
import static general.Utils.request;

/**
 *tests for /todos (json/xml, post/get/head/options + bad cases)
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DocumentedTodosApiTest extends BaseApiTest {

    // bad body json
    @Test
    public void testMalformedJsonBody() throws Exception {
        String bad = "{\"title\":\"Bad JSON\""; // missing }
        HttpURLConnection c = request(TODOS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT, bad);
        int code = c.getResponseCode();
        String msg = c.getResponseMessage();
        String body = readResponse(c);

        assertEquals(400, code);
        assertEquals("Bad Request", msg);
        assertTrue(body.contains("errorMessages"));
        c.disconnect();
    }

    // bad body xml
    @Test
    public void testMalformedXmlBody() throws Exception {
        String bad = "<todo><title>Missing ending tags";
        HttpURLConnection c = request(TODOS_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT, bad);
        int code = c.getResponseCode();
        String msg = c.getResponseMessage();
        String body = readResponse(c);

        assertEquals(400, code);
        assertEquals("Bad Request", msg);
        assertTrue(body.contains("errorMessages"));
        c.disconnect();
    }

    // GET /todos json
    @Test
    public void testGetTodosJson() throws Exception {
        HttpURLConnection c = request(TODOS_ENDPOINT, GET_METHOD, JSON_FORMAT, JSON_FORMAT, null);
        int code = c.getResponseCode();
        String msg = c.getResponseMessage();
        String ct = c.getContentType();
        String body = readResponse(c);

        ObjectMapper m = new ObjectMapper();
        JsonTodo todos = m.readValue(body, JsonTodo.class);

        assertEquals(200, code);
        assertEquals("OK", msg);
        assertTrue(ct.contains(JSON_FORMAT));
        assertNotNull(todos.getTodos());
        c.disconnect();
    }

    // GET /todos xml
    @Test
    public void testGetTodosXml() throws Exception {
        HttpURLConnection c = request(TODOS_ENDPOINT, GET_METHOD, XML_FORMAT, XML_FORMAT, null);
        int code = c.getResponseCode();
        String msg = c.getResponseMessage();
        String ct = c.getContentType();
        String body = readResponse(c);

        XmlMapper m = new XmlMapper();
        XmlTodo todos = m.readValue(body, XmlTodo.class);

        assertEquals(200, code);
        assertEquals("OK", msg);
        assertTrue(ct.contains(XML_FORMAT));
        assertNotNull(todos.getTodos());
        c.disconnect();
    }

    // HEAD json
    @Test
    public void testHeadTodosJson() throws Exception {
        HttpURLConnection c = request(TODOS_ENDPOINT, HEAD_METHOD, JSON_FORMAT, JSON_FORMAT, null);
        int code = c.getResponseCode();
        String msg = c.getResponseMessage();
        String ct = c.getContentType();
        String body = readResponse(c);

        assertEquals(200, code);
        assertEquals("OK", msg);
        if (ct != null) assertTrue(ct.contains(JSON_FORMAT));
        assertEquals("", body);
        c.disconnect();
    }

    // HEAD xml
    @Test
    public void testHeadTodosXml() throws Exception {
        HttpURLConnection c = request(TODOS_ENDPOINT, HEAD_METHOD, XML_FORMAT, XML_FORMAT, null);
        int code = c.getResponseCode();
        String msg = c.getResponseMessage();
        String ct = c.getContentType();
        String body = readResponse(c);

        assertEquals(200, code);
        assertEquals("OK", msg);
        if (ct != null) assertTrue(ct.contains(XML_FORMAT));
        assertEquals("", body);
        c.disconnect();
    }

    // POST json ok
    @Test
    public void testPostTodosJson() throws Exception {
        Todo.TodoBody tb = new Todo.TodoBody("New Task", "Something to do");
        ObjectMapper m = new ObjectMapper();
        String jb = m.writeValueAsString(tb);

        HttpURLConnection c = request(TODOS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT, jb);
        int code = c.getResponseCode();
        String msg = c.getResponseMessage();
        String ct = c.getContentType();
        String resp = readResponse(c);

        Todo created = m.readValue(resp, Todo.class);
        assertEquals(201, code);
        assertEquals("Created", msg);
        assertTrue(ct.contains(JSON_FORMAT));
        assertTrue(tb.bodySameAs(created));
        c.disconnect();
    }

    // POST xml ok (or fail 400)
    @Test
    public void testPostTodosXml() throws Exception {
        String title = "Another Task";
        String desc = "Do something else";
        String xml = "<todo><title>" + title + "</title><description>" + desc + "</description></todo>";

        HttpURLConnection c = request(TODOS_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT, xml);
        int code = c.getResponseCode();
        String msg = c.getResponseMessage();
        String ct = c.getContentType();
        String resp = readResponse(c);

        XmlMapper m = new XmlMapper();
        if (code == 201) {
            Todo created = m.readValue(resp, Todo.class);
            assertEquals("Created", msg);
            assertTrue(ct.contains(XML_FORMAT));
            assertEquals(title, created.getTitle());
            assertEquals(desc, created.getDescription());
        } else {
            assertEquals(400, code);
        }
        c.disconnect();
    }

    // POST json no title
    @Test
    public void testPostTodosNoTitleJson() throws Exception {
        String body = "{\"description\":\"No title provided\"}";
        HttpURLConnection c = request(TODOS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT, body);
        int code = c.getResponseCode();
        String msg = c.getResponseMessage();
        String resp = readResponse(c);

        assertEquals(400, code);
        assertEquals("Bad Request", msg);
        assertEquals(TODOS_MISSING_TITLE_JSON, resp);
        c.disconnect();
    }

    // POST xml no title
    @Test
    public void testPostTodosNoTitleXml() throws Exception {
        String xml = "<todo><description>No title provided</description></todo>";
        HttpURLConnection c = request(TODOS_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT, xml);
        int code = c.getResponseCode();
        String msg = c.getResponseMessage();
        String resp = readResponse(c);

        assertEquals(400, code);
        assertEquals("Bad Request", msg);
        assertTrue(resp.contains("title"));
        c.disconnect();
    }

    // POST json empty title
    @Test
    public void testPostTodosEmptyTitleJson() throws Exception {
        String body = "{\"title\":\"\",\"description\":\"Empty title\"}";
        HttpURLConnection c = request(TODOS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT, body);
        int code = c.getResponseCode();
        String msg = c.getResponseMessage();
        String resp = readResponse(c);

        assertEquals(400, code);
        assertEquals("Bad Request", msg);
        assertEquals(TODOS_EMPTY_TITLE_JSON, resp);
        c.disconnect();
    }

    // POST xml empty title
    @Test
    public void testPostTodosEmptyTitleXml() throws Exception {
        String xml = "<todo><title></title><description>Empty title</description></todo>";
        HttpURLConnection c = request(TODOS_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT, xml);
        int code = c.getResponseCode();
        String msg = c.getResponseMessage();
        String resp = readResponse(c);

        assertEquals(400, code);
        assertEquals("Bad Request", msg);
        assertTrue(resp.contains("title"));
        c.disconnect();
    }

    // OPTIONS
    @Test
    public void testOptionsTodos() throws Exception {
        HttpURLConnection c = request(TODOS_ENDPOINT, OPTIONS_METHOD, JSON_FORMAT, JSON_FORMAT, null);
        int code = c.getResponseCode();
        String msg = c.getResponseMessage();
        String allow = c.getHeaderField("Allow");

        assertEquals(200, code);
        assertEquals("OK", msg);
        assertEquals(TODOS_OPTIONS, allow);
        c.disconnect();
    }
}
