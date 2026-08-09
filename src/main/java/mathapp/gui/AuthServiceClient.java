package mathapp.gui;

import java.io.IOException;

import mathapp.logging.MathLogger;

public class AuthServiceClient {

    private final ApiClient apiClient;

    public AuthServiceClient(String baseUrl) {
        this.apiClient = new ApiClient(baseUrl);
    }

    public AuthState login(String email, String password) throws IOException, InterruptedException {
        MathLogger.operationStarted("AUTH_LOGIN", email);
        String payload = "{\"email\":" + jsonString(email) + ",\"password\":" + jsonString(password) + "}";
        String body = apiClient.postJson("/api/auth/login", payload, null);

        AuthState state = new AuthState();

        state.setToken(JacksonLite.extractString(body, "token"));
        state.setUsername(JacksonLite.extractString(body, "username"));
        state.setEmail(JacksonLite.extractString(body, "email"));

        MathLogger.operationSuccess("AUTH_LOGIN", "success for " + email);
        return state;
    }

    public AuthState register(String email, String username, String password) throws IOException, InterruptedException {
        MathLogger.operationStarted("AUTH_REGISTER", email);
        String payload = "{\"email\":" + jsonString(email) + ",\"username\":" + jsonString(username) + ",\"password\":" + jsonString(password) + "}";
        String body = apiClient.postJson("/api/auth/register", payload, null);

        AuthState state = new AuthState();
        state.setToken(JacksonLite.extractString(body, "token"));
        state.setUsername(JacksonLite.extractString(body, "username"));
        state.setEmail(JacksonLite.extractString(body, "email"));

        MathLogger.operationSuccess("AUTH_REGISTER", "success for " + email);
        return state;
    }

    public void forgotPassword(String email) throws IOException, InterruptedException {
        MathLogger.operationStarted("AUTH_FORGOT_PASSWORD", email);
        String payload = "{\"email\":" + jsonString(email) + "}";
        apiClient.postJson("/api/auth/forgot-password", payload, null);
        MathLogger.operationSuccess("AUTH_FORGOT_PASSWORD", "request sent for " + email);
    }

    public void logout(String token) {
        // Stateless JWT: token is discarded by client.
        MathLogger.operationStarted("AUTH_LOGOUT", null);
        MathLogger.operationSuccess("AUTH_LOGOUT", "token discarded by client");
    }

    private static String jsonString(String s) {
        if (s == null) return "null";
        String escaped = s.replace("\\", "\\\\").replace("\"", "\\\"");
        return "\"" + escaped + "\"";
    }
}

