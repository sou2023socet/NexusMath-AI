package mathapp.equations;

import mathapp.equations.api.EquationSolver;
import mathapp.logging.MathLogger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class EquationGenerator {

    private static final String CAT = "EQUATION_GENERATOR";

    private final EquationSolver solver;

    public EquationGenerator(EquationSolver solver) {
        this.solver = solver;
    }

    public Map<String, Double> processAndSolve(String rawData, String formula) {
        MathLogger.operationStarted("processAndSolve", "formula=" + formula + ", rawData=" + rawData);

        Map<String, Double> values = new HashMap<>();
        String unknownVar = null;

        String[] assignments = rawData.trim().split("\\s+");
        for (String assignment : assignments) {
            String[] parts = assignment.split("=");
            if (parts.length != 2) continue;

            String varName = parts[0].trim();
            String valueStr = parts[1].trim();

            if (valueStr.equals("?")) {
                unknownVar = varName;
            } else {
                values.put(varName, Double.parseDouble(valueStr));
            }
        }

        MathLogger.operationStep(CAT, "PARSE_INPUT", "values=" + values + ", unknown=" + unknownVar);

        if (unknownVar == null) {
            MathLogger.operationFailed(CAT, "No unknown variable ('=?') specified in input data.");
            throw new IllegalArgumentException("No unknown variable ('=?') specified in input data.");
        }

     // 1. Normalize implicit multiplication (e.g., "3x" -> "3*x")
        String normalizedFormula = normalizeImplicitMultiplication(formula);
        MathLogger.operationStep(CAT, "NORMALIZE_FORMULA", "normalized=" + normalizedFormula);

        // 2. Solve linear equation numerically
        double result = solveLinearEquation(normalizedFormula, unknownVar, values);
        MathLogger.operationStep(CAT, "SOLVE_RESULT", "result=" + result);

        values.put(unknownVar, result);
        MathLogger.operationSuccess(CAT, "processAndSolve result=" + values);
        return values;
    }

    private String generateCorrectEquation(String formula, String targetVar) {
        formula = formula.replaceAll("\\s+", "");
        String[] sides = formula.split("=");
        if (sides.length != 2) {
            throw new IllegalArgumentException("Invalid formula structure. Must contain exactly one '='.");
        }

        String leftSide = sides[0];
        String rightSide = sides[1];

        // ✅ LOGIC FIX: Ensure the math expression is ALWAYS on the left side of the '=' 
        // so EquationEvaluator's split("=")[0] grabs the math, not the target variable!
        if (leftSide.equals(targetVar)) {
            return rightSide + "=" + targetVar;
        } else if (rightSide.equals(targetVar)) {
            return leftSide + "=" + targetVar;
        }

        // If the variable is trapped inside the expression, do basic rearranging
        String expressionWithTarget = leftSide.contains(targetVar) ? leftSide : rightSide;
        String isolatedSide = leftSide.contains(targetVar) ? rightSide : leftSide;

        List<String> tokens = tokenizeExpression(expressionWithTarget);
        StringBuilder correctedLeftExpression = new StringBuilder(isolatedSide);

        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);

            if ((token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/")) && i + 1 < tokens.size()) {
                String nextToken = tokens.get(i + 1);
                if (!nextToken.equals(targetVar)) {
                    char inverseOp = getInverseOperator(token.charAt(0));
                    correctedLeftExpression.append(inverseOp).append(nextToken);
                    i++;
                }
            } else if (i == 0 && !token.equals(targetVar) && tokens.size() > 1) {
                correctedLeftExpression.append("-").append(token);
            }
        }

        // Returns format: mathematical_expression=targetVariable
        return correctedLeftExpression.toString() + "=" + targetVar;
    }
    
    private double solveLinearEquation(String formula, String targetVar, Map<String, Double> knownValues) {
        String[] sides = formula.split("=");
        if (sides.length != 2) {
            throw new IllegalArgumentException("Invalid formula structure. Must contain exactly one '='.");
        }

        String lhs = sides[0].trim();
        String rhs = sides[1].trim();

        // Evaluate f(0)
        Map<String, Double> eval0 = new HashMap<>(knownValues);
        eval0.put(targetVar, 0.0);
        double f0 = solver.solveEquation(lhs, eval0) - solver.solveEquation(rhs, eval0);

        // Evaluate f(1)
        Map<String, Double> eval1 = new HashMap<>(knownValues);
        eval1.put(targetVar, 1.0);
        double f1 = solver.solveEquation(lhs, eval1) - solver.solveEquation(rhs, eval1);

        double A = f1 - f0;
        double B = f0;

        if (Math.abs(A) < 1e-9) {
            throw new ArithmeticException("Equation has no unique solution for variable '" + targetVar + "' (A = 0).");
        }

        return -B / A;
    }

    
    public static String normalizeImplicitMultiplication(String expr) {
        return expr.replaceAll("(\\d+(?:\\.\\d+)?)\\s*([a-zA-Z_])", "$1*$2");
    }

    private char getInverseOperator(char op) {
        switch (op) {
            case '+': return '-';
            case '-': return '+';
            case '*': return '/';
            case '/': return '*';
            default: return op;
        }
    }

    private List<String> tokenizeExpression(String expr) {
        List<String> tokens = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        for (char c : expr.toCharArray()) {
            if (c == '+' || c == '-' || c == '*' || c == '/') {
                if (buffer.length() > 0) {
                    tokens.add(buffer.toString());
                    buffer.setLength(0);
                }
                tokens.add(String.valueOf(c));
            } else {
                buffer.append(c);
            }
        }
        if (buffer.length() > 0) tokens.add(buffer.toString());
        return tokens;
    }
}