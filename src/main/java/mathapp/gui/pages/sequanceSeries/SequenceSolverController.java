package mathapp.gui.pages.sequanceSeries;

import mathapp.gui.pages.sequanceSeries.PatternsAndSequencesController;
import mathapp.gui.pages.sequanceSeries.PatternsAndSequencesController.Domain2Property;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import mathapp.logging.MathLogger;

public class SequenceSolverController {

    @FXML private ComboBox<Domain2Property> operationComboBox;
    @FXML private Label inputLabel;
    @FXML private TextField sequenceInputField;
    @FXML private TextArea outputArea;
    @FXML private Button actionAnalyzeButton;

    private final PatternsAndSequencesController domainController = new PatternsAndSequencesController();

    @FXML
    public void initialize() {
        // Populate ComboBox with domain operations
        operationComboBox.setItems(FXCollections.observableArrayList(Domain2Property.values()));
        operationComboBox.getSelectionModel().select(Domain2Property.ANALYZE_SEQUENCE_PATTERN);

        // Update prompt dynamically when switching operations
        operationComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateInputPrompt(newVal);
            }
        });

        updateInputPrompt(Domain2Property.ANALYZE_SEQUENCE_PATTERN);
    }

    private void updateInputPrompt(Domain2Property property) {
        switch (property) {
            case POLYGONAL_NUMBERS:
                inputLabel.setText("Sides, Count:");
                sequenceInputField.setPromptText("e.g., 5, 10 (10 Pentagonal numbers)");
                break;
            case FIBONACCI_SEQUENCE:
            case CATALAN_NUMBERS:
            case PASCALS_TRIANGLE:
                inputLabel.setText("Term Count / Rows:");
                sequenceInputField.setPromptText("e.g., 10");
                break;
            default:
                inputLabel.setText("Sequence Input:");
                sequenceInputField.setPromptText("e.g., 2, 5, 10, 17, 26 or 3, 6, 12, 24");
                break;
        }
    }

    @FXML
    void handleAnalyzeSequence(ActionEvent event) {
        Domain2Property selectedOp = operationComboBox.getValue();
        String input = sequenceInputField.getText() != null ? sequenceInputField.getText().trim() : "";

        if (selectedOp == null) {
            outputArea.setText("❌ Please select a sequence operation.");
            return;
        }

        if (input.isEmpty()) {
            outputArea.setText("❌ Please enter input data or sequence terms.");
            return;
        }

        MathLogger.operationStarted("SEQUENCE_EVALUATE", "op=" + selectedOp + ", input=" + input);

        try {
            String result = domainController.evaluateProperty(selectedOp, input);
            outputArea.setText("✅ Result:\n\n" + result);
            MathLogger.operationSuccess("SEQUENCE_EVALUATE", "Evaluation completed");
        } catch (Exception e) {
            MathLogger.operationFailed("SEQUENCE_EVALUATE", e.getMessage(), e);
            outputArea.setText("❌ Error evaluating sequence: " + e.getMessage());
        }
    }

    @FXML
    void handleLoadSample(ActionEvent event) {
        Domain2Property selectedOp = operationComboBox.getValue();
        if (selectedOp == null) return;

        switch (selectedOp) {
            case POLYGONAL_NUMBERS:
                sequenceInputField.setText("5, 8");
                break;
            case FIBONACCI_SEQUENCE:
            case CATALAN_NUMBERS:
            case PASCALS_TRIANGLE:
                sequenceInputField.setText("8");
                break;
            default:
                sequenceInputField.setText("3, 7, 13, 21, 31");
                break;
        }
        outputArea.setText("Sample loaded. Click 'Analyze / Calculate'.");
    }

    @FXML
    void handleClear(ActionEvent event) {
        sequenceInputField.clear();
        outputArea.clear();
    }
}