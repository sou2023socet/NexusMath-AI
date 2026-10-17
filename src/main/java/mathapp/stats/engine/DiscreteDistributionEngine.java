package mathapp.stats.engine;

import mathapp.stats.dto.DiscreteDistributionResult;

import java.util.LinkedHashMap;

import java.util.Map;

/**
 * High-precision Discrete Probability Engine supporting exact PMF/CDF evaluations,
 * moments, and analytical reports.
 */
public class DiscreteDistributionEngine {

    /**
     * Bernoulli Distribution: B(1, p)
     * $P(X = k) = p^k (1-p)^{1-k}$ for $k \in \{0, 1\}$
     */
    public static DiscreteDistributionResult evaluateBernoulli(double p, int k) {
        validateProbability(p);
        if (k != 0 && k != 1) {
            throw new IllegalArgumentException("Bernoulli random variable k must be 0 or 1.");
        }

        double pmf = (k == 1) ? p : (1.0 - p);
        double cdf = (k == 0) ? (1.0 - p) : 1.0;
        double mean = p;
        double variance = p * (1.0 - p);
        double q = 1.0 - p;
        double skewness = (q - p) / Math.sqrt(p * q);
        double kurtosis = (1.0 - 6.0 * p * q) / (p * q);

        Map<Integer, Double> pmfTable = new LinkedHashMap<>();
        pmfTable.put(0, 1.0 - p);
        pmfTable.put(1, p);

        Map<String, Number> params = Map.of("p", p);
        String latex = String.format("P(X = %d) = %.4f^{%d}(1 - %.4f)^{1 - %d} = %.6f", k, p, k, p, k, pmf);

        StringBuilder report = new StringBuilder();
        report.append("### Bernoulli Distribution Evaluation B(1, p)\n\n");
        report.append(String.format("* **Parameter:** Success Probability $p = %.4f$\n", p));
        report.append(String.format("* **Target Outcome ($k$):** `%d`\n", k));
        report.append(String.format("* **PMF $P(X = %d)$:** `%.6f` (%.2f%%)\n", k, pmf, pmf * 100.0));
        report.append(String.format("* **CDF $P(X \\le %d)$:** `%.6f` (%.2f%%)\n", k, cdf, cdf * 100.0));
        report.append(String.format("* **Mean $\\mathbb{E}[X]$:** `%.4f` | **Variance $\\text{Var}(X)$:** `%.4f`\n", mean, variance));

        return new DiscreteDistributionResult("Bernoulli", params, k, pmf, cdf, mean, variance, skewness, kurtosis, pmfTable, latex, report.toString());
    }

    /**
     * Binomial Distribution: B(n, p)
     * $P(X = k) = \binom{n}{k} p^k (1-p)^{n-k}$
     */
    public static DiscreteDistributionResult evaluateBinomial(int n, double p, int k) {
        validatePositive(n, "n (number of trials)");
        validateProbability(p);
        validateRange(k, 0, n, "k (number of successes)");

        double q = 1.0 - p;
        double pmf = combinationAsDouble(n, k)
                * Math.pow(p, k) * Math.pow(q, n - k);

        double cdf = 0.0;
        Map<Integer, Double> pmfTable = new LinkedHashMap<>();
        for (int i = 0; i <= n; i++) {
            double prob = combinationAsDouble(n, i)
                    * Math.pow(p, i) * Math.pow(q, n - i);
            pmfTable.put(i, prob);
            if (i <= k) {
                cdf += prob;
            }
        }

        double mean = n * p;
        double variance = n * p * q;
        double skewness = (q - p) / Math.sqrt(n * p * q);
        double kurtosis = (1.0 - 6.0 * p * q) / (n * p * q);

        Map<String, Number> params = Map.of("n", n, "p", p);
        String latex = String.format("P(X = %d) = \\binom{%d}{%d} (%.4f)^{%d} (%.4f)^{%d} = %.6f", 
                k, n, k, p, k, q, n - k, pmf);

        StringBuilder report = new StringBuilder();
        report.append("### Binomial Distribution Evaluation B(n, p)\n\n");
        report.append(String.format("* **Parameters:** $n = %d$ trials, $p = %.4f$ success probability\n", n, p));
        report.append(String.format("* **Target Successes ($k$):** `%d`\n", k));
        report.append(String.format("* **PMF $P(X = %d)$:** `%.6f`\n", k, pmf));
        report.append(String.format("* **CDF $P(X \\le %d)$:** `%.6f`\n", k, cdf));
        report.append(String.format("* **Expected Value $\\mathbb{E}[X]$:** `%.4f` | **Variance:** `%.4f`\n", mean, variance));

        return new DiscreteDistributionResult("Binomial", params, k, pmf, cdf, mean, variance, skewness, kurtosis, pmfTable, latex, report.toString());
    }

    /**
     * Poisson Distribution: Poisson(\lambda)
     * $P(X = k) = \frac{\lambda^k e^{-\lambda}}{k!}$
     */
    public static DiscreteDistributionResult evaluatePoisson(double lambda, int k) {
        if (lambda <= 0) {
            throw new IllegalArgumentException("Rate parameter lambda must be strictly positive (> 0).");
        }
        if (k < 0) {
            throw new IllegalArgumentException("Count k must be a non-negative integer.");
        }

        double logPmf = k * Math.log(lambda) - lambda - CombinatoricsCalculator.logFactorial(k);
        double pmf = Math.exp(logPmf);

        double cdf = 0.0;
        Map<Integer, Double> pmfTable = new LinkedHashMap<>();
        int upperLimit = Math.max(k + 10, (int) (lambda + 4 * Math.sqrt(lambda)));
        for (int i = 0; i <= upperLimit; i++) {
            double prob = Math.exp(i * Math.log(lambda) - lambda - CombinatoricsCalculator.logFactorial(i));
            pmfTable.put(i, prob);
            if (i <= k) {
                cdf += prob;
            }
        }

        double mean = lambda;
        double variance = lambda;
        double skewness = 1.0 / Math.sqrt(lambda);
        double kurtosis = 1.0 / lambda;

        Map<String, Number> params = Map.of("lambda", lambda);
        String latex = String.format("P(X = %d) = \\frac{%.4f^{%d} e^{-%.4f}}{%d!} = %.6f", k, lambda, k, lambda, k, pmf);

        StringBuilder report = new StringBuilder();
        report.append("### Poisson Distribution Evaluation Poisson(\\lambda)\n\n");
        report.append(String.format("* **Rate Parameter ($\\lambda$):** `%.4f` occurrences/interval\n", lambda));
        report.append(String.format("* **Target Occurrences ($k$):** `%d`\n", k));
        report.append(String.format("* **PMF $P(X = %d)$:** `%.6f` (%.2f%%)\n", k, pmf, pmf * 100.0));
        report.append(String.format("* **CDF $P(X \\le %d)$:** `%.6f` (%.2f%%)\n", k, cdf, cdf * 100.0));
        report.append(String.format("* **Mean $\\mathbb{E}[X]$:** `%.4f` | **Variance $\\text{Var}(X)$:** `%.4f`\n", mean, variance));

        return new DiscreteDistributionResult("Poisson", params, k, pmf, cdf, mean, variance, skewness, kurtosis, pmfTable, latex, report.toString());
    }

    /**
     * Geometric Distribution: Geo(p) (Number of trials up to first success)
     * $P(X = k) = (1-p)^{k-1} p$ for $k \ge 1$
     */
    public static DiscreteDistributionResult evaluateGeometric(double p, int k) {
        validateProbability(p);
        if (k < 1) {
            throw new IllegalArgumentException("Geometric trial count k must be >= 1.");
        }

        double q = 1.0 - p;
        double pmf = Math.pow(q, k - 1) * p;
        double cdf = 1.0 - Math.pow(q, k);

        double mean = 1.0 / p;
        double variance = q / (p * p);
        double skewness = (2.0 - p) / Math.sqrt(q);
        double kurtosis = 6.0 + (p * p) / q;

        Map<Integer, Double> pmfTable = new LinkedHashMap<>();
        int maxK = Math.max(k + 10, (int) (mean + 3 * Math.sqrt(variance)));
        for (int i = 1; i <= maxK; i++) {
            pmfTable.put(i, Math.pow(q, i - 1) * p);
        }

        Map<String, Number> params = Map.of("p", p);
        String latex = String.format("P(X = %d) = (1 - %.4f)^{%d - 1} (%.4f) = %.6f", k, p, k, p, pmf);

        StringBuilder report = new StringBuilder();
        report.append("### Geometric Distribution Evaluation Geo(p)\n\n");
        report.append(String.format("* **Success Probability ($p$):** `%.4f`\n", p));
        report.append(String.format("* **First Success Trial ($k$):** `%d`\n", k));
        report.append(String.format("* **PMF $P(X = %d)$:** `%.6f`\n", k, pmf));
        report.append(String.format("* **CDF $P(X \\le %d)$:** `%.6f`\n", k, cdf));
        report.append(String.format("* **Mean $\\mathbb{E}[X]$:** `%.4f` trials | **Variance:** `%.4f`\n", mean, variance));

        return new DiscreteDistributionResult("Geometric", params, k, pmf, cdf, mean, variance, skewness, kurtosis, pmfTable, latex, report.toString());
    }

    /**
     * Hypergeometric Distribution: Hypergeometric(N, K, n)
     * $P(X = k) = \frac{\binom{K}{k} \binom{N-K}{n-k}}{\binom{N}{n}}$
     */
    public static DiscreteDistributionResult evaluateHypergeometric(int popN, int successK, int sampleN, int k) {
        validatePositive(popN, "Population size N");
        validateRange(successK, 0, popN, "Success states K");
        validateRange(sampleN, 0, popN, "Sample size n");

        int minK = Math.max(0, sampleN - (popN - successK));
        int maxK = Math.min(sampleN, successK);
        validateRange(k, minK, maxK, "Observed successes k");

         double num = combinationAsDouble(successK, k)
             * combinationAsDouble(popN - successK, sampleN - k);
         double den = combinationAsDouble(popN, sampleN);
        double pmf = num / den;

        double cdf = 0.0;
        Map<Integer, Double> pmfTable = new LinkedHashMap<>();
        for (int i = minK; i <= maxK; i++) {
            double prob = (combinationAsDouble(successK, i)
                         * combinationAsDouble(popN - successK, sampleN - i)) / den;
            pmfTable.put(i, prob);
            if (i <= k) {
                cdf += prob;
            }
        }

        double mean = sampleN * ((double) successK / popN);
        double variance = sampleN * ((double) successK / popN) * (1.0 - (double) successK / popN) * ((double) (popN - sampleN) / (popN - 1));
        double skewness = ((double) (popN - 2 * successK) * Math.sqrt(popN - 1) * (popN - 2 * sampleN)) /
                ((popN - 2) * Math.sqrt(sampleN * successK * (popN - successK) * (popN - sampleN)));

        Map<String, Number> params = Map.of("N", popN, "K", successK, "n", sampleN);
        String latex = String.format("P(X = %d) = \\frac{\\binom{%d}{%d} \\binom{%d}{%d}}{\\binom{%d}{%d}} = %.6f",
                k, successK, k, popN - successK, sampleN - k, popN, sampleN, pmf);

        StringBuilder report = new StringBuilder();
        report.append("### Hypergeometric Distribution Evaluation Hypergeometric(N, K, n)\n\n");
        report.append(String.format("* **Population Size ($N$):** `%d` | **Total Successes ($K$):** `%d`\n", popN, successK));
        report.append(String.format("* **Sample Size ($n$):** `%d` | **Sample Successes ($k$):** `%d`\n", sampleN, k));
        report.append(String.format("* **PMF $P(X = %d)$:** `%.6f`\n", k, pmf));
        report.append(String.format("* **CDF $P(X \\le %d)$:** `%.6f`\n", k, cdf));
        report.append(String.format("* **Mean $\\mathbb{E}[X]$:** `%.4f` | **Variance:** `%.4f`\n", mean, variance));

        return new DiscreteDistributionResult("Hypergeometric", params, k, pmf, cdf, mean, variance, skewness, 0.0, pmfTable, latex, report.toString());
    }

    // Helper validation methods
    private static double combinationAsDouble(int n, int k) {
        if (k < 0 || k > n) {
            return 0.0;
        }
        int reducedK = Math.min(k, n - k);
        double result = 1.0;
        for (int i = 1; i <= reducedK; i++) {
            result *= (double) (n - reducedK + i) / i;
        }
        return result;
    }

    private static void validateProbability(double p) {
        if (p < 0.0 || p > 1.0) {
            throw new IllegalArgumentException("Probability p must lie in range [0, 1]. Provided: " + p);
        }
    }

    private static void validatePositive(int val, String paramName) {
        if (val <= 0) {
            throw new IllegalArgumentException(paramName + " must be strictly positive integer (> 0). Provided: " + val);
        }
    }

    private static void validateRange(int val, int min, int max, String paramName) {
        if (val < min || val > max) {
            throw new IllegalArgumentException(String.format("%s must lie in range [%d, %d]. Provided: %d", paramName, min, max, val));
        }
    }
}