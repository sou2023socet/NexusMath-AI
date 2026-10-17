package mathapp.gui.pages.sequanceSeries.analyzer.models;

import mathapp.gui.pages.sequanceSeries.analyzer.MathUtils;
import mathapp.gui.pages.sequanceSeries.analyzer.SequenceModel;
import mathapp.gui.pages.sequanceSeries.analyzer.SequenceType;

/**
 * a_n = c1*a_(n-1) + c2*a_(n-2)   (n >= 3), seeded by a1, a2.
 *
 * Closed form comes from the characteristic equation x^2 - c1*x - c2 = 0:
 *   - distinct real roots alpha, beta : a_n = A*alpha^(n-1) + B*beta^(n-1)
 *   - repeated real root alpha        : a_n = (A + B*n) * alpha^(n-1)
 *   - complex roots                   : no clean real closed form is used here;
 *                                        the recurrence itself is iterated instead
 *                                        (still exact, just not a one-line formula).
 */
public class LinearRecurrenceSequence extends SequenceModel {

    private final double a1, a2, c1, c2;
    private final boolean complexRoots;
    private final boolean repeatedRoot;
    private final double alpha, beta; // roots (beta unused if repeated)
    private final double A, B;

    public LinearRecurrenceSequence(double[] originalTerms, double a1, double a2, double c1, double c2) {
        super(originalTerms);
        this.a1 = a1;
        this.a2 = a2;
        this.c1 = c1;
        this.c2 = c2;

        double discriminant = c1 * c1 + 4 * c2;
        if (discriminant < 0) {
            complexRoots = true;
            repeatedRoot = false;
            alpha = beta = A = B = Double.NaN;
        } else {
            complexRoots = false;
            double sqrtD = Math.sqrt(discriminant);
            double r1 = (c1 + sqrtD) / 2;
            double r2 = (c1 - sqrtD) / 2;
            if (MathUtils.approxEquals(r1, r2)) {
                repeatedRoot = true;
                alpha = r1;
                beta = Double.NaN;
                // a_n = (A + B*n) * alpha^(n-1): a1 = (A+B), a2 = (A+2B)*alpha
                double Bsolved, Asolved;
                if (MathUtils.approxEquals(alpha, 0.0)) {
                    Asolved = a1;
                    Bsolved = 0.0;
                } else {
                    Bsolved = a2 / alpha - a1;
                    Asolved = a1 - Bsolved;
                }
                A = Asolved;
                B = Bsolved;
            } else {
                repeatedRoot = false;
                alpha = r1;
                beta = r2;
                // a1 = A + B ; a2 = A*alpha + B*beta
                double Bsolved = (a2 - a1 * alpha) / (beta - alpha);
                double Asolved = a1 - Bsolved;
                A = Asolved;
                B = Bsolved;
            }
        }
    }

    @Override public SequenceType getType() { return SequenceType.LINEAR_RECURRENCE; }

    @Override public double nthTerm(int n) {
        // Always compute by iterating the exact integer/rational recurrence
        // forward, rather than through the irrational closed-form roots
        // (Binet's formula). Mathematically both are correct, but the
        // closed form accumulates floating-point noise from Math.pow on
        // irrational bases (e.g. golden ratio powers) even for sequences
        // that are exactly integer-valued, such as Fibonacci. The root-based
        // closed form is still reported by getGeneralTermFormula() for its
        // mathematical/educational value.
        if (n == 1) return a1;
        if (n == 2) return a2;
        double prev2 = a1, prev1 = a2, current = a2;
        for (int i = 3; i <= n; i++) {
            current = c1 * prev1 + c2 * prev2;
            prev2 = prev1;
            prev1 = current;
        }
        return current;
    }

    @Override public double sumOfN(int n) {
        double sum = 0.0;
        for (int i = 1; i <= n; i++) sum += nthTerm(i);
        return sum;
    }

    @Override public String getGeneralTermFormula() {
        if (complexRoots) {
            return String.format(
                "a_n = %.6g*a_(n-1) + %.6g*a_(n-2)  [complex roots — no real closed form; computed iteratively]",
                c1, c2);
        }
        if (repeatedRoot) {
            return String.format("a_n = (%.6g + %.6g*n) * (%.6g)^(n-1)   [repeated root]", A, B, alpha);
        }
        return String.format("a_n = %.6g*(%.6g)^(n-1) + %.6g*(%.6g)^(n-1)", A, alpha, B, beta);
    }

    @Override public String getSumFormula() {
        return "S_n = sum_{k=1..n} a_k  (via the closed-form/iterative a_n above)";
    }

    public double getC1() { return c1; }
    public double getC2() { return c2; }
}
