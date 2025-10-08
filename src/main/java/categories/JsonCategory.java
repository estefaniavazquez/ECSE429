package categories;

public class JsonCategory {
    private Category[] categories;

    public JsonCategory() {
    }

    public JsonCategory(Category[] categories) {
        this.categories = categories;
    }

    public Category[] getCategories() {
        return categories.clone();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"categories\":[");
        for (int i = 0; i < categories.length; i++) {
            sb.append(categories[i].toString());
            if (i < categories.length - 1) {
                sb.append(",");
            }
        }
        sb.append("]}");

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

    public int size() {
        return categories != null ? categories.length : 0;
    }
}
