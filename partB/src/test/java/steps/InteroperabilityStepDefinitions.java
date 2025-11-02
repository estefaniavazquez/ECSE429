package steps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import api.Api;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import models.CategoryInteroperability;
import models.ProjectInteroperability;
import models.Relationship;
import models.TodoInteroperability;
import setup.ScenarioContext;

public class InteroperabilityStepDefinitions {
    private final ScenarioContext context;
    private final Api api;

    public InteroperabilityStepDefinitions(ScenarioContext context, Api api) {
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
                Response response = api.deleteRequest("/todos/" + todo.getId());

                assert (response.getStatusCode() == 200);
            }
        }

        // Get all projects
        Response projectsResponse = api.getRequest("/projects");
        ProjectInteroperability[] projects = projectsResponse.jsonPath().getObject("projects",
                ProjectInteroperability[].class);

        // Delete each project
        if (projects != null) {
            for (ProjectInteroperability project : projects) {
                Response response = api.deleteRequest("/projects/" + project.getId());

                assert (response.getStatusCode() == 200);
            }
        }

        // Get all categories
        Response categoriesResponse = api.getRequest("/categories");
        CategoryInteroperability[] categories = categoriesResponse.jsonPath().getObject("categories",
                models.CategoryInteroperability[].class);

        // Delete each category
        if (categories != null) {
            for (CategoryInteroperability category : categories) {
                Response response = api.deleteRequest("/categories/" + category.getId());

                assert (response.getStatusCode() == 200);
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
            String newId = response.jsonPath().getString("id");

            assert (response.getStatusCode() == 201);
            assert (id.equals(newId));
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
            String newId = response.jsonPath().getString("id");

            assert (response.getStatusCode() == 201);
            assert (id.equals(newId));
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
        String newId = response.jsonPath().getString("id");

        assert (response.getStatusCode() == 201);
        assert (id.equals(newId));
    }

    @When("the user associates the todo with id {string} with the project with id {string}")
    public void theUserAssociatesTheTodoWithIdWithTheProjectWithId(String todoId, String projectId) {
        Relationship tasksof = new Relationship(projectId);
        Map<String, Object> payloadMap = tasksof.toPayloadMap();
        String jsonBody = api.toJson(payloadMap);

        Response response = api.postRequest("/todos/" + todoId + "/tasksof", jsonBody);
        context.setLastResponse(response);
    }

    @When("the user removes the association between the todo with id {string} and the project with id {string}")
    public void theUserRemovesTheAssociationBetweenTheTodoWithIdAndTheProjectWithId(String todoId, String projectId) {
        Response response = api.deleteRequest("/todos/" + todoId + "/tasksof/" + projectId);
        context.setLastResponse(response);
    }

    @When("the user deletes the todo with id {string}")
    public void theUserDeletesTheTodoWithId(String todoId) {
        Response response = api.deleteRequest("/todos/" + todoId);
        context.setLastResponse(response);
    }

    @When("the user gets the todos associated with the project with id {string}")
    public void theUserGetsTheTodosAssociatedWithTheProjectWithId(String projectId) {
        Response response = api.getRequest("/projects/" + projectId + "/tasks");
        context.setLastResponse(response);
    }

    @When("the user assigns the category with id {string} to the todo with id {string}")
    public void theUserAssignsTheCategoryWithIdToTheTodoWithId(String categoryId, String todoId) {
        Relationship todo = new Relationship(todoId);
        Map<String, Object> payloadMap = todo.toPayloadMap();
        String jsonBody = api.toJson(payloadMap);

        Response response = api.postRequest("/categories/" + categoryId + "/todos", jsonBody);
        context.setLastResponse(response);
    }

    @When("the user assigns the category with id {string} to the project with id {string}")
    public void theUserAssignsTheCategoryWithIdToTheProjectWithId(String categoryId, String projectId) {
        Relationship projects = new Relationship(projectId);
        Map<String, Object> payloadMap = projects.toPayloadMap();
        String jsonBody = api.toJson(payloadMap);

        Response response = api.postRequest("/categories/" + categoryId + "/projects", jsonBody);
        context.setLastResponse(response);
    }

    @Then("no instance of a todo with id {string} exists")
    public void noInstanceOfATodoWithIdExists(String todoId) {
        Response response = api.getRequest("/todos/" + todoId);

        // Trying to get a non-existing todo should return 404
        assert (response.getStatusCode() == 404);
    }

    @Then("the returned todos are:")
    public void theReturnedTodosAre(DataTable dataTable) {
        TodoInteroperability[] expectedTodos = dataTable.asMaps().stream()
                .map(row -> {
                    String id = row.get("todoId");
                    String title = row.get("todoTitle");
                    String description = row.get("todoDescription");
                    String doneStatus = row.get("todoDoneStatus");
                    String tasksofId = row.get("tasksofId");
                    List<Relationship> tasksof = new ArrayList<>();
                    if (tasksofId != null && !tasksofId.isEmpty()) {
                        tasksof.add(new Relationship(tasksofId));
                    }
                    return new TodoInteroperability(id, title, description, doneStatus, tasksof, null);
                })
                .toArray(TodoInteroperability[]::new);

        // Make a GET request to retrieve the current todos in the project
        Response response = context.getLastResponse();
        TodoInteroperability[] actualTodos = response.jsonPath().getObject("todos", TodoInteroperability[].class);

        // Ensure both objects are equal
        assert (actualTodos.length == expectedTodos.length);
        for (TodoInteroperability expectedTodo : expectedTodos) {
            boolean matchFound = false;
            for (TodoInteroperability actualTodo : actualTodos) {
                if (actualTodo.equals(expectedTodo)) {
                    matchFound = true;
                    break;
                }
            }
            assert (matchFound);
        }
    }

    @Then("the returned todos is empty")
    public void theReturnedTodosIsEmpty() {
        // Make a GET request to retrieve the current todos in the project
        Response response = context.getLastResponse();
        List<String> actualTodoIds = response.jsonPath().getList("todos.id.flatten()");

        assert (actualTodoIds.isEmpty());
    }

    @Then("the entries in the relationship tasksof of the todo with id {string} now contain:")
    public void theEntriesInTheRelationshipTasksofOfTheTodoWithIdNowContain(String todoId, DataTable dataTable) {
        // Get all the column entries from the data table
        List<String> expectedProjectIds = dataTable.asMaps().stream()
                .map(row -> row.get("id"))
                .toList();

        // Make a GET request to retrieve the current tasksof relationships of the todo
        Response response = api.getRequest("/todos/" + todoId);
        List<String> actualProjectIds = response.jsonPath().getList("todos.tasksof.id.flatten()");

        assert (actualProjectIds.containsAll(expectedProjectIds) && expectedProjectIds.containsAll(actualProjectIds));
    }

    @Then("the entries in the relationship tasks of the project with id {string} now contain:")
    public void theEntriesInTheRelationshipTasksOfTheProjectWithIdNowContain(String projectId, DataTable dataTable) {
        // Get all the column entries from the data table
        List<String> expectedTodoIds = dataTable.asMaps().stream()
                .map(row -> row.get("id"))
                .toList();

        // Make a GET request to retrieve the current tasks in the project
        Response response = api.getRequest("/projects/" + projectId);
        List<String> actualTodoIds = response.jsonPath().getList("projects.tasks.id.flatten()");

        assert (actualTodoIds.containsAll(expectedTodoIds) && expectedTodoIds.containsAll(actualTodoIds));
    }

    @Then("the entries in the relationship todos of the category with id {string} now contain:")
    public void theEntriesInTheRelationshipTodosOfTheCategoryWithIdNowContain(String categoryId, DataTable dataTable) {
        // Get all the column entries from the data table
        List<String> expectedTodoIds = dataTable.asMaps().stream()
                .map(row -> row.get("id"))
                .toList();

        // Make a GET request to retrieve the current todos in the category
        Response response = api.getRequest("/categories/" + categoryId);
        List<String> actualTodoIds = response.jsonPath().getList("categories.todos.id.flatten()");

        assert (actualTodoIds.containsAll(expectedTodoIds) && expectedTodoIds.containsAll(actualTodoIds));
    }

    @Then("the entries in the relationship categories of the todo with id {string} now contain:")
    public void theEntriesInTheRelationshipCategoriesOfTheTodoWithIdNowContain(String todoId, DataTable dataTable) {
        // Get all the column entries from the data table
        List<String> expectedCategoryIds = dataTable.asMaps().stream()
                .map(row -> row.get("id"))
                .toList();

        // Make a GET request to retrieve the current categories of the todo
        Response response = api.getRequest("/todos/" + todoId);
        List<String> actualCategoryIds = response.jsonPath().getList("todos.categories.id.flatten()");

        assert (actualCategoryIds.containsAll(expectedCategoryIds)
                && expectedCategoryIds.containsAll(actualCategoryIds));
    }

    @Then("the entries in the relationship projects of the category with id {string} now contain:")
    public void theEntriesInTheRelationshipProjectsOfTheCategoryWithIdNowContain(String categoryId,
            DataTable dataTable) {
        // Get all the column entries from the data table
        List<String> expectedProjectIds = dataTable.asMaps().stream()
                .map(row -> row.get("id"))
                .toList();

        // Make a GET request to retrieve the current projects of the category
        Response response = api.getRequest("/categories/" + categoryId);
        List<String> actualProjectIds = response.jsonPath().getList("categories.projects.id.flatten()");

        assert (actualProjectIds.containsAll(expectedProjectIds) && expectedProjectIds.containsAll(actualProjectIds));
    }

    // REFACTORED TO CommonStepDefinitions - using consolidated error message
    // verification
    // Now handled by: the_system_should_tell_me_if_there_was_an_error() in
    // CommonStepDefinitions
    // which also supports the "an error message {string} is returned" step phrase
    // @Then("an error message {string} is returned")
    // public void anErrorMessageIsReturned(String expectedErrorMessage) {
    // Response response = context.getLastResponse();
    // try {
    // List<String> errorMessages =
    // response.jsonPath().getList("errorMessages.flatten()");
    // String actualErrorMessage = String.join(", ", errorMessages);
    // assert(actualErrorMessage.equals(expectedErrorMessage));
    // } catch (Exception e) {
    // System.out.println("Failed to retrieve error message from response.");
    // assert(false);
    // }
    // }

    // REFACTORED TO CommonStepDefinitions
    // @Then("the response status code is {string}")
    // public void theResponseStatusCodeIs(String expectedStatusCode) {
    // Response response = context.getLastResponse();

    // assert(response.getStatusCode() == Integer.parseInt(expectedStatusCode));
    // }

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

            if (response.getStatusCode() != 201) {
                throw new RuntimeException("Failed to associate todo " + todoId + " with project " + projectId
                        + ". Status code: " + response.getStatusCode());
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

            assert (response.getStatusCode() == 201);
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

            assert (response.getStatusCode() == 201);
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

            assert (response.getStatusCode() == 201);
        }
    }
}