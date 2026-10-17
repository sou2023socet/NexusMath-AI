package mathapp.gui.pages.sequanceSeries.analyzer.models;

import mathapp.gui.pages.sequanceSeries.analyzer.MathUtils;
import mathapp.gui.pages.sequanceSeries.analyzer.SequenceModel;
import mathapp.gui.pages.sequanceSeries.analyzer.SequenceType;

/** a_n = a * r^(n-1),  S_n = a*(r^n - 1)/(r - 1),  S_infinity = a/(1-r) if |r|<1 */
public class GeometricSequence extends SequenceModel {

    private final double a;
    private final double r;

    public GeometricSequence(double[] originalTerms, double a, double r) {
        super(originalTerms);
        this.a = a;
        this.r = r;
    }

    public double getFirstTerm() { return a; }
    public double getCommonRatio() { return r; }

    @Override public SequenceType getType() { return SequenceType.GEOMETRIC; }

    @Override public double nthTerm(int n) { return a * Math.pow(r, n - 1); }

    @Override public double sumOfN(int n) {
        if (MathUtils.approxEquals(r, 1.0)) return a * n;
        return a * (Math.pow(r, n) - 1) / (r - 1);
    }

    /** Sum to infinity; only mathematically valid when |r| < 1. Returns NaN otherwise. */
    public double sumToInfinity() {
        if (Math.abs(r) < 1.0) return a / (1 - r);
        return Double.NaN;
    }

    @Override public String getGeneralTermFormula() {
        return String.format("a_n = %.6g * (%.6g)^(n-1)", a, r);
    }

    @Override public String getSumFormula() {
        return String.format("S_n = %.6g * ((%.6g)^n - 1) / (%.6g - 1)", a, r, r);
    }
}
