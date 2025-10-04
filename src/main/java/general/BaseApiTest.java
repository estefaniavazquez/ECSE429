package general;

import org.junit.Before;
import org.junit.After;

import java.net.HttpURLConnection;
import java.net.URL;

import static general.CommonConstants.BASE_URL;
import static general.CommonConstants.PATH_TO_SERVER_JAR;

public abstract class BaseApiTest {
    private static Process serverProcess;

    @Before
    public void startServer() throws Exception {
        System.out.println("=======================Starting server...");
        serverProcess = new ProcessBuilder("java", "-jar", PATH_TO_SERVER_JAR).start();

        String endpointToTestConnection = BASE_URL + "docs";
        for (int attempt = 0; attempt < 10; attempt++) {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(endpointToTestConnection).openConnection();
                Thread.sleep(500);
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    connection.disconnect();
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
            System.out.println("Server stopped.=======================");
        }
    }
}