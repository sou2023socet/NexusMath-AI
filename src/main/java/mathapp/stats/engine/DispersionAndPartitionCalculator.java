package mathapp.stats.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pure mathematical engine for dispersion and partitioning statistics.
 */
public class DispersionAndPartitionCalculator {

    /**
     * Calculates Range: \text{Range} = x_{\max} - x_{\min}
     */
    public static double calculateRange(List<Double> data) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Dataset cannot be empty or null.");
        }
        double min = Collections.min(data);
        double max = Collections.max(data);
        return max - min;
    }

    /**
     * Calculates Sample Variance: s^2 = \frac{1}{n-1} \sum_{i=1}^n (x_i - \bar{x})^2
     */
    public static double calculateSampleVariance(List<Double> data, double mean) {
        if (data == null || data.size() < 2) {
            throw new IllegalArgumentException("Sample variance requires at least 2 data points.");
        }
        double sumSquaredDiffs = 0.0;
        for (double val : data) {
            double diff = val - mean;
            sumSquaredDiffs += diff * diff;
        }
        return sumSquaredDiffs / (data.size() - 1);
    }

    /**
     * Calculates Population Variance: \sigma^2 = \frac{1}{n} \sum_{i=1}^n (x_i - \mu)^2
     */
    public static double calculatePopulationVariance(List<Double> data, double mean) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Dataset cannot be empty or null.");
        }
        double sumSquaredDiffs = 0.0;
        for (double val : data) {
            double diff = val - mean;
            sumSquaredDiffs += diff * diff;
        }
        return sumSquaredDiffs / data.size();
    }

    /**
     * Calculates Sample Standard Deviation: s = \sqrt{s^2}
     */
    public static double calculateSampleStandardDeviation(List<Double> data, double mean) {
        return Math.sqrt(calculateSampleVariance(data, mean));
    }

    /**
     * Calculates Population Standard Deviation: \sigma = \sqrt{\sigma^2}
     */
    public static double calculatePopulationStandardDeviation(List<Double> data, double mean) {
        return Math.sqrt(calculatePopulationVariance(data, mean));
    }

    /**
     * Calculates Standard Error of the Mean (SEM): \text{SEM} = \frac{s}{\sqrt{n}}
     */
    public static double calculateStandardErrorOfMean(double standardDeviation, int sampleSize) {
        if (sampleSize <= 0) {
            throw new IllegalArgumentException("Sample size must be greater than 0.");
        }
        return standardDeviation / Math.sqrt(sampleSize);
    }

    /**
     * Calculates Mean Absolute Deviation (MAD): \text{MAD} = \frac{1}{n} \sum_{i=1}^n |x_i - \bar{x}|
     */
    public static double calculateMeanAbsoluteDeviation(List<Double> data, double mean) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Dataset cannot be empty or null.");
        }
        double sumAbsDiffs = 0.0;
        for (double val : data) {
            sumAbsDiffs += Math.abs(val - mean);
        }
        return sumAbsDiffs / data.size();
    }

    /**
     * Calculates Root Mean Square (RMS): \text{RMS} = \sqrt{\frac{1}{n} \sum_{i=1}^n x_i^2}
     */
    public static double calculateRootMeanSquare(List<Double> data) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Dataset cannot be empty or null.");
        }
        double sumSquares = 0.0;
        for (double val : data) {
            sumSquares += val * val;
        }
        return Math.sqrt(sumSquares / data.size());
    }

    /**
     * Calculates Coefficient of Variation (CV): \text{CV} = \frac{s}{\bar{x}} \times 100\%
     */
    public static double calculateCoefficientOfVariation(double standardDeviation, double mean) {
        if (mean == 0.0) {
            return Double.NaN; // CV is undefined for a zero mean
        }
        return (standardDeviation / Math.abs(mean)) * 100.0;
    }

    /**
     * Calculates Percentile using linear interpolation on a sorted copy of dataset.
     * 
     * @param data Raw input list
     * @param percentile Value in range [0, 100]
     */
    public static double calculatePercentile(List<Double> data, double percentile) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Dataset cannot be empty or null.");
        }
        if (percentile < 0.0 || percentile > 100.0) {
            throw new IllegalArgumentException("Percentile must be between 0 and 100.");
        }

        List<Double> sorted = new ArrayList<>(data);
        Collections.sort(sorted);
        int n = sorted.size();

        if (n == 1) return sorted.get(0);
        if (percentile == 0.0) return sorted.get(0);
        if (percentile == 100.0) return sorted.get(n - 1);

        double rank = (percentile / 100.0) * (n - 1);
        int index = (int) Math.floor(rank);
        double fraction = rank - index;

        if (index >= n - 1) {
            return sorted.get(n - 1);
        }

        return sorted.get(index) + fraction * (sorted.get(index + 1) - sorted.get(index));
    }

    /**
     * Calculates Decile (D1 through D9) corresponding to percentile d * 10.
     * 
     * @param decile Number from 1 to 9
     */
    public static double calculateDecile(List<Double> data, int decile) {
        if (decile < 1 || decile > 9) {
            throw new IllegalArgumentException("Decile must be an integer between 1 and 9.");
        }
        return calculatePercentile(data, decile * 10.0);
    }

    /**
     * Calculates First Quartile (Q1 - 25th percentile).
     */
    public static double calculateQ1(List<Double> data) {
        return calculatePercentile(data, 25.0);
    }

    /**
     * Calculates Third Quartile (Q3 - 75th percentile).
     */
    public static double calculateQ3(List<Double> data) {
        return calculatePercentile(data, 75.0);
    }

    /**
     * Calculates Interquartile Range (IQR): \text{IQR} = Q_3 - Q_1
     */
    public static double calculateIQR(double q1, double q3) {
        return q3 - q1;
    }

    /**
     * Calculates Semi-Interquartile Range (Quartile Deviation): \text{Semi-IQR} = \frac{Q_3 - Q_1}{2}
     */
    public static double calculateSemiIQR(double q1, double q3) {
        return (q3 - q1) / 2.0;
    }
}