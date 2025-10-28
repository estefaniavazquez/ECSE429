package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectInteroperability {
    private String id;
    private String title;
    private String description;
    private Boolean completed;
    private Boolean active;
    private List<Relationship> tasks;

    public ProjectInteroperability() { }

    public ProjectInteroperability(String title, String description, String completedString, String activeString) {
        this.title = title;
        this.description = description;

        // Conversion from Gherkin String to Java Boolean object
        if (completedString != null && !completedString.isEmpty()) {
            this.completed = Boolean.parseBoolean(completedString);
        } else {
            this.completed = null;
        }

        if (activeString != null && !activeString.isEmpty()) {
            this.active = Boolean.parseBoolean(activeString);
        } else {
            this.active = null;
        }

        // Relationships initialized as null
        this.tasks = null;
    }

    public Map<String, Object> toPayloadMap() {
        Map<String, Object> payload = new HashMap<>();

        if (title != null && !title.isEmpty()) {
            payload.put("title", title);
        }

        if (description != null && !description.isEmpty()) {
            payload.put("description", description);
        }

        if (this.completed != null) {
            payload.put("completed", this.completed);
        }

        if (this.active != null) {
            payload.put("active", this.active);
        }

        if (this.tasks != null) {
            payload.put("tasks", this.tasks);
        }

        return payload;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Boolean getCompleted() { return completed; }
    public Boolean getActive() { return active; }
    public List<Relationship> getTasks() { return tasks; }

    // Add tasks relationship
    public void setTasks(Relationship tasks) {
        if (this.tasks == null) {
            this.tasks = new ArrayList<>();
        }
        this.tasks.add(tasks);
    }
}
