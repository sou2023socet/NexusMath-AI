package mathapp.gui.pages.sequanceSeries.analyzer.detectors;

import mathapp.gui.pages.sequanceSeries.analyzer.DetectionResult;
import mathapp.gui.pages.sequanceSeries.analyzer.MathUtils;
import mathapp.gui.pages.sequanceSeries.analyzer.SequenceType;
import mathapp.gui.pages.sequanceSeries.analyzer.models.ArithmeticoGeometricSequence;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Key algebraic fact this detector relies on: a sequence of the form
 * a_n = (a + (n-1)d) * r^(n-1) ALWAYS satisfies the homogeneous order-2
 * recurrence a_n = 2r*a_(n-1) - r^2*a_(n-2), i.e. its characteristic equation
 * x^2 - c1*x - c2 = 0 has a REPEATED root equal to r.
 *
 * So: solve c1, c2 the same way a generic order-2 recurrence would, check the
 * discriminant is ~0 (repeated root), extract r = c1/2, then recover a and d
 * from the first two terms and verify the rest.
 *
 * Example: 1, 4, 12, 32, 80  (a=1, d=1, r=2)
 */
public class ArithmeticoGeometricDetector implements SequenceDetector {

    @Override
    public DetectionResult tryDetect(double[] terms) {
        if (terms.length < 5) return null; // need >=1 verification term beyond the 4 used to solve

        double a1 = terms[0], a2 = terms[1], a3 = terms[2], a4 = terms[3];
        double[] coeffs = RecurrenceCoefficientSolver.solve(a1, a2, a3, a4);
        if (coeffs == null) return null;
        double c1 = coeffs[0], c2 = coeffs[1];

        double discriminant = c1 * c1 + 4 * c2;
        if (!MathUtils.approxEquals(discriminant, 0.0)) return null; // not a repeated root -> not AGP-shaped

        double r = c1 / 2.0;
        if (MathUtils.approxEquals(r, 0.0)) return null; // degenerate

        double a = a1;
        double d = a2 / r - a;

        int verified = 0;
        for (int i = 0; i < terms.length; i++) {
            double predicted = (a + i * d) * Math.pow(r, i);
            if (!MathUtils.approxEquals(predicted, terms[i])) return null;
            verified++;
        }

        Map<String, Double> params = new LinkedHashMap<>();
        params.put("a", a);
        params.put("d", d);
        params.put("r", r);

        ArithmeticoGeometricSequence model = new ArithmeticoGeometricSequence(terms, a, d, r);
        double confidence = verified >= terms.length ? 1.0 : 0.8;

        return new DetectionResult(SequenceType.ARITHMETICO_GEOMETRIC, params, confidence, model,
            String.format("(a + (n-1)d)*r^(n-1) with a=%.6g, d=%.6g, r=%.6g, verified all %d terms.",
                a, d, r, verified));
    }
}
