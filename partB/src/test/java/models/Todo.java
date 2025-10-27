package models;

import java.util.HashMap;
import java.util.Map;

/**
 * doneStatus is stored as Boolean to ensure it serializes as a JSON primitive (true/false, unquoted).
 */
public class Todo {

    private String id;
    private String title;
    private String description;
    private Boolean doneStatus; 

    // Constructor used for creating request objects from Gherkin strings
    public Todo(String title, String description, String doneStatusString) {
        this.title = title;
        this.description = description;
        
        // Conversion from Gherkin String to Java Boolean object
        if (doneStatusString != null && !doneStatusString.isEmpty()) {
            // Converts "true", "TRUE", "false", "FALSE" (etc.) into Boolean.TRUE or Boolean.FALSE
            this.doneStatus = Boolean.parseBoolean(doneStatusString);
        } else {
            // Stores null if omitted (Alternate Flow)
            this.doneStatus = null; 
        }
    }

    public Todo() {}

    /**
     * Creates a Map suitable for JSON serialization, omitting fields that are null or empty.
     */
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
        
        return payload;
    }

    // Standard Getters (for assertions)
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Boolean getDoneStatus() { return doneStatus; }
}
