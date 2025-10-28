package api;

public class ProjectsAPI {
    private static final String BASE_URL = "http://localhost:4567";
    private static final Gson GSON = new Gson();

    public ProjectsAPI() {
        RestAssured.baseURI = BASE_URL;
    }

    /**
     * Utility to convert a Project object (or map) into a JSON string payload.
     */
    public String toJson(Map<String, Object> payloadMap) {
        // Use Gson to convert the map containing only the necessary fields to a JSON
        // string
        return GSON.toJson(payloadMap);
    }

    /**
     * Sends a POST request to an endpoint with a JSON body.
     */
    public Response postRequest(String endpoint, String body) {
        // Implementation for POST request using RestAssured...
        return RestAssured.given()
                .baseUri(BASE_URL)
                .header("Content-Type", "application/json")
                .body(body)
                .post(endpoint);
    }

    /**
     * Sends a GET request to an endpoint.
     */
    public Response getRequest(String endpoint) {
        return RestAssured.given()
                .header("Accept", "application/json")
                .get(endpoint)
                .then().extract().response();
    }

    /**
     * Implements the logic for GIVEN the system is initialized with an empty todo
     * list.
     * This is crucial for test isolation.
     */
    public void deleteAllData() {
        // Step 1: Get all current todos
        Response response = getRequest("/project");

        // Use a list of Todo objects to ensure all items in the array are processed
        Todo[] todos = response.jsonPath().getObject("projects", Todo[].class);

        if (todos != null) {
            for (Todo todo : todos) {
                // Step 2: Delete each todo item
                if (todo.getId() != null) {
                    RestAssured.given().delete("/projects/" + todo.getId());
                }
            }
        }
    }

    /**
     * Checks if the service is running (Part of Background setup)
     */
    public void checkServiceStatus() {
        try {
            Response response = RestAssured.get(BASE_URL + "/todos");
            // If we get any successful response (even empty), the service is up
            if (response.getStatusCode() < 200 || response.getStatusCode() >= 300) {
                fail("Service is reachable but returned non-2xx status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            fail("Service is not running at " + BASE_URL
                    + ". Ensure java -jar runTodoManagerRestAPI-1.5.5.jar is executed.");
        }
    }

    private void fail(String message) {
        throw new RuntimeException(message);
    }
}
