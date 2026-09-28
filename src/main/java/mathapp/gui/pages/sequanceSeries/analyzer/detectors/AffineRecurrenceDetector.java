package mathapp.gui.pages.sequanceSeries.analyzer.detectors;

import mathapp.gui.pages.sequanceSeries.analyzer.DetectionResult;
import mathapp.gui.pages.sequanceSeries.analyzer.MathUtils;
import mathapp.gui.pages.sequanceSeries.analyzer.SequenceType;
import mathapp.gui.pages.sequanceSeries.analyzer.models.AffineRecurrenceSequence;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Detects a_n = r*a_(n-1) + c by solving r and c from the first three terms,
 * then verifying every remaining term against the recurrence.
 *
 * Example: 2, 5, 11, 23, 47
 *   r = (11-5)/(5-2) = 2,  c = 5 - 2*2 = 1  ->  a_n = 2*a_(n-1) + 1
 */
public class AffineRecurrenceDetector implements SequenceDetector {

    @Override
    public DetectionResult tryDetect(double[] terms) {
        if (terms.length < 4) return null; // need at least 1 verification term

        double a1 = terms[0], a2 = terms[1], a3 = terms[2];
        double denom = a2 - a1;
        if (MathUtils.approxEquals(denom, 0.0)) return null; // avoid div-by-zero / degenerate

        double r = (a3 - a2) / denom;
        if (MathUtils.approxEquals(r, 1.0)) return null; // that's just arithmetic

        double c = a2 - r * a1;

        int verified = 0;
        for (int i = 2; i < terms.length; i++) {
            double predicted = r * terms[i - 1] + c;
            if (!MathUtils.approxEquals(predicted, terms[i])) return null;
            verified++;
        }

        Map<String, Double> params = new LinkedHashMap<>();
        params.put("a1", a1);
        params.put("r", r);
        params.put("c", c);

        AffineRecurrenceSequence model = new AffineRecurrenceSequence(terms, a1, r, c);
        double confidence = verified >= 3 ? 1.0 : 0.75;

        return new DetectionResult(SequenceType.AFFINE_RECURRENCE, params, confidence, model,
            String.format("a_n = %.6g*a_(n-1) + %.6g, verified against %d term(s).", r, c, verified));
    }
}
