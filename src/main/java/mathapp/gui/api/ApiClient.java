package mathapp.gui.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import mathapp.logging.MathLogger;

public class ApiClient {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private final String baseUrl;

    public ApiClient() {
        this(configuredBaseUrl());
    }

    public ApiClient(String baseUrl) {
        String configuredUrl = baseUrl == null || baseUrl.isBlank() ? configuredBaseUrl() : baseUrl;
        this.baseUrl = configuredUrl.replaceAll("/+$", "");
    }

    public static String configuredBaseUrl() {
        String systemProperty = System.getProperty("mathapp.api.base-url");
        if (systemProperty != null && !systemProperty.isBlank()) {
            return systemProperty.trim();
        }
        String environmentVariable = System.getenv("MATHAPP_API_BASE_URL");
        return environmentVariable == null || environmentVariable.isBlank()
                ? "http://localhost:8081"
                : environmentVariable.trim();
    }

    public String postJson(String path, String json, String bearerToken) throws IOException, InterruptedException {
        MathLogger.apiCall("POST", path);
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .timeout(Duration.ofSeconds(15))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8));

        if (bearerToken != null && !bearerToken.isBlank()) {
            builder.header("Authorization", "Bearer " + bearerToken);
        }

        HttpRequest request = builder.build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            MathLogger.apiSuccess("POST", path, response.statusCode());
            return response.body();
        }
        MathLogger.apiError("POST", path, response.statusCode(), response.body());
        throw new IOException("HTTP " + response.statusCode() + ": " + response.body());
    }
}
