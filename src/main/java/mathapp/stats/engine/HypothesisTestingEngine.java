package mathapp.stats.engine;

import mathapp.stats.dto.HypothesisTestResult;

import java.util.Arrays;

/**
 * Inferential Statistics Engine performing Z-tests, t-tests, and Chi-Square goodness-of-fit tests.
 */
public class HypothesisTestingEngine {

    /**
     * One-Sample Z-Test for Population Mean \mu (Known \sigma).
     */
    public static HypothesisTestResult oneSampleZTest(double sampleMean, double mu0, double sigma, int n, double alpha, String tail) {
        if (n <= 0 || sigma <= 0.0) throw new IllegalArgumentException("Sample size n and sigma must be positive.");

        double se = sigma / Math.sqrt(n);
        double z = (sampleMean - mu0) / se;

        double pValue;
        double zCrit;

        if ("two-tailed".equalsIgnoreCase(tail)) {
            pValue = 2.0 * (1.0 - ContinuousDistributionEngine.standardNormalCDF(Math.abs(z)));
            zCrit = ContinuousDistributionEngine.inverseStandardNormalCDF(1.0 - alpha / 2.0);
        } else if ("right".equalsIgnoreCase(tail)) {
            pValue = 1.0 - ContinuousDistributionEngine.standardNormalCDF(z);
            zCrit = ContinuousDistributionEngine.inverseStandardNormalCDF(1.0 - alpha);
        } else { // left
            pValue = ContinuousDistributionEngine.standardNormalCDF(z);
            zCrit = -ContinuousDistributionEngine.inverseStandardNormalCDF(1.0 - alpha);
        }

        boolean reject = pValue < alpha;
        double margin = zCrit * se;
        double ciLower = sampleMean - margin;
        double ciUpper = sampleMean + margin;

        String h0 = String.format("H_0: \\mu = %.4f", mu0);
        String h1 = String.format("H_1: \\mu \\neq %.4f", mu0);

        StringBuilder report = new StringBuilder();
        report.append("### One-Sample Z-Test Analysis\n\n");
        report.append(String.format("* **Null Hypothesis ($H_0$):** $%s$\n", h0));
        report.append(String.format("* **Alternative Hypothesis ($H_1$):** $%s$\n", h1));
        report.append(String.format("* **Sample Mean ($\\bar{x}$):** `%.4f` | **Hypothesized Mean ($\\mu_0$):** `%.4f`\n", sampleMean, mu0));
        report.append(String.format("* **Standard Error ($SE$):** `%.4f`\n", se));
        report.append(String.format("* **Test Statistic ($Z$):** `%.4f`\n", z));
        report.append(String.format("* **$p$-value:** `%.6f` | **Significance Level ($\\alpha$):** `%.2f`\n", pValue, alpha));
        report.append(String.format("* **Decision:** `%s` Null Hypothesis\n", reject ? "REJECT" : "FAIL TO REJECT"));
        report.append(String.format("* **%.1f%% Confidence Interval:** `[%.4f, %.4f]`\n", (1.0 - alpha) * 100.0, ciLower, ciUpper));

        return new HypothesisTestResult("One-Sample Z-Test", z, pValue, zCrit, alpha, 0, reject, ciLower, ciUpper, h0, h1, report.toString());
    }

    /**
     * One-Sample t-Test for Population Mean \mu (Unknown \sigma).
     */
    public static HypothesisTestResult oneSampleTTest(double[] sample, double mu0, double alpha) {
        if (sample == null || sample.length < 2) throw new IllegalArgumentException("t-Test requires at least 2 sample observations.");

        int n = sample.length;
        int df = n - 1;
        double mean = Arrays.stream(sample).average().orElse(0.0);

        double sumSq = 0.0;
        for (double val : sample) sumSq += Math.pow(val - mean, 2);
        double s = Math.sqrt(sumSq / (n - 1));

        double se = s / Math.sqrt(n);
        double t = (mean - mu0) / se;

        double pValue = 2.0 * (1.0 - ContinuousDistributionEngine.studentTCDF(Math.abs(t), df));
        boolean reject = pValue < alpha;

        String h0 = String.format("H_0: \\mu = %.4f", mu0);
        String h1 = String.format("H_1: \\mu \\neq %.4f", mu0);

        StringBuilder report = new StringBuilder();
        report.append("### One-Sample t-Test Analysis\n\n");
        report.append(String.format("* **Sample Size ($n$):** `%d` | **Degrees of Freedom ($df$):** `%d`\n", n, df));
        report.append(String.format("* **Sample Mean ($\\bar{x}$):** `%.4f` | **Sample Std Dev ($s$):** `%.4f`\n", mean, s));
        report.append(String.format("* **t-Statistic:** `%.4f`\n", t));
        report.append(String.format("* **$p$-value:** `%.6f`\n", pValue));
        report.append(String.format("* **Verdict:** `%s` $H_0$ at $\\alpha = %.2f$\n", reject ? "REJECT" : "FAIL TO REJECT", alpha));

        return new HypothesisTestResult("One-Sample t-Test", t, pValue, 0.0, alpha, df, reject, 0.0, 0.0, h0, h1, report.toString());
    }

    /**
     * Chi-Square Goodness-of-Fit Test: \chi^2 = \sum \frac{(O_i - E_i)^2}{E_i}
     */
    public static HypothesisTestResult chiSquareGoodnessOfFit(double[] observed, double[] expected, double alpha) {
        if (observed.length != expected.length || observed.length < 2) {
            throw new IllegalArgumentException("Observed and expected arrays must match in length >= 2.");
        }

        int k = observed.length;
        int df = k - 1;
        double chi2 = 0.0;

        for (int i = 0; i < k; i++) {
            if (expected[i] <= 0.0) throw new IllegalArgumentException("Expected frequencies must be strictly positive.");
            double diff = observed[i] - expected[i];
            chi2 += (diff * diff) / expected[i];
        }

        // Approximate p-value for Chi-square using Wilson-Hilferty transformation
        double z = Math.pow(chi2 / df, 1.0 / 3.0) - (1.0 - 2.0 / (9.0 * df));
        z /= Math.sqrt(2.0 / (9.0 * df));
        double pValue = 1.0 - ContinuousDistributionEngine.standardNormalCDF(z);

        boolean reject = pValue < alpha;

        StringBuilder report = new StringBuilder();
        report.append("### Chi-Square Goodness-of-Fit Test\n\n");
        report.append(String.format("* **Categories ($k$):** `%d` | **Degrees of Freedom ($df$):** `%d`\n", k, df));
        report.append(String.format("* **$\\chi^2$ Test Statistic:** `%.4f`\n", chi2));
        report.append(String.format("* **Approximate $p$-value:** `%.6f`\n", pValue));
        report.append(String.format("* **Verdict:** `%s` $H_0$ at $\\alpha = %.2f$\n", reject ? "REJECT" : "FAIL TO REJECT", alpha));

        return new HypothesisTestResult("Chi-Square Goodness-of-Fit", chi2, pValue, 0.0, alpha, df, reject, 0.0, 0.0, "H_0: Distributions Match", "H_1: Distributions Differ", report.toString());
    }
}