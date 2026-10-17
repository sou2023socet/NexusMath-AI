package mathapp.gui.pages.sequanceSeries.analyzer.detectors;

import mathapp.gui.pages.sequanceSeries.analyzer.DetectionResult;
import mathapp.gui.pages.sequanceSeries.analyzer.MathUtils;
import mathapp.gui.pages.sequanceSeries.analyzer.SequenceType;
import mathapp.gui.pages.sequanceSeries.analyzer.models.ArithmeticSequence;

import java.util.LinkedHashMap;
import java.util.Map;

public class ArithmeticDetector implements SequenceDetector {

    @Override
    public DetectionResult tryDetect(double[] terms) {
        if (terms.length < 3) return null;

        double[] diffs = MathUtils.firstDifference(terms);
        if (!MathUtils.isConstant(diffs)) return null;

        double a = terms[0];
        double d = diffs[0];

        Map<String, Double> params = new LinkedHashMap<>();
        params.put("a", a);
        params.put("d", d);

        ArithmeticSequence model = new ArithmeticSequence(terms, a, d);
        double confidence = terms.length >= 5 ? 1.0 : 0.85;

        return new DetectionResult(SequenceType.ARITHMETIC, params, confidence, model,
            "Constant first difference (d = " + d + ") across all " + diffs.length + " gaps.");
    }
}
