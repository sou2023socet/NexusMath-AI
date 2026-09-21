package mathapp.stats.engine;

import java.util.*;

/**
 * Pure mathematical engine for hypothesis testing: t-tests, ANOVA, Chi-Square,
 * and p-value numerical approximations for Student's t and F distributions.
 */
public class HypothesisTestingCalculator {

    // =========================================================================
    // 1. ONE-SAMPLE T-TEST
    // =========================================================================

    public record TTestResult(
            double testStatistic,
            double degreesOfFreedom,
            double pValueTwoTailed,
            double pValueOneTailed,
            double mean,
            double stdError,
            double ciLower,
            double ciUpper
    ) {}

    public static TTestResult calculateOneSampleTTest(double[] sample, double targetMean, double alpha) {
        int n = sample.length;
        if (n < 2) {
            throw new IllegalArgumentException("Sample size must be at least 2 for t-test.");
        }

        double mean = Arrays.stream(sample).average().orElse(0.0);
        double sumSq = 0.0;
        for (double v : sample) {
            double diff = v - mean;
            sumSq += diff * diff;
        }
        double variance = sumSq / (n - 1);
        double stdDev = Math.sqrt(variance);
        double stdError = stdDev / Math.sqrt(n);

        double t = (stdError == 0.0) ? 0.0 : (mean - targetMean) / stdError;
        double df = n - 1;

        double pTwoTailed = calculateStudentTPValue(Math.abs(t), df) * 2.0;
        pTwoTailed = Math.min(1.0, Math.max(0.0, pTwoTailed));
        double pOneTailed = pTwoTailed / 2.0;

        // Approximate critical value for confidence interval using normal-t adjustment
        double zCrit = DistributionsCalculator.calculateInverseStandardNormalCDF(1.0 - alpha / 2.0);
        double tCritApprox = zCrit + (zCrit * zCrit * zCrit + zCrit) / (4.0 * df);

        double ciLower = mean - tCritApprox * stdError;
        double ciUpper = mean + tCritApprox * stdError;

        return new TTestResult(t, df, pTwoTailed, pOneTailed, mean, stdError, ciLower, ciUpper);
    }

    // =========================================================================
    // 2. TWO-SAMPLE WELCH'S T-TEST (UNEQUAL VARIANCES)
    // =========================================================================

    public static TTestResult calculateWelchTTest(double[] sample1, double[] sample2, double alpha) {
        int n1 = sample1.length;
        int n2 = sample2.length;
        if (n1 < 2 || n2 < 2) {
            throw new IllegalArgumentException("Both samples must contain at least 2 observations.");
        }

        double mean1 = Arrays.stream(sample1).average().orElse(0.0);
        double mean2 = Arrays.stream(sample2).average().orElse(0.0);

        double var1 = computeSampleVariance(sample1, mean1);
        double var2 = computeSampleVariance(sample2, mean2);

        double se1 = var1 / n1;
        double se2 = var2 / n2;
        double pooledSE = Math.sqrt(se1 + se2);

        double t = (pooledSE == 0.0) ? 0.0 : (mean1 - mean2) / pooledSE;

        // Welch-Satterthwaite equation for degrees of freedom:
        // df = \frac{(se_1 + se_2)^2}{\frac{se_1^2}{n_1 - 1} + \frac{se_2^2}{n_2 - 1}}
        double num = Math.pow(se1 + se2, 2);
        double denom = (Math.pow(se1, 2) / (n1 - 1)) + (Math.pow(se2, 2) / (n2 - 1));
        double df = (denom == 0.0) ? (n1 + n2 - 2) : num / denom;

        double pTwoTailed = Math.min(1.0, Math.max(0.0, calculateStudentTPValue(Math.abs(t), df) * 2.0));
        double pOneTailed = pTwoTailed / 2.0;

        double diff = mean1 - mean2;
        double zCrit = DistributionsCalculator.calculateInverseStandardNormalCDF(1.0 - alpha / 2.0);
        double tCritApprox = zCrit + (zCrit * zCrit * zCrit + zCrit) / (4.0 * df);

        double ciLower = diff - tCritApprox * pooledSE;
        double ciUpper = diff + tCritApprox * pooledSE;

        return new TTestResult(t, df, pTwoTailed, pOneTailed, diff, pooledSE, ciLower, ciUpper);
    }

    // =========================================================================
    // 3. PAIRED T-TEST
    // =========================================================================

    public static TTestResult calculatePairedTTest(double[] before, double[] after, double alpha) {
        if (before.length != after.length) {
            throw new IllegalArgumentException("Paired t-test requires equal sample sizes.");
        }
        int n = before.length;
        double[] differences = new double[n];
        for (int i = 0; i < n; i++) {
            differences[i] = before[i] - after[i];
        }
        return calculateOneSampleTTest(differences, 0.0, alpha);
    }

    // =========================================================================
    // 4. ONE-WAY ANOVA (ANALYSIS OF VARIANCE)
    // =========================================================================

    public record AnovaResult(
            double fStatistic,
            int dfBetween,
            int dfWithin,
            double pValue,
            double ssBetween,
            double ssWithin,
            double ssTotal,
            double msBetween,
            double msWithin
    ) {}

    public static AnovaResult calculateOneWayANOVA(List<double[]> groups) {
        int k = groups.size();
        if (k < 2) {
            throw new IllegalArgumentException("One-Way ANOVA requires at least 2 groups.");
        }

        int N = 0;
        double grandSum = 0.0;
        for (double[] group : groups) {
            N += group.length;
            for (double v : group) grandSum += v;
        }
        double grandMean = grandSum / N;

        double ssBetween = 0.0;
        double ssWithin = 0.0;

        for (double[] group : groups) {
            int n_i = group.length;
            double groupMean = Arrays.stream(group).average().orElse(0.0);
            ssBetween += n_i * Math.pow(groupMean - grandMean, 2);

            for (double v : group) {
                ssWithin += Math.pow(v - groupMean, 2);
            }
        }

        double ssTotal = ssBetween + ssWithin;
        int dfBetween = k - 1;
        int dfWithin = N - k;

        double msBetween = ssBetween / dfBetween;
        double msWithin = ssWithin / dfWithin;

        double f = (msWithin == 0.0) ? 0.0 : msBetween / msWithin;
        double pValue = calculateFPValue(f, dfBetween, dfWithin);

        return new AnovaResult(f, dfBetween, dfWithin, pValue, ssBetween, ssWithin, ssTotal, msBetween, msWithin);
    }

    // =========================================================================
    // 5. CHI-SQUARE GOODNESS-OF-FIT TEST
    // =========================================================================

    public record ChiSquareTestResult(
            double chiSquare,
            int degreesOfFreedom,
            double pValue
    ) {}

    public static ChiSquareTestResult calculateChiSquareGoodnessOfFit(double[] observed, double[] expected) {
        if (observed.length != expected.length || observed.length < 2) {
            throw new IllegalArgumentException("Observed and expected arrays must be equal size >= 2.");
        }

        double chiSquare = 0.0;
        for (int i = 0; i < observed.length; i++) {
            if (expected[i] <= 0) {
                throw new IllegalArgumentException("Expected frequencies must be strictly positive.");
            }
            double diff = observed[i] - expected[i];
            chiSquare += (diff * diff) / expected[i];
        }

        int df = observed.length - 1;
        double pValue = calculateChiSquarePValue(chiSquare, df);

        return new ChiSquareTestResult(chiSquare, df, pValue);
    }

    // =========================================================================
    // NUMERICAL APPROXIMATIONS FOR P-VALUES
    // =========================================================================

    /**
     * Tail probability P(T > t) for Student's t-distribution using regularized incomplete beta approximation.
     */
    public static double calculateStudentTPValue(double t, double df) {
        double x = df / (df + t * t);
        return 0.5 * incompleteBeta(x, 0.5 * df, 0.5);
    }

    /**
     * Upper tail probability P(F > f) for F-distribution.
     */
    public static double calculateFPValue(double f, int df1, int df2) {
        if (f <= 0.0) return 1.0;
        double x = df2 / (df2 + df1 * f);
        return incompleteBeta(x, 0.5 * df2, 0.5 * df1);
    }

    /**
     * Upper tail probability P(X^2 > x) for Chi-Square distribution using incomplete gamma function.
     */
    public static double calculateChiSquarePValue(double chiSq, int df) {
        if (chiSq <= 0.0) return 1.0;
        return 1.0 - incompleteGamma(0.5 * df, 0.5 * chiSq);
    }

    // Series expansion approximation for Regularized Incomplete Beta Function I_x(a, b)
    private static double incompleteBeta(double x, double a, double b) {
        if (x <= 0.0) return 0.0;
        if (x >= 1.0) return 1.0;

        double lbeta = logGamma(a) + logGamma(b) - logGamma(a + b);
        double front = Math.exp(a * Math.log(x) + b * Math.log(1.0 - x) - lbeta) / a;

        double sum = 1.0;
        double term = 1.0;
        for (int i = 1; i <= 200; i++) {
            term *= (i - b) * x / (a + i);
            sum += term;
            if (Math.abs(term) < 1e-10) break;
        }

        return Math.min(1.0, Math.max(0.0, front * sum));
    }

    // Series approximation for Lower Incomplete Gamma Function P(a, x)
    private static double incompleteGamma(double a, double x) {
        if (x <= 0.0) return 0.0;
        double sum = 1.0 / a;
        double term = 1.0 / a;
        for (int n = 1; n <= 200; n++) {
            term *= x / (a + n);
            sum += term;
            if (Math.abs(term) < 1e-10) break;
        }
        return Math.min(1.0, Math.max(0.0, Math.exp(-x + a * Math.log(x) - logGamma(a)) * sum));
    }

    // Lanczos 6-term approximation for Log-Gamma \ln \Gamma(z)
    private static double logGamma(double z) {
        double[] p = {
                676.5203681218851, -1259.1392167228333,
                771.3234287776531, -176.61502916214059,
                12.507343278686905, -0.13857109526572012
        };
        if (z < 0.5) {
            return Math.log(Math.PI / Math.sin(Math.PI * z)) - logGamma(1.0 - z);
        }
        z -= 1.0;
        double x = 0.99999999999980993;
        for (int i = 0; i < p.length; i++) {
            x += p[i] / (z + i + 1);
        }
        double t = z + p.length - 0.5;
        return 0.5 * Math.log(2.0 * Math.PI) + (z + 0.5) * Math.log(t) - t + Math.log(x);
    }

    private static double computeSampleVariance(double[] array, double mean) {
        double ss = 0.0;
        for (double v : array) {
            double d = v - mean;
            ss += d * d;
        }
        return ss / (array.length - 1);
    }
}