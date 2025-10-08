package interoperability;

import static general.CommonConstants.BASE_URL;
import static general.CommonConstants.CATEGORIES_ENDPOINT;
import static general.CommonConstants.DELETE_METHOD;
import static general.CommonConstants.GET_METHOD;
import static general.CommonConstants.JSON_FORMAT;
import static general.CommonConstants.POST_METHOD;
import static general.CommonConstants.PROJECTS_CATEGORIES_ENDPOINT;
import static general.CommonConstants.PROJECTS_CATEGORIES_ID_ENDPOINT;
import static general.CommonConstants.PROJECTS_ENDPOINT;
import static general.CommonConstants.PROJECTS_TASKS_ENDPOINT;
import static general.CommonConstants.PROJECTS_TASKS_ID_ENDPOINT;
import static general.CommonConstants.PUT_METHOD;
import static general.CommonConstants.TODOS_CATEGORIES_ENDPOINT;
import static general.CommonConstants.TODOS_CATEGORIES_ID_ENDPOINT;
import static general.CommonConstants.TODOS_ENDPOINT;
import static general.CommonConstants.XML_FORMAT;
import static general.Utils.readResponse;
import static general.Utils.request;
import static general.Utils.requestWithId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import categories.Category;
import categories.JsonCategory;
import categories.XmlCategory;
import general.BaseApiTest;
import projects.JsonProject;
import projects.Project;
import projects.XmlProject;

/**
 * Comprehensive behavioural coverage of the REST API mirroring the Python
 * suite.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ComprehensiveApiBehaviourTest extends BaseApiTest {

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static final XmlMapper XML_MAPPER = new XmlMapper();
    private static final DocumentBuilderFactory DOCUMENT_FACTORY = DocumentBuilderFactory.newInstance();

    private final Set<String> projectsToDelete = Collections.synchronizedSet(new LinkedHashSet<>());
    private final Set<String> todosToDelete = Collections.synchronizedSet(new LinkedHashSet<>());
    private final Set<String> categoriesToDelete = Collections.synchronizedSet(new LinkedHashSet<>());
    private final Set<RelationshipRef> relationshipsToDelete = Collections
            .synchronizedSet(new LinkedHashSet<>());

    private static final class ApiResponse {
        final int code;
        final String body;

        ApiResponse(int code, String body) {
            this.code = code;
            this.body = body != null ? body : "";
        }
    }

    private static final class RelationshipRef {
        private final String template;
        private final String parentId;
        private final String childId;

        RelationshipRef(String template, String parentId, String childId) {
            this.template = template;
            this.parentId = parentId;
            this.childId = childId;
        }

        String endpoint() {
            return String.format(template, parentId, childId);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof RelationshipRef))
                return false;
            RelationshipRef that = (RelationshipRef) o;
            return template.equals(that.template) && parentId.equals(that.parentId)
                    && childId.equals(that.childId);
        }

        @Override
        public int hashCode() {
            int result = template.hashCode();
            result = 31 * result + parentId.hashCode();
            result = 31 * result + childId.hashCode();
            return result;
        }
    }

    @Before
    public void verifyServerHealth() throws Exception {
        ApiResponse health = execute(request(PROJECTS_ENDPOINT, GET_METHOD, JSON_FORMAT, JSON_FORMAT, null));
        assertEquals("Server health check failed", 200, health.code);
    }

    @After
    public void cleanupResources() {
        for (RelationshipRef ref : new ArrayList<>(relationshipsToDelete)) {
            deleteRelationshipQuietly(ref);
        }
        relationshipsToDelete.clear();

        for (String todoId : new ArrayList<>(todosToDelete)) {
            deleteTodoQuietly(todoId);
        }
        todosToDelete.clear();

        for (String categoryId : new ArrayList<>(categoriesToDelete)) {
            deleteCategoryQuietly(categoryId);
        }
        categoriesToDelete.clear();

        for (String projectId : new ArrayList<>(projectsToDelete)) {
            deleteProjectQuietly(projectId);
        }
        projectsToDelete.clear();
    }

    private ApiResponse execute(HttpURLConnection connection) throws Exception {
        try {
            int code = connection.getResponseCode();
            String body = readResponse(connection);
            return new ApiResponse(code, body);
        } finally {
            connection.disconnect();
        }
    }

    private String unique(String prefix) {
        return prefix + UUID.randomUUID();
    }

    private Project createProjectResource(String title, String description) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("title", title);
        if (description != null) {
            body.put("description", description);
        }
        String payload = JSON_MAPPER.writeValueAsString(body);
        ApiResponse response = execute(request(PROJECTS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT, payload));
        assertEquals(201, response.code);
        Project project = parseProjectBody(response.body);
        assertNotNull("Project payload should deserialize", project);
        projectsToDelete.add(project.getId());
        return project;
    }

    private Todo createTodoResource(String title, String description) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("title", title);
        if (description != null) {
            body.put("description", description);
        }
        body.put("doneStatus", false);
        String payload = JSON_MAPPER.writeValueAsString(body);
        ApiResponse response = execute(request(TODOS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT, payload));
        assertEquals(201, response.code);
        Todo todo = JSON_MAPPER.readValue(response.body, Todo.class);
        todosToDelete.add(todo.getId());
        return todo;
    }

    private Category createCategoryResource(String title, String description) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("title", title);
        if (description != null) {
            body.put("description", description);
        }
        String payload = JSON_MAPPER.writeValueAsString(body);
        ApiResponse response = execute(request(CATEGORIES_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT, payload));
        assertEquals(201, response.code);
        Category category = JSON_MAPPER.readValue(response.body, Category.class);
        categoriesToDelete.add(category.getId());
        return category;
    }

    private void deleteProjectQuietly(String id) {
        try {
            HttpURLConnection connection = requestWithId(PROJECTS_ENDPOINT, DELETE_METHOD, JSON_FORMAT, JSON_FORMAT, id,
                    null);
            connection.getResponseCode();
            connection.disconnect();
        } catch (Exception ignored) {
        }
    }

    private void deleteTodoQuietly(String id) {
        try {
            HttpURLConnection connection = requestWithId(TODOS_ENDPOINT, DELETE_METHOD, JSON_FORMAT, JSON_FORMAT, id,
                    null);
            connection.getResponseCode();
            connection.disconnect();
        } catch (Exception ignored) {
        }
    }

    private void deleteCategoryQuietly(String id) {
        try {
            HttpURLConnection connection = requestWithId(CATEGORIES_ENDPOINT, DELETE_METHOD, JSON_FORMAT, JSON_FORMAT,
                    id, null);
            connection.getResponseCode();
            connection.disconnect();
        } catch (Exception ignored) {
        }
    }

    private void deleteRelationshipQuietly(RelationshipRef ref) {
        try {
            HttpURLConnection connection = request(ref.endpoint(), DELETE_METHOD, JSON_FORMAT, JSON_FORMAT, null);
            connection.getResponseCode();
            connection.disconnect();
        } catch (Exception ignored) {
        }
    }

    private Document parseXml(String xml) throws Exception {
        DocumentBuilder builder = DOCUMENT_FACTORY.newDocumentBuilder();
        InputStream stream = new java.io.ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
        return builder.parse(stream);
    }

    private void assertXmlContainsElement(Document doc, String tagName) {
        NodeList nodes = doc.getElementsByTagName(tagName);
        assertTrue("Expected tag " + tagName + " in XML", nodes != null && nodes.getLength() > 0);
    }

    private JsonNode parseJson(String body) throws Exception {
        return JSON_MAPPER.readTree(body);
    }

    private Project parseProjectBody(String body) throws Exception {
        if (body == null || body.isBlank()) {
            return null;
        }
        JsonNode root = JSON_MAPPER.readTree(body);
        if (root.isObject() && root.has("projects")) {
            JsonNode projectsNode = root.get("projects");
            if (projectsNode.isArray() && projectsNode.size() > 0) {
                return JSON_MAPPER.treeToValue(projectsNode.get(0), Project.class);
            }
            return null;
        }
        if (root.isArray() && root.size() > 0) {
            return JSON_MAPPER.treeToValue(root.get(0), Project.class);
        }
        return JSON_MAPPER.treeToValue(root, Project.class);
    }

    private Todo parseTodoBody(String body) throws Exception {
        if (body == null || body.isBlank()) {
            return null;
        }
        JsonNode root = JSON_MAPPER.readTree(body);
        if (root.isObject() && root.has("todos")) {
            JsonNode todosNode = root.get("todos");
            if (todosNode.isArray() && todosNode.size() > 0) {
                return JSON_MAPPER.treeToValue(todosNode.get(0), Todo.class);
            }
            return null;
        }
        if (root.isArray() && root.size() > 0) {
            return JSON_MAPPER.treeToValue(root.get(0), Todo.class);
        }
        return JSON_MAPPER.treeToValue(root, Todo.class);
    }

    private void registerRelationship(String template, String parentId, String childId) {
        relationshipsToDelete.add(new RelationshipRef(template, parentId, childId));
    }

    private List<String> fetchProjectIds() throws Exception {
        ApiResponse response = execute(request(PROJECTS_ENDPOINT, GET_METHOD, JSON_FORMAT, JSON_FORMAT, null));
        assertEquals(200, response.code);
        JsonProject projectList = JSON_MAPPER.readValue(response.body, JsonProject.class);
        List<String> ids = new ArrayList<>();
        if (projectList.getProjects() != null) {
            for (Project project : projectList.getProjects()) {
                ids.add(project.getId());
            }
        }
        return ids;
    }

    private void assertJsonFieldPresent(String endpoint, String expectedField) throws Exception {
        ApiResponse response = execute(request(endpoint, GET_METHOD, JSON_FORMAT, JSON_FORMAT, null));
        assertEquals(200, response.code);
        JsonNode node = parseJson(response.body);
        assertTrue("Expected JSON field '" + expectedField + "' in response for " + endpoint,
                node.has(expectedField));
    }

    private void assertXmlTagPresent(String endpoint, String tagName) throws Exception {
        ApiResponse response = execute(request(endpoint, GET_METHOD, XML_FORMAT, XML_FORMAT, null));
        assertEquals(200, response.code);
        Document document = parseXml(response.body);
        assertXmlContainsElement(document, tagName);
    }

    private static class CurlResult {
        final int exitCode;
        final int status;
        final String body;

        CurlResult(int exitCode, int status, String body) {
            this.exitCode = exitCode;
            this.status = status;
            this.body = body;
        }
    }

    private CurlResult runCurl(List<String> command) throws Exception {
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.redirectErrorStream(true);
        Process process = builder.start();
        try (InputStream stream = process.getInputStream();
                ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            byte[] chunk = new byte[1024];
            int read;
            while ((read = stream.read(chunk)) != -1) {
                buffer.write(chunk, 0, read);
            }
            int exitCode = process.waitFor();
            String output = buffer.toString(StandardCharsets.UTF_8);
            String trimmed = output.trim();
            int lastNewline = trimmed.lastIndexOf('\n');
            if (lastNewline == -1) {
                throw new AssertionError("Unexpected curl output: " + trimmed);
            }
            String statusLine = trimmed.substring(lastNewline + 1).trim();
            int status = Integer.parseInt(statusLine);
            String body = trimmed.substring(0, lastNewline);
            return new CurlResult(exitCode, status, body);
        }
    }

    private ApiResponse getProjectById(String id) throws Exception {
        return execute(requestWithId(PROJECTS_ENDPOINT, GET_METHOD, JSON_FORMAT, JSON_FORMAT, id, null));
    }

    private ApiResponse getTodoById(String id) throws Exception {
        return execute(requestWithId(TODOS_ENDPOINT, GET_METHOD, JSON_FORMAT, JSON_FORMAT, id, null));
    }

    private ApiResponse getCategoryById(String id) throws Exception {
        return execute(requestWithId(CATEGORIES_ENDPOINT, GET_METHOD, JSON_FORMAT, JSON_FORMAT, id, null));
    }

    // Individual tests will be appended below following the user-specified suite.

    /*
     * =============================
     * 1. Projects CRUD parity tests
     * =============================
     */

    /**
     * Creates a project via JSON payload and verifies it appears in listings then
     * cleans up to keep the catalog stable.
     */
    @Test
    public void testCreateProjectJson201() throws Exception {
        List<String> beforeIds = fetchProjectIds();

        String title = unique("project-json-");
        String description = "desc";

        Project created = createProjectResource(title, description);
        assertNotNull("Project id should be generated", created.getId());
        assertEquals(title, created.getTitle());
        assertEquals(description, created.getDescription());

        assertTrue("completed field missing", created.isCompleted() == false || created.isCompleted() == true);
        assertTrue("active field missing", created.isActive() == false || created.isActive() == true);

        deleteProjectQuietly(created.getId());
        projectsToDelete.remove(created.getId());

        assertEquals("Project list should be restored after cleanup", beforeIds,
                fetchProjectIds());
    }

    /**
     * Confirms that a freshly created project gets returned by GET /projects.
     */
    @Test
    public void testGetProjectsContainsCreated() throws Exception {
        Project created = createProjectResource(unique("project-list-"), "list-desc");

        ApiResponse response = execute(request(PROJECTS_ENDPOINT, GET_METHOD, JSON_FORMAT, JSON_FORMAT, null));
        assertEquals(200, response.code);
        JsonProject projectList = JSON_MAPPER.readValue(response.body, JsonProject.class);
        assertTrue("Created project should be present in GET /projects",
                projectList.contains(created));
    }

    /**
     * Retrieves a project by id and checks the JSON fields mirror creation
     * results.
     */
    @Test
    public void testGetProjectJsonFields() throws Exception {
        Project created = createProjectResource(unique("project-get-"), "single-desc");

        ApiResponse response = getProjectById(created.getId());
        assertEquals(200, response.code);
        Project fetched = parseProjectBody(response.body);
        assertNotNull("GET /projects/{id} should return project payload", fetched);

        assertEquals(created.getId(), fetched.getId());
        assertEquals(created.getTitle(), fetched.getTitle());
        assertEquals(created.getDescription(), fetched.getDescription());
        assertEquals(created.isCompleted(), fetched.isCompleted());
        assertEquals(created.isActive(), fetched.isActive());
    }

    /**
     * Updates project metadata using PUT and ensures the persisted values are
     * refreshed.
     */
    @Test
    public void testUpdateProjectPatchOrPut() throws Exception {
        Project created = createProjectResource(unique("project-update-"), "initial");
        Map<String, Object> updateBody = new HashMap<>();
        updateBody.put("title", created.getTitle());
        updateBody.put("description", "updated description");
        updateBody.put("completed", created.isCompleted());
        updateBody.put("active", created.isActive());

        String payload = JSON_MAPPER.writeValueAsString(updateBody);
        ApiResponse updateResponse = execute(requestWithId(PROJECTS_ENDPOINT, PUT_METHOD, JSON_FORMAT, JSON_FORMAT,
                created.getId(), payload));

        assertEquals(200, updateResponse.code);
        Project updated = parseProjectBody(updateResponse.body);
        assertNotNull("Update response should include project", updated);
        assertEquals("updated description", updated.getDescription());
        assertEquals(created.getTitle(), updated.getTitle());

        ApiResponse fetchedResponse = getProjectById(created.getId());
        Project fetched = parseProjectBody(fetchedResponse.body);
        assertNotNull("Fetched project should deserialize", fetched);
        assertEquals("updated description", fetched.getDescription());
        assertEquals(created.getTitle(), fetched.getTitle());
    }

    /**
     * Deletes a project and verifies the resource is removed while returning a
     * success status code.
     */
    @Test
    public void testDeleteProject() throws Exception {
        Project created = createProjectResource(unique("project-delete-"), "to-remove");

        ApiResponse deleteResponse = execute(
                requestWithId(PROJECTS_ENDPOINT, DELETE_METHOD, JSON_FORMAT, JSON_FORMAT, created.getId(), null));
        assertTrue(deleteResponse.code == 200 || deleteResponse.code == 204);

        projectsToDelete.remove(created.getId());

        ApiResponse fetchAfterDeletion = getProjectById(created.getId());
        assertEquals(404, fetchAfterDeletion.code);
    }

    /**
     * Ensures the XML representation of the project collection contains newly
     * added entries.
     */
    @Test
    public void testGetProjectsCollectionXml() throws Exception {
        Project created = createProjectResource(unique("project-xml-"), "xml-desc");

        ApiResponse response = execute(request(PROJECTS_ENDPOINT, GET_METHOD, XML_FORMAT, XML_FORMAT, null));
        assertEquals(200, response.code);
        XmlProject xmlProjects = XML_MAPPER.readValue(response.body, XmlProject.class);
        assertTrue("XML list should include created project", xmlProjects.contains(created));
    }

    /*
     * ===========================
     * 2. Todos CRUD parity tests
     * ===========================
     */

    /**
     * Creates a todo via JSON and confirms fields plus cleanup behaviour.
     */
    @Test
    public void testCreateTodoJson201() throws Exception {
        Todo created = createTodoResource(unique("todo-json-"), "todo-desc");
        assertNotNull(created.getId());
        assertEquals("todo-desc", created.getDescription());
        assertEquals("false", created.getDoneStatus());

        deleteTodoQuietly(created.getId());
        todosToDelete.remove(created.getId());
    }

    /**
     * Fetches a todo by id and checks all JSON fields align with what was
     * created.
     */
    @Test
    public void testGetTodoJson() throws Exception {
        Todo created = createTodoResource(unique("todo-get-"), "todo-get-desc");

        ApiResponse response = getTodoById(created.getId());
        assertEquals(200, response.code);
        Todo fetched = parseTodoBody(response.body);

        assertEquals(created.getId(), fetched.getId());
        assertEquals(created.getTitle(), fetched.getTitle());
        assertEquals(created.getDescription(), fetched.getDescription());
        assertEquals(created.getDoneStatus(), fetched.getDoneStatus());
    }

    /**
     * Validates the XML todo collection includes a newly created todo entry.
     */
    @Test
    public void testGetTodosXml() throws Exception {
        Todo created = createTodoResource(unique("todo-xml-"), "todo-xml-desc");

        ApiResponse response = execute(request(TODOS_ENDPOINT, GET_METHOD, XML_FORMAT, XML_FORMAT, null));
        assertEquals(200, response.code);
        XmlTodo xmlTodos = XML_MAPPER.readValue(response.body, XmlTodo.class);
        assertTrue("XML todos should include created todo", xmlTodos.contains(created));
    }

    /**
     * Ensures deleting a todo removes it and the API returns not-found on a
     * follow-up fetch.
     */
    @Test
    public void testDeleteTodo() throws Exception {
        Todo created = createTodoResource(unique("todo-delete-"), "cleanup");

        ApiResponse deleteResponse = execute(
                requestWithId(TODOS_ENDPOINT, DELETE_METHOD, JSON_FORMAT, JSON_FORMAT, created.getId(), null));
        assertTrue(deleteResponse.code == 200 || deleteResponse.code == 204);
        todosToDelete.remove(created.getId());

        ApiResponse fetchResponse = getTodoById(created.getId());
        assertEquals(404, fetchResponse.code);
    }

    /**
     * Confirms deleting a todo doesn't cascade to unrelated project or category
     * resources.
     */
    @Test
    public void testNoUnintendedSideEffectsOnDelete() throws Exception {
        Todo todo = createTodoResource(unique("todo-side-effects-"), "side-effect");
        Project project = createProjectResource(unique("project-side-effects-"), "project-side");
        Category category = createCategoryResource(unique("category-side-effects-"), "category-side");

        ApiResponse deleteResponse = execute(
                requestWithId(TODOS_ENDPOINT, DELETE_METHOD, JSON_FORMAT, JSON_FORMAT, todo.getId(), null));
        assertTrue(deleteResponse.code == 200 || deleteResponse.code == 204);
        todosToDelete.remove(todo.getId());

        ApiResponse projectResponse = getProjectById(project.getId());
        assertEquals("Unrelated project should remain", 200, projectResponse.code);

        ApiResponse categoryResponse = getCategoryById(category.getId());
        assertEquals("Unrelated category should remain", 200, categoryResponse.code);
    }

    /*
     * ================================
     * 3. Categories CRUD parity tests
     * ================================
     */

    /**
     * Creates a category using JSON and verifies the API returns an id and
     * persisted description.
     */
    @Test
    public void testCreateCategoryJson201() throws Exception {
        Category created = createCategoryResource(unique("category-json-"), "cat-desc");
        assertNotNull(created.getId());
        assertEquals("cat-desc", created.getDescription());

        deleteCategoryQuietly(created.getId());
        categoriesToDelete.remove(created.getId());
    }

    /**
     * Checks that GET /categories lists a recently created category.
     */
    @Test
    public void testGetCategoriesJson() throws Exception {
        Category created = createCategoryResource(unique("category-list-"), "category-list-desc");

        ApiResponse response = execute(request(CATEGORIES_ENDPOINT, GET_METHOD, JSON_FORMAT, JSON_FORMAT, null));
        assertEquals(200, response.code);
        JsonCategory categories = JSON_MAPPER.readValue(response.body, JsonCategory.class);
        assertTrue("Created category should be listed", categories.contains(created));
    }

    /**
     * Verifies XML listings include the newly inserted category resource.
     */
    @Test
    public void testGetCategoriesXml() throws Exception {
        Category created = createCategoryResource(unique("category-xml-"), "category-xml-desc");

        ApiResponse response = execute(request(CATEGORIES_ENDPOINT, GET_METHOD, XML_FORMAT, XML_FORMAT, null));
        assertEquals(200, response.code);
        XmlCategory categories = XML_MAPPER.readValue(response.body, XmlCategory.class);
        assertTrue("XML categories should include created category", categories.contains(created));
    }

    /**
     * Updates a category's description and ensures the change sticks.
     */
    @Test
    public void testUpdateCategory() throws Exception {
        Category created = createCategoryResource(unique("category-update-"), "before");
        Map<String, Object> updateBody = new HashMap<>();
        updateBody.put("title", created.getTitle());
        updateBody.put("description", "after");

        String payload = JSON_MAPPER.writeValueAsString(updateBody);
        ApiResponse updateResponse = execute(requestWithId(CATEGORIES_ENDPOINT, PUT_METHOD, JSON_FORMAT, JSON_FORMAT,
                created.getId(), payload));

        assertEquals(200, updateResponse.code);
        Category updated = JSON_MAPPER.readValue(updateResponse.body, Category.class);
        assertEquals("after", updated.getDescription());
        assertEquals(created.getTitle(), updated.getTitle());
    }

    /**
     * Deletes a category and confirms related todos/projects remain intact while
     * the category disappears.
     */
    @Test
    public void testDeleteCategory() throws Exception {
        Category category = createCategoryResource(unique("category-delete-"), "delete-me");
        Todo todo = createTodoResource(unique("todo-category-side-effect-"), "side");
        Project project = createProjectResource(unique("project-category-side-effect-"), "side");

        ApiResponse deleteResponse = execute(requestWithId(CATEGORIES_ENDPOINT, DELETE_METHOD, JSON_FORMAT,
                JSON_FORMAT, category.getId(), null));
        assertTrue(deleteResponse.code == 200 || deleteResponse.code == 204);
        categoriesToDelete.remove(category.getId());

        ApiResponse fetchResponse = execute(
                requestWithId(CATEGORIES_ENDPOINT, GET_METHOD, JSON_FORMAT, JSON_FORMAT, category.getId(), null));
        assertEquals(404, fetchResponse.code);

        assertEquals(200, getTodoById(todo.getId()).code);
        assertEquals(200, getProjectById(project.getId()).code);
    }

    /*
     * ========================================================
     * 4. Project tasks linking – expected & actual behaviour
     * ========================================================
     */

    /**
     * Creates a todo under a project with a boolean doneStatus and verifies it
     * can be retrieved from the project's task list.
     */
    @Test
    public void testCreateTaskWithBooleanDoneStatus() throws Exception {
        Project project = createProjectResource(unique("project-task-"), "task-parent");

        Map<String, Object> body = new HashMap<>();
        body.put("title", unique("task-"));
        body.put("description", "task-desc");
        body.put("doneStatus", false);

        String payload = JSON_MAPPER.writeValueAsString(body);
        String endpoint = String.format(PROJECTS_TASKS_ENDPOINT, project.getId());
        ApiResponse response = execute(request(endpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT, payload));

        assertEquals(201, response.code);
        Todo createdTask = JSON_MAPPER.readValue(response.body, Todo.class);
        assertEquals("false", createdTask.getDoneStatus());
        assertEquals(body.get("title"), createdTask.getTitle());

        todosToDelete.add(createdTask.getId());
        registerRelationship(PROJECTS_TASKS_ID_ENDPOINT, project.getId(), createdTask.getId());

        ApiResponse tasksResponse = execute(request(endpoint, GET_METHOD, JSON_FORMAT, JSON_FORMAT, null));
        JsonTodo projectTasks = JSON_MAPPER.readValue(tasksResponse.body, JsonTodo.class);
        assertTrue("Project tasks should include created todo", projectTasks.contains(createdTask));
    }

    /**
     * Attempts to create a project task with a string doneStatus and expects a
     * validation error response.
     */
    @Test
    public void testCreateTaskWithStringDoneStatusFailsExpected() throws Exception {
        Project project = createProjectResource(unique("project-task-invalid-"), "task-parent");

        Map<String, Object> body = new HashMap<>();
        body.put("title", unique("task-invalid-"));
        body.put("description", "task-desc");
        body.put("doneStatus", "open");

        String payload = JSON_MAPPER.writeValueAsString(body);
        String endpoint = String.format(PROJECTS_TASKS_ENDPOINT, project.getId());
        ApiResponse response = execute(request(endpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT, payload));

        assertEquals(400, response.code);
        assertTrue("Expected validation message for invalid doneStatus",
                response.body.contains("doneStatus") || response.body.contains("Failed Validation"));
    }

    /**
     * Links an existing todo to a project using a capitalised Id key and confirms
     * it appears in the project task list.
     */
    @Test
    public void testLinkExistingTodoByIdCapitalized() throws Exception {
        Project project = createProjectResource(unique("project-link-"), "link-parent");
        Todo todo = createTodoResource(unique("todo-link-"), "todo-link-desc");

        Map<String, Object> body = new HashMap<>();
        body.put("Id", todo.getId());

        String payload = JSON_MAPPER.writeValueAsString(body);
        String endpoint = String.format(PROJECTS_TASKS_ENDPOINT, project.getId());
        ApiResponse response = execute(request(endpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT, payload));

        assertEquals(201, response.code);

        registerRelationship(PROJECTS_TASKS_ID_ENDPOINT, project.getId(), todo.getId());

        ApiResponse tasksResponse = execute(request(endpoint, GET_METHOD, JSON_FORMAT, JSON_FORMAT, null));
        JsonTodo projectTasks = JSON_MAPPER.readValue(tasksResponse.body, JsonTodo.class);
        assertTrue(projectTasks.contains(todo));
    }

    /**
     * Demonstrates that both numeric and string Id payloads succeed when linking
     * an existing todo to a project.
     */
    @Test
    public void testLinkExistingTodoStringVsNumericId() throws Exception {
        Project project = createProjectResource(unique("project-link-multi-"), "link-parent");
        Todo todo = createTodoResource(unique("todo-link-multi-"), "todo-link-desc");

        String endpoint = String.format(PROJECTS_TASKS_ENDPOINT, project.getId());

        String numericPayload = String.format("{\"Id\": \"%s\"}", todo.getId());
        ApiResponse numericResponse = execute(request(endpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT, numericPayload));
        assertEquals(201, numericResponse.code);
        registerRelationship(PROJECTS_TASKS_ID_ENDPOINT, project.getId(), todo.getId());

        String stringPayload = String.format("{\"Id\": %s}", todo.getId());
        ApiResponse stringResponse = execute(request(endpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT, stringPayload));
        assertEquals(201, stringResponse.code);

        ApiResponse tasksResponse = execute(request(endpoint, GET_METHOD, JSON_FORMAT, JSON_FORMAT, null));
        JsonTodo tasks = JSON_MAPPER.readValue(tasksResponse.body, JsonTodo.class);
        assertTrue(tasks.contains(todo));
    }

    /**
     * Posts a task payload containing an unsupported status field and expects a
     * validation failure in line with documented behaviour.
     */
    @Test
    public void testCreateTaskWithStatusFieldShouldFailExpected() throws Exception {
        Project project = createProjectResource(unique("project-status-expected-"), "status-parent");
        Map<String, Object> body = new HashMap<>();
        body.put("title", "Implement API");
        body.put("status", false);

        String payload = JSON_MAPPER.writeValueAsString(body);
        String endpoint = String.format(PROJECTS_TASKS_ENDPOINT, project.getId());
        ApiResponse response = execute(request(endpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT, payload));

        assertEquals(400, response.code);
        assertTrue("Expected structured validation error",
                response.body.contains("errorMessages") || response.body.contains("Failed Validation"));
    }

    /*
     * ======================================================
     * 5. Projects ↔ Categories association expected/actual
     * ======================================================
     */

    /**
     * Attaches a category to a project using the documented JSON shape and
     * verifies the link via retrieval.
     */
    @Test
    public void testAttachCategoryByIdExpected() throws Exception {
        Project project = createProjectResource(unique("project-cat-link-expected-"), "parent");
        Category category = createCategoryResource(unique("category-cat-link-expected-"), "child");

        String endpoint = String.format(PROJECTS_CATEGORIES_ENDPOINT, project.getId());
        String payload = String.format("{\"id\": \"%s\"}", category.getId());
        ApiResponse response = execute(request(endpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT, payload));

        assertTrue(response.code == 200 || response.code == 201 || response.code == 204);
        registerRelationship(PROJECTS_CATEGORIES_ID_ENDPOINT, project.getId(), category.getId());

        ApiResponse getResponse = execute(request(endpoint, GET_METHOD, JSON_FORMAT, JSON_FORMAT, null));
        JsonCategory jsonCategory = JSON_MAPPER.readValue(getResponse.body, JsonCategory.class);
        assertTrue("Category should appear under project", jsonCategory.contains(category));
    }

    /**
     * Shows that supplying a numeric id in the payload produces the observed
     * server-side failure.
     */
    @Test
    public void testAttachCategoryWithPlainNumericPayloadActual() throws Exception {
        Project project = createProjectResource(unique("project-cat-link-actual-"), "parent");
        Category category = createCategoryResource(unique("category-cat-link-actual-"), "child");

        String endpoint = String.format(PROJECTS_CATEGORIES_ENDPOINT, project.getId());
        String payload = String.format("{\"id\": %s}", category.getId());
        ApiResponse response = execute(request(endpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT, payload));

        assertTrue("Expect documented failure when using numeric id",
                response.code == 400 || response.code == 404);
        assertTrue(response.body.contains("Could not find") || response.body.contains("id"));
    }

    /**
     * Submits malformed JSON when attaching a category to illustrate parser
     * error handling.
     */
    @Test
    public void testAttachCategoryWithMalformedJsonActual() throws Exception {
        Project project = createProjectResource(unique("project-cat-malformed-"), "parent");

        String endpoint = String.format(PROJECTS_CATEGORIES_ENDPOINT, project.getId());
        String malformedPayload = "{\"id\": \"" + project.getId(); // missing closing quote/brace
        ApiResponse response = execute(request(endpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT, malformedPayload));

        assertEquals(400, response.code);
        assertTrue("Malformed JSON should surface parser error",
                response.body.contains("Malformed") || response.body.contains("Exception"));
    }

    /**
     * Documents the NullPointerException emitted when using category_id instead
     * of id in the attachment payload.
     */
    @Test
    public void testAttachCategoryWithCategoryIdFieldActual() throws Exception {
        Project project = createProjectResource(unique("project-cat-categoryid-"), "parent");
        Category category = createCategoryResource(unique("category-cat-categoryid-"), "child");

        String endpoint = String.format(PROJECTS_CATEGORIES_ENDPOINT, project.getId());
        String payload = String.format("{\"Id\": \"%s\"}", category.getId());
        ApiResponse response = execute(request(endpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT, payload));

        ApiResponse getResponse = execute(request(endpoint, GET_METHOD, JSON_FORMAT, JSON_FORMAT, null));

        assertEquals(201, response.code);
        assertEquals(200, getResponse.code);
        assertTrue(getResponse.body.contains(String.format("\"%s\"", category.getId())));
    }

    /*
     * =============================================
     * 6/7. Todo ↔ Category association & malformed
     * =============================================
     */

    /**
     * Attaches a category to a todo using the documented contract and confirms it
     * is reflected in the todo's category list.
     */
    @Test
    public void testAttachCategoryToTodoValid() throws Exception {
        Todo todo = createTodoResource(unique("todo-cat-link-"), "todo-parent");
        Category category = createCategoryResource(unique("category-todo-link-"), "cat-child");

        String endpoint = String.format(TODOS_CATEGORIES_ENDPOINT, todo.getId());
        String payload = String.format("{\"id\": \"%s\"}", category.getId());
        ApiResponse response = execute(request(endpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT, payload));

        assertTrue(response.code == 200 || response.code == 201 || response.code == 204);
        registerRelationship(TODOS_CATEGORIES_ID_ENDPOINT, todo.getId(), category.getId());

        ApiResponse getResponse = execute(request(endpoint, GET_METHOD, JSON_FORMAT, JSON_FORMAT, null));
        JsonCategory categories = JSON_MAPPER.readValue(getResponse.body, JsonCategory.class);
        assertTrue(categories.contains(category));
    }

    /**
     * Sends malformed JSON when linking a category to a todo to assert the API
     * returns a 400 with diagnostic content.
     */
    @Test
    public void testAttachCategoryToTodoMalformed() throws Exception {
        Todo todo = createTodoResource(unique("todo-cat-malformed-"), "todo-parent");

        String endpoint = String.format(TODOS_CATEGORIES_ENDPOINT, todo.getId());
        String malformed = "{\"" + todo.getId() + "\"}"; // intentionally malformed
        ApiResponse response = execute(request(endpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT, malformed));

        assertEquals(400, response.code);
        assertTrue(response.body.contains("NullPointerException") || response.body.contains("Malformed")
                || response.body.contains("Exception"));
    }

    /*
     * ==============================================
     * 8. Read endpoints JSON/XML parity verification
     * ==============================================
     */

    /**
     * Confirms GET /projects/{id}/categories returns a JSON categories field.
     */
    @Test
    public void testGetProjectCategoriesJson() throws Exception {
        Project project = createProjectResource(unique("project-read-json-"), "parent");
        String endpoint = String.format(PROJECTS_CATEGORIES_ENDPOINT, project.getId());
        assertJsonFieldPresent(endpoint, "categories");
    }

    /**
     * Validates the XML variant of project categories exposes the categories tag.
     */
    @Test
    public void testGetProjectCategoriesXml() throws Exception {
        Project project = createProjectResource(unique("project-read-xml-"), "parent");
        String endpoint = String.format(PROJECTS_CATEGORIES_ENDPOINT, project.getId());
        assertXmlTagPresent(endpoint, "categories");
    }

    /**
     * Checks that todo category listing responses include the expected JSON
     * collection field.
     */
    @Test
    public void testGetTodoCategoriesJson() throws Exception {
        Todo todo = createTodoResource(unique("todo-read-json-"), "parent");
        String endpoint = String.format(TODOS_CATEGORIES_ENDPOINT, todo.getId());
        assertJsonFieldPresent(endpoint, "categories");
    }

    /**
     * Confirms XML todo category responses expose a categories tag.
     */
    @Test
    public void testGetTodoCategoriesXml() throws Exception {
        Todo todo = createTodoResource(unique("todo-read-xml-"), "parent");
        String endpoint = String.format(TODOS_CATEGORIES_ENDPOINT, todo.getId());
        assertXmlTagPresent(endpoint, "categories");
    }

    /**
     * Ensures project task listings in JSON contain a todos array field.
     */
    @Test
    public void testGetProjectTasksJson() throws Exception {
        Project project = createProjectResource(unique("project-read-tasks-json-"), "parent");
        String endpoint = String.format(PROJECTS_TASKS_ENDPOINT, project.getId());
        assertJsonFieldPresent(endpoint, "todos");
    }

    /**
     * Validates the XML project tasks listing contains a todos tag.
     */
    @Test
    public void testGetProjectTasksXml() throws Exception {
        Project project = createProjectResource(unique("project-read-tasks-xml-"), "parent");
        String endpoint = String.format(PROJECTS_TASKS_ENDPOINT, project.getId());
        assertXmlTagPresent(endpoint, "todos");
    }

    /**
     * Checks the root projects collection exposes a projects field in JSON.
     */
    @Test
    public void testGetProjectsCollectionJson() throws Exception {
        assertJsonFieldPresent(PROJECTS_ENDPOINT, "projects");
    }

    /**
     * Confirms the XML projects collection includes the projects tag.
     */
    @Test
    public void testGetProjectsCollectionXmlTags() throws Exception {
        assertXmlTagPresent(PROJECTS_ENDPOINT, "projects");
    }

    /**
     * Ensures GET /todos returns a todos field in its JSON payload.
     */
    @Test
    public void testGetTodosCollectionJson() throws Exception {
        assertJsonFieldPresent(TODOS_ENDPOINT, "todos");
    }

    /**
     * Validates the XML todos collection contains a todos tag for parity.
     */
    @Test
    public void testGetTodosCollectionXml() throws Exception {
        assertXmlTagPresent(TODOS_ENDPOINT, "todos");
    }

    /**
     * Checks the JSON categories collection lists categories as a top-level field.
     */
    @Test
    public void testGetCategoriesCollectionJson() throws Exception {
        assertJsonFieldPresent(CATEGORIES_ENDPOINT, "categories");
    }

    /**
     * Ensures the XML categories collection provides a categories tag.
     */
    @Test
    public void testGetCategoriesCollectionXml() throws Exception {
        assertXmlTagPresent(CATEGORIES_ENDPOINT, "categories");
    }

    /*
     * ==========================================
     * 9. Relationship deletion and idempotency
     * ==========================================
     */

    /**
     * Deletes a todo relationship from a project and verifies both the link is
     * gone and the todo itself persists.
     */
    @Test
    public void testDeleteTaskFromProject() throws Exception {
        Project project = createProjectResource(unique("project-delete-rel-"), "parent");
        Todo todo = createTodoResource(unique("todo-delete-rel-"), "child");

        String endpoint = String.format(PROJECTS_TASKS_ENDPOINT, project.getId());
        String payload = String.format("{\"Id\": \"%s\"}", todo.getId());
        ApiResponse createResponse = execute(request(endpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT, payload));
        assertEquals(201, createResponse.code);
        registerRelationship(PROJECTS_TASKS_ID_ENDPOINT, project.getId(), todo.getId());

        String deleteEndpoint = String.format(PROJECTS_TASKS_ID_ENDPOINT, project.getId(), todo.getId());
        ApiResponse deleteResponse = execute(request(deleteEndpoint, DELETE_METHOD, JSON_FORMAT, JSON_FORMAT, null));
        assertTrue(deleteResponse.code == 200 || deleteResponse.code == 204);
        relationshipsToDelete.remove(new RelationshipRef(PROJECTS_TASKS_ID_ENDPOINT, project.getId(), todo.getId()));

        ApiResponse tasksResponse = execute(request(endpoint, GET_METHOD, JSON_FORMAT, JSON_FORMAT, null));
        JsonTodo tasks = JSON_MAPPER.readValue(tasksResponse.body, JsonTodo.class);
        assertFalse("Todo should no longer be linked", tasks.contains(todo));

        ApiResponse todoResponse = getTodoById(todo.getId());
        assertEquals("Underlying todo should remain", 200, todoResponse.code);
    }

    /**
     * Performs consecutive deletes on the same relationship to confirm the API is
     * effectively idempotent.
     */
    @Test
    public void testDeleteNonexistentRelationshipIdempotency() throws Exception {
        Project project = createProjectResource(unique("project-delete-idem-"), "parent");
        Todo todo = createTodoResource(unique("todo-delete-idem-"), "child");

        String endpoint = String.format(PROJECTS_TASKS_ENDPOINT, project.getId());
        String payload = String.format("{\"Id\": \"%s\"}", todo.getId());
        ApiResponse createResponse = execute(request(endpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT, payload));
        assertEquals(201, createResponse.code);

        String deleteEndpoint = String.format(PROJECTS_TASKS_ID_ENDPOINT, project.getId(), todo.getId());
        ApiResponse firstDelete = execute(request(deleteEndpoint, DELETE_METHOD, JSON_FORMAT, JSON_FORMAT, null));
        assertTrue(firstDelete.code == 200 || firstDelete.code == 204);

        ApiResponse secondDelete = execute(request(deleteEndpoint, DELETE_METHOD, JSON_FORMAT, JSON_FORMAT, null));
        assertTrue("Second delete should indicate missing resource or idempotency",
                secondDelete.code == 404 || secondDelete.code == 200 || secondDelete.code == 204);
    }

    /*
     * =============================================
     * 10. Malformed JSON handling and safe errors
     * =============================================
     */

    /**
     * Posts malformed JSON to /projects to assert the API returns a structured
     * 400 error.
     */
    @Test
    public void testMalformedJsonReturnsStructuredError() throws Exception {
        String malformed = "{\"title\": \"Missing brace"; // unbalanced JSON
        ApiResponse response = execute(request(PROJECTS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT, malformed));

        assertEquals(400, response.code);
        assertTrue("Expect server to identify malformed JSON",
                response.body.contains("Malformed") || response.body.contains("errorMessages"));
    }

    /**
     * Sends JSON with an XML content type to highlight the server's error
     * handling for mismatched headers.
     */
    @Test
    public void testMalformedJsonWithXmlContentType() throws Exception {
        String body = JSON_MAPPER.writeValueAsString(Map.of("title", unique("project-xml-contenttype-")));
        ApiResponse response = execute(request(PROJECTS_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT, body));

        assertEquals(400, response.code);
        assertTrue("Expect clear error when JSON sent with XML content type",
                response.body.contains("Malformed") || response.body.contains("errorMessages")
                        || response.body.contains("NullPointerException"));
    }

    /**
     * Sends JSON with an XML content type and confirms that the server creates
     * the resource while returning JSON.
     * 
     * @throws Exception
     */
    @Test
    public void testMalformedJsonWithXmlContentTypeActualBehavior() throws Exception {
        String body = JSON_MAPPER.writeValueAsString(Map.of("title", unique("project-xml-contenttype-")));
        ApiResponse response = execute(request(PROJECTS_ENDPOINT, POST_METHOD, XML_FORMAT, XML_FORMAT, body));

        assertEquals(201, response.code);
        assertTrue("Expect server to create resource",
                response.body.contains("title"));
    }

    /*
     * =====================================
     * 11. Command-line curl compatibility
     * =====================================
     */

    /**
     * Executes curl to create a project and inspects both HTTP status and body
     * for alignment with REST results.
     */
    @Test
    public void testCurlCreateProject() throws Exception {
        String title = unique("curl-project-");
        String description = "curl-desc";
        String payload = String.format("{\"title\":\"%s\",\"description\":\"%s\"}", title, description);

        List<String> command = new ArrayList<>();
        command.add("curl");
        command.add("-s");
        command.add("-w");
        command.add("\n%{http_code}\n");
        command.add("-X");
        command.add("POST");
        command.add(BASE_URL + PROJECTS_ENDPOINT);
        command.add("-H");
        command.add("Content-Type: application/json");
        command.add("-d");
        command.add(payload);

        CurlResult result = runCurl(command);
        assertEquals(0, result.exitCode);
        assertEquals(201, result.status);

        JsonNode node = parseJson(result.body);
        assertEquals(title, node.get("title").asText());
        assertEquals(description, node.get("description").asText());
        assertTrue(node.has("id"));

        String id = node.get("id").asText();
        projectsToDelete.add(id);
    }

    /**
     * Uses curl with Accept: application/xml to confirm the CLI can retrieve XML
     * task listings.
     */
    @Test
    public void testCurlAcceptXml() throws Exception {
        Project project = createProjectResource(unique("curl-project-xml-"), "curl");
        List<String> command = new ArrayList<>();
        command.add("curl");
        command.add("-s");
        command.add("-w");
        command.add("\n%{http_code}\n");
        command.add("-H");
        command.add("Accept: application/xml");
        command.add(BASE_URL + String.format(PROJECTS_TASKS_ENDPOINT, project.getId()));

        CurlResult result = runCurl(command);
        assertEquals(0, result.exitCode);
        assertEquals(200, result.status);
        assertTrue("Curl XML response should contain <todos>", result.body.contains("<todos"));
    }

    /**
     * Posts malformed JSON via curl and expects a 400 along with diagnostic text.
     */
    @Test
    public void testCurlPostMalformedJson() throws Exception {
        String malformed = "{\"id\": \"3\""; // missing closing brace
        List<String> command = new ArrayList<>();
        command.add("curl");
        command.add("-s");
        command.add("-w");
        command.add("\n%{http_code}\n");
        command.add("-X");
        command.add("POST");
        command.add(BASE_URL + PROJECTS_ENDPOINT);
        command.add("-H");
        command.add("Content-Type: application/json");
        command.add("-d");
        command.add(malformed);

        CurlResult result = runCurl(command);
        assertEquals(0, result.exitCode);
        assertEquals(400, result.status);
        assertTrue(result.body.contains("Malformed") || result.body.contains("Exception"));
    }

    /*
     * ========================================
     * 12. Return code consistency expectations
     * ========================================
     */

    /**
     * Verifies project creation success, bad payload, and missing resource codes
     * align with expectations.
     */
    @Test
    public void testProjectsSuccessAndErrors() throws Exception {
        Map<String, Object> valid = new HashMap<>();
        valid.put("title", unique("project-success-"));
        valid.put("description", "desc");
        String validPayload = JSON_MAPPER.writeValueAsString(valid);

        ApiResponse validResponse = execute(request(PROJECTS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                validPayload));
        assertEquals(201, validResponse.code);
        JsonNode validNode = parseJson(validResponse.body);
        projectsToDelete.add(validNode.get("id").asText());

        ApiResponse getResponse = execute(requestWithId(PROJECTS_ENDPOINT, GET_METHOD, JSON_FORMAT, JSON_FORMAT,
                validNode.get("id").asText(), null));
        assertEquals(200, getResponse.code);

        ApiResponse invalidPayloadResponse = execute(request(PROJECTS_ENDPOINT, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                "{\"unknown\":\"value\"}"));
        assertEquals(400, invalidPayloadResponse.code);

        ApiResponse notFoundResponse = execute(requestWithId(PROJECTS_ENDPOINT, GET_METHOD, JSON_FORMAT, JSON_FORMAT,
                "999999", null));
        assertEquals(404, notFoundResponse.code);
    }

    /**
     * Checks relationship endpoints for correct status codes across success,
     * validation error, and missing parent scenarios.
     */
    @Test
    public void testTasksAndAssociationsReturnCodes() throws Exception {
        Project project = createProjectResource(unique("project-task-codes-"), "desc");
        Todo todo = createTodoResource(unique("todo-task-codes-"), "desc");

        String endpoint = String.format(PROJECTS_TASKS_ENDPOINT, project.getId());
        String payload = String.format("{\"Id\": \"%s\"}", todo.getId());
        ApiResponse createResponse = execute(request(endpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT, payload));
        assertEquals(201, createResponse.code);
        registerRelationship(PROJECTS_TASKS_ID_ENDPOINT, project.getId(), todo.getId());

        ApiResponse badPayloadResponse = execute(request(endpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT,
                "{\"status\":false}"));
        assertEquals(400, badPayloadResponse.code);

        String missingProjectEndpoint = String.format(PROJECTS_TASKS_ENDPOINT, "999999");
        ApiResponse missingProjectResponse = execute(request(missingProjectEndpoint, POST_METHOD, JSON_FORMAT,
                JSON_FORMAT, payload));
        assertTrue(missingProjectResponse.code == 404 || missingProjectResponse.code == 400);
    }

    /*
     * ===============================================
     * 13. Concurrency and idempotency stress testing
     * ===============================================
     */

    /**
     * Runs concurrent create/link/delete cycles to ensure the API remains stable
     * and leaves no dangling relationships.
     */
    @Test
    public void testCreateAndDeleteLoop() throws Exception {
        Project project = createProjectResource(unique("project-loop-"), "loop");
        int iterations = 5;

        ExecutorService executor = Executors.newFixedThreadPool(Math.min(3, iterations));
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(iterations);
        List<Exception> errors = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < iterations; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    Todo todo = createTodoResource(unique("todo-loop-"), "loop");
                    String endpoint = String.format(PROJECTS_TASKS_ENDPOINT, project.getId());
                    String payload = String.format("{\"Id\": \"%s\"}", todo.getId());

                    ApiResponse createResp = execute(
                            request(endpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT, payload));
                    if (createResp.code != 201) {
                        throw new AssertionError("Expected 201 but got " + createResp.code);
                    }

                    RelationshipRef ref = new RelationshipRef(PROJECTS_TASKS_ID_ENDPOINT, project.getId(),
                            todo.getId());
                    registerRelationship(PROJECTS_TASKS_ID_ENDPOINT, project.getId(), todo.getId());

                    String deleteEndpoint = ref.endpoint();
                    ApiResponse deleteResp = execute(
                            request(deleteEndpoint, DELETE_METHOD, JSON_FORMAT, JSON_FORMAT, null));
                    if (!(deleteResp.code == 200 || deleteResp.code == 204)) {
                        throw new AssertionError("Unexpected delete status " + deleteResp.code);
                    }
                    relationshipsToDelete.remove(ref);

                    ApiResponse todoDelete = execute(requestWithId(TODOS_ENDPOINT, DELETE_METHOD, JSON_FORMAT,
                            JSON_FORMAT, todo.getId(), null));
                    if (!(todoDelete.code == 200 || todoDelete.code == 204)) {
                        throw new AssertionError("Unexpected todo delete status " + todoDelete.code);
                    }
                    todosToDelete.remove(todo.getId());
                } catch (Exception ex) {
                    errors.add(ex);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        boolean completed = doneLatch.await(60, TimeUnit.SECONDS);
        executor.shutdownNow();

        assertTrue("Loop tasks did not finish in time", completed);
        assertTrue("Errors during concurrent loop: " + errors, errors.isEmpty());

        ApiResponse tasksResponse = execute(
                request(String.format(PROJECTS_TASKS_ENDPOINT, project.getId()), GET_METHOD, JSON_FORMAT, JSON_FORMAT,
                        null));
        JsonTodo tasks = JSON_MAPPER.readValue(tasksResponse.body, JsonTodo.class);
        assertEquals("Project should have no linked tasks after loop", 0, tasks.size());
    }

    /**
     * Issues simultaneous deletes against the same relationship and asserts all
     * observed status codes are acceptable for idempotent behaviour.
     */
    @Test
    public void testIdempotentDeleteRelationship() throws Exception {
        Project project = createProjectResource(unique("project-idempotent-"), "loop");
        Todo todo = createTodoResource(unique("todo-idempotent-"), "loop");

        String endpoint = String.format(PROJECTS_TASKS_ENDPOINT, project.getId());
        String payload = String.format("{\"Id\": \"%s\"}", todo.getId());
        ApiResponse createResponse = execute(request(endpoint, POST_METHOD, JSON_FORMAT, JSON_FORMAT, payload));
        assertEquals(201, createResponse.code);

        String deleteEndpoint = String.format(PROJECTS_TASKS_ID_ENDPOINT, project.getId(), todo.getId());

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(1);
        List<Integer> statuses = Collections.synchronizedList(new ArrayList<>());

        Runnable deleteTask = () -> {
            try {
                latch.await();
                ApiResponse response = execute(
                        request(deleteEndpoint, DELETE_METHOD, JSON_FORMAT, JSON_FORMAT, null));
                statuses.add(response.code);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        executor.submit(deleteTask);
        executor.submit(deleteTask);

        latch.countDown();
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        assertEquals("Two delete attempts should have been recorded", 2, statuses.size());
        assertTrue(statuses.stream().allMatch(code -> code == 200 || code == 204 || code == 404));
    }
}
