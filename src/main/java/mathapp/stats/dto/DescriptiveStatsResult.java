package mathapp.stats.dto;

import java.util.List;

/**
 * Immutable DTO containing complete univariate descriptive statistics and step-by-step analysis.
 */
public record DescriptiveStatsResult(
        int sampleSize,
        double arithmeticMean,
        double weightedMean,
        double geometricMean,
        double harmonicMean,
        double trimmedMean,
        double median,
        List<Double> modes,
        double minimum,
        double maximum,
        double range,
        double q1,
        double q3,
        double iqr,
        double semiIqr,
        double variance,
        double standardDeviation,
        double standardErrorOfMean,
        double meanAbsoluteDeviation,
        double rootMeanSquare,
        double coefficientOfVariation,
        double pearsonSkewness,
        double fisherPearsonSkewness,
        double excessKurtosis,
        List<Double> outliersIqr,
        List<Double> outliersZScore,
        boolean isSample,
        String stepByStepReport
) {}