package com.mathapp.auth.service;

import org.junit.jupiter.api.Test;
import mathapp.stats.core.StatisticalState.DataPoint2D;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MathServiceTest {

    private final MathService mathService = new MathService();

    @Test
    void evaluatesArithmeticOperations() {
        assertEquals(5.0, mathService.arithmetic("ADD", 2.0, 3.0));
        assertEquals(9.0, mathService.arithmetic("SQUARE", 3.0, null));
    }

    @Test
    void rejectsDivisionByZero() {
        assertThrows(ArithmeticException.class, () -> mathService.arithmetic("DIVIDE", 1.0, 0.0));
    }

    @Test
    void rejectsNullOrUnsupportedOperationsInsteadOfThrowingNullPointerException() {
        assertThrows(IllegalArgumentException.class, () -> mathService.arithmetic(null, 1.0, null));
        assertThrows(IllegalArgumentException.class, () -> mathService.evaluateMatrix(null, new double[][]{{1.0}}));
        assertThrows(IllegalArgumentException.class,
                () -> mathService.evaluateDistribution(null, null, null, null, null, null, null));
    }

    @Test
    void evaluatesSequenceOperations() {
        assertTrue(mathService.evaluateSequence("FIBONACCI_SEQUENCE", "6", null).contains("[0, 1, 1, 2, 3, 5]"));
    }

    @Test
    void evaluatesMatrixOperations() {
        assertEquals("-2.0", mathService.evaluateMatrix("DETERMINANT",
                new double[][]{{1.0, 2.0}, {3.0, 4.0}}));
    }

    @Test
    void rejectsNullRowsAndNonSquareMatrices() {
        assertThrows(IllegalArgumentException.class,
                () -> mathService.evaluateMatrix("DETERMINANT", new double[][]{{1.0}, null}));
        assertThrows(IllegalArgumentException.class,
                () -> mathService.evaluateMatrix("DETERMINANT", new double[][]{{1.0, 2.0}}));
    }

    @Test
    void rejectsNullSequenceTerms() {
        assertThrows(IllegalArgumentException.class, () -> mathService.analyzeSequence(
                java.util.Arrays.asList(1.0, null, 3.0)));
    }

    @Test
    void runsStatisticalAndDistributionOperations() {
        var regression = mathService.fitRegression("LINEAR", List.of(
                new DataPoint2D(1.0, 2.0),
                new DataPoint2D(2.0, 4.0),
                new DataPoint2D(3.0, 6.0)), null, null);
        var distribution = mathService.evaluateDistribution(
                "NORMAL", 0.0, 1.0, 0.5, null, null, null);

        assertTrue(regression.report().contains("Regression"));
        assertEquals(101, distribution.points().size());
    }

    @Test
    void solvesEquationSystem() {
        String result = mathService.solveSystem(
                List.of("distance = speed * time", "acceleration = speed / time"),
                "distance=100 time=5 acceleration=?");
        assertTrue(result.contains("acceleration = 4.0"));
    }
}
