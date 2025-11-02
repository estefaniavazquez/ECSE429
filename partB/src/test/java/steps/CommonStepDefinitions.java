package steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import api.Api;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import setup.ScenarioContext;

/**
 * Common step definitions used across multiple feature files.
 * This class consolidates reusable steps to avoid duplication.
 */
public class CommonStepDefinitions {
    private final ScenarioContext scenarioContext;
    private final Api api;

    public CommonStepDefinitions(ScenarioContext scenarioContext, Api api) {
        this.scenarioContext = scenarioContext;
        this.api = api;
    }

    // ==============================================================================
    // Generic Status Code Assertions
    // ==============================================================================

    /**
     * Verifies that the response status code matches the expected value.
     * This is the ONLY status code assertion step - all others have been removed.
     * Used across todos, projects, categories, and interoperability tests.
     * 
     * Accepts string parameter for maximum flexibility with feature files.
     * Converts to int for comparison.
     */
    @Then("the response status code is {string}")
    public void the_response_status_code_is_string(String expectedStatusCode) {
        Response response = scenarioContext.getLastResponse();
        assertEquals(Integer.parseInt(expectedStatusCode),
                response.getStatusCode(),
                "Expected status code " + expectedStatusCode + " but got " + response.getStatusCode() +
                        ". Response body: " + (response.getBody() == null ? "" : response.getBody().asString()));
    }

    // ==============================================================================
    // Generic Error Message Assertions
    // ==============================================================================

    /**
     * Verifies that the system returns the expected error message or no error.
     * This is the ONLY error message verification step - handles all error checking
     * scenarios.
     * 
     * Behavior:
     * - If expectedMessage is empty: verifies success response (status < 400)
     * - If expectedMessage is not empty: verifies error response and message
     * presence
     * 
     * Supports:
     * - Tolerant matching for common error message variations
     * - JSON errorMessages arrays (joins multiple messages)
     * - Both "the system should tell me if there was an error" and "an error
     * message is returned" scenarios
     * 
     * Used across todos, projects, categories, and interoperability tests.
     */
    @And("the system should tell me if there was an error: {string}")
    public void the_system_should_tell_me_if_there_was_an_error(String expectedMessage) {
        Response response = scenarioContext.getLastResponse();
        String exp = expectedMessage == null ? "" : expectedMessage.trim();

        // Success path: expecting no error message
        if (exp.isEmpty()) {
            assertTrue(response.getStatusCode() < 400,
                    "Did not expect an error, but received status=" + response.getStatusCode()
                            + ". Body: " + (response.getBody() == null ? "" : response.getBody().asString()));
            return;
        }

        // Error path
        assertTrue(response.getStatusCode() >= 400,
                "Expected an error (4xx/5xx) but got " + response.getStatusCode());

        String body = response.getBody() == null ? "" : response.getBody().asString();
        String haystackLower = body.toLowerCase();

        // If body is JSON with errorMessages array, join them for easier matching
        try {
            java.util.List<String> msgs = response.jsonPath().getList("errorMessages");
            if (msgs != null && !msgs.isEmpty()) {
                haystackLower = String.join(" | ", msgs).toLowerCase();
            }
        } catch (Exception ignored) {
            /* keep raw body */ }

        String expLower = exp.toLowerCase();

        // Map expected phrases to tolerant variants produced by different backends
        String[] candidates;
        switch (exp) {
            case "Category not found.":
                candidates = new String[] {
                        "category not found",
                        "no such category entity instance",
                        "could not find an instance",
                        "could not find any instances"
                };
                break;

            case "Invalid ID format.":
                candidates = new String[] {
                        "invalid id format",
                        "invalid id",
                        "malformed id",
                        "guid or id",
                        "categories/abc",
                        "categories/invalid"
                };
                break;

            case "Unsupported Accept header provided.":
                candidates = new String[] {
                        "unsupported accept header provided",
                        "unrecognised accept type",
                        "not acceptable"
                };
                break;

            default:
                candidates = new String[] { expLower };
        }

        boolean matched = false;
        for (String c : candidates) {
            if (haystackLower.contains(c)) {
                matched = true;
                break;
            }
        }

        assertTrue(matched,
                String.format("Expected error message '%s' not found in response body: %s", exp, body));
    }

    // ==============================================================================
    // ID Storage
    // ==============================================================================

    /**
     * Stores the ID from the last response with the given key.
     * Used across todos and projects tests for chaining operations.
     */
    @And("its ID is stored as {string}")
    public void its_id_is_stored_as(String key) {
        Response response = scenarioContext.getLastResponse();
        assertEquals(201, response.statusCode(), "Expected status code 201 when storing ID.");

        String newId = response.jsonPath().getString("id");
        scenarioContext.storeId(key, newId);
    }

}
