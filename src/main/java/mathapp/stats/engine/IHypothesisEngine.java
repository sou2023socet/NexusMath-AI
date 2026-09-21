package mathapp.stats.engine;

import mathapp.stats.engine.HypothesisEngineImpl.HypothesisTestReport;
import java.util.List;

/**
 * Service contract for parametric and non-parametric hypothesis testing execution.
 */
public interface IHypothesisEngine {

    /** Runs a one-sample Student's t-test against a hypothesized population mean. */
    HypothesisTestReport runOneSampleTTest(double[] sample, double nullMean, double alpha);

    /** Runs Welch's two-sample t-test assuming unequal variances. */
    HypothesisTestReport runWelchTTest(double[] sample1, double[] sample2, double alpha);

    /** Runs a One-Way Analysis of Variance (ANOVA) F-test across multiple group samples. */
    HypothesisTestReport runOneWayANOVA(List<double[]> groups, double alpha);
}