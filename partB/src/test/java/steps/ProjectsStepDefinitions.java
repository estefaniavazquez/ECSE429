package steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.Map;

import api.Api;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import models.Projects;
import setup.ScenarioContext;

/**
 * Implements the Gherkin steps for CRUD and verification related to the
 * /projects endpoint.
 */
public class ProjectsStepDefinitions {
    private final ScenarioContext context;
    private final Api api;

    public ProjectsStepDefinitions(ScenarioContext context, Api api) {
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
        String title = data.get("title");
        String description = data.get("description");
        String active = data.get("active");
        Projects newProject = new Projects(null, title, null, active, description);

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
    // EXCEPTION: Verify Error Message
    // ==============================================================================
    // REFACTORED TO CommonStepDefinitions - using consolidated error message
    // verification
    // Now handled by: the_system_should_tell_me_if_there_was_an_error() in
    // CommonStepDefinitions
    // which supports both "the system should tell me/notify me if there was an
    // error"
    // and "an error message is returned" step phrases
    // @And("the system should notify me if there was an error: {string}")
    // public void the_system_should_tell_me_if_there_was_an_error(String
    // expectedErrorMessage) {
    // Response response = context.getLastResponse();
    // // 1. Success Expected
    // if (expectedErrorMessage.isEmpty()) {
    // assertFalse(response.getStatusCode() >= 400,
    // "Expected a successful response, but received error status code: "
    // + response.getStatusCode() + ". Response body: " +
    // response.getBody().asString());
    // return;
    // }
    // // 2. Error Expected
    // assertTrue(response.getStatusCode() >= 400,
    // "Expected an error response, but received successful status code: "
    // + response.getStatusCode() + ". Response body: " +
    // response.getBody().asString());
    // String responseBody = response.getBody().asString();
    // assertTrue(responseBody.contains(expectedErrorMessage),
    // "Expected error message '" + expectedErrorMessage + "' not found in response
    // body: "
    // + responseBody);
    // }

    // ==============================================================================
    // STEP 2: THEN - ASSERTION (Status Code)
    // ==============================================================================
    // REFACTORED TO CommonStepDefinitions
    // @Then("the response status code should be {int}")
    // public void the_response_status_code_should_be(Integer expectedStatusCode) {
    // Response response = context.getLastResponse();
    // assertEquals(expectedStatusCode,
    // response.statusCode(),
    // "Expected status code did not match actual code. Response body: " +
    // response.getBody().asString());
    // }

    // ==============================================================================
    // STEP 3: AND - ASSERTION (Verify Response Data)
    // ==============================================================================
    @And("the project details should show field {string} with value {string}")
    public void the_project_details_should_show_field_with_value(String fieldName, String expectedValue) {
        Response response = context.getLastResponse();
        if (response.getStatusCode() == 201 || response.getStatusCode() == 200) {
            // Successful response, verify the field value

            if (fieldName.equals("title")) {
                String actualValue = response.jsonPath().getString("title");
                assertEquals(expectedValue, actualValue, "The 'title' field value does not match.");
            } else if (fieldName.equals("description")) {
                String actualValue = response.jsonPath().getString("description");
                assertEquals(expectedValue, actualValue, "The 'description' field value does not match.");
            } else if (fieldName.equals("id")) {
                String actualValue = response.jsonPath().getString("id");
                assertEquals(expectedValue, actualValue, "The 'id' field value does not match.");
            } else if (fieldName.equals("completed")) {
                String actualValue = response.jsonPath().getString("completed");
                String expectedBool = String.valueOf(Boolean.parseBoolean(expectedValue));
                assertEquals(expectedBool, actualValue, "The 'completed' field value does not match.");
            } else if (fieldName.equals("active")) {
                String actualValue = response.jsonPath().getString("active");
                String expectedBool = String.valueOf(Boolean.parseBoolean(expectedValue));
                assertEquals(expectedBool, actualValue, "The 'active' field value does not match.");
            } else {
                fail("Field name '" + fieldName + "' is not recognized for verification.");
            }
        }
    }

    // ==============================================================================
    // STEP 5: GIVEN - Create a project for testing
    // ==============================================================================
    @Given("a project exists with title {string}, description {string}, and active {string}")
    public void a_project_exists_with_title_description_and_active(String title, String description, String active) {
        // Create a project with the specified details
        Projects newProject = new Projects(null, title, null, active, description);
        Map<String, Object> payloadMap = newProject.toPayloadMap();
        String jsonBody = api.toJson(payloadMap);

        Response response = api.postRequest("/projects", jsonBody);
        context.setLastResponse(response);

        // Store the ID for later use
        if (response.getStatusCode() == 201) {
            String newId = response.jsonPath().getString("id");
            context.storeId("last_created_id", newId);
        } else {
            fail("Failed to create project for test setup. Status: " + response.getStatusCode());
        }
    }

    @Given("a project exists with title {string}")
    public void a_project_exists_with_title(String title) {
        // Create a project with just a title
        a_project_exists_with_title_description_and_active(title, "", "false");
    }

    // // STEP 6: AND - Store ID
    // //
    // ==============================================================================
    // @And("its ID is stored as {string}")
    // public void its_id_is_stored_as(String key) {
    // // Retrieve the last created ID and store it with the specified key
    // String lastId = context.retrieveId("last_created_id");
    // context.storeId(key, lastId);
    // }

    // ==============================================================================
    // STEP 6B: AND - Mark project as completed
    // ==============================================================================
    @And("the project is marked as completed")
    public void the_project_is_marked_as_completed() {
        // Get the last created project's ID
        String projectId = context.retrieveId("last_created_id");

        // Create a payload to update the 'completed' field to true
        Projects updateProject = new Projects(null, null, "true", null, null);
        Map<String, Object> payloadMap = updateProject.toPayloadMap();
        String jsonBody = api.toJson(payloadMap);

        // Send POST request to update the project's completed status
        Response response = api.postRequest("/projects/" + projectId, jsonBody);

        // Verify the update was successful
        if (response.getStatusCode() != 200) {
            fail("Failed to mark project as completed. Status: " + response.getStatusCode()
                    + ". Response: " + response.getBody().asString());
        }

        // Optionally verify the completed field is now true
        String completedValue = response.jsonPath().getString("completed");
        if (!"true".equals(completedValue)) {
            fail("Project completed status was not set to true. Current value: " + completedValue);
        }
    }

    // ==============================================================================
    // STEP 6C: AND - Update a specific project field
    // ==============================================================================
    @And("the project field {string} is updated to {string}")
    public void the_project_field_is_updated_to(String fieldName, String fieldValue) {
        // Get the last created project's ID
        String projectId = context.retrieveId("last_created_id");

        // Create a payload to update the specified field
        Projects updateProject;
        if (fieldName.equals("active")) {
            updateProject = new Projects(null, null, null, fieldValue, null);
        } else if (fieldName.equals("completed")) {
            updateProject = new Projects(null, null, fieldValue, null, null);
        } else if (fieldName.equals("title")) {
            updateProject = new Projects(null, fieldValue, null, null, null);
        } else if (fieldName.equals("description")) {
            updateProject = new Projects(null, null, null, null, fieldValue);
        } else {
            fail("Unsupported field name for update: " + fieldName);
            return;
        }

        Map<String, Object> payloadMap = updateProject.toPayloadMap();
        String jsonBody = api.toJson(payloadMap);

        // Send POST request to update the project field
        Response response = api.postRequest("/projects/" + projectId, jsonBody);

        // Verify the update was successful
        if (response.getStatusCode() != 200) {
            fail("Failed to update project field '" + fieldName + "'. Status: " + response.getStatusCode()
                    + ". Response: " + response.getBody().asString());
        }

        // Verify the field was updated correctly
        String actualValue = response.jsonPath().getString(fieldName);
        String expectedValue = fieldValue;

        // For boolean fields, normalize the comparison
        if (fieldName.equals("active") || fieldName.equals("completed")) {
            expectedValue = String.valueOf(Boolean.parseBoolean(fieldValue));
        }

        if (!expectedValue.equals(actualValue)) {
            fail("Field '" + fieldName + "' was not updated correctly. Expected: " + expectedValue
                    + ", Actual: " + actualValue);
        }
    }

    // ==============================================================================
    // STEP 7: WHEN - Update project (POST)
    // ==============================================================================
    // //
    // ==============================================================================
    // STEP 7: WHEN - Update project (POST)
    // ==============================================================================
    @When("I send a request to update project {string} with body:")
    public void i_send_a_request_to_update_project_with_body(String targetId, DataTable dataTable) {
        // Extract data from the table
        Map<String, String> data = dataTable.asMaps().get(0);

        // If targetId is "1", use the stored project_id
        String actualId = targetId.equals("1") ? context.retrieveId("project_id") : targetId;

        // Build the request body
        String title = data.get("title");
        String description = data.get("description");
        String active = data.get("active");

        Projects updateProject = new Projects(null, title, null, active, description);
        Map<String, Object> payloadMap = updateProject.toPayloadMap();
        String jsonBody = api.toJson(payloadMap);

        // Send POST request to update
        Response response = api.postRequest("/projects/" + actualId, jsonBody);
        context.setLastResponse(response);
    }

    // ==============================================================================
    // STEP 8: THEN - Update status
    // ==============================================================================
    // REFACTORED TO CommonStepDefinitions - using "the response status code is
    // {string}"
    // @Then("the update status should be {string}")
    // public void the_update_status_should_be(String expectedStatusCode) {
    // Response response = context.getLastResponse();
    // assertEquals(Integer.parseInt(expectedStatusCode), response.statusCode(),
    // "Expected update status code did not match. Response: " +
    // response.getBody().asString());
    // }

    // ==============================================================================
    // STEP 9: AND - Verify updated project fields
    // ==============================================================================
    @And("the updated project should show field {string} with value {string}")
    public void the_updated_project_should_show_field_with_value(String fieldName, String expectedValue) {
        // Reuse the existing verification method
        the_project_details_should_show_field_with_value(fieldName, expectedValue);
    }

    // ==============================================================================
    // STEP 10: WHEN - View projects with filter
    // ==============================================================================
    @When("I send a request to view projects filtered by the query {string}")
    public void i_send_a_request_to_view_projects_filtered_by_query(String queryParams) {
        String endpoint = "/projects" + queryParams;
        Response response = api.getRequest(endpoint);
        context.setLastResponse(response);
    }

    // ==============================================================================
    // STEP 11: THEN - Status code (generic)
    // ==============================================================================
    // REFACTORED TO CommonStepDefinitions
    // @Then("the status code should be {string}")
    // public void the_status_code_should_be(String expectedStatusCode) {
    // Response response = context.getLastResponse();
    // assertEquals(Integer.parseInt(expectedStatusCode), response.statusCode(),
    // "Expected status code did not match. Response: " +
    // response.getBody().asString());
    // }

    // ==============================================================================
    // STEP 12: AND - Verify list count
    // ==============================================================================
    @And("the list should contain {int} projects")
    public void the_list_should_contain_projects(Integer expectedCount) {
        Response response = context.getLastResponse();
        if (response.getStatusCode() == 200) {
            List<Map<String, Object>> projects = response.jsonPath().getList("projects");
            int actualCount = (projects != null) ? projects.size() : 0;
            assertEquals(expectedCount, actualCount,
                    "Expected number of projects does not match actual count.");
        } else {
            // For error cases, we might expect 0 projects
            assertEquals(expectedCount, 0, "Expected 0 projects for error response.");
        }
    }

    // ==============================================================================
    // STEP 13: WHEN - Delete project
    // ==============================================================================
    @When("I send a request to delete project {string}")
    public void i_send_a_request_to_delete_project(String targetId) {
        String actualId;

        // Map target IDs to stored IDs
        if (targetId.equals("1")) {
            actualId = context.retrieveId("delete_project_id");
        } else if (targetId.equals("2")) {
            actualId = context.retrieveId("completed_project_id");
        } else {
            actualId = targetId;
        }

        Response response = api.deleteRequest("/projects/" + actualId);
        context.setLastResponse(response);
    }

    // ==============================================================================
    // STEP 14: THEN - Deletion status
    // ==============================================================================
    // REFACTORED TO CommonStepDefinitions - using "the response status code is
    // {string}"
    // @Then("the deletion status should be {string}")
    // public void the_deletion_status_should_be(String expectedStatusCode) {
    // Response response = context.getLastResponse();
    // assertEquals(Integer.parseInt(expectedStatusCode), response.statusCode(),
    // "Expected deletion status code did not match. Response: " +
    // response.getBody().asString());
    // }

    // ==============================================================================
    // STEP 15: AND - Verify deletion via GET
    // ==============================================================================
    @And("the project with ID {string} should yield a {string} on a quick check")
    public void the_project_with_id_should_yield_on_quick_check(String targetId, String expectedStatusCode) {
        String actualId;

        // Map target IDs to stored IDs
        if (targetId.equals("1")) {
            actualId = context.retrieveId("delete_project_id");
        } else if (targetId.equals("2")) {
            actualId = context.retrieveId("completed_project_id");
        } else {
            actualId = targetId;
        }

        Response response = api.getRequest("/projects/" + actualId);
        assertEquals(Integer.parseInt(expectedStatusCode), response.statusCode(),
                "Quick check status code did not match expected value.");
    }

    // ==============================================================================
    // STEP 16: WHEN - View single project
    // ==============================================================================
    @When("I send a request to view project {string}")
    public void i_send_a_request_to_view_project(String targetId) {
        String actualId;

        // Map target IDs to stored IDs
        if (targetId.equals("1")) {
            actualId = context.retrieveId("get_project_id");
        } else if (targetId.equals("2")) {
            actualId = context.retrieveId("updated_project_id");
        } else {
            actualId = targetId;
        }

        Response response = api.getRequest("/projects/" + actualId);
        context.setLastResponse(response);
    }

    // ==============================================================================
    // STEP 17: AND - Verify project field (alternative naming)
    // ==============================================================================
    @And("the project should show field {string} with value {string}")
    public void the_project_should_show_field_with_value(String fieldName, String expectedValue) {
        Response response = context.getLastResponse();

        // For GET /projects/{id}, the response wraps the project in a "projects" array
        // Check if this is a single project retrieval (status 200) with projects array
        if (response.getStatusCode() == 200 && response.jsonPath().get("projects") != null) {
            List<Map<String, Object>> projects = response.jsonPath().getList("projects");

            if (projects != null && !projects.isEmpty()) {
                // Get the first (and only) project from the array
                Map<String, Object> project = projects.get(0);
                String actualValue = project.get(fieldName) != null ? project.get(fieldName).toString() : null;

                // For boolean fields, normalize the comparison
                if (fieldName.equals("active") || fieldName.equals("completed")) {
                    String expectedBool = String.valueOf(Boolean.parseBoolean(expectedValue));
                    assertEquals(expectedBool, actualValue, "The '" + fieldName + "' field value does not match.");
                } else {
                    assertEquals(expectedValue, actualValue, "The '" + fieldName + "' field value does not match.");
                }
            } else {
                fail("Expected a project in the response, but the projects array was empty.");
            }
        } else {
            // For POST/PUT responses, use the existing verification method
            the_project_details_should_show_field_with_value(fieldName, expectedValue);
        }
    }
}
