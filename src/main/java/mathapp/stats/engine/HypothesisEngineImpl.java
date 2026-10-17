package mathapp.stats.engine;

import mathapp.stats.engine.HypothesisTestingCalculator.*;
import java.util.List;

/**
 * Concrete engine running hypothesis tests and returning formatted reports with decision rules.
 */
public class HypothesisEngineImpl implements IHypothesisEngine {

    public record HypothesisTestReport(
            String testName,
            double testStatistic,
            double degreesOfFreedom,
            double pValue,
            double alpha,
            boolean rejectNull,
            String confidenceIntervalFormatted,
            String stepByStepReport
    ) {}

    @Override
    public HypothesisTestReport runOneSampleTTest(double[] sample, double nullMean, double alpha) {
        TTestResult res = HypothesisTestingCalculator.calculateOneSampleTTest(sample, nullMean, alpha);
        boolean reject = res.pValueTwoTailed() < alpha;

        StringBuilder sb = new StringBuilder();
        sb.append("### One-Sample $t$-Test Report\n\n");
        sb.append(String.format("* **Null Hypothesis ($H_0$):** $\\mu = \\%.4f$\n", nullMean));
        sb.append(String.format("* **Alternative Hypothesis ($H_a$):** $\\mu \\neq \\%.4f$\n", nullMean));
        sb.append(String.format("* **Significance Level ($\\alpha$):** `%.2f`\n", alpha));
        sb.append(String.format("* Sample Mean ($\\bar{x}$): `%.4f` | Standard Error: `%.4f`\n", res.mean(), res.stdError()));
        sb.append(String.format("* Test Statistic ($t$): `%.4f` (Degrees of Freedom = `%.1f`)\n", res.testStatistic(), res.degreesOfFreedom()));
        sb.append(String.format("* Two-Tailed $p$-value: `%.6f`\n", res.pValueTwoTailed()));
        sb.append(String.format("* `%.0f%%` Confidence Interval: `[%.4f, %.4f]`\n\n", (1.0 - alpha) * 100.0, res.ciLower(), res.ciUpper()));
        sb.append(String.format("**Statistical Decision:** %s $H_0$ at $\\alpha = \\%.2f$ level (%s).\n",
                reject ? "**Reject**" : "**Fail to Reject**", alpha,
                reject ? "$p < \\alpha$, significant evidence against $H_0$" : "$p \\ge \\alpha$, insufficient evidence against $H_0$"));

        String ciStr = String.format("[%.4f, %.4f]", res.ciLower(), res.ciUpper());

        return new HypothesisTestReport("One-Sample t-Test", res.testStatistic(), res.degreesOfFreedom(),
                res.pValueTwoTailed(), alpha, reject, ciStr, sb.toString());
    }

    @Override
    public HypothesisTestReport runWelchTTest(double[] sample1, double[] sample2, double alpha) {
        TTestResult res = HypothesisTestingCalculator.calculateWelchTTest(sample1, sample2, alpha);
        boolean reject = res.pValueTwoTailed() < alpha;

        StringBuilder sb = new StringBuilder();
        sb.append("### Two-Sample Welch's $t$-Test Report (Unequal Variances)\n\n");
        sb.append("* **Null Hypothesis ($H_0$):** $\\mu_1 - \\mu_2 = 0$\n");
        sb.append("* **Alternative Hypothesis ($H_a$):** $\\mu_1 - \\mu_2 \\neq 0$\n");
        sb.append(String.format("* Difference in Means ($\\bar{x}_1 - \\bar{x}_2$): `%.4f`\n", res.mean()));
        sb.append(String.format("* Welch's Test Statistic ($t$): `%.4f` (Welch-Satterthwaite df = `%.2f`)\n", res.testStatistic(), res.degreesOfFreedom()));
        sb.append(String.format("* Two-Tailed $p$-value: `%.6f`\n", res.pValueTwoTailed()));
        sb.append(String.format("* `%.0f%%` Confidence Interval for Difference: `[%.4f, %.4f]`\n\n", (1.0 - alpha) * 100.0, res.ciLower(), res.ciUpper()));
        sb.append(String.format("**Statistical Decision:** %s $H_0$ at $\\alpha = \\%.2f$.\n",
                reject ? "**Reject**" : "**Fail to Reject**", alpha));

        String ciStr = String.format("[%.4f, %.4f]", res.ciLower(), res.ciUpper());

        return new HypothesisTestReport("Welch's Two-Sample t-Test", res.testStatistic(), res.degreesOfFreedom(),
                res.pValueTwoTailed(), alpha, reject, ciStr, sb.toString());
    }

    @Override
    public HypothesisTestReport runOneWayANOVA(List<double[]> groups, double alpha) {
        AnovaResult res = HypothesisTestingCalculator.calculateOneWayANOVA(groups);
        boolean reject = res.pValue() < alpha;

        StringBuilder sb = new StringBuilder();
        sb.append("### One-Way Analysis of Variance (ANOVA) Report\n\n");
        sb.append("* **Null Hypothesis ($H_0$):** $\\mu_1 = \\mu_2 = \\dots = \\mu_k$\n");
        sb.append("* **Alternative Hypothesis ($H_a$):** At least one group mean is different\n\n");
        sb.append("| Source of Variation | Sum of Squares (SS) | Degrees of Freedom (df) | Mean Square (MS) | $F$-Statistic | $p$-value |\n");
        sb.append("| :--- | :--- | :--- | :--- | :--- | :--- |\n");
        sb.append(String.format("| **Between Groups** | `%.4f` | %d | `%.4f` | **`%.4f`** | **`%.6f`** |\n",
                res.ssBetween(), res.dfBetween(), res.msBetween(), res.fStatistic(), res.pValue()));
        sb.append(String.format("| **Within Groups (Error)** | `%.4f` | %d | `%.4f` | - | - |\n",
                res.ssWithin(), res.dfWithin(), res.msWithin()));
        sb.append(String.format("| **Total** | `%.4f` | %d | - | - | - |\n\n",
                res.ssTotal(), res.dfBetween() + res.dfWithin()));
        sb.append(String.format("**Statistical Decision:** %s $H_0$ at $\\alpha = \\%.2f$.\n",
                reject ? "**Reject**" : "**Fail to Reject**", alpha));

        return new HypothesisTestReport("One-Way ANOVA", res.fStatistic(), res.dfBetween(),
                res.pValue(), alpha, reject, "N/A", sb.toString());
    }
}