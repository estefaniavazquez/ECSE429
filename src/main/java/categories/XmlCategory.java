package categories;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "categories")
public class XmlCategory {
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "category")
    private Category[] categories;

    public XmlCategory() {}

    public XmlCategory(Category[] categories) {
        this.categories = categories;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<categories>");
        for (Category category : categories) {
            sb.append(category.toStringXml());
        }
        sb.append("</categories>");

        return sb.toString();
    }

    public boolean isIn(Category category) {
        for (Category c : categories) {
            if (c.equals(category)) {
                return true;
            }
        }

        return false;
    }

    public boolean areIn(Category[] categories) {
        for (Category category : categories) {
            if (!isIn(category)) {
                return false;
            }
        }

        return true;
    }

    public boolean contains(Category category) {
        for (Category c : categories) {
            if (c.equals(category)) {
                return true;
            }
        }

        return false;
    }

    public boolean containsAll(Category[] categories) {
        for (Category category : categories) {
            if (!contains(category)) {
                return false;
            }
        }

        return true;
    }
}
