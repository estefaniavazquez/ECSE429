package todos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// quick wrapper for json todos resp
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonTodo {

    @JsonProperty("todos")
    private Todo[] todos; // array of todos

    // getter lol
    public Todo[] getTodos() {
        return todos;
    }
}
