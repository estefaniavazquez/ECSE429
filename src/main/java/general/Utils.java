package general;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.util.List;

import static general.CommonConstants.*;

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

    public static HttpURLConnection requestPATCH(String endpoint, String acceptType, String contentType) throws Exception {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(POST_METHOD);
        connection.setRequestProperty("Accept", acceptType);
        connection.setRequestProperty("Content-Type", contentType);
        connection.setRequestProperty("X-HTTP-Method-Override", PATCH_METHOD);
        connection.setDoOutput(true);

        return connection;
    }

    public static HttpURLConnection requestWithId(String endpoint, String method, String acceptType, String contentType, String id, String body) throws Exception {
        String newEndpoint = endpoint + "/" + id;

        return request(newEndpoint, method, acceptType, contentType, body);
    }

    public static HttpURLConnection requestWithIdPATCH(String endpoint, String acceptType, String contentType, String id) throws Exception {
        String newEndpoint = endpoint + "/" + id;

        return requestPATCH(newEndpoint, acceptType, contentType);
    }

    public static HttpURLConnection requestWithIdAndQueryParams(String endpoint, String method, String acceptType, String contentType, String id, String body, List<NameValuePair> queryParams) throws Exception {
        String newEndpoint = BASE_URL + endpoint + "/" + id;

        URI uri = new URIBuilder(newEndpoint).addParameters(queryParams).build();

        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();

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

    public static List<NameValuePair> createQueryParams(String title, String description) {
        List<NameValuePair> queryParams = new java.util.ArrayList<>();

        if (title != null && !title.isEmpty()) {
            queryParams.add(new BasicNameValuePair("title", title));
        }
        if (description != null && !description.isEmpty()) {
            queryParams.add(new BasicNameValuePair("description", description));
        }

        return queryParams;
    }
}
