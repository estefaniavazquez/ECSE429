package api;

import java.util.Map;

import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class CategoryApi {

    private static final String BASE_URL = "http://localhost:4567";
    private static final Gson GSON = new Gson();

    public CategoryApi() {
        RestAssured.baseURI = BASE_URL;
    }

    // make json from map
    public String toJson(Map<String, Object> payloadMap) {
        return GSON.toJson(payloadMap);
    }

    // send post request
    public Response postRequest(String endpoint, String body) {
        return RestAssured.given()
                .baseUri(BASE_URL)
                .header("Content-Type", "application/json")
                .body(body)
                .post(endpoint)
                .then().extract().response();
    }

    // send get request
    public Response getRequest(String endpoint) {
        return RestAssured.given()
                .baseUri(BASE_URL)
                .header("Accept", "application/json")
                .get(endpoint)
                .then().extract().response();
    }

    // send delete request
    public Response deleteRequest(String endpoint) {
        return RestAssured.given()
                .baseUri(BASE_URL)
                .delete(endpoint)
                .then().extract().response();
    }

    // delete all categories
    public void deleteAllCategories() {
        Response r = getRequest("/categories");
        CategoryDTO[] cats = r.jsonPath().getObject("categories", CategoryDTO[].class);
        if (cats != null) {
            for (CategoryDTO c : cats) {
                if (c != null && c.id != null) {
                    deleteRequest("/categories/" + c.id);
                }
            }
        }
    }

    // simple category object
    private static class CategoryDTO {
        String id;
        String title;
        String description;
    }

    // check if service is running
    public void checkServiceUp() {
        try {
            Response response = RestAssured.get(BASE_URL + "/categories");
            if (response.getStatusCode() < 200 || response.getStatusCode() >= 300) {
                fail("Service reachable but non-2xx for /categories: " + response.getStatusCode());
            }
        } catch (Exception e) {
            fail("Service not running at " + BASE_URL + " (start the jar).");
        }
    }

    private void fail(String msg) { throw new RuntimeException(msg); }
}
