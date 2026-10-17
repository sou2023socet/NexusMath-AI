package mathapp.gui.pages.sequanceSeries.analyzer.detectors;

import mathapp.gui.pages.sequanceSeries.analyzer.DetectionResult;
import mathapp.gui.pages.sequanceSeries.analyzer.MathUtils;
import mathapp.gui.pages.sequanceSeries.analyzer.SequenceType;
import mathapp.gui.pages.sequanceSeries.analyzer.models.LinearRecurrenceSequence;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Detects a_n = c1*a_(n-1) + c2*a_(n-2) (Fibonacci-style, but general
 * coefficients, not just 1,1). Runs AFTER ArithmeticoGeometricDetector in the
 * pipeline, so the repeated-root (AGP) case has already been claimed by then
 * — this handles distinct-real-root and complex-root cases.
 *
 * Example: 1, 1, 2, 3, 5, 8  -> c1=1, c2=1
 */
public class LinearRecurrenceDetector implements SequenceDetector {

    @Override
    public DetectionResult tryDetect(double[] terms) {
        if (terms.length < 5) return null; // solve from 4, verify at least 1 more

        double a1 = terms[0], a2 = terms[1], a3 = terms[2], a4 = terms[3];
        double[] coeffs = RecurrenceCoefficientSolver.solve(a1, a2, a3, a4);
        if (coeffs == null) return null;
        double c1 = coeffs[0], c2 = coeffs[1];

        // Verify by iterating the recurrence forward, not via closed-form roots,
        // to avoid compounding floating point error from sqrt/roots during verification.
        double prev2 = a1, prev1 = a2;
        int verified = 0;
        for (int i = 2; i < terms.length; i++) {
            double predicted = c1 * prev1 + c2 * prev2;
            if (!MathUtils.approxEquals(predicted, terms[i])) return null;
            prev2 = prev1;
            prev1 = terms[i];
            verified++;
        }

        Map<String, Double> params = new LinkedHashMap<>();
        params.put("c1", c1);
        params.put("c2", c2);
        params.put("a1", a1);
        params.put("a2", a2);

        LinearRecurrenceSequence model = new LinearRecurrenceSequence(terms, a1, a2, c1, c2);
        double confidence = verified >= 3 ? 1.0 : 0.8;

        return new DetectionResult(SequenceType.LINEAR_RECURRENCE, params, confidence, model,
            String.format("a_n = %.6g*a_(n-1) + %.6g*a_(n-2), verified against %d term(s).", c1, c2, verified));
    }
}
