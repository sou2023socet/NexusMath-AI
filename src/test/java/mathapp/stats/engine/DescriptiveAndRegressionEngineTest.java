package mathapp.stats.engine;

import mathapp.stats.core.StatisticalState.DataPoint2D;
import mathapp.stats.dto.LogisticRegressionResult;
import mathapp.stats.dto.RegressionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Regression & Correlation Engine Unit Tests")
class DescriptiveAndRegressionEngineTest {

    private IRegressionEngine regressionEngine;

    @BeforeEach
    void setUp() {
        regressionEngine = new RegressionEngineImpl();
    }

    @Test
    @DisplayName("Linear OLS Regression: Perfect Linear Alignment y = 2x + 1")
    void testPerfectLinearFit() {
        List<DataPoint2D> points = List.of(
                new DataPoint2D(1.0, 3.0), new DataPoint2D(2.0, 5.0),
                new DataPoint2D(3.0, 7.0), new DataPoint2D(4.0, 9.0),
                new DataPoint2D(5.0, 11.0));

        RegressionResult result = regressionEngine.fitLinearRegression(points);

        assertEquals(2.0, result.slope(), 1e-6);
        assertEquals(1.0, result.intercept(), 1e-6);
        assertEquals(1.0, result.rSquared(), 1e-6);
        assertEquals(1.0, result.pearsonR(), 1e-6);
        assertEquals(0.0, result.residuals().stream().mapToDouble(value -> value * value).sum(), 1e-6);
    }

    @Test
    @DisplayName("Polynomial Regression: Degree 2 Fit for y = x^2")
    void testPolynomialQuadraticFit() {
        List<DataPoint2D> points = List.of(
                new DataPoint2D(-2.0, 4.0), new DataPoint2D(-1.0, 1.0),
                new DataPoint2D(0.0, 0.0), new DataPoint2D(1.0, 1.0),
                new DataPoint2D(2.0, 4.0));

        RegressionResult result = regressionEngine.fitPolynomialRegression(points, 2);

        assertEquals(1.0, result.rSquared(), 1e-4);
        assertEquals(0.0, result.slope(), 1e-4);
        assertEquals(0.0, result.intercept(), 1e-4);
        assertNotNull(result.regressionEquation());
    }

    @Test
    @DisplayName("Logistic Regression: Binary Classification Convergence")
    void testLogisticRegressionConvergence() {
        List<DataPoint2D> dataset = List.of(
                new DataPoint2D(0.5, 0.0), new DataPoint2D(1.0, 0.0),
                new DataPoint2D(1.5, 0.0), new DataPoint2D(3.0, 1.0),
                new DataPoint2D(4.0, 1.0), new DataPoint2D(5.0, 1.0));

        LogisticRegressionResult result = regressionEngine.fitLogisticRegression(dataset, 0.5);

        assertTrue(result.accuracy() >= 0.83);
        assertTrue(result.slope() > 0.0);
        assertNotNull(result.stepByStepReport());
    }

    @Test
    void testInsufficientPointsThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> regressionEngine.fitLinearRegression(List.of(new DataPoint2D(1.0, 2.0))));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 6})
    void testInvalidPolynomialDegree(int invalidDegree) {
        List<DataPoint2D> points = List.of(
                new DataPoint2D(1.0, 2.0), new DataPoint2D(2.0, 4.0), new DataPoint2D(3.0, 5.0));

        assertThrows(IllegalArgumentException.class,
                () -> regressionEngine.fitPolynomialRegression(points, invalidDegree));
    }
}
