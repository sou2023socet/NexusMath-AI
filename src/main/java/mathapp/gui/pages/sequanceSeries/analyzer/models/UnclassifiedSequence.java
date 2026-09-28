package mathapp.gui.pages.sequanceSeries.analyzer.models;

import mathapp.gui.pages.sequanceSeries.analyzer.SequenceModel;
import mathapp.gui.pages.sequanceSeries.analyzer.SequenceType;

/**
 * Returned when nothing in the pipeline could explain the series with
 * sufficient confidence. It deliberately does NOT fabricate a formula —
 * calling nthTerm/sumOfN throws, because pretending to extrapolate an
 * unclassified series would be worse than admitting the engine doesn't know.
 */
public class UnclassifiedSequence extends SequenceModel {

    private final String diagnosticNote;

    public UnclassifiedSequence(double[] originalTerms, String diagnosticNote) {
        super(originalTerms);
        this.diagnosticNote = diagnosticNote;
    }

    public String getDiagnosticNote() { return diagnosticNote; }

    @Override public SequenceType getType() { return SequenceType.UNCLASSIFIED; }

    @Override public double nthTerm(int n) {
        throw new UnsupportedOperationException("Cannot extrapolate: " + diagnosticNote);
    }

    @Override public double sumOfN(int n) {
        throw new UnsupportedOperationException("Cannot sum: " + diagnosticNote);
    }

    @Override public String getGeneralTermFormula() {
        return "Unknown — no pattern matched. " + diagnosticNote;
    }

    @Override public String getSumFormula() {
        return "Unknown — no pattern matched.";
    }
}
