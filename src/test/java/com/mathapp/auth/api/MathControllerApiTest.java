package com.mathapp.auth.api;

import com.mathapp.auth.config.SecurityConfig;
import com.mathapp.auth.logging.LogService;
import com.mathapp.auth.security.JwtAuthFilter;
import com.mathapp.auth.service.MathService;
import com.mathapp.auth.service.NumberPropertyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MathController.class)
@Import({SecurityConfig.class, JwtAuthFilter.class})
@TestPropertySource(properties = "auth.jwt.secret=test-only-secret-with-at-least-32-characters")
class MathControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MathService mathService;

    @MockBean
    private NumberPropertyService numberPropertyService;

    @MockBean
    private LogService logService;

    @Test
    void calculationEndpointRequiresAuthentication() throws Exception {
        mockMvc.perform(post("/api/math/arithmetic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"operation\":\"ADD\",\"firstOperand\":2,\"secondOperand\":3}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticatedRequestCallsMathServiceAndReturnsJsonNumber() throws Exception {
        when(mathService.arithmetic("ADD", 2.0, 3.0)).thenReturn(5.0);

        mockMvc.perform(post("/api/math/arithmetic")
                        .with(user("test-user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"operation\":\"ADD\",\"firstOperand\":2,\"secondOperand\":3}"))
                .andExpect(status().isOk())
                .andExpect(content().json("5.0"));

        verify(mathService).arithmetic("ADD", 2.0, 3.0);
    }

    @Test
    void rejectsNullRequiredFieldsWithClientError() throws Exception {
        mockMvc.perform(post("/api/math/arithmetic")
                        .with(user("test-user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"operation\":\"ADD\",\"firstOperand\":null}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"error\":\"firstOperand must not be null\"}", false));
    }

    @Test
    void rejectsMalformedJsonWithClientError() throws Exception {
        mockMvc.perform(post("/api/math/arithmetic")
                        .with(user("test-user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":\"Request body is missing or malformed.\"}"));
    }
}
