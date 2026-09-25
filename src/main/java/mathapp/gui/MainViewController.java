package mathapp.gui;

import javafx.event.ActionEvent;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import mathapp.gui.auth.AuthContext;
import mathapp.gui.auth.AuthServiceClient;
import mathapp.gui.auth.AuthState;
import mathapp.gui.navigation.NavigationManager;
import mathapp.logging.MathLogger;

public class MainViewController {

    private static MainViewController instance;

    // View Containers / Screens
    @FXML private VBox authScreen;
    @FXML private VBox welcomeScreen;
    @FXML private VBox guideScreen;
    @FXML private VBox aboutScreen;
    @FXML private VBox calculatorScreen;
    @FXML private VBox simpleCalculatorTab;
    @FXML private VBox numberPropertiesScreen;
    @FXML private VBox statisticsScreen;
    @FXML private VBox probabilityScreen;
    @FXML private VBox mathEnginesScreen;

    // Header / User Labels
    @FXML private Labeled userNameLabel;
    @FXML private Labeled userEmailLabel;
    @FXML private MenuButton userMenuButton;
    @FXML private MenuItem menuLogin;
    @FXML private MenuItem menuLogout;
    @FXML private Button themeButton;

    // Navigation Tabs
    @FXML private Button tabHome;
    @FXML private Button tabSolver;
    @FXML private Button tabCalculator;
    @FXML private Button tabNumberProperties;
    @FXML private Button tabSequences;
    @FXML private Button tabStatistics;
    @FXML private Button tabProbability;
    @FXML private Button tabMathEngines;
    @FXML private VBox sequenceSolverScreen;
    @FXML private Button tabGuide;
    @FXML private Button tabAbout;

    private NavigationManager navigationManager;
    private final AuthState authState = AuthContext.getState();
    private final AuthServiceClient authClient = new AuthServiceClient("http://localhost:8081");
    private boolean lightMode;

    @FXML
    public void initialize() {
        instance = this;
        MathLogger.screenOpened("MAIN_VIEW_INITIALIZED");
        this.navigationManager = new NavigationManager(authScreen);
        Platform.runLater(() -> applyTheme(false));
        refreshUserLabels();
    }

    @FXML
    void handleThemeToggle(ActionEvent event) {
        lightMode = !lightMode;
        applyTheme(lightMode);
    }

    private void applyTheme(boolean lightMode) {
        Parent root = themeButton == null || themeButton.getScene() == null
                ? null : themeButton.getScene().getRoot();
        if (root == null) {
            return;
        }
        root.getStyleClass().removeAll("theme-light", "theme-dark");
        root.getStyleClass().add(lightMode ? "theme-light" : "theme-dark");
        themeButton.setText(lightMode ? "☀" : "☾");
        themeButton.setAccessibleText(lightMode ? "Switch to dark mode" : "Switch to light mode");
    }

    public void refreshUserLabels() {
        boolean loggedIn = authState.isLoggedIn();

        tabSolver.setDisable(!loggedIn);
        tabCalculator.setDisable(!loggedIn);
        tabNumberProperties.setDisable(!loggedIn);
        tabSequences.setDisable(!loggedIn);
        tabStatistics.setDisable(!loggedIn);
        tabProbability.setDisable(!loggedIn);
        tabMathEngines.setDisable(!loggedIn);
        tabAbout.setDisable(!loggedIn);
        tabGuide.setDisable(!loggedIn);

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
            instance.navigationManager.switchScreen(instance.welcomeScreen);
        }
    }

    // --- Navigation Actions ---
    @FXML void navToCalculatorTab(ActionEvent event) { navigationManager.switchScreen(simpleCalculatorTab); }
    @FXML void navToNumberProperties(ActionEvent event) { navigationManager.switchScreen(numberPropertiesScreen); }
    @FXML void navToSequences(ActionEvent event) { navigationManager.switchScreen(sequenceSolverScreen); }
    @FXML void navToStatistics(ActionEvent event) { navigationManager.switchScreen(statisticsScreen); }
    @FXML void navToProbability(ActionEvent event) { navigationManager.switchScreen(probabilityScreen); }
    @FXML void navToMathEngines(ActionEvent event) { navigationManager.switchScreen(mathEnginesScreen); }
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

    public static MainViewController getInstance() {
        return instance;
    }
}