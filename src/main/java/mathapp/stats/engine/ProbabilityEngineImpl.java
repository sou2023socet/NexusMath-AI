package mathapp.stats.engine;

import mathapp.stats.engine.ProbabilityAndSetCalculator.*;
import java.util.List;

/**
 * Concrete probability engine managing set theory operations, contingency metrics, and Bayesian updates.
 */
public class ProbabilityEngineImpl implements IProbabilityEngine {

    @Override
    public Venn2SetResult calculateVenn2Sets(double pA, double pB, double pIntersect) {
        return ProbabilityAndSetCalculator.solveVenn2Set(pA, pB, pIntersect, 1.0);
    }

    @Override
    public Venn3SetResult calculateVenn3Sets(
            double pA, double pB, double pC,
            double pAandB, double pBandC, double pAandC,
            double pAllThree
    ) {
        return ProbabilityAndSetCalculator.solveVenn3Set(pA, pB, pC, pAandB, pBandC, pAandC, pAllThree);
    }

    @Override
    public ContingencyResult evaluateContingencyTable(double[][] counts) {
        return ProbabilityAndSetCalculator.analyzeContingencyTable(counts);
    }

    @Override
    public BayesResult updateBayesianPosterior(List<String> hypothesisNames, List<Double> priors, List<Double> likelihoods) {
        return ProbabilityAndSetCalculator.computeBayesTheorem(hypothesisNames, priors, likelihoods);
    }
}