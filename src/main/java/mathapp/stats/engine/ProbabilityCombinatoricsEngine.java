package mathapp.stats.engine;

import mathapp.stats.dto.CombinatoricsResult;

import java.math.BigInteger;

/**
 * Engine for Combinatorics, Counting Rules, Bayes' Theorem, and Fundamental Probability.
 */
public class ProbabilityCombinatoricsEngine {

    /**
     * Permutations of n items taken r at a time: P(n, r) = \frac{n!}{(n - r)!}
     */
    public static CombinatoricsResult calculatePermutations(int n, int r) {
        validateNAndR(n, r);

        BigInteger num = factorial(n);
        BigInteger den = factorial(n - r);
        BigInteger result = num.divide(den);

        String latex = String.format("{}_{%d}P_{%d} = \\frac{%d!}{(%d - %d)!} = %s", n, r, n, n, r, result);

        StringBuilder report = new StringBuilder();
        report.append("### Permutations Calculation ($nPr$)\n\n");
        report.append(String.format("* **Total Items ($n$):** `%d` | **Items Selected ($r$):** `%d`\n", n, r));
        report.append(String.format("* **Formula:** $P(n,r) = \\frac{n!}{(n-r)!}$\n"));
        report.append(String.format("* **Exact Result:** `%s`\n", result));

        return new CombinatoricsResult("Permutations (nPr)", result, null, latex, report.toString());
    }

    /**
     * Combinations of n items taken r at a time: C(n, r) = \frac{n!}{r!(n - r)!}
     */
    public static CombinatoricsResult calculateCombinations(int n, int r) {
        validateNAndR(n, r);

        BigInteger num = factorial(n);
        BigInteger den = factorial(r).multiply(factorial(n - r));
        BigInteger result = num.divide(den);

        String latex = String.format("\\binom{%d}{%d} = \\frac{%d!}{%d!(%d - %d)!} = %s", n, r, n, r, n, r, result);

        StringBuilder report = new StringBuilder();
        report.append("### Combinations Calculation ($nCr$)\n\n");
        report.append(String.format("* **Total Items ($n$):** `%d` | **Items Selected ($r$):** `%d`\n", n, r));
        report.append(String.format("* **Formula:** $\\binom{n}{r} = \\frac{n!}{r!(n-r)!}$\n"));
        report.append(String.format("* **Exact Result:** `%s`\n", result));

        return new CombinatoricsResult("Combinations (nCr)", result, null, latex, report.toString());
    }

    /**
     * Permutations with Repeated Objects: \frac{n!}{n_1! n_2! \dots n_k!}
     */
    public static CombinatoricsResult permutationsWithRepetition(int totalN, int[] groupCounts) {
        if (totalN < 0) throw new IllegalArgumentException("Total count n cannot be negative.");
        int sum = 0;
        BigInteger denominator = BigInteger.ONE;

        for (int count : groupCounts) {
            if (count < 0) throw new IllegalArgumentException("Group count cannot be negative.");
            sum += count;
            denominator = denominator.multiply(factorial(count));
        }

        if (sum != totalN) {
            throw new IllegalArgumentException(String.format("Sum of group counts (%d) must equal total N (%d).", sum, totalN));
        }

        BigInteger numerator = factorial(totalN);
        BigInteger result = numerator.divide(denominator);

        String latex = String.format("\\frac{%d!}{\\prod n_i!} = %s", totalN, result);

        StringBuilder report = new StringBuilder();
        report.append("### Permutations with Repeated Objects\n\n");
        report.append(String.format("* **Total Elements ($N$):** `%d`\n", totalN));
        report.append(String.format("* **Exact Distinct Permutations:** `%s`\n", result));

        return new CombinatoricsResult("Permutations with Repetition", result, null, latex, report.toString());
    }

    /**
     * Circular Permutations: (n - 1)!
     */
    public static CombinatoricsResult circularPermutations(int n) {
        if (n < 1) throw new IllegalArgumentException("Circular arrangements require n >= 1.");
        BigInteger result = factorial(n - 1);

        String latex = String.format("P_{\\text{circular}} = (%d - 1)! = %s", n, result);

        StringBuilder report = new StringBuilder();
        report.append("### Circular Permutations\n\n");
        report.append(String.format("* **Formula:** $(n - 1)! = (%d - 1)!$\n", n));
        report.append(String.format("* **Distinct Arrangements:** `%s`\n", result));

        return new CombinatoricsResult("Circular Permutations", result, null, latex, report.toString());
    }

    /**
     * Bayes' Theorem: P(A|B) = \frac{P(B|A) \cdot P(A)}{P(B|A) \cdot P(A) + P(B|\neg A) \cdot P(\neg A)}
     */
    public static CombinatoricsResult bayesTheorem(double pA, double pBGivenA, double pBGivenNotA) {
        validateProbability(pA, "P(A)");
        validateProbability(pBGivenA, "P(B|A)");
        validateProbability(pBGivenNotA, "P(B|not A)");

        double pNotA = 1.0 - pA;
        double pB = (pBGivenA * pA) + (pBGivenNotA * pNotA);

        if (pB == 0.0) {
            throw new ArithmeticException("Total probability of evidence P(B) is zero.");
        }

        double pAGivenB = (pBGivenA * pA) / pB;

        String latex = String.format("P(A|B) = \\frac{P(B|A) P(A)}{P(B)} = \\frac{%.4f \\cdot %.4f}{%.4f} = %.6f",
                pBGivenA, pA, pB, pAGivenB);

        StringBuilder report = new StringBuilder();
        report.append("### Bayes' Theorem Analysis\n\n");
        report.append(String.format("* **Prior Probability $P(A)$:** `%.4f` | **$P(\\neg A)$:** `%.4f`\n", pA, pNotA));
        report.append(String.format("* **Likelihood $P(B\\vert{}A)$:** `%.4f` | **$P(B\\vert{}\\neg A)$:** `%.4f`\n", pBGivenA, pBGivenNotA));
        report.append(String.format("* **Total Evidence Probability $P(B)$:** `%.6f`\n", pB));
        report.append(String.format("* **Posterior Probability $P(A\\vert{}B)$:** `%.6f`\n", pAGivenB));

        return new CombinatoricsResult("Bayes' Theorem", null, pAGivenB, latex, report.toString());
    }

    /**
     * Hypergeometric Probability: P(X = k) = \frac{\binom{K}{k} \binom{N - K}{n - k}}{\binom{N}{n}}
     */
    public static CombinatoricsResult hypergeometricProbability(int N, int K, int n, int k) {
        if (N < 1 || K < 0 || K > N || n < 1 || n > N || k < 0 || k > K || k > n) {
            throw new IllegalArgumentException("Invalid Hypergeometric parameters: N=" + N + ", K=" + K + ", n=" + n + ", k=" + k);
        }

        BigInteger waysSuccess = nCrBig(K, k);
        BigInteger waysFailure = nCrBig(N - K, n - k);
        BigInteger totalWays = nCrBig(N, n);

        BigInteger favorableWays = waysSuccess.multiply(waysFailure);
        double prob = favorableWays.doubleValue() / totalWays.doubleValue();

        String latex = String.format("P(X = %d) = \\frac{\\binom{%d}{%d} \\binom{%d}{%d}}{\\binom{%d}{%d}} = %.6f",
                k, K, k, N - K, n - k, N, n, prob);

        StringBuilder report = new StringBuilder();
        report.append("### Hypergeometric Distribution Probability\n\n");
        report.append(String.format("* **Population Size ($N$):** `%d` | **Total Successes ($K$):** `%d`\n", N, K));
        report.append(String.format("* **Sample Size ($n$):** `%d` | **Sample Successes ($k$):** `%d`\n", n, k));
        report.append(String.format("* **Favorable Outcomes:** `%s` / **Total Outcomes:** `%s`\n", favorableWays, totalWays));
        report.append(String.format("* **Probability $P(X = k)$:** `%.6f`\n", prob));

        return new CombinatoricsResult("Hypergeometric Probability", favorableWays, prob, latex, report.toString());
    }

    // Mathematical Helpers
    public static BigInteger factorial(int n) {
        if (n < 0) throw new IllegalArgumentException("Factorial is undefined for negative integers: " + n);
        BigInteger fact = BigInteger.ONE;
        for (int i = 2; i <= n; i++) {
            fact = fact.multiply(BigInteger.valueOf(i));
        }
        return fact;
    }

    private static BigInteger nCrBig(int n, int r) {
        if (r < 0 || r > n) return BigInteger.ZERO;
        return factorial(n).divide(factorial(r).multiply(factorial(n - r)));
    }

    private static void validateNAndR(int n, int r) {
        if (n < 0 || r < 0) throw new IllegalArgumentException("Parameters n and r must be non-negative.");
        if (r > n) throw new IllegalArgumentException("r (" + r + ") cannot be greater than n (" + n + ").");
    }

    private static void validateProbability(double p, String name) {
        if (p < 0.0 || p > 1.0) {
            throw new IllegalArgumentException(name + " must lie in [0, 1]. Provided: " + p);
        }
    }
}