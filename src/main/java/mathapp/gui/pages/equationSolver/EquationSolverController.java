package mathapp.gui.pages.equationSolver;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import mathapp.equations.EquationGenerator;
import mathapp.equations.MultipleEquationHandler;
import mathapp.equations.api.EquationSolver;
import mathapp.logging.MathLogger;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EquationSolverController {

    @FXML private TextArea equationsArea;
    @FXML private TextField variablesField;
    @FXML private TextArea outputArea;
    @FXML private Button actionSolveButton;
    @FXML private Button actionLoadTestCaseButton;

    private MultipleEquationHandler multiHandler;

    @FXML
    public void initialize() {
        EquationSolver coreSolver = new EquationSolver();
        EquationGenerator generator = new EquationGenerator(coreSolver);
        this.multiHandler = new MultipleEquationHandler(generator, coreSolver);
    }

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