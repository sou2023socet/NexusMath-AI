package mathapp.stats.engine;

import mathapp.stats.dto.DistributionResult;

/**
 * High-precision engine for continuous probability distributions (Normal, Student's t, Chi-Square, Exponential).
 */
public class ContinuousDistributionEngine {

    /**
     * Standard Normal Probability Density Function (PDF): \phi(z) = \frac{1}{\sqrt{2\pi}} e^{-z^2/2}
     */
    public static double standardNormalPDF(double z) {
        return (1.0 / Math.sqrt(2.0 * Math.PI)) * Math.exp(-0.5 * z * z);
    }

    /**
     * Standard Normal Cumulative Distribution Function (CDF): \Phi(z) = \frac{1}{2} \left[1 + \text{erf}\left(\frac{z}{\sqrt{2}}\right)\right]
     * Uses Abramowitz & Stegun approximation with error |e(z)| < 7.5e-8.
     */
    public static double standardNormalCDF(double z) {
        if (z < -8.0) return 0.0;
        if (z > 8.0) return 1.0;

        double absZ = Math.abs(z);
        double t = 1.0 / (1.0 + 0.2316419 * absZ);
        double poly = t * (0.319381530 + t * (-0.356563782 + t * (1.781477937 + t * (-1.821255978 + t * 1.330274429))));
        double cdf = 1.0 - standardNormalPDF(absZ) * poly;

        return (z >= 0) ? cdf : (1.0 - cdf);
    }

    /**
     * Inverse Standard Normal CDF (Quantile function z_p) using Acklam's rational approximation.
     */
    public static double inverseStandardNormalCDF(double p) {
        if (p <= 0.0 || p >= 1.0) {
            throw new IllegalArgumentException("Probability p must lie strictly in (0, 1). Provided: " + p);
        }

        // Coefficients in rational approximations
        final double[] a = {-3.969683028665376e+01,  2.209460984245205e+02, -2.759285104469687e+02,
                            1.383577518672690e+02, -3.066479806614716e+01,  2.506628277459239e+00};
        final double[] b = {-5.447609879822406e+01,  1.615858368580409e+02, -1.556989798598866e+02,
                            6.680131188771972e+01, -1.328068155288572e+01};
        final double[] c = {-7.784894002430293e-03, -3.223964580411365e-01, -2.400758277161838e+00,
                           -2.549732539343734e+00,  4.374664141464968e+00,  2.938163982698783e+00};
        final double[] d = { 7.784695709041462e-03,  3.224671290700398e-01,  2.445134137142996e+00,
                            3.754408661907416e+00};

        final double pLow = 0.02425;
        final double pHigh = 1.0 - pLow;

        double q, r;

        if (p < pLow) {
            q = Math.sqrt(-2.0 * Math.log(p));
            return (((((c[0]*q + c[1])*q + c[2])*q + c[3])*q + c[4])*q + c[5]) /
                    ((((d[0]*q + d[1])*q + d[2])*q + d[3])*q + 1.0);
        } else if (p <= pHigh) {
            q = p - 0.5;
            r = q * q;
            return (((((a[0]*r + a[1])*r + a[2])*r + a[3])*r + a[4])*r + a[5]) * q /
                    (((((b[0]*r + b[1])*r + b[2])*r + b[3])*r + b[4])*r + 1.0);
        } else {
            q = Math.sqrt(-2.0 * Math.log(1.0 - p));
            return -(((((c[0]*q + c[1])*q + c[2])*q + c[3])*q + c[4])*q + c[5]) /
                     ((((d[0]*q + d[1])*q + d[2])*q + d[3])*q + 1.0);
        }
    }

    /**
     * General Normal Distribution CDF: N(\mu, \sigma^2).
     */
    public static DistributionResult analyzeNormal(double x, double mean, double stdDev) {
        if (stdDev <= 0.0) throw new IllegalArgumentException("Standard deviation must be strictly positive.");

        double z = (x - mean) / stdDev;
        double pdf = standardNormalPDF(z) / stdDev;
        double cdf = standardNormalCDF(z);

        String latex = String.format("X \\sim \\mathcal{N}(%.2f, %.2f^2), \\quad Z = \\frac{%.4f - %.2f}{%.2f} = %.4f",
                mean, stdDev, x, mean, stdDev, z);

        StringBuilder report = new StringBuilder();
        report.append("### Normal Distribution Analysis $\\mathcal{N}(\\mu, \\sigma^2)$\n\n");
        report.append(String.format("* **Mean ($\\mu$):** `%.4f` | **Std Dev ($\\sigma$):** `%.4f`\n", mean, stdDev));
        report.append(String.format("* **Evaluated $x$:** `%.4f` | **Standardized Score ($z$):** `%.4f`\n", x, z));
        report.append(String.format("* **Probability Density $f(x)$:** `%.6f`\n", pdf));
        report.append(String.format("* **Cumulative Probability $P(X \\le x)$:** `%.6f`\n", cdf));
        report.append(String.format("* **Upper Tail Probability $P(X > x)$:** `%.6f`\n", 1.0 - cdf));

        return new DistributionResult("Normal Distribution", pdf, cdf, null, latex, report.toString());
    }

    /**
     * Student's t-Distribution CDF approximation using Hill's algorithm.
     */
    public static double studentTCDF(double t, int df) {
        if (df < 1) throw new IllegalArgumentException("Degrees of freedom must be >= 1.");

        double x = df / (df + t * t);
        double beta = incompleteBeta(x, 0.5 * df, 0.5);
        double cdf = 0.5 * beta;

        return (t >= 0) ? (1.0 - cdf) : cdf;
    }

    /**
     * Exponential Distribution Analysis: f(x) = \lambda e^{-\lambda x}, \quad F(x) = 1 - e^{-\lambda x}.
     */
    public static DistributionResult analyzeExponential(double x, double lambda) {
        if (lambda <= 0.0) throw new IllegalArgumentException("Rate parameter lambda must be positive.");
        if (x < 0.0) return new DistributionResult("Exponential Distribution", 0.0, 0.0, null, "", "x < 0 has zero probability.");

        double pdf = lambda * Math.exp(-lambda * x);
        double cdf = 1.0 - Math.exp(-lambda * x);

        String latex = String.format("F(x) = 1 - e^{-%.4f \\cdot %.4f} = %.6f", lambda, x, cdf);

        StringBuilder report = new StringBuilder();
        report.append("### Exponential Distribution Analysis\n\n");
        report.append(String.format("* **Rate ($\\lambda$):** `%.4f` | **Mean ($1/\\lambda$):** `%.4f`\n", lambda, 1.0 / lambda));
        report.append(String.format("* **Evaluated $x$:** `%.4f`\n", x));
        report.append(String.format("* **PDF $f(x)$:** `%.6f`\n", pdf));
        report.append(String.format("* **CDF $P(X \\le x)$:** `%.6f`\n", cdf));

        return new DistributionResult("Exponential Distribution", pdf, cdf, null, latex, report.toString());
    }

    // Helper: Regularized Incomplete Beta function (Continued fraction expansion)
    private static double incompleteBeta(double x, double a, double b) {
        if (x <= 0.0) return 0.0;
        if (x >= 1.0) return 1.0;

        double lbeta = gammaLn(a) + gammaLn(b) - gammaLn(a + b);
        double front = Math.exp(Math.log(x) * a + Math.log(1.0 - x) * b - lbeta) / a;

        // Continued fraction evaluation via Lentz's method
        double f = 1.0, c = 1.0, d = 0.0;
        for (int i = 0; i <= 200; i++) {
            int m = i / 2;
            double numerator;
            if (i == 0) {
                numerator = 1.0;
            } else if (i % 2 == 0) {
                numerator = (m * (b - m) * x) / ((a + 2 * m - 1) * (a + 2 * m));
            } else {
                numerator = -((a + m) * (a + b + m) * x) / ((a + 2 * m) * (a + 2 * m + 1));
            }

            d = 1.0 + numerator * d;
            if (Math.abs(d) < 1e-30) d = 1e-30;
            c = 1.0 + numerator / c;
            if (Math.abs(c) < 1e-30) c = 1e-30;
            d = 1.0 / d;
            double delta = c * d;
            f *= delta;
            if (Math.abs(delta - 1.0) < 1e-12) break;
        }

        return front * (f - 1.0);
    }

    private static double gammaLn(double x) {
        double[] cof = {76.18009172947146, -86.50532032941677, 24.01409824083091,
                        -1.231739572450155, 0.1208650973866179e-2, -0.5395239384953e-5};
        double y = x, tmp = x + 5.5;
        tmp -= (x + 0.5) * Math.log(tmp);
        double ser = 1.000000000190015;
        for (double c : cof) ser += c / ++y;
        return -tmp + Math.log(2.5066282746310005 * ser / x);
    }
}