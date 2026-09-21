package mathapp.stats.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pure mathematical engine for skewness, kurtosis, and outlier detection routines.
 */
public class ShapeAndOutlierCalculator {

    /**
     * Calculates Pearson's Mode Skewness: \frac{\bar{x} - \text{Mode}}{s}
     */
    public static double calculatePearsonModeSkewness(double mean, double mode, double standardDeviation) {
        if (standardDeviation == 0.0) return 0.0;
        return (mean - mode) / standardDeviation;
    }

    /**
     * Calculates Pearson's Median Skewness: \frac{3(\bar{x} - \text{Median})}{s}
     */
    public static double calculatePearsonMedianSkewness(double mean, double median, double standardDeviation) {
        if (standardDeviation == 0.0) return 0.0;
        return 3.0 * (mean - median) / standardDeviation;
    }

    /**
     * Calculates Adjusted Fisher-Pearson Standardized Skewness Coefficient (G_1):
     * G_1 = \frac{n}{(n-1)(n-2)} \sum_{i=1}^n \left( \frac{x_i - \bar{x}}{s} \right)^3
     */
    public static double calculateFisherPearsonSkewness(List<Double> data, double mean, double standardDeviation, boolean isSample) {
        if (data == null || data.size() < 3 || standardDeviation == 0.0) {
            return 0.0;
        }

        int n = data.size();
        double sumCubedDeviations = 0.0;
        for (double val : data) {
            double dev = (val - mean) / standardDeviation;
            sumCubedDeviations += Math.pow(dev, 3);
        }

        if (isSample) {
            double factor = (double) n / ((n - 1) * (n - 2));
            return factor * sumCubedDeviations;
        } else {
            return sumCubedDeviations / n;
        }
    }

    /**
     * Calculates Sample Excess Kurtosis (G_2):
     * G_2 = \frac{n(n+1)}{(n-1)(n-2)(n-3)} \sum \left(\frac{x_i - \bar{x}}{s}\right)^4 - \frac{3(n-1)^2}{(n-2)(n-3)}
     */
    public static double calculateExcessKurtosis(List<Double> data, double mean, double standardDeviation, boolean isSample) {
        if (data == null || data.size() < 4 || standardDeviation == 0.0) {
            return 0.0;
        }

        int n = data.size();
        double sumFourthDeviations = 0.0;
        for (double val : data) {
            double dev = (val - mean) / standardDeviation;
            sumFourthDeviations += Math.pow(dev, 4);
        }

        if (isSample) {
            double term1 = ((double) n * (n + 1)) / ((n - 1) * (n - 2) * (n - 3));
            double term2 = (3.0 * Math.pow(n - 1, 2)) / ((n - 2) * (n - 3));
            return (term1 * sumFourthDeviations) - term2;
        } else {
            return (sumFourthDeviations / n) - 3.0;
        }
    }

    /**
     * Identifies outliers using 1.5 * IQR rule (Tukey's Fences):
     * Outlier if x < Q1 - 1.5*IQR or x > Q3 + 1.5*IQR
     */
    public static List<Double> detectOutliersIQR(List<Double> data, double q1, double q3) {
        if (data == null || data.isEmpty()) {
            return Collections.emptyList();
        }

        double iqr = q3 - q1;
        double lowerFence = q1 - 1.5 * iqr;
        double upperFence = q3 + 1.5 * iqr;

        List<Double> outliers = new ArrayList<>();
        for (double val : data) {
            if (val < lowerFence || val > upperFence) {
                outliers.add(val);
            }
        }
        return outliers;
    }

    /**
     * Identifies outliers using 3-Sigma Rule:
     * Outlier if |z| = |(x - mean) / standardDeviation| > 3.0
     */
    public static List<Double> detectOutliersZScore(List<Double> data, double mean, double standardDeviation) {
        if (data == null || data.isEmpty() || standardDeviation == 0.0) {
            return Collections.emptyList();
        }

        List<Double> outliers = new ArrayList<>();
        for (double val : data) {
            double zScore = Math.abs(val - mean) / standardDeviation;
            if (zScore > 3.0) {
                outliers.add(val);
            }
        }
        return outliers;
    }
}