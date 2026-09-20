package mathapp.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import mathapp.gui.auth.AuthContext;
import mathapp.gui.auth.AuthServiceClient;
import mathapp.gui.auth.AuthState;
import mathapp.gui.navigation.NavigationManager;
import mathapp.logging.MathLogger;

public class MainViewController {

    // View Containers / Screens
    @FXML private VBox authScreen;
    @FXML private VBox welcomeScreen;
    @FXML private VBox guideScreen;
    @FXML private VBox aboutScreen;
    @FXML private VBox calculatorScreen;
    @FXML private VBox simpleCalculatorTab;
    @FXML private VBox numberPropertiesScreen;

    // Header / User Labels
    @FXML private Labeled userNameLabel;
    @FXML private Labeled userEmailLabel;
    @FXML private MenuButton userMenuButton;
    @FXML private MenuItem menuLogin;
    @FXML private MenuItem menuLogout;

    // Navigation Tabs
    @FXML private Button tabHome;
    @FXML private Button tabSolver;
    @FXML private Button tabCalculator;
    @FXML private Button tabNumberProperties;
    @FXML private Button tabGuide;
    @FXML private Button tabAbout;

    private NavigationManager navigationManager;
    private final AuthState authState = AuthContext.getState();
    private final AuthServiceClient authClient = new AuthServiceClient("http://localhost:8081");

    @FXML
    public void initialize() {
        MathLogger.screenOpened("MAIN_VIEW_INITIALIZED");
        this.navigationManager = new NavigationManager(authScreen);
        refreshUserLabels();
    }

    public void refreshUserLabels() {
        boolean loggedIn = authState.isLoggedIn();

        tabSolver.setDisable(!loggedIn);
        tabCalculator.setDisable(!loggedIn);

        if (!loggedIn) {
            userNameLabel.setText("Guest User");
            userEmailLabel.setText("Please Login");
            userMenuButton.setText("Login");
            menuLogin.setDisable(false);
            menuLogout.setDisable(true);
        } else {
            userNameLabel.setText(authState.getUsername() != null ? authState.getUsername() : "Unknown");
            userEmailLabel.setText(authState.getEmail() != null ? authState.getEmail() : "Unknown");
            userMenuButton.setText("Account");
            menuLogin.setDisable(true);
            menuLogout.setDisable(false);
        }
    }

    // --- Navigation Actions ---
    @FXML void navToCalculatorTab(ActionEvent event) { navigationManager.switchScreen(simpleCalculatorTab); }
    @FXML void navToNumberProperties(ActionEvent event) { navigationManager.switchScreen(numberPropertiesScreen); }
    @FXML void navToGuide(ActionEvent event) { navigationManager.switchScreen(guideScreen); }
    @FXML void navToAbout(ActionEvent event) { navigationManager.switchScreen(aboutScreen); }
    @FXML void navToCalculator(ActionEvent event) { navigationManager.switchScreen(calculatorScreen); }
    @FXML void navToWelcome(ActionEvent event) { navigationManager.switchScreen(welcomeScreen); }

    @FXML
    void handleLoginMenu(ActionEvent event) {
        navigationManager.switchScreen(authScreen);
    }

    @FXML
    void handleLogout(ActionEvent event) {
        MathLogger.operationStarted("LOGOUT", authState.getUsername());
        try {
            authClient.logout(authState.getToken());
        } catch (Exception e) {
            MathLogger.error("AUTH", "Logout service call failed", e);
        }
        authState.setToken(null);
        authState.setUsername(null);
        authState.setEmail(null);

        refreshUserLabels();
        MathLogger.operationSuccess("LOGOUT", "user logged out");
        navigationManager.switchScreen(authScreen);
    }
}