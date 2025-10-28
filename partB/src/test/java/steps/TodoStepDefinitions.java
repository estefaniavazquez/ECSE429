package steps;

import api.TodoApi;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.lu.an;
import io.restassured.response.Response;
import models.Todo;
import setup.ScenarioContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apiguardian.api.API;

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
    // When (T1)
    // ==============================================================================
    @When("I send a request to create a task with these details:")
    public void i_send_a_request_to_create_a_task_with_these_details(DataTable dataTable) {
        // Extract the single row of input data from the Gherkin table
        Map<String, String> data = dataTable.asMaps().get(0);
        
        // Create a Todo object from the input data
        String title = data.get("title");
        String description = data.get("description");
        String doneStatus = data.get("doneStatus");
        
        Todo newTodo = new Todo(title, description, doneStatus);

        // Convert the Todo object to a Map<String, Object>
        Map<String, Object> payloadMap = newTodo.toPayloadMap();
        String jsonBody = api.toJson(payloadMap);

        // Send the request and store the response
        Response response = api.postRequest("/todos", jsonBody, "application/json");
        assertNotNull(response, "API postRequest returned null response");
        context.setLastResponse(response);

        // If successful (201), store the new ID
        if (response.getStatusCode() == 201) {
            String newId = response.jsonPath().getString("id");
            context.storeId("last_created_id", newId); 
        }
    }

    // ==============================================================================
    // Generic Status Code Assertion Step
    // ==============================================================================
    @Then("the status code should be {int}")
    public void the_creation_status_should_be(int expectedStatusCode) {
        Response response = context.getLastResponse();
        assertNotNull(response, "Response object is null - API call may have failed");
        assertEquals(expectedStatusCode, 
                     response.statusCode(),
                     "Expected status code did not match actual code. Response body: " + response.getBody().asString());
    }
    
    // ==============================================================================
    // And Assertions for verifying saved/updated fields
    // ==============================================================================
    @And("the saved task should show field {string} with value {string}")
    public void the_saved_task_should_show_field_with_value(String fieldName, String expectedValue) {
        Response response = context.getLastResponse();
        assertNotNull(response, "Response object is null - API call may have failed");
        
        if (response.statusCode() == 201 || response.statusCode() == 200) {
            
            if (fieldName.equals("doneStatus")) {
                Boolean expectedBoolean = Boolean.parseBoolean(expectedValue);
                // Use getBoolean() to correctly extract the unquoted primitive value
                Boolean actualBoolean = response.jsonPath().getBoolean(fieldName); 
                
                assertEquals(expectedBoolean, actualBoolean,
                    String.format("Field '%s' boolean value did not match. Expected: %s, Actual: %s", 
                                  fieldName, expectedBoolean, actualBoolean));
            
            } else if (expectedValue.isEmpty()) {
                String actualValue = response.jsonPath().getString(fieldName);
                assertTrue(
                    actualValue == null || actualValue.isEmpty(),
                    String.format("Field '%s' was expected to be empty but was '%s'.", fieldName, actualValue)
                );
            } else {
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
    // And Error Message Verification Step
    // ==============================================================================
    @And("the system should tell me if there was an error: {string}")
    public void the_system_should_tell_me_if_there_was_an_error(String expectedMessage) {
        Response response = context.getLastResponse();
        assertNotNull(response, "Response object is null - API call may have failed");
    
        // Success 
        if (expectedMessage.isEmpty()) {
            // Assert that we did NOT receive a 4xx error.
            assertFalse(response.statusCode() >= 400, 
                    "Did not expect an error, but received status code: " + response.statusCode() + 
                    ". Body: " + response.getBody().asString());
            return;
        }

        // Failure
    
        // Assert that we received a 4xx error status code
        assertTrue(response.statusCode() >= 400, 
               "Expected error status code (4xx) but received: " + response.statusCode());
               
        String fullResponseBody = response.getBody().asString();

        String messageToCheck = expectedMessage.replace("\"", "").trim(); 

        // Assert that the error message is contained in the response body
        assertTrue(
            fullResponseBody.contains(messageToCheck),
            String.format("Expected error message '%s' not found in response body: %s", messageToCheck, fullResponseBody)
        );

        // print confirmation
        System.out.println("Confirmed presence of expected error message: " + messageToCheck);
    }

    // ==============================================================================
    // Given (T2, T3, T4, T5)
    // ==============================================================================
    @Given("a todo item exists with title {string}, description {string}, and doneStatus {string}")
    public void a_todo_exists_with_title_description_and_doneStatus(String title, String description, String doneStatus) {
        // Create a new todo item with the provided details
        Todo todo = new Todo(title, description, doneStatus);

        // Verify creation was successful
        Response response = api.postRequest("/todos", api.toJson(todo.toPayloadMap()), "application/json");
        assertNotNull(response, "API postRequest returned null response when creating prerequisite todo");
        assertEquals(201, response.statusCode(), "Failed to create prerequisite todo item.");
        String newId = response.jsonPath().getString("id");
        context.storeId("preexisting_todo_id", newId);
        context.setLastResponse(response);
    }

    // ==============================================================================
    // Id Storage Step (T2, T3, T4, T5)
    // ==============================================================================
    @And("its ID is stored as {string}")
    public void its_id_is_stored_as(String key) {
        Response response = context.getLastResponse();
        assertNotNull(response, "Response object is null - API call may have failed");
        assertEquals(201, response.statusCode(), "Expected status code 201 when creating todo item.");
        
        String newId = response.jsonPath().getString("id");
        context.storeId(key, newId);
    }

    // ==============================================================================
    // Update Step (T2)
    // ==============================================================================
    @When("I send a request to update task {string} with body:")
    public void i_send_a_request_to_update_task_with_body(String targetIdKey, DataTable dataTable) {
        // Extract the target ID from the context
        String targetId = context.retrieveId(targetIdKey);
        // Extract the single row of input data from the Gherkin table
        Map<String, String> data = dataTable.asMaps().get(0);
        // Create a Todo object from the input data
        String description = data.get("description");
        String doneStatus = data.get("doneStatus");
        Todo updatedTodo = new Todo(null, description, doneStatus);
        // Convert the Todo object to a Map<String, Object>
        Map<String, Object> payloadMap = updatedTodo.toPayloadMap();
        String jsonBody = api.toJson(payloadMap);

        // Send the POST request to update the todo item
        Response response = api.postRequest("/todos/" + targetId, jsonBody, "application/json");
        assertNotNull(response, "API postRequest returned null response during update");
        context.setLastResponse(response);
    }


    // ==============================================================================
    // When (T3)
    // ==============================================================================
    @When("I send a request to view tasks filtered by the query {string} and requested format {string}")
    public void i_send_a_request_to_view_tasks_filtered_by_the_query_and_requested_format(String queryParams, String acceptHeader) {
        // Send GET request with query parameters and Accept header
        Response response = api.getRequest("/todos" + queryParams, acceptHeader);
        assertNotNull(response, "API getRequest returned null response");
        context.setLastResponse(response);
    }

    // ==============================================================================
    // Task list format (T3)
    // ==============================================================================
    @And("the task list format should be {string}")
    public void the_task_list_format_should_be(String expectedContentType) {
        Response response = context.getLastResponse();
        assertNotNull(response, "Response object is null - API call may have failed");
        String actualContentType = response.getHeader("Content-Type");
        assertTrue(
            actualContentType.contains(expectedContentType),
            String.format("Expected Content-Type to contain '%s' but was '%s'", expectedContentType, actualContentType)
        );
    }

    // ==============================================================================
    // Task count and filter status (T3)
    // ==============================================================================
    @And("the list should contain {int} tasks with completion status {string}")
    public void the_list_should_contain_tasks_with_completion_status(int expectedCount, String filterStatus) {
        Response response = context.getLastResponse();
        assertNotNull(response, "Response object is null - API call may have failed");
        List<Map<String, Object>> todos;
        Boolean expectedDoneStatus = Boolean.parseBoolean(filterStatus);
        if (response.getHeader("Content-Type").contains("application/xml")) {
            // For XML, we need to parse each field individually since XML structure is different
            List<String> doneStatusList = response.xmlPath().getList("todos.todo.doneStatus");
            List<String> titleList = response.xmlPath().getList("todos.todo.title");
            List<String> descriptionList = response.xmlPath().getList("todos.todo.description");
            List<String> idList = response.xmlPath().getList("todos.todo.id");
            
            // Convert XML data to Map structure for consistency
            todos = new ArrayList<>();
            for (int i = 0; i < doneStatusList.size(); i++) {
                Map<String, Object> todo = new HashMap<>();
                todo.put("doneStatus", Boolean.parseBoolean(doneStatusList.get(i)));
                todo.put("title", titleList.get(i));
                todo.put("description", descriptionList.get(i));
                todo.put("id", idList.get(i));
                todos.add(todo);
            }
            assertNotNull(todos, "Failed to parse todos from XML response");
        } else {
            todos = response.jsonPath().getList("todos");
            assertNotNull(todos, "Failed to parse todos from response");
        }
        
        assertNotNull(todos, "Failed to parse todos from response");
        System.out.println("Retrieved " + todos.size() + " todos from response for filtering check.");
        long actualCount = todos.stream()
            .filter(todo -> {
                Object doneStatusObj = todo.get("doneStatus");
                if (doneStatusObj instanceof String) {
                    return Boolean.parseBoolean((String) doneStatusObj) == expectedDoneStatus;
                } else if (doneStatusObj instanceof Boolean) {
                    return ((Boolean) doneStatusObj).booleanValue() == expectedDoneStatus;
                }
                return false;
            })
            .count();
        assertEquals(expectedCount, actualCount,
            String.format("Expected %d tasks with doneStatus %s, but found %d.",
                          expectedCount, filterStatus, actualCount));
    }

    // ==============================================================================
    // When (T4)
    // ==============================================================================
    @When("I send a request to delete task {string} with requested format {string}")
    public void i_send_a_request_to_delete_task_with_requested_format(String targetIdKey, String acceptHeader) {
        // Extract the target ID from the context
        String targetId = context.retrieveId(targetIdKey);
        // Send DELETE request with Accept header
        Response response = api.deleteRequest("/todos/" + targetId, acceptHeader);
        assertNotNull(response, "API deleteRequest returned null response");
        context.setLastResponse(response);
    }

    // ==============================================================================
    // Check Existence Step (T4)
    // ==============================================================================
    @And("the task with ID {string} should yield a {string} on a quick check")
    public void the_task_with_id_should_yield_a_on_a_quick_check(String targetIdKey, String expectedStatusCodeStr) {
        // Extract the target ID from the context
        String targetId = context.retrieveId(targetIdKey);
        int expectedStatusCode = Integer.parseInt(expectedStatusCodeStr);
        // Send GET request to check existence
        Response response = api.getRequest("/todos/" + targetId, "application/json");
        assertNotNull(response, "API getRequest returned null response during existence check");
        assertEquals(expectedStatusCode, response.getStatusCode(),
            String.format("Expected status code %d when checking existence of todo ID %s, but got %d.",
                          expectedStatusCode, targetId, response.getStatusCode()));
    }   

    // ==============================================================================
    // When (T5)
    // ==============================================================================
    @When("I send a request to fully replace task {string} with body:")
    public void i_send_a_request_to_fully_replace_task_with_body(String targetIdKey, DataTable dataTable) {
        // Extract the target ID from the context
        String targetId = context.retrieveId(targetIdKey);
        // Extract the single row of input data from the Gherkin table
        Map<String, String> data = dataTable.asMaps().get(0);
        
        // Create a Todo object from the input data (The Todo constructor handles String-to-Boolean conversion)
        String title = data.get("title");
        String description = data.get("description");
        String doneStatus = data.get("doneStatus");
        
        Todo newTodo = new Todo(title, description, doneStatus);

        // Convert the Todo object to a Map<String, Object> (to ensure boolean primitive)
        Map<String, Object> payloadMap = newTodo.toPayloadMap();
        String jsonBody = api.toJson(payloadMap);
        // Send PUT request with JSON body
        Response response = api.putRequest("/todos/" + targetId, jsonBody, "application/json");
        assertNotNull(response, "API putRequest returned null response");
        context.setLastResponse(response);
    }

}
