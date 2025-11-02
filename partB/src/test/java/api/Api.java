package api;

import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.util.Map;

public class Api {
    private static final String BASE_URL = "http://localhost:4567";
    private static final Gson GSON = new Gson();

    public Api() {
        RestAssured.baseURI = BASE_URL;
    }

    // --- Utility Methods ---

    /**
     * Converts a Map (payload) into a JSON string.
     */
    public String toJson(Map<String, Object> payloadMap) {
        return GSON.toJson(payloadMap);
    }

    private void fail(String message) {
        throw new RuntimeException(message);
    }

    // --- Service Health Check ---

    /**
     * Checks if the service is running by hitting the /todos endpoint.
     */
    public void checkServiceStatus() {
        try {
            Response response = RestAssured.get(BASE_URL + "/todos");
            if (response.getStatusCode() < 200 || response.getStatusCode() >= 300) {
                fail("Service is reachable but returned non-2xx status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            fail("Service is not running at " + BASE_URL
                    + ". Ensure java -jar runTodoManagerRestAPI-1.5.5.jar is executed.");
        }
    }

    // --- Generic Request Methods ---

    /**
     * Sends a POST request to an endpoint with a JSON body.
     */
    public Response postRequest(String endpoint, String body) {
        return RestAssured.given()
                .baseUri(BASE_URL)
                .header("Content-Type", "application/json")
                .body(body)
                .post(endpoint);
    }
    
    /**
     * Sends a POST request to an endpoint with a specified Content-Type.
     */
    public Response postRequest(String endpoint, String body, String contentType) {
        return RestAssured.given()
                .baseUri(BASE_URL)
                .header("Content-Type", contentType)
                .body(body)
                .post(endpoint);
    }

    /**
     * Sends a PUT request to an endpoint with a specified Content-Type.
     */
    public Response putRequest(String endpoint, String body, String contentType) {
        return RestAssured.given()
                .baseUri(BASE_URL)
                .header("Content-Type", contentType)
                .body(body)
                .put(endpoint);
    }

    /**
     * Sends a GET request to an endpoint, defaulting Accept to application/json.
     */
    public Response getRequest(String endpoint) {
        return RestAssured.given()
                .header("Accept", "application/json")
                .get(endpoint)
                .then().extract().response();
    }
    
    /**
     * Sends a GET request to an endpoint with a specific Accept header.
     */
    public Response getRequest(String endpoint, String acceptHeader) {
        return RestAssured.given()
                .header("Accept", acceptHeader)
                .get(endpoint)
                .then().extract().response();
    }

    /**
     * Sends a DELETE request to an endpoint.
     */
    public Response deleteRequest(String endpoint) {
        return RestAssured.given()
                .delete(endpoint)
                .then().extract().response();
    }
    
    /**
     * Sends a DELETE request to an endpoint with a specific Accept header.
     */
    public Response deleteRequest(String endpoint, String acceptHeader) {
        return RestAssured.given()
            .header("Accept", acceptHeader)
            .delete(endpoint)
            .then().extract().response();
    }

    // --- Cleanup Methods ---
    
    // Note: To use the cleanup methods below, you'll need the DTO classes 
    // for Todo, Projects, and Category to be available (e.g., in a 'models' package).
    // I am including a simplified Category DTO for completeness, but you'll need 
    // to ensure 'Todo' and 'Projects' DTOs are also available if you use this code.

    // A simple DTO class for Category, as it was in the original CategoryApi
    private static class CategoryDTO {
        String id;
        String title;
        String description;
    }
    
    // A simplified DTO class for Project (assuming it has an getId method)
    // NOTE: This relies on a 'Projects' class with a getId() method.
    private static class Projects {
        public String getId() { return id; }
        String id;
        // other fields...
    }
    
    // A simplified DTO class for Todo (assuming it has an getId method)
    // NOTE: This relies on a 'Todo' class with a getId() method.
    private static class Todo {
        public String getId() { return id; }
        String id;
        // other fields...
    }


    /**
     * Deletes all Todo items. Crucial for test isolation.
     */
    public void deleteAllTodos() {
        Response response = getRequest("/todos", "application/json");
        Todo[] todos = response.jsonPath().getObject("todos", Todo[].class);

        if (todos != null) {
            for (Todo todo : todos) {
                if (todo.getId() != null) {
                     RestAssured.given().delete("/todos/" + todo.getId());
                }
            }
        }
    }

    /**
     * Deletes all Project items. Crucial for test isolation.
     */
    public void deleteAllProjects() {
        Response response = getRequest("/projects", "application/json");
        Projects[] projects = response.jsonPath().getObject("projects", Projects[].class);

        if (projects != null) {
            for (Projects project : projects) {
                if (project.getId() != null) {
                    RestAssured.given().delete("/projects/" + project.getId());
                }
            }
        }
    }
    
    /**
     * Deletes all Category items.
     */
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

    public void deleteAllData() {
        deleteAllTodos();
        deleteAllProjects();
        deleteAllCategories();
    }
}
