package mathapp.stats.engine;

import mathapp.stats.dto.DistributionResult;
import mathapp.stats.engine.DistributionEngineImpl.DistributionEvaluationResult;

public interface IDistributionEngine {
    DistributionEvaluationResult evaluateBinomial(int n, double p, int k);
    DistributionResult evaluateNormal(double mean, double sd, double lower, double upper, double evalX);
    DistributionResult evaluateBinomial(int n, double p, double lower, double upper, int evalK);
    DistributionResult evaluatePoisson(double lambda, double lower, double upper, int evalK);
    DistributionResult evaluateUniform(double min, double max, double lower, double upper, double evalX);
    DistributionResult evaluateExponential(double lambda, double lower, double upper, double evalX);
    DistributionEvaluationResult evaluateNormal(double mean, double stdDev, double x);
}