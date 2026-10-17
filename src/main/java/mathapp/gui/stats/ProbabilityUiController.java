package mathapp.gui.stats;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import mathapp.gui.api.MathServiceClient;
import mathapp.gui.auth.AuthContext;
import mathapp.logging.MathLogger;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JavaFX Controller for Set Theory, Contingency Tables, and Bayesian Probability Updates.
 */
public class ProbabilityUiController {

    // Set Theory Fields
    @FXML private TextField paField;
    @FXML private TextField pbField;
    @FXML private TextField pIntersectField;
    @FXML private TextArea setOutputArea;

    // Bayes Engine Fields
    @FXML private TextField hypothesisNamesField;
    @FXML private TextField priorsField;
    @FXML private TextField likelihoodsField;
    @FXML private TextArea bayesOutputArea;

    // Contingency Table Fields
    @FXML private TextArea contingencyInputArea;
    @FXML private TextArea contingencyOutputArea;

    private final MathServiceClient mathServiceClient = new MathServiceClient();

    @FXML
    void handleSolveVenn2(ActionEvent event) {
        try {
            double pA = Double.parseDouble(paField.getText().trim());
            double pB = Double.parseDouble(pbField.getText().trim());
            double pIntersect = Double.parseDouble(pIntersectField.getText().trim());

            setOutputArea.setText(mathServiceClient.solveVenn2(
                    pA, pB, pIntersect, AuthContext.getState().getToken()));
        } catch (Exception e) {
            setOutputArea.setText("❌ Input Error: " + e.getMessage());
        }
    }

    @FXML
    void handleComputeBayes(ActionEvent event) {
        try {
            List<String> names = Arrays.stream(hypothesisNamesField.getText().split("[,\\s]+"))
                    .filter(s -> !s.isBlank()).collect(Collectors.toList());

            List<Double> priors = Arrays.stream(priorsField.getText().split("[,\\s]+"))
                    .filter(s -> !s.isBlank()).map(Double::parseDouble).collect(Collectors.toList());

            List<Double> likelihoods = Arrays.stream(likelihoodsField.getText().split("[,\\s]+"))
                    .filter(s -> !s.isBlank()).map(Double::parseDouble).collect(Collectors.toList());

            bayesOutputArea.setText(mathServiceClient.updateBayes(
                    names, priors, likelihoods, AuthContext.getState().getToken()));
        } catch (Exception e) {
            bayesOutputArea.setText("❌ Bayes Computation Error: " + e.getMessage());
        }
    }

    @FXML
    void handleAnalyzeContingencyTable(ActionEvent event) {
        try {
            String[] lines = contingencyInputArea.getText().trim().split("\\r?\\n");
            int rows = lines.length;
            double[][] counts = null;

            for (int r = 0; r < rows; r++) {
                String[] tokens = lines[r].trim().split("[,\\s]+");
                if (counts == null) {
                    counts = new double[rows][tokens.length];
                }
                for (int c = 0; c < tokens.length; c++) {
                    counts[r][c] = Double.parseDouble(tokens[c]);
                }
            }

            contingencyOutputArea.setText(mathServiceClient.analyzeContingency(
                    counts, AuthContext.getState().getToken()));
        } catch (Exception e) {
            contingencyOutputArea.setText("❌ Table Analysis Error: " + e.getMessage());
        }
    }
}