package mathapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import mathapp.gui.api.MathServiceClient;
import mathapp.gui.auth.AuthContext;

import java.util.Arrays;
import java.util.List;

/**
 * Controller for the main application interface managing tab inputs,
 * executing math engine calculations, and rendering formatted results.
 */
public class MainViewController {

    // Sequence Tab
    @FXML private TextField sequenceInput;
    @FXML private TextArea sequenceOutput;

    // Regression Tab
    @FXML private TextField xValuesInput;
    @FXML private TextField yValuesInput;
    @FXML private TextField predictXInput;
    @FXML private TextArea regressionOutput;

    // Combinatorics Tab
    @FXML private TextField nInput;
    @FXML private TextField rInput;
    @FXML private TextArea combinatoricsOutput;

    // Matrix Tab
    @FXML private TextArea matrixInputArea;
    @FXML private TextArea matrixOutputArea;

    private final MathServiceClient mathServiceClient = new MathServiceClient();

    @FXML
    public void initialize() {
        // Initialization logic for default inputs or listeners
    }

    @FXML
    private void handleAnalyzeSequence() {
        try {
            List<Double> terms = Arrays.stream(parseDoubleArray(sequenceInput.getText())).boxed().toList();
            sequenceOutput.setText(mathServiceClient.analyzeSequence(terms, AuthContext.getState().getToken()));
        } catch (Exception e) {
            sequenceOutput.setText("Error analyzing sequence: " + e.getMessage());
        }
    }

    @FXML
    private void handleCalculateRegression() {
        try {
            double[] x = parseDoubleArray(xValuesInput.getText());
            double[] y = parseDoubleArray(yValuesInput.getText());
            Double xToPredict = predictXInput.getText().isBlank() ? null : Double.parseDouble(predictXInput.getText().trim());

            String report = mathServiceClient.analyzeBivariate(
                    x, y, xToPredict, AuthContext.getState().getToken());
            regressionOutput.setText(report);
        } catch (Exception e) {
            regressionOutput.setText("Error computing correlation/regression: " + e.getMessage());
        }
    }

    @FXML
    private void handleCalculateCombinations() {
        try {
            int n = Integer.parseInt(nInput.getText().trim());
            int r = Integer.parseInt(rInput.getText().trim());
            String report = mathServiceClient.calculateCombinations(n, r, AuthContext.getState().getToken());
            combinatoricsOutput.setText(report);
        } catch (Exception e) {
            combinatoricsOutput.setText("Error computing combinatorics: " + e.getMessage());
        }
    }

    @FXML
    private void handleInvertMatrix() {
        try {
            double[][] data = parseMatrixData(matrixInputArea.getText());
            matrixOutputArea.setText(mathServiceClient.evaluateMatrix(
                    "INVERSE_WITH_DETERMINANT", data, AuthContext.getState().getToken()));
        } catch (Exception e) {
            matrixOutputArea.setText("Error processing matrix: " + e.getMessage());
        }
    }

    private double[] parseDoubleArray(String text) {
        if (text == null || text.isBlank()) throw new IllegalArgumentException("Input string cannot be empty.");
        return Arrays.stream(text.split("[,\\s]+"))
                .mapToDouble(Double::parseDouble)
                .toArray();
    }

    private double[][] parseMatrixData(String text) {
        if (text == null || text.isBlank()) throw new IllegalArgumentException("Matrix data cannot be empty.");
        String[] lines = text.trim().split("\\r?\\n");
        int rows = lines.length;
        double[][] matrix = null;

        for (int i = 0; i < rows; i++) {
            double[] row = parseDoubleArray(lines[i]);
            if (matrix == null) {
                matrix = new double[rows][row.length];
            } else if (row.length != matrix[0].length) {
                throw new IllegalArgumentException("All rows in the matrix must have the same number of columns.");
            }
            matrix[i] = row;
        }
        return matrix;
    }
}