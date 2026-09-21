package mathapp.numerical.engine;

import mathapp.numerical.dto.RootFindingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

/**
 * Numerical Analysis Engine for Root Finding, Integration, and Optimization.
 */
public class NumericalOptimizationEngine {

    /**
     * Bisection Method for finding roots on a continuous interval [a, b] where f(a) * f(b) < 0.
     */
    public static RootFindingResult bisectionMethod(DoubleUnaryOperator f, double a, double b, double tol, int maxIter) {
        validateInterval(a, b);
        double fa = f.applyAsDouble(a);
        double fb = f.applyAsDouble(b);

        if (fa * fb > 0) {
            throw new IllegalArgumentException(
                String.format("Root is not bracketed: f(a)=%.4f and f(b)=%.4f must have opposite signs.", fa, fb)
            );
        }

        List<Double> history = new ArrayList<>();
        double mid = a;
        double fMid = fa;
        boolean converged = false;
        int iter = 0;

        StringBuilder report = new StringBuilder();
        report.append("### Bisection Method Root Finding\n\n");
        report.append(String.format("* **Bracket Interval:** `[%.4f, %.4f]`\n", a, b));
        report.append(String.format("* **Target Tolerance:** `%.1e` | **Max Iterations:** `%d`\n\n", tol, maxIter));
        report.append("| Iter | $a$ | $b$ | $m$ (Midpoint) | $f(m)$ | Error |\n");
        report.append("|---|---|---|---|---|---|\n");

        for (iter = 1; iter <= maxIter; iter++) {
            mid = a + (b - a) / 2.0;
            fMid = f.applyAsDouble(mid);
            history.add(mid);

            double error = (b - a) / 2.0;
            report.append(String.format("| %d | %.6f | %.6f | **%.6f** | %.6e | %.6e |\n",
                    iter, a, b, mid, fMid, error));

            if (Math.abs(fMid) < tol || error < tol) {
                converged = true;
                break;
            }

            if (fa * fMid < 0) {
                b = mid;
                fb = fMid;
            } else {
                a = mid;
                fa = fMid;
            }
        }

        String latex = "m_k = \\frac{a_k + b_k}{2}, \\quad f(a_k) \\cdot f(m_k) < 0 \\implies b_{k+1} = m_k";

        return new RootFindingResult("Bisection Method", mid, fMid, iter, converged, tol,
                history, latex, report.toString());
    }

    /**
     * Newton-Raphson Method using analytical first-derivative f'(x).
     */
    public static RootFindingResult newtonRaphsonMethod(DoubleUnaryOperator f, DoubleUnaryOperator df, double initialGuess, double tol, int maxIter) {
        List<Double> history = new ArrayList<>();
        double x = initialGuess;
        double fx = f.applyAsDouble(x);
        boolean converged = false;
        int iter = 0;

        StringBuilder report = new StringBuilder();
        report.append("### Newton-Raphson Method Root Finding\n\n");
        report.append(String.format("* **Initial Guess ($x_0$):** `%.6f`\n", initialGuess));
        report.append(String.format("* **Target Tolerance:** `%.1e` | **Max Iterations:** `%d`\n\n", tol, maxIter));
        report.append("| Iter | $x_n$ | $f(x_n)$ | $f'(x_n)$ | Step ($\\vert{}x_{n+1} - x_n\\vert{}$) |\n");
        report.append("|---|---|---|---|---|\n");

        for (iter = 1; iter <= maxIter; iter++) {
            history.add(x);
            double dfx = df.applyAsDouble(x);

            if (Math.abs(dfx) < 1e-12) {
                throw new ArithmeticException("Derivative near zero (" + dfx + ") encountered at iteration " + iter);
            }

            double step = fx / dfx;
            double xNext = x - step;
            double fNext = f.applyAsDouble(xNext);

            report.append(String.format("| %d | %.6f | %.6e | %.6f | %.6e |\n",
                    iter, x, fx, dfx, Math.abs(step)));

            if (Math.abs(step) < tol || Math.abs(fNext) < tol) {
                x = xNext;
                fx = fNext;
                history.add(x);
                converged = true;
                break;
            }

            x = xNext;
            fx = fNext;
        }

        String latex = "x_{n+1} = x_n - \\frac{f(x_n)}{f'(x_n)}";

        return new RootFindingResult("Newton-Raphson Method", x, fx, iter, converged, tol,
                history, latex, report.toString());
    }

    /**
     * Secant Method (Derivative-Free root finding requiring two initial points).
     */
    public static RootFindingResult secantMethod(DoubleUnaryOperator f, double x0, double x1, double tol, int maxIter) {
        List<Double> history = new ArrayList<>();
        history.add(x0);
        history.add(x1);

        double fx0 = f.applyAsDouble(x0);
        double fx1 = f.applyAsDouble(x1);
        boolean converged = false;
        int iter = 0;

        StringBuilder report = new StringBuilder();
        report.append("### Secant Method Root Finding\n\n");
        report.append(String.format("* **Initial Points:** $x_0 = %.6f, x_1 = %.6f$\n", x0, x1));
        report.append("| Iter | $x_{n-1}$ | $x_n$ | $x_{n+1}$ | $f(x_{n+1})$ |\n");
        report.append("|---|---|---|---|---|\n");

        for (iter = 1; iter <= maxIter; iter++) {
            double diffF = fx1 - fx0;
            if (Math.abs(diffF) < 1e-12) {
                throw new ArithmeticException("Division by zero in Secant slope update at iteration " + iter);
            }

            double xNext = x1 - fx1 * (x1 - x0) / diffF;
            double fNext = f.applyAsDouble(xNext);
            history.add(xNext);

            report.append(String.format("| %d | %.6f | %.6f | **%.6f** | %.6e |\n",
                    iter, x0, x1, xNext, fNext));

            if (Math.abs(xNext - x1) < tol || Math.abs(fNext) < tol) {
                x1 = xNext;
                fx1 = fNext;
                converged = true;
                break;
            }

            x0 = x1;
            fx0 = fx1;
            x1 = xNext;
            fx1 = fNext;
        }

        String latex = "x_{n+1} = x_n - f(x_n) \\cdot \\frac{x_n - x_{n-1}}{f(x_n) - f(x_{n-1})}";

        return new RootFindingResult("Secant Method", x1, fx1, iter, converged, tol,
                history, latex, report.toString());
    }

    /**
     * Composite Simpson's 1/3 Rule for definite numerical integration: \int_a^b f(x) dx.
     * 
     * @param intervals Must be an EVEN positive integer.
     */
    public static double integrateSimpsonsOneThird(DoubleUnaryOperator f, double a, double b, int intervals) {
        validateInterval(a, b);
        if (intervals <= 0 || intervals % 2 != 0) {
            throw new IllegalArgumentException("Composite Simpson's 1/3 rule requires an EVEN positive number of subintervals.");
        }

        double h = (b - a) / intervals;
        double sumOdd = 0.0;
        double sumEven = 0.0;

        for (int i = 1; i < intervals; i++) {
            double x = a + i * h;
            if (i % 2 == 0) {
                sumEven += f.applyAsDouble(x);
            } else {
                sumOdd += f.applyAsDouble(x);
            }
        }

        double fa = f.applyAsDouble(a);
        double fb = f.applyAsDouble(b);

        return (h / 3.0) * (fa + 4.0 * sumOdd + 2.0 * sumEven + fb);
    }

    /**
     * 1D Gradient Descent Minimization Engine for f(x).
     */
    public static RootFindingResult gradientDescent1D(DoubleUnaryOperator f, DoubleUnaryOperator df, double x0, double learningRate, double tol, int maxIter) {
        List<Double> history = new ArrayList<>();
        double x = x0;
        boolean converged = false;
        int iter = 0;

        StringBuilder report = new StringBuilder();
        report.append("### 1D Gradient Descent Minimization\n\n");
        report.append(String.format("* **Start Point ($x_0$):** `%.6f` | **Learning Rate ($\\eta$):** `%.4f`\n\n", x0, learningRate));

        for (iter = 1; iter <= maxIter; iter++) {
            history.add(x);
            double grad = df.applyAsDouble(x);
            double step = learningRate * grad;
            double xNext = x - step;

            if (Math.abs(grad) < tol || Math.abs(step) < tol) {
                converged = true;
                x = xNext;
                break;
            }

            x = xNext;
        }

        double fx = f.applyAsDouble(x);
        report.append(String.format("* **Optimized Minimum ($x^*$):** `%.6f`\n", x));
        report.append(String.format("* **Minimum Function Value ($f(x^*)$):** `%.6f`\n", fx));
        report.append(String.format("* **Iterations:** `%d` | **Converged:** `%b`\n", iter, converged));

        String latex = "x_{k+1} = x_k - \\eta \\cdot f'(x_k)";

        return new RootFindingResult("1D Gradient Descent", x, fx, iter, converged, tol,
                history, latex, report.toString());
    }

    private static void validateInterval(double a, double b) {
        if (a >= b) {
            throw new IllegalArgumentException("Lower bound 'a' must be strictly less than upper bound 'b'. Provided: a=" + a + ", b=" + b);
        }
    }
}