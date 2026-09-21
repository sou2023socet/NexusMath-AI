package mathapp.gui.stats;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import mathapp.logging.MathLogger;
import mathapp.stats.core.StatisticalState.DataPoint2D;
import mathapp.stats.dto.LogisticRegressionResult;
import mathapp.stats.dto.RegressionResult;
import mathapp.stats.facade.StatsServiceFacade;

import java.util.ArrayList;
import java.util.List;

/**
 * JavaFX Controller for Bivariate Regression Analysis (Linear, Polynomial, Exponential, Logistic).
 */
public class StatsController {

    @FXML private TextArea inputDataArea;
    @FXML private ComboBox<String> modelTypeCombo;
    @FXML private Spinner<Integer> polyDegreeSpinner;
    @FXML private TextField thresholdField;
    @FXML private TextArea reportOutputArea;

    @FXML private LineChart<Number, Number> regressionChart;
    @FXML private NumberAxis xAxis;
    @FXML private NumberAxis yAxis;

    private final StatsServiceFacade statsFacade = new StatsServiceFacade();

    @FXML
    public void initialize() {
        modelTypeCombo.getItems().addAll("Linear", "Polynomial", "Exponential", "Logistic");
        modelTypeCombo.getSelectionModel().select("Linear");

        polyDegreeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 5, 2));
        polyDegreeSpinner.visibleProperty().bind(modelTypeCombo.valueProperty().isEqualTo("Polynomial"));

        thresholdField.visibleProperty().bind(modelTypeCombo.valueProperty().isEqualTo("Logistic"));
        thresholdField.setText("0.5");
    }

    @FXML
    void handleRunAnalysis(ActionEvent event) {
        List<DataPoint2D> points = parseDataPoints(inputDataArea.getText());
        if (points.size() < 2) {
            reportOutputArea.setText("❌ Error: At least 2 valid data points (x, y) are required.");
            return;
        }

        String selectedModel = modelTypeCombo.getValue();
        MathLogger.operationStarted("FIT_MODEL", "Type=" + selectedModel + ", Points=" + points.size());

        try {
            regressionChart.getData().clear();

            // Plot Raw Observations
            XYChart.Series<Number, Number> rawSeries = new XYChart.Series<>();
            rawSeries.setName("Observed Points");
            for (DataPoint2D pt : points) {
                rawSeries.getData().add(new XYChart.Data<>(pt.x(), pt.y()));
            }
            regressionChart.getData().add(rawSeries);

            if ("Logistic".equals(selectedModel)) {
                double threshold = Double.parseDouble(thresholdField.getText().trim());
                LogisticRegressionResult result = statsFacade.fitLogisticModel(points, threshold);
                reportOutputArea.setText(result.stepByStepReport());

                XYChart.Series<Number, Number> fitSeries = new XYChart.Series<>();
                fitSeries.setName("Logistic Probability Fit");
                for (DataPoint2D pt : result.predictedProbabilities()) {
                    fitSeries.getData().add(new XYChart.Data<>(pt.x(), pt.y()));
                }
                regressionChart.getData().add(fitSeries);
            } else {
                RegressionResult result;
                switch (selectedModel) {
                    case "Polynomial" -> result = statsFacade.fitPolynomialModel(points, polyDegreeSpinner.getValue());
                    case "Exponential" -> result = statsFacade.fitExponentialModel(points);
                    default -> result = statsFacade.fitLinearModel(points);
                }

                reportOutputArea.setText(result.stepByStepReport());

                XYChart.Series<Number, Number> fitSeries = new XYChart.Series<>();
                fitSeries.setName("Fitted Curve: " + result.latexFormula());
                // for (DataPoint2D pt : result.fittedPoints()) {
                //     fitSeries.getData().add(new XYChart.Data<>(pt.x(), pt.y()));
                // }
                regressionChart.getData().add(fitSeries);
            }

            MathLogger.operationSuccess("FIT_MODEL", "Successfully fitted " + selectedModel);
        } catch (Exception e) {
            MathLogger.operationFailed("FIT_MODEL", e.getMessage(), e);
            reportOutputArea.setText("❌ Analysis Error: " + e.getMessage());
        }
    }

    private List<DataPoint2D> parseDataPoints(String rawText) {
        List<DataPoint2D> points = new ArrayList<>();
        if (rawText == null || rawText.isBlank()) return points;

        String[] lines = rawText.split("\\r?\\n");
        for (String line : lines) {
            String[] tokens = line.trim().split("[,\\s]+");
            if (tokens.length >= 2) {
                try {
                    double x = Double.parseDouble(tokens[0]);
                    double y = Double.parseDouble(tokens[1]);
                    points.add(new DataPoint2D(x, y));
                } catch (NumberFormatException ignored) {
                    // Skip header or malformed line
                }
            }
        }
        return points;
    }

    @FXML
    void handleLoadSampleData(ActionEvent event) {
        inputDataArea.setText("1.0, 2.1\n2.0, 3.9\n3.0, 6.1\n4.0, 8.2\n5.0, 9.8\n6.0, 12.4");
    }
}