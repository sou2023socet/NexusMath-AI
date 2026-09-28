package mathapp.gui.pages.sequanceSeries.analyzer.models;

import mathapp.gui.pages.sequanceSeries.analyzer.MathUtils;
import mathapp.gui.pages.sequanceSeries.analyzer.SequenceModel;
import mathapp.gui.pages.sequanceSeries.analyzer.SequenceType;

/**
 * Any sequence whose k-th finite difference is constant is a degree-k
 * polynomial in n. Newton's Forward Difference Formula reconstructs it from
 * just the LEADING value of each difference row (leadingDiffs[i] = the i-th
 * difference row's first entry):
 *
 *     a_n = sum_{i=0}^{k} C(n-1, i) * leadingDiffs[i]
 *
 * The sum formula uses the hockey-stick identity ( sum_{m=0}^{n-1} C(m,i) =
 * C(n, i+1) ) to get a closed form instead of a numeric loop:
 *
 *     S_n = sum_{i=0}^{k} C(n, i+1) * leadingDiffs[i]
 */
public class PolynomialSequence extends SequenceModel {

    private final int degree;
    private final double[] leadingDiffs; // size degree+1

    public PolynomialSequence(double[] originalTerms, int degree, double[] leadingDiffs) {
        super(originalTerms);
        this.degree = degree;
        this.leadingDiffs = leadingDiffs;
    }

    public int getDegree() { return degree; }
    public double[] getLeadingDiffs() { return leadingDiffs; }

    @Override public SequenceType getType() { return SequenceType.POLYNOMIAL; }

    @Override public double nthTerm(int n) {
        double sum = 0.0;
        for (int i = 0; i <= degree; i++) {
            sum += MathUtils.binomial(n - 1, i) * leadingDiffs[i];
        }
        return sum;
    }

    @Override public double sumOfN(int n) {
        double sum = 0.0;
        for (int i = 0; i <= degree; i++) {
            sum += MathUtils.binomial(n, i + 1) * leadingDiffs[i];
        }
        return sum;
    }

    @Override public String getGeneralTermFormula() {
        StringBuilder sb = new StringBuilder("a_n = ");
        for (int i = 0; i <= degree; i++) {
            if (i > 0) sb.append(" + ");
            sb.append(String.format("C(n-1,%d)*%.6g", i, leadingDiffs[i]));
        }
        sb.append(String.format("   [degree %d polynomial, via Newton's forward difference]", degree));
        return sb.toString();
    }

    @Override public String getSumFormula() {
        StringBuilder sb = new StringBuilder("S_n = ");
        for (int i = 0; i <= degree; i++) {
            if (i > 0) sb.append(" + ");
            sb.append(String.format("C(n,%d)*%.6g", i + 1, leadingDiffs[i]));
        }
        return sb.toString();
    }
}
