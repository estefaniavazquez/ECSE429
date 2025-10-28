package models;

import java.util.HashMap;
import java.util.Map;

public class Category {
    private String id;
    private String title;
    private String description;

    public Category() {}

    // Convenience ctor for step table inputs
    public Category(String title, String description) {
        this.title = title;
        this.description = description;
    }

    /** Only include non-empty fields (so we can test “missing title” cases). */
    public Map<String, Object> toPayloadMap() {
        Map<String, Object> m = new HashMap<>();
        if (title != null && !title.isEmpty()) m.put("title", title);
        if (description != null && !description.isEmpty()) m.put("description", description);
        return m;
    }

    // Getters for assertions if needed
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
}
