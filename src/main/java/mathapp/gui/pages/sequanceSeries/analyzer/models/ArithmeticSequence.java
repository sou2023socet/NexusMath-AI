package mathapp.gui.pages.sequanceSeries.analyzer.models;

import mathapp.gui.pages.sequanceSeries.analyzer.SequenceModel;
import mathapp.gui.pages.sequanceSeries.analyzer.SequenceType;

/** a_n = a + (n-1)d,  S_n = n/2 * [2a + (n-1)d] */
public class ArithmeticSequence extends SequenceModel {

    private final double a;
    private final double d;

    public ArithmeticSequence(double[] originalTerms, double a, double d) {
        super(originalTerms);
        this.a = a;
        this.d = d;
    }

    public double getFirstTerm() { return a; }
    public double getCommonDifference() { return d; }

    @Override public SequenceType getType() { return SequenceType.ARITHMETIC; }

    @Override public double nthTerm(int n) { return a + (n - 1) * d; }

    @Override public double sumOfN(int n) { return n / 2.0 * (2 * a + (n - 1) * d); }

    @Override public String getGeneralTermFormula() {
        return String.format("a_n = %.6g + (n-1) * %.6g", a, d);
    }

    @Override public String getSumFormula() {
        return String.format("S_n = n/2 * [2*%.6g + (n-1)*%.6g]", a, d);
    }
}
