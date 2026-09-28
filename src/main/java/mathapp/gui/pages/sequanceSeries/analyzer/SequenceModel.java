package mathapp.gui.pages.sequanceSeries.analyzer;

/**
 * Strategy interface (as an abstract class so shared helpers like
 * generateNext/getFullSeriesWithNext don't need to be reimplemented by every
 * concrete family). Every detected pattern — AP, GP, polynomial, recurrence,
 * difference-reduced, etc. — is exposed through exactly this contract, so the
 * rest of the application never needs to know which concrete type it holds.
 */
public abstract class SequenceModel {

    protected final double[] originalTerms;

    protected SequenceModel(double[] originalTerms) {
        this.originalTerms = originalTerms;
    }

    public abstract SequenceType getType();

    /** 1-indexed: nthTerm(1) is the first term of the series. */
    public abstract double nthTerm(int n);

    /** Sum of the first n terms (1-indexed). */
    public abstract double sumOfN(int n);

    public abstract String getGeneralTermFormula();

    public abstract String getSumFormula();

    public double[] getOriginalTerms() {
        return originalTerms;
    }

    /**
     * Produces the next `count` terms after the given series, using the
     * detected rule — this answers "as per user input 1 or 3, show next
     * terms" from the requirements.
     */
    public double[] generateNext(int count) {
        int start = originalTerms.length + 1;
        double[] next = new double[count];
        for (int i = 0; i < count; i++) {
            next[i] = nthTerm(start + i);
        }
        return next;
    }

    /** Original series followed by `count` freshly generated terms. */
    public double[] getFullSeriesWithNext(int count) {
        double[] next = generateNext(count);
        double[] full = new double[originalTerms.length + next.length];
        System.arraycopy(originalTerms, 0, full, 0, originalTerms.length);
        System.arraycopy(next, 0, full, originalTerms.length, next.length);
        return full;
    }
}
