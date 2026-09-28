package mathapp.gui.pages.sequanceSeries.analyzer.models;

import mathapp.gui.pages.sequanceSeries.analyzer.MathUtils;
import mathapp.gui.pages.sequanceSeries.analyzer.SequenceModel;
import mathapp.gui.pages.sequanceSeries.analyzer.SequenceType;

/**
 * a_n = (a + (n-1)d) * r^(n-1)
 * S_n = [a - (a+(n-1)d)*r^n] / (1-r)  +  d*r*(1 - r^(n-1)) / (1-r)^2      (r != 1)
 */
public class ArithmeticoGeometricSequence extends SequenceModel {

    private final double a;
    private final double d;
    private final double r;

    public ArithmeticoGeometricSequence(double[] originalTerms, double a, double d, double r) {
        super(originalTerms);
        this.a = a;
        this.d = d;
        this.r = r;
    }

    @Override public SequenceType getType() { return SequenceType.ARITHMETICO_GEOMETRIC; }

    @Override public double nthTerm(int n) {
        return (a + (n - 1) * d) * Math.pow(r, n - 1);
    }

    @Override public double sumOfN(int n) {
        if (MathUtils.approxEquals(r, 1.0)) {
            // degenerates to a pure arithmetic sum
            return n / 2.0 * (2 * a + (n - 1) * d);
        }
        double rn = Math.pow(r, n);
        double term1 = (a - (a + (n - 1) * d) * rn) / (1 - r);
        double term2 = d * r * (1 - Math.pow(r, n - 1)) / ((1 - r) * (1 - r));
        return term1 + term2;
    }

    @Override public String getGeneralTermFormula() {
        return String.format("a_n = (%.6g + (n-1)*%.6g) * (%.6g)^(n-1)", a, d, r);
    }

    @Override public String getSumFormula() {
        return String.format(
            "S_n = [%.6g - (%.6g+(n-1)*%.6g)*(%.6g)^n] / (1-%.6g) + %.6g*%.6g*(1-(%.6g)^(n-1)) / (1-%.6g)^2",
            a, a, d, r, r, d, r, r, r);
    }
}
