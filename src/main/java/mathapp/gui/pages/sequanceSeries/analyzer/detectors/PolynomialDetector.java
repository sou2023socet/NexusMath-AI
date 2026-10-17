package mathapp.gui.pages.sequanceSeries.analyzer.detectors;

import mathapp.gui.pages.sequanceSeries.analyzer.DetectionResult;
import mathapp.gui.pages.sequanceSeries.analyzer.MathUtils;
import mathapp.gui.pages.sequanceSeries.analyzer.SequenceType;
import mathapp.gui.pages.sequanceSeries.analyzer.models.PolynomialSequence;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Repeatedly differences the series (Delta^0, Delta^1, Delta^2, ...) until a
 * row becomes constant. The degree at which that happens is the polynomial's
 * degree; the leading value of every row feeds Newton's Forward Difference
 * Formula in PolynomialSequence. Degree is capped at 5 and at least 2 equal
 * values are required in the final row to avoid over-fitting short inputs.
 *
 * Example: 1, 4, 9, 16, 25 (squares) -> Delta^2 constant (=2) -> degree 2.
 */
public class PolynomialDetector implements SequenceDetector {

    private static final int MAX_DEGREE = 5;

    @Override
    public DetectionResult tryDetect(double[] terms) {
        int maxK = Math.min(MAX_DEGREE, terms.length - 2);
        if (maxK < 1) return null;

        List<double[]> rows = new ArrayList<>();
        rows.add(terms);

        int foundDegree = -1;
        double[] row = terms;
        for (int k = 1; k <= maxK; k++) {
            double[] next = MathUtils.firstDifference(row);
            rows.add(next);
            row = next;
            if (next.length >= 2 && MathUtils.isConstant(next)) {
                foundDegree = k;
                break;
            }
        }
        if (foundDegree == -1) return null;

        double[] leadingDiffs = new double[foundDegree + 1];
        for (int i = 0; i <= foundDegree; i++) leadingDiffs[i] = rows.get(i)[0];

        int confirmingPoints = rows.get(foundDegree).length;
        double confidence = Math.min(1.0, 0.5 + 0.1 * confirmingPoints);

        Map<String, Double> params = new LinkedHashMap<>();
        params.put("degree", (double) foundDegree);
        for (int i = 0; i <= foundDegree; i++) params.put("delta" + i + "_leading", leadingDiffs[i]);

        PolynomialSequence model = new PolynomialSequence(terms, foundDegree, leadingDiffs);

        return new DetectionResult(SequenceType.POLYNOMIAL, params, confidence, model,
            "Degree-" + foundDegree + " polynomial: Delta^" + foundDegree + " is constant across "
                + confirmingPoints + " confirming point(s).");
    }
}
