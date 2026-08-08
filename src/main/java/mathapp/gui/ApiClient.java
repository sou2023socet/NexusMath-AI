package mathapp.gui;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class ApiClient {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String baseUrl;

    public ApiClient(String baseUrl) {
        if (baseUrl == null || baseUrl.isBlank()) baseUrl = "http://localhost:8081";
        this.baseUrl = baseUrl;
    }

    public String postJson(String path, String json, String bearerToken) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8));

        if (bearerToken != null && !bearerToken.isBlank()) {
            builder.header("Authorization", "Bearer " + bearerToken);
        }

        HttpRequest request = builder.build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return response.body();
        }
        throw new IOException("HTTP " + response.statusCode() + ": " + response.body());
    }
}

