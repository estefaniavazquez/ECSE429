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

        System.out.println(responseBody);

        XmlMapper xmlMapper = new XmlMapper();
        XmlCategory categories = xmlMapper.readValue(responseBody, XmlCategory.class);
        System.out.println(categories);

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

        assertEquals(201, responseCode);
        assertEquals("Created", responseMessage);
        assertTrue(contentType.contains(JSON_FORMAT));
        assertTrue(body.bodySameAsCategory(createdCategory));

        connection.disconnect();

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

        assertEquals(201, responseCode);
        assertEquals("Created", responseMessage);
        assertTrue(contentType.contains(XML_FORMAT));
        assertTrue(body.bodySameAsCategory(createdCategory));

        connection.disconnect();

        System.out.println("testPostCategoriesXml passed.");
    }

    @Test
    public void testPostCategoriesDuplicateBodyJson() throws Exception {
        System.out.println("Running testPostCategoriesDuplicateBodyJson...");

        CategoryBody body = new CategoryBody("Title", "Duplicate title test");
        ObjectMapper objectMapper = new ObjectMapper();
        String bodyJson = objectMapper.writeValueAsString(body);

        HttpURLConnection connection1 = request(CATEGORIES_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT, bodyJson);
        HttpURLConnection connection2 = request(CATEGORIES_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT, bodyJson);
        int responseCode = connection2.getResponseCode();
        String responseMessage = connection2.getResponseMessage();
        String contentType = connection2.getContentType();
        String responseBody = readResponse(connection2);

        Category createdCategory = objectMapper.readValue(responseBody, Category.class);

        assertEquals(201, responseCode);
        assertEquals("Created", responseMessage);
        assertTrue(contentType.contains(JSON_FORMAT));
        assertTrue(body.bodySameAsCategory(createdCategory));

        connection1.disconnect();
        connection2.disconnect();

        System.out.println("testPostCategoriesDuplicateBodyJson passed.");
    }

    @Test
    public void testPostCategoriesDuplicateBodyXml() throws Exception {
        System.out.println("Running testPostCategoriesDuplicateBodyXml...");

        CategoryBody body = new CategoryBody("Title", "Duplicate title test");
        XmlMapper xmlMapper = new XmlMapper();
        String bodyXml = xmlMapper.writeValueAsString(body);

        HttpURLConnection connection1 = request(CATEGORIES_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT, bodyXml);
        HttpURLConnection connection2 = request(CATEGORIES_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT, bodyXml);
        int responseCode = connection2.getResponseCode();
        String responseMessage = connection2.getResponseMessage();
        String contentType = connection2.getContentType();
        String responseBody = readResponse(connection2);

        Category createdCategory = xmlMapper.readValue(responseBody, Category.class);

        assertEquals(201, responseCode);
        assertEquals("Created", responseMessage);
        assertTrue(contentType.contains(XML_FORMAT));
        assertTrue(body.bodySameAsCategory(createdCategory));

        connection1.disconnect();
        connection2.disconnect();

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
        assertEquals("{\"errorMessages\":[\"title : field is mandatory\"]}", responseBody);

        connection.disconnect();

        System.out.println("testPostCategoriesNoTitleFailureJson passed.");
    }

    @Test
    public void testPostCategoriesNoTitleFailureXml() throws Exception {
        System.out.println("Running testPostCategoriesNoTitleFailureXml...");

        String descriptionBody = "<CategoryBody><description>No title test</description></CategoryBody>";
        HttpURLConnection connection = request(CATEGORIES_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT, descriptionBody);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);

        assertEquals(400, responseCode);
        assertEquals("Bad Request", responseMessage);
        assertEquals("<errorMessages><errorMessage>title : field is mandatory</errorMessage></errorMessages>", responseBody);

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
        assertTrue(responseBody.contains(": can not be empty"));

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
        assertTrue(responseBody.contains(": can not be empty"));

        connection.disconnect();

        System.out.println("testPostCategoriesEmptyTitleFailureXml passed.");
    }
}
