package mathapp.gui.pages.sequanceSeries;

import mathapp.gui.pages.sequanceSeries.PatternsAndSequencesController;
import mathapp.gui.pages.sequanceSeries.PatternsAndSequencesController.Domain2Property;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import mathapp.logging.MathLogger;

public class SequenceSolverController {

    @FXML private ComboBox<Domain2Property> operationComboBox;
    @FXML private Label inputLabel;
    @FXML private Label termIndexLabel;
    @FXML private TextField sequenceInputField;
    @FXML private TextField termIndexField;
    @FXML private TextArea outputArea;
    @FXML private Button actionAnalyzeButton;

    private final PatternsAndSequencesController domainController = new PatternsAndSequencesController();

    @FXML
    public void initialize() {
        operationComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Domain2Property property) {
                return property == null ? "" : property.getDisplayName();
            }

            @Override
            public Domain2Property fromString(String displayName) {
                for (Domain2Property property : Domain2Property.values()) {
                    if (property.getDisplayName().equals(displayName)) {
                        return property;
                    }
                }
                return null;
            }
        });

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
        boolean requiresTermIndex = property == Domain2Property.FIND_NTH_TERM ||
                property == Domain2Property.SUM_SERIES_TO_N;
        termIndexLabel.setVisible(requiresTermIndex);
        termIndexLabel.setManaged(requiresTermIndex);
        termIndexField.setVisible(requiresTermIndex);
        termIndexField.setManaged(requiresTermIndex);

        if (requiresTermIndex) {
            termIndexLabel.setText(property == Domain2Property.FIND_NTH_TERM ? "Target term n:" : "Sum through n:");
            termIndexField.setPromptText("e.g., 10");
            inputLabel.setText("Sequence Terms:");
            sequenceInputField.setPromptText("e.g., 2, 5, 10, 17");
            return;
        }

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
        Integer targetN = null;

        if (selectedOp == null) {
            outputArea.setText("❌ Please select a sequence operation.");
            return;
        }

        if (input.isEmpty()) {
            outputArea.setText("❌ Please enter input data or sequence terms.");
            return;
        }

        if (selectedOp == Domain2Property.FIND_NTH_TERM || selectedOp == Domain2Property.SUM_SERIES_TO_N) {
            try {
                targetN = Integer.parseInt(termIndexField.getText().trim());
                if (targetN < 1) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                outputArea.setText("❌ Enter a positive whole number for n.");
                return;
            }
        }

        MathLogger.operationStarted("SEQUENCE_EVALUATE", "op=" + selectedOp + ", input=" + input);

        try {
            String result = domainController.evaluateProperty(selectedOp, input, targetN);
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
            case FIND_NTH_TERM:
            case SUM_SERIES_TO_N:
                sequenceInputField.setText("2, 5, 10, 17");
                termIndexField.setText("8");
                break;
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
        termIndexField.clear();
        outputArea.clear();
    }
}