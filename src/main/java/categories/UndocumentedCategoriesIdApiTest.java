package categories;

import org.junit.*;
import org.junit.runners.MethodSorters;

import general.BaseApiTest;

import java.net.HttpURLConnection;

import static org.junit.Assert.*;

import static general.Utils.*;
import static general.CommonConstants.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UndocumentedCategoriesIdApiTest extends BaseApiTest {
    /*   /categories/{id} endpoint tests   */

    // Test PATCH /categories/{id} (not documented)

    @Test
    public void testPatchCategoriesIdJson() throws Exception {
        System.out.println("Running testPatchCategoriesIdJson...");

        String homeId = homeCategory.getId();

        HttpURLConnection connection = requestWithIdPATCH(CATEGORIES_ENDPOINT, JSON_FORMAT, JSON_FORMAT, homeId);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);

        assertEquals(405, responseCode);
        assertEquals("Method Not Allowed", responseMessage);
        assertEquals("", responseBody);

        connection.disconnect();

        System.out.println("testPatchCategoriesIdJson passed.");
    }

    @Test
    public void testPatchCategoriesIdXml() throws Exception {
        System.out.println("Running testPatchCategoriesIdXml...");

        String homeId = homeCategory.getId();

        HttpURLConnection connection = requestWithIdPATCH(CATEGORIES_ENDPOINT, XML_FORMAT, XML_FORMAT, homeId);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);

        assertEquals(405, responseCode);
        assertEquals("Method Not Allowed", responseMessage);
        assertEquals("", responseBody);

        connection.disconnect();

        System.out.println("testPatchCategoriesIdXml passed.");
    }

    // Test OPTIONS /categories/{id} (not documented)

    @Test
    public void testOptionsCategoriesIdJson() throws Exception {
        System.out.println("Running testOptionsCategoriesId...");

        String homeId = homeCategory.getId();

        HttpURLConnection connection = requestWithId(CATEGORIES_ENDPOINT, OPTIONS_METHOD, JSON_FORMAT, JSON_FORMAT, homeId, null);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String allowHeader = connection.getHeaderField("Allow");
        String responseBody = readResponse(connection);

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertEquals(CATEGORY_ID_OPTIONS, allowHeader);
        assertEquals("", responseBody);

        connection.disconnect();

        System.out.println("testOptionsCategoriesIdJson passed.");
    }

    @Test
    public void testOptionsCategoriesIdXml() throws Exception {
        System.out.println("Running testOptionsCategoriesIdXml...");

        String homeId = homeCategory.getId();

        HttpURLConnection connection = requestWithId(CATEGORIES_ENDPOINT, OPTIONS_METHOD, XML_FORMAT, XML_FORMAT, homeId, null);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String allowHeader = connection.getHeaderField("Allow");
        String responseBody = readResponse(connection);

        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertEquals(CATEGORY_ID_OPTIONS, allowHeader);
        assertEquals("", responseBody);

        connection.disconnect();

        System.out.println("testOptionsCategoriesIdXml passed.");
    }
}
