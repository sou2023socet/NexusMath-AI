package mathapp.stats.engine;

import mathapp.stats.dto.DescriptiveStatsResult;

import java.util.*;

/**
 * Concrete implementation of IDescriptiveEngine assembling Central Tendency, Dispersion, Shape, and Outliers.
 */
public class DescriptiveEngineImpl implements IDescriptiveEngine {

    @Override
    public DescriptiveStatsResult computeDescriptiveStats(List<Double> data, boolean isSample, double binWidth) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Dataset cannot be empty.");
        }

        int n = data.size();
        double mean = CentralTendencyCalculator.calculateArithmeticMean(data);
        double median = CentralTendencyCalculator.calculateUngroupedMedian(data);
        List<Double> modes = CentralTendencyCalculator.calculateUngroupedModes(data);
        double primaryMode = modes.isEmpty() ? median : modes.get(0);

        double weightedMean = CentralTendencyCalculator.calculateArithmeticMean(data); // Equal weights
        double geometricMean = CentralTendencyCalculator.calculateGeometricMean(data);
        double harmonicMean = CentralTendencyCalculator.calculateHarmonicMean(data);
        double trimmedMean = CentralTendencyCalculator.calculateTrimmedMean(data, 0.10); // 10% trim

        double min = Collections.min(data);
        double max = Collections.max(data);
        double range = DispersionAndPartitionCalculator.calculateRange(data);

        double q1 = DispersionAndPartitionCalculator.calculateQ1(data);
        double q3 = DispersionAndPartitionCalculator.calculateQ3(data);
        double iqr = DispersionAndPartitionCalculator.calculateIQR(q1, q3);
        double semiIqr = DispersionAndPartitionCalculator.calculateSemiIQR(q1, q3);

        double variance = isSample ?
                DispersionAndPartitionCalculator.calculateSampleVariance(data, mean) :
                DispersionAndPartitionCalculator.calculatePopulationVariance(data, mean);

        double stdDev = isSample ?
                DispersionAndPartitionCalculator.calculateSampleStandardDeviation(data, mean) :
                DispersionAndPartitionCalculator.calculatePopulationStandardDeviation(data, mean);

        double sem = DispersionAndPartitionCalculator.calculateStandardErrorOfMean(stdDev, n);
        double mad = DispersionAndPartitionCalculator.calculateMeanAbsoluteDeviation(data, mean);
        double rms = DispersionAndPartitionCalculator.calculateRootMeanSquare(data);
        double cv = DispersionAndPartitionCalculator.calculateCoefficientOfVariation(stdDev, mean);

        double pearsonSkew = ShapeAndOutlierCalculator.calculatePearsonMedianSkewness(mean, median, stdDev);
        double fisherSkew = ShapeAndOutlierCalculator.calculateFisherPearsonSkewness(data, mean, stdDev, isSample);
        double excessKurtosis = ShapeAndOutlierCalculator.calculateExcessKurtosis(data, mean, stdDev, isSample);

        List<Double> outliersIqr = ShapeAndOutlierCalculator.detectOutliersIQR(data, q1, q3);
        List<Double> outliersZ = ShapeAndOutlierCalculator.detectOutliersZScore(data, mean, stdDev);

        String report = buildStepByStepReport(data, n, mean, median, modes, min, max, range,
                q1, q3, iqr, variance, stdDev, sem, fisherSkew, excessKurtosis, outliersIqr, isSample);

        return new DescriptiveStatsResult(
                n, mean, weightedMean, geometricMean, harmonicMean, trimmedMean,
                median, modes, min, max, range, q1, q3, iqr, semiIqr,
                variance, stdDev, sem, mad, rms, cv,
                pearsonSkew, fisherSkew, excessKurtosis,
                outliersIqr, outliersZ, isSample, report
        );
    }

    @Override
    public DescriptiveStatsResult computeGroupedStats(List<Double> midpoints, List<Double> frequencies, boolean isSample) {
        // Expands grouped frequency distribution for consolidated evaluation
        List<Double> expandedData = new ArrayList<>();
        for (int i = 0; i < midpoints.size(); i++) {
            int count = (int) Math.round(frequencies.get(i));
            for (int k = 0; k < count; k++) {
                expandedData.add(midpoints.get(i));
            }
        }
        return computeDescriptiveStats(expandedData, isSample, 1.0);
    }

    private String buildStepByStepReport(
            List<Double> data, int n, double mean, double median, List<Double> modes,
            double min, double max, double range, double q1, double q3, double iqr,
            double variance, double stdDev, double sem, double skew, double kurt,
            List<Double> outliers, boolean isSample
    ) {
        StringBuilder sb = new StringBuilder();
        sb.append("### Step-by-Step Descriptive Analysis Report\n\n");
        sb.append("**1. Sample Size & Central Tendency:**\n");
        sb.append(String.format("* Total Observations ($n$): %d\n", n));
        sb.append(String.format("* Mean ($\\bar{x}$): `%.4f` = $\\frac{\\sum x}{n}$\n", mean));
        sb.append(String.format("* Median ($Q_2$): `%.4f`\n", median));
        sb.append(String.format("* Mode(s): %s\n\n", modes.isEmpty() ? "No distinct mode" : modes.toString()));

        sb.append("**2. Dispersion & Spread:**\n");
        sb.append(String.format("* Min / Max: `%.4f` / `%.4f` (Range: `%.4f`)\n", min, max, range));
        sb.append(String.format("* First Quartile ($Q_1$): `%.4f` | Third Quartile ($Q_3$): `%.4f`\n", q1, q3));
        sb.append(String.format("* Interquartile Range (IQR): `%.4f`\n", iqr));
        sb.append(String.format("* %s Variance ($%s^2$): `%.4f`\n", isSample ? "Sample" : "Population", isSample ? "s" : "\\sigma", variance));
        sb.append(String.format("* Standard Deviation ($%s$): `%.4f`\n", isSample ? "s" : "\\sigma", stdDev));
        sb.append(String.format("* Standard Error of Mean (SEM): `%.4f`\n\n", sem));

        sb.append("**3. Distribution Shape & Outliers:**\n");
        sb.append(String.format("* Fisher-Pearson Skewness ($G_1$): `%.4f` (%s)\n",
                skew, skew > 0.5 ? "Right-skewed" : skew < -0.5 ? "Left-skewed" : "Approximately symmetric"));
        sb.append(String.format("* Excess Kurtosis ($G_2$): `%.4f` (%s)\n",
                kurt, kurt > 0.5 ? "Leptokurtic / Heavy-tailed" : kurt < -0.5 ? "Platykurtic / Light-tailed" : "Mesokurtic / Normal"));
        sb.append(String.format("* Outliers Detected ($1.5 \\times \\text{IQR}$): %s\n",
                outliers.isEmpty() ? "None detected" : outliers.toString()));

        return sb.toString();
    }
}