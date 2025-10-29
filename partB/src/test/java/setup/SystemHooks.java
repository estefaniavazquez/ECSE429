package setup;

import io.cucumber.java.After;
import io.cucumber.java.Before;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.nio.file.Paths;


public class SystemHooks {
    private static final String BASE_URL = "http://localhost:4567";
    public static final String PATH_TO_SERVER_JAR = Paths.get(System.getProperty("user.dir"), "lib", "runTodoManagerRestAPI-1.5.5.jar").toString();

    private static Process serverProcess;

    @Before
    public void startServer() throws Exception {
        System.out.println("=======================Starting server...");
        serverProcess = new ProcessBuilder("java", "-jar", PATH_TO_SERVER_JAR).start();

        for (int attempt = 0; attempt < 10; attempt++) {
            try {
                Thread.sleep(500);
                Response response = RestAssured.get(BASE_URL + "/todos");

                if (response.getStatusCode() == 200) {
                    System.out.println("Server is up and running.\n");
                    break;
                }
            } catch (Exception e) {
                System.out.println("Waiting for server to start...");
                Thread.sleep(1000);
            }
        }
    }

    @After
    public void stopServer() throws Exception {
        System.out.println("\nStopping server...");
        if (serverProcess != null) {
            serverProcess.destroy();
            serverProcess.waitFor();
            System.out.println("Server stopped.=======================\n");
        }
    }
}
