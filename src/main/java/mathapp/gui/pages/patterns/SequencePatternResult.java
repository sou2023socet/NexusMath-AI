package mathapp.gui.pages.patterns;

import java.util.List;

/**
 * Immutable DTO containing detected sequence classifications, explicit closed-form 
 * formulas (a_n and S_n), difference tables, predicted future terms, and reports.
 */
public record SequencePatternResult(
        String patternType,
        String explicitFormula,
        String recursiveFormula,
        String sumFormula,
        List<Double> predictedTerms,
        List<List<Double>> differenceTable,
        boolean isConvergent,
        Double infiniteSumLimit,
        String latexFormula,
        String stepByStepReport
) {}