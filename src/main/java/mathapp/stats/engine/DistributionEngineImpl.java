package mathapp.stats.engine;

import mathapp.stats.dto.DistributionResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete probability distribution engine generating PMF/PDF data series, CDFs,
 * confidence interval bounds, and step-by-step reporting.
 */
public class DistributionEngineImpl implements IDistributionEngine {

    public record DistributionPoint(double x, double pdfOrPmf, double cdf) {}

    public record DistributionEvaluationResult(
            String distributionName,
            double mean,
            double variance,
            double stdDev,
            double evaluatedPdfOrPmf,
            double evaluatedCdf,
            double evaluatedSurvival,
            List<DistributionPoint> curvePoints,
            String stepByStepReport
    ) {}

    public DistributionEvaluationResult evaluateBinomial(int n, double p, int k) {
        double mean = n * p;
        double variance = n * p * (1.0 - p);
        double stdDev = Math.sqrt(variance);

        double pmfAtK = DistributionsCalculator.calculateBinomialPMF(n, k, p);
        double cdfAtK = DistributionsCalculator.calculateBinomialCDF(n, k, p);
        double survivalAtK = 1.0 - cdfAtK;

        List<DistributionPoint> points = new ArrayList<>();
        for (int i = 0; i <= n; i++) {
            double pmf = DistributionsCalculator.calculateBinomialPMF(n, i, p);
            double cdf = DistributionsCalculator.calculateBinomialCDF(n, i, p);
            points.add(new DistributionPoint(i, pmf, cdf));
        }

        String report = String.format("### Binomial Distribution Evaluation B(n=%d, p=%.4f)\n\n", n, p) +
                String.format("* Expected Value $E[X] = n \\cdot p$: `%.4f`\n", mean) +
                String.format("* Variance $\\text{Var}(X) = n \\cdot p \\cdot (1-p)$: `%.4f` (StdDev: `%.4f`)\n", variance, stdDev) +
                String.format("* Probability Mass $P(X = %d)$: `%.6f` (%.2f%%)\n", k, pmfAtK, pmfAtK * 100.0) +
                String.format("* Cumulative Probability $P(X \\le %d)$: `%.6f`\n", k, cdfAtK) +
                String.format("* Upper Tail Probability $P(X > %d)$: `%.6f`\n", k, survivalAtK);

        return new DistributionEvaluationResult("Binomial", mean, variance, stdDev, pmfAtK, cdfAtK, survivalAtK, points, report);
    }

    @Override
    public DistributionResult evaluateBinomial(int n, double p, double lower, double upper, int evalK) {
        DistributionEvaluationResult evaluation = evaluateBinomial(n, p, evalK);
        double cdfLower = DistributionsCalculator.calculateBinomialCDF(n, (int) Math.floor(lower) - 1, p);
        double cdfUpper = DistributionsCalculator.calculateBinomialCDF(n, (int) Math.floor(upper), p);
        return new DistributionResult("Binomial", evaluation.evaluatedPdfOrPmf(), cdfUpper - cdfLower,
                null, "P(X = " + evalK + ")", evaluation.stepByStepReport());
    }

    public DistributionEvaluationResult evaluateNormal(double mean, double stdDev, double x) {
        if (stdDev <= 0.0) {
            throw new IllegalArgumentException("Standard deviation must be strictly positive.");
        }

        double variance = stdDev * stdDev;
        double zScore = (x - mean) / stdDev;
        double pdfAtX = DistributionsCalculator.calculateStandardNormalPDF(zScore) / stdDev;
        double cdfAtX = DistributionsCalculator.calculateStandardNormalCDF(zScore);
        double survivalAtX = 1.0 - cdfAtX;

        List<DistributionPoint> points = new ArrayList<>();
        double startX = mean - 4.0 * stdDev;
        double endX = mean + 4.0 * stdDev;
        double step = (endX - startX) / 100.0;

        for (int i = 0; i <= 100; i++) {
            double currentX = startX + i * step;
            double z = (currentX - mean) / stdDev;
            double pdf = DistributionsCalculator.calculateStandardNormalPDF(z) / stdDev;
            double cdf = DistributionsCalculator.calculateStandardNormalCDF(z);
            points.add(new DistributionPoint(currentX, pdf, cdf));
        }

        String report = String.format("### Normal Distribution Evaluation N(\\mu=%.4f, \\sigma^2=%.4f)\n\n", mean, variance) +
                String.format("* Evaluated Point ($x$): `%.4f` (Standardized Z-Score: `%.4f`)\n", x, zScore) +
                String.format("* Probability Density $f(x)$: `%.6f`\n", pdfAtX) +
                String.format("* Cumulative Area $P(X \\le x)$: `%.6f` (%.2f%% percentile)\n", cdfAtX, cdfAtX * 100.0) +
                String.format("* Right-Tail Area $P(X > x)$: `%.6f`\n", survivalAtX);

        return new DistributionEvaluationResult("Normal", mean, variance, stdDev, pdfAtX, cdfAtX, survivalAtX, points, report);
    }

    @Override
    public DistributionResult evaluateNormal(double mean, double sd, double lower, double upper, double evalX) {
        DistributionEvaluationResult evaluation = evaluateNormal(mean, sd, evalX);
        double lowerCdf = DistributionsCalculator.calculateStandardNormalCDF((lower - mean) / sd);
        double upperCdf = DistributionsCalculator.calculateStandardNormalCDF((upper - mean) / sd);
        return new DistributionResult("Normal", evaluation.evaluatedPdfOrPmf(), upperCdf - lowerCdf,
                null, "f(x) and P(" + lower + " <= X <= " + upper + ")", evaluation.stepByStepReport());
    }

    @Override
    public DistributionResult evaluatePoisson(double lambda, double lower, double upper, int evalK) {
        double pmf = DistributionsCalculator.calculatePoissonPMF(evalK, lambda);
        double cdf = DistributionsCalculator.calculatePoissonCDF((int) Math.floor(upper), lambda)
                - DistributionsCalculator.calculatePoissonCDF((int) Math.floor(lower) - 1, lambda);
        return new DistributionResult("Poisson", pmf, cdf, null,
                "P(X = " + evalK + ")", "Poisson PMF and interval probability");
    }

    @Override
    public DistributionResult evaluateUniform(double min, double max, double lower, double upper, double evalX) {
        if (max <= min) throw new IllegalArgumentException("Uniform maximum must exceed minimum.");
        double density = evalX >= min && evalX <= max ? 1.0 / (max - min) : 0.0;
        double probability = Math.max(0.0, Math.min(max, upper) - Math.max(min, lower)) / (max - min);
        return new DistributionResult("Uniform", density, probability, null,
                "f(x) = 1/(b-a)", "Uniform density and interval probability");
    }

    @Override
    public DistributionResult evaluateExponential(double lambda, double lower, double upper, double evalX) {
        if (lambda <= 0.0) throw new IllegalArgumentException("Rate parameter lambda must be positive.");
        double pdf = DistributionsCalculator.calculateExponentialPDF(evalX, lambda);
        double probability = DistributionsCalculator.calculateExponentialCDF(upper, lambda)
                - DistributionsCalculator.calculateExponentialCDF(lower, lambda);
        return new DistributionResult("Exponential", pdf, probability, null,
                "f(x) = lambda * exp(-lambda*x)", "Exponential density and interval probability");
    }
}