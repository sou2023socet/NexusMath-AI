package mathapp.stats.dto;

import java.math.BigInteger;

/**
 * Immutable DTO encapsulating exact combinatorial counts, probability evaluations,
 * LaTeX formulas, and execution explanations.
 */
public record CombinatoricsResult(
        String operationName,
        BigInteger exactCount,
        Double numericProbability,
        String latexFormula,
        String stepByStepReport
) {}