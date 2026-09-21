package mathapp.algebra;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MatrixEngineTest {

    @Test
    void testDeterminantAndInversion() {
        double[][] data = {
            {4.0, 7.0},
            {2.0, 6.0}
        };
        Matrix A = new Matrix(data);

        double det = MatrixEngine.determinant(A);
        assertEquals(10.0, det, 1e-6);

        Matrix inv = MatrixEngine.inverse(A);
        assertEquals(0.6, inv.get(0, 0), 1e-6);
        assertEquals(-0.7, inv.get(0, 1), 1e-6);
        assertEquals(-0.2, inv.get(1, 0), 1e-6);
        assertEquals(0.4, inv.get(1, 1), 1e-6);
    }

    @Test
    void testSingularMatrixException() {
        double[][] data = {
            {1.0, 2.0},
            {2.0, 4.0}
        };
        Matrix A = new Matrix(data);

        assertEquals(0.0, MatrixEngine.determinant(A), 1e-6);
        assertThrows(ArithmeticException.class, () -> MatrixEngine.inverse(A));
    }
}