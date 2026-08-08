package com.mathapp.auth.api.dto;

public class AuthDtos {

    public static class RegisterRequest {
        public String email;
        public String username;
        public String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class LoginRequest {
        public String email;
        public String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class AuthResponse {
        public String token;
        public String username;
        public String email;

        public AuthResponse() {}

        public AuthResponse(String token, String username, String email) {
            this.token = token;
            this.username = username;
            this.email = email;
        }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class ForgotPasswordRequest {
        public String email;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}

