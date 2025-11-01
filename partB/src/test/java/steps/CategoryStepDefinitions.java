package steps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import api.CategoryApi;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.Category;
import setup.ScenarioContext;

public class CategoryStepDefinitions {

    private static final String BASE_URL = "http://localhost:4567";

    private final ScenarioContext context;
    private final CategoryApi api;

    // local alias store (e.g., "category_id" -> "123")
    private final Map<String, String> idAliases = new HashMap<>();

    public CategoryStepDefinitions(ScenarioContext context) {
        this.context = context;
        this.api = new CategoryApi();
    }

    /* -------------------- Background -------------------- */

    @Given("the Category API is reachable")
    public void the_category_api_is_reachable() {
        api.checkServiceUp();
    }

    @Given("my list of categories is cleared to start fresh")
    public void my_list_of_categories_is_cleared_to_start_fresh() {
        api.deleteAllCategories();
        idAliases.clear();
    }

    /* -------------------- Create -------------------- */

    @When("I send a request to create a category with these details:")
    public void i_send_a_request_to_create_a_category_with_these_details(DataTable dataTable) {
        Map<String, String> data = dataTable.asMaps(String.class, String.class).get(0);
        String title = data.getOrDefault("title", "");
        String description = data.getOrDefault("description", "");

        Category payload = new Category(title, description);
        String json = api.toJson(payload.toPayloadMap());

        Response r = api.postRequest("/categories", json);
        context.setLastResponse(r);

        if (r.getStatusCode() == 201) {
            String id = r.jsonPath().getString("id");
            if (id != null && !id.isEmpty()) {
                idAliases.put("last_category_id", id);
            }
        }
    }

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
                "Expected success response first. Got: " + r.getStatusCode());

        String actual = r.jsonPath().getString(field);
        if (expectedValue.isEmpty()) {
            assertTrue(actual == null || actual.isEmpty(),
                    String.format("Field '%s' expected empty but was '%s'", field, actual));
        } else {
            assertEquals(expectedValue, actual, "Field mismatch for '" + field + "'");
        }
    }

    /* -------------------- Test data creation + ID storage -------------------- */

    @Given("a category exists with title {string} and description {string}")
    public void a_category_exists_with_title_and_description(String title, String description) {
        Category payload = new Category(title, description);
        String json = api.toJson(payload.toPayloadMap());

        Response r = api.postRequest("/categories", json);
        assertEquals(201, r.getStatusCode(), "Seed category creation failed: " + r.asString());

        context.setLastResponse(r);
        String id = r.jsonPath().getString("id");
        assertTrue(id != null && !id.isEmpty(), "Seed category missing id");
        idAliases.put("last_category_id", id);
    }

    @And("its ID is stored as {string}")
    public void its_id_is_stored_as(String key) {
        Response r = context.getLastResponse();
        String id = (r != null) ? r.jsonPath().getString("id") : idAliases.get("last_category_id");
        assertTrue(id != null && !id.isEmpty(), "No category id available to store");
        idAliases.put(key, id);
    }

    /* -------------------- Update (POST /categories/:id) -------------------- */

    @When("I send a request to update category {string} with body:")
    public void i_send_a_request_to_update_category_with_body(String targetId, DataTable dataTable) {
        Map<String, String> data = dataTable.asMaps(String.class, String.class).get(0);
        String title = data.getOrDefault("title", "");
        String description = data.getOrDefault("description", "");

        String id = resolveTargetId(targetId);

        Category payload = new Category(title, description);
        String json = api.toJson(payload.toPayloadMap());

        Response r = api.postRequest("/categories/" + id, json);
        context.setLastResponse(r);
    }

    @Then("the update status should be {int}")
    public void the_update_status_should_be(Integer expected) {
        Response r = context.getLastResponse();
        assertEquals(expected.intValue(), r.statusCode(),
                "Update status mismatch. Body: " + r.getBody().asString());
    }

    @And("the updated category should show field {string} with value {string}")
    public void the_updated_category_should_show_field_with_value(String field, String expectedValue) {
        the_saved_category_should_show_field_with_value(field, expectedValue);
    }

    /* -------------------- List (GET /categories with Accept) -------------------- */

    @When("I send a request to view all categories in format {string}")
    public void i_send_a_request_to_view_all_categories_in_format(String accept) {
        Response r = RestAssured.given()
                .baseUri(BASE_URL)
                .header("Accept", accept)
                .get("/categories")
                .then().extract().response();
        context.setLastResponse(r);
    }

    @Then("the response status should be {int}")
    public void the_response_status_should_be(Integer expected) {
        Response r = context.getLastResponse();
        assertEquals(expected.intValue(), r.getStatusCode(),
                "Status mismatch. Body: " + r.getBody().asString());
    }

    @And("the response format should be {string}")
    public void the_response_format_should_be(String expectedContentType) {
        if (expectedContentType == null || expectedContentType.trim().isEmpty()) {
            return; // error flow case
        }
        Response r = context.getLastResponse();
        String ct = r.getHeader("Content-Type");
        assertTrue(ct != null && ct.contains(expectedContentType),
                "Content-Type mismatch. Expected contains: " + expectedContentType + " but was: " + ct);
    }

    @And("the list should contain at least {int} categories")
    public void the_list_should_contain_at_least_categories(Integer expectedMin) {
        Response r = context.getLastResponse();
        String ct = r.getHeader("Content-Type");
        int count;
        if (ct != null && ct.toLowerCase().contains("json")) {
            List<Map<String, Object>> cats = r.jsonPath().getList("categories");
            count = (cats == null) ? 0 : cats.size();
        } else {
            List<?> cats = r.xmlPath().getList("categories.category");
            count = (cats == null) ? 0 : cats.size();
        }
        assertTrue(count >= expectedMin,
                "Expected at least " + expectedMin + " categories but was " + count + ". Body: " + r.asString());
    }

    /* -------------------- View one (GET /categories/:id) -------------------- */

    @When("I send a request to view category {string}")
    public void i_send_a_request_to_view_category(String targetId) {
        String id = resolveTargetId(targetId);
        Response r = api.getRequest("/categories/" + id);
        context.setLastResponse(r);
    }

    @And("the category details should contain field {string} with value {string}")
    public void the_category_details_should_contain_field_with_value(String field, String expectedValue) {
        the_saved_category_should_show_field_with_value(field, expectedValue);
    }

    /* -------------------- Delete (DELETE /categories/:id) -------------------- */

    @When("I send a request to delete category {string}")
    public void i_send_a_request_to_delete_category(String targetId) {
        String id = resolveTargetId(targetId);
        Response r = api.deleteRequest("/categories/" + id);
        context.setLastResponse(r);
        idAliases.put("last_deleted_candidate_id", id);
    }

    @Then("the deletion status should be {int}")
    public void the_deletion_status_should_be(Integer expected) {
        Response r = context.getLastResponse();
        assertEquals(expected.intValue(), r.getStatusCode(),
                "Deletion status mismatch. Body: " + r.getBody().asString());
    }

    @And("when I verify deletion by requesting GET \\/categories\\/{string}, the response should be {int}")
    public void when_i_verify_deletion_by_requesting_get_categories_the_response_should_be(String targetId, Integer expected) {
        String id = resolveTargetId(targetId);
        Response r = api.getRequest("/categories/" + id);
        assertEquals(expected.intValue(), r.getStatusCode(),
                "Verification status mismatch. Body: " + r.getBody().asString());
    }

    /* -------------------- Category-specific error step (unique wording) -------------------- */

    @And("the category error should contain {string}")
    public void the_category_error_should_contain(String expectedFragment) {
        Response r = context.getLastResponse();
        assertTrue(r.getStatusCode() >= 400, "Expected an error code, got " + r.getStatusCode());
        String body = r.getBody().asString();
        assertTrue(body != null && body.contains(expectedFragment.replace("\"", "").trim()),
                "Expected error fragment not found. Body: " + body);
    }

    /* -------------------- Helpers -------------------- */

    private String resolveTargetId(String raw) {
        if (raw == null) return null;
        String alias = idAliases.get(raw); // prefer alias (e.g., "category_id")
        return (alias != null && !alias.isEmpty()) ? alias : raw; // else treat as literal id
    }
}
