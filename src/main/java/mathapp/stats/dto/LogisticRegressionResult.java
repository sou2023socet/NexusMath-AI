package mathapp.stats.dto;

import mathapp.stats.core.StatisticalState.DataPoint2D;
import java.util.List;

/**
 * Immutable DTO containing classification performance metrics, confusion matrix counts,
 * and odds ratios for logistic regression models.
 */
public record LogisticRegressionResult(
        double intercept,
        double slope,
        double oddsRatio,
        double logLikelihood,
        double decisionThreshold,
        int truePositives,
        int falsePositives,
        int trueNegatives,
        int falseNegatives,
        double accuracy,
        double precision,
        double recall,
        double specificity,
        double f1Score,
        List<DataPoint2D> predictedProbabilities,
        double areaUnderRoc,
        String stepByStepReport
) {}