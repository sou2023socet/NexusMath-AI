package mathapp.gui.pages.sequanceSeries.analyzer.detectors;

import mathapp.gui.pages.sequanceSeries.analyzer.DetectionResult;
import mathapp.gui.pages.sequanceSeries.analyzer.MathUtils;
import mathapp.gui.pages.sequanceSeries.analyzer.SequenceType;
import mathapp.gui.pages.sequanceSeries.analyzer.models.GeometricSequence;

import java.util.LinkedHashMap;
import java.util.Map;

public class GeometricDetector implements SequenceDetector {

    @Override
    public DetectionResult tryDetect(double[] terms) {
        if (terms.length < 3) return null;
        if (MathUtils.containsZero(terms)) return null; // ratio undefined

        double[] ratios = new double[terms.length - 1];
        for (int i = 0; i < ratios.length; i++) ratios[i] = terms[i + 1] / terms[i];

        if (!MathUtils.isConstant(ratios)) return null;

        double a = terms[0];
        double r = ratios[0];

        Map<String, Double> params = new LinkedHashMap<>();
        params.put("a", a);
        params.put("r", r);

        GeometricSequence model = new GeometricSequence(terms, a, r);
        double confidence = terms.length >= 5 ? 1.0 : 0.85;

        return new DetectionResult(SequenceType.GEOMETRIC, params, confidence, model,
            "Constant ratio (r = " + r + ") across all " + ratios.length + " gaps.");
    }
}
