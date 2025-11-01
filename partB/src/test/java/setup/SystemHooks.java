package setup;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.nio.file.Paths;

public class SystemHooks {
    private static final String BASE_URL = "http://localhost:4567";
    public static final String PATH_TO_SERVER_JAR =
            Paths.get(System.getProperty("user.dir"), "lib", "runTodoManagerRestAPI-1.5.5.jar").toString();

    private static Process serverProcess;

    @Before
    public void startServer() throws Exception {
        // Check if server auto-start is disabled
        String autoStart = System.getProperty("autoStartServer", "true"); // default: true
        if (autoStart.equalsIgnoreCase("false")) {
            System.out.println("[INFO] Skipping automatic server startup (manual mode enabled).");
            return;
        }

        serverProcess = new ProcessBuilder("java", "-jar", PATH_TO_SERVER_JAR).start();

        // Wait for the server to start responding
        for (int attempt = 0; attempt < 10; attempt++) {
            try {
                Thread.sleep(500);
                Response response = RestAssured.get(BASE_URL + "/todos");
                if (response.getStatusCode() == 200) {
                    return;
                }
            } catch (Exception e) {
                System.out.println("Waiting for server to start...");
                Thread.sleep(1000);
            }
        }
    }

    @After
    public void stopServer() throws Exception {
        String autoStart = System.getProperty("autoStartServer", "true");
        if (autoStart.equalsIgnoreCase("false")) {
            System.out.println("[INFO] Manual mode active — skipping server shutdown.");
            return;
        }

        if (serverProcess != null) {
            serverProcess.destroy();
            serverProcess.waitFor();
        }
    }
}
