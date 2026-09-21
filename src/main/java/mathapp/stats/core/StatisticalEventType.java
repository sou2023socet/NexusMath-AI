package mathapp.stats.core;

/**
 * Defines all mutation events that occur within the Statistics & Probability domain.
 */
public enum StatisticalEventType {
    UNIVARIATE_DATA_CHANGED,
    BIVARIATE_DATA_CHANGED,
    REGRESSION_MODEL_CHANGED,
    LOGISTIC_THRESHOLD_CHANGED,
    PROBABILITY_SETS_CHANGED,
    DISTRIBUTION_PARAMS_CHANGED,
    DISTRIBUTION_BOUNDS_CHANGED,
    COMBINATORICS_PARAMS_CHANGED,
    STATE_RESET
}