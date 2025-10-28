package models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TodoInteroperability {
    private String id;
    private String title;
    private String description;
    private Boolean doneStatus;
    private List<Relationship> taskof;
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
        this.taskof = null;
        this.categories = null;
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

        if (this.taskof != null) {
            payload.put("taskof", this.taskof);
        }

        if (this.categories != null) {
            payload.put("categories", this.categories);
        }
        
        return payload;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Boolean getDoneStatus() { return doneStatus; }
    public List<Relationship> getTaskof() { return taskof; }
    public List<Relationship> getCategories() { return categories; }
}
