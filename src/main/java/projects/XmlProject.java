package projects;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "projects")
public class XmlProject {
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "project")
    private Project[] projects;

    public XmlProject() {}

    public XmlProject(Project[] projects) {
        this.projects = projects;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<projects>");
        for (Project project : projects) {
            sb.append(project.toStringXml());
        }
        sb.append("</projects>");

        return sb.toString();
    }

    public boolean contains(Project project) {
        for (Project p : projects) {
            if (p.equals(project)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsAll(Project[] projects) {
        for (Project project : projects) {
            if (!contains(project)) {
                return false;
            }
        }
        return true;
    }
}
