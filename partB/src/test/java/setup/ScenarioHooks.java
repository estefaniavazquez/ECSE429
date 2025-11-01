package setup;

import api.TodoApi;
import io.cucumber.java.en.Given;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Handles all setup, teardown, and environment checks for the Gherkin Background.
 */
public class ScenarioHooks {

    private final TodoApi api;

    public ScenarioHooks(TodoApi api) {
        this.api = api;
    }

    // ==============================================================================
    // Check Service Status
    // Given the Rest API Todo List Manager is running
    // ==============================================================================
    @Given("the Rest API Todo List Manager is running")
    public void the_rest_api_todo_list_manager_is_running() {
        // Use the API client to confirm the service is reachable.
        api.checkServiceStatus(); 
    }

    // ==============================================================================
    // System Reset
    // And my list of tasks is cleared to start fresh
    // ==============================================================================
    @Given("my list of tasks is cleared to start fresh")
    public void the_system_is_initialized_with_an_empty_todo_list() {
        // This method deletes ALL existing todo items to ensure a clean slate.
        api.deleteAllData();
    }
}
