package mathapp.controller;

import mathapp.algebra.Matrix;
import mathapp.algebra.MatrixEngine;
import mathapp.gui.pages.patterns.SequencePatternEngine;
import mathapp.gui.pages.patterns.SequencePatternResult;
import mathapp.stats.engine.CorrelationRegressionEngine;
import mathapp.stats.engine.ProbabilityCombinatoricsEngine;

/**
 * Orchestrating Application Controller bridging domain engines with presentation UI.
 */
public class MathAppMainController {

    public String processSequenceRequest(double[] terms) {
        SequencePatternResult result = SequencePatternEngine.analyzeSequence(terms, 5);
        return result.stepByStepReport();
    }

    public String processRegressionRequest(double[] x, double[] y, Double xToPredict) {
        return CorrelationRegressionEngine.analyzeBivariate(x, y, xToPredict, 0.05).stepByStepReport();
    }

    public String processCombinatoricsRequest(int n, int r) {
        return ProbabilityCombinatoricsEngine.calculateCombinations(n, r).stepByStepReport();
    }

    public String processMatrixInversion(double[][] matrixData) {
        Matrix A = new Matrix(matrixData);
        double det = MatrixEngine.determinant(A);

        if (Math.abs(det) < 1e-10) {
            return "### Matrix Inversion Error\n\n* **Status:** Matrix is **singular** (Determinant = 0). Inverse does not exist.";
        }

        Matrix inv = MatrixEngine.inverse(A);
        return String.format("### Matrix Inversion Successful\n\n* **Determinant:** `%.4f`\n\n**Inverse Matrix $A^{-1}$:**\n```\n%s```", det, inv.toString());
    }
}