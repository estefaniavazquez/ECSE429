package setup;

import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * ScenarioContext manages state and data sharing between Cucumber steps.
 */
public class ScenarioContext {

    private Response lastResponse;
    // Map to store IDs and other dynamic data (e.g., "todo_id", "project_id")
    private final Map<String, String> dataStore = new HashMap<>();

    public Response getLastResponse() {
        return lastResponse;
    }

    public void setLastResponse(Response response) {
        this.lastResponse = response;
    }

    public void storeId(String key, String id) {
        dataStore.put(key, id);
    }

    public String retrieveId(String key) {
        return dataStore.get(key);
    }
}
