package mathapp.gui;

public class AuthState {

    private String token;
    private String username;
    private String email;

    public boolean isLoggedIn() {
        return token != null && !token.isBlank();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

