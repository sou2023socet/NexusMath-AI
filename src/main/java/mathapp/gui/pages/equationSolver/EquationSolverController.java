package mathapp.gui.pages.equationSolver;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import mathapp.gui.api.MathServiceClient;
import mathapp.gui.auth.AuthContext;
import mathapp.logging.MathLogger;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EquationSolverController {

    @FXML private TextArea equationsArea;
    @FXML private TextField variablesField;
    @FXML private TextArea outputArea;
    @FXML private Button actionSolveButton;
    @FXML private Button actionLoadTestCaseButton;

    private final MathServiceClient mathServiceClient = new MathServiceClient();

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
            String result = mathServiceClient.solveSystem(
                    equationsList, varText, AuthContext.getState().getToken());
            outputArea.setText("✅ Success!\n\n" + result);
            MathLogger.operationSuccess("SOLVE", "equation system solved remotely");
        } catch (Exception e) {
            MathLogger.operationFailed("SOLVE", "System Logic Error", e);
            outputArea.setText("❌ System Logic Error. Check syntax and variable names.\n" +
                    e.getClass().getSimpleName() + ": " + e.getMessage());
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
}