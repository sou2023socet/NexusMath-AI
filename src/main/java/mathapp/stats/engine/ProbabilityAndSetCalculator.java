package mathapp.stats.engine;

import java.util.*;

/**
 * Pure mathematical engine for Set Theory, Contingency Tables, and Bayesian Probability.
 */
public class ProbabilityAndSetCalculator {

    // =========================================================================
    // 1. SET THEORY & VENN DIAGRAM SOLVERS
    // =========================================================================

    /**
     * Solves 2-Set Venn Diagram Region Counts / Probabilities.
     * Union Rule: P(A ∪ B) = P(A) + P(B) - P(A ∩ B)
     */
    public record Venn2SetResult(
            double pA, double pB, double pIntersect, double pUnion,
            double pAOnly, double pBOnly, double pNeither
    ) {}

    public static Venn2SetResult solveVenn2Set(double pA, double pB, double pIntersect, double sampleSpaceSize) {
        if (pIntersect > pA || pIntersect > pB) {
            throw new IllegalArgumentException("Intersection P(A ∩ B) cannot exceed individual probabilities P(A) or P(B).");
        }
        double pUnion = pA + pB - pIntersect;
        if (pUnion > 1.0) {
            throw new IllegalArgumentException("Calculated union P(A ∪ B) exceeds 1.0. Check input probabilities.");
        }
        double pAOnly = pA - pIntersect;
        double pBOnly = pB - pIntersect;
        double pNeither = 1.0 - pUnion;

        return new Venn2SetResult(pA, pB, pIntersect, pUnion, pAOnly, pBOnly, pNeither);
    }

    /**
     * Solves 3-Set Venn Diagram Region Counts (Inclusion-Exclusion Principle).
     * P(A ∪ B ∪ C) = P(A) + P(B) + P(C) - P(A∩B) - P(B∩C) - P(A∩C) + P(A∩B∩C)
     */
    public record Venn3SetResult(
            double onlyA, double onlyB, double onlyC,
            double onlyAandB, double onlyBandC, double onlyAandC,
            double allThree, double unionAll, double none
    ) {}

    public static Venn3SetResult solveVenn3Set(
            double pA, double pB, double pC,
            double pAandB, double pBandC, double pAandC,
            double pAllThree
    ) {
        double pUnion = pA + pB + pC - pAandB - pBandC - pAandC + pAllThree;
        if (pUnion > 1.0 + 1e-9) {
            throw new IllegalArgumentException("Probability of Union exceeds 1.0. Check set intersection inputs.");
        }

        double onlyAandB = pAandB - pAllThree;
        double onlyBandC = pBandC - pAllThree;
        double onlyAandC = pAandC - pAllThree;

        double onlyA = pA - (onlyAandB + onlyAandC + pAllThree);
        double onlyB = pB - (onlyAandB + onlyBandC + pAllThree);
        double onlyC = pC - (onlyAandC + onlyBandC + pAllThree);

        if (onlyA < -1e-9 || onlyB < -1e-9 || onlyC < -1e-9) {
            throw new IllegalArgumentException("Invalid set inputs: negative individual region probabilities computed.");
        }

        double none = Math.max(0.0, 1.0 - pUnion);

        return new Venn3SetResult(
                Math.max(0.0, onlyA), Math.max(0.0, onlyB), Math.max(0.0, onlyC),
                Math.max(0.0, onlyAandB), Math.max(0.0, onlyBandC), Math.max(0.0, onlyAandC),
                pAllThree, Math.min(1.0, pUnion), none
        );
    }

    // =========================================================================
    // 2. CONTINGENCY TABLE ANALYSIS (M x N)
    // =========================================================================

    public record ContingencyResult(
            double[][] jointProbabilities,
            double[] rowMarginals,
            double[] colMarginals,
            double grandTotal,
            double chiSquare,
            int degreesOfFreedom,
            double oddsRatio,       // Applicable for 2x2
            double relativeRisk,     // Applicable for 2x2
            String stepByStepReport
    ) {}

    public static ContingencyResult analyzeContingencyTable(double[][] counts) {
        int rows = counts.length;
        int cols = counts[0].length;

        double grandTotal = 0.0;
        double[] rowTotals = new double[rows];
        double[] colTotals = new double[cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grandTotal += counts[r][c];
                rowTotals[r] += counts[r][c];
                colTotals[c] += counts[r][c];
            }
        }

        if (grandTotal <= 0) {
            throw new IllegalArgumentException("Contingency table sum must be greater than zero.");
        }

        double[][] jointProbs = new double[rows][cols];
        double[] rowMarginals = new double[rows];
        double[] colMarginals = new double[cols];

        for (int r = 0; r < rows; r++) {
            rowMarginals[r] = rowTotals[r] / grandTotal;
            for (int c = 0; c < cols; c++) {
                jointProbs[r][c] = counts[r][c] / grandTotal;
            }
        }
        for (int c = 0; c < cols; c++) {
            colMarginals[c] = colTotals[c] / grandTotal;
        }

        // Chi-Square Independence Test: \chi^2 = \sum \frac{(O - E)^2}{E}
        double chiSquare = 0.0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                double expected = (rowTotals[r] * colTotals[c]) / grandTotal;
                if (expected > 0) {
                    double diff = counts[r][c] - expected;
                    chiSquare += (diff * diff) / expected;
                }
            }
        }
        int df = (rows - 1) * (cols - 1);

        double oddsRatio = Double.NaN;
        double relativeRisk = Double.NaN;

        // Specific 2x2 epidemiology / association metrics
        if (rows == 2 && cols == 2) {
            double a = counts[0][0];
            double b = counts[0][1];
            double c = counts[1][0];
            double d = counts[1][1];

            if (b * c != 0) oddsRatio = (a * d) / (b * c);
            double riskGroup1 = (a + b == 0) ? 0 : a / (a + b);
            double riskGroup2 = (c + d == 0) ? 0 : c / (c + d);
            if (riskGroup2 != 0) relativeRisk = riskGroup1 / riskGroup2;
        }

        String report = buildContingencyReport(rows, cols, grandTotal, chiSquare, df, oddsRatio, relativeRisk);

        return new ContingencyResult(
                jointProbs, rowMarginals, colMarginals, grandTotal, chiSquare, df, oddsRatio, relativeRisk, report
        );
    }

    private static String buildContingencyReport(
            int rows, int cols, double total, double chiSq, int df, double oddsRatio, double relativeRisk
    ) {
        StringBuilder sb = new StringBuilder();
        sb.append("### Contingency Table & Association Analysis Report\n\n");
        sb.append(String.format("* Grand Total Sample Size ($N$): `%.0f`\n", total));
        sb.append(String.format("* Matrix Dimensions: %d x %d\n", rows, cols));
        sb.append(String.format("* Chi-Square Test Statistic ($\\chi^2$): `%.4f` (Degrees of Freedom = %d)\n", chiSq, df));

        if (!Double.isNaN(oddsRatio)) {
            sb.append(String.format("* Odds Ratio (OR): `%.4f`\n", oddsRatio));
            sb.append(String.format("* Relative Risk (RR): `%.4f`\n", relativeRisk));
        }

        return sb.toString();
    }

    // =========================================================================
    // 3. BAYES' THEOREM ENGINE (MULTI-HYPOTHESIS)
    // =========================================================================

    public record BayesianHypothesis(
            String name,
            double priorProbability,
            double likelihood,
            double jointProbability,
            double posteriorProbability
    ) {}

    public record BayesResult(
            List<BayesianHypothesis> hypotheses,
            double totalEvidenceProbability, // P(B)
            String stepByStepReport
    ) {}

    /**
     * Computes Bayes' Theorem for K mutually exclusive and exhaustive hypotheses:
     * P(H_k | E) = \frac{P(E | H_k) P(H_k)}{\sum_{i=1}^K P(E | H_i) P(H_i)}
     */
    public static BayesResult computeBayesTheorem(List<String> names, List<Double> priors, List<Double> likelihoods) {
        if (names == null || priors == null || likelihoods == null ||
                names.size() != priors.size() || priors.size() != likelihoods.size()) {
            throw new IllegalArgumentException("Hypothesis arrays must be non-null and equal length.");
        }

        int k = names.size();
        double sumPriors = priors.stream().mapToDouble(Double::doubleValue).sum();
        if (Math.abs(sumPriors - 1.0) > 1e-4) {
            throw new IllegalArgumentException("Sum of prior probabilities must equal 1.0. Current sum: " + sumPriors);
        }

        double totalEvidence = 0.0;
        double[] joints = new double[k];

        for (int i = 0; i < k; i++) {
            joints[i] = priors.get(i) * likelihoods.get(i);
            totalEvidence += joints[i];
        }

        if (totalEvidence == 0.0) {
            throw new ArithmeticException("Total probability of evidence P(E) is zero. Cannot update posteriors.");
        }

        List<BayesianHypothesis> results = new ArrayList<>();
        StringBuilder report = new StringBuilder();
        report.append("### Step-by-Step Bayes' Theorem Update Report\n\n");
        report.append("$$\nP(H_k \\vert{} E) = \\frac{P(E \\vert{} H_k) \\cdot P(H_k)}{\\sum_{i=1}^K P(E \\vert{} H_i) \\cdot P(H_i)}\n$$\n\n");
        report.append(String.format("**Total Marginal Likelihood of Evidence $P(E)$:** `%.6f`\n\n", totalEvidence));
        report.append("| Hypothesis ($H_k$) | Prior $P(H_k)$ | Likelihood $P(E\\vert{}H_k)$ | Joint $P(E \\cap H_k)$ | Posterior $P(H_k\\vert{}E)$ |\n");
        report.append("| :--- | :--- | :--- | :--- | :--- |\n");

        for (int i = 0; i < k; i++) {
            double posterior = joints[i] / totalEvidence;
            results.add(new BayesianHypothesis(names.get(i), priors.get(i), likelihoods.get(i), joints[i], posterior));

            report.append(String.format("| **%s** | `%.4f` | `%.4f` | `%.6f` | **`%.6f`** (%.2f%%) |\n",
                    names.get(i), priors.get(i), likelihoods.get(i), joints[i], posterior, posterior * 100.0));
        }

        return new BayesResult(results, totalEvidence, report.toString());
    }
}