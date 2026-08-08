package mathapp.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AuthMainController {

    @FXML private VBox authContainer;

    @FXML private Button actionSolveButton;
    @FXML private Button actionCalculateButton;

    private final AuthServiceClient authClient = new AuthServiceClient("http://localhost:8081");
    private final AuthState authState = AuthContext.getState();

    private boolean authViewsLoaded = false;
    private Node loginView;
    private Node registerView;
    private Node forgotView;

    // Store direct references to UI fields for reliable access (survives toggle replacements)
    private final Map<String, TextInputControl> textFieldRefs = new HashMap<>();
    private final Map<String, Label> labelRefs = new HashMap<>();
    private final Map<String, Button> buttonRefs = new HashMap<>();

    @FXML
    public void initialize() {
        if (authContainer == null || authViewsLoaded) {
            return;
        }

        authViewsLoaded = true;
        try {
            FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("auth_login_view.fxml"));
            loginLoader.setController(this);
            loginView = loginLoader.load();

            FXMLLoader regLoader = new FXMLLoader(getClass().getResource("auth_registration_view.fxml"));
            regLoader.setController(this);
            registerView = regLoader.load();

            FXMLLoader forgotLoader = new FXMLLoader(getClass().getResource("auth_forgot_view.fxml"));
            forgotLoader.setController(this);
            forgotView = forgotLoader.load();

            // Cache all field references from loaded views
            cacheFields(loginView);
            cacheFields(registerView);
            cacheFields(forgotView);

        } catch (IOException e) {
            e.printStackTrace();
        }

        showLogin();
        updateActionButtons();
    }

    /**
     * Cache all TextInputControl, Label, and Button references from a view by fx:id.
     * This ensures we have stable references even when nodes are swapped in/out.
     */
    private void cacheFields(Node view) {
        if (view == null) return;
        for (Node child : view.lookupAll("*")) {
            String id = child.getId();
            if (id == null || id.isBlank()) continue;

            if (child instanceof TextInputControl) {
                textFieldRefs.put(id, (TextInputControl) child);
            } else if (child instanceof Label) {
                labelRefs.put(id, (Label) child);
            } else if (child instanceof Button) {
                buttonRefs.put(id, (Button) child);
            }
        }
    }

    /**
     * Get cached TextInputControl reference by fx:id.
     */
    private TextInputControl getTextInput(String fxId) {
        TextInputControl ref = textFieldRefs.get(fxId);
        if (ref == null) {
            // Fallback: try lookup (in case toggle replaced the node)
            Node found = (loginView != null) ? loginView.lookup("#" + fxId) : null;
            if (found == null) found = (registerView != null) ? registerView.lookup("#" + fxId) : null;
            if (found == null) found = (forgotView != null) ? forgotView.lookup("#" + fxId) : null;
            if (found instanceof TextInputControl) {
                textFieldRefs.put(fxId, (TextInputControl) found);
                return (TextInputControl) found;
            }
        }
        return ref;
    }

    /**
     * Get cached Label reference by fx:id.
     */
    private Label getLabel(String fxId) {
        Label ref = labelRefs.get(fxId);
        if (ref == null) {
            Node found = (loginView != null) ? loginView.lookup("#" + fxId) : null;
            if (found == null) found = (registerView != null) ? registerView.lookup("#" + fxId) : null;
            if (found == null) found = (forgotView != null) ? forgotView.lookup("#" + fxId) : null;
            if (found instanceof Label) {
                labelRefs.put(fxId, (Label) found);
                return (Label) found;
            }
        }
        return ref;
    }

    /**
     * Get cached Button reference by fx:id.
     */
    private Button getButton(String fxId) {
        Button ref = buttonRefs.get(fxId);
        if (ref == null) {
            Node found = (loginView != null) ? loginView.lookup("#" + fxId) : null;
            if (found == null) found = (registerView != null) ? registerView.lookup("#" + fxId) : null;
            if (found == null) found = (forgotView != null) ? forgotView.lookup("#" + fxId) : null;
            if (found instanceof Button) {
                buttonRefs.put(fxId, (Button) found);
                return (Button) found;
            }
        }
        return ref;
    }

    // ==========================
    // Password Visibility Toggle
    // ==========================

    @FXML
    void handleLoginPasswordToggle(ActionEvent event) {
        togglePasswordVisibility("loginPassword", "loginPasswordToggle", loginView);
    }

    @FXML
    void handleRegPasswordToggle(ActionEvent event) {
        togglePasswordVisibility("regPassword", "regPasswordToggle", registerView);
    }

    private void togglePasswordVisibility(String passwordFieldId, String toggleButtonId, Node parentView) {
        if (parentView == null) return;

        Button toggleBtn = getButton(toggleButtonId);
        if (toggleBtn == null) return;

        // Find the wrapper HBox that contains the password field
        HBox wrapper = (HBox) toggleBtn.getParent();
        if (wrapper == null) return;

        // Find the password input node (could be PasswordField or TextField)
        Node currentInput = wrapper.lookup("#" + passwordFieldId);
        if (currentInput == null) return;

        String currentText = "";
        if (currentInput instanceof TextInputControl) {
            currentText = ((TextInputControl) currentInput).getText();
        }

        int index = wrapper.getChildren().indexOf(currentInput);
        if (index < 0) return;

        if (toggleBtn.getText().equals("👁")) {
            // Switch to visible text field
            TextField visibleField = new TextField(currentText);
            visibleField.setId(passwordFieldId);
            visibleField.setPromptText(((TextInputControl) currentInput).getPromptText());
            visibleField.getStyleClass().addAll("password-field-base");
            visibleField.setPrefHeight(((TextInputControl) currentInput).getPrefHeight());
            HBox.setHgrow(visibleField, javafx.scene.layout.Priority.ALWAYS);

            wrapper.getChildren().set(index, visibleField);
            toggleBtn.setText("🙈");

            // Update cache with new reference
            textFieldRefs.put(passwordFieldId, visibleField);

        } else {
            // Switch back to password field
            PasswordField newPasswordField = new PasswordField();
            newPasswordField.setId(passwordFieldId);
            newPasswordField.setText(currentText);
            newPasswordField.setPromptText(((TextInputControl) currentInput).getPromptText());
            newPasswordField.getStyleClass().addAll("password-field-base");
            newPasswordField.setPrefHeight(((TextInputControl) currentInput).getPrefHeight());
            HBox.setHgrow(newPasswordField, javafx.scene.layout.Priority.ALWAYS);

            wrapper.getChildren().set(index, newPasswordField);
            toggleBtn.setText("👁");

            // Update cache with new reference
            textFieldRefs.put(passwordFieldId, newPasswordField);
        }
    }

    // ==========================
    // Navigation
    // ==========================

    @FXML
    void showRegistration(ActionEvent event) {
        showRegister();
    }

    @FXML
    void showForgotPassword(ActionEvent event) {
        showForgot();
    }

    @FXML
    void showLogin(ActionEvent event) {
        showLogin();
    }

    private void showLogin() {
        if (authContainer == null) return;
        authContainer.getChildren().clear();
        if (loginView != null) {
            authContainer.getChildren().add(loginView);
            clearLoginFields();
        }
    }

    private void showRegister() {
        if (authContainer == null) return;
        authContainer.getChildren().clear();
        if (registerView != null) {
            authContainer.getChildren().add(registerView);
            clearRegisterFields();
        }
    }

    private void showForgot() {
        if (authContainer == null) return;
        authContainer.getChildren().clear();
        if (forgotView != null) {
            authContainer.getChildren().add(forgotView);
            clearForgotFields();
        }
    }

    // ==========================
    // Field Clearing Helpers
    // ==========================

    private void clearLoginFields() {
        clearTextField("loginEmail");
        clearTextField("loginPassword");
        clearLabelText("loginMessage");
        clearLabelText("loginEmailError");
        clearLabelText("loginPasswordError");
        clearFieldErrorStyle("loginEmail");
        clearFieldErrorStyle("loginPassword");
    }

    private void clearRegisterFields() {
        clearTextField("regEmail");
        clearTextField("regUsername");
        clearTextField("regPassword");
        clearLabelText("regMessage");
        clearLabelText("regEmailError");
        clearLabelText("regUsernameError");
        clearLabelText("regPasswordError");
        clearFieldErrorStyle("regEmail");
        clearFieldErrorStyle("regUsername");
        clearFieldErrorStyle("regPassword");
    }

    private void clearForgotFields() {
        clearTextField("forgotEmail");
        clearLabelText("forgotMessage");
        clearLabelText("forgotEmailError");
        clearFieldErrorStyle("forgotEmail");
    }

    private void clearTextField(String fxId) {
        TextInputControl field = getTextInput(fxId);
        if (field != null) {
            field.clear();
        }
    }

    private void clearLabelText(String fxId) {
        Label label = getLabel(fxId);
        if (label != null) {
            label.setText("");
            label.setManaged(false);
            label.getStyleClass().remove("success-label");
        }
    }

    private void clearFieldErrorStyle(String fxId) {
        TextInputControl field = getTextInput(fxId);
        if (field != null) {
            field.getStyleClass().remove("field-error");
        }
    }

    // ==========================
    // Auth Operations
    // ==========================

    @FXML
    void handleLogin(ActionEvent event) {
        TextInputControl emailField = getTextInput("loginEmail");
        TextInputControl passField = getTextInput("loginPassword");
        Label msg = getLabel("loginMessage");

        if (emailField == null || passField == null) {
            System.err.println("ERROR: Login fields not found in cache. Cannot proceed.");
            return;
        }

        // Clear previous errors
        clearLabelText("loginEmailError");
        clearLabelText("loginPasswordError");
        clearFieldErrorStyle("loginEmail");
        clearFieldErrorStyle("loginPassword");

        String e = emailField.getText().trim();
        String p = passField.getText();

        // Client-side validation
        if (e.isEmpty()) {
            setFieldError("loginEmail", "loginEmailError", "Email is required");
            return;
        }
        if (p.isEmpty()) {
            setFieldError("loginPassword", "loginPasswordError", "Password is required");
            return;
        }

        try {
            AuthState state = authClient.login(e, p);
            authState.setToken(state.getToken());
            authState.setUsername(state.getUsername());
            authState.setEmail(state.getEmail());
            if (msg != null) {
                msg.setText("");
                msg.setManaged(false);
            }
            // Clear sensitive fields after successful login
            clearTextField("loginPassword");
            updateActionButtons();
            notifyUserLabelsUpdated();
        } catch (Exception ex) {
            ex.printStackTrace();
            if (msg != null) {
                msg.setText("❌ Login failed: " + ex.getMessage());
                msg.setManaged(true);
            }
        }
    }

    @FXML
    void handleRegistration(ActionEvent event) {
        TextInputControl emailField = getTextInput("regEmail");
        TextInputControl usernameField = getTextInput("regUsername");
        TextInputControl passField = getTextInput("regPassword");
        Label msg = getLabel("regMessage");

        if (emailField == null || usernameField == null || passField == null) {
            System.err.println("ERROR: Registration fields not found in cache. Cannot proceed.");
            return;
        }

        // Clear previous errors
        clearLabelText("regEmailError");
        clearLabelText("regUsernameError");
        clearLabelText("regPasswordError");
        clearFieldErrorStyle("regEmail");
        clearFieldErrorStyle("regUsername");
        clearFieldErrorStyle("regPassword");

        String e = emailField.getText().trim();
        String u = usernameField.getText().trim();
        String p = passField.getText();
        boolean hasError = false;

        if (e.isEmpty()) {
            setFieldError("regEmail", "regEmailError", "Email is required");
            hasError = true;
        }
        if (u.isEmpty()) {
            setFieldError("regUsername", "regUsernameError", "Username is required");
            hasError = true;
        }
        if (p.isEmpty()) {
            setFieldError("regPassword", "regPasswordError", "Password is required");
            hasError = true;
        } else if (p.length() < 6) {
            setFieldError("regPassword", "regPasswordError", "Password must be at least 6 characters");
            hasError = true;
        }

        if (hasError) return;

        try {
            AuthState state = authClient.register(e, u, p);
            authState.setToken(state.getToken());
            authState.setUsername(state.getUsername());
            authState.setEmail(state.getEmail());

            System.out.println("====== Shared AuthState ======");
            System.out.println(authState.getToken());
            System.out.println(authState.getUsername());
            System.out.println(authState.getEmail());
            if (msg != null) {
                msg.setText("✅ Registered successfully.");
                msg.setManaged(true);
                msg.getStyleClass().add("success-label");
            }
            // Clear fields after successful registration
            clearRegisterFields();
            updateActionButtons();
            notifyUserLabelsUpdated();
        } catch (Exception ex) {
            ex.printStackTrace();
            if (msg != null) {
                msg.setText("❌ Registration failed: " + ex.getMessage());
                msg.setManaged(true);
            }
        }
    }

    @FXML
    void handleForgotPassword(ActionEvent event) {
        TextInputControl emailField = getTextInput("forgotEmail");
        Label msg = getLabel("forgotMessage");

        if (emailField == null) return;

        // Clear previous errors
        clearLabelText("forgotEmailError");
        clearFieldErrorStyle("forgotEmail");

        String e = emailField.getText().trim();
        if (e.isEmpty()) {
            setFieldError("forgotEmail", "forgotEmailError", "Email is required");
            return;
        }

        try {
            authClient.forgotPassword(e);
            if (msg != null) {
                msg.setText("✅ If the email exists, a reset link was sent.");
                msg.setManaged(true);
                msg.getStyleClass().add("success-label");
            }
            clearTextField("forgotEmail");
        } catch (Exception ex) {
            ex.printStackTrace();
            if (msg != null) {
                msg.setText("❌ Failed: " + ex.getMessage());
                msg.setManaged(true);
            }
        }
    }

    // ==========================
    // Error Handling Helpers
    // ==========================

    private void setFieldError(String fieldId, String errorLabelId, String errorMessage) {
        TextInputControl field = getTextInput(fieldId);
        Label errorLabel = getLabel(errorLabelId);

        if (field != null) {
            field.getStyleClass().add("field-error");
        }
        if (errorLabel != null) {
            errorLabel.setText(errorMessage);
            errorLabel.setManaged(true);
        }
    }

    // ==========================
    // Action Buttons & User Labels
    // ==========================

    private void updateActionButtons() {
        boolean enabled = authState.isLoggedIn();
        if (actionSolveButton != null) actionSolveButton.setDisable(!enabled);
        if (actionCalculateButton != null) actionCalculateButton.setDisable(!enabled);
    }

    /**
     * Notify MultiEquationController to update user labels after login state change.
     */
    private void notifyUserLabelsUpdated() {
        MultiEquationController controller = MultiEquationController.getInstance();
        if (controller != null) {
            controller.refreshUserLabels();
        }
    }
}

