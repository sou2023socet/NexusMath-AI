package mathapp.gui.pages.sequanceSeries;

import mathapp.gui.pages.sequanceSeries.analyzer.DetectionResult;
import mathapp.gui.pages.sequanceSeries.analyzer.SequenceAnalyzer;
import mathapp.gui.pages.sequanceSeries.analyzer.SequenceModel;
import mathapp.gui.pages.sequanceSeries.analyzer.SequenceType;
import java.util.*;

public class PatternsAndSequencesController {

    public enum Domain2Property {
        ANALYZE_SEQUENCE_PATTERN("Analyze Sequence Pattern"),
        PREDICT_NEXT_TERMS("Predict Next Terms"),
        GENERATE_DIFFERENCE_TABLE("Generate Difference Table"),
        FIND_NTH_TERM("Find nth Term"),
        SUM_SERIES_TO_N("Sum Series Through n"),
        POLYGONAL_NUMBERS("Polygonal Numbers"),
        FIBONACCI_SEQUENCE("Fibonacci Sequence"),
        CATALAN_NUMBERS("Catalan Numbers"),
        PASCALS_TRIANGLE("Pascal's Triangle"),
        CHECK_MONOTONIC_AND_BOUNDS("Check Monotonicity and Bounds"),
        FREQUENCY_TABLE("Frequency Table"),
        STEM_AND_LEAF_PLOT("Stem and Leaf Plot");

        private final String displayName;

        Domain2Property(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum InputType {
        SINGLE_DOUBLE,
        DOUBLE_LIST,
        INTEGER_LIST
    }

    private final SequenceAnalyzer sequenceAnalyzer = new SequenceAnalyzer();

    public String evaluateProperty(Domain2Property property, Object input) {
        return evaluateProperty(property, input, null);
    }

    public String evaluateProperty(Domain2Property property, Object input, Integer targetN) {
        try {
            switch (property) {
                case ANALYZE_SEQUENCE_PATTERN: {
                    List<Double> list = handleInput(input, InputType.DOUBLE_LIST);
                    return analyzeSequence(list);
                }

                case PREDICT_NEXT_TERMS: {
                    List<Double> list = handleInput(input, InputType.DOUBLE_LIST);
                    return predictNextTerms(list, 3);
                }

                case GENERATE_DIFFERENCE_TABLE: {
                    List<Double> list = handleInput(input, InputType.DOUBLE_LIST);
                    return formatDifferenceTable(SequenceAnalysisEngine.differenceTable(list));
                }

                case FIND_NTH_TERM: {
                    List<Double> list = handleInput(input, InputType.DOUBLE_LIST);
                    int n = requireTargetN(targetN, "Term number");
                    return "Nth-term formula: " + SequenceAnalysisEngine.formatNthTermFormula(list) +
                            "\na_" + n + " = " + SequenceAnalysisEngine.termAt(list, n);
                }

                case SUM_SERIES_TO_N: {
                    List<Double> list = handleInput(input, InputType.DOUBLE_LIST);
                    int n = requireTargetN(targetN, "Sum limit");
                    return "Partial-sum formula: " + SeriesAnalysisEngine.formatPartialSumFormula(list) +
                            "\nS_" + n + " = " + SeriesAnalysisEngine.sumThrough(list, n);
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

    private int requireTargetN(Integer targetN, String label) {
        if (targetN == null || targetN < 1) {
            throw new IllegalArgumentException(label + " n must be a positive integer.");
        }
        return targetN;
    }

    private String formatDifferenceTable(List<List<Double>> table) {
        StringBuilder result = new StringBuilder("Difference Table:\n");
        for (int order = 0; order < table.size(); order++) {
            result.append("Order ").append(order).append(": ").append(table.get(order)).append('\n');
        }
        return result.toString();
    }

    private String analyzeSequence(List<Double> terms) {
        DetectionResult detection = sequenceAnalyzer.analyze(terms.stream().mapToDouble(Double::doubleValue).toArray());
        SequenceModel model = detection.getModel();
        StringBuilder report = new StringBuilder()
                .append("Pattern Type: ").append(detection.getType())
                .append("\nConfidence: ").append(String.format(Locale.ROOT, "%.2f", detection.getConfidence()))
                .append("\nAnalysis: ").append(detection.getNote());

        if (model == null || detection.getType() == SequenceType.UNCLASSIFIED) {
            return report.append("\nGeneral term and sum formulas are unavailable for this input.").toString();
        }

        return report.append("\nGeneral term: ").append(model.getGeneralTermFormula())
                .append("\nSum formula: ").append(model.getSumFormula()).toString();
    }

    private String predictNextTerms(List<Double> terms, int count) {
        DetectionResult detection = sequenceAnalyzer.analyze(terms.stream().mapToDouble(Double::doubleValue).toArray());
        SequenceModel model = detection.getModel();
        if (model == null || detection.getType() == SequenceType.UNCLASSIFIED) {
            return "Cannot predict next terms: " + detection.getNote();
        }

        return "Detected pattern: " + detection.getType() + "\nNext " + count + " terms: " +
                Arrays.toString(model.generateNext(count));
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