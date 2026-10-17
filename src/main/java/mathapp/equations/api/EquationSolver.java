package mathapp.equations.api;

import mathapp.equations.core.EquationEvaluator;

import java.util.Map;

/**
 * Public entry for equation evaluation.
 * Delegates to the packaged core evaluator.
 */
public class EquationSolver {
    private final EquationEvaluator evaluator;

    public EquationSolver() {
        this.evaluator = new EquationEvaluator();
    }

    public double solveEquation(String equation, Map<String, Double> values) {
        return evaluator.solveEquation(equation, values);
    }
}

