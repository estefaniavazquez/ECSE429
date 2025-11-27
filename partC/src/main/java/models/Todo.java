package models;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Todo {

    private String id;
    private String title;
    private String doneStatus;
    private String description;
    

    public Todo() {}

    public Todo(String title, String description) {
        this.title = title;
        this.description = description;
        this.doneStatus = "false";
    }

    public Map<String, Object> toPayloadMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("description", description);
        map.put("doneStatus", doneStatus);
        return map;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDoneStatus() { return doneStatus; }
}
