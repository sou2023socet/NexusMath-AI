package mathapp.gui;

public class AuthContext {

    private static final AuthState STATE = new AuthState();

    public static AuthState getState() {
        return STATE;
    }
}

