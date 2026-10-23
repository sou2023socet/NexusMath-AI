package com.mathapp.auth.api;

import com.mathapp.auth.chat.ChatSessionService;
import com.mathapp.auth.chat.ChatAiProperties;
import com.mathapp.auth.config.SecurityConfig;
import com.mathapp.auth.logging.LogService;
import com.mathapp.auth.security.JwtAuthFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DepartmentChatController.class)
@Import({SecurityConfig.class, JwtAuthFilter.class})
@TestPropertySource(properties = "auth.jwt.secret=test-only-secret-with-at-least-32-characters")
class DepartmentChatControllerApiTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatSessionService chatSessionService;

    @MockBean
    private ChatAiProperties chatAiProperties;

    @MockBean
    private LogService logService;

    @Test
    void exposesOnlyAiEnabledStateToAuthenticatedClients() throws Exception {
        when(chatAiProperties.isEnabled()).thenReturn(false);

        mockMvc.perform(get("/api/ai-chat/config").with(user("alice")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled").value(false))
                .andExpect(jsonPath("$.apiKey").doesNotExist());
    }

    @Test
    void reportsChatDisabledWhenProviderKeyIsMissing() throws Exception {
        when(chatAiProperties.isEnabled()).thenReturn(true);
        when(chatAiProperties.getApiKey()).thenReturn(" ");

        mockMvc.perform(get("/api/ai-chat/config").with(user("alice")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled").value(false));
    }

    @Test
    void reportsChatEnabledWhenProviderKeyIsConfigured() throws Exception {
        when(chatAiProperties.isEnabled()).thenReturn(true);
        when(chatAiProperties.getApiKey()).thenReturn("test-only-provider-key");

        mockMvc.perform(get("/api/ai-chat/config").with(user("alice")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled").value(true))
                .andExpect(jsonPath("$.apiKey").doesNotExist());
    }

    @Test
    void sessionCreationRequiresAuthentication() throws Exception {
        mockMvc.perform(post("/api/ai-chat/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"department\":\"MATHEMATICS\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsUnsupportedDepartments() throws Exception {
        mockMvc.perform(post("/api/ai-chat/sessions")
                        .with(user("alice"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"department\":\"GENERAL\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsBlankOrOverlongMessagesBeforeCallingChatService() throws Exception {
        mockMvc.perform(post("/api/ai-chat/sessions/00000000-0000-0000-0000-000000000001/messages")
                        .with(user("alice"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"message\":\"  \"}"))
                .andExpect(status().isBadRequest());

        String overlongMessage = "x".repeat(4001);
        mockMvc.perform(post("/api/ai-chat/sessions/00000000-0000-0000-0000-000000000001/messages")
                        .with(user("alice"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"message\":\"" + overlongMessage + "\"}"))
                .andExpect(status().isBadRequest());
    }
}
