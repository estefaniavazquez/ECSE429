package models;

import java.util.HashMap;
import java.util.Map;

public class Projects {
    private String id;
    private String title;
    private boolean completed;
    private boolean active;
    private String description;

    public Projects(String id, String title, String completed, String active, String description) {
        this.id = id;
        this.title = title;
        this.description = description;

        if (completed != null && !completed.isEmpty()) {
            this.completed = Boolean.parseBoolean(completed);
        } else {
            this.completed = false;
        }

        if (active != null && !active.isEmpty()) {
            this.active = Boolean.parseBoolean(active);
        } else {
            this.active = false;
        }
    }

    public Map<String, Object> toPayloadMap() {
        Map<String, Object> payload = new HashMap<>();

        if (title != null && !title.isEmpty()) {
            payload.put("title", title);
        }

        payload.put("completed", completed);
        payload.put("active", active);

        if (description != null && !description.isEmpty()) {
            payload.put("description", description);
        }

        return payload;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public boolean isActive() {
        return active;
    }

    public String getDescription() {
        return description;
    }
}
