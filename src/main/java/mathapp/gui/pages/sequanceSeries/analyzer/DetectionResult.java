package mathapp.gui.pages.sequanceSeries.analyzer;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * What every detector (and the top-level analyzer) hands back: which family
 * was found, its parameters (a, d, r, c1, c2, degree...), a confidence score
 * (Section 9 of the design — Occam's-razor ranking needs this), the fully
 * usable SequenceModel, and a human-readable note explaining how it was found
 * (direct match vs. recursive difference-chain reduction, etc.).
 */
public class DetectionResult {

    private final SequenceType type;
    private final Map<String, Double> parameters;
    private final double confidence;
    private final SequenceModel model;
    private final String note;

    public DetectionResult(SequenceType type, Map<String, Double> parameters,
                            double confidence, SequenceModel model, String note) {
        this.type = type;
        this.parameters = parameters;
        this.confidence = confidence;
        this.model = model;
        this.note = note;
    }

    public static DetectionResult unclassified(String note) {
        return new DetectionResult(SequenceType.UNCLASSIFIED, new LinkedHashMap<>(), 0.0, null, note);
    }

    public SequenceType getType() { return type; }
    public Map<String, Double> getParameters() { return parameters; }
    public double getConfidence() { return confidence; }
    public SequenceModel getModel() { return model; }
    public String getNote() { return note; }
}
