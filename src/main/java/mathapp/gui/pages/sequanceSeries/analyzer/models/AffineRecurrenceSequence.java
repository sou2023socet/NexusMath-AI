package mathapp.gui.pages.sequanceSeries.analyzer.models;

import mathapp.gui.pages.sequanceSeries.analyzer.SequenceModel;
import mathapp.gui.pages.sequanceSeries.analyzer.SequenceType;

/**
 * a_n = r * a_(n-1) + c   (r != 1).
 *
 * Solved via the fixed point x* = c / (1 - r), the point where the recurrence
 * would stay still (x* = r*x* + c). Then a_n - x* is a pure geometric
 * sequence with ratio r, giving the closed form:
 *
 *     a_n = r^(n-1) * (a_1 - x*) + x*
 *     S_n = x* * n + (a_1 - x*) * (r^n - 1) / (r - 1)
 *
 * Example: 2, 5, 11, 23, 47, ...  ->  a_n = 2*a_(n-1) + 1
 *          x* = 1/(1-2) = -1  =>  a_n = 3*2^(n-1) - 1
 */
public class AffineRecurrenceSequence extends SequenceModel {

    private final double a1, r, c, xStar;

    public AffineRecurrenceSequence(double[] originalTerms, double a1, double r, double c) {
        super(originalTerms);
        this.a1 = a1;
        this.r = r;
        this.c = c;
        this.xStar = c / (1 - r);
    }

    public double getRatio() { return r; }
    public double getConstant() { return c; }
    public double getFixedPoint() { return xStar; }

    @Override public SequenceType getType() { return SequenceType.AFFINE_RECURRENCE; }

    @Override public double nthTerm(int n) {
        return Math.pow(r, n - 1) * (a1 - xStar) + xStar;
    }

    @Override public double sumOfN(int n) {
        return xStar * n + (a1 - xStar) * (Math.pow(r, n) - 1) / (r - 1);
    }

    @Override public String getGeneralTermFormula() {
        return String.format("a_n = %.6g * (%.6g)^(n-1) + %.6g   [derived from a_n = %.6g*a_(n-1) + %.6g]",
            a1 - xStar, r, xStar, r, c);
    }

    @Override public String getSumFormula() {
        return String.format("S_n = %.6g*n + %.6g * ((%.6g)^n - 1) / (%.6g - 1)",
            xStar, a1 - xStar, r, r);
    }
}
