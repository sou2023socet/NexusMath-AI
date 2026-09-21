package mathapp.stats.facade;

import mathapp.stats.core.StatisticalState.DataPoint2D;
import mathapp.stats.dto.LogisticRegressionResult;
import mathapp.stats.dto.RegressionResult;
import mathapp.stats.engine.*;
import mathapp.stats.engine.DistributionEngineImpl.DistributionEvaluationResult;
import mathapp.stats.engine.HypothesisEngineImpl.HypothesisTestReport;
import mathapp.stats.engine.ProbabilityAndSetCalculator.*;

import java.util.*;

/**
 * Central thread-safe Facade orchestrating calculations across all sub-engines.
 * Serves as the primary entry point for application UI components and APIs.
 */
public class StatsServiceFacade {

    private final IRegressionEngine regressionEngine;
    private final IProbabilityEngine probabilityEngine;
    private final IDistributionEngine distributionEngine;
    private final IHypothesisEngine hypothesisEngine;

    public StatsServiceFacade() {
        this.regressionEngine = new RegressionEngineImpl();
        this.probabilityEngine = new ProbabilityEngineImpl();
        this.distributionEngine = new DistributionEngineImpl();
        this.hypothesisEngine = new HypothesisEngineImpl();
    }

    public StatsServiceFacade(
            IRegressionEngine regressionEngine,
            IProbabilityEngine probabilityEngine,
            IDistributionEngine distributionEngine,
            IHypothesisEngine hypothesisEngine
    ) {
        this.regressionEngine = Objects.requireNonNull(regressionEngine);
        this.probabilityEngine = Objects.requireNonNull(probabilityEngine);
        this.distributionEngine = Objects.requireNonNull(distributionEngine);
        this.hypothesisEngine = Objects.requireNonNull(hypothesisEngine);
    }

    // =========================================================================
    // 1. CORRELATION & REGRESSION DELEGATION
    // =========================================================================

    public RegressionResult fitLinearModel(List<DataPoint2D> points) {
        return regressionEngine.fitLinearRegression(points);
    }

    public RegressionResult fitPolynomialModel(List<DataPoint2D> points, int degree) {
        return regressionEngine.fitPolynomialRegression(points, degree);
    }

    public RegressionResult fitExponentialModel(List<DataPoint2D> points) {
        return regressionEngine.fitExponentialRegression(points);
    }

    public LogisticRegressionResult fitLogisticModel(List<DataPoint2D> points, double threshold) {
        return regressionEngine.fitLogisticRegression(points, threshold);
    }

    // =========================================================================
    // 2. PROBABILITY & BAYES DELEGATION
    // =========================================================================

    public Venn2SetResult solveVenn2(double pA, double pB, double pIntersect) {
        return probabilityEngine.calculateVenn2Sets(pA, pB, pIntersect);
    }

    public Venn3SetResult solveVenn3(
            double pA, double pB, double pC,
            double pAandB, double pBandC, double pAandC,
            double pAllThree
    ) {
        return probabilityEngine.calculateVenn3Sets(pA, pB, pC, pAandB, pBandC, pAandC, pAllThree);
    }

    public ContingencyResult analyzeContingency(double[][] contingencyMatrix) {
        return probabilityEngine.evaluateContingencyTable(contingencyMatrix);
    }

    public BayesResult updateBayes(List<String> hypothesisNames, List<Double> priors, List<Double> likelihoods) {
        return probabilityEngine.updateBayesianPosterior(hypothesisNames, priors, likelihoods);
    }

    // =========================================================================
    // 3. PROBABILITY DISTRIBUTIONS DELEGATION
    // =========================================================================

    public DistributionEvaluationResult evaluateBinomialDistribution(int n, double p, int k) {
        return distributionEngine.evaluateBinomial(n, p, k);
    }

    public DistributionEvaluationResult evaluateNormalDistribution(double mean, double stdDev, double x) {
        return distributionEngine.evaluateNormal(mean, stdDev, x);
    }

    // =========================================================================
    // 4. HYPOTHESIS TESTING DELEGATION
    // =========================================================================

    public HypothesisTestReport performOneSampleTTest(double[] sample, double nullMean, double alpha) {
        return hypothesisEngine.runOneSampleTTest(sample, nullMean, alpha);
    }

    public HypothesisTestReport performWelchTTest(double[] sample1, double[] sample2, double alpha) {
        return hypothesisEngine.runWelchTTest(sample1, sample2, alpha);
    }

    public HypothesisTestReport performANOVA(List<double[]> groups, double alpha) {
        return hypothesisEngine.runOneWayANOVA(groups, alpha);
    }

    // =========================================================================
    // 5. COMPOSITE EXECUTIVE ANALYSIS REPORT GENERATOR
    // =========================================================================

    /**
     * Generates a combined Markdown summary report for a bivariate dataset containing both
     * descriptive statistics, linear fit, and standard inference.
     */
    public String generateCompositeBivariateReport(List<DataPoint2D> dataset, String xLabel, String yLabel) {
        if (dataset == null || dataset.isEmpty()) {
            return "### Error\nDataset is empty or null.";
        }

        RegressionResult regResult = fitLinearModel(dataset);
        double[] xVals = dataset.stream().mapToDouble(DataPoint2D::x).toArray();
        double[] yVals = dataset.stream().mapToDouble(DataPoint2D::y).toArray();

        HypothesisTestReport xTest = performOneSampleTTest(xVals, 0.0, 0.05);
        HypothesisTestReport yTest = performOneSampleTTest(yVals, 0.0, 0.05);

        StringBuilder report = new StringBuilder();
        report.append(String.format("## Executive Bivariate Summary Report: %s vs %s\n\n", yLabel, xLabel));
        report.append(String.format("* **Sample Size ($N$):** `%d` points\n", dataset.size()));
        report.append(String.format("* **Pearson Correlation ($r$):** `%.4f` | **Spearman Rank ($\\rho$):** `%.4f`\n",
                regResult.pearsonR(), regResult.spearmanRho()));
        report.append(String.format("* **Fitted Regression Model:** $%s$\n", regResult.latexFormula()));
        report.append(String.format("* **Coefficient of Determination ($R^2$):** `%.4f` (Adjusted $R^2$: `%.4f`)\n\n",
                regResult.rSquared(), regResult.rSquared()));

        report.append("### Individual Variable Profiles\n\n");
        report.append(String.format("* **%s (X):** Mean = `%.4f`, 95%% CI = `%s`\n", xLabel, xTest.testStatistic(), xTest.confidenceIntervalFormatted()));
        report.append(String.format("* **%s (Y):** Mean = `%.4f`, 95%% CI = `%s`\n\n", yLabel, yTest.testStatistic(), yTest.confidenceIntervalFormatted()));

        report.append("--- \n");
        report.append(regResult.stepByStepReport());

        return report.toString();
    }
}