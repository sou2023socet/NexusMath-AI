package mathapp.gui.pages.patterns;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;

/**
 * Engine for detecting algebraic patterns, computing finite differences, 
 * deriving closed-form nth-term formulas, and generating sequence expansions.
 */
public class SequencePatternEngine {

    private static final double EPSILON = 1e-7;

    /**
     * Primary entry point: Analyzes an arbitrary numerical sequence.
     */
    public static SequencePatternResult analyzeSequence(double[] terms, int termsToPredict) {
        if (terms == null || terms.length < 3) {
            throw new IllegalArgumentException("Sequence analysis requires at least 3 initial terms.");
        }

        // 1. Check Arithmetic Progression
        Double commonDiff = checkArithmetic(terms);
        if (commonDiff != null) {
            return buildArithmeticResult(terms, commonDiff, termsToPredict);
        }

        // 2. Check Geometric Progression
        Double commonRatio = checkGeometric(terms);
        if (commonRatio != null) {
            return buildGeometricResult(terms, commonRatio, termsToPredict);
        }

        // 3. Method of Finite Differences (Polynomial Detection)
        List<List<Double>> diffTable = computeDifferenceTable(terms);
        int polyDegree = detectPolynomialDegree(diffTable);
        if (polyDegree > 0) {
            return buildPolynomialResult(terms, diffTable, polyDegree, termsToPredict);
        }

        // 4. Check Second-Order Recurrence (e.g., Fibonacci-like: a_n = a_{n-1} + a_{n-2})
        if (checkFibonacciLike(terms)) {
            return buildFibonacciResult(terms, termsToPredict);
        }

        // 5. Fallback for unknown / complex pattern
        return buildUnknownResult(terms, diffTable);
    }

    // =========================================================================
    // PATTERN DETECTORS
    // =========================================================================

    private static Double checkArithmetic(double[] terms) {
        double d = terms[1] - terms[0];
        for (int i = 1; i < terms.length - 1; i++) {
            if (Math.abs((terms[i + 1] - terms[i]) - d) > EPSILON) {
                return null;
            }
        }
        return d;
    }

    private static Double checkGeometric(double[] terms) {
        if (Math.abs(terms[0]) < EPSILON) return null;
        double r = terms[1] / terms[0];
        for (int i = 1; i < terms.length - 1; i++) {
            if (Math.abs(terms[i]) < EPSILON) return null;
            if (Math.abs((terms[i + 1] / terms[i]) - r) > EPSILON) {
                return null;
            }
        }
        return r;
    }

    private static List<List<Double>> computeDifferenceTable(double[] terms) {
        List<List<Double>> table = new ArrayList<>();
        List<Double> current = new ArrayList<>();
        for (double val : terms) current.add(val);
        table.add(current);

        while (current.size() > 1) {
            List<Double> nextDiff = new ArrayList<>();
            boolean allZero = true;
            for (int i = 0; i < current.size() - 1; i++) {
                double diff = current.get(i + 1) - current.get(i);
                nextDiff.add(diff);
                if (Math.abs(diff) > EPSILON) {
                    allZero = false;
                }
            }
            table.add(nextDiff);
            if (allZero || nextDiff.size() <= 1) break;
            current = nextDiff;
        }

        return table;
    }

    private static int detectPolynomialDegree(List<List<Double>> diffTable) {
        for (int order = 1; order < diffTable.size(); order++) {
            List<Double> row = diffTable.get(order);
            if (row.isEmpty()) continue;
            boolean constant = true;
            double firstVal = row.get(0);
            for (double val : row) {
                if (Math.abs(val - firstVal) > EPSILON) {
                    constant = false;
                    break;
                }
            }
            if (constant) return order;
        }
        return -1;
    }

    private static boolean checkFibonacciLike(double[] terms) {
        for (int i = 2; i < terms.length; i++) {
            if (Math.abs(terms[i] - (terms[i - 1] + terms[i - 2])) > EPSILON) {
                return false;
            }
        }
        return true;
    }

    // =========================================================================
    // BUILDERS
    // =========================================================================

    private static SequencePatternResult buildArithmeticResult(double[] terms, double d, int count) {
        double a1 = terms[0];
        List<Double> predicted = new ArrayList<>();
        double last = terms[terms.length - 1];
        for (int i = 1; i <= count; i++) {
            predicted.add(last + i * d);
        }

        String explicit = String.format("a_n = %.4f + (n - 1) \\cdot %.4f", a1, d);
        String recursive = String.format("a_n = a_{n-1} + %.4f, \\quad a_1 = %.4f", d, a1);
        String sumForm = String.format("S_n = \\frac{n}{2} \\left(2(%.4f) + (n - 1)%.4f\\right)", a1, d);

        StringBuilder report = new StringBuilder();
        report.append("### Arithmetic Progression (AP) Detected\n\n");
        report.append(String.format("* **First Term ($a_1$):** `%.4f` | **Common Difference ($d$):** `%.4f`\n", a1, d));
        report.append(String.format("* **Explicit Formula:** $%s$\n", explicit));
        report.append(String.format("* **Recursive Relation:** $%s$\n", recursive));
        report.append(String.format("* **Partial Sum Formula:** $%s$\n", sumForm));
        report.append(String.format("* **Next %d Predicted Terms:** `%s`\n", count, predicted.toString()));

        return new SequencePatternResult("Arithmetic Progression", explicit, recursive, sumForm,
                predicted, List.of(), false, null, explicit, report.toString());
    }

    private static SequencePatternResult buildGeometricResult(double[] terms, double r, int count) {
        double a1 = terms[0];
        List<Double> predicted = new ArrayList<>();
        double last = terms[terms.length - 1];
        for (int i = 1; i <= count; i++) {
            predicted.add(last * Math.pow(r, i));
        }

        boolean convergent = Math.abs(r) < 1.0;
        Double infiniteSum = convergent ? a1 / (1.0 - r) : null;

        String explicit = String.format("a_n = %.4f \\cdot (%.4f)^{n-1}", a1, r);
        String recursive = String.format("a_n = %.4f \\cdot a_{n-1}, \\quad a_1 = %.4f", r, a1);
        String sumForm = String.format("S_n = %.4f \\cdot \\frac{1 - (%.4f)^n}{1 - %.4f}", a1, r, r);

        StringBuilder report = new StringBuilder();
        report.append("### Geometric Progression (GP) Detected\n\n");
        report.append(String.format("* **First Term ($a_1$):** `%.4f` | **Common Ratio ($r$):** `%.4f`\n", a1, r));
        report.append(String.format("* **Explicit Formula:** $%s$\n", explicit));
        report.append(String.format("* **Recursive Relation:** $\n%s$\n", recursive));
        report.append(String.format("* **Series Convergence:** `%s`\n", convergent ? "Convergent (|r| < 1)" : "Divergent (|r| >= 1)"));
        if (convergent) {
            report.append(String.format("* **Infinite Series Sum ($S_\\infty$):** `%.6f`\n", infiniteSum));
        }
        report.append(String.format("* **Next %d Predicted Terms:** `%s`\n", count, predicted.toString()));

        return new SequencePatternResult("Geometric Progression", explicit, recursive, sumForm,
                predicted, List.of(), convergent, infiniteSum, explicit, report.toString());
    }

    private static SequencePatternResult buildPolynomialResult(double[] terms, List<List<Double>> diffTable, int degree, int count) {
        // Derive quadratic formula coefficients if degree == 2: a_n = A*n^2 + B*n + C
        double firstDiff = diffTable.get(1).get(0);
        double secondDiff = diffTable.get(2).get(0);

        double A = secondDiff / 2.0;
        double B = firstDiff - 3.0 * A;
        double C = terms[0] - A - B;

        String explicit = String.format("a_n = %.4fn^2 + %.4fn + %.4f", A, B, C);
        List<Double> predicted = new ArrayList<>();
        int currentN = terms.length + 1;
        for (int i = 0; i < count; i++) {
            int n = currentN + i;
            predicted.add(A * n * n + B * n + C);
        }

        StringBuilder report = new StringBuilder();
        report.append(String.format("### Polynomial Pattern Detected (Degree %d)\n\n", degree));
        report.append("* **Method:** Finite Difference Analysis\n");
        report.append(String.format("* **Constant Difference (Order %d):** `%.4f`\n", degree, secondDiff));
        report.append(String.format("* **Derived Explicit Formula ($a_n$):** $%s$\n", explicit));
        report.append(String.format("* **Next %d Predicted Terms:** `%s`\n", count, predicted.toString()));

        return new SequencePatternResult("Polynomial (Degree " + degree + ")", explicit, "N/A", "N/A",
                predicted, diffTable, false, null, explicit, report.toString());
    }

    private static SequencePatternResult buildFibonacciResult(double[] terms, int count) {
        List<Double> predicted = new ArrayList<>();
        double prev2 = terms[terms.length - 2];
        double prev1 = terms[terms.length - 1];

        for (int i = 0; i < count; i++) {
            double nextVal = prev1 + prev2;
            predicted.add(nextVal);
            prev2 = prev1;
            prev1 = nextVal;
        }

        String explicit = "a_n = \\frac{\\phi^n - (1-\\phi)^n}{\\sqrt{5}} \\quad \\text{(Binet's Formula Proxy)}";
        String recursive = "a_n = a_{n-1} + a_{n-2}";

        StringBuilder report = new StringBuilder();
        report.append("### Fibonacci-Type Recurrence Detected\n\n");
        report.append("* **Recurrence Rule:** $a_n = a_{n-1} + a_{n-2}$\n");
        report.append(String.format("* **Initial Seed Terms:** $a_1 = %.4f, a_2 = %.4f$\n", terms[0], terms[1]));
        report.append(String.format("* **Golden Ratio Approximation ($\\frac{a_n}{a_{n-1}}$):** `%.6f`\n", terms[terms.length - 1] / terms[terms.length - 2]));
        report.append(String.format("* **Next %d Predicted Terms:** `%s`\n", count, predicted.toString()));

        return new SequencePatternResult("Fibonacci Recurrence", explicit, recursive, "N/A",
                predicted, List.of(), false, null, recursive, report.toString());
    }

    private static SequencePatternResult buildUnknownResult(double[] terms, List<List<Double>> diffTable) {
        StringBuilder report = new StringBuilder();
        report.append("### Pattern Analysis (Inconclusive / Higher Order)\n\n");
        report.append("* **Status:** Input sequence does not match standard 1st/2nd degree polynomial, GP, or Fibonacci relations.\n");
        report.append(String.format("* **Analyzed Input Length:** `%d` terms\n", terms.length));
        report.append("* **Finite Difference Table Depth:** `").append(diffTable.size()).append("` levels\n");

        return new SequencePatternResult("Unknown / Non-Standard", "Undefined", "Undefined", "Undefined",
                List.of(), diffTable, false, null, "", report.toString());
    }
}