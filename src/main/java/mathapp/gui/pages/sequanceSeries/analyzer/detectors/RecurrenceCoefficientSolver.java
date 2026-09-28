package mathapp.gui.pages.sequanceSeries.analyzer.detectors;

import mathapp.gui.pages.sequanceSeries.analyzer.MathUtils;

/**
 * Solves c1, c2 for a homogeneous order-2 recurrence
 *     a3 = c1*a2 + c2*a1
 *     a4 = c1*a3 + c2*a2
 * via Cramer's rule. Shared by ArithmeticoGeometricDetector (which then checks
 * for a repeated root) and LinearRecurrenceDetector (distinct/complex roots).
 */
final class RecurrenceCoefficientSolver {

    private RecurrenceCoefficientSolver() { }

    static double[] solve(double a1, double a2, double a3, double a4) {
        double det = a2 * a2 - a1 * a3;
        if (MathUtils.approxEquals(det, 0.0)) return null; // singular system

        double c1 = (a3 * a2 - a1 * a4) / det;
        double c2 = (a2 * a4 - a3 * a3) / det;
        return new double[] { c1, c2 };
    }
}
