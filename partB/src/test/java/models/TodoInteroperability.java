package models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class TodoInteroperability {
    private String id;
    private String title;
    private String description;
    private Boolean doneStatus;
    private List<Relationship> tasksof;
    private List<Relationship> categories;

    public TodoInteroperability() {}

    public TodoInteroperability(String title, String description, String doneStatus) {
        this.title = title;
        this.description = description;

        if (doneStatus != null && !doneStatus.isEmpty()) {
            this.doneStatus = Boolean.parseBoolean(doneStatus);
        } else {
            this.doneStatus = null;
        }

        // Relationships initialized as null
        this.tasksof = null;
        this.categories = null;
    }

    public TodoInteroperability(String id, String title, String description, String doneStatus, List<Relationship> tasksof, List<Relationship> categories) {
        this.id = id;
        this.title = title;
        this.description = description;

        if (doneStatus != null && !doneStatus.isEmpty()) {
            this.doneStatus = Boolean.parseBoolean(doneStatus);
        }
        else {
            this.doneStatus = null;
        }

        this.tasksof = tasksof;
        this.categories = categories;
    }

    public Map<String, Object> toPayloadMap() {
        Map<String, Object> payload = new HashMap<>();
        
        if (title != null && !title.isEmpty()) {
            payload.put("title", title);
        }
        
        if (description != null && !description.isEmpty()) {
            payload.put("description", description);
        }

        if (this.doneStatus != null) {
            payload.put("doneStatus", this.doneStatus); 
        }

        if (this.tasksof != null) {
            payload.put("tasksof", this.tasksof);
        }

        if (this.categories != null) {
            payload.put("categories", this.categories);
        }
        
        return payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TodoInteroperability that = (TodoInteroperability) o;

        return Objects.equals(id, that.id) &&
               Objects.equals(title, that.title) &&
               Objects.equals(description, that.description) &&
               Objects.equals(doneStatus, that.doneStatus) &&
               Objects.equals(tasksof, that.tasksof) &&
               Objects.equals(categories, that.categories);
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Boolean getDoneStatus() { return doneStatus; }
    public List<Relationship> getTasksof() { return tasksof; }
    public List<Relationship> getCategories() { return categories; }
}
