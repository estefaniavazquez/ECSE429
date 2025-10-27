package steps;

import api.TodoApi;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import models.Todo;
import setup.ScenarioContext;

import java.util.Map;
import static org.junit.jupiter.api.Assertions.*; 

/**
 * Implements the Gherkin steps for CRUD and verification related to the /todos endpoint.
 */
public class TodoStepDefinitions {

    private final ScenarioContext context;
    private final TodoApi api; 

    public TodoStepDefinitions(ScenarioContext context, TodoApi api) {
        this.context = context;
        this.api = api;
    }

    // ==============================================================================
    // STEP 1: WHEN - ACTION (T1)
    // ==============================================================================
    @When("I send a POST request to {string} with body:")
    public void i_send_a_post_request_to_with_body(String endpoint, DataTable dataTable) {
        // 1. Extract the single row of input data from the Gherkin table
        Map<String, String> data = dataTable.asMaps().get(0);
        
        // 2. Create a Todo object from the input data (The Todo constructor handles String-to-Boolean conversion)
        String title = data.get("title");
        String description = data.get("description");
        String doneStatus = data.get("doneStatus");
        
        Todo newTodo = new Todo(title, description, doneStatus);

        // 3. Convert the Todo object to a Map<String, Object> (to ensure boolean primitive)
        Map<String, Object> payloadMap = newTodo.toPayloadMap();
        String jsonBody = api.toJson(payloadMap);

        // 4. Send the request and store the response
        Response response = api.postRequest(endpoint, jsonBody);
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
    public void the_response_status_code_should_be(int expectedStatusCode) {
        Response response = context.getLastResponse();
        assertEquals(expectedStatusCode, 
                     response.statusCode(),
                     "Expected status code did not match actual code. Response body: " + response.getBody().asString());
    }
    
    // ==============================================================================
    // STEP 3: AND - ASSERTION (Verify individual field value in success response)
    // ==============================================================================
    @And("the response body should contain the value {string} for the {string} field")
    public void the_response_body_should_contain_the_value_for_the_field(String expectedValue, String fieldName) {
        Response response = context.getLastResponse();
        
        if (response.statusCode() == 201 || response.statusCode() == 200) {
            
            if (fieldName.equals("doneStatus")) {
                // --- BOOLEAN ASSERTION BRANCH ---
                Boolean expectedBoolean = Boolean.parseBoolean(expectedValue);
                // Use getBoolean() to correctly extract the unquoted primitive value
                Boolean actualBoolean = response.jsonPath().getBoolean(fieldName); 
                
                assertEquals(expectedBoolean, actualBoolean,
                    String.format("Field '%s' boolean value did not match. Expected: %s, Actual: %s", 
                                  fieldName, expectedBoolean, actualBoolean));
            
            } else if (expectedValue.isEmpty()) {
                // --- EMPTY STRING ASSERTION BRANCH (for description in Alternate Flow) ---
                String actualValue = response.jsonPath().getString(fieldName);
                assertTrue(
                    actualValue == null || actualValue.isEmpty(),
                    String.format("Field '%s' was expected to be empty but was '%s'.", fieldName, actualValue)
                );
            } else {
                // --- STANDARD STRING ASSERTION BRANCH (for title and description) ---
                String actualValue = response.jsonPath().getString(fieldName);
                assertEquals(
                    expectedValue, 
                    actualValue,
                    String.format("Field '%s' value did not match.", fieldName)
                );
            }
        }
    }
    
    // ==============================================================================
    // STEP 4: AND - ASSERTION (Verify error message in failure response)
    // ==============================================================================
    @And("the response body should confirm the error message {string} when applicable")
    public void the_response_body_should_confirm_the_error_message_when_applicable(String expectedMessage) {
        Response response = context.getLastResponse();
    
        // 1. CASE: Success Expected (The Examples cell is empty, so expectedMessage == "")
        if (expectedMessage.isEmpty()) {
            // Assert that we did NOT receive a 4xx error.
            assertFalse(response.statusCode() >= 400, 
                    "Did not expect an error, but received status code: " + response.statusCode() + 
                    ". Body: " + response.getBody().asString());
            return; // Exit the step, test passed for this assertion.
        }

        // 2. CASE: Failure Expected (expectedMessage is non-empty, i.e., "title : field is mandatory")
    
        // Assert that we received a 4xx error status code
        assertTrue(response.statusCode() >= 400, 
               "Expected error status code (4xx) but received: " + response.statusCode());
               
        String fullResponseBody = response.getBody().asString();
        // Remove quotes if they were mistakenly left in the Examples table
        String messageToCheck = expectedMessage.replace("\"", "").trim(); 

        // Assert that the error message is contained in the response body
        assertTrue(
            fullResponseBody.contains(messageToCheck),
            String.format("Expected error message '%s' not found in response body: %s", messageToCheck, fullResponseBody)
        );
    }
}
