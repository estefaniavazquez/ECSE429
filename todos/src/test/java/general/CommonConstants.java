package general;

// shared stuff for todo tests
public class CommonConstants {

    // server
    public static final String BASE_URL = "http://localhost:4567/";
    public static final String PATH_TO_SERVER_JAR = "lib/runTodoManagerRestAPI-1.5.5.jar";

    // endpoints
    public static final String TODOS_ENDPOINT = "todos";

    // content types
    public static final String JSON_FORMAT = "application/json";
    public static final String XML_FORMAT  = "application/xml";

    // methods
    public static final String GET_METHOD = "GET";
    public static final String HEAD_METHOD = "HEAD";
    public static final String POST_METHOD = "POST";
    public static final String PUT_METHOD = "PUT";
    public static final String DELETE_METHOD = "DELETE";
    public static final String PATCH_METHOD = "PATCH";
    public static final String OPTIONS_METHOD = "OPTIONS";

    // options headers
    public static final String TODOS_OPTIONS = "OPTIONS, GET, HEAD, POST";
    public static final String TODOS_ID_OPTIONS = "OPTIONS, GET, HEAD, POST, PUT, DELETE";

    // errors
    public static final String TODOS_MISSING_TITLE_JSON =
        "{\"errorMessages\":[\"title : field is mandatory\"]}";
    public static final String TODOS_MISSING_TITLE_XML =
        "<errorMessages><errorMessage>title : field is mandatory</errorMessage></errorMessages>";

    public static final String TODOS_EMPTY_TITLE_JSON =
        "{\"errorMessages\":[\"Failed Validation: title : can not be empty\"]}";
    public static final String TODOS_EMPTY_TITLE_XML =
        "<errorMessages><errorMessage>Failed Validation: title : can not be empty</errorMessage></errorMessages>";

    public static final String TODOS_INEXISTENT_ID_JSON_PREFIX =
        "{\"errorMessages\":[\"Could not find an instance with todos/";
    public static final String TODOS_INEXISTENT_ID_JSON_SUFFIX = "\"]}";
    public static final String TODOS_INEXISTENT_ID_XML_PREFIX =
        "<errorMessages><errorMessage>Could not find an instance with todos/";
    public static final String TODOS_INEXISTENT_ID_XML_SUFFIX =
        "</errorMessage></errorMessages>";

    public static final String TODOS_DELETE_INEXISTENT_ID_JSON =
        "{\"errorMessages\":[\"Could not find any instances with todos/";
    public static final String TODOS_DELETE_INEXISTENT_ID_XML =
        "<errorMessages><errorMessage>Could not find any instances with todos/";
}
