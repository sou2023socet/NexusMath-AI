package mathapp.stats.dto;

/**
 * Immutable DTO encapsulating computed probabilities, density values,
 * critical quantiles, parameters, and step-by-step reports.
 */
public record DistributionResult(
        String distributionName,
        double pdfValue,
        double cdfValue,
        Double quantileValue,
        String latexFormula,
        String stepByStepReport
) {}