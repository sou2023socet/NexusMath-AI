package com.mathapp.auth.api;

import com.mathapp.auth.config.SecurityConfig;
import com.mathapp.auth.config.CorsConfig;
import com.mathapp.auth.logging.LogService;
import com.mathapp.auth.security.JwtAuthFilter;
import com.mathapp.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, CorsConfig.class})
@TestPropertySource(properties = "auth.jwt.secret=test-only-secret-with-at-least-32-characters")
class AuthControllerCorsTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private LogService logService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    void allowsOriginsConfiguredByDevelopmentProfile() throws Exception {
        assertAllowedOrigin("http://localhost:4201");
        assertAllowedOrigin("http://localhost:4202");
    }

    private void assertAllowedOrigin(String origin) throws Exception {
        mockMvc.perform(options("/api/auth/login")
                        .header(HttpHeaders.ORIGIN, origin)
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin));
    }
}
