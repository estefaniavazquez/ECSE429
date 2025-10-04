package general;

import categories.Category;

public class CommonConstants {
    public static final String BASE_URL = "http://localhost:4567/";
    public static final String PATH_TO_SERVER_JAR = "lib/runTodoManagerRestAPI-1.5.5.jar";

    public static final String CATEGORIES_ENDPOINT = "categories";

    public static final String JSON_FORMAT = "application/json";
    public static final String XML_FORMAT = "application/xml";

    public static final String GET_METHOD = "GET";
    public static final String HEAD_METHOD = "HEAD";
    public static final String POST_METHOD = "POST";
    public static final String PUT_METHOD = "PUT";
    public static final String DELETE_METHOD = "DELETE";
    public static final String PATCH_METHOD = "PATCH";
    public static final String OPTIONS_METHOD = "OPTIONS";

    public static final String[] CATEGORY_OPTIONS = {"GET", "HEAD", "POST", "OPTIONS"};

    public static final Category officeCategory = new Category("1", "Office", "");
    public static final Category homeCategory = new Category("2", "Home", "");
    public static final Category[] defaultCategories = {officeCategory, homeCategory};
}
