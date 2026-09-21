package mathapp.numerical.dto;

import java.util.List;

/**
 * Immutable DTO encapsulating numerical convergence results, iteration counts,
 * function values at critical points, residual errors, and execution steps.
 */
public record RootFindingResult(
        String methodName,
        double estimatedRoot,
        double functionValueAtRoot,
        int iterationsPerformed,
        boolean converged,
        double tolerance,
        List<Double> iterationHistory,
        String latexFormula,
        String stepByStepReport
) {}