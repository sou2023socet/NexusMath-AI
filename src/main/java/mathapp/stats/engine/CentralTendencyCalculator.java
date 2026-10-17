package mathapp.stats.engine;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Pure mathematical engine for central tendency calculations.
 * Supports both ungrouped raw datasets and binned/grouped frequency distributions.
 */
public class CentralTendencyCalculator {

    /**
     * Calculates the Arithmetic Mean: \bar{x} = \frac{1}{n} \sum_{i=1}^n x_i
     */
    public static double calculateArithmeticMean(List<Double> data) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Cannot calculate mean for an empty or null dataset.");
        }
        double sum = 0.0;
        for (double val : data) {
            sum += val;
        }
        return sum / data.size();
    }

    /**
     * Calculates the Weighted Mean: \bar{x}_w = \frac{\sum_{i=1}^n w_i x_i}{\sum_{i=1}^n w_i}
     */
    public static double calculateWeightedMean(List<Double> data, List<Double> weights) {
        if (data == null || weights == null || data.isEmpty() || data.size() != weights.size()) {
            throw new IllegalArgumentException("Data and weights lists must be non-null and of equal size.");
        }
        double weightedSum = 0.0;
        double weightSum = 0.0;

        for (int i = 0; i < data.size(); i++) {
            double w = weights.get(i);
            if (w < 0) {
                throw new IllegalArgumentException("Weights must be non-negative.");
            }
            weightedSum += data.get(i) * w;
            weightSum += w;
        }

        if (weightSum == 0) {
            throw new ArithmeticException("Sum of weights cannot be zero.");
        }
        return weightedSum / weightSum;
    }

    /**
     * Calculates the Geometric Mean: G = \exp\left(\frac{1}{n} \sum_{i=1}^n \ln(x_i)\right)
     * Requires all values x_i > 0.
     */
    public static double calculateGeometricMean(List<Double> data) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Dataset cannot be empty.");
        }
        double logSum = 0.0;
        for (double val : data) {
            if (val <= 0) {
                return Double.NaN; // Geometric mean is undefined for non-positive numbers
            }
            logSum += Math.log(val);
        }
        return Math.exp(logSum / data.size());
    }

    /**
     * Calculates the Harmonic Mean: H = \frac{n}{\sum_{i=1}^n \frac{1}{x_i}}
     * Requires all values x_i > 0.
     */
    public static double calculateHarmonicMean(List<Double> data) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Dataset cannot be empty.");
        }
        double reciprocalSum = 0.0;
        for (double val : data) {
            if (val <= 0) {
                return Double.NaN; // Harmonic mean is undefined for non-positive numbers
            }
            reciprocalSum += 1.0 / val;
        }
        return data.size() / reciprocalSum;
    }

    /**
     * Calculates the Trimmed Mean by removing a proportion p (0.0 <= p < 0.5) of values from both ends.
     */
    public static double calculateTrimmedMean(List<Double> data, double trimProportion) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Dataset cannot be empty.");
        }
        if (trimProportion < 0.0 || trimProportion >= 0.5) {
            throw new IllegalArgumentException("Trim proportion must be in range [0.0, 0.5).");
        }

        List<Double> sorted = new ArrayList<>(data);
        Collections.sort(sorted);

        int k = (int) Math.floor(data.size() * trimProportion);
        int remainingSize = sorted.size() - 2 * k;

        if (remainingSize <= 0) {
            return calculateArithmeticMean(sorted);
        }

        double sum = 0.0;
        for (int i = k; i < sorted.size() - k; i++) {
            sum += sorted.get(i);
        }
        return sum / remainingSize;
    }

    /**
     * Calculates the Median for raw (ungrouped) data.
     */
    public static double calculateUngroupedMedian(List<Double> data) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Dataset cannot be empty.");
        }
        List<Double> sorted = new ArrayList<>(data);
        Collections.sort(sorted);
        int n = sorted.size();

        if (n % 2 != 0) {
            return sorted.get(n / 2);
        } else {
            return (sorted.get((n / 2) - 1) + sorted.get(n / 2)) / 2.0;
        }
    }

    /**
     * Calculates the Grouped Median: L + \left( \frac{\frac{N}{2} - F}{f_m} \right) \cdot w
     *
     * @param lowerBounds Lower class boundaries
     * @param upperBounds Upper class boundaries
     * @param frequencies Class frequencies
     */
    public static double calculateGroupedMedian(List<Double> lowerBounds, List<Double> upperBounds, List<Double> frequencies) {
        double totalN = frequencies.stream().mapToDouble(Double::doubleValue).sum();
        double halfN = totalN / 2.0;

        double cumulativeFrequency = 0.0;
        int medianClassIndex = 0;
        double cfPrevious = 0.0;

        for (int i = 0; i < frequencies.size(); i++) {
            double f = frequencies.get(i);
            if (cumulativeFrequency + f >= halfN) {
                medianClassIndex = i;
                cfPrevious = cumulativeFrequency;
                break;
            }
            cumulativeFrequency += f;
        }

        double L = lowerBounds.get(medianClassIndex);
        double f_m = frequencies.get(medianClassIndex);
        double classWidth = upperBounds.get(medianClassIndex) - L;

        return L + (((halfN - cfPrevious) / f_m) * classWidth);
    }

    /**
     * Detects all Modes for raw (ungrouped) data (supports unimodal, bimodal, multimodal).
     */
    public static List<Double> calculateUngroupedModes(List<Double> data) {
        if (data == null || data.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Double, Long> frequencyMap = new HashMap<>();
        for (Double val : data) {
            frequencyMap.put(val, frequencyMap.getOrDefault(val, 0L) + 1L);
        }

        long maxFrequency = Collections.max(frequencyMap.values());

        // If all elements appear with equal frequency 1, or all frequencies are equal, return empty (no distinct mode)
        if (maxFrequency == 1 || frequencyMap.values().stream().distinct().count() == 1) {
            return Collections.emptyList();
        }

        return frequencyMap.entrySet().stream()
                .filter(entry -> entry.getValue() == maxFrequency)
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Calculates Grouped Mode using modal class interpolation:
     * Mode = L + \left( \frac{f_m - f_1}{(f_m - f_1) + (f_m - f_2)} \right) \cdot w
     */
    public static double calculateGroupedMode(List<Double> lowerBounds, List<Double> upperBounds, List<Double> frequencies) {
        int modalIndex = 0;
        double maxFreq = -1.0;

        for (int i = 0; i < frequencies.size(); i++) {
            if (frequencies.get(i) > maxFreq) {
                maxFreq = frequencies.get(i);
                modalIndex = i;
            }
        }

        double L = lowerBounds.get(modalIndex);
        double f_m = maxFreq;
        double f_1 = (modalIndex > 0) ? frequencies.get(modalIndex - 1) : 0.0;
        double f_2 = (modalIndex < frequencies.size() - 1) ? frequencies.get(modalIndex + 1) : 0.0;
        double w = upperBounds.get(modalIndex) - L;

        double denominator = (f_m - f_1) + (f_m - f_2);
        if (denominator == 0) {
            return L + (w / 2.0); // Fallback to midpoint if flat distribution
        }

        return L + (((f_m - f_1) / denominator) * w);
    }
}