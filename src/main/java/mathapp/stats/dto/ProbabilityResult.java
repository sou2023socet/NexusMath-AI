package mathapp.stats.dto;

/**
 * Immutable DTO containing set operator outputs, conditional evaluations, and Bayes' updates.
 */
public record ProbabilityResult(
        double probA,
        double probB,
        double probC,
        double probAUnionB,
        double probAIntersectB,
        double probAGivenB,
        double probBGivenA,
        double probAComplement,
        double probBComplement,
        boolean isIndependent,
        boolean isMutuallyExclusive,
        double oddsInFavorA,
        double oddsAgainstA,
        double bayesPosterior,
        String stepByStepReport
) {}