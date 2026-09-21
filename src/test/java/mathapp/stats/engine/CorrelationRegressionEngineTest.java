package mathapp.stats.engine;

import mathapp.stats.dto.RegressionResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CorrelationRegressionEngineTest {

    @Test
    void testLinearRegressionCalculation() {
        double[] x = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] y = {2.0, 4.0, 5.0, 4.0, 5.0};

        RegressionResult result = CorrelationRegressionEngine.analyzeBivariate(x, y, 6.0, 0.05);

        assertEquals(0.6, result.slope(), 1e-4);
        assertEquals(2.2, result.intercept(), 1e-4);
        assertEquals(5.8, result.predictedY(), 1e-4);
        assertEquals(0.7745966692, result.pearsonR(), 1e-4);
    }

    @Test
    void testMismatchedArrayDimensionsException() {
        double[] x = {1.0, 2.0};
        double[] y = {1.0, 2.0, 3.0};

        assertThrows(IllegalArgumentException.class, () -> CorrelationRegressionEngine.analyzeBivariate(x, y, null, 0.05));
    }
}
