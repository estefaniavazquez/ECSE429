package categories;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.*;
import org.junit.runners.MethodSorters;

import general.BaseApiTest;

import java.net.HttpURLConnection;

import static org.junit.Assert.*;

import static general.Utils.*;
import static general.CommonConstants.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UndocumentedCategoriesApiTest extends BaseApiTest {
    /*   /categories endpoint tests   */

    // Test PUT /categories (not documented)

    @Test
    public void testPutCategoriesJson() throws Exception {
        System.out.println("Running testPutCategoriesJson...");

        Category existingHomeCategory = new Category(homeCategory.getId(), "new title", "new description");
        ObjectMapper objectMapper = new ObjectMapper();
        String categoryJson = objectMapper.writeValueAsString(existingHomeCategory);

        HttpURLConnection connection = request(CATEGORIES_ENDPOINT, PUT_METHOD, JSON_FORMAT, JSON_FORMAT, categoryJson);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();

        assertEquals(405, responseCode);
        assertEquals("Method Not Allowed", responseMessage);

        connection.disconnect();

        System.out.println("testPutCategoriesJson passed.");
    }

    @Test
    public void testPutCategoriesXml() throws Exception {
        System.out.println("Running testPutCategoriesXml...");

        Category existingHomeCategory = new Category(homeCategory.getId(), "new title", "new description");
        String categoryXml = existingHomeCategory.toStringXml();

        HttpURLConnection connection = request(CATEGORIES_ENDPOINT, PUT_METHOD, XML_FORMAT, XML_FORMAT, categoryXml);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();

        assertEquals(405, responseCode);
        assertEquals("Method Not Allowed", responseMessage);

        connection.disconnect();

        System.out.println("testPutCategoriesXml passed.");
    }

    // Test DELETE /categories (not documented)

    @Test
    public void testDeleteCategoriesJson() throws Exception {
        System.out.println("Running testDeleteCategoriesJson...");

        String idBody = "{\"id\":\"" + homeCategory.getId() + "\"}";

        HttpURLConnection connection = request(CATEGORIES_ENDPOINT, DELETE_METHOD, JSON_FORMAT, JSON_FORMAT, idBody);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();

        assertEquals(405, responseCode);
        assertEquals("Method Not Allowed", responseMessage);

        connection.disconnect();

        System.out.println("testDeleteCategoriesJson passed.");
    }

    @Test
    public void testDeleteCategoriesXml() throws Exception {
        System.out.println("Running testDeleteCategoriesXml...");

        String idBody = "<category><id>" + homeCategory.getId() + "</id></category>";

        HttpURLConnection connection = request(CATEGORIES_ENDPOINT, DELETE_METHOD, XML_FORMAT, XML_FORMAT, idBody);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();

        assertEquals(405, responseCode);
        assertEquals("Method Not Allowed", responseMessage);

        connection.disconnect();

        System.out.println("testDeleteCategoriesXml passed.");
    }

    @Test
    public void testPatchCategoriesJson() throws Exception {
        System.out.println("Running testPatchCategoriesJson...");

        HttpURLConnection connection = request(CATEGORIES_ENDPOINT, PATCH_METHOD, JSON_FORMAT, JSON_FORMAT, null);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();

        assertEquals(405, responseCode);
        assertEquals("Method Not Allowed", responseMessage);

        connection.disconnect();

        System.out.println("testPatchCategoriesJson passed.");
    }

    @Test
    public void testPatchCategoriesXml() throws Exception {
        System.out.println("Running testPatchCategoriesXml...");

        HttpURLConnection connection = request(CATEGORIES_ENDPOINT, PATCH_METHOD, XML_FORMAT, XML_FORMAT, null);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();

        assertEquals(405, responseCode);
        assertEquals("Method Not Allowed", responseMessage);

        connection.disconnect();

        System.out.println("testPatchCategoriesXml passed.");
    }

    @Test
    public void testOptionsCategoriesJson() throws Exception {
        System.out.println("Running testOptionsCategoriesJson...");

        HttpURLConnection connection = request(CATEGORIES_ENDPOINT, OPTIONS_METHOD, JSON_FORMAT, JSON_FORMAT, null);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String allowHeader = connection.getHeaderField("Allow");

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertNotNull(allowHeader);

        connection.disconnect();

        System.out.println("testOptionsCategoriesJson passed.");
    }

    @Test
    public void testOptionsCategoriesXml() throws Exception {
        System.out.println("Running testOptionsCategoriesXml...");

        HttpURLConnection connection = request(CATEGORIES_ENDPOINT, OPTIONS_METHOD, XML_FORMAT, XML_FORMAT, null);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String allowHeader = connection.getHeaderField("Allow");

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertNotNull(allowHeader);

        connection.disconnect();

        System.out.println("testOptionsCategoriesXml passed.");
    }
}
