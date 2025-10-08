package interoperability;

public class JsonRelationship {
    private String id;

    public JsonRelationship() {
    }

    public JsonRelationship(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String toStringJson() {
        return "{\"id\":\"" + id + "\"}";
    }
}