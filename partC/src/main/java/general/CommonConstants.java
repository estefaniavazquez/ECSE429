package general;

import java.nio.file.Paths;

public class CommonConstants {
    public static final String BASE_URL = "http://localhost:4567/";
    public static final String PATH_TO_SERVER_JAR = Paths.get(System.getProperty("user.dir"), "lib", "runTodoManagerRestAPI-1.5.5.jar").toString();
    public static final String TODOS_ENDPOINT = "todos";
    public static final String CATEGORIES_ENDPOINT = "categories";
    public static final String PROJECTS_ENDPOINT = "projects";

    public static final String JSON_FORMAT = "application/json";

    public static final String POST_METHOD = "POST";
    public static final String PUT_METHOD = "PUT";
    public static final String DELETE_METHOD = "DELETE";

    public static final int[] NUM_OBJECTS_FOR_PERFORMANCE_TESTING = { 1, 5, 10, 50, 100, 500, 1000, 5000, 10000, 50000};
    public static final int MAX_NUM_OBJECTS_FOR_PERFORMANCE_TESTING = 50000;
}
