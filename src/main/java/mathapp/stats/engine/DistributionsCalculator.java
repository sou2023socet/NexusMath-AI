package mathapp.stats.engine;

import java.util.ArrayList;
import java.util.List;

/**
 * Pure mathematical engine for discrete and continuous probability distributions.
 */
public class DistributionsCalculator {

    // =========================================================================
    // 1. DISCRETE: BINOMIAL DISTRIBUTION B(n, p)
    // =========================================================================

    /**
     * Calculates Binomial PMF: P(X = k) = \binom{n}{k} p^k (1-p)^{n-k}
     */
    public static double calculateBinomialPMF(int n, int k, double p) {
        if (n < 0 || k < 0 || k > n) return 0.0;
        if (p < 0.0 || p > 1.0) {
            throw new IllegalArgumentException("Probability p must be in range [0, 1].");
        }
        return logCombination(n, k) + (k * Math.log(p)) + ((n - k) * Math.log(1.0 - p));
    }

    /**
     * Log-space combinations \ln \binom{n}{k} for high numerical stability.
     */
    private static double logCombination(int n, int k) {
        if (k < 0 || k > n) return Double.NEGATIVE_INFINITY;
        if (k == 0 || k == n) return 0.0;
        return logFactorial(n) - logFactorial(k) - logFactorial(n - k);
    }

    private static double logFactorial(int n) {
        double logFact = 0.0;
        for (int i = 2; i <= n; i++) {
            logFact += Math.log(i);
        }
        return logFact;
    }

    /**
     * Calculates Binomial Cumulative Distribution Function (CDF): P(X \le k)
     */
    public static double calculateBinomialCDF(int n, int k, double p) {
        if (k < 0) return 0.0;
        if (k >= n) return 1.0;
        double sum = 0.0;
        for (int i = 0; i <= k; i++) {
            sum += Math.exp(calculateBinomialPMF(n, i, p));
        }
        return Math.min(1.0, sum);
    }

    // =========================================================================
    // 2. DISCRETE: POISSON DISTRIBUTION P(\lambda)
    // =========================================================================

    /**
     * Calculates Poisson PMF: P(X = k) = \frac{\lambda^k e^{-\lambda}}{k!}
     */
    public static double calculatePoissonPMF(int k, double lambda) {
        if (k < 0 || lambda <= 0.0) return 0.0;
        double logP = (k * Math.log(lambda)) - lambda - logFactorial(k);
        return Math.exp(logP);
    }

    /**
     * Calculates Poisson CDF: P(X \le k) = \sum_{i=0}^k \frac{\lambda^i e^{-\lambda}}{i!}
     */
    public static double calculatePoissonCDF(int k, double lambda) {
        if (k < 0) return 0.0;
        double sum = 0.0;
        for (int i = 0; i <= k; i++) {
            sum += calculatePoissonPMF(i, lambda);
        }
        return Math.min(1.0, sum);
    }

    // =========================================================================
    // 3. CONTINUOUS: NORMAL DISTRIBUTION N(\mu, \sigma^2)
    // =========================================================================

    /**
     * Standard Normal PDF: \phi(z) = \frac{1}{\sqrt{2\pi}} e^{-\frac{z^2}{2}}
     */
    public static double calculateStandardNormalPDF(double z) {
        return (1.0 / Math.sqrt(2.0 * Math.PI)) * Math.exp(-0.5 * z * z);
    }

    /**
     * Standard Normal CDF using Abramowitz & Stegun error function approximation:
     * \Phi(z) = P(Z \le z)
     */
    public static double calculateStandardNormalCDF(double z) {
        return 0.5 * (1.0 + errorFunction(z / Math.sqrt(2.0)));
    }

    /**
     * Error Function (erf) approximation with error < 1.5 * 10^-7.
     */
    public static double errorFunction(double x) {
        double sign = (x < 0) ? -1.0 : 1.0;
        x = Math.abs(x);

        double a1 = 0.254829592;
        double a2 = -0.284496736;
        double a3 = 1.421413741;
        double a4 = -1.453152027;
        double a5 = 1.061405429;
        double p = 0.3275911;

        double t = 1.0 / (1.0 + p * x);
        double y = 1.0 - (((((a5 * t + a4) * t) + a3) * t + a2) * t + a1) * t * Math.exp(-x * x);

        return sign * y;
    }

    /**
     * Inverse Standard Normal CDF (Quantile Function z = \Phi^{-1}(p))
     * Rational approximation algorithm (Wichura / Beasley-Springer-Moro).
     */
    public static double calculateInverseStandardNormalCDF(double p) {
        if (p <= 0.0 || p >= 1.0) {
            throw new IllegalArgumentException("Probability p must be strictly in range (0, 1).");
        }

        if (p < 0.5) {
            return -rationalApproximation(Math.sqrt(-2.0 * Math.log(p)));
        } else {
            return rationalApproximation(Math.sqrt(-2.0 * Math.log(1.0 - p)));
        }
    }

    private static double rationalApproximation(double t) {
        double c0 = 2.515517;
        double c1 = 0.802853;
        double c2 = 0.010328;
        double d1 = 1.432788;
        double d2 = 0.189269;
        double d3 = 0.001308;

        return t - ((c2 * t + c1) * t + c0) / (((d3 * t + d2) * t + d1) * t + 1.0);
    }

    // =========================================================================
    // 4. CONTINUOUS: EXPONENTIAL DISTRIBUTION Exp(\lambda)
    // =========================================================================

    /**
     * Exponential PDF: f(x) = \lambda e^{-\lambda x} for x \ge 0
     */
    public static double calculateExponentialPDF(double x, double lambda) {
        if (x < 0.0 || lambda <= 0.0) return 0.0;
        return lambda * Math.exp(-lambda * x);
    }

    /**
     * Exponential CDF: F(x) = 1 - e^{-\lambda x} for x \ge 0
     */
    public static double calculateExponentialCDF(double x, double lambda) {
        if (x < 0.0) return 0.0;
        if (lambda <= 0.0) throw new IllegalArgumentException("Rate parameter lambda must be positive.");
        return 1.0 - Math.exp(-lambda * x);
    }

    /**
     * Exponential Quantile (Inverse CDF): x = -\frac{\ln(1 - p)}{\lambda}
     */
    public static double calculateExponentialQuantile(double p, double lambda) {
        if (p < 0.0 || p >= 1.0) {
            throw new IllegalArgumentException("Probability p must be in range [0, 1).");
        }
        return -Math.log(1.0 - p) / lambda;
    }
}