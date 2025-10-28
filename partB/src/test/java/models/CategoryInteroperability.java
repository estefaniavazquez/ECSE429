package models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryInteroperability {
    private String id;
    private String title;
    private String description;
    private List<Relationship> todos;
    private List<Relationship> projects;

    public CategoryInteroperability() { }

    public CategoryInteroperability(String title, String description) {
        this.title = title;
        this.description = description;

        // Relationships initialized as null
        this.todos = null;
        this.projects = null;
    }

    public Map<String, Object> toPayloadMap() {
        Map<String, Object> payload = new HashMap<>();

        if (title != null && !title.isEmpty()) {
            payload.put("title", title);
        }

        if (description != null && !description.isEmpty()) {
            payload.put("description", description);
        }

        if (this.todos != null) {
            payload.put("todos", this.todos);
        }

        if (this.projects != null) {
            payload.put("projects", this.projects);
        }

        return payload;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public List<Relationship> getTodos() { return todos; }
    public List<Relationship> getProjects() { return projects; }
}
