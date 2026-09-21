package mathapp.stats.engine;

import mathapp.stats.core.StatisticalState.DataPoint2D;
import java.util.*;

/**
 * Pure mathematical engine for covariance, parametric/non-parametric correlation,
 * linear regression, exponential transformation, and matrix-based polynomial fitting.
 */
public class CorrelationAndRegressionCalculator {

    /**
     * Calculates Sample Covariance: Cov(X, Y) = \frac{1}{n-1} \sum (x_i - \bar{x})(y_i - \bar{y})
     */
    public static double calculateCovariance(List<DataPoint2D> points, double meanX, double meanY) {
        if (points == null || points.size() < 2) {
            throw new IllegalArgumentException("Covariance requires at least 2 data points.");
        }
        double sumCov = 0.0;
        for (DataPoint2D p : points) {
            sumCov += (p.x() - meanX) * (p.y() - meanY);
        }
        return sumCov / (points.size() - 1);
    }

    /**
     * Calculates Pearson Product-Moment Correlation Coefficient (r).
     */
    public static double calculatePearsonR(List<DataPoint2D> points, double meanX, double meanY) {
        if (points == null || points.size() < 2) {
            return 0.0;
        }

        double num = 0.0;
        double denomX = 0.0;
        double denomY = 0.0;

        for (DataPoint2D p : points) {
            double diffX = p.x() - meanX;
            double diffY = p.y() - meanY;
            num += diffX * diffY;
            denomX += diffX * diffX;
            denomY += diffY * diffY;
        }

        double denom = Math.sqrt(denomX * denomY);
        if (denom == 0.0) return 0.0;
        return num / denom;
    }

    /**
     * Calculates Spearman Rank Correlation Coefficient (\rho) with handling for tied ranks.
     */
    public static double calculateSpearmanRho(List<DataPoint2D> points) {
        if (points == null || points.size() < 2) {
            return 0.0;
        }

        int n = points.size();
        double[] xVals = new double[n];
        double[] yVals = new double[n];

        for (int i = 0; i < n; i++) {
            xVals[i] = points.get(i).x();
            yVals[i] = points.get(i).y();
        }

        double[] xRanks = computeRanks(xVals);
        double[] yRanks = computeRanks(yVals);

        List<DataPoint2D> rankedPoints = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            rankedPoints.add(new DataPoint2D(xRanks[i], yRanks[i]));
        }

        double meanXRank = (n + 1) / 2.0;
        double meanYRank = (n + 1) / 2.0;

        return calculatePearsonR(rankedPoints, meanXRank, meanYRank);
    }

    /**
     * Computes fractional ranks for an array of values, handling tied ranks.
     */
    private static double[] computeRanks(double[] values) {
        int n = values.length;
        Integer[] indices = new Integer[n];
        for (int i = 0; i < n; i++) indices[i] = i;

        Arrays.sort(indices, Comparator.comparingDouble(i -> values[i]));

        double[] ranks = new double[n];
        int i = 0;
        while (i < n) {
            int j = i;
            while (j < n - 1 && Double.compare(values[indices[j]], values[indices[j + 1]]) == 0) {
                j++;
            }
            double averageRank = (i + j + 2) / 2.0; // 1-based indexing rank average
            for (int k = i; k <= j; k++) {
                ranks[indices[k]] = averageRank;
            }
            i = j + 1;
        }
        return ranks;
    }

    /**
     * Solves OLS polynomial coefficients \mathbf{\beta} = (\mathbf{X}^T \mathbf{X})^{-1} \mathbf{X}^T \mathbf{Y}
     * using Gaussian Elimination with partial pivoting.
     */
    public static double[] fitPolynomialCoefficients(List<DataPoint2D> points, int degree) {
        int n = points.size();
        int m = degree + 1;

        if (n < m) {
            throw new IllegalArgumentException("Insufficient points (" + n + ") to fit polynomial of degree " + degree);
        }

        double[][] XTX = new double[m][m];
        double[] XTY = new double[m];

        for (DataPoint2D p : points) {
            double x = p.x();
            double y = p.y();

            double[] xPowers = new double[2 * degree + 1];
            xPowers[0] = 1.0;
            for (int k = 1; k <= 2 * degree; k++) {
                xPowers[k] = xPowers[k - 1] * x;
            }

            for (int row = 0; row < m; row++) {
                for (int col = 0; col < m; col++) {
                    XTX[row][col] += xPowers[row + col];
                }
                XTY[row] += y * xPowers[row];
            }
        }

        return solveLinearSystem(XTX, XTY);
    }

    /**
     * Gaussian Elimination with Partial Pivoting.
     */
    private static double[] solveLinearSystem(double[][] A, double[] b) {
        int n = b.length;
        double[][] M = new double[n][n + 1];

        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, M[i], 0, n);
            M[i][n] = b[i];
        }

        for (int p = 0; p < n; p++) {
            int maxRow = p;
            for (int i = p + 1; i < n; i++) {
                if (Math.abs(M[i][p]) > Math.abs(M[maxRow][p])) {
                    maxRow = i;
                }
            }

            double[] temp = M[p];
            M[p] = M[maxRow];
            M[maxRow] = temp;

            if (Math.abs(M[p][p]) < 1e-12) {
                throw new ArithmeticException("Matrix is singular or near-singular.");
            }

            for (int i = p + 1; i < n; i++) {
                double alpha = M[i][p] / M[p][p];
                for (int j = p; j <= n; j++) {
                    M[i][j] -= alpha * M[p][j];
                }
            }
        }

        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double sum = 0.0;
            for (int j = i + 1; j < n; j++) {
                sum += M[i][j] * x[j];
            }
            x[i] = (M[i][n] - sum) / M[i][i];
        }
        return x;
    }
}