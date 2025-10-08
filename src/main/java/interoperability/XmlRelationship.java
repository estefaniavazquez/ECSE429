package interoperability;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "project")
public class XmlRelationship {
    private String id;

    public XmlRelationship() {
    }

    public XmlRelationship(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String toStringXml() {
        return "<project><id>" + id + "</id></project>";
    }

    public String toStringXmlCategory() {
        return "<category><id>" + id + "</id></category>";
    }

    public String toStringXmlTodo() {
        return "<todo><id>" + id + "</id></todo>";
    }
}
