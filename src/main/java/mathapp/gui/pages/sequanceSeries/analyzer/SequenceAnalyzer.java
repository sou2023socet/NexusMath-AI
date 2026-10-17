package mathapp.gui.pages.sequanceSeries.analyzer;

import mathapp.gui.pages.sequanceSeries.analyzer.detectors.*;
import mathapp.gui.pages.sequanceSeries.analyzer.models.DifferenceReducedSequence;
import mathapp.gui.pages.sequanceSeries.analyzer.models.UnclassifiedSequence;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Entry point of the whole engine. Pipeline order (Section 10 of the design):
 *
 *   1. Try every direct detector (AP, GP, HP, AGP, Affine recurrence,
 *      Linear recurrence, Polynomial) — cheapest and most explainable first.
 *   2. If none matched with sufficient confidence, recursively analyze the
 *      FIRST DIFFERENCE series (Section 6) — this catches things like
 *      2,5,11,23,47 being a plain affine recurrence directly, but also
 *      catches genuinely harder cases where only the difference-of-difference
 *      (or deeper) reveals a clean pattern.
 *   3. Recursion is capped in depth to avoid chasing noise indefinitely.
 *   4. If nothing at any depth matches, return UNCLASSIFIED with a
 *      diagnostic note instead of silently guessing.
 *
 * NOTE: composite/interleaved-stream detection and piecewise/block-rule
 * detection (Sections 7-8 of the design) are intentionally NOT wired in yet —
 * they are a planned next iteration once this core engine is validated,
 * alongside "odd one out" anomaly detection.
 */
public class SequenceAnalyzer {

    private static final int MAX_RECURSION_DEPTH = 4;
    private static final double CONFIDENCE_THRESHOLD = 0.6;

    private final List<SequenceDetector> directDetectors;

    public SequenceAnalyzer() {
        directDetectors = new ArrayList<>();
        // Order matters: more specific / cheaper families first.
        directDetectors.add(new ArithmeticDetector());
        directDetectors.add(new GeometricDetector());
        directDetectors.add(new HarmonicDetector());
        directDetectors.add(new ArithmeticoGeometricDetector()); // repeated-root case, before generic recurrence
        directDetectors.add(new AffineRecurrenceDetector());
        directDetectors.add(new LinearRecurrenceDetector());
        directDetectors.add(new PolynomialDetector());
    }

    public DetectionResult analyze(double[] terms) {
        return analyzeInternal(terms, 0);
    }

    /**
     * Convenience overload for int input (e.g. 2,5,11,23,47 typed as ints).
     * int -> double widening is always exact (no precision loss), so this is
     * a safe, lossless conversion before handing off to the core engine.
     */
    public DetectionResult analyze(int[] terms) {
        return analyzeInternal(toDoubleArray(terms), 0);
    }

    /**
     * Convenience overload for long input. long -> double CAN lose precision
     * for magnitudes beyond 2^53 (~9.007e15) since double has a 53-bit
     * mantissa; that is an inherent limit of using double as the engine's
     * internal numeric type, not a bug in this conversion.
     */
    public DetectionResult analyze(long[] terms) {
        return analyzeInternal(toDoubleArray(terms), 0);
    }

    /**
     * Convenience overload for float input. float -> double widening is
     * always exact (double is a strict superset of float's precision).
     */
    public DetectionResult analyze(float[] terms) {
        return analyzeInternal(toDoubleArray(terms), 0);
    }

    private static double[] toDoubleArray(int[] src) {
        double[] out = new double[src.length];
        for (int i = 0; i < src.length; i++) out[i] = src[i];
        return out;
    }

    private static double[] toDoubleArray(long[] src) {
        double[] out = new double[src.length];
        for (int i = 0; i < src.length; i++) out[i] = src[i];
        return out;
    }

    private static double[] toDoubleArray(float[] src) {
        double[] out = new double[src.length];
        for (int i = 0; i < src.length; i++) out[i] = src[i];
        return out;
    }

    private DetectionResult analyzeInternal(double[] terms, int depth) {
        if (terms.length < 2) {
            return DetectionResult.unclassified("Not enough terms to analyze (need at least 2).");
        }

        DetectionResult best = null;
        for (SequenceDetector detector : directDetectors) {
            DetectionResult candidate;
            try {
                candidate = detector.tryDetect(terms);
            } catch (RuntimeException ex) {
                candidate = null; // a detector failing (e.g. numeric edge case) should never crash the pipeline
            }
            if (candidate != null && candidate.getConfidence() >= CONFIDENCE_THRESHOLD) {
                if (best == null || candidate.getConfidence() > best.getConfidence()) {
                    best = candidate;
                }
            }
        }
        if (best != null) return best;

        // Section 6: recursive difference-chain reduction.
        if (depth < MAX_RECURSION_DEPTH && terms.length >= 3) {
            double[] diff = MathUtils.firstDifference(terms);
            DetectionResult subResult = analyzeInternal(diff, depth + 1);

            if (subResult.getType() != SequenceType.UNCLASSIFIED) {
                double discountedConfidence = Math.max(0.5, subResult.getConfidence() - 0.15 * (depth + 1));

                DifferenceReducedSequence model = new DifferenceReducedSequence(
                    terms, terms[0], subResult.getModel(), depth + 1);

                Map<String, Double> params = new LinkedHashMap<>(subResult.getParameters());
                params.put("firstTerm", terms[0]);
                params.put("reductionDepth", (double) (depth + 1));

                return new DetectionResult(SequenceType.DIFFERENCE_REDUCED, params, discountedConfidence, model,
                    "No direct pattern on the original terms; the difference series (depth " + (depth + 1)
                        + ") resolved as " + subResult.getType() + " -> " + subResult.getNote());
            }
        }

        UnclassifiedSequence fallbackModel = new UnclassifiedSequence(terms,
            "No known family matched directly or within " + MAX_RECURSION_DEPTH + " levels of differencing.");
        return new DetectionResult(SequenceType.UNCLASSIFIED, new LinkedHashMap<>(), 0.0, fallbackModel,
            fallbackModel.getDiagnosticNote());
    }
}
