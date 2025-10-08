package interoperability;

import static general.CommonConstants.CATEGORIES_ENDPOINT;
import static general.CommonConstants.CATEGORIES_PROJECTS_ENDPOINT;
import static general.CommonConstants.CATEGORIES_TODOS_ENDPOINT;
import static general.CommonConstants.DELETE_METHOD;
import static general.CommonConstants.GET_METHOD;
import static general.CommonConstants.JSON_FORMAT;
import static general.CommonConstants.POST_METHOD;
import static general.CommonConstants.PROJECTS_CATEGORIES_ENDPOINT;
import static general.CommonConstants.PROJECTS_ENDPOINT;
import static general.CommonConstants.PROJECTS_TASKS_ENDPOINT;
import static general.CommonConstants.TODOS_CATEGORIES_ENDPOINT;
import static general.CommonConstants.TODOS_CATEGORIES_ID_ENDPOINT;
import static general.CommonConstants.TODOS_ENDPOINT;
import static general.CommonConstants.TODOS_TASKSOF_ENDPOINT;
import static general.CommonConstants.defaultProject;
import static general.CommonConstants.homeCategory;
import static general.CommonConstants.officeCategory;
import static general.Utils.readResponse;
import static general.Utils.request;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.HttpURLConnection;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.fasterxml.jackson.databind.ObjectMapper;

import categories.Category.CategoryBody;
import categories.JsonCategory;
import general.BaseApiTest;
import projects.JsonProject;
import projects.Project.ProjectBody;

/**
 * Test class for complex cross-relationship interoperability scenarios.
 * 
 * This class tests advanced interoperability scenarios involving multiple
 * entities
 * and relationships simultaneously. It verifies data consistency, bidirectional
 * relationship integrity, and complex workflows that span across different
 * entity types.
 * 
 * Tested scenarios:
 * - Complete interoperability workflows involving todos, projects, and
 * categories
 * - Bidirectional relationship consistency verification
 * - Relationship deletion consistency across all endpoints
 * - Multiple simultaneous relationships per entity
 * 
 * Focus areas:
 * - Data consistency across related endpoints
 * - Proper cleanup and relationship management
 * - Complex relationship scenarios and edge cases
 * - End-to-end interoperability validation
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CrossRelationshipInteropApiTest extends BaseApiTest {

    /* Test comprehensive interoperability scenarios across all entity types */

    /**
     * Tests a complete interoperability workflow across all entity types.
     * This comprehensive test creates todos, projects, and categories, then
     * establishes
     * and verifies all possible relationships between them. Tests the full
     * lifecycle:
     * 1. Create entities (todo, project, category)
     * 2. Establish all relationships (todo-category, todo-project,
     * project-category)
     * 3. Verify relationships exist and are accessible from all endpoints
     * 4. Clean up by removing relationships
     * Expected: All operations succeed with proper HTTP status codes and data
     * consistency.
     */
    @Test
    public void testCompleteInteroperabilityWorkflowJson() throws Exception {
        System.out.println("Running testCompleteInteroperabilityWorkflowJson...");

        ObjectMapper objectMapper = new ObjectMapper();

        // Step 1: Create a new category
        CategoryBody categoryBody = new CategoryBody("Test Category", "Category for interoperability testing");
        String categoryJson = objectMapper.writeValueAsString(categoryBody);

        HttpURLConnection createCategoryConnection = request(CATEGORIES_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                categoryJson);
        String createCategoryResponse = readResponse(createCategoryConnection);
        categories.Category createdCategory = objectMapper.readValue(createCategoryResponse, categories.Category.class);
        createCategoryConnection.disconnect();

        // Step 2: Create a new project
        ProjectBody projectBody = new ProjectBody("Test Project", false, true, "Project for interoperability testing");
        String projectJson = objectMapper.writeValueAsString(projectBody);

        HttpURLConnection createProjectConnection = request(PROJECTS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                projectJson);
        String createProjectResponse = readResponse(createProjectConnection);
        projects.Project createdProject = objectMapper.readValue(createProjectResponse, projects.Project.class);
        createProjectConnection.disconnect();

        // Step 3: Create a new todo
        Todo.TodoBody todoBody = new Todo.TodoBody("Test Todo", false, "Todo for interoperability testing");
        String todoJson = objectMapper.writeValueAsString(todoBody);

        HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                todoJson);
        String createTodoResponse = readResponse(createTodoConnection);
        Todo createdTodo = objectMapper.readValue(createTodoResponse, Todo.class);
        createTodoConnection.disconnect();

        // Step 4: Link todo to category
        JsonRelationship todoCategoryRel = new JsonRelationship(createdCategory.getId());
        String todoCategoryJson = objectMapper.writeValueAsString(todoCategoryRel);

        String todoCategoryEndpoint = String.format(TODOS_CATEGORIES_ENDPOINT, createdTodo.getId());
        HttpURLConnection todoCategoryConnection = request(todoCategoryEndpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                todoCategoryJson);
        assertEquals(201, todoCategoryConnection.getResponseCode());
        todoCategoryConnection.disconnect();

        // Step 5: Link todo to project (tasksof)
        JsonRelationship todoProjectRel = new JsonRelationship(createdProject.getId());
        String todoProjectJson = objectMapper.writeValueAsString(todoProjectRel);

        String todoProjectEndpoint = String.format(TODOS_TASKSOF_ENDPOINT, createdTodo.getId());
        HttpURLConnection todoProjectConnection = request(todoProjectEndpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                todoProjectJson);
        assertEquals(201, todoProjectConnection.getResponseCode());
        todoProjectConnection.disconnect();

        // Step 6: Link project to category
        JsonRelationship projectCategoryRel = new JsonRelationship(createdCategory.getId());
        String projectCategoryJson = objectMapper.writeValueAsString(projectCategoryRel);

        String projectCategoryEndpoint = String.format(PROJECTS_CATEGORIES_ENDPOINT, createdProject.getId());
        HttpURLConnection projectCategoryConnection = request(projectCategoryEndpoint, POST_METHOD, JSON_FORMAT,
                JSON_FORMAT, projectCategoryJson);
        assertEquals(201, projectCategoryConnection.getResponseCode());
        projectCategoryConnection.disconnect();

        // Step 7: Verify relationships from todo perspective
        HttpURLConnection verifyTodoCategoryConnection = request(todoCategoryEndpoint, GET_METHOD, JSON_FORMAT,
                JSON_FORMAT, null);
        String todoCategoryResponse = readResponse(verifyTodoCategoryConnection);
        JsonCategory todoCategories = objectMapper.readValue(todoCategoryResponse, JsonCategory.class);
        assertTrue("Todo should have the created category", todoCategories.contains(createdCategory));
        verifyTodoCategoryConnection.disconnect();

        HttpURLConnection verifyTodoProjectConnection = request(todoProjectEndpoint, GET_METHOD, JSON_FORMAT,
                JSON_FORMAT, null);
        String todoProjectResponse = readResponse(verifyTodoProjectConnection);
        JsonProject todoProjects = objectMapper.readValue(todoProjectResponse, JsonProject.class);
        assertTrue("Todo should have the created project", todoProjects.contains(createdProject));
        verifyTodoProjectConnection.disconnect();

        // Step 8: Verify relationships from project perspective
        HttpURLConnection verifyProjectCategoryConnection = request(projectCategoryEndpoint, GET_METHOD, JSON_FORMAT,
                JSON_FORMAT, null);
        String projectCategoryResponse = readResponse(verifyProjectCategoryConnection);
        JsonCategory projectCategories = objectMapper.readValue(projectCategoryResponse, JsonCategory.class);
        assertTrue("Project should have the created category", projectCategories.contains(createdCategory));
        verifyProjectCategoryConnection.disconnect();

        String projectTasksEndpoint = String.format(PROJECTS_TASKS_ENDPOINT, createdProject.getId());
        HttpURLConnection verifyProjectTasksConnection = request(projectTasksEndpoint, GET_METHOD, JSON_FORMAT,
                JSON_FORMAT, null);
        String projectTasksResponse = readResponse(verifyProjectTasksConnection);
        JsonTodo projectTodos = objectMapper.readValue(projectTasksResponse, JsonTodo.class);
        assertTrue("Project should have the created todo", projectTodos.contains(createdTodo));
        verifyProjectTasksConnection.disconnect();

        // Step 9: Verify relationships from category perspective
        String categoryProjectsEndpoint = String.format(CATEGORIES_PROJECTS_ENDPOINT, createdCategory.getId());
        HttpURLConnection verifyCategoryProjectsConnection = request(categoryProjectsEndpoint, GET_METHOD, JSON_FORMAT,
                JSON_FORMAT, null);
        String categoryProjectsResponse = readResponse(verifyCategoryProjectsConnection);
        JsonProject categoryProjects = objectMapper.readValue(categoryProjectsResponse, JsonProject.class);
        assertTrue("Category should have the created project", categoryProjects.contains(createdProject));
        verifyCategoryProjectsConnection.disconnect();

        String categoryTodosEndpoint = String.format(CATEGORIES_TODOS_ENDPOINT, createdCategory.getId());
        HttpURLConnection verifyCategoryTodosConnection = request(categoryTodosEndpoint, GET_METHOD, JSON_FORMAT,
                JSON_FORMAT, null);
        String categoryTodosResponse = readResponse(verifyCategoryTodosConnection);
        JsonTodo categoryTodos = objectMapper.readValue(categoryTodosResponse, JsonTodo.class);
        assertTrue("Category should have the created todo", categoryTodos.contains(createdTodo));
        verifyCategoryTodosConnection.disconnect();

        System.out.println("testCompleteInteroperabilityWorkflowJson passed.");
    }

    /**
     * Tests bidirectional relationship consistency across endpoints.
     * This test verifies that when a relationship is created between two entities,
     * it appears consistently when accessed from both endpoints. For example,
     * when a todo-category relationship is created, it should be visible both from
     * /todos/{id}/categories and /categories/{id}/todos endpoints.
     * Expected: Relationships are consistent and visible from both directions.
     */
    @Test
    public void testBidirectionalRelationshipConsistencyJson() throws Exception {
        System.out.println("Running testBidirectionalRelationshipConsistencyJson...");

        ObjectMapper objectMapper = new ObjectMapper();

        // Create entities
        Todo.TodoBody todoBody = new Todo.TodoBody("Consistency Test Todo", false, "Consistency testing");
        String todoJson = objectMapper.writeValueAsString(todoBody);

        HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                todoJson);
        String createTodoResponse = readResponse(createTodoConnection);
        Todo createdTodo = objectMapper.readValue(createTodoResponse, Todo.class);
        createTodoConnection.disconnect();

        ProjectBody projectBody = new ProjectBody("Consistency Test Project", false, true, "Consistency testing");
        String projectJson = objectMapper.writeValueAsString(projectBody);

        HttpURLConnection createProjectConnection = request(PROJECTS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                projectJson);
        String createProjectResponse = readResponse(createProjectConnection);
        projects.Project createdProject = objectMapper.readValue(createProjectResponse, projects.Project.class);
        createProjectConnection.disconnect();

        // Create relationship from todo side (todo -> project via tasksof)
        JsonRelationship relationshipBody = new JsonRelationship(createdProject.getId());
        String relationshipJson = objectMapper.writeValueAsString(relationshipBody);

        String todoTasksofEndpoint = String.format(TODOS_TASKSOF_ENDPOINT, createdTodo.getId());
        HttpURLConnection createRelationshipConnection = request(todoTasksofEndpoint, POST_METHOD, JSON_FORMAT,
                JSON_FORMAT, relationshipJson);
        assertEquals(201, createRelationshipConnection.getResponseCode());
        createRelationshipConnection.disconnect();

        // Verify the relationship exists from project side (project -> todo via tasks)
        String projectTasksEndpoint = String.format(PROJECTS_TASKS_ENDPOINT, createdProject.getId());
        HttpURLConnection verifyFromProjectConnection = request(projectTasksEndpoint, GET_METHOD, JSON_FORMAT,
                JSON_FORMAT, null);
        String projectTasksResponse = readResponse(verifyFromProjectConnection);
        JsonTodo projectTodos = objectMapper.readValue(projectTasksResponse, JsonTodo.class);
        assertTrue("Bidirectional consistency: Project should show the todo in tasks",
                projectTodos.contains(createdTodo));
        verifyFromProjectConnection.disconnect();

        // Verify the relationship still exists from todo side
        HttpURLConnection verifyFromTodoConnection = request(todoTasksofEndpoint, GET_METHOD, JSON_FORMAT, JSON_FORMAT,
                null);
        String todoTasksofResponse = readResponse(verifyFromTodoConnection);
        JsonProject todoProjects = objectMapper.readValue(todoTasksofResponse, JsonProject.class);
        assertTrue("Bidirectional consistency: Todo should show the project in tasksof",
                todoProjects.contains(createdProject));
        verifyFromTodoConnection.disconnect();

        System.out.println("testBidirectionalRelationshipConsistencyJson passed.");
    }

    /**
     * Tests relationship deletion consistency across all endpoints.
     * This test verifies that when a relationship is deleted from one endpoint,
     * it is properly removed and no longer appears when accessed from any related
     * endpoints. Creates relationships, deletes them, then verifies they are gone
     * from all possible access points to ensure data consistency.
     * Expected: Deleted relationships disappear from all related endpoints
     * consistently.
     */
    @Test
    public void testRelationshipDeletionConsistencyJson() throws Exception {
        System.out.println("Running testRelationshipDeletionConsistencyJson...");

        ObjectMapper objectMapper = new ObjectMapper();

        // Create entities and relationships
        Todo.TodoBody todoBody = new Todo.TodoBody("Deletion Test Todo", false, "Deletion testing");
        String todoJson = objectMapper.writeValueAsString(todoBody);

        HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                todoJson);
        String createTodoResponse = readResponse(createTodoConnection);
        Todo createdTodo = objectMapper.readValue(createTodoResponse, Todo.class);
        createTodoConnection.disconnect();

        // Create relationship with existing category (id "1")
        JsonRelationship relationshipBody = new JsonRelationship("1");
        String relationshipJson = objectMapper.writeValueAsString(relationshipBody);

        String todoCategoriesEndpoint = String.format(TODOS_CATEGORIES_ENDPOINT, createdTodo.getId());
        HttpURLConnection createRelationshipConnection = request(todoCategoriesEndpoint, POST_METHOD, JSON_FORMAT,
                JSON_FORMAT, relationshipJson);
        assertEquals(201, createRelationshipConnection.getResponseCode());
        createRelationshipConnection.disconnect();

        // Verify relationship exists
        HttpURLConnection verifyBeforeConnection = request(todoCategoriesEndpoint, GET_METHOD, JSON_FORMAT, JSON_FORMAT,
                null);
        String beforeResponse = readResponse(verifyBeforeConnection);
        JsonCategory beforeCategories = objectMapper.readValue(beforeResponse, JsonCategory.class);
        assertTrue("Relationship should exist before deletion", beforeCategories.size() > 0);
        verifyBeforeConnection.disconnect();

        // Delete the relationship
        String deleteEndpoint = String.format(TODOS_CATEGORIES_ID_ENDPOINT, createdTodo.getId(), "1");
        HttpURLConnection deleteConnection = request(deleteEndpoint, DELETE_METHOD, JSON_FORMAT, JSON_FORMAT, null);
        assertEquals(200, deleteConnection.getResponseCode());
        deleteConnection.disconnect();

        // Verify relationship is deleted from todo side
        HttpURLConnection verifyAfterConnection = request(todoCategoriesEndpoint, GET_METHOD, JSON_FORMAT, JSON_FORMAT,
                null);
        String afterResponse = readResponse(verifyAfterConnection);
        JsonCategory afterCategories = objectMapper.readValue(afterResponse, JsonCategory.class);
        assertFalse("Relationship should not exist after deletion", afterCategories.contains(officeCategory));
        verifyAfterConnection.disconnect();

        // Verify relationship is also deleted from category side
        String categoryTodosEndpoint = String.format(CATEGORIES_TODOS_ENDPOINT, "1");
        HttpURLConnection verifyCategorySideConnection = request(categoryTodosEndpoint, GET_METHOD, JSON_FORMAT,
                JSON_FORMAT, null);
        String categorySideResponse = readResponse(verifyCategorySideConnection);
        JsonTodo categoryTodos = objectMapper.readValue(categorySideResponse, JsonTodo.class);
        assertFalse("Bidirectional deletion: Category should not show the deleted todo",
                categoryTodos.contains(createdTodo));
        verifyCategorySideConnection.disconnect();

        System.out.println("testRelationshipDeletionConsistencyJson passed.");
    }

    /**
     * Tests multiple relationships per entity across different relationship types.
     * This test verifies that a single entity can maintain multiple relationships
     * of different types simultaneously. Creates a todo and establishes
     * relationships
     * with multiple categories and projects to ensure the API can handle complex
     * relationship scenarios without conflicts.
     * Expected: Single entity can have multiple relationships of different types
     * simultaneously.
     */
    @Test
    public void testMultipleRelationshipsPerEntityJson() throws Exception {
        System.out.println("Running testMultipleRelationshipsPerEntityJson...");

        ObjectMapper objectMapper = new ObjectMapper();

        // Create a todo
        Todo.TodoBody todoBody = new Todo.TodoBody("Multi Rel Todo", false, "Multiple relationships test");
        String todoJson = objectMapper.writeValueAsString(todoBody);

        HttpURLConnection createTodoConnection = request(TODOS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                todoJson);
        String createTodoResponse = readResponse(createTodoConnection);
        Todo createdTodo = objectMapper.readValue(createTodoResponse, Todo.class);
        createTodoConnection.disconnect();

        // Create relationships with both default categories (Office and Home)
        JsonRelationship officeCategoryRel = new JsonRelationship("1");
        JsonRelationship homeCategoryRel = new JsonRelationship("2");

        String todoCategoriesEndpoint = String.format(TODOS_CATEGORIES_ENDPOINT, createdTodo.getId());

        // Add Office category relationship
        String officeCategoryJson = objectMapper.writeValueAsString(officeCategoryRel);
        HttpURLConnection officeRelConnection = request(todoCategoriesEndpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                officeCategoryJson);
        assertEquals(201, officeRelConnection.getResponseCode());
        officeRelConnection.disconnect();

        // Add Home category relationship
        String homeCategoryJson = objectMapper.writeValueAsString(homeCategoryRel);
        HttpURLConnection homeRelConnection = request(todoCategoriesEndpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                homeCategoryJson);
        assertEquals(201, homeRelConnection.getResponseCode());
        homeRelConnection.disconnect();

        // Add relationship with default project
        JsonRelationship projectRel = new JsonRelationship("1");
        String projectJson = objectMapper.writeValueAsString(projectRel);

        String todoTasksofEndpoint = String.format(TODOS_TASKSOF_ENDPOINT, createdTodo.getId());
        HttpURLConnection projectRelConnection = request(todoTasksofEndpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                projectJson);
        assertEquals(201, projectRelConnection.getResponseCode());
        projectRelConnection.disconnect();

        // Verify todo has multiple categories
        HttpURLConnection verifyCategoriesConnection = request(todoCategoriesEndpoint, GET_METHOD, JSON_FORMAT,
                JSON_FORMAT, null);
        String categoriesResponse = readResponse(verifyCategoriesConnection);
        JsonCategory todoCategories = objectMapper.readValue(categoriesResponse, JsonCategory.class);
        assertTrue("Todo should have Office category", todoCategories.contains(officeCategory));
        assertTrue("Todo should have Home category", todoCategories.contains(homeCategory));
        assertEquals("Todo should have exactly 2 categories", 2, todoCategories.size());
        verifyCategoriesConnection.disconnect();

        // Verify todo has project relationship
        HttpURLConnection verifyProjectConnection = request(todoTasksofEndpoint, GET_METHOD, JSON_FORMAT, JSON_FORMAT,
                null);
        String projectResponse = readResponse(verifyProjectConnection);
        JsonProject todoProjects = objectMapper.readValue(projectResponse, JsonProject.class);
        assertTrue("Todo should have default project", todoProjects.contains(defaultProject));
        verifyProjectConnection.disconnect();

        System.out.println("testMultipleRelationshipsPerEntityJson passed.");
    }
}