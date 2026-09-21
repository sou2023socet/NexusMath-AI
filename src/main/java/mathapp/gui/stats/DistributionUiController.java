package mathapp.gui.stats;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import mathapp.stats.engine.DistributionEngineImpl.DistributionEvaluationResult;
import mathapp.stats.engine.DistributionEngineImpl.DistributionPoint;
import mathapp.stats.facade.StatsServiceFacade;

/**
 * JavaFX Controller for Plotting Binomial and Gaussian/Normal Distribution Curves.
 */
public class DistributionUiController {

    @FXML private RadioButton radioNormal;
    @FXML private RadioButton radioBinomial;
    @FXML private ToggleGroup distributionToggleGroup;

    // Normal Inputs
    @FXML private TextField meanField;
    @FXML private TextField stdDevField;
    @FXML private TextField xValField;

    // Binomial Inputs
    @FXML private TextField nTrialsField;
    @FXML private TextField probPField;
    @FXML private TextField kSuccessesField;

    @FXML private LineChart<Number, Number> continuousChart;
    @FXML private BarChart<String, Number> discreteChart;
    @FXML private TextArea distributionReportArea;

    private final StatsServiceFacade statsFacade = new StatsServiceFacade();

    @FXML
    public void initialize() {
        radioNormal.setSelected(true);
        toggleDistributionType();
    }

    @FXML
    void handleDistributionSelection(ActionEvent event) {
        toggleDistributionType();
    }

    private void toggleDistributionType() {
        boolean isNormal = radioNormal.isSelected();
        meanField.setDisable(!isNormal);
        stdDevField.setDisable(!isNormal);
        xValField.setDisable(!isNormal);

        nTrialsField.setDisable(isNormal);
        probPField.setDisable(isNormal);
        kSuccessesField.setDisable(isNormal);

        continuousChart.setVisible(isNormal);
        discreteChart.setVisible(!isNormal);
    }

    @FXML
    void handlePlotDistribution(ActionEvent event) {
        try {
            if (radioNormal.isSelected()) {
                double mean = Double.parseDouble(meanField.getText().trim());
                double stdDev = Double.parseDouble(stdDevField.getText().trim());
                double x = Double.parseDouble(xValField.getText().trim());

                DistributionEvaluationResult res = statsFacade.evaluateNormalDistribution(mean, stdDev, x);
                distributionReportArea.setText(res.stepByStepReport());

                continuousChart.getData().clear();
                XYChart.Series<Number, Number> pdfSeries = new XYChart.Series<>();
                pdfSeries.setName(String.format("N(μ=%.2f, σ=%.2f)", mean, stdDev));

                for (DistributionPoint pt : res.curvePoints()) {
                    pdfSeries.getData().add(new XYChart.Data<>(pt.x(), pt.pdfOrPmf()));
                }
                continuousChart.getData().add(pdfSeries);

            } else {
                int n = Integer.parseInt(nTrialsField.getText().trim());
                double p = Double.parseDouble(probPField.getText().trim());
                int k = Integer.parseInt(kSuccessesField.getText().trim());

                DistributionEvaluationResult res = statsFacade.evaluateBinomialDistribution(n, p, k);
                distributionReportArea.setText(res.stepByStepReport());

                discreteChart.getData().clear();
                XYChart.Series<String, Number> pmfSeries = new XYChart.Series<>();
                pmfSeries.setName(String.format("B(n=%d, p=%.2f)", n, p));

                for (DistributionPoint pt : res.curvePoints()) {
                    pmfSeries.getData().add(new XYChart.Data<>(String.valueOf((int) pt.x()), pt.pdfOrPmf()));
                }
                discreteChart.getData().add(pmfSeries);
            }
        } catch (Exception e) {
            distributionReportArea.setText("❌ Evaluation Error: " + e.getMessage());
        }
    }
}