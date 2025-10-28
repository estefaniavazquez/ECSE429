package steps;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import api.CategoryApi;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import models.Category;
import setup.ScenarioContext;

public class CategoryStepDefinitions {

    private final ScenarioContext context;
    private final CategoryApi api;

    // Cucumber will construct this; ScenarioContext already exists from your todos suite.
    public CategoryStepDefinitions(ScenarioContext context) {
        this.context = context;
        this.api = new CategoryApi();
    }

    // -------------------- Background helpers (category-specific) --------------------

    @Given("the Category API is reachable")
    public void the_category_api_is_reachable() {
        api.checkServiceUp();
    }

    @Given("my list of categories is cleared to start fresh")
    public void my_list_of_categories_is_cleared_to_start_fresh() {
        api.deleteAllCategories();
    }

    // -------------------- Create category --------------------

    @When("I send a request to create a category with these details:")
    public void i_send_a_request_to_create_a_category_with_these_details(DataTable dataTable) {
        Map<String, String> data = dataTable.asMaps().get(0);

        String title = data.getOrDefault("title", "");
        String description = data.getOrDefault("description", "");

        Category newCat = new Category(title, description);
        String json = api.toJson(newCat.toPayloadMap());

        Response r = api.postRequest("/categories", json);
        context.setLastResponse(r);

        if (r.getStatusCode() == 201) {
            String id = r.jsonPath().getString("id");
            if (id != null) context.storeId("last_category_id", id);
        }
    }

    // -------------------- Generic assertions (status, fields, errors) --------------------

    @Then("the category creation status should be {int}")
    public void the_category_creation_status_should_be(Integer expected) {
        Response r = context.getLastResponse();
        assertEquals(expected.intValue(), r.statusCode(),
                "Status mismatch. Body: " + r.getBody().asString());
    }

    @And("the saved category should show field {string} with value {string}")
    public void the_saved_category_should_show_field_with_value(String field, String expectedValue) {
        Response r = context.getLastResponse();
        assertTrue(r.statusCode() == 201 || r.statusCode() == 200,
                "Expected success response first. Got: " + r.statusCode());

        String actual = r.jsonPath().getString(field);
        if (expectedValue.isEmpty()) {
            assertTrue(actual == null || actual.isEmpty(),
                    String.format("Field '%s' expected empty but was '%s'", field, actual));
        } else {
            assertEquals(expectedValue, actual, "Field mismatch for '" + field + "'");
        }
    }

    @And("the category error should contain {string}")
    public void the_category_error_should_contain(String expectedFragment) {
        Response r = context.getLastResponse();
        assertTrue(r.statusCode() >= 400, "Expected an error code, got " + r.statusCode());
        String body = r.getBody().asString();
        assertTrue(body.contains(expectedFragment.replace("\"","").trim()),
                "Expected error fragment not found. Body: " + body);
    }
}
