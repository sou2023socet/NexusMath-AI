package mathapp.stats.dto;

import java.util.List;

/**
 * Immutable DTO encapsulating bivariate correlation metrics, regression parameters,
 * goodness-of-fit indicators, residuals, predicted points, and formatted step-by-step reports.
 */
public record RegressionResult(
        int sampleSize,
        double sampleCovariance,
        double populationCovariance,
        double pearsonR,
        double spearmanRho,
        double rSquared,
        double slope,
        double intercept,
        double standardErrorOfEstimate,
        List<Double> residuals,
        Double predictedX,
        Double predictedY,
        double predictionSE,
        double ciLower,
        double ciUpper,
        String regressionEquation,
        String latexFormula,
        String stepByStepReport
) {}