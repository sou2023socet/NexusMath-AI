package mathapp.gui;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
// import mathapp.equations.EquationGenerator;
// import mathapp.equations.MultipleEquationHandler;
import mathapp.equations.api.EquationSolver;
// import mathapp.equations.api.EquationSolver;
import mathapp.equations.EquationGenerator;
import mathapp.equations.MultipleEquationHandler;
import mathapp.logging.MathLogger;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MultiEquationController {

    // Static singleton reference for cross-controller communication
    private static MultiEquationController instance;

    public static MultiEquationController getInstance() {
        return instance;
    }

    // Screens
    @FXML private VBox authScreen;
    @FXML private VBox welcomeScreen;
    @FXML private VBox guideScreen;
    @FXML private VBox aboutScreen;
    @FXML private VBox calculatorScreen;
    @FXML private VBox simpleCalculatorTab;
    @FXML private VBox numberPropertiesScreen;
    
    @FXML private Labeled userNameLabel;
    @FXML private Labeled userEmailLabel;

    @FXML private MenuButton userMenuButton;
    @FXML private MenuItem menuLogin;
    @FXML private MenuItem menuLogout;

    @FXML private Button tabHome;
    @FXML private Button tabSolver;
    @FXML private Button tabCalculator;
    @FXML private Button tabNumberProperties;
    @FXML private Button tabGuide;
    @FXML private Button tabAbout;

    // Calculator Elements
    @FXML private TextArea equationsArea;
    @FXML private TextField variablesField;
    @FXML private TextArea outputArea;
    @FXML private Button actionSolveButton;
    @FXML private Button actionLoadTestCaseButton;

    private MultipleEquationHandler multiHandler;
    private VBox currentScreen;

    private final AuthState authState = AuthContext.getState();
    private final AuthServiceClient authClient = new AuthServiceClient("http://localhost:8081");

    private Boolean loggedIn;

    @FXML
    public void initialize() {
        // Store static instance reference
        instance = this;
        MathLogger.screenOpened("AUTH_SCREEN");
        // Initialize Engine
        EquationSolver coreSolver = new EquationSolver();
        EquationGenerator generator = new EquationGenerator(coreSolver);
        this.multiHandler = new MultipleEquationHandler(generator, coreSolver);

        currentScreen = authScreen;
        authScreen.setVisible(true);
        
        
        refreshUserLabels();
    }

    @FXML
    void handleLoginMenu(ActionEvent event) {

        try {
            authScreen.setVisible(true);

            // optional: show login screen directly
            switchScreen(authScreen);

        } catch (Exception e) {
            MathLogger.error("NAV", "handleLoginMenu failed", e);
        }
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

            userNameLabel.setText(
                    authState.getUsername() != null ? authState.getUsername() : "Unknown");

            userEmailLabel.setText(
                    authState.getEmail() != null ? authState.getEmail() : "Unknown");

            userMenuButton.setText("Account");

            menuLogin.setDisable(true);
            menuLogout.setDisable(false);
        }
    }
    // --- NAVIGATION LOGIC WITH ANIMATION ---
    @FXML void navToCalculatorTab(ActionEvent event) { switchScreen(simpleCalculatorTab); }
    @FXML void navToNumberProperties(ActionEvent event) { switchScreen(numberPropertiesScreen); }
    @FXML void navToGuide(ActionEvent event) { switchScreen(guideScreen); }
    @FXML void navToAbout(ActionEvent event) { switchScreen(aboutScreen); }
    @FXML void navToCalculator(ActionEvent event) { switchScreen(calculatorScreen); }

    @FXML
    private void navToWelcome(ActionEvent event) {
        switchScreen(welcomeScreen);
    }   

    private void switchScreen(VBox newScreen) {
        if (currentScreen == newScreen) return;

        String fromName = screenName(currentScreen);
        String toName = screenName(newScreen);
        MathLogger.operationStarted("NAVIGATE", fromName + " -> " + toName);

        // Fade out current screen
        FadeTransition fadeOut = new FadeTransition(Duration.millis(150), currentScreen);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            MathLogger.screenClosed(fromName);
            currentScreen.setVisible(false);
            
            // Fade in new screen
            newScreen.setOpacity(0.0);
            newScreen.setVisible(true);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), newScreen);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
            
            currentScreen = newScreen;
            MathLogger.screenOpened(toName);
        });
        fadeOut.play();
    }

    private String screenName(VBox screen) {
        if (screen == null) return "UNKNOWN";
        String id = screen.getId();
        return id != null ? id : "UNKNOWN";
    }

    // --- CALCULATOR LOGIC ---

    @FXML
    void handleSolve(ActionEvent event) {
        String eqText = equationsArea.getText().trim();
        String varText = variablesField.getText().trim();

        if (eqText.isEmpty() || varText.isEmpty() || !(varText.contains("= ?") || varText.contains("=?"))) {
            MathLogger.operationFailed("SOLVE", "Missing equations or target variable (=?)");
            outputArea.setText("❌ Error: Missing equations or target variable (=?).");
            return;
        }

        MathLogger.operationStarted("SOLVE", "equations=" + eqText.replace("\n", " | ") + ", values=" + varText);
        List<String> equationsList = Arrays.stream(eqText.split("\\r?\\n"))
                                           .filter(line -> !line.trim().isEmpty())
                                           .collect(Collectors.toList());

        try {
            Map<String, Double> finalState = multiHandler.solveSystem(equationsList, varText);
            
            StringBuilder sb = new StringBuilder("✅ Success! Resolved Variables:\n\n");
            for (Map.Entry<String, Double> entry : finalState.entrySet()) {
                sb.append(" • ").append(entry.getKey()).append(" = ").append(entry.getValue()).append("\n");
            }
            outputArea.setText(sb.toString());
            MathLogger.operationSuccess("SOLVE", "result=" + finalState);
        } catch (Exception e) {
            MathLogger.operationFailed("SOLVE", "System Logic Error", e);
            outputArea.setText("❌ System Logic Error. Check syntax and variable names.\n"+ e.getClass() + "\n" + e.getMessage() + "\n" + e.getClass().getSimpleName());
        }
    }

    @FXML
    void handleTestCase(ActionEvent event) {
        MathLogger.operationStarted("LOAD_TEST_CASE", "");
        equationsArea.setText("distance = speed * time\nacceleration = speed / time");
        variablesField.setText("distance=100 time=5 acceleration=?");
        outputArea.setText("Test case loaded. Press 'Solve'.");
        MathLogger.operationSuccess("LOAD_TEST_CASE", "test case loaded");
    }

    @FXML
    void handleClear(ActionEvent event) {
        MathLogger.operationStarted("CLEAR_SOLVER", "");
        equationsArea.clear();
        variablesField.clear();
        outputArea.clear();
        MathLogger.operationSuccess("CLEAR_SOLVER", "inputs cleared");
    }

    @FXML
    void handleLogout(ActionEvent event) {
        MathLogger.operationStarted("LOGOUT", authState.getUsername());
        authClient.logout(authState.getToken());
        authState.setToken(null);
        authState.setUsername(null);
        authState.setEmail(null);
        refreshUserLabels();
        MathLogger.operationSuccess("LOGOUT", "user logged out");
        switchScreen(authScreen);
    }
}