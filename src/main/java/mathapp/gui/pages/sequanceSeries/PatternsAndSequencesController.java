package mathapp.gui.pages.sequanceSeries;

import java.util.*;

public class PatternsAndSequencesController {

    public enum Domain2Property {
        ANALYZE_SEQUENCE_PATTERN,
        PREDICT_NEXT_TERMS,
        GENERATE_DIFFERENCE_TABLE,
        POLYGONAL_NUMBERS,
        FIBONACCI_SEQUENCE,
        CATALAN_NUMBERS,
        PASCALS_TRIANGLE,
        CHECK_MONOTONIC_AND_BOUNDS,
        FREQUENCY_TABLE,
        STEM_AND_LEAF_PLOT
    }

    public enum InputType {
        SINGLE_DOUBLE,
        DOUBLE_LIST,
        INTEGER_LIST
    }

    public String evaluateProperty(Domain2Property property, Object input) {
        try {
            switch (property) {
                case ANALYZE_SEQUENCE_PATTERN: {
                    List<Double> list = handleInput(input, InputType.DOUBLE_LIST);
                    SequenceSolver solver = PatternsAndSequences.analyzeSeries(list);
                    return "Pattern Type: " + solver.getType() + "\nFormula: " + solver.getGeneralTermFormula();
                }

                case PREDICT_NEXT_TERMS: {
                    List<Double> list = handleInput(input, InputType.DOUBLE_LIST);
                    List<Double> predicted = PatternsAndSequences.predictNextTerms(list, 3);
                    return "Next 3 Terms: " + predicted;
                }

                case GENERATE_DIFFERENCE_TABLE: {
                    List<Double> list = handleInput(input, InputType.DOUBLE_LIST);
                    SequenceSolver solver = PatternsAndSequences.analyzeSeries(list);
                    return solver.formatDifferenceTableText();
                }

                case POLYGONAL_NUMBERS: {
                    List<Integer> list = handleInput(input, InputType.INTEGER_LIST);
                    if (list.size() < 2) {
                        throw new IllegalArgumentException("Provide sides and count (e.g., 5, 10 for 10 Pentagonal numbers).");
                    }
                    List<Long> result = PatternsAndSequences.getPolygonalNumbers(list.get(0), list.get(1));
                    return list.get(0) + "-gonal Numbers: " + result;
                }

                case FIBONACCI_SEQUENCE: {
                    Double count = handleInput(input, InputType.SINGLE_DOUBLE);
                    return "Fibonacci: " + PatternsAndSequences.getFibonacciSequence(count.intValue());
                }

                case CATALAN_NUMBERS: {
                    Double count = handleInput(input, InputType.SINGLE_DOUBLE);
                    return "Catalan Numbers: " + PatternsAndSequences.getCatalanNumbers(count.intValue());
                }

                case PASCALS_TRIANGLE: {
                    Double rows = handleInput(input, InputType.SINGLE_DOUBLE);
                    return "Pascal's Triangle (" + rows.intValue() + " rows):\n" +
                            PatternsAndSequences.getPascalsTriangle(rows.intValue());
                }

                case CHECK_MONOTONIC_AND_BOUNDS: {
                    List<Double> list = handleInput(input, InputType.DOUBLE_LIST);
                    return PatternsAndSequences.evaluateMonotonicityAndBounds(list);
                }

                case FREQUENCY_TABLE: {
                    List<Double> list = handleInput(input, InputType.DOUBLE_LIST);
                    Map<Double, Integer> map = PatternsAndSequences.generateFrequencyTable(list);
                    StringBuilder sb = new StringBuilder("Frequency Table:\nValue | Frequency\n");
                    map.forEach((k, v) -> sb.append(k).append(" | ").append(v).append("\n"));
                    return sb.toString();
                }

                case STEM_AND_LEAF_PLOT: {
                    List<Integer> list = handleInput(input, InputType.INTEGER_LIST);
                    return PatternsAndSequences.generateStemAndLeafPlot(list);
                }

                default:
                    throw new UnsupportedOperationException("Property not implemented: " + property);
            }
        } catch (Exception e) {
            return "Error evaluating request: " + e.getMessage();
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T handleInput(Object input, InputType expectedType) {
        if (input == null) throw new IllegalArgumentException("Input cannot be null.");
        
        switch (expectedType) {
            case SINGLE_DOUBLE:
                if (input instanceof Number) return (T) Double.valueOf(((Number) input).doubleValue());
                return (T) Double.valueOf(input.toString().trim());

            case DOUBLE_LIST:
                if (input instanceof List) return (T) input;
                String[] parts = input.toString().split(",");
                List<Double> doubles = new ArrayList<>();
                for (String p : parts) doubles.add(Double.parseDouble(p.trim()));
                return (T) doubles;

            case INTEGER_LIST:
                if (input instanceof List) return (T) input;
                String[] iParts = input.toString().split(",");
                List<Integer> ints = new ArrayList<>();
                for (String p : iParts) ints.add(Integer.parseInt(p.trim()));
                return (T) ints;

            default:
                throw new IllegalArgumentException("Unsupported Input Type");
        }
    }
}