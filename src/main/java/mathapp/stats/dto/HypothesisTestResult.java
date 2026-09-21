package mathapp.stats.dto;

/**
 * Immutable DTO encapsulating hypothesis test results, critical bounds, p-values,
 * degrees of freedom, decision status, and markdown reports.
 */
public record HypothesisTestResult(
        String testName,
        double testStatistic,
        double pValue,
        double criticalValue,
        double alpha,
        int degreesOfFreedom,
        boolean rejectNull,
        double ciLower,
        double ciUpper,
        String nullHypothesis,
        String alternativeHypothesis,
        String stepByStepReport
) {}