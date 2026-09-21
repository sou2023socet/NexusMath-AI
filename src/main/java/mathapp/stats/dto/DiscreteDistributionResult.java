package mathapp.stats.dto;

import java.util.Map;

/**
 * Immutable DTO encapsulating numerical properties, distribution parameters, 
 * evaluated PMF/CDF values, and step-by-step reports for discrete distributions.
 */
public record DiscreteDistributionResult(
        String distributionName,
        Map<String, Number> parameters,
        int k,
        double pmf,
        double cdf,
        double mean,
        double variance,
        double skewness,
        double kurtosis,
        Map<Integer, Double> pmfTable,
        String latexFormula,
        String stepByStepReport
) {}