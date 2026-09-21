package mathapp.stats.engine;

import mathapp.stats.dto.CombinatoricsResult;

import java.math.BigInteger;
import java.util.Map;

/**
 * High-precision combinatorial engine supporting exact BigInteger math and log-gamma approximations.
 */
public class CombinatoricsCalculator {

    private static final int MAX_EXACT_FACTORIAL = 10000;

    /**
     * Calculates exact n! using BigInteger.
     */
    public static BigInteger factorialExact(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Factorial is undefined for negative integers: n = " + n);
        }
        if (n > MAX_EXACT_FACTORIAL) {
            throw new ArithmeticException("n exceeds maximum allowable limit for exact BigInteger evaluation (10,000).");
        }

        BigInteger result = BigInteger.ONE;
        for (int i = 2; i <= n; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return result;
    }

    /**
     * Calculates natural log of factorial \ln(n!) via Lanczos Approximation for Stirling's series.
     * Prevents numerical overflow for large n.
     */
    public static double logFactorial(int n) {
        if (n < 0) return Double.NaN;
        if (n <= 1) return 0.0;

        // Lanczos Gamma Approximation: \ln \Gamma(n + 1) = \ln(n!)
        double x = n + 1.0;
        double[] c = {
                0.99999999999980993,
                676.5203681218851,
                -1259.139216722289,
                771.32342877765313,
                -176.61502916214059,
                12.507343278686905,
                -0.13857109526572012,
                9.9843695780195716e-6,
                1.5056327351493116e-7
        };

        double sum = c[0];
        for (int i = 1; i < c.length; i++) {
            sum += c[i] / (x + i - 1);
        }

        double base = x + 4.5;
        return (x - 0.5) * Math.log(base) - base + Math.log(Math.sqrt(2 * Math.PI) * sum);
    }

    /**
     * Calculates Permutations P(n, r) = n! / (n - r)!
     */
    public static CombinatoricsResult calculatePermutations(int n, int r) {
        validateInputs(n, r);

        BigInteger num = factorialExact(n);
        BigInteger den = factorialExact(n - r);
        BigInteger result = num.divide(den);

        double logVal = logFactorial(n) - logFactorial(n - r);

        String latex = String.format("P(%d, %d) = \\frac{%d!}{(%d - %d)!} = %s", n, r, n, n, r, result.toString());

        StringBuilder report = new StringBuilder();
        report.append("### Permutations Calculation (Ordered Selection)\n\n");
        report.append(String.format("* **Formula:** $P(n, r) = \\frac{n!}{(n - r)!}$\n"));
        report.append(String.format("* **Parameters:** $n = %d$, $r = %d$\n", n, r));
        report.append(String.format("* **Steps:** $\\frac{%d!}{(%d)!} = %s$\n", n, n - r, result.toString()));
        report.append(String.format("* **Total Ordered Arrangements:** `%s`\n", result.toString()));

        return new CombinatoricsResult("Permutations P(n, r)", result, null, latex, report.toString());
    }

    /**
     * Calculates Combinations C(n, r) = n! / (r! * (n - r)!)
     */
    public static CombinatoricsResult calculateCombinations(int n, int r) {
        validateInputs(n, r);

        BigInteger num = factorialExact(n);
        BigInteger den = factorialExact(r).multiply(factorialExact(n - r));
        BigInteger result = num.divide(den);

        double logVal = logFactorial(n) - (logFactorial(r) + logFactorial(n - r));

        String latex = String.format("\\binom{%d}{%d} = \\frac{%d!}{%d!(%d - %d)!} = %s", n, r, n, r, n, r, result.toString());

        StringBuilder report = new StringBuilder();
        report.append("### Combinations Calculation (Unordered Selection)\n\n");
        report.append(String.format("* **Formula:** $C(n, r) = \\binom{n}{r} = \\frac{n!}{r!(n - r)!}$\n"));
        report.append(String.format("* **Parameters:** $n = %d$, $r = %d$\n", n, r));
        report.append(String.format("* **Steps:** $\\frac{%d!}{(%d!)(%d!)} = %s$\n", n, r, n - r, result.toString()));
        report.append(String.format("* **Total Unique Subsets:** `%s`\n", result.toString()));

        return new CombinatoricsResult("Combinations C(n, r)", result, null, latex, report.toString());
    }

    /**
     * Calculates Derangements !n = n! \sum_{i=0}^n \frac{(-1)^i}{i!}
     */
    public static CombinatoricsResult calculateDerangements(int n) {
        if (n < 0) throw new IllegalArgumentException("n must be non-negative");

        BigInteger d;
        if (n == 0) d = BigInteger.ONE;
        else if (n == 1) d = BigInteger.ZERO;
        else {
            BigInteger dSub2 = BigInteger.ONE;  // !0
            BigInteger dSub1 = BigInteger.ZERO; // !1
            BigInteger current = BigInteger.ZERO;

            for (int i = 2; i <= n; i++) {
                current = BigInteger.valueOf(i - 1).multiply(dSub1.add(dSub2));
                dSub2 = dSub1;
                dSub1 = current;
            }
            d = current;
        }

        String latex = String.format("!%d = %s", n, d.toString());

        StringBuilder report = new StringBuilder();
        report.append("### Derangements (!n) - Permutations with No Fixed Points\n\n");
        report.append(String.format("* **Formula:** $!n = (n-1)(!(n-1) + !(n-2))$\n"));
        report.append(String.format("* **Number of Items ($n$):** `%d`\n", n));
        report.append(String.format("* **Total Derangements:** `%s`\n", d.toString()));

        return new CombinatoricsResult("Derangements !n", d, null, latex, report.toString());
    }

    /**
     * Calculates Stars and Bars distribution: \binom{n + k - 1}{k - 1}
     */
    public static CombinatoricsResult calculateStarsAndBars(int nItems, int kBins) {
        if (nItems < 0 || kBins <= 0) {
            throw new IllegalArgumentException("Items must be >= 0 and bins must be > 0");
        }

        int totalN = nItems + kBins - 1;
        int totalR = kBins - 1;

        return calculateCombinations(totalN, totalR);
    }

    /**
     * Calculates Identical Items Multiset Permutations: \frac{n!}{n_1! n_2! ... n_k!}
     */
    public static CombinatoricsResult calculateMultisetPermutations(Map<String, Integer> itemCounts) {
        int totalN = itemCounts.values().stream().mapToInt(Integer::intValue).sum();
        BigInteger num = factorialExact(totalN);

        BigInteger den = BigInteger.ONE;
        StringBuilder denBreakdown = new StringBuilder();

        for (Map.Entry<String, Integer> entry : itemCounts.entrySet()) {
            int count = entry.getValue();
            den = den.multiply(factorialExact(count));
            denBreakdown.append(count).append("! \\cdot ");
        }

        if (denBreakdown.length() > 8) {
            denBreakdown.setLength(denBreakdown.length() - 8);
        }

        BigInteger result = num.divide(den);

        StringBuilder report = new StringBuilder();
        report.append("### Multiset Permutations (Distinguishable Grouping)\n\n");
        report.append(String.format("* **Total Items ($N$):** `%d`\n", totalN));
        report.append(String.format("* **Formula:** $\\frac{N!}{n_1! n_2! \\dots n_k!} = \\frac{%d!}{%s} = %s$\n",
                totalN, denBreakdown.toString(), result.toString()));

        return new CombinatoricsResult("Multiset Permutations", result, null, "", report.toString());
    }

    private static void validateInputs(int n, int r) {
        if (n < 0 || r < 0) {
            throw new IllegalArgumentException("n and r must be non-negative integers.");
        }
        if (r > n) {
            throw new IllegalArgumentException(String.format("r (%d) cannot exceed n (%d).", r, n));
        }
    }
}