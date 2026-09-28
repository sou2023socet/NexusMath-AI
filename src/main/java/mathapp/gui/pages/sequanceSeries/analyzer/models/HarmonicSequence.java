package mathapp.gui.pages.sequanceSeries.analyzer.models;

import mathapp.gui.pages.sequanceSeries.analyzer.SequenceModel;
import mathapp.gui.pages.sequanceSeries.analyzer.SequenceType;

/**
 * a_n = 1 / (a' + (n-1)d'), where a', d' are the first term and common
 * difference of the AP formed by the RECIPROCALS of this sequence.
 * The harmonic series has no elementary closed-form sum, so sumOfN is
 * computed numerically (exact, just not a one-line formula).
 */
public class HarmonicSequence extends SequenceModel {

    private final double aPrime;
    private final double dPrime;

    public HarmonicSequence(double[] originalTerms, double aPrime, double dPrime) {
        super(originalTerms);
        this.aPrime = aPrime;
        this.dPrime = dPrime;
    }

    @Override public SequenceType getType() { return SequenceType.HARMONIC; }

    @Override public double nthTerm(int n) { return 1.0 / (aPrime + (n - 1) * dPrime); }

    @Override public double sumOfN(int n) {
        double sum = 0.0;
        for (int i = 1; i <= n; i++) sum += nthTerm(i);
        return sum;
    }

    @Override public String getGeneralTermFormula() {
        return String.format("a_n = 1 / (%.6g + (n-1) * %.6g)", aPrime, dPrime);
    }

    @Override public String getSumFormula() {
        return "S_n = sum_{k=1..n} a_k  (no elementary closed form for harmonic sums; computed numerically)";
    }
}
