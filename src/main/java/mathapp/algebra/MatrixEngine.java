package mathapp.algebra;

/**
 * Linear Algebra Engine for Determinants, Matrix Inversion, and System Solving.
 */
public class MatrixEngine {

    private static final double EPSILON = 1e-10;

    /**
     * Computes the determinant of a square matrix via LU Decomposition with partial pivoting.
     */
    public static double determinant(Matrix A) {
        if (!A.isSquare()) throw new IllegalArgumentException("Determinant is only defined for square matrices.");
        int n = A.getRows();
        double[][] a = A.toArray();
        double det = 1.0;

        for (int i = 0; i < n; i++) {
            // Partial pivoting
            int pivot = i;
            for (int j = i + 1; j < n; j++) {
                if (Math.abs(a[j][i]) > Math.abs(a[pivot][i])) pivot = j;
            }

            if (pivot != i) {
                double[] temp = a[i];
                a[i] = a[pivot];
                a[pivot] = temp;
                det = -det;
            }

            if (Math.abs(a[i][i]) < EPSILON) return 0.0; // Singular matrix

            det *= a[i][i];

            for (int j = i + 1; j < n; j++) {
                double factor = a[j][i] / a[i][i];
                for (int k = i + 1; k < n; k++) {
                    a[j][k] -= factor * a[i][k];
                }
            }
        }
        return det;
    }

    /**
     * Computes the inverse of a square matrix A^{-1} using Gauss-Jordan elimination.
     */
    public static Matrix inverse(Matrix A) {
        if (!A.isSquare()) throw new IllegalArgumentException("Matrix must be square to invert.");
        int n = A.getRows();
        double[][] aug = new double[n][2 * n];
        double[][] aData = A.toArray();

        for (int i = 0; i < n; i++) {
            System.arraycopy(aData[i], 0, aug[i], 0, n);
            aug[i][n + i] = 1.0;
        }

        for (int i = 0; i < n; i++) {
            int pivot = i;
            for (int j = i + 1; j < n; j++) {
                if (Math.abs(aug[j][i]) > Math.abs(aug[pivot][i])) pivot = j;
            }

            if (Math.abs(aug[pivot][i]) < EPSILON) {
                throw new ArithmeticException("Matrix is singular and cannot be inverted.");
            }

            double[] temp = aug[i];
            aug[i] = aug[pivot];
            aug[pivot] = temp;

            double pivotVal = aug[i][i];
            for (int j = 0; j < 2 * n; j++) aug[i][j] /= pivotVal;

            for (int j = 0; j < n; j++) {
                if (j != i) {
                    double factor = aug[j][i];
                    for (int k = 0; k < 2 * n; k++) {
                        aug[j][k] -= factor * aug[i][k];
                    }
                }
            }
        }

        double[][] invData = new double[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(aug[i], n, invData[i], 0, n);
        }

        return new Matrix(invData);
    }

    /**
     * Solves linear system A \mathbf{x} = \mathbf{b} using A^{-1} \mathbf{b}.
     */
    public static Matrix solveSystem(Matrix A, Matrix b) {
        if (b.getCols() != 1) throw new IllegalArgumentException("Right-hand side b must be a column vector.");
        Matrix invA = inverse(A);
        return invA.multiply(b);
    }
}