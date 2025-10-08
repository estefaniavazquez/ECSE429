package projects;

// This class is used to map the JSON array of projects, which is commonly returned by the API as a response
public class JsonProject {
    private Project[] projects;

    // Required for Jackson deserialization
    public JsonProject() {
    }

    public JsonProject(Project[] projects) {
        this.projects = projects;
    }

    public Project[] getProjects() {
        return projects;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"projects\":[");
        for (int i = 0; i < projects.length; i++) {
            sb.append(projects[i].toStringJson());
            if (i < projects.length - 1) {
                sb.append(",");
            }
        }
        sb.append("]}");
        return sb.toString();
    }

    // Check if a specific project is in the array of projects that the JsonProject
    // object returned
    public boolean contains(Project project) {
        for (Project p : projects) {
            if (p.equals(project)) {
                return true;
            }
        }
        return false;
    }

    // Check if all projects in the given array are in the array of projects that
    // the JsonProject object returned
    public boolean containsAll(Project[] projects) {
        for (Project project : projects) {
            if (!contains(project)) {
                return false;
            }
        }
        return true;
    }

    public int size() {
        return projects != null ? projects.length : 0;
    }
}
