package categories;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.net.HttpURLConnection;

import static org.junit.Assert.*;

import general.BaseApiTest;
import categories.Category.*;

import static general.CommonConstants.*;
import static general.Utils.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DocumentedCategoriesIdApiTest extends BaseApiTest {
    /*   /categories/:id endpoint tests   */

    // Test GET /categories/:id

    @Test
    public void testGetCategoriesIdJson() throws  Exception{
        System.out.println("Running testGetCategoriesIdJson...");

        HttpURLConnection connection = requestWithId(CATEGORIES_ENDPOINT, GET_METHOD, JSON_FORMAT, JSON_FORMAT, officeCategory.getId(), null);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String contentType = connection.getContentType();
        String responseBody = readResponse(connection);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonCategory category = objectMapper.readValue(responseBody, JsonCategory.class);

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertTrue(contentType.contains(JSON_FORMAT));
        assertTrue(category.isIn(officeCategory));

        connection.disconnect();

        System.out.println("testGetCategoriesIdJson passed.");
    }

    @Test
    public void testGetCategoriesIdXml() throws  Exception {
        System.out.println("Running testGetCategoriesIdXml...");

        HttpURLConnection connection = requestWithId(CATEGORIES_ENDPOINT, GET_METHOD, XML_FORMAT, XML_FORMAT, officeCategory.getId(), null);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String contentType = connection.getContentType();
        String responseBody = readResponse(connection);

        XmlMapper xmlMapper = new XmlMapper();
        XmlCategory category = xmlMapper.readValue(responseBody, XmlCategory.class);

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertTrue(contentType.contains(XML_FORMAT));
        assertTrue(category.isIn(officeCategory));

        connection.disconnect();

        System.out.println("testGetCategoriesIdXml passed.");
    }

    @Test
    public void testGetCategoriesIdInexistentJson() throws  Exception {
        System.out.println("Running testGetCategoriesIdInexistentJson...");

        String inexistentId = "999";
        HttpURLConnection connection = requestWithId(CATEGORIES_ENDPOINT, GET_METHOD, JSON_FORMAT, JSON_FORMAT, inexistentId, null);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);

        assertEquals(404, responseCode);
        assertEquals("Not Found", responseMessage);
        assertEquals(CATEGORIES_INEXISTENT_ID_JSON_PREFIX + inexistentId + CATEGORIES_INEXISTENT_ID_JSON_SUFFIX, responseBody);

        connection.disconnect();

        System.out.println("testGetCategoriesIdInexistentJson passed.");
    }

    @Test
    public void testGetCategoriesIdInexistentXml() throws  Exception {
        System.out.println("Running testGetCategoriesIdInexistentXml...");

        String inexistentId = "999";
        HttpURLConnection connection = requestWithId(CATEGORIES_ENDPOINT, GET_METHOD, XML_FORMAT, XML_FORMAT, inexistentId, null);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);

        assertEquals(404, responseCode);
        assertEquals("Not Found", responseMessage);
        assertEquals(CATEGORIES_INEXISTENT_ID_XML_PREFIX + inexistentId + CATEGORIES_INEXISTENT_ID_XML_SUFFIX, responseBody);

        connection.disconnect();

        System.out.println("testGetCategoriesIdInexistentXml passed.");
    }

    // Test HEAD /categories/:id

    @Test
    public void testHeadCategoriesIdJson() throws Exception {
        System.out.println("Running testHeadCategoriesIdJson...");

        String homeId = homeCategory.getId();
        HttpURLConnection connection = requestWithId(CATEGORIES_ENDPOINT, HEAD_METHOD, JSON_FORMAT, JSON_FORMAT, officeCategory.getId(), homeId);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String contentType = connection.getContentType();
        String responseBody = readResponse(connection);

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertTrue(contentType.contains(JSON_FORMAT));
        assertEquals("", responseBody);

        connection.disconnect();

        System.out.println("testHeadCategoriesIdJson passed.");
    }

    @Test
    public void testHeadCategoriesIdXml() throws Exception {
        System.out.println("Running testHeadCategoriesIdXml...");

        String homeCategoryId = homeCategory.getId();
        HttpURLConnection connection = requestWithId(CATEGORIES_ENDPOINT, HEAD_METHOD, XML_FORMAT, XML_FORMAT, officeCategory.getId(), homeCategoryId);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String contentType = connection.getContentType();
        String responseBody = readResponse(connection);

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertTrue(contentType.contains(XML_FORMAT));
        assertEquals("", responseBody);

        connection.disconnect();

        System.out.println("testHeadCategoriesIdXml passed.");
    }

    // Test POST /categories/:id

    @Test
    public void testPostCategoriesIdJson() throws Exception {
        System.out.println("Running testPostCategoriesIdJson...");

        String officeId = officeCategory.getId();
        CategoryBody newOfficeBody = new CategoryBody("New Office", "New Description");
        ObjectMapper objectMapper = new ObjectMapper();
        String newOfficeBodyJson = objectMapper.writeValueAsString(newOfficeBody);

        Category newOffice = new Category(officeId, newOfficeBody.getTitle(), newOfficeBody.getDescription());

        HttpURLConnection connection = requestWithId(CATEGORIES_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT, officeId, newOfficeBodyJson);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String contentType = connection.getContentType();
        String responseBody = readResponse(connection);

        Category returnedOffice = objectMapper.readValue(responseBody, Category.class);

        HttpURLConnection connectionAll = request(CATEGORIES_ENDPOINT, GET_METHOD, JSON_FORMAT, JSON_FORMAT, null);
        String responseBodyAll = readResponse(connectionAll);

        JsonCategory allCategories = objectMapper.readValue(responseBodyAll, JsonCategory.class);

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertTrue(contentType.contains(JSON_FORMAT));
        assertEquals(newOffice, returnedOffice);
        assertTrue(allCategories.areIn(new Category[]{newOffice, homeCategory}));

        connection.disconnect();
        connectionAll.disconnect();

        System.out.println("testPostCategoriesIdJson passed.");
    }

    @Test
    public void testPostCategoriesIdXml() throws Exception {
        System.out.println("Running testPostCategoriesIdXml...");

        String officeId = officeCategory.getId();
        CategoryBody newOfficeBody = new CategoryBody("New Office", "New Description");
        XmlMapper xmlMapper = new XmlMapper();
        String newOfficeBodyXml = xmlMapper.writeValueAsString(newOfficeBody);

        Category newOffice = new Category(officeId, newOfficeBody.getTitle(), newOfficeBody.getDescription());

        HttpURLConnection connection = requestWithId(CATEGORIES_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT, officeId, newOfficeBodyXml);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String contentType = connection.getContentType();
        String responseBody = readResponse(connection);

        Category returnedOffice = xmlMapper.readValue(responseBody, Category.class);

        HttpURLConnection connectionAll = request(CATEGORIES_ENDPOINT, GET_METHOD, XML_FORMAT, XML_FORMAT, null);
        String responseBodyAll = readResponse(connectionAll);

        XmlCategory allCategories = xmlMapper.readValue(responseBodyAll, XmlCategory.class);

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertTrue(contentType.contains(XML_FORMAT));
        assertEquals(newOffice, returnedOffice);
        assertTrue(allCategories.areIn(new Category[]{newOffice, homeCategory}));

        connection.disconnect();
        connectionAll.disconnect();

        System.out.println("testPostCategoriesIdXml passed.");
    }

    // Test PUT /categories/:id

    @Test
    public void testPutCategoriesIdJson() throws Exception {
        System.out.println("Running testPutCategoriesIdJson...");

        String officeId = officeCategory.getId();
        CategoryBody newOfficeBody = new CategoryBody("New Office", "New Description");
        ObjectMapper objectMapper = new ObjectMapper();
        String newOfficeBodyJson = objectMapper.writeValueAsString(newOfficeBody);

        Category newOffice = new Category(officeId, newOfficeBody.getTitle(), newOfficeBody.getDescription());

        HttpURLConnection connection = requestWithId(CATEGORIES_ENDPOINT, PUT_METHOD, JSON_FORMAT, JSON_FORMAT, officeId, newOfficeBodyJson);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String contentType = connection.getContentType();
        String responseBody = readResponse(connection);

        Category returnedOffice = objectMapper.readValue(responseBody, Category.class);

        HttpURLConnection connectionAll = request(CATEGORIES_ENDPOINT, GET_METHOD, JSON_FORMAT, JSON_FORMAT, null);
        String responseBodyAll = readResponse(connectionAll);

        JsonCategory allCategories = objectMapper.readValue(responseBodyAll, JsonCategory.class);

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertTrue(contentType.contains(JSON_FORMAT));
        assertEquals(newOffice, returnedOffice);
        assertTrue(allCategories.areIn(new Category[]{newOffice, homeCategory}));

        connection.disconnect();
        connectionAll.disconnect();

        System.out.println("testPutCategoriesIdJson passed.");
    }

    @Test
    public void testPutCategoriesIdXml() throws Exception {
        System.out.println("Running testPutCategoriesIdXml...");

        String officeId = officeCategory.getId();
        CategoryBody newOfficeBody = new CategoryBody("New Office", "New Description");
        XmlMapper xmlMapper = new XmlMapper();
        String newOfficeBodyXml = xmlMapper.writeValueAsString(newOfficeBody);

        Category newOffice = new Category(officeId, newOfficeBody.getTitle(), newOfficeBody.getDescription());

        HttpURLConnection connection = requestWithId(CATEGORIES_ENDPOINT, PUT_METHOD, XML_FORMAT, XML_FORMAT, officeId, newOfficeBodyXml);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String contentType = connection.getContentType();
        String responseBody = readResponse(connection);

        Category returnedOffice = xmlMapper.readValue(responseBody, Category.class);

        HttpURLConnection connectionAll = request(CATEGORIES_ENDPOINT, GET_METHOD, XML_FORMAT, XML_FORMAT, null);
        String responseBodyAll = readResponse(connectionAll);

        XmlCategory allCategories = xmlMapper.readValue(responseBodyAll, XmlCategory.class);

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertTrue(contentType.contains(XML_FORMAT));
        assertEquals(newOffice, returnedOffice);
        assertTrue(allCategories.areIn(new Category[]{newOffice, homeCategory}));

        connection.disconnect();
        connectionAll.disconnect();

        System.out.println("testPutCategoriesIdXml passed.");
    }

    // Test DELETE /categories/:id

    @Test
    public void testDeleteCategoriesIdJson() throws Exception {
        System.out.println("Running testDeleteCategoriesIdJson...");

        String officeId = officeCategory.getId();
        HttpURLConnection connection = requestWithId(CATEGORIES_ENDPOINT, DELETE_METHOD, JSON_FORMAT, JSON_FORMAT, officeId, null);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String contentType = connection.getContentType();
        String responseBody = readResponse(connection);

        HttpURLConnection connectionAll = request(CATEGORIES_ENDPOINT, GET_METHOD, JSON_FORMAT, JSON_FORMAT, null);
        String responseBodyAll = readResponse(connectionAll);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonCategory allCategories = objectMapper.readValue(responseBodyAll, JsonCategory.class);

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertTrue(contentType.contains(JSON_FORMAT));
        assertEquals("", responseBody);
        assertTrue(allCategories.areIn(new Category[]{homeCategory}));

        connection.disconnect();
        connectionAll.disconnect();

        System.out.println("testDeleteCategoriesIdJson passed.");
    }

    @Test
    public void testDeleteCategoriesIdXml() throws Exception {
        System.out.println("Running testDeleteCategoriesIdXml...");

        String officeId = officeCategory.getId();
        HttpURLConnection connection = requestWithId(CATEGORIES_ENDPOINT, DELETE_METHOD, XML_FORMAT, XML_FORMAT, officeId, null);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String contentType = connection.getContentType();
        String responseBody = readResponse(connection);

        HttpURLConnection connectionAll = request(CATEGORIES_ENDPOINT, GET_METHOD, XML_FORMAT, XML_FORMAT, null);
        String responseBodyAll = readResponse(connectionAll);

        XmlMapper xmlMapper = new XmlMapper();
        XmlCategory allCategories = xmlMapper.readValue(responseBodyAll, XmlCategory.class);

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertTrue(contentType.contains(XML_FORMAT));
        assertEquals("", responseBody);
        assertTrue(allCategories.areIn(new Category[]{homeCategory}));

        connection.disconnect();
        connectionAll.disconnect();

        System.out.println("testDeleteCategoriesIdXml passed.");
    }

    @Test
    public void testDeleteCategoriesSameIdTwiceJson() throws Exception {
        System.out.println("Running testDeleteCategoriesSameIdTwiceJson...");

        String officeId = officeCategory.getId();

        HttpURLConnection connection1 = requestWithId(CATEGORIES_ENDPOINT, DELETE_METHOD, JSON_FORMAT, JSON_FORMAT, officeId, null);
        int responseCode1 = connection1.getResponseCode();
        String responseMessage1 = connection1.getResponseMessage();
        String contentType1 = connection1.getContentType();
        String responseBody1 = readResponse(connection1);

        HttpURLConnection connectionAll = request(CATEGORIES_ENDPOINT, GET_METHOD, JSON_FORMAT, JSON_FORMAT, null);
        String responseBodyAll = readResponse(connectionAll);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonCategory allCategories = objectMapper.readValue(responseBodyAll, JsonCategory.class);

        assertEquals(200, responseCode1);
        assertEquals("OK", responseMessage1);
        assertTrue(contentType1.contains(JSON_FORMAT));
        assertEquals("", responseBody1);
        assertTrue(allCategories.areIn(new Category[]{homeCategory}));

        // Try to delete the same category again
        HttpURLConnection connection2 = requestWithId(CATEGORIES_ENDPOINT, DELETE_METHOD, JSON_FORMAT, JSON_FORMAT, officeId, null);
        int responseCode2 = connection2.getResponseCode();
        String responseMessage2 = connection2.getResponseMessage();
        String responseBody2 = readResponse(connection2);

        assertEquals(404, responseCode2);
        assertEquals("Not Found", responseMessage2);
        assertEquals(CATEGORIES_DELETE_INEXISTENT_ID_JSON + officeId + CATEGORIES_INEXISTENT_ID_JSON_SUFFIX, responseBody2);

        connection1.disconnect();
        connectionAll.disconnect();
        connection2.disconnect();

        System.out.println("testDeleteCategoriesSameIdTwiceJson passed.");
    }

    @Test
    public void testDeleteCategoriesSameIdTwiceXml() throws Exception {
        System.out.println("Running testDeleteCategoriesSameIdTwiceXml...");

        String officeId = officeCategory.getId();

        HttpURLConnection connection1 = requestWithId(CATEGORIES_ENDPOINT, DELETE_METHOD, XML_FORMAT, XML_FORMAT, officeId, null);
        int responseCode1 = connection1.getResponseCode();
        String responseMessage1 = connection1.getResponseMessage();
        String contentType1 = connection1.getContentType();
        String responseBody1 = readResponse(connection1);

        HttpURLConnection connectionAll = request(CATEGORIES_ENDPOINT, GET_METHOD, XML_FORMAT, XML_FORMAT, null);
        String responseBodyAll = readResponse(connectionAll);

        XmlMapper xmlMapper = new XmlMapper();
        XmlCategory allCategories = xmlMapper.readValue(responseBodyAll, XmlCategory.class);

        assertEquals(200, responseCode1);
        assertEquals("OK", responseMessage1);
        assertTrue(contentType1.contains(XML_FORMAT));
        assertEquals("", responseBody1);
        assertTrue(allCategories.areIn(new Category[]{homeCategory}));

        // Try to delete the same category again
        HttpURLConnection connection2 = requestWithId(CATEGORIES_ENDPOINT, DELETE_METHOD, XML_FORMAT, XML_FORMAT, officeId, null);
        int responseCode2 = connection2.getResponseCode();
        String responseMessage2 = connection2.getResponseMessage();
        String responseBody2 = readResponse(connection2);

        assertEquals(404, responseCode2);
        assertEquals("Not Found", responseMessage2);
        assertEquals(CATEGORIES_DELETE_INEXISTENT_ID_XML + officeId + CATEGORIES_INEXISTENT_ID_XML_SUFFIX, responseBody2);

        connection1.disconnect();
        connectionAll.disconnect();
        connection2.disconnect();

        System.out.println("testDeleteCategoriesSameIdTwiceXml passed.");
    }
}
