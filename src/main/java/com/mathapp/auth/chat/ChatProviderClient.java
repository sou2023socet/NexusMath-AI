package com.mathapp.auth.chat;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class ChatProviderClient {
    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/";

    private final ChatAiProperties properties;
    private final RestClient restClient;

    public ChatProviderClient(ChatAiProperties properties) {
        this.properties = properties;
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(properties.getTimeout());
        requestFactory.setReadTimeout(properties.getTimeout());
        this.restClient = RestClient.builder().requestFactory(requestFactory).build();
    }

    public String complete(ChatDepartment department, List<ChatTurn> history) {
        if (properties.getApiKey() == null || properties.getApiKey().isBlank()) {
            throw new ChatProviderException();
        }

        String provider = properties.getProvider() == null
                ? ""
                : properties.getProvider().trim().toLowerCase(Locale.ROOT);
        try {
            return switch (provider) {
                case "openai" -> completeWithOpenAi(department, history);
                case "gemini" -> completeWithGemini(department, history);
                default -> throw new ChatProviderException();
            };
        } catch (RestClientException exception) {
            throw new ChatProviderException();
        }
    }

    private String completeWithOpenAi(ChatDepartment department, List<ChatTurn> history) {
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", department.systemInstruction()));
        for (ChatTurn turn : history) {
            messages.add(Map.of("role", turn.role(), "content", turn.content()));
        }

        JsonNode response = restClient.post()
                .uri(OPENAI_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + properties.getApiKey())
                .body(Map.of(
                        "model", properties.getOpenaiModel(),
                        "messages", messages,
                        "max_completion_tokens", 2048,
                        "store", false
                ))
                .retrieve()
                .body(JsonNode.class);
        JsonNode content = response == null ? null
                : response.path("choices").path(0).path("message").path("content");
        if (content == null || !content.isTextual() || content.asText().isBlank()) {
            throw new ChatProviderException();
        }
        return content.asText();
    }

    private String completeWithGemini(ChatDepartment department, List<ChatTurn> history) {
        List<Map<String, Object>> contents = history.stream()
                .map(turn -> Map.<String, Object>of(
                        "role", turn.role().equals("assistant") ? "model" : "user",
                        "parts", List.of(Map.of("text", turn.content()))))
                .toList();

        String url = GEMINI_URL + properties.getGeminiModel() + ":generateContent";
        JsonNode response = restClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .header("x-goog-api-key", properties.getApiKey())
                .body(Map.of(
                        "systemInstruction", Map.of(
                                "parts", List.of(Map.of("text", department.systemInstruction()))),
                        "contents", contents,
                        "generationConfig", Map.of("maxOutputTokens", 2048, "temperature", 0.3),
                        "store", false
                ))
                .retrieve()
                .body(JsonNode.class);

        JsonNode parts = response == null ? null
                : response.path("candidates").path(0).path("content").path("parts");
        if (parts == null || !parts.isArray()) {
            throw new ChatProviderException();
        }
        StringBuilder text = new StringBuilder();
        for (JsonNode part : parts) {
            JsonNode partText = part.get("text");
            if (partText != null && partText.isTextual()) {
                if (!text.isEmpty()) {
                    text.append('\n');
                }
                text.append(partText.asText());
            }
        }
        if (text.isEmpty()) {
            throw new ChatProviderException();
        }
        return text.toString();
    }
}
