package general;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;

import static general.CommonConstants.BASE_URL;
import static general.CommonConstants.PATCH_METHOD;
import static general.CommonConstants.POST_METHOD;

// http helpers for tests
public class Utils {

    // normal request
    public static HttpURLConnection request(String endpoint, String method, String accept, String content, String body) throws Exception {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setRequestMethod(method);
        c.setRequestProperty("Accept", accept);
        c.setRequestProperty("Content-Type", content);
        c.setDoOutput(true);
        if (body != null && !body.isEmpty()) {
            c.setDoInput(true);
            c.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
        }
        return c;
    }

    // patch request (post+override)
    public static HttpURLConnection requestPATCH(String endpoint, String accept, String content) throws Exception {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setRequestMethod(POST_METHOD);
        c.setRequestProperty("Accept", accept);
        c.setRequestProperty("Content-Type", content);
        c.setRequestProperty("X-HTTP-Method-Override", PATCH_METHOD);
        c.setDoOutput(true);
        return c;
    }

    // request /endpoint/id
    public static HttpURLConnection requestWithId(String endpoint, String method, String accept, String content, String id, String body) throws Exception {
        return request(endpoint + "/" + id, method, accept, content, body);
    }

    // patch /endpoint/id
    public static HttpURLConnection requestWithIdPATCH(String endpoint, String accept, String content, String id) throws Exception {
        return requestPATCH(endpoint + "/" + id, accept, content);
    }

    // /endpoint/id?params
    public static HttpURLConnection requestWithIdAndQueryParams(String endpoint, String method, String accept, String content, String id, String body, List<NameValuePair> params) throws Exception {
        String base = BASE_URL + endpoint + "/" + id;
        URI uri = new URIBuilder(base).addParameters(params).build();
        HttpURLConnection c = (HttpURLConnection) uri.toURL().openConnection();
        c.setRequestMethod(method);
        c.setRequestProperty("Accept", accept);
        c.setRequestProperty("Content-Type", content);
        c.setDoOutput(true);
        if (body != null && !body.isEmpty()) {
            c.setDoInput(true);
            c.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
        }
        return c;
    }

    // read response from server
    public static String readResponse(HttpURLConnection c) throws Exception {
        StringBuilder sb = new StringBuilder();
        BufferedReader r;
        if (c.getErrorStream() != null) {
            r = new BufferedReader(new InputStreamReader(c.getErrorStream(), StandardCharsets.UTF_8));
        } else {
            r = new BufferedReader(new InputStreamReader(c.getInputStream(), StandardCharsets.UTF_8));
        }
        String line;
        while ((line = r.readLine()) != null) sb.append(line);
        return sb.toString();
    }

    // build query params
    public static List<NameValuePair> createQueryParams(String title, String desc) {
        List<NameValuePair> params = new java.util.ArrayList<>();
        if (title != null && !title.isEmpty()) params.add(new BasicNameValuePair("title", title));
        if (desc != null && !desc.isEmpty()) params.add(new BasicNameValuePair("description", desc));
        return params;
    }
}
