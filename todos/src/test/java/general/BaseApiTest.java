package general;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.After;
import org.junit.Before;

import static general.CommonConstants.BASE_URL;
import static general.CommonConstants.PATH_TO_SERVER_JAR;

// base test starts/stops server
public abstract class BaseApiTest {
    private static Process serverProcess;

    @Before
    public void startServer() throws Exception {
        serverProcess = new ProcessBuilder("java", "-jar", PATH_TO_SERVER_JAR).start();
        String url = BASE_URL + "docs";

        for (int i = 0; i < 10; i++) {
            try {
                HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
                Thread.sleep(500);
                if (c.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    c.disconnect();
                    break;
                }
            } catch (Exception e) {
                Thread.sleep(1000);
            }
        }
    }

    @After
    public void stopServer() throws Exception {
        if (serverProcess != null) {
            serverProcess.destroy();
            serverProcess.waitFor();
        }
    }
}
