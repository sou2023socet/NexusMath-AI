package mathapp.gui.pages.sequanceSeries.analyzer.detectors;

import mathapp.gui.pages.sequanceSeries.analyzer.DetectionResult;

/**
 * One node in the Chain of Responsibility. Returns null if this detector's
 * family clearly doesn't fit (so the analyzer can just try the next one),
 * or a DetectionResult (with its own confidence score) if it does.
 */
public interface SequenceDetector {
    DetectionResult tryDetect(double[] terms);
}
