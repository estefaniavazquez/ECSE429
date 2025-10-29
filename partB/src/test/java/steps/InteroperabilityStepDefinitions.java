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
import java.util.stream.Collectors;

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
        // There can be multiple rows, so we iterate over each row
        List<Map<String, String>> rows = dataTable.asMaps();

        for (Map<String, String> row : rows) {
            String id = row.get("todoId");
            String title = row.get("todoTitle");
            String doneStatus = row.get("todoDoneStatus");
            String description = row.get("todoDescription");
            TodoInteroperability todo = new TodoInteroperability(title, description, doneStatus);

            Map<String, Object> payloadMap = todo.toPayloadMap();
            String jsonBody = api.toJson(payloadMap);

            Response response = api.postRequest("/todos", jsonBody);

            if (response.getStatusCode() == 201) {
                String newId = response.jsonPath().getString("id");
                if (id.equals(newId)) {
                    // IDs match, store the body response in the objectStore
                    System.out.println("Created todo: " + response.getBody().asPrettyString() + "\n");
                }
                else {
                    // IDs do not match, handle the discrepancy
                    throw new RuntimeException("Expected todo ID " + id + " but got " + newId);
                }
            }
        }
    }

    @Given("the following projects exist:")
    public void theFollowingProjectsExist(DataTable dataTable) {
        // There can be multiple rows, so we iterate over each row
        List<Map<String, String>> rows = dataTable.asMaps();

        for (Map<String, String> row : rows) {
            String id = row.get("projectId");
            String title = row.get("projectTitle");
            String description = row.get("projectDescription");
            String completed = row.get("projectCompleted");
            String active = row.get("projectActive");
            ProjectInteroperability project = new ProjectInteroperability(title, description, completed, active);

            Map<String, Object> payloadMap = project.toPayloadMap();
            String jsonBody = api.toJson(payloadMap);

            Response response = api.postRequest("/projects", jsonBody);

            if (response.getStatusCode() == 201) {
                String newId = response.jsonPath().getString("id");
                if (id.equals(newId)) {
                    // Ids match, store the body response in the objectStore
                    System.out.println("Created project: " + response.getBody().asPrettyString() + "\n");
                }
                else {
                    // IDs do not match, handle the discrepancy
                    throw new RuntimeException("Expected project ID " + id + " but got " + newId);
                }
            }
        }
    }

    @Given("the following categories exist:")
    public void theFollowingCategoriesExist(DataTable dataTable) {
        // There is only one row, so we extract that single row
        Map<String, String> row = dataTable.asMaps().get(0);
        String id = row.get("categoryId");
        String title = row.get("categoryTitle");
        String description = row.get("categoryDescription");
        CategoryInteroperability category = new CategoryInteroperability(title, description);

        Map<String, Object> payloadMap = category.toPayloadMap();
        String jsonBody = api.toJson(payloadMap);

        Response response = api.postRequest("/categories", jsonBody);

        if (response.getStatusCode() == 201) {
            String newId = response.jsonPath().getString("id");
            if (id.equals(newId)) {
                // IDs match, store the body response in the objectStore
                System.out.println("Created category: " + response.getBody().asPrettyString() + "\n");
            }
            else {
                // IDs do not match, handle the discrepancy
                throw new RuntimeException("Expected category ID " + id + " but got " + newId);
            }
        }
    }

    @When("the user associates the todo with id {string} with the project with id {string}")
    public void theUserAssociatesTheTodoWithIdWithTheProjectWithId(String todoId, String projectId) {
        Relationship tasksof = new Relationship(projectId);
        Map<String, Object> payloadMap = tasksof.toPayloadMap();
        String jsonBody = api.toJson(payloadMap);

        Response response = api.postRequest("/todos/" + todoId + "/tasksof", jsonBody);
        context.setLastResponse(response);

        if (response.getStatusCode() == 201) {
            System.out.println("Successfully associated todo " + todoId + " with project " + projectId);
        }
        else {
            System.out.println("Failed to associate todo " + todoId + " with project " + projectId + ". Status code: " + response.getStatusCode() + ". Response body: " + response.getBody().asPrettyString());
        }
    }

    @When("the user removes the association between the todo with id {string} and the project with id {string}")
    public void theUserRemovesTheAssociationBetweenTheTodoWithIdAndTheProjectWithId(String todoId, String projectId) {
        Response response = api.deleteRequest("/todos/" + todoId + "/tasksof/" + projectId);
        context.setLastResponse(response);

        if (response.getStatusCode() == 200) {
            System.out.println("Successfully removed association between todo " + todoId + " and project " + projectId);
        }
        else {
            System.out.println("Failed to remove association between todo " + todoId + " and project " + projectId + ". Status code: " + response.getStatusCode());
        }
    }

    @When("the user deletes the todo with id {string}")
    public void theUserDeletesTheTodoWithId(String todoId) {
        Response response = api.deleteRequest("/todos/" + todoId);
        context.setLastResponse(response);

        if (response.getStatusCode() == 200) {
            System.out.println("Successfully deleted todo with id " + todoId);
        }
        else {
            System.out.println("Failed to delete todo with id " + todoId + ". Status code: " + response.getStatusCode());
        }
    }

    @When("the user gets the todos associated with the project with id {string}")
    public void theUserGetsTheTodosAssociatedWithTheProjectWithId(String projectId) {
        Response response = api.getRequest("/projects/" + projectId + "/tasks");
        context.setLastResponse(response);

        if (response.getStatusCode() == 200) {
            System.out.println("Successfully retrieved todos associated with project " + projectId);
        }
        else {
            System.out.println("Failed to retrieve todos associated with project " + projectId + ". Status code: " + response.getStatusCode());
        }
    }

    @When("the user assigns the category with id {string} to the todo with id {string}")
    public void theUserAssignsTheCategoryWithIdToTheTodoWithId(String categoryId, String todoId) {
        Relationship todo = new Relationship(todoId);
        Map<String, Object> payloadMap = todo.toPayloadMap();
        String jsonBody = api.toJson(payloadMap);

        Response response = api.postRequest("/categories/" + categoryId + "/todos", jsonBody);
        context.setLastResponse(response);

        if (response.getStatusCode() == 201) {
            System.out.println("Successfully assigned category " + categoryId + " to todo " + todoId);
        }
        else {
            System.out.println("Failed to assign category " + categoryId + " to todo " + todoId + ". Status code: " + response.getStatusCode());
        }
    }

    @When("the user assigns the category with id {string} to the project with id {string}")
    public void theUserAssignsTheCategoryWithIdToTheProjectWithId(String categoryId, String projectId) {
        Relationship projects = new Relationship(projectId);
        Map<String, Object> payloadMap = projects.toPayloadMap();
        String jsonBody = api.toJson(payloadMap);

        Response response = api.postRequest("/categories/" + categoryId + "/projects", jsonBody);
        context.setLastResponse(response);

        if (response.getStatusCode() == 201) {
            System.out.println("Successfully assigned category " + categoryId + " to project " + projectId);
        }
        else {
            System.out.println("Failed to assign category " + categoryId + " to project " + projectId + ". Status code: " + response.getStatusCode());
        }
    }

    @Then("no instance of a todo with id {string} exists")
    public void noInstanceOfATodoWithIdExists(String todoId) {
        System.out.println("\nVerifying that todo with id " + todoId + " does not exist");

        Response response = api.getRequest("/todos/" + todoId);

        assert(response.getStatusCode() == 404);
    }

    @Then("the returned todos associated with the project with id {string} contain:")
    public void theReturnedTodosAssociatedWithTheProjectWithIdContain(String projectId, DataTable dataTable) {
        System.out.println("\nRetrieving todos associated with the project with id " + projectId);

        // Get all the column entries from the data table
        List<String> expectedTodoIds = dataTable.asMaps().stream()
                .map(row -> row.get("todoId"))
                .toList();
        System.out.println("Expected Todo IDs: " + expectedTodoIds);

        // Make a GET request to retrieve the current todos in the project
        Response response = api.getRequest("/projects/" + projectId + "/tasks");
        System.out.println("\nObtained the following response body: " + response.getBody().asPrettyString() + "\n");

        List<String> actualTodoIds = response.jsonPath().getList("todos.id.flatten()");
        System.out.println("Actual Todos IDs: " + actualTodoIds);

        assert(actualTodoIds.containsAll(expectedTodoIds) && expectedTodoIds.containsAll(actualTodoIds));
    }

    @Then("the returned todos associated with the project with id {string} is empty")
    public void theReturnedTodosAssociatedWithTheProjectWithIdIsEmpty(String projectId) {
        System.out.println("\nRetrieving todos associated with the project with id " + projectId);

        // Make a GET request to retrieve the current todos in the project
        Response response = api.getRequest("/projects/" + projectId + "/tasks");
        System.out.println("\nObtained the following response body: " + response.getBody().asPrettyString() + "\n");

        List<String> actualTodoIds = response.jsonPath().getList("todos.id.flatten()");
        System.out.println("Actual Todos IDs: " + actualTodoIds);

        assert(actualTodoIds.isEmpty());
    }

    @Then("the entries in the relationship tasksof of the todo with id {string} now contain:")
    public void theEntriesInTheRelationshipTasksofOfTheTodoWithIdNowContain(String todoId, DataTable dataTable) {
        System.out.println("\nRetrieving todos associated with the project with id " + todoId);

        // Get all the column entries from the data table
        List<String> expectedProjectIds = dataTable.asMaps().stream()
                .map(row -> row.get("id"))
                .toList();
        System.out.println("Expected Project IDs: " + expectedProjectIds);

        // Make a GET request to retrieve the current tasksof relationships of the todo
        Response response = api.getRequest("/todos/" + todoId);
        System.out.println("\nObtained the following response body: " + response.getBody().asPrettyString() + "\n");

        List<String> actualProjectIds = response.jsonPath().getList("todos.tasksof.id.flatten()");
        System.out.println("Actual Project IDs: " + actualProjectIds);

        assert(actualProjectIds.containsAll(expectedProjectIds) && expectedProjectIds.containsAll(actualProjectIds));
    }

    @Then("the entries in the relationship tasks of the project with id {string} now contain:")
    public void theEntriesInTheRelationshipTasksOfTheProjectWithIdNowContain(String projectId, DataTable dataTable) {
        System.out.println("\nRetrieving todos associated with the project with id " + projectId);

        // Get all the column entries from the data table
        List<String> expectedTodoIds = dataTable.asMaps().stream()
                .map(row -> row.get("id"))
                .toList();
        System.out.println("Expected Todo IDs: " + expectedTodoIds);

        // Make a GET request to retrieve the current tasks in the project
        Response response = api.getRequest("/projects/" + projectId);
        System.out.println("\nObtained the following response body: " + response.getBody().asPrettyString() + "\n");

        List<String> actualTodoIds = response.jsonPath().getList("projects.tasks.id.flatten()");
        System.out.println("Actual Todos IDs: " + actualTodoIds);

        assert(actualTodoIds.containsAll(expectedTodoIds) && expectedTodoIds.containsAll(actualTodoIds));
    }

    @Then("the entries in the relationship todos of the category with id {string} now contain:")
    public void theEntriesInTheRelationshipTodosOfTheCategoryWithIdNowContain(String categoryId, DataTable dataTable) {
        System.out.println("\nRetrieving todos associated with the category with id " + categoryId);

        // Get all the column entries from the data table
        List<String> expectedTodoIds = dataTable.asMaps().stream()
                .map(row -> row.get("id"))
                .toList();
        System.out.println("Expected Todo IDs: " + expectedTodoIds);

        // Make a GET request to retrieve the current todos in the category
        Response response = api.getRequest("/categories/" + categoryId);
        System.out.println("\nObtained the following response body: " + response.getBody().asPrettyString() + "\n");

        List<String> actualTodoIds = response.jsonPath().getList("categories.todos.id.flatten()");
        System.out.println("Actual Todos IDs: " + actualTodoIds);

        assert(actualTodoIds.containsAll(expectedTodoIds) && expectedTodoIds.containsAll(actualTodoIds));
    }

    @Then("the entries in the relationship categories of the todo with id {string} now contain:")
    public void theEntriesInTheRelationshipCategoriesOfTheTodoWithIdNowContain(String todoId, DataTable dataTable) {
        System.out.println("\nRetrieving todos associated with the category with id " + todoId);

        // Get all the column entries from the data table
        List<String> expectedCategoryIds = dataTable.asMaps().stream()
                .map(row -> row.get("id"))
                .toList();
        System.out.println("Expected Category IDs: " + expectedCategoryIds);

        // Make a GET request to retrieve the current categories of the todo
        Response response = api.getRequest("/todos/" + todoId);
        System.out.println("\nObtained the following response body: " + response.getBody().asPrettyString() + "\n");

        List<String> actualCategoryIds = response.jsonPath().getList("todos.categories.id.flatten()");
        System.out.println("Actual Category IDs: " + actualCategoryIds);

        assert(actualCategoryIds.containsAll(expectedCategoryIds) && expectedCategoryIds.containsAll(actualCategoryIds));
    }

    @Then("the entries in the relationship projects of the category with id {string} now contain:")
    public void theEntriesInTheRelationshipProjectsOfTheCategoryWithIdNowContain(String categoryId, DataTable dataTable) {
        System.out.println("\nRetrieving todos associated with the category with id " + categoryId);

        // Get all the column entries from the data table
        List<String> expectedProjectIds = dataTable.asMaps().stream()
                .map(row -> row.get("id"))
                .toList();
        System.out.println("Expected Project IDs: " + expectedProjectIds);

        // Make a GET request to retrieve the current projects of the category
        Response response = api.getRequest("/categories/" + categoryId);
        System.out.println("\nObtained the following response body: " + response.getBody().asPrettyString() + "\n");

        List<String> actualProjectIds = response.jsonPath().getList("categories.projects.id.flatten()");
        System.out.println("Actual Project IDs: " + actualProjectIds);

        assert(actualProjectIds.containsAll(expectedProjectIds) && expectedProjectIds.containsAll(actualProjectIds));
    }

    @Then("an error message {string} is returned")
    public void anErrorMessageIsReturned(String expectedErrorMessage) {
        System.out.println("\nExpected error message: " + expectedErrorMessage);

        Response response = context.getLastResponse();
        System.out.println("\nObtained the following response body: " + response.getBody().asPrettyString() + "\n");
        try {
            List<String> errorMessages = response.jsonPath().getList("errorMessages.flatten()");
            String actualErrorMessage = String.join(", ", errorMessages);
            System.out.println("Actual error message: " + actualErrorMessage);

            assert (actualErrorMessage.equals(expectedErrorMessage));
        } catch (Exception e) {
            throw new RuntimeException("Error message not found in response.");
        }
    }

    @Then("the response status code is {string}")
    public void theResponseStatusCodeIs(String expectedStatusCode) {
        Response response = context.getLastResponse();
        System.out.println("Status code: " + response.getStatusCode());

        assert(response.getStatusCode() == Integer.parseInt(expectedStatusCode));
    }

    @And("the entries in the relationship tasksof of the todo with id {string} contain:")
    public void theEntriesInTheRelationshipTasksofOfTheTodoWithIdContain(String todoId, DataTable dataTable) {
        // Get all the column entries from the data table
        List<String> desiredTasksof = dataTable.asMaps().stream()
                .map(row -> row.get("id"))
                .toList();

        for (String projectId : desiredTasksof) {
            Relationship tasksof = new Relationship(projectId);
            Map<String, Object> payloadMap = tasksof.toPayloadMap();
            String jsonBody = api.toJson(payloadMap);

            Response response = api.postRequest("/todos/" + todoId + "/tasksof", jsonBody);

            if (response.getStatusCode() == 201) {
                // Add the tasksof relationship to the todo
                System.out.println("Associated todo " + todoId + " with project " + projectId + "\n");
            }
            else {
                throw new RuntimeException("Failed to associate todo " + todoId + " with project " + projectId + ". Status code: " + response.getStatusCode());
            }
        }
    }

    @And("the entries in the relationship tasks of the project with id {string} contain:")
    public void theEntriesInTheRelationshipTasksOfTheProjectWithIdContain(String projectId, DataTable dataTable) {
        // Get all the column entries from the data table
        List<String> desiredTasks = dataTable.asMaps().stream()
                .map(row -> row.get("id"))
                .toList();

        for (String todoId : desiredTasks) {
            Relationship tasks = new Relationship(todoId);
            Map<String, Object> payloadMap = tasks.toPayloadMap();
            String jsonBody = api.toJson(payloadMap);

            Response response = api.postRequest("/projects/" + projectId + "/tasks", jsonBody);

            if (response.getStatusCode() == 201) {
                // Add the tasks relationship to the project
                System.out.println("Associated project " + projectId + " with todo " + todoId + "\n");
            }
            else {
                throw new RuntimeException("Failed to associate project " + projectId + " with todo " + todoId + ". Status code: " + response.getStatusCode());
            }
        }
    }

    @And("the entries in the relationship todos of the category with id {string} contain:")
    public void theEntriesInTheRelationshipTodosOfTheCategoryWithIdContain(String categoryId, DataTable dataTable) {
        // Get all the column entries from the data table
        List<String> desiredTodos = dataTable.asMaps().stream()
                .map(row -> row.get("id"))
                .toList();

        for (String todoId : desiredTodos) {
            Relationship todos = new Relationship(todoId);
            Map<String, Object> payloadMap = todos.toPayloadMap();
            String jsonBody = api.toJson(payloadMap);

            Response response = api.postRequest("/categories/" + categoryId + "/todos", jsonBody);

            if (response.getStatusCode() == 201) {
                // Add the todos relationship to the category
                System.out.println("Associated category " + categoryId + " with todo " + todoId + "\n");
            }
            else {
                throw new RuntimeException("Failed to associate category " + categoryId + " with todo " + todoId + ". Status code: " + response.getStatusCode());
            }
        }
    }

    @And("the entries in the relationship projects of the category with id {string} contain:")
    public void theEntriesInTheRelationshipProjectsOfTheCategoryWithIdContain(String categoryId, DataTable dataTable) {
        // Get all the column entries from the data table
        List<String> desiredProjects = dataTable.asMaps().stream()
                .map(row -> row.get("id"))
                .toList();

        for (String projectId : desiredProjects) {
            Relationship projects = new Relationship(projectId);
            Map<String, Object> payloadMap = projects.toPayloadMap();
            String jsonBody = api.toJson(payloadMap);

            Response response = api.postRequest("/categories/" + categoryId + "/projects", jsonBody);

            if (response.getStatusCode() == 201) {
                // Add the projects relationship to the category
                System.out.println("Associated category " + categoryId + " with project " + projectId + "\n");
            }
            else {
                throw new RuntimeException("Failed to associate category " + categoryId + " with project " + projectId + ". Status code: " + response.getStatusCode());
            }
        }
    }
}