package mathapp.gui;

import java.io.IOException;

public class AuthServiceClient {

    private final ApiClient apiClient;

    public AuthServiceClient(String baseUrl) {
        this.apiClient = new ApiClient(baseUrl);
    }

    public AuthState login(String email, String password) throws IOException, InterruptedException {
        String payload = "{\"email\":" + jsonString(email) + ",\"password\":" + jsonString(password) + "}";
        String body = apiClient.postJson("/api/auth/login", payload, null);

        AuthState state = new AuthState();

        state.setToken(JacksonLite.extractString(body, "token"));
        state.setUsername(JacksonLite.extractString(body, "username"));
        state.setEmail(JacksonLite.extractString(body, "email"));
        return state;
    }

    public AuthState register(String email, String username, String password) throws IOException, InterruptedException {
        String payload = "{\"email\":" + jsonString(email) + ",\"username\":" + jsonString(username) + ",\"password\":" + jsonString(password) + "}";
        String body = apiClient.postJson("/api/auth/register", payload, null);

        AuthState state = new AuthState();
        state.setToken(JacksonLite.extractString(body, "token"));
        state.setUsername(JacksonLite.extractString(body, "username"));
        state.setEmail(JacksonLite.extractString(body, "email"));
        return state;
    }

    public void forgotPassword(String email) throws IOException, InterruptedException {
        String payload = "{\"email\":" + jsonString(email) + "}";
        apiClient.postJson("/api/auth/forgot-password", payload, null);
    }

    public void logout(String token) {
        // Stateless JWT: token is discarded by client.
    }

    private static String jsonString(String s) {
        if (s == null) return "null";
        String escaped = s.replace("\\", "\\\\").replace("\"", "\\\"");
        return "\"" + escaped + "\"";
    }
}

