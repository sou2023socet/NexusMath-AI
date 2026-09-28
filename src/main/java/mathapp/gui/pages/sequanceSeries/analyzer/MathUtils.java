package mathapp.gui.pages.sequanceSeries.analyzer;

/**
 * Numeric helpers shared by every detector and model.
 * All floating point comparisons in the engine MUST go through approxEquals /
 * isConstant instead of == , because sequence terms are doubles and direct
 * equality checks would make every detector brittle.
 */
public final class MathUtils {

    /** Relative tolerance used everywhere for "is this basically the same number". */
    public static final double EPS = 1e-6;

    private MathUtils() { }

    public static boolean approxEquals(double a, double b) {
        double scale = Math.max(1.0, Math.max(Math.abs(a), Math.abs(b)));
        return Math.abs(a - b) <= EPS * scale;
    }

    /** True if every element of the array is approximately equal to the first. */
    public static boolean isConstant(double[] arr) {
        if (arr.length == 0) return false;
        double first = arr[0];
        for (double v : arr) {
            if (!approxEquals(v, first)) return false;
        }
        return true;
    }

    /** First-order difference series: d[i] = terms[i+1] - terms[i]. */
    public static double[] firstDifference(double[] terms) {
        double[] d = new double[terms.length - 1];
        for (int i = 0; i < d.length; i++) {
            d[i] = terms[i + 1] - terms[i];
        }
        return d;
    }

    public static boolean containsZero(double[] arr) {
        for (double v : arr) if (approxEquals(v, 0.0)) return true;
        return false;
    }

    /**
     * Binomial coefficient C(m, i) computed for non-negative integer m and i,
     * using the product form so it works cleanly even when m < i (returns 0)
     * without needing factorials (which overflow fast).
     */
    public static double binomial(int m, int i) {
        if (i < 0) return 0.0;
        if (i == 0) return 1.0;
        double result = 1.0;
        for (int j = 0; j < i; j++) {
            result *= (m - j);
            result /= (j + 1);
        }
        return result;
    }
}
