package steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import api.InteroperabilityApi;
import io.restassured.response.Response;
import models.CategoryInteroperability;
import models.ProjectInteroperability;
import models.TodoInteroperability;
import models.Relationship;
import setup.ScenarioContext;

import java.util.List;
import java.util.Map;

public class InteroperabilityStepDefinitions {
    private final ScenarioContext context;
    private final InteroperabilityApi api;

    public InteroperabilityStepDefinitions(ScenarioContext context, InteroperabilityApi api) {
        this.context = context;
        this.api = api;
    }

    @Given("the system is running")
    public void theSystemIsRunning() {
        api.checkServiceStatus();
    }

    @Given("the system is initialized with no data")
    public void theSystemIsInitializedWithNoData() {
        // Get all todos
        Response todosResponse = api.getRequest("/todos");
        TodoInteroperability[] todos = todosResponse.jsonPath().getObject("todos", TodoInteroperability[].class);

        // Delete each todo
        if (todos != null) {
            for (TodoInteroperability todo : todos) {
                api.deleteRequest("/todos/" + todo.getId());
            }
        }

        // Get all projects
        Response projectsResponse = api.getRequest("/projects");
        ProjectInteroperability[] projects = projectsResponse.jsonPath().getObject("projects", ProjectInteroperability[].class);

        // Delete each project
        if (projects != null) {
            for (ProjectInteroperability project : projects) {
                api.deleteRequest("/projects/" + project.getId());
            }
        }

        // Get all categories
        Response categoriesResponse = api.getRequest("/categories");
        CategoryInteroperability[] categories = categoriesResponse.jsonPath().getObject("categories", models.CategoryInteroperability[].class);

        // Delete each category
        if (categories != null) {
            for (CategoryInteroperability category : categories) {
                api.deleteRequest("/categories/" + category.getId());
            }
        }
    }

    @Given("the following todos exist:")
    public void theFollowingTodosExist(DataTable dataTable) {
        Map<String, String> row = dataTable.asMaps().get(0);
        String id = row.get("todoId");
        String title = row.get("todoTitle");
        String doneStatus = row.get("todoDoneStatus");
        String description = row.get("todoDescription");
        TodoInteroperability todo = new TodoInteroperability(title, description, doneStatus);

        Map<String, Object> payloadMap = todo.toPayloadMap();
        String jsonBody = api.toJson(payloadMap);

        Response response = api.postRequest("/todos", jsonBody);
        context.setLastResponse(response);

        if (response.getStatusCode() == 201) {
            String newId = response.jsonPath().getString("id");
            if (id.equals(newId)) {
                // IDs match, store the ID
                context.storeId("last_created_todo_id", id);
            }
            else {
                // IDs do not match, handle the discrepancy
                throw new RuntimeException("Expected todo ID " + id + " but got " + newId);
            }
        }
    }

    @Given("the following projects exist:")
    public void theFollowingProjectsExist(DataTable dataTable) {
    }

    @Given("the following categories exist:")
    public void theFollowingCategoriesExist(DataTable dataTable) {
        Map<String, String> row = dataTable.asMaps().get(0);
        String id = row.get("categoryId");
        String title = row.get("categoryTitle");
        String description = row.get("categoryDescription");
        CategoryInteroperability category = new CategoryInteroperability(title, description);

        Map<String, Object> payloadMap = category.toPayloadMap();
        String jsonBody = api.toJson(payloadMap);

        Response response = api.postRequest("/categories", jsonBody);
        context.setLastResponse(response);

        if (response.getStatusCode() == 201) {
            String newId = response.jsonPath().getString("id");
            if (id.equals(newId)) {
                // IDs match, store the ID
                context.storeId("last_created_category_id", id);
            }
            else {
                // IDs do not match, handle the discrepancy
                throw new RuntimeException("Expected category ID " + id + " but got " + newId);
            }
        }
    }

    @When("the user associates the todo with id {string} with the project with id {string}")
    public void theUserAssociatesTheTodoWithIdWithTheProjectWithId(String todoId, String projectId) {
    }

    @When("the user removes the association between the todo with id {string} and the project with id {string}")
    public void theUserRemovesTheAssociationBetweenTheTodoWithIdAndTheProjectWithId(String todoId, String projectId) {
    }

    @When("the user deletes the todo with id {string}")
    public void theUserDeletesTheTodoWithId(String todoId) {
    }

    @When("the user gets the todos associated with the project with id {string}")
    public void theUserGetsTheTodosAssociatedWithTheProjectWithId(String projectId) {
    }

    @When("the user assigns the category with id {string} to the todo with id {string}")
    public void theUserAssignsTheCategoryWithIdToTheTodoWithId(String categoryId, String todoId) {
        Relationship todo = new Relationship(todoId);

        Map<String, Object> payloadMap = todo.toPayloadMap();
        String jsonBody = api.toJson(payloadMap);

        Response response = api.postRequest("/categories/" + categoryId + "/todos", jsonBody);
        context.setLastResponse(response);
    }

    @When("the user assigns the todo with id {string} to the category with id {string}")
    public void theUserAssignsTheTodoWithIdToTheCategoryWithId(String todoId, String categoryId) {
    }

    @When("the user assigns the category with id {string} to the project with id {string}")
    public void theUserAssignsTheCategoryWithIdToTheProjectWithId(String categoryId, String projectId) {
    }

    @Then("an instance of a relationship taskof exists in the todo with id {string} that points to the project with id {string}")
    public void anInstanceOfARelationshipTaskofExistsInTheTodoWithIdThatPointsToTheProjectWithId(String todoId, String projectId) {
    }

    @Then("no instance of a relationship taskof exists in the todo with id {string} that points to the project with id {string}")
    public void noInstanceOfARelationshipTaskofExistsInTheTodoWithIdThatPointsToTheProjectWithId(String todoId, String projectId) {
    }

    @Then("no instance of a todo with id {string} exists")
    public void noInstanceOfATodoWithIdExists(String todoId) {
    }

    @Then("the returned todos associated with the project with id {string} contain:")
    public void theReturnedTodosAssociatedWithTheProjectWithIdContain(String projectId, DataTable dataTable) {
    }

    @Then("the returned todos associated with the project with id {string} is empty")
    public void theReturnedTodosAssociatedWithTheProjectWithIdIsEmpty(String projectId) {
    }

    @Then("the entries in the relationship taskof of the todo with id {string} now contain:")
    public void theEntriesInTheRelationshipTaskofOfTheTodoWithIdNowContain(String todoId, DataTable dataTable) {
    }

    @Then("the entries in the relationship tasks of the project with id {string} now contain:")
    public void theEntriesInTheRelationshipTasksOfTheProjectWithIdNowContain(String projectId, DataTable dataTable) {
    }

    @Then("the entries in the relationship todos of the category with id {string} now contain:")
    public void theEntriesInTheRelationshipTodosOfTheCategoryWithIdNowContain(String categoryId, DataTable dataTable) {
        // Get all the column entries from the data table
        List<String> expectedTodoIds = dataTable.asMaps().stream()
                .map(row -> row.get("todoId"))
                .toList();

        // Make a GET request to retrieve the current todos in the category
        Response response = api.getRequest("/categories/" + categoryId);
        List<String> actualTodoIds = response.jsonPath().getList("todos.id");

        assert(actualTodoIds.containsAll(expectedTodoIds) && expectedTodoIds.containsAll(actualTodoIds));
    }

    @Then("the entries in the relationship categories of the todo with id {string} now contain:")
    public void theEntriesInTheRelationshipCategoriesOfTheTodoWithIdNowContain(String todoId, DataTable dataTable) {
    }

    @Then("the entries in the relationship projects of the category with id {string} now contain:")
    public void theEntriesInTheRelationshipProjectsOfTheCategoryWithIdNowContain(String categoryId, DataTable dataTable) {
    }

    @Then("an error message {string} is returned")
    public void anErrorMessageIsReturned(String expectedErrorMessage) {
    }

    @Then("the response status code is {string}")
    public void theResponseStatusCodeIs(String expectedStatusCode) {
        Response response = context.getLastResponse();

        assert(response.getStatusCode() == Integer.parseInt(expectedStatusCode));
    }

    @And("the entries in the relationship taskof of the todo with id {string} contain:")
    public void theEntriesInTheRelationshipTaskofOfTheTodoWithIdContain(String todoId, DataTable dataTable) {
    }

    @And("the entries in the relationship tasks of the project with id {string} contain:")
    public void theEntriesInTheRelationshipTasksOfTheProjectWithIdContain(String projectId, DataTable dataTable) {
    }

    @And("the entries in the relationship todos of the category with id {string} contain:")
    public void theEntriesInTheRelationshipTodosOfTheCategoryWithIdContain(String categoryId, DataTable dataTable) {
    }

    @And("the entries in the relationship categories of the todo with id {string} contain:")
    public void theEntriesInTheRelationshipCategoriesOfTheTodoWithIdContain(String todoId, DataTable dataTable) {
    }

    @And("the entries in the relationship projects of the category with id {string} contain:")
    public void theEntriesInTheRelationshipProjectsOfTheCategoryWithIdContain(String categoryId, DataTable dataTable) {
    }
}
