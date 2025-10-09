package general;

import categories.Category;
import projects.Project;

public class CommonConstants {
    public static final String BASE_URL = "http://localhost:4567/";
    public static final String PATH_TO_SERVER_JAR = "src/lib/runTodoManagerRestAPI-1.5.5.jar";

    public static final String CATEGORIES_ENDPOINT = "categories";
    public static final String PROJECTS_ENDPOINT = "projects";

    public static final String JSON_FORMAT = "application/json";
    public static final String XML_FORMAT = "application/xml";

    public static final String GET_METHOD = "GET";
    public static final String HEAD_METHOD = "HEAD";
    public static final String POST_METHOD = "POST";
    public static final String PUT_METHOD = "PUT";
    public static final String DELETE_METHOD = "DELETE";
    public static final String PATCH_METHOD = "PATCH";
    public static final String OPTIONS_METHOD = "OPTIONS";

    public static final String CATEGORIES_MISSING_TITLE_JSON = "{\"errorMessages\":[\"title : field is mandatory\"]}";
    public static final String CATEGORIES_MISSING_TITLE_XML = "<errorMessages><errorMessage>title : field is mandatory</errorMessage></errorMessages>";
    public static final String CATEGORIES_EMPTY_TITLE_JSON = "{\"errorMessages\":[\"Failed Validation: title : can not be empty\"]}";
    public static final String CATEGORIES_EMPTY_TITLE_XML = "<errorMessages><errorMessage>Failed Validation: title : can not be empty</errorMessage></errorMessages>";
    public static final String CATEGORIES_INEXISTENT_ID_JSON_PREFIX = "{\"errorMessages\":[\"Could not find an instance with categories/";
    public static final String CATEGORIES_INEXISTENT_ID_JSON_SUFFIX = "\"]}";
    public static final String CATEGORIES_INEXISTENT_ID_XML_PREFIX = "<errorMessages><errorMessage>Could not find an instance with categories/";
    public static final String CATEGORIES_INEXISTENT_ID_XML_SUFFIX = "</errorMessage></errorMessages>";
    public static final String CATEGORIES_DELETE_INEXISTENT_ID_JSON = "{\"errorMessages\":[\"Could not find any instances with categories/";
    public static final String CATEGORIES_DELETE_INEXISTENT_ID_XML = "<errorMessages><errorMessage>Could not find any instances with categories/";

    public static final String CATEGORY_OPTIONS = "OPTIONS, GET, HEAD, POST";
    public static final String CATEGORY_ID_OPTIONS = "OPTIONS, GET, HEAD, POST, PUT, DELETE";

    public static final Category officeCategory = new Category("1", "Office", "");
    public static final Category homeCategory = new Category("2", "Home", "");
    public static final Category[] defaultCategories = { officeCategory, homeCategory };

    public static final Project defaultProject = new Project("1", "Office Work", false, false, "");
    public static final String PROJECT_OPTIONS = "OPTIONS, GET, HEAD, POST";
    public static final String PROJECT_ID_OPTIONS = "OPTIONS, GET, HEAD, POST, PUT, DELETE";

    // Interoperability endpoints
    public static final String TODOS_ENDPOINT = "todos";
    public static final String TODOS_EMPTY_TITLE_JSON = "{\"errorMessages\":[\"Failed Validation: title : can not be empty\"]}";
    public static final String TODOS_OPTIONS = "OPTIONS, GET, HEAD, POST";
    public static final String TODOS_MISSING_TITLE_JSON = "{\"errorMessages\":[\"title : field is mandatory\"]}";
    public static final String TODOS_TASKSOF_ENDPOINT = "todos/%s/tasksof";
    public static final String TODOS_CATEGORIES_ENDPOINT = "todos/%s/categories";
    public static final String PROJECTS_CATEGORIES_ENDPOINT = "projects/%s/categories";
    public static final String PROJECTS_TASKS_ENDPOINT = "projects/%s/tasks";
    public static final String CATEGORIES_PROJECTS_ENDPOINT = "categories/%s/projects";
    public static final String CATEGORIES_TODOS_ENDPOINT = "categories/%s/todos";

    // Relationship deletion endpoints
    public static final String TODOS_TASKSOF_ID_ENDPOINT = "todos/%s/tasksof/%s";
    public static final String TODOS_CATEGORIES_ID_ENDPOINT = "todos/%s/categories/%s";
    public static final String PROJECTS_CATEGORIES_ID_ENDPOINT = "projects/%s/categories/%s";
    public static final String PROJECTS_TASKS_ID_ENDPOINT = "projects/%s/tasks/%s";
    public static final String CATEGORIES_PROJECTS_ID_ENDPOINT = "categories/%s/projects/%s";
    public static final String CATEGORIES_TODOS_ID_ENDPOINT = "categories/%s/todos/%s";

    // OPTIONS responses for interoperability endpoints
    public static final String TODOS_TASKSOF_OPTIONS = "OPTIONS, GET, HEAD, POST";
    public static final String TODOS_CATEGORIES_OPTIONS = "OPTIONS, GET, HEAD, POST";
    public static final String PROJECTS_CATEGORIES_OPTIONS = "OPTIONS, GET, HEAD, POST";
    public static final String PROJECTS_TASKS_OPTIONS = "OPTIONS, GET, HEAD, POST";
    public static final String CATEGORIES_PROJECTS_OPTIONS = "OPTIONS, GET, HEAD, POST";
    public static final String CATEGORIES_TODOS_OPTIONS = "OPTIONS, GET, HEAD, POST";

    public static final String RELATIONSHIP_ID_OPTIONS = "OPTIONS, DELETE";
}
