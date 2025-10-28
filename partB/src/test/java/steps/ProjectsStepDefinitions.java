package steps;

/**
 * Implements the Gherkin steps for CRUD and verification related to the
 * /projects endpoint.
 */
public class ProjectsStepDefinitions {
    private final ScenarioContext context;
    private final ProjectsAPI api;

    public ProjectsStepDefinitions(ScenarioContext context, ProjectsAPI api) {
        this.context = context;
        this.api = api;
    }

    // ==============================================================================
    // STEP 1: WHEN - ACTION (T1)
    // ==============================================================================
    @When("I send a request to create a project with these details:")
    public void i_send_a_request_to_create_a_project_with_these_details(DataTable dataTable) {
        // 1. Extract the single row of input data from the Gherkin table
        Map<String, String> data = dataTable.asMaps().get(0);

        // 2. Create a Project object from the input data
        String name = data.get("name");
        String description = data.get("description");
        Project newProject = new Project(name, description);

        // 3. Convert the Project object to a Map<String, Object>
        Map<String, Object> payloadMap = newProject.toPayloadMap();
        String jsonBody = api.toJson(payloadMap);

        // 4. Send the request and store the response
        Response response = api.postRequest("/projects", jsonBody);
        context.setLastResponse(response);

        // 5. If successful (201), store the new ID
        if (response.getStatusCode() == 201) {
            String newId = response.jsonPath().getString("id");
            context.storeId("last_created_id", newId);
        }
    }

    // ==============================================================================
    // STEP 2: THEN - ASSERTION (Status Code)
    // ==============================================================================
    @Then("the response status code should be {int}")
    public void the_response_status_code_should_be(Integer expectedStatusCode) {
        Response response = context.getLastResponse();
        assertEquals(expectedStatusCode,
                response.statusCode(),
                "Expected status code did not match actual code. Response body: " + response.getBody().asString());
    }

    // ==============================================================================
    // STEP 3: AND - ASSERTION (Verify Response Data)
    // ==============================================================================
    @And("the project details should show field {string} with value {string}")
    public void the_project_details_should_show_field_with_value(String fieldName, String expectedValue) {
        Response response = context.getLastResponse();
        if (response.getStatusCode() == 201 || response.getStatusCode() == 200) {
            // Successful response, verify the field value

            if (fieldName.equals("title")) {
                String actualValue = response.jsonPath().getString("name");
                assertEquals(expectedValue, actualValue, "The 'name' field value does not match.");
            } else if (fieldName.equals("description")) {
                String actualValue = response.jsonPath().getString("description");
                assertEquals(expectedValue, actualValue, "The 'description' field value does not match.");
            } else if (fieldName.equals("id")) {
                String actualValue = response.jsonPath().getString("id");
                assertEquals(expectedValue, actualValue, "The 'id' field value does not match.");
            } else if (fieldName.equals("completed")) {
                String actualValue = response.jsonPath().getString("completed");
                Boolean expectedBool = Boolean.parseBoolean(expectedValue);
                assertEquals(expectedValue, actualValue, "The 'completed' field value does not match.");
            } else if (fieldName.equals("active")) {
                String actualValue = response.jsonPath().getString("active");
                Boolean expectedBool = Boolean.parseBoolean(expectedValue);
                assertEquals(expectedBool, actualValue, "The 'active' field value does not match.");
            } else {
                fail("Field name '" + fieldName + "' is not recognized for verification.");
            }
        } else {
            fail("Cannot verify field value as the response status code is not 201. Actual status code: "
                    + response.getStatusCode());
        }
    }

    // ==============================================================================
    // STEP 4: AND - ASSERTION (Verify Error Message)
    // ==============================================================================
    @And("the system should tell me if there was an error: {string}")
    public void the_system_should_tell_me_if_there_was_an_error(String expectedErrorMessage) {
        Response response = context.getLastResponse();

        // 1. Sucess Expected
        if (expectedErrorMessage.isEmpty()) {
            // Assert that we did not receive an error
            assertFalse(
                    response.getStatusCode() >= 400,
                    "Expected a successful response, but received error status code: "
                            + response.getStatusCode() + ". Response body: " + response.getBody().asString());
        }

        // 2. Error Expected
        // Assert that we did receive an error
        assertTrue(response.getStatusCode() >= 400,
                "Expected an error response, but received successful status code: "
                        + response.getStatusCode() + ". Response body: " + response.getBody().asString());

        // Verify the error message is present in the response body
        String responseBody = response.getBody().asString();

        assertTrue(
                responseBody.contains(expectedErrorMessage),
                "Expected error message '" + expectedErrorMessage + "' not found in response body: "
                        + responseBody);
    }
}
