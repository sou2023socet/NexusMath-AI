package mathapp.gui.pages.sequanceSeries;

import java.util.List;

public final class SeriesAnalysisEngine {

    private SeriesAnalysisEngine() {
    }

    public static String formatPartialSumFormula(List<Double> terms) {
        SequenceAnalysisEngine.PatternType pattern = SequenceAnalysisEngine.detectPattern(terms);
        double firstTerm = terms.get(0);
        return switch (pattern) {
            case CONSTANT -> "S_n = " + SequenceAnalysisEngine.formatNumber(firstTerm) + " * n";
            case ARITHMETIC -> "S_n = n / 2 * (2 * " + SequenceAnalysisEngine.formatNumber(firstTerm) +
                    " + (n - 1) * " + SequenceAnalysisEngine.formatNumber(terms.get(1) - firstTerm) + ")";
            case GEOMETRIC -> formatGeometricSumFormula(terms);
            case FIBONACCI_LIKE -> "S_n = a_(n + 2) - a_2";
            case FINITE_DIFFERENCE -> formatFiniteDifferenceSumFormula(terms);
        };
    }

    private static String formatGeometricSumFormula(List<Double> terms) {
        double firstTerm = terms.get(0);
        double commonRatio = terms.get(1) / firstTerm;
        if (commonRatio == 1.0) {
            return "S_n = " + SequenceAnalysisEngine.formatNumber(firstTerm) + " * n";
        }
        String ratio = SequenceAnalysisEngine.formatNumber(commonRatio);
        return "S_n = " + SequenceAnalysisEngine.formatNumber(firstTerm) +
                " * (1 - " + ratio + "^n) / (1 - " + ratio + ")";
    }

    private static String formatFiniteDifferenceSumFormula(List<Double> terms) {
        List<List<Double>> table = SequenceAnalysisEngine.differenceTable(terms);
        StringBuilder formula = new StringBuilder("S_n = ");

        for (int order = 0; order < table.size(); order++) {
            double coefficient = table.get(order).get(0);
            if (coefficient == 0.0) continue;

            if (formula.length() > "S_n = ".length()) {
                formula.append(coefficient < 0 ? " - " : " + ");
            } else if (coefficient < 0) {
                formula.append('-');
            }
            formula.append(SequenceAnalysisEngine.formatNumber(Math.abs(coefficient)))
                    .append(" * C(n, ").append(order + 1).append(')');
        }

        return formula.length() == "S_n = ".length() ? formula.append('0').toString() : formula.toString();
    }

    public static double sumThrough(List<Double> terms, int n) {
        if (n < 1) {
            throw new IllegalArgumentException("Sum limit n must be at least 1.");
        }

        return switch (SequenceAnalysisEngine.detectPattern(terms)) {
            case CONSTANT -> terms.get(0) * n;
            case ARITHMETIC -> n / 2.0 * (2.0 * terms.get(0) + (n - 1.0) * (terms.get(1) - terms.get(0)));
            case GEOMETRIC -> geometricSum(terms, n);
            case FIBONACCI_LIKE -> SequenceAnalysisEngine.termAt(terms, Math.addExact(n, 2)) - terms.get(1);
            case FINITE_DIFFERENCE -> finiteDifferenceSum(terms, n);
        };
    }

    private static double geometricSum(List<Double> terms, int n) {
        double firstTerm = terms.get(0);
        double commonRatio = terms.get(1) / firstTerm;
        if (commonRatio == 1.0) return firstTerm * n;
        return firstTerm * (1.0 - Math.pow(commonRatio, n)) / (1.0 - commonRatio);
    }

    private static double finiteDifferenceSum(List<Double> terms, int n) {
        List<List<Double>> table = SequenceAnalysisEngine.differenceTable(terms);
        double sum = 0.0;
        for (int order = 0; order < table.size(); order++) {
            sum += table.get(order).get(0) * SequenceAnalysisEngine.binomialCoefficient(n, order + 1);
        }
        return sum;
    }
}