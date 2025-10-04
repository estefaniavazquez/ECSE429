package categories;

import java.net.HttpURLConnection;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.http.NameValuePair;
import org.junit.*;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.*;

import general.BaseApiTest;

import static general.Utils.*;
import static general.CommonConstants.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ActualBehaviourWorkingCategoriesApiTest extends BaseApiTest {
    /*   /categories/{id} endpoint tests   */

    // Test POST /categories/{id}?description={description}&title={title} (discovered bug)

    @Test
    public void testPostCategoriesIdWithQueryParamsActualXml() throws Exception {
        System.out.println("Running testPostCategoriesIdWithQueryParamsActualXml...");

        String homeId = homeCategory.getId();

        List<NameValuePair> nameValuePairs = createQueryParams(homeCategory.getTitle(), homeCategory.getDescription());

        HttpURLConnection connection = requestWithIdAndQueryParams(CATEGORIES_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT, homeId, null, nameValuePairs);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);

        XmlMapper xmlMapper = new XmlMapper();
        Category responseCategory = xmlMapper.readValue(responseBody, Category.class);

        HttpURLConnection connectionAll = request(CATEGORIES_ENDPOINT, GET_METHOD, XML_FORMAT, XML_FORMAT, null);
        String allResponseBody = readResponse(connectionAll);

        XmlCategory allCategories = xmlMapper.readValue(allResponseBody, XmlCategory.class);

        // Bug: should return 400 Bad Request with empty body, but returns 200 OK with category in body
        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertEquals(homeCategory, responseCategory);
        assertTrue(allCategories.areIn(defaultCategories));

        connection.disconnect();

        System.out.println("testPostCategoriesIdWithQueryParamsActualXml showed expected behaviour failing.");
    }
}
