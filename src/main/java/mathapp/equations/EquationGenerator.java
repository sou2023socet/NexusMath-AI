package mathapp.equations;

import mathapp.equations.api.EquationSolver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class EquationGenerator {
    private final EquationSolver solver;

    public EquationGenerator(EquationSolver solver) {
        this.solver = solver;
    }

    public Map<String, Double> processAndSolve(String rawData, String formula) {
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

        if (unknownVar == null) {
            throw new IllegalArgumentException("No unknown variable ('=?') specified in input data.");
        }

        String correctedEquation = generateCorrectEquation(formula, unknownVar);
        double result = solver.solveEquation(correctedEquation, values);

        values.put(unknownVar, result);
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