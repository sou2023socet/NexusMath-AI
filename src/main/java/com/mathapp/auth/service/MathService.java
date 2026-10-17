package com.mathapp.auth.service;

import mathapp.algebra.Matrix;
import mathapp.algebra.MatrixEngine;
import mathapp.arithmetic.ArithmeticOperations;
import mathapp.arithmetic.ExponentFunctions;
import mathapp.equations.EquationGenerator;
import mathapp.equations.MultipleEquationHandler;
import mathapp.equations.api.EquationSolver;
import mathapp.gui.pages.sequanceSeries.PatternsAndSequencesController;
import mathapp.gui.pages.sequanceSeries.PatternsAndSequencesController.Domain2Property;
import mathapp.gui.pages.patterns.SequencePatternEngine;
import mathapp.stats.core.StatisticalState.DataPoint2D;
import mathapp.stats.dto.LogisticRegressionResult;
import mathapp.stats.dto.RegressionResult;
import mathapp.stats.engine.DistributionEngineImpl.DistributionEvaluationResult;
import mathapp.stats.engine.DistributionEngineImpl.DistributionPoint;
import mathapp.stats.engine.CorrelationRegressionEngine;
import mathapp.stats.engine.ProbabilityCombinatoricsEngine;
import mathapp.stats.engine.ProbabilityAndSetCalculator.BayesResult;
import mathapp.stats.engine.ProbabilityAndSetCalculator.ContingencyResult;
import mathapp.stats.engine.ProbabilityAndSetCalculator.Venn2SetResult;
import mathapp.stats.facade.StatsServiceFacade;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class MathService {

    private final StatsServiceFacade statsService = new StatsServiceFacade();

    public double arithmetic(String operation, double first, Double second) {
        requireText(operation, "An arithmetic operation is required.");
        return switch (operation) {
            case "ADD" -> ArithmeticOperations.sum(first, requireSecond(second));
            case "SUBTRACT" -> ArithmeticOperations.subtract(first, requireSecond(second));
            case "MULTIPLY" -> ArithmeticOperations.multiply(first, requireSecond(second));
            case "DIVIDE" -> ArithmeticOperations.divide(first, requireSecond(second));
            case "REMAINDER" -> ArithmeticOperations.remainder(first, requireSecond(second));
            case "POWER" -> ExponentFunctions.power(first, requireSecond(second));
            case "SQUARE" -> ArithmeticOperations.square(first);
            default -> throw new IllegalArgumentException("Unsupported arithmetic operation.");
        };
    }

    public String solveSystem(List<String> equations, String variableValues) {
        if (equations == null || equations.isEmpty() || equations.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("At least one equation is required.");
        }
        requireText(variableValues, "Variable values are required.");
        EquationSolver solver = new EquationSolver();
        MultipleEquationHandler handler = new MultipleEquationHandler(new EquationGenerator(solver), solver);
        Map<String, Double> result = handler.solveSystem(equations, variableValues);
        return result.entrySet().stream()
                .map(entry -> entry.getKey() + " = " + entry.getValue())
                .collect(Collectors.joining(System.lineSeparator(), "Resolved variables:" + System.lineSeparator(), ""));
    }

    public String evaluateSequence(String operation, String input, Integer targetN) {
        requireText(operation, "A sequence operation is required.");
        requireText(input, "Sequence input is required.");
        Domain2Property property;
        try {
            property = Domain2Property.valueOf(operation);
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new IllegalArgumentException("Unsupported sequence operation.");
        }

        Object parsedInput = switch (property) {
            case ANALYZE_SEQUENCE_PATTERN, PREDICT_NEXT_TERMS, GENERATE_DIFFERENCE_TABLE,
                    FIND_NTH_TERM, SUM_SERIES_TO_N, CHECK_MONOTONIC_AND_BOUNDS,
                    FREQUENCY_TABLE -> parseDoubles(input);
            case POLYGONAL_NUMBERS, STEM_AND_LEAF_PLOT -> parseIntegers(input);
            case FIBONACCI_SEQUENCE, CATALAN_NUMBERS, PASCALS_TRIANGLE -> parseSingleDouble(input);
        };
        return new PatternsAndSequencesController().evaluateProperty(property, parsedInput, targetN);
    }

    public String evaluateMatrix(String operation, double[][] values) {
        requireText(operation, "A matrix operation is required.");
        if (values == null || values.length == 0 || Arrays.stream(values).anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Matrix values must contain at least one non-empty row.");
        }
        int columns = values[0].length;
        if (columns == 0 || values.length != columns
                || Arrays.stream(values).anyMatch(row -> row.length != columns)) {
            throw new IllegalArgumentException("Matrix values must form a non-empty square matrix.");
        }
        Matrix matrix = new Matrix(values);
        return switch (operation) {
            case "DETERMINANT" -> Double.toString(MatrixEngine.determinant(matrix));
            case "INVERSE" -> MatrixEngine.inverse(matrix).toString();
            case "INVERSE_WITH_DETERMINANT" -> {
                double determinant = MatrixEngine.determinant(matrix);
                if (Math.abs(determinant) < 1e-10) {
                    yield "Matrix is singular (Determinant = 0). Inverse does not exist.";
                }
                Matrix inverse = MatrixEngine.inverse(matrix);
                yield String.format("Determinant: %.4f%n%nInverse Matrix A^-1:%n%s", determinant, inverse);
            }
            default -> throw new IllegalArgumentException("Unsupported matrix operation.");
        };
    }

    public String analyzeSequence(List<Double> terms) {
        if (terms == null || terms.isEmpty() || terms.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Sequence terms are required.");
        }
        return SequencePatternEngine.analyzeSequence(
                terms.stream().mapToDouble(Double::doubleValue).toArray(), 5).stepByStepReport();
    }

    public String analyzeBivariate(double[] x, double[] y, Double targetX) {
        if (x == null || y == null || x.length != y.length || x.length < 2) {
            throw new IllegalArgumentException("Matching X and Y data with at least two points are required.");
        }
        return CorrelationRegressionEngine.analyzeBivariate(x, y, targetX, 0.05).stepByStepReport();
    }

    public String calculateCombinations(int n, int r) {
        return ProbabilityCombinatoricsEngine.calculateCombinations(n, r).stepByStepReport();
    }

    public RegressionResponse fitRegression(
            String model, List<DataPoint2D> points, Integer polynomialDegree, Double threshold) {
        requireText(model, "A regression model is required.");
        if (points == null || points.size() < 2 || points.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("At least two data points are required.");
        }

        return switch (model) {
            case "LINEAR" -> new RegressionResponse(
                    statsService.fitLinearModel(points).stepByStepReport(), List.of());
            case "POLYNOMIAL" -> {
                if (polynomialDegree == null) {
                    throw new IllegalArgumentException("Polynomial degree is required.");
                }
                RegressionResult result = statsService.fitPolynomialModel(points, polynomialDegree);
                yield new RegressionResponse(result.stepByStepReport(), List.of());
            }
            case "EXPONENTIAL" -> new RegressionResponse(
                    statsService.fitExponentialModel(points).stepByStepReport(), List.of());
            case "LOGISTIC" -> {
                if (threshold == null) {
                    throw new IllegalArgumentException("A logistic decision threshold is required.");
                }
                LogisticRegressionResult result = statsService.fitLogisticModel(points, threshold);
                yield new RegressionResponse(result.stepByStepReport(), result.predictedProbabilities());
            }
            default -> throw new IllegalArgumentException("Unsupported regression model.");
        };
    }

    public String solveVenn2(double pA, double pB, double pIntersect) {
        Venn2SetResult result = statsService.solveVenn2(pA, pB, pIntersect);
        return String.format(
                "### 2-Set Venn Diagram Analysis%n%n"
                        + "* $P(A)$: `%.4f` | $P(B)$: `%.4f` | $P(A \\cap B)$: `%.4f`%n"
                        + "* Union $P(A \\cup B)$: `%.4f` (%.2f%%)%n"
                        + "* Only $A$: `%.4f` | Only $B$: `%.4f`%n"
                        + "* Neither $P((A \\cup B)^c)$: `%.4f`",
                result.pA(), result.pB(), result.pIntersect(), result.pUnion(), result.pUnion() * 100.0,
                result.pAOnly(), result.pBOnly(), result.pNeither());
    }

    public String updateBayes(List<String> names, List<Double> priors, List<Double> likelihoods) {
        if (names == null || priors == null || likelihoods == null
                || names.size() != priors.size() || names.size() != likelihoods.size()
                || names.stream().anyMatch(Objects::isNull)
                || priors.stream().anyMatch(Objects::isNull)
                || likelihoods.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Hypothesis names, priors, and likelihoods must have matching values.");
        }
        BayesResult result = statsService.updateBayes(names, priors, likelihoods);
        return result.stepByStepReport();
    }

    public String analyzeContingency(double[][] counts) {
        if (counts == null || counts.length == 0 || Arrays.stream(counts).anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Contingency counts must contain at least one row.");
        }
        ContingencyResult result = statsService.analyzeContingency(counts);
        return result.stepByStepReport();
    }

    public DistributionResponse evaluateDistribution(
            String distribution, Double mean, Double standardDeviation, Double x,
            Integer trials, Double probability, Integer successes) {
        requireText(distribution, "A distribution type is required.");
        DistributionEvaluationResult result = switch (distribution) {
            case "NORMAL" -> statsService.evaluateNormalDistribution(
                    requireValue(mean, "Mean"), requireValue(standardDeviation, "Standard deviation"),
                    requireValue(x, "Evaluation point"));
            case "BINOMIAL" -> statsService.evaluateBinomialDistribution(
                    requireValue(trials, "Trial count"), requireValue(probability, "Probability"),
                    requireValue(successes, "Success count"));
            default -> throw new IllegalArgumentException("Unsupported distribution.");
        };
        return new DistributionResponse(result.stepByStepReport(), result.curvePoints());
    }

    private double requireSecond(Double second) {
        if (second == null) {
            throw new IllegalArgumentException("A second operand is required for this operation.");
        }
        return second;
    }

    private List<Double> parseDoubles(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Input cannot be empty.");
        }
        return Arrays.stream(input.trim().split("[,\\s]+"))
                .map(Double::parseDouble)
                .toList();
    }

    private List<Integer> parseIntegers(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Input cannot be empty.");
        }
        return Arrays.stream(input.trim().split("[,\\s]+"))
                .map(Integer::parseInt)
                .toList();
    }

    private double parseSingleDouble(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Input cannot be empty.");
        }
        return Double.parseDouble(input.trim());
    }

    private <T> T requireValue(T value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " is required.");
        }
        return value;
    }

    private void requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

    public record RegressionResponse(String report, List<DataPoint2D> predictedProbabilities) {
    }

    public record DistributionResponse(String report, List<DistributionPoint> points) {
    }
}
