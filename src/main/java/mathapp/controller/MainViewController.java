package mathapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import mathapp.algebra.Matrix;
import mathapp.algebra.MatrixEngine;
import mathapp.gui.pages.patterns.SequencePatternEngine;
import mathapp.gui.pages.patterns.SequencePatternResult;
import mathapp.stats.engine.CorrelationRegressionEngine;
import mathapp.stats.engine.ProbabilityCombinatoricsEngine;

import java.util.Arrays;

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

    @FXML
    public void initialize() {
        // Initialization logic for default inputs or listeners
    }

    @FXML
    private void handleAnalyzeSequence() {
        try {
            double[] terms = parseDoubleArray(sequenceInput.getText());
            SequencePatternResult result = SequencePatternEngine.analyzeSequence(terms, 5);
            sequenceOutput.setText(result.stepByStepReport());
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

            String report = CorrelationRegressionEngine.analyzeBivariate(x, y, xToPredict, 0.05).stepByStepReport();
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
            String report = ProbabilityCombinatoricsEngine.calculateCombinations(n, r).stepByStepReport();
            combinatoricsOutput.setText(report);
        } catch (Exception e) {
            combinatoricsOutput.setText("Error computing combinatorics: " + e.getMessage());
        }
    }

    @FXML
    private void handleInvertMatrix() {
        try {
            double[][] data = parseMatrixData(matrixInputArea.getText());
            Matrix A = new Matrix(data);
            double det = MatrixEngine.determinant(A);

            if (Math.abs(det) < 1e-10) {
                matrixOutputArea.setText("Matrix is singular (Determinant = 0). Inverse does not exist.");
                return;
            }

            Matrix inv = MatrixEngine.inverse(A);
            matrixOutputArea.setText(String.format("Determinant: %.4f\n\nInverse Matrix A^-1:\n%s", det, inv.toString()));
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