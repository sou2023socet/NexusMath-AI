package mathapp.gui.pages.sequanceSeries;

import java.util.ArrayList;
import java.util.List;

public class SequenceSolver {

    public enum SequenceType {
        ARITHMETIC,
        GEOMETRIC,
        QUADRATIC,
        FIBONACCI_LIKE,
        CONSTANT,
        UNKNOWN
    }

    private final List<Double> terms;
    private SequenceType type = SequenceType.UNKNOWN;
    private Double commonDifference;
    private Double commonRatio;
    private String generalTermFormula;
    private List<List<Double>> differenceTable;

    public SequenceSolver(List<Double> terms) {
        this.terms = terms != null ? new ArrayList<>(terms) : new ArrayList<>();
        this.differenceTable = new ArrayList<>();
        analyzeSequence();
    }

    private void analyzeSequence() {
        if (terms.size() < 2) {
            this.type = SequenceType.UNKNOWN;
            return;
        }

        buildDifferenceTable();

        // 1. Check Arithmetic (First differences are equal)
        if (isConstantSeries(differenceTable.get(1))) {
            this.type = SequenceType.ARITHMETIC;
            this.commonDifference = differenceTable.get(1).get(0);
            double a1 = terms.get(0);
            this.generalTermFormula = String.format("a_n = %.2f + (n - 1) * %.2f", a1, commonDifference);
            return;
        }

        // 2. Check Geometric (Constant ratio between terms)
        boolean isGeometric = true;
        double r = terms.get(1) / terms.get(0);
        for (int i = 1; i < terms.size() - 1; i++) {
            if (terms.get(i) == 0 || Math.abs((terms.get(i + 1) / terms.get(i)) - r) > 1e-6) {
                isGeometric = false;
                break;
            }
        }
        if (isGeometric) {
            this.type = SequenceType.GEOMETRIC;
            this.commonRatio = r;
            this.generalTermFormula = String.format("a_n = %.2f * (%.2f)^(n - 1)", terms.get(0), commonRatio);
            return;
        }

        // 3. Check Quadratic (Second differences are equal)
        if (differenceTable.size() > 2 && isConstantSeries(differenceTable.get(2))) {
            this.type = SequenceType.QUADRATIC;
            double secondDiff = differenceTable.get(2).get(0);
            double a = secondDiff / 2.0;
            double b = differenceTable.get(1).get(0) - (3 * a);
            double c = terms.get(0) - a - b;
            this.generalTermFormula = String.format("a_n = %.2fn^2 + %.2fn + %.2f", a, b, c);
            return;
        }

        // 4. Check Fibonacci-like (a_n = a_{n-1} + a_{n-2})
        if (terms.size() >= 3) {
            boolean isFib = true;
            for (int i = 2; i < terms.size(); i++) {
                if (Math.abs(terms.get(i) - (terms.get(i - 1) + terms.get(i - 2))) > 1e-6) {
                    isFib = false;
                    break;
                }
            }
            if (isFib) {
                this.type = SequenceType.FIBONACCI_LIKE;
                this.generalTermFormula = "a_n = a_{n-1} + a_{n-2}";
                return;
            }
        }

        this.generalTermFormula = "No explicit elementary general term detected.";
    }

    private void buildDifferenceTable() {
        differenceTable.add(new ArrayList<>(terms));
        List<Double> current = new ArrayList<>(terms);

        while (current.size() > 1) {
            List<Double> nextDiff = new ArrayList<>();
            for (int i = 0; i < current.size() - 1; i++) {
                nextDiff.add(current.get(i + 1) - current.get(i));
            }
            differenceTable.add(nextDiff);
            current = nextDiff;
        }
    }

    private boolean isConstantSeries(List<Double> series) {
        if (series == null || series.isEmpty()) return false;
        double first = series.get(0);
        for (double val : series) {
            if (Math.abs(val - first) > 1e-6) return false;
        }
        return true;
    }

    // Getters and helper exporters
    public SequenceType getType() { return type; }
    public String getGeneralTermFormula() { return generalTermFormula; }
    public List<List<Double>> getDifferenceTable() { return differenceTable; }

    public String formatDifferenceTableText() {
        StringBuilder sb = new StringBuilder("📊 Difference Table:\n");
        for (int i = 0; i < differenceTable.size(); i++) {
            sb.append("Order ").append(i).append(": ").append(differenceTable.get(i)).append("\n");
        }
        return sb.toString();
    }
}