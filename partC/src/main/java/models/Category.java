package models;

import java.util.HashMap;
import java.util.Map;

public class Category {
    private String id;
    private String title;
    private String description;

    public Category() {}

    // make new with title and description
    public Category(String title, String description) {
        this.title = title;
        this.description = description;
    }

    // make new with id, title and description --> Used for default categories
    public Category(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    // only add fields if not empty
    public Map<String, Object> toPayloadMap() {
        Map<String, Object> m = new HashMap<>();
        if (title != null && !title.isEmpty()) m.put("title", title);
        if (description != null && !description.isEmpty()) m.put("description", description);
        return m;
    }

    // get id
    public String getId() { return id; }
    // get title
    public String getTitle() { return title; }
    // get description
    public String getDescription() { return description; }
}
