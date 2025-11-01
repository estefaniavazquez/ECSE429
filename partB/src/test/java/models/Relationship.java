package models;

import java.util.HashMap;
import java.util.Map;

public class Relationship {
    private String id;

    public Relationship() {}

    public Relationship(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Relationship) && ((Relationship) o).id.equals(this.id);
    }

    public Map<String, Object> toPayloadMap() {
        Map<String, Object> payload = new HashMap<>();

        if (this.id != null) {
            payload.put("id", this.id);
        }

        return payload;
    }

    public String getId() { return id; }
}
