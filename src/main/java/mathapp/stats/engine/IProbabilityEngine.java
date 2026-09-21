package mathapp.stats.engine;

import mathapp.stats.engine.ProbabilityAndSetCalculator.*;
import java.util.List;

/**
 * Service contract for set operations, contingency analysis, and Bayesian updating.
 */
public interface IProbabilityEngine {

    /** Solves 2-set Venn diagram region probabilities. */
    Venn2SetResult calculateVenn2Sets(double pA, double pB, double pIntersect);

    /** Solves 3-set Venn diagram region probabilities using the Inclusion-Exclusion principle. */
    Venn3SetResult calculateVenn3Sets(
            double pA, double pB, double pC,
            double pAandB, double pBandC, double pAandC,
            double pAllThree
    );

    /** Performs cross-tabulation metrics and Chi-Square independence test on an M x N contingency table. */
    ContingencyResult evaluateContingencyTable(double[][] counts);

    /** Updates posterior probabilities across K mutually exclusive hypotheses using Bayes' Theorem. */
    BayesResult updateBayesianPosterior(List<String> hypothesisNames, List<Double> priors, List<Double> likelihoods);
}