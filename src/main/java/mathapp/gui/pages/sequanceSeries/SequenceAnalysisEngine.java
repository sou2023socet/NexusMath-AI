package mathapp.gui.pages.sequanceSeries;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class SequenceAnalysisEngine {

    enum PatternType {
        CONSTANT,
        ARITHMETIC,
        GEOMETRIC,
        FIBONACCI_LIKE,
        FINITE_DIFFERENCE
    }

    private SequenceAnalysisEngine() {
    }

    public static List<List<Double>> differenceTable(List<Double> terms) {
        validateTerms(terms);

        List<List<Double>> table = new ArrayList<>();
        List<Double> current = new ArrayList<>(terms);
        table.add(List.copyOf(current));

        while (current.size() > 1) {
            List<Double> differences = new ArrayList<>(current.size() - 1);
            for (int index = 0; index < current.size() - 1; index++) {
                differences.add(current.get(index + 1) - current.get(index));
            }
            table.add(List.copyOf(differences));
            current = differences;
        }

        return List.copyOf(table);
    }

    public static String formatNthTermFormula(List<Double> terms) {
        List<List<Double>> table = differenceTable(terms);
        PatternType pattern = detectPattern(terms);

        return switch (pattern) {
            case CONSTANT -> "a_n = " + formatNumber(terms.get(0));
            case ARITHMETIC -> "a_n = " + formatNumber(terms.get(0)) + " + (n - 1) * " +
                formatNumber(terms.get(1) - terms.get(0));
            case GEOMETRIC -> "a_n = " + formatNumber(terms.get(0)) + " * " +
                formatNumber(terms.get(1) / terms.get(0)) + "^(n - 1)";
            case FIBONACCI_LIKE -> "a_n = a_(n - 1) + a_(n - 2), a_1 = " +
                formatNumber(terms.get(0)) + ", a_2 = " + formatNumber(terms.get(1));
            case FINITE_DIFFERENCE -> formatFiniteDifferenceFormula(table);
        };
        }

        private static String formatFiniteDifferenceFormula(List<List<Double>> table) {
        StringBuilder formula = new StringBuilder("a_n = ");

        for (int order = 0; order < table.size(); order++) {
            double coefficient = table.get(order).get(0);
            if (coefficient == 0.0) continue;

            appendSignedTerm(formula, coefficient, order, "n - 1");
        }

        return formula.length() == "a_n = ".length() ? formula.append('0').toString() : formula.toString();
    }

    public static double termAt(List<Double> terms, int n) {
        if (n < 1) {
            throw new IllegalArgumentException("Term number n must be at least 1.");
        }

        List<List<Double>> table = differenceTable(terms);
        switch (detectPattern(terms)) {
            case CONSTANT:
                return terms.get(0);
            case ARITHMETIC:
                return terms.get(0) + (n - 1.0) * (terms.get(1) - terms.get(0));
            case GEOMETRIC:
                return terms.get(0) * Math.pow(terms.get(1) / terms.get(0), n - 1);
            case FIBONACCI_LIKE:
                if (n == 1) return terms.get(0);
                if (n == 2) return terms.get(1);
                double previous = terms.get(0);
                double current = terms.get(1);
                for (int index = 3; index <= n; index++) {
                    double next = previous + current;
                    previous = current;
                    current = next;
                }
                return current;
            case FINITE_DIFFERENCE:
                break;
        }

        double result = 0.0;
        for (int order = 0; order < table.size(); order++) {
            result += table.get(order).get(0) * binomialCoefficient(n - 1, order);
        }
        return result;
    }

    static PatternType detectPattern(List<Double> terms) {
        validateTerms(terms);
        if (terms.size() == 1) return PatternType.CONSTANT;

        double firstDifference = terms.get(1) - terms.get(0);
        boolean arithmetic = true;
        for (int index = 1; index < terms.size() - 1; index++) {
            if (!approximatelyEqual(terms.get(index + 1) - terms.get(index), firstDifference)) {
                arithmetic = false;
                break;
            }
        }
        if (arithmetic) return PatternType.ARITHMETIC;
        if (isGeometric(terms)) return PatternType.GEOMETRIC;
        if (isFibonacciLike(terms)) return PatternType.FIBONACCI_LIKE;
        return PatternType.FINITE_DIFFERENCE;
    }

    public static String describePattern(List<Double> terms) {
        return switch (detectPattern(terms)) {
            case CONSTANT -> "Constant sequence";
            case ARITHMETIC -> "Arithmetic sequence";
            case GEOMETRIC -> "Geometric sequence";
            case FIBONACCI_LIKE -> "Fibonacci-like recurrence";
            case FINITE_DIFFERENCE -> "Polynomial finite-difference pattern";
        };
    }

    private static boolean isGeometric(List<Double> terms) {
        if (terms.get(0) == 0.0) return false;
        double ratio = terms.get(1) / terms.get(0);
        if (!Double.isFinite(ratio)) return false;

        for (int index = 1; index < terms.size() - 1; index++) {
            double current = terms.get(index);
            double next = terms.get(index + 1);
            if (current == 0.0) {
                if (next != 0.0) return false;
            } else if (!approximatelyEqual(next / current, ratio)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isFibonacciLike(List<Double> terms) {
        if (terms.size() < 3) return false;
        for (int index = 2; index < terms.size(); index++) {
            if (!approximatelyEqual(terms.get(index), terms.get(index - 1) + terms.get(index - 2))) {
                return false;
            }
        }
        return true;
    }

    private static boolean approximatelyEqual(double left, double right) {
        return Math.abs(left - right) <= 1e-9 * Math.max(1.0, Math.max(Math.abs(left), Math.abs(right)));
    }

    static void validateTerms(List<Double> terms) {
        if (terms == null || terms.isEmpty()) {
            throw new IllegalArgumentException("Enter at least one sequence term.");
        }
        for (Double term : terms) {
            if (term == null || !Double.isFinite(term)) {
                throw new IllegalArgumentException("Sequence terms must be finite numbers.");
            }
        }
    }

    static double binomialCoefficient(int n, int k) {
        if (k < 0 || k > n) return 0.0;
        int smallerK = Math.min(k, n - k);
        double result = 1.0;
        for (int index = 1; index <= smallerK; index++) {
            result = result * (n - smallerK + index) / index;
        }
        return result;
    }

    static void appendSignedTerm(StringBuilder expression, double coefficient, int order, String binomialArgument) {
        if (expression.length() > "a_n = ".length()) {
            expression.append(coefficient < 0 ? " - " : " + ");
        } else if (coefficient < 0) {
            expression.append('-');
        }

        expression.append(formatNumber(Math.abs(coefficient)));
        if (order > 0) {
            expression.append(" * C(").append(binomialArgument).append(", ").append(order).append(')');
        }
    }

    static String formatNumber(double value) {
        return BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
    }
}