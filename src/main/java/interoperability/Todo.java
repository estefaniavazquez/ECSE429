package interoperability;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Todo {
    private String id;
    private String title;
    private String doneStatus;
    private String description;

    public Todo() {
    }

    public Todo(String id, String title, String doneStatus, String description) {
        this.id = id;
        this.title = title;
        this.doneStatus = doneStatus;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDoneStatus() {
        return doneStatus;
    }

    public String getDescription() {
        return description;
    }

    public String toStringJson() {
        return "{\"id\":\"" + id + "\",\"title\":\"" + title + "\",\"doneStatus\":\"" + doneStatus
                + "\",\"description\":\"" + description + "\"}";
    }

    public String toStringXml() {
        return "<todo><id>" + id + "</id><title>" + title + "</title><doneStatus>" + doneStatus
                + "</doneStatus><description>" + description + "</description></todo>";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Todo todo = (Todo) obj;
        return id.equals(todo.id) && title.equals(todo.title) &&
                doneStatus.equals(todo.doneStatus) && description.equals(todo.description);
    }

    @JacksonXmlRootElement(localName = "todo")
    public static class TodoBody {
        private String title;
        private boolean doneStatus;
        private String description;

        public TodoBody() {
        }

        public TodoBody(String title, boolean doneStatus, String description) {
            this.title = title;
            this.doneStatus = doneStatus;
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public boolean getDoneStatus() {
            return doneStatus;
        }

        public String getDescription() {
            return description;
        }

        public boolean bodySameAsTodo(Todo todo) {
            return this.title.equals(todo.title) &&
                    String.valueOf(this.doneStatus).equals(todo.doneStatus) &&
                    this.description.equals(todo.description);
        }
    }
}