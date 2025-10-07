package categories;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

public class Category {
    private String id;
    private String title;
    private String description;

    public Category() {}

    public Category(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String toStringJson() {
        return "{\"id\":\"" + id + "\",\"title\":\"" + title + "\",\"description\":\"" + description + "\"}";
    }

    public String toStringXml() {
        return "<category><id>" + id + "</id><title>" + title + "</title><description>" + description + "</description></category>";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Category category = (Category) obj;
        return id.equals(category.id) && title.equals(category.title) && description.equals(category.description);
    }

    @JacksonXmlRootElement(localName = "category")
    public static class CategoryBody {
        private String title;
        private String description;

        public CategoryBody() {}

        public CategoryBody(String title, String description) {
            this.title = title;
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public boolean bodySameAsCategory(Category category) {
            return this.title.equals(category.title) && this.description.equals(category.description);
        }
    }
}
