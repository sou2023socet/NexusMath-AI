package com.mathapp.auth.api;

import com.mathapp.auth.chat.ChatDepartment;
import com.mathapp.auth.chat.ChatAiProperties;
import com.mathapp.auth.chat.ChatProviderException;
import com.mathapp.auth.chat.ChatSessionException;
import com.mathapp.auth.chat.ChatSessionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/ai-chat")
public class DepartmentChatController {
    private static final int MAX_INPUT_CHARACTERS = 4000;

    private final ChatSessionService chatSessions;
    private final ChatAiProperties chatAiProperties;

    public DepartmentChatController(ChatSessionService chatSessions, ChatAiProperties chatAiProperties) {
        this.chatSessions = chatSessions;
        this.chatAiProperties = chatAiProperties;
    }

    @GetMapping("/config")
    public ChatConfigResponse config() {
        String apiKey = chatAiProperties.getApiKey();
        return new ChatConfigResponse(
                chatAiProperties.isEnabled() && apiKey != null && !apiKey.isBlank());
    }

    @PostMapping("/sessions")
    public ResponseEntity<ChatSessionService.SessionState> start(
            Authentication authentication, @Valid @RequestBody StartChatRequest request) {
        ChatDepartment department = ChatDepartment.valueOf(request.department());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(chatSessions.start(authentication.getName(), department));
    }

    @PostMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<ChatSessionService.MessageState> send(
            Authentication authentication,
            @PathVariable UUID sessionId,
            @Valid @RequestBody SendMessageRequest request) {
        return ResponseEntity.ok(chatSessions.send(
                authentication.getName(), sessionId, request.message().strip()));
    }

    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<Void> exit(Authentication authentication, @PathVariable UUID sessionId) {
        chatSessions.exit(authentication.getName(), sessionId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(ChatSessionException.class)
    public ResponseEntity<ErrorResponse> handleChatSessionError(ChatSessionException exception) {
        return ResponseEntity.status(exception.status()).body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(ChatProviderException.class)
    public ResponseEntity<ErrorResponse> handleProviderError(ChatProviderException exception) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponse("The AI service is temporarily unavailable. Please try again shortly."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequest(MethodArgumentNotValidException exception) {
        return ResponseEntity.badRequest().body(new ErrorResponse(
                "Chat request contains an invalid department or message."));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMalformedRequest(HttpMessageNotReadableException exception) {
        return ResponseEntity.badRequest().body(new ErrorResponse("Chat request is missing or malformed."));
    }

    public record StartChatRequest(
            @NotBlank
            @Pattern(regexp = "MATHEMATICS|PHYSICS|DSA")
            String department) {
    }

    public record SendMessageRequest(
            @NotBlank
            @Size(max = MAX_INPUT_CHARACTERS)
            String message) {
    }

    public record ErrorResponse(String error) {
    }

    public record ChatConfigResponse(boolean enabled) {
    }
}
