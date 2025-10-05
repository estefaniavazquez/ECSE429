package projects;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Project {
    private String id;
    private String title;
    private boolean completed;
    private boolean active;
    private String description;

    public Project() {
    }

    public Project(String id, String title, boolean completed, boolean active, String description) {
        this.id = id;
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

    public boolean isCompleted() {
        return completed;
    }

    public boolean isActive() {
        return active;
    }

    public String getDescription() {
        return description;
    }

    public String toStringJson() {
        return "{\"id\":\"" + id + "\",\"title\":\"" + title + "\",\"completed\":" + completed + ",\"active\":" + active
                + ",\"description\":\"" + description + "\"}";
    }

    public String toStringXml() {
        return "<project><id>" + id + "</id><title>" + title + "</title><completed>" + completed
                + "</completed><active>" + active + "</active><description>" + description + "</description></project>";
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

    @JacksonXmlRootElement(localName = "project")
    public static class ProjectBody {
        private String title;
        private boolean completed;
        private boolean active;
        private String description;

        public ProjectBody() {
        }

        public ProjectBody(String title, boolean completed, boolean active, String description) {
            this.title = title;
            this.completed = completed;
            this.active = active;
            this.description = description;
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

        public boolean bodySameAsProject(Project project) {
            return title.equals(project.getTitle()) && completed == project.isCompleted() &&
                    active == project.isActive() && description.equals(project.getDescription());
        }
    }
}
