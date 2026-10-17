package mathapp.stats.engine;

import mathapp.stats.dto.RegressionResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Bivariate Statistical Analysis Engine for Covariance, Pearson & Spearman Correlation,
 * Least-Squares Linear Regression, Residual Analysis, and Forecasting.
 */
public class CorrelationRegressionEngine {

    /**
     * Performs complete bivariate analysis on paired datasets X and Y.
     *
     * @param x Data array for independent variable X
     * @param y Data array for dependent variable Y
     * @param xToPredict Optional X value for point forecasting (can be null)
     * @param alpha Significance level for prediction interval (e.g., 0.05)
     */
    public static RegressionResult analyzeBivariate(double[] x, double[] y, Double xToPredict, double alpha) {
        validateInputs(x, y);
        int n = x.length;

        // Means
        double meanX = Arrays.stream(x).average().orElse(0.0);
        double meanY = Arrays.stream(y).average().orElse(0.0);

        // Sums of squares and cross-products
        double sxx = 0.0;
        double syy = 0.0;
        double sxy = 0.0;

        for (int i = 0; i < n; i++) {
            double dx = x[i] - meanX;
            double dy = y[i] - meanY;
            sxx += dx * dx;
            syy += dy * dy;
            sxy += dx * dy;
        }

        if (sxx == 0.0) {
            throw new ArithmeticException("Variance of X is zero. Cannot compute linear regression.");
        }
        if (syy == 0.0) {
            throw new ArithmeticException("Variance of Y is zero. Cannot compute correlation.");
        }

        // Covariances
        double sampleCovariance = sxy / (n - 1);
        double populationCovariance = sxy / n;

        // Pearson Correlation (r)
        double pearsonR = sxy / Math.sqrt(sxx * syy);

        // Spearman Rank Correlation (\rho)
        double spearmanRho = computeSpearmanRankCorrelation(x, y);

        // Least-Squares Regression Line: y = beta1 * x + beta0
        double slope = sxy / sxx;
        double intercept = meanY - slope * meanX;

        // Coefficient of Determination (R^2)
        double rSquared = pearsonR * pearsonR;

        // Residuals and Sum of Squared Errors (SSE)
        List<Double> residuals = new ArrayList<>(n);
        double sse = 0.0;
        for (int i = 0; i < n; i++) {
            double yHat = slope * x[i] + intercept;
            double res = y[i] - yHat;
            residuals.add(res);
            sse += res * res;
        }

        // Standard Error of Estimate: s_e = \sqrt{\frac{SSE}{n - 2}}
        double standardErrorOfEstimate = (n > 2) ? Math.sqrt(sse / (n - 2)) : 0.0;

        // Point Prediction and Prediction Interval (if requested)
        Double predictedY = null;
        double predSE = 0.0;
        double ciLower = 0.0;
        double ciUpper = 0.0;

        if (xToPredict != null) {
            predictedY = slope * xToPredict + intercept;
            if (n > 2) {
                double xDiffSq = Math.pow(xToPredict - meanX, 2);
                predSE = standardErrorOfEstimate * Math.sqrt(1.0 + (1.0 / n) + (xDiffSq / sxx));
                double zCrit = ContinuousDistributionEngine.inverseStandardNormalCDF(1.0 - alpha / 2.0);
                ciLower = predictedY - zCrit * predSE;
                ciUpper = predictedY + zCrit * predSE;
            }
        }

        String equation = String.format("y = %.4fx + %.4f", slope, intercept);
        String latex = String.format("\\hat{y} = %.4fx %s %.4f, \\quad R^2 = %.4f",
                slope, (intercept >= 0 ? "+" : "-"), Math.abs(intercept), rSquared);

        StringBuilder report = new StringBuilder();
        report.append("### Bivariate Correlation & Linear Regression Analysis\n\n");
        report.append(String.format("* **Sample Size ($n$):** `%d`\n", n));
        report.append(String.format("* **Mean X ($\\bar{x}$):** `%.4f` | **Mean Y ($\\bar{y}$):** `%.4f`\n", meanX, meanY));
        report.append(String.format("* **Sample Covariance ($Cov(X,Y)$):** `%.4f`\n", sampleCovariance));
        report.append(String.format("* **Pearson Correlation ($r$):** `%.4f` (%s)\n",
                pearsonR, interpretCorrelationStrength(pearsonR)));
        report.append(String.format("* **Spearman Rank Correlation ($\\rho$):** `%.4f`\n", spearmanRho));
        report.append(String.format("* **Coefficient of Determination ($R^2$):** `%.4f` (Explains `%.1f%%` of Y variance)\n\n",
                rSquared, rSquared * 100.0));

        report.append("**Least-Squares Regression Line:**\n");
        report.append(String.format("* **Slope ($\\beta_1$):** `%.4f` = $\\frac{S_{xy}}{S_{xx}}$\n", slope));
        report.append(String.format("* **Intercept ($\\beta_0$):** `%.4f` = $\\bar{y} - \\beta_1 \\bar{x}$\n", intercept));
        report.append(String.format("* **Equation:** `%s`\n", equation));
        report.append(String.format("* **Standard Error of Estimate ($s_e$):** `%.4f`\n\n", standardErrorOfEstimate));

        if (xToPredict != null && predictedY != null) {
            report.append("**Prediction Output:**\n");
            report.append(String.format("* **Target $x_0$:** `%.4f` \\implies **Predicted $\\hat{y}_0$:** `%.4f`\n", xToPredict, predictedY));
            report.append(String.format("* **Standard Error of Forecast:** `%.4f`\n", predSE));
            report.append(String.format("* **%.1f%% Prediction Interval:** `[%.4f, %.4f]`\n", (1.0 - alpha) * 100.0, ciLower, ciUpper));
        }

        return new RegressionResult(n, sampleCovariance, populationCovariance, pearsonR, spearmanRho,
                rSquared, slope, intercept, standardErrorOfEstimate, residuals, xToPredict, predictedY,
                predSE, ciLower, ciUpper, equation, latex, report.toString());
    }

    /**
     * Computes Spearman's Rank Correlation Coefficient (\rho) using fractional ranking for ties.
     * Formula: \rho = 1 - \frac{6 \sum d_i^2}{n(n^2 - 1)}
     */
    public static double computeSpearmanRankCorrelation(double[] x, double[] y) {
        validateInputs(x, y);
        int n = x.length;

        double[] rankX = computeRanks(x);
        double[] rankY = computeRanks(y);

        double sumD2 = 0.0;
        for (int i = 0; i < n; i++) {
            double d = rankX[i] - rankY[i];
            sumD2 += d * d;
        }

        return 1.0 - (6.0 * sumD2) / (n * ((double) n * n - 1.0));
    }

    private static double[] computeRanks(double[] values) {
        int n = values.length;
        Integer[] indices = new Integer[n];
        for (int i = 0; i < n; i++) indices[i] = i;

        Arrays.sort(indices, (a, b) -> Double.compare(values[a], values[b]));

        double[] ranks = new double[n];
        int i = 0;
        while (i < n) {
            int j = i;
            while (j < n - 1 && Math.abs(values[indices[j]] - values[indices[j + 1]]) < 1e-9) {
                j++;
            }

            double averageRank = (i + 1 + j + 1) / 2.0;
            for (int k = i; k <= j; k++) {
                ranks[indices[k]] = averageRank;
            }
            i = j + 1;
        }

        return ranks;
    }

    private static String interpretCorrelationStrength(double r) {
        double absR = Math.abs(r);
        String direction = r >= 0 ? "Positive" : "Negative";
        if (absR >= 0.90) return "Very Strong " + direction;
        if (absR >= 0.70) return "Strong " + direction;
        if (absR >= 0.50) return "Moderate " + direction;
        if (absR >= 0.30) return "Weak " + direction;
        return "Negligible / Very Weak";
    }

    private static void validateInputs(double[] x, double[] y) {
        if (x == null || y == null) {
            throw new IllegalArgumentException("Input data arrays cannot be null.");
        }
        if (x.length != y.length) {
            throw new IllegalArgumentException(
                String.format("Data length mismatch: X length (%d) != Y length (%d).", x.length, y.length)
            );
        }
        if (x.length < 3) {
            throw new IllegalArgumentException("Correlation and regression require at least 3 paired observations.");
        }
    }
}