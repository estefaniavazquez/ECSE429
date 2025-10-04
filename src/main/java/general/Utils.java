package general;

import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;

import static general.CommonConstants.BASE_URL;

public class Utils {
    public static HttpURLConnection request(String endpoint, String method, String acceptType, String contentType, String body) throws Exception {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(method);
        connection.setRequestProperty("Accept", acceptType);
        connection.setRequestProperty("Content-Type", contentType);
        connection.setDoOutput(true);
        if (body != null && !body.isEmpty()) {
            connection.setDoInput(true);
            connection.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
        }

        return connection;
    }

    public static HttpURLConnection requestWithId(String endpoint, String method, String acceptType, String contentType, int id, String body) throws Exception {
        URL url = new URL(BASE_URL + endpoint + "/" + id);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(method);
        connection.setRequestProperty("Accept", acceptType);
        connection.setRequestProperty("Content-Type", contentType);
        connection.setDoOutput(true);
        if (body != null && !body.isEmpty()) {
            connection.setDoInput(true);
            connection.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
        }

        return connection;
    }

    public static String readResponse(HttpURLConnection connection) throws Exception {
        StringBuilder response = new StringBuilder();
        BufferedReader reader;

        if (connection.getErrorStream() != null) {
            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
        }
        else {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        }

        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        return response.toString();
    }
}
