package mathapp.stats.dto;

import java.util.Map;

/**
 * Immutable DTO encapsulating numerical properties, distribution parameters,
 * evaluated PDF, CDF, Quantile, and step-by-step mathematical reports for continuous distributions.
 */
public record ContinuousDistributionResult(
        String distributionName,
        Map<String, Number> parameters,
        double evaluatedX,
        double pdf,
        double cdf,
        double quantile,
        double mean,
        double variance,
        double skewness,
        double excessKurtosis,
        String latexFormula,
        String stepByStepReport
) {}