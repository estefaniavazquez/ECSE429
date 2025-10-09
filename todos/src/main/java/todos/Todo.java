package todos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

// todo obj for json/xml
@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "todo")
public class Todo {

    @JsonProperty("id")
    private String id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("doneStatus")
    private String doneStatus;

    // getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDoneStatus() { return doneStatus; }

    // string formats
    public String toStringJson() {
        return "{\"id\":\"" + id + "\",\"title\":\"" + title + "\",\"description\":\"" + description + "\",\"doneStatus\":\"" + doneStatus + "\"}";
    }

    public String toStringXml() {
        return "<todo><id>" + id + "</id><title>" + title + "</title><description>" + description + "</description><doneStatus>" + doneStatus + "</doneStatus></todo>";
    }

    @Override
    public String toString() {
        return "Todo{id='" + id + "', title='" + title + "', description='" + description + "', doneStatus='" + doneStatus + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Todo t = (Todo) o;
        return id.equals(t.id) && title.equals(t.title) && description.equals(t.description);
    }

    // for post/put bodies
    public static class TodoBody {

        @JsonProperty("title")
        private String title;
        @JsonProperty("description")
        private String description;

        public TodoBody() {}

        public TodoBody(String title, String description) {
            this.title = title;
            this.description = description;
        }

        public String getTitle() { return title; }
        public String getDescription() { return description; }

        public boolean bodySameAs(Todo todo) {
            return todo.getTitle().equals(title) && todo.getDescription().equals(description);
        }
    }
}
