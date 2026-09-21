package mathapp.gui.pages.patterns;

import mathapp.gui.pages.patterns.SequencePatternEngine;
import mathapp.gui.pages.patterns.SequencePatternResult;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

/**
 * Domain controller serving as the orchestration layer between the user interface
 * and the low-level sequence and pattern engines.
 */
public class PatternsAndSequencesController {

    public enum Domain2Property {
        ANALYZE_SEQUENCE_PATTERN("Analyze & Detect Pattern"),
        POLYGONAL_NUMBERS("Polygonal / Figurative Numbers"),
        FIBONACCI_SEQUENCE("Fibonacci Sequence Generator"),
        CATALAN_NUMBERS("Catalan Numbers Generator"),
        PASCALS_TRIANGLE("Pascal's Triangle Generator");

        private final String displayName;

        Domain2Property(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    /**
     * Main evaluation endpoint called by {@code SequenceSolverController}.
     *
     * @param property The selected sequence operation mode
     * @param input Raw text string from UI input field
     * @return Formatted string output for UI rendering
     */
    public String evaluateProperty(Domain2Property property, String input) {
        if (property == null) {
            throw new IllegalArgumentException("Property operation cannot be null.");
        }

        switch (property) {
            case ANALYZE_SEQUENCE_PATTERN:
                return handleSequenceAnalysis(input);

            case POLYGONAL_NUMBERS:
                return handlePolygonalNumbers(input);

            case FIBONACCI_SEQUENCE:
                return handleFibonacci(input);

            case CATALAN_NUMBERS:
                return handleCatalan(input);

            case PASCALS_TRIANGLE:
                return handlePascalsTriangle(input);

            default:
                throw new UnsupportedOperationException("Unsupported operation: " + property);
        }
    }

    private String handleSequenceAnalysis(String input) {
        double[] terms = parseCommaSeparatedDoubles(input);
        if (terms.length < 3) {
            return "❌ Sequence pattern detection requires at least 3 comma-separated terms.\nExample: 2, 5, 10, 17, 26";
        }

        SequencePatternResult result = SequencePatternEngine.analyzeSequence(terms, 5);
        return result.stepByStepReport();
    }

    private String handlePolygonalNumbers(String input) {
        int[] params = parseCommaSeparatedInts(input);
        int sides = (params.length > 0) ? params[0] : 5;
        int count = (params.length > 1) ? params[1] : 10;

        List<BigInteger> numbers = PolygonalAndSpecialSequenceEngine.generatePolygonalNumbers(sides, count);

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("### %d-Gonal (Polygonal) Numbers\n\n", sides));
        sb.append(String.format("* **Formula:** $P_{%d}(n) = \\frac{%dn^2 - %dn}{2}$\n", sides, sides - 2, sides - 4));
        sb.append(String.format("* **Generated Terms (%d):**\n\n", count));
        
        for (int i = 0; i < numbers.size(); i++) {
            sb.append(String.format("  P_{%d}(%d) = %s\n", sides, i + 1, numbers.get(i)));
        }
        return sb.toString();
    }

    private String handleFibonacci(String input) {
        int count = parseSingleInt(input, 10);
        List<BigInteger> fib = PolygonalAndSpecialSequenceEngine.generateFibonacciSequence(count);

        StringBuilder sb = new StringBuilder();
        sb.append("### Fibonacci Sequence Generator\n\n");
        sb.append("* **Recurrence:** $F_n = F_{n-1} + F_{n-2}, \\quad F_0 = 0, F_1 = 1$\n");
        sb.append(String.format("* **First %d Terms:**\n\n", count));
        sb.append("  ").append(fib.toString()).append("\n");
        return sb.toString();
    }

    private String handleCatalan(String input) {
        int count = parseSingleInt(input, 10);
        List<BigInteger> catalan = PolygonalAndSpecialSequenceEngine.generateCatalanNumbers(count);

        StringBuilder sb = new StringBuilder();
        sb.append("### Catalan Numbers Generator\n\n");
        sb.append("* **Formula:** $C_n = \\frac{1}{n+1}\\binom{2n}{n}$\n");
        sb.append(String.format("* **First %d Terms ($C_0$ to $C_{%d}$):**\n\n", count, count - 1));
        
        for (int i = 0; i < catalan.size(); i++) {
            sb.append(String.format("  C_{%d} = %s\n", i, catalan.get(i)));
        }
        return sb.toString();
    }

    private String handlePascalsTriangle(String input) {
        int rows = parseSingleInt(input, 8);
        List<List<BigInteger>> triangle = PolygonalAndSpecialSequenceEngine.generatePascalsTriangle(rows);

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("### Pascal's Triangle (%d Rows)\n\n", rows));
        for (int i = 0; i < triangle.size(); i++) {
            sb.append(String.format("Row %2d: %s\n", i, triangle.get(i).toString()));
        }
        return sb.toString();
    }

    // Input Parsing Helpers
    private double[] parseCommaSeparatedDoubles(String input) {
        return Arrays.stream(input.split("[,\\s]+"))
                .filter(s -> !s.isBlank())
                .mapToDouble(Double::parseDouble)
                .toArray();
    }

    private int[] parseCommaSeparatedInts(String input) {
        return Arrays.stream(input.split("[,\\s]+"))
                .filter(s -> !s.isBlank())
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    private int parseSingleInt(String input, int defaultValue) {
        try {
            return Integer.parseInt(input.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }
}