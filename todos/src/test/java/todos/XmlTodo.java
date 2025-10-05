package todos;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

// xml wrapper for todos
@JacksonXmlRootElement(localName = "todos")
public class XmlTodo {

    @JacksonXmlProperty(localName = "todo")
    @JacksonXmlElementWrapper(useWrapping = false)
    private Todo[] todos; // list of todos

    // getter
    public Todo[] getTodos() {
        return todos;
    }
}
