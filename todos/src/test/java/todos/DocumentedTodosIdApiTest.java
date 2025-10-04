package todos;

import java.net.HttpURLConnection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import general.BaseApiTest;
import static general.CommonConstants.GET_METHOD;
import static general.CommonConstants.JSON_FORMAT;
import static general.CommonConstants.POST_METHOD;
import static general.CommonConstants.PUT_METHOD;
import static general.CommonConstants.TODOS_ENDPOINT;
import static general.CommonConstants.XML_FORMAT;
import static general.Utils.readResponse;
import static general.Utils.request;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DocumentedTodosIdApiTest extends BaseApiTest {

    // make a todo and get its id
    private String makeTodo() throws Exception {
        String body = "{\"title\":\"baseline-id\",\"description\":\"desc\"}";
        HttpURLConnection c = request(TODOS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT, body);
        assertEquals(201, c.getResponseCode());

        String resp = readResponse(c);
        ObjectMapper m = new ObjectMapper();
        JsonNode root = m.readTree(resp);
        String id = root.path("id").asText();
        assertNotNull("id null??", id);
        c.disconnect();
        return id;
    }

    // GET /todos/{id} json
    @Test
    public void testGetTodosIdJson() throws Exception {
        String baseId = makeTodo();
        String gotId = null;
        int tries = 0;

        while (tries < 5) {
            HttpURLConnection c = request(TODOS_ENDPOINT + "/" + baseId, GET_METHOD, JSON_FORMAT, JSON_FORMAT, null);
            String body = readResponse(c);
            ObjectMapper m = new ObjectMapper();
            JsonNode root = m.readTree(body);
            JsonNode arr = root.path("todos");
            if (arr.isArray() && arr.size() > 0) {
                gotId = arr.get(0).path("id").asText(null);
            }
            c.disconnect();
            if (gotId != null && !gotId.isEmpty()) break;
            Thread.sleep(200);
            tries++;
        }

        assertNotNull("should return id", gotId);
        assertEquals(baseId, gotId);
    }

    // GET /todos/{id} xml
    @Test
    public void testGetTodosIdXml() throws Exception {
        String baseId = makeTodo();
        String gotId = null;
        int tries = 0;

        while (tries < 5) {
            HttpURLConnection c = request(TODOS_ENDPOINT + "/" + baseId, GET_METHOD, XML_FORMAT, XML_FORMAT, null);
            String body = readResponse(c);
            XmlMapper xm = new XmlMapper();
            JsonNode root = xm.readTree(body.getBytes());
            JsonNode todo = root.path("todo");
            if (!todo.isMissingNode()) {
                gotId = todo.path("id").asText(null);
            }
            c.disconnect();
            if (gotId != null && !gotId.isEmpty()) break;
            Thread.sleep(200);
            tries++;
        }

        assertNotNull("should return id", gotId);
        assertEquals(baseId, gotId);
    }

    // PUT /todos/{id} xml
    @Test
    public void testPutTodosIdXml() throws Exception {
        String baseId = makeTodo();
        String newTitle = "updated-title";
        String newDesc = "updated-desc";
        String xml = "<todo><title>" + newTitle + "</title><description>" + newDesc + "</description></todo>";

        HttpURLConnection c = request(TODOS_ENDPOINT + "/" + baseId, PUT_METHOD, XML_FORMAT, XML_FORMAT, xml);
        int code = c.getResponseCode();
        String msg = c.getResponseMessage();
        String resp = readResponse(c);

        assertEquals(200, code);
        assertEquals("OK", msg);

        XmlMapper xm = new XmlMapper();
        JsonNode updated = xm.readTree(resp.getBytes());
        assertEquals(newTitle, updated.path("title").asText());
        assertEquals(newDesc, updated.path("description").asText());

        c.disconnect();
    }
}
