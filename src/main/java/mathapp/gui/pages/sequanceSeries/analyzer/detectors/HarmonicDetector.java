package mathapp.gui.pages.sequanceSeries.analyzer.detectors;

import mathapp.gui.pages.sequanceSeries.analyzer.DetectionResult;
import mathapp.gui.pages.sequanceSeries.analyzer.MathUtils;
import mathapp.gui.pages.sequanceSeries.analyzer.SequenceType;
import mathapp.gui.pages.sequanceSeries.analyzer.models.HarmonicSequence;

import java.util.LinkedHashMap;
import java.util.Map;

public class HarmonicDetector implements SequenceDetector {

    @Override
    public DetectionResult tryDetect(double[] terms) {
        if (terms.length < 3) return null;
        if (MathUtils.containsZero(terms)) return null; // reciprocal undefined

        double[] reciprocals = new double[terms.length];
        for (int i = 0; i < terms.length; i++) reciprocals[i] = 1.0 / terms[i];

        double[] diffs = MathUtils.firstDifference(reciprocals);
        if (!MathUtils.isConstant(diffs)) return null;

        double aPrime = reciprocals[0];
        double dPrime = diffs[0];

        // A "harmonic" sequence with dPrime ~ 0 is really just a constant sequence;
        // let ArithmeticDetector claim that trivial case instead.
        if (MathUtils.approxEquals(dPrime, 0.0)) return null;

        Map<String, Double> params = new LinkedHashMap<>();
        params.put("reciprocalFirstTerm", aPrime);
        params.put("reciprocalCommonDifference", dPrime);

        HarmonicSequence model = new HarmonicSequence(terms, aPrime, dPrime);
        double confidence = terms.length >= 5 ? 1.0 : 0.85;

        return new DetectionResult(SequenceType.HARMONIC, params, confidence, model,
            "Reciprocals form an AP with common difference " + dPrime + ".");
    }
}
