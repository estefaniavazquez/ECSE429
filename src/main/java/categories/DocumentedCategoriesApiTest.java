package categories;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.net.HttpURLConnection;

import org.junit.*;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.*;

import general.BaseApiTest;
import categories.Category.*;
import categories.XmlCategory.*;

import static general.Utils.*;
import static general.CommonConstants.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DocumentedCategoriesApiTest extends BaseApiTest {
    /*   /categories endpoint tests    */

    // Test malformed JSON/XML in request body
    @Test
    public void testMalformedJsonBody() throws Exception {
        System.out.println("Running testMalformedJsonBody...");

        String malformedJsonBody = "{\"title\":\"Malformed JSON\",\"description\":\"Missing ending brace\"";

        HttpURLConnection connection = request(CATEGORIES_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT, malformedJsonBody);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);

        assertEquals(400, responseCode);
        assertEquals("Bad Request", responseMessage);
        assertTrue(responseBody.contains("errorMessages"));

        connection.disconnect();

        System.out.println("testMalformedJsonBody passed.");
    }

    @Test
    public void testMalformedXmlBody() throws Exception {
        System.out.println("Running testMalformedXmlBody...");

        String malformedXmlBody = "<category><title>Malformed XML</title><description>Missing ending tag</description>";

        HttpURLConnection connection = request(CATEGORIES_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT, malformedXmlBody);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);

        assertEquals(400, responseCode);
        assertEquals("Bad Request", responseMessage);
        assertTrue(responseBody.contains("errorMessages"));

        connection.disconnect();

        System.out.println("testMalformedXmlBody passed.");
    }

    // Test GET /categories

    @Test
    public void testGetCategoriesJson() throws Exception {
        System.out.println("Running testGetCategoriesJson...");

        HttpURLConnection connection = request(CATEGORIES_ENDPOINT, GET_METHOD, JSON_FORMAT, JSON_FORMAT, null);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String contentType = connection.getContentType();
        String responseBody = readResponse(connection);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonCategory categories = objectMapper.readValue(responseBody, JsonCategory.class);

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertTrue(contentType.contains(JSON_FORMAT));
        assertTrue(categories.areIn(defaultCategories));

        connection.disconnect();

        System.out.println("testGetCategoriesJson passed.");
    }

    @Test
    public void testGetCategoriesXml() throws Exception {
        System.out.println("Running testGetCategoriesXml...");

        HttpURLConnection connection = request(CATEGORIES_ENDPOINT, GET_METHOD, XML_FORMAT, XML_FORMAT, null);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String contentType = connection.getContentType();
        String responseBody = readResponse(connection);

        XmlMapper xmlMapper = new XmlMapper();
        XmlCategory categories = xmlMapper.readValue(responseBody, XmlCategory.class);

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertTrue(contentType.contains(XML_FORMAT));
        assertTrue(categories.areIn(defaultCategories));

        connection.disconnect();

        System.out.println("testGetCategoriesXml passed.");
    }

    // Test HEAD /categories

    @Test
    public void testHeadCategoriesJson() throws Exception {
        System.out.println("Running testHeadCategoriesJson...");

        HttpURLConnection connection = request(CATEGORIES_ENDPOINT, HEAD_METHOD, JSON_FORMAT, JSON_FORMAT, null);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String contentType = connection.getContentType();
        String responseBody = readResponse(connection);

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertTrue(contentType.contains(JSON_FORMAT));
        assertEquals("", responseBody);

        connection.disconnect();

        System.out.println("testHeadCategoriesJson passed.");
    }

    @Test
    public void testHeadCategoriesXml() throws Exception {
        System.out.println("Running testHeadCategoriesXml...");

        HttpURLConnection connection = request(CATEGORIES_ENDPOINT, HEAD_METHOD, XML_FORMAT, XML_FORMAT, null);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String contentType = connection.getContentType();
        String responseBody = readResponse(connection);

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertTrue(contentType.contains(XML_FORMAT));
        assertEquals("", responseBody);

        connection.disconnect();

        System.out.println("testHeadCategoriesXml passed.");
    }

    // Test POST /categories

    @Test
    public void testPostCategoriesJson() throws Exception {
        System.out.println("Running testPostCategoriesJson...");

        CategoryBody body = new CategoryBody("Fitness", "Fitness related tasks");
        ObjectMapper objectMapper = new ObjectMapper();
        String bodyJson = objectMapper.writeValueAsString(body);

        HttpURLConnection connection = request(CATEGORIES_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT, bodyJson);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String contentType = connection.getContentType();
        String responseBody = readResponse(connection);

        Category createdCategory = objectMapper.readValue(responseBody, Category.class);

        // Verify the category was added and nothing else was changed
        HttpURLConnection connectionAll = request(CATEGORIES_ENDPOINT, GET_METHOD, JSON_FORMAT, JSON_FORMAT, null);
        String allResponseBody = readResponse(connectionAll);
        JsonCategory allCategories = objectMapper.readValue(allResponseBody, JsonCategory.class);

        assertEquals(201, responseCode);
        assertEquals("Created", responseMessage);
        assertTrue(contentType.contains(JSON_FORMAT));
        assertTrue(body.bodySameAsCategory(createdCategory));
        assertTrue(allCategories.areIn(new Category[]{createdCategory, officeCategory, homeCategory}));

        connection.disconnect();
        connectionAll.disconnect();

        System.out.println("testPostCategoriesJson passed.");
    }

    @Test
    public void testPostCategoriesXml() throws Exception {
        System.out.println("Running testPostCategoriesXml...");

        CategoryBody body = new CategoryBody("Work", "Work related tasks");
        XmlMapper xmlMapper = new XmlMapper();
        String bodyXml = xmlMapper.writeValueAsString(body);

        HttpURLConnection connection = request(CATEGORIES_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT, bodyXml);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String contentType = connection.getContentType();
        String responseBody = readResponse(connection);

        Category createdCategory = xmlMapper.readValue(responseBody, Category.class);

        // Verify the category was added and nothing else was changed
        HttpURLConnection connectionAll = request(CATEGORIES_ENDPOINT, GET_METHOD, XML_FORMAT, XML_FORMAT, null);
        String allResponseBody = readResponse(connectionAll);
        XmlCategory allCategories = xmlMapper.readValue(allResponseBody, XmlCategory.class);

        assertEquals(201, responseCode);
        assertEquals("Created", responseMessage);
        assertTrue(contentType.contains(XML_FORMAT));
        assertTrue(body.bodySameAsCategory(createdCategory));
        assertTrue(allCategories.areIn(new Category[]{createdCategory, officeCategory, homeCategory}));

        connection.disconnect();
        connectionAll.disconnect();

        System.out.println("testPostCategoriesXml passed.");
    }

    @Test
    public void testPostCategoriesDuplicateBodyJson() throws Exception {
        System.out.println("Running testPostCategoriesDuplicateBodyJson...");

        CategoryBody body = new CategoryBody("Title", "Duplicate title test");
        ObjectMapper objectMapper = new ObjectMapper();
        String bodyJson = objectMapper.writeValueAsString(body);

        HttpURLConnection connection1 = request(CATEGORIES_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT, bodyJson);
        String responseBody1 = readResponse(connection1);

        Category createdCategory1 = objectMapper.readValue(responseBody1, Category.class);

        HttpURLConnection connection2 = request(CATEGORIES_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT, bodyJson);
        int responseCode2 = connection2.getResponseCode();
        String responseMessage2 = connection2.getResponseMessage();
        String contentType2 = connection2.getContentType();
        String responseBody2 = readResponse(connection2);

        Category createdCategory2 = objectMapper.readValue(responseBody2, Category.class);

        HttpURLConnection connectionAll = request(CATEGORIES_ENDPOINT, GET_METHOD, JSON_FORMAT, JSON_FORMAT, null);
        String allResponseBody = readResponse(connectionAll);

        JsonCategory allCategories = objectMapper.readValue(allResponseBody, JsonCategory.class);

        assertEquals(201, responseCode2);
        assertEquals("Created", responseMessage2);
        assertTrue(contentType2.contains(JSON_FORMAT));
        assertTrue(body.bodySameAsCategory(createdCategory2));
        assertNotEquals(createdCategory1.getId(), createdCategory2.getId());
        assertTrue(allCategories.areIn(new Category[]{createdCategory1, createdCategory2, officeCategory, homeCategory}));

        connection1.disconnect();
        connection2.disconnect();
        connectionAll.disconnect();

        System.out.println("testPostCategoriesDuplicateBodyJson passed.");
    }

    @Test
    public void testPostCategoriesDuplicateBodyXml() throws Exception {
        System.out.println("Running testPostCategoriesDuplicateBodyXml...");

        CategoryBody body = new CategoryBody("Title", "Duplicate title test");
        XmlMapper xmlMapper = new XmlMapper();
        String bodyXml = xmlMapper.writeValueAsString(body);

        HttpURLConnection connection1 = request(CATEGORIES_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT, bodyXml);
        String responseBody1 = readResponse(connection1);

        Category createdCategory1 = xmlMapper.readValue(responseBody1, Category.class);

        HttpURLConnection connection2 = request(CATEGORIES_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT, bodyXml);
        int responseCode2 = connection2.getResponseCode();
        String responseMessage2 = connection2.getResponseMessage();
        String contentType2 = connection2.getContentType();
        String responseBody2 = readResponse(connection2);

        Category createdCategory2 = xmlMapper.readValue(responseBody2, Category.class);

        HttpURLConnection connectionAll = request(CATEGORIES_ENDPOINT, GET_METHOD, XML_FORMAT, XML_FORMAT, null);
        String allResponseBody = readResponse(connectionAll);

        XmlCategory allCategories = xmlMapper.readValue(allResponseBody, XmlCategory.class);

        assertEquals(201, responseCode2);
        assertEquals("Created", responseMessage2);
        assertTrue(contentType2.contains(XML_FORMAT));
        assertTrue(body.bodySameAsCategory(createdCategory2));
        assertNotEquals(createdCategory1.getId(), createdCategory2.getId());
        assertTrue(allCategories.areIn(new Category[]{createdCategory1, createdCategory2, officeCategory, homeCategory}));

        connection1.disconnect();
        connection2.disconnect();
        connectionAll.disconnect();

        System.out.println("testPostCategoriesDuplicateBodyXml passed.");
    }

    @Test
    public void testPostCategoriesNoTitleFailureJson() throws Exception {
        System.out.println("Running testPostCategoriesNoTitleFailureJson...");

        String descriptionBody = "{\"description\":\"No title test\"}";
        HttpURLConnection connection = request(CATEGORIES_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT, descriptionBody);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);

        assertEquals(400, responseCode);
        assertEquals("Bad Request", responseMessage);
        assertEquals(CATEGORIES_MISSING_TITLE_JSON, responseBody);

        connection.disconnect();

        System.out.println("testPostCategoriesNoTitleFailureJson passed.");
    }

    @Test
    public void testPostCategoriesNoTitleFailureXml() throws Exception {
        System.out.println("Running testPostCategoriesNoTitleFailureXml...");

        String descriptionBody = "<category><description>No title test</description></category>";
        HttpURLConnection connection = request(CATEGORIES_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT, descriptionBody);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);

        assertEquals(400, responseCode);
        assertEquals("Bad Request", responseMessage);
        assertEquals(CATEGORIES_MISSING_TITLE_XML, responseBody);

        connection.disconnect();

        System.out.println("testPostCategoriesNoTitleFailureXml passed.");
    }

    @Test
    public void testPostCategoriesEmptyTitleFailureJson() throws Exception {
        System.out.println("Running testPostCategoriesEmptyTitleFailureJson...");

        String emptyTitleBody = "{\"title\":\"\",\"description\":\"Empty title test\"}";
        HttpURLConnection connection = request(CATEGORIES_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT, emptyTitleBody);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);

        assertEquals(400, responseCode);
        assertEquals("Bad Request", responseMessage);
        assertEquals(CATEGORIES_EMPTY_TITLE_JSON, responseBody);

        connection.disconnect();

        System.out.println("testPostCategoriesEmptyTitleFailureJson passed.");
    }

    @Test
    public void testPostCategoriesEmptyTitleFailureXml() throws Exception {
        System.out.println("Running testPostCategoriesEmptyTitleFailureXml...");

        String emptyTitleBody = "<category><title></title><description>Empty title test</description></category>";
        HttpURLConnection connection = request(CATEGORIES_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT, emptyTitleBody);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);

        assertEquals(400, responseCode);
        assertEquals("Bad Request", responseMessage);
        assertEquals(CATEGORIES_EMPTY_TITLE_XML, responseBody);

        connection.disconnect();

        System.out.println("testPostCategoriesEmptyTitleFailureXml passed.");
    }
}
