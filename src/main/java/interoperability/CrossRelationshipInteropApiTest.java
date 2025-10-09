package interoperability;

import static general.CommonConstants.CATEGORIES_ENDPOINT;
import static general.CommonConstants.CATEGORIES_TODOS_ENDPOINT;
import static general.CommonConstants.DELETE_METHOD;
import static general.CommonConstants.GET_METHOD;
import static general.CommonConstants.JSON_FORMAT;
import static general.CommonConstants.POST_METHOD;
import static general.CommonConstants.PROJECTS_ENDPOINT;
import static general.CommonConstants.PROJECTS_TASKS_ENDPOINT;
import static general.CommonConstants.TODOS_CATEGORIES_ENDPOINT;
import static general.CommonConstants.TODOS_CATEGORIES_ID_ENDPOINT;
import static general.CommonConstants.TODOS_ENDPOINT;
import static general.CommonConstants.TODOS_TASKSOF_ENDPOINT;
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
import interoperability.models.JsonRelationship;
import interoperability.models.JsonTodo;
import interoperability.models.Todo;
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

                ProjectBody projectBody = new ProjectBody("Consistency Test Project", false, true,
                                "Consistency testing");
                String projectJson = objectMapper.writeValueAsString(projectBody);

                HttpURLConnection createProjectConnection = request(PROJECTS_ENDPOINT, POST_METHOD, JSON_FORMAT,
                                JSON_FORMAT,
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
                HttpURLConnection verifyFromTodoConnection = request(todoTasksofEndpoint, GET_METHOD, JSON_FORMAT,
                                JSON_FORMAT,
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
                assertEquals(201, createTodoConnection.getResponseCode());
                assertEquals("Created", createTodoConnection.getResponseMessage());
                createTodoConnection.disconnect();

                // Create a category to link with
                CategoryBody categoryBody = new CategoryBody("Deletion Test Category",
                                "Category for deletion testing");
                String categoryJson = objectMapper.writeValueAsString(categoryBody);
                HttpURLConnection createCategoryConnection = request(CATEGORIES_ENDPOINT, POST_METHOD, JSON_FORMAT,
                                JSON_FORMAT,
                                categoryJson);
                String createCategoryResponse = readResponse(createCategoryConnection);
                categories.Category createdCategory = objectMapper.readValue(createCategoryResponse,
                                categories.Category.class);
                assertEquals(201, createCategoryConnection.getResponseCode());
                assertEquals("Created", createCategoryConnection.getResponseMessage());
                createCategoryConnection.disconnect();

                // Create relationship with existing category (id "1")
                JsonRelationship relationshipBody = new JsonRelationship(createdCategory.getId());
                String relationshipJson = objectMapper.writeValueAsString(relationshipBody);

                String todoCategoriesEndpoint = String.format(TODOS_CATEGORIES_ENDPOINT, createdTodo.getId());
                HttpURLConnection createRelationshipConnection = request(todoCategoriesEndpoint, POST_METHOD,
                                JSON_FORMAT,
                                JSON_FORMAT, relationshipJson);
                assertEquals(201, createRelationshipConnection.getResponseCode());
                createRelationshipConnection.disconnect();

                // Verify relationship exists
                HttpURLConnection verifyBeforeConnection = request(todoCategoriesEndpoint, GET_METHOD, JSON_FORMAT,
                                JSON_FORMAT,
                                null);
                String beforeResponse = readResponse(verifyBeforeConnection);
                JsonCategory beforeCategories = objectMapper.readValue(beforeResponse, JsonCategory.class);
                assertEquals(200, verifyBeforeConnection.getResponseCode());

                assertTrue("Relationship should exist before deletion", beforeCategories.size() > 0);
                verifyBeforeConnection.disconnect();

                // Delete the relationship
                String deleteEndpoint = String.format(TODOS_CATEGORIES_ID_ENDPOINT, createdTodo.getId(),
                                createdCategory.getId());
                HttpURLConnection deleteConnection = request(deleteEndpoint, DELETE_METHOD, JSON_FORMAT, JSON_FORMAT,
                                null);
                assertEquals(200, deleteConnection.getResponseCode());
                deleteConnection.disconnect();

                // Verify relationship is deleted from todo side
                HttpURLConnection verifyAfterConnection = request(todoCategoriesEndpoint, GET_METHOD, JSON_FORMAT,
                                JSON_FORMAT,
                                null);
                String afterResponse = readResponse(verifyAfterConnection);
                JsonCategory afterCategories = objectMapper.readValue(afterResponse, JsonCategory.class);
                assertFalse("Relationship should not exist after deletion", afterCategories.contains(officeCategory));
                verifyAfterConnection.disconnect();

                // Verify relationship is also deleted from category side
                String categoryTodosEndpoint = String.format(CATEGORIES_TODOS_ENDPOINT, createdCategory.getId());
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
                assertEquals(201, createTodoConnection.getResponseCode());
                assertEquals("Created", createTodoConnection.getResponseMessage());
                createTodoConnection.disconnect();

                // Create a category to link with
                CategoryBody categoryBody = new CategoryBody("Office", "Office related tasks");
                String categoryJson = objectMapper.writeValueAsString(categoryBody);
                HttpURLConnection createCategoryConnection = request(CATEGORIES_ENDPOINT, POST_METHOD, JSON_FORMAT,
                                JSON_FORMAT,
                                categoryJson);
                String createCategoryResponse = readResponse(createCategoryConnection);
                categories.Category createdCategory = objectMapper.readValue(createCategoryResponse,
                                categories.Category.class);
                assertEquals(201, createCategoryConnection.getResponseCode());
                assertEquals("Created", createCategoryConnection.getResponseMessage());
                createCategoryConnection.disconnect();

                // Create another category to link with
                CategoryBody categoryBody2 = new CategoryBody("Home", "Home related tasks");
                String categoryJson2 = objectMapper.writeValueAsString(categoryBody2);
                HttpURLConnection createCategoryConnection2 = request(CATEGORIES_ENDPOINT, POST_METHOD, JSON_FORMAT,
                                JSON_FORMAT,
                                categoryJson2);
                String createCategoryResponse2 = readResponse(createCategoryConnection2);
                categories.Category createdCategory2 = objectMapper.readValue(createCategoryResponse2,
                                categories.Category.class);
                assertEquals(201, createCategoryConnection2.getResponseCode());
                assertEquals("Created", createCategoryConnection2.getResponseMessage());
                createCategoryConnection2.disconnect();

                // Create relationships with both default categories (Office and Home)
                JsonRelationship officeCategoryRel = new JsonRelationship(createdCategory.getId());
                JsonRelationship homeCategoryRel = new JsonRelationship(createdCategory2.getId());

                String todoCategoriesEndpoint = String.format(TODOS_CATEGORIES_ENDPOINT, createdTodo.getId());

                // Add Office category relationship
                String officeCategoryJson = objectMapper.writeValueAsString(officeCategoryRel);
                HttpURLConnection officeRelConnection = request(todoCategoriesEndpoint, POST_METHOD, JSON_FORMAT,
                                JSON_FORMAT,
                                officeCategoryJson);
                assertEquals(201, officeRelConnection.getResponseCode());
                assertEquals("Created", officeRelConnection.getResponseMessage());
                officeRelConnection.disconnect();

                // Add Home category relationship
                String homeCategoryJson = objectMapper.writeValueAsString(homeCategoryRel);
                HttpURLConnection homeRelConnection = request(todoCategoriesEndpoint, POST_METHOD, JSON_FORMAT,
                                JSON_FORMAT,
                                homeCategoryJson);
                assertEquals(201, homeRelConnection.getResponseCode());
                assertEquals("Created", homeRelConnection.getResponseMessage());
                homeRelConnection.disconnect();

                // Verify todo has multiple categories
                HttpURLConnection verifyCategoriesConnection = request(todoCategoriesEndpoint, GET_METHOD, JSON_FORMAT,
                                JSON_FORMAT, null);
                String categoriesResponse = readResponse(verifyCategoriesConnection);
                JsonCategory todoCategories = objectMapper.readValue(categoriesResponse, JsonCategory.class);
                // Check titles directly to avoid relying on toString formatting
                categories.Category[] categoriesArr = todoCategories.getCategories();
                boolean foundOffice = false;
                boolean foundHome = false;
                for (categories.Category c : categoriesArr) {
                        if (c.getTitle() != null && c.getTitle().equalsIgnoreCase("Office")) {
                                foundOffice = true;
                        }
                        if (c.getTitle() != null && c.getTitle().equalsIgnoreCase("Home")) {
                                foundHome = true;
                        }
                }
                assertTrue("Todo should have Office category", foundOffice);
                assertTrue("Todo should have Home category", foundHome);
                assertEquals("Todo should have exactly 2 categories", 2, todoCategories.size());
                verifyCategoriesConnection.disconnect();

                System.out.println("testMultipleRelationshipsPerEntityJson passed.");
        }
}