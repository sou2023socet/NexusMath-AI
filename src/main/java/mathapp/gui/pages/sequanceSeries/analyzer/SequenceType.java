package mathapp.gui.pages.sequanceSeries.analyzer;

/**
 * Every family of sequence the analyzer knows how to recognize.
 * DIFFERENCE_REDUCED is not a "root" family on its own — it means the engine
 * had to fall back to Section 6 of the design (recursively analyzing the
 * difference series) because none of the direct detectors matched the
 * original terms.
 */
public enum SequenceType {
    ARITHMETIC,
    GEOMETRIC,
    HARMONIC,
    ARITHMETICO_GEOMETRIC,
    POLYNOMIAL,
    LINEAR_RECURRENCE,      // homogeneous, order 2: a_n = c1*a_(n-1) + c2*a_(n-2)
    AFFINE_RECURRENCE,      // a_n = r*a_(n-1) + c
    DIFFERENCE_REDUCED,     // pattern only visible after differencing (recursive)
    UNCLASSIFIED
}
