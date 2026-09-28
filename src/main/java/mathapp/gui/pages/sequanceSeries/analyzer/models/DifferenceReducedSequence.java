package mathapp.gui.pages.sequanceSeries.analyzer.models;

import mathapp.gui.pages.sequanceSeries.analyzer.SequenceModel;
import mathapp.gui.pages.sequanceSeries.analyzer.SequenceType;

/**
 * Used when NO direct detector matches the original series, but the
 * difference series (recursively re-analyzed) DOES resolve into a known
 * pattern. This is the self-similar "difference of nth term is itself a
 * sequence" design: reconstruction is done by telescoping —
 *
 *     a_n = a_1 + sum_{k=1}^{n-1} diff_k
 *
 * and since diffModel.sumOfN(n-1) already IS that sum (first n-1 terms of the
 * difference sequence), reconstruction is a single call, no inner loop needed
 * for nthTerm. sumOfN still loops over nthTerm because a general closed-form
 * double-sum isn't guaranteed for an arbitrary wrapped model.
 */
public class DifferenceReducedSequence extends SequenceModel {

    private final double firstTerm;
    private final SequenceModel diffModel; // model fitted to the difference series
    private final int reductionDepth;

    public DifferenceReducedSequence(double[] originalTerms, double firstTerm,
                                      SequenceModel diffModel, int reductionDepth) {
        super(originalTerms);
        this.firstTerm = firstTerm;
        this.diffModel = diffModel;
        this.reductionDepth = reductionDepth;
    }

    public SequenceModel getDiffModel() { return diffModel; }
    public int getReductionDepth() { return reductionDepth; }

    @Override public SequenceType getType() { return SequenceType.DIFFERENCE_REDUCED; }

    @Override public double nthTerm(int n) {
        if (n <= 1) return firstTerm;
        return firstTerm + diffModel.sumOfN(n - 1);
    }

    @Override public double sumOfN(int n) {
        double sum = 0.0;
        for (int i = 1; i <= n; i++) sum += nthTerm(i);
        return sum;
    }

    @Override public String getGeneralTermFormula() {
        return String.format(
            "a_n = %.6g + sum_{k=1}^{n-1} d_k,  where d_k follows: %s   [difference-chain depth %d]",
            firstTerm, diffModel.getGeneralTermFormula(), reductionDepth);
    }

    @Override public String getSumFormula() {
        return "S_n = sum_{k=1..n} a_k  (telescoped from the difference series above)";
    }
}
