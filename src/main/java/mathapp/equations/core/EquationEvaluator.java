package mathapp.equations.core;

import mathapp.arithmetic.ArithmeticOperations;
import mathapp.arithmetic.ExponentFunctions;

import java.util.*;

public class EquationEvaluator {

    // --- 1. Priority Handler ---
    // Higher return value means higher precedence
    private static int getPriority(char operator) {
        switch (operator) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            case '^':
                return 3;
            default:
                return -1;
        }
    }

    // --- 2. Main Solve Method ---
    public double solveEquation(String equation, Map<String, Double> values) {
        // Clean up whitespace
        equation = equation.replaceAll("\\s+", "");

        // If the equation contains an '=', isolate the expression side
        if (equation.contains("=")) {
            equation = equation.split("=")[0];
        }

        List<String> tokens = tokenize(equation);
        List<String> rpn = convertToRPN(tokens);
        return evaluateRPN(rpn, values);
    }

    // --- 3. Helper: Break string into variables and operators ---
    private List<String> tokenize(String equation) {
        List<String> tokens = new ArrayList<>();
        StringBuilder variable = new StringBuilder();

        for (int i = 0; i < equation.length(); i++) {
            char c = equation.charAt(i);

            if (getPriority(c) != -1) {
                if (variable.length() > 0) {
                    tokens.add(variable.toString());
                    variable.setLength(0);
                }
                tokens.add(String.valueOf(c));
            } else {
                variable.append(c);
            }
        }

        if (variable.length() > 0) {
            tokens.add(variable.toString());
        }
        return tokens;
    }

    // --- 4. Shunting-Yard Algorithm (Using Priority) ---
    private List<String> convertToRPN(List<String> tokens) {
        List<String> output = new ArrayList<>();
        Stack<Character> operators = new Stack<>();

        for (String token : tokens) {
            if (token.length() == 1 && getPriority(token.charAt(0)) != -1) {
                char op = token.charAt(0);
                // While top of stack has higher or equal priority, pop it to output
                while (!operators.isEmpty() && getPriority(operators.peek()) >= getPriority(op)) {
                    output.add(String.valueOf(operators.pop()));
                }
                operators.push(op);
            } else {
                output.add(token);
            }
        }

        while (!operators.isEmpty()) {
            output.add(String.valueOf(operators.pop()));
        }

        return output;
    }

    // --- 5. RPN Evaluator ---
    private double evaluateRPN(List<String> rpn, Map<String, Double> values) {
        Stack<Double> stack = new Stack<>();

        for (String token : rpn) {
            if (token.length() == 1 && getPriority(token.charAt(0)) != -1) {
                double b = stack.pop();
                double a = stack.pop();
                char op = token.charAt(0);

                double result = performOperation(a, b, op);
                stack.push(result);
            } else {
                double val;
                if (values != null && values.containsKey(token)) {
                    val = values.get(token);
                } else {
                    try {
                        val = Double.parseDouble(token);
                    } catch (NumberFormatException e) {
                        // Catch the specific variable causing the issue cleanly
                        System.out.println("⚠️ Evaluation Error: The variable '" + token + "' is unknown or has not been solved yet!");
                        return Double.NaN; // Return 'Not a Number' instead of crashing the whole app
                    }
                }
                stack.push(val);
            }
        }

        return stack.pop();
    }

    // --- 6. Math Operations ---
    private double performOperation(double a, double b, char operator) {
        switch (operator) {
            case '+':
                return ArithmeticOperations.sum(a, b);
            case '-':
                return ArithmeticOperations.subtract(a, b);
            case '*':
                return ArithmeticOperations.multiply(a, b);
            case '/':
                return ArithmeticOperations.divide(a, b);
            case '^':
                return ExponentFunctions.power(a, b);
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }
}

