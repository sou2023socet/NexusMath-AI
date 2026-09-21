package mathapp.gui.stats;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import mathapp.logging.MathLogger;
import mathapp.stats.engine.ProbabilityAndSetCalculator.*;
import mathapp.stats.facade.StatsServiceFacade;

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

    private final StatsServiceFacade statsFacade = new StatsServiceFacade();

    @FXML
    void handleSolveVenn2(ActionEvent event) {
        try {
            double pA = Double.parseDouble(paField.getText().trim());
            double pB = Double.parseDouble(pbField.getText().trim());
            double pIntersect = Double.parseDouble(pIntersectField.getText().trim());

            Venn2SetResult res = statsFacade.solveVenn2(pA, pB, pIntersect);

            StringBuilder sb = new StringBuilder("### 2-Set Venn Diagram Analysis\n\n");
            sb.append(String.format("* $P(A)$: `%.4f` | $P(B)$: `%.4f` | $P(A \\cap B)$: `%.4f`\n", res.pA(), res.pB(), res.pIntersect()));
            sb.append(String.format("* Union $P(A \\cup B)$: `%.4f` (%.2f%%)\n", res.pUnion(), res.pUnion() * 100.0));
            sb.append(String.format("* Only $A$: `%.4f` | Only $B$: `%.4f`\n", res.pAOnly(), res.pBOnly()));
            sb.append(String.format("* Neither $P((A \\cup B)^c)$: `%.4f`\n", res.pNeither()));

            setOutputArea.setText(sb.toString());
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

            BayesResult result = statsFacade.updateBayes(names, priors, likelihoods);
            bayesOutputArea.setText(result.stepByStepReport());
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

            ContingencyResult result = statsFacade.analyzeContingency(counts);
            contingencyOutputArea.setText(result.stepByStepReport());
        } catch (Exception e) {
            contingencyOutputArea.setText("❌ Table Analysis Error: " + e.getMessage());
        }
    }
}