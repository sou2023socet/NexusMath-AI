package mathapp.gui.pages.sequanceSeries;

import java.util.*;
import mathapp.gui.pages.sequanceSeries.SequenceSolver.SequenceType;

public class PatternsAndSequences {

    // Helper check
    private static boolean isInteger(double num) {
        return Math.floor(num) == num && !Double.isInfinite(num);
    }

    // ==========================================
    // 2.1 PATTERNS & GENERALISATIONS
    // ==========================================

    public static SequenceSolver analyzeSeries(List<Double> series) {
        return new SequenceSolver(series);
    }

    public static List<Double> predictNextTerms(List<Double> series, int count) {
        SequenceSolver solver = new SequenceSolver(series);
        List<Double> predicted = new ArrayList<>(series);

        for (int k = 0; k < count; k++) {
            int n = predicted.size() + 1;
            if (solver.getType() == SequenceType.ARITHMETIC) {
                predicted.add(predicted.get(0) + (n - 1) * (predicted.get(1) - predicted.get(0)));
            } else if (solver.getType() == SequenceType.GEOMETRIC) {
                predicted.add(predicted.get(0) * Math.pow(predicted.get(1) / predicted.get(0), n - 1));
            } else if (solver.getType() == SequenceType.FIBONACCI_LIKE) {
                int sz = predicted.size();
                predicted.add(predicted.get(sz - 1) + predicted.get(sz - 2));
            } else {
                // Fallback via difference table prediction
                List<List<Double>> diffs = solver.getDifferenceTable();
                double nextVal = predicted.get(predicted.size() - 1) + diffs.get(1).get(diffs.get(1).size() - 1);
                predicted.add(nextVal);
            }
        }
        return predicted.subList(series.size(), predicted.size());
    }

    // ==========================================
    // 2.2 SPECIAL SEQUENCES GENERATORS
    // ==========================================

    public static List<Long> getPolygonalNumbers(int sides, int count) {
        List<Long> result = new ArrayList<>();
        if (sides < 3 || count <= 0) return result;
        for (int n = 1; n <= count; n++) {
            long val = ((long) (sides - 2) * n * n - (long) (sides - 4) * n) / 2;
            result.add(val);
        }
        return result;
    }

    public static List<Long> getFibonacciSequence(int count) {
        List<Long> list = new ArrayList<>();
        if (count <= 0) return list;
        long a = 0, b = 1;
        for (int i = 0; i < count; i++) {
            list.add(a);
            long next = a + b;
            a = b;
            b = next;
        }
        return list;
    }

    public static List<Long> getCatalanNumbers(int count) {
        List<Long> catalan = new ArrayList<>();
        for (int n = 0; n < count; n++) {
            catalan.add(nCr(2 * n, n) / (n + 1));
        }
        return catalan;
    }

    public static String getPascalsTriangle(int rows) {
        StringBuilder sb = new StringBuilder();
        for (int line = 0; line < rows; line++) {
            for (int j = 0; j <= line; j++) {
                sb.append(nCr(line, j)).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private static long nCr(int n, int r) {
        if (r > n) return 0;
        if (r == 0 || r == n) return 1;
        long res = 1;
        for (int i = 1; i <= r; i++) {
            res = res * (n - i + 1) / i;
        }
        return res;
    }

    // ==========================================
    // 2.3 SEQUENCE BOUNDS & CONVERGENCE
    // ==========================================

    public static String evaluateMonotonicityAndBounds(List<Double> series) {
        if (series.size() < 2) return "Insufficient terms to determine monotonicity.";

        boolean increasing = true;
        boolean decreasing = true;

        for (int i = 0; i < series.size() - 1; i++) {
            if (series.get(i + 1) < series.get(i)) increasing = false;
            if (series.get(i + 1) > series.get(i)) decreasing = false;
        }

        double min = Collections.min(series);
        double max = Collections.max(series);

        String monotonicity = increasing ? "Monotonically Increasing" :
                              decreasing ? "Monotonically Decreasing" : "Non-Monotonic";

        return String.format("Status: %s\nBounded Range in sample: [%.2f, %.2f]", monotonicity, min, max);
    }

    // ==========================================
    // 2.4 DATA HANDLING & FREQUENCY TABLES
    // ==========================================

    public static Map<Double, Integer> generateFrequencyTable(List<Double> data) {
        Map<Double, Integer> freqMap = new TreeMap<>();
        for (double val : data) {
            freqMap.put(val, freqMap.getOrDefault(val, 0) + 1);
        }
        return freqMap;
    }

    public static String generateStemAndLeafPlot(List<Integer> data) {
        Collections.sort(data);
        Map<Integer, List<Integer>> stems = new TreeMap<>();

        for (int num : data) {
            int stem = num / 10;
            int leaf = Math.abs(num % 10);
            stems.computeIfAbsent(stem, k -> new ArrayList<>()).add(leaf);
        }

        StringBuilder sb = new StringBuilder("🌿 Stem-and-Leaf Plot:\n");
        for (var entry : stems.entrySet()) {
            sb.append(String.format("%3d | ", entry.getKey()));
            for (int leaf : entry.getValue()) {
                sb.append(leaf).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}