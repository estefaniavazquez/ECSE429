package models;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Project {
    private String id;
    private String title;
    private boolean completed;
    private boolean active;
    private String description;

    public Project() {
    }

    // constructor with none of the boolean fields
    public Project(String title, String description) {
        this.title = title;
        this.completed = false;
        this.active = true;
        this.description = description;
    }

    // default constructor with id
    public Project(String id, String title, boolean completed, boolean active, String description) {
        this.id = id;
        this.title = title;
        this.completed = completed;
        this.active = active;
        this.description = description;
    }

    // make new without id
    public Project(String title, boolean completed, boolean active, String description) {
        this.title = title;
        this.completed = completed;
        this.active = active;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String setId(String id) {
        this.id = id;
        return this.id;
    }

    public String getTitle() {
        return title;
    }

    public boolean getCompleted() {
        return completed;
    }

    public boolean getActive() {
        return active;
    }

    public String getDescription() {
        return description;
    }

    public Map<String, Object> toPayloadMap() {
        Map<String, Object> projectMap = new LinkedHashMap<>();
        projectMap.put("title", title);
        projectMap.put("completed", completed);
        projectMap.put("active", active);
        projectMap.put("description", description);
        return projectMap;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Project project = (Project) obj;
        return id.equals(project.id) && title.equals(project.title) && completed == project.completed
                && active == project.active && description.equals(project.description);
    }


}
