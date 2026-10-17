package com.mathapp.auth.api;

import com.mathapp.auth.service.MathService;
import com.mathapp.auth.service.NumberPropertyService;
import mathapp.stats.core.StatisticalState.DataPoint2D;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/math")
public class MathController {

    private final MathService mathService;
    private final NumberPropertyService numberPropertyService;

    public MathController(MathService mathService, NumberPropertyService numberPropertyService) {
        this.mathService = mathService;
        this.numberPropertyService = numberPropertyService;
    }

    @PostMapping("/arithmetic")
    public ResponseEntity<Double> arithmetic(@Valid @RequestBody ArithmeticRequest request) {
        return ResponseEntity.ok(mathService.arithmetic(
                request.operation(), request.firstOperand(), request.secondOperand()));
    }

    @PostMapping("/equations/solve")
    public ResponseEntity<String> solveSystem(@Valid @RequestBody EquationRequest request) {
        return ResponseEntity.ok(mathService.solveSystem(request.equations(), request.variableValues()));
    }

    @PostMapping("/sequences/evaluate")
    public ResponseEntity<String> evaluateSequence(@Valid @RequestBody SequenceRequest request) {
        return ResponseEntity.ok(mathService.evaluateSequence(
                request.operation(), request.input(), request.targetN()));
    }

    @PostMapping("/sequences/analyze")
    public ResponseEntity<String> analyzeSequence(@Valid @RequestBody SequenceTermsRequest request) {
        return ResponseEntity.ok(mathService.analyzeSequence(request.terms()));
    }

    @PostMapping("/matrices/evaluate")
    public ResponseEntity<String> evaluateMatrix(@Valid @RequestBody MatrixRequest request) {
        return ResponseEntity.ok(mathService.evaluateMatrix(request.operation(), request.values()));
    }

    @PostMapping("/statistics/correlation")
    public ResponseEntity<String> analyzeBivariate(@Valid @RequestBody CorrelationRequest request) {
        return ResponseEntity.ok(mathService.analyzeBivariate(
                request.x(), request.y(), request.targetX()));
    }

    @PostMapping("/combinatorics")
    public ResponseEntity<String> calculateCombinations(@Valid @RequestBody CombinatoricsRequest request) {
        return ResponseEntity.ok(mathService.calculateCombinations(request.n(), request.r()));
    }

    @PostMapping("/number-properties/evaluate")
    public ResponseEntity<String> evaluateNumberProperty(@Valid @RequestBody NumberPropertyRequest request) {
        return ResponseEntity.ok(numberPropertyService.evaluate(request.operation(), request.input()));
    }

    @PostMapping("/statistics/regression")
    public ResponseEntity<MathService.RegressionResponse> fitRegression(
            @Valid @RequestBody RegressionRequest request) {
        return ResponseEntity.ok(mathService.fitRegression(
                request.operation(), request.points(), request.polynomialDegree(), request.threshold()));
    }

    @PostMapping("/statistics/venn2")
    public ResponseEntity<String> solveVenn2(@Valid @RequestBody Venn2Request request) {
        return ResponseEntity.ok(mathService.solveVenn2(
                request.probabilityA(), request.probabilityB(), request.intersection()));
    }

    @PostMapping("/statistics/bayes")
    public ResponseEntity<String> updateBayes(@Valid @RequestBody BayesRequest request) {
        return ResponseEntity.ok(mathService.updateBayes(
                request.hypothesisNames(), request.priors(), request.likelihoods()));
    }

    @PostMapping("/statistics/contingency")
    public ResponseEntity<String> analyzeContingency(@Valid @RequestBody ContingencyRequest request) {
        return ResponseEntity.ok(mathService.analyzeContingency(request.counts()));
    }

    @PostMapping("/distributions/evaluate")
    public ResponseEntity<MathService.DistributionResponse> evaluateDistribution(
            @Valid @RequestBody DistributionRequest request) {
        return ResponseEntity.ok(mathService.evaluateDistribution(
                request.distribution(), request.mean(), request.standardDeviation(), request.x(),
                request.trials(), request.probability(), request.successes()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleInvalidMathRequest(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(ArithmeticException.class)
    public ResponseEntity<ErrorResponse> handleArithmeticFailure(ArithmeticException exception) {
        return ResponseEntity.badRequest().body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequest(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .orElse("Request validation failed.");
        return ResponseEntity.badRequest().body(new ErrorResponse(message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableRequest(HttpMessageNotReadableException exception) {
        return ResponseEntity.badRequest().body(new ErrorResponse("Request body is missing or malformed."));
    }

    public record ArithmeticRequest(
            @NotBlank String operation,
            @NotNull Double firstOperand,
            Double secondOperand) {
    }

    public record EquationRequest(
            @NotEmpty List<@NotBlank String> equations,
            @NotBlank String variableValues) {
    }

    public record SequenceRequest(
            @NotBlank String operation,
            @NotBlank String input,
            Integer targetN) {
    }

    public record SequenceTermsRequest(@NotEmpty List<@NotNull Double> terms) {
    }

    public record MatrixRequest(
            @NotBlank String operation,
            @NotNull double[][] values) {
    }

    public record CorrelationRequest(
            @NotNull double[] x,
            @NotNull double[] y,
            Double targetX) {
    }

    public record CombinatoricsRequest(int n, int r) {
    }

    public record NumberPropertyRequest(
            @NotBlank String operation,
            @NotBlank String input) {
    }

    public record RegressionRequest(
            @NotBlank String operation,
            @NotEmpty List<@NotNull DataPoint2D> points,
            Integer polynomialDegree,
            Double threshold) {
    }

    public record Venn2Request(
            @NotNull Double probabilityA,
            @NotNull Double probabilityB,
            @NotNull Double intersection) {
    }

    public record BayesRequest(
            @NotEmpty List<@NotBlank String> hypothesisNames,
            @NotEmpty List<@NotNull Double> priors,
            @NotEmpty List<@NotNull Double> likelihoods) {
    }

    public record ContingencyRequest(@NotNull double[][] counts) {
    }

    public record DistributionRequest(
            @NotBlank String distribution,
            Double mean,
            Double standardDeviation,
            Double x,
            Integer trials,
            Double probability,
            Integer successes) {
    }

    public record ErrorResponse(String error) {
    }
}
