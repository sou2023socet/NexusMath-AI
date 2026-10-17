package mathapp.numberproperties;

import mathapp.equations.core.EquationEvaluator;
import mathapp.logging.MathLogger;
import java.util.*;

public class NumberPropertyChecker {

    private static final String CAT = "PROPERTY_CHECKER";
    private final EquationEvaluator evaluator;

    public NumberPropertyChecker() {
        this.evaluator = new EquationEvaluator();
    }

    /**
     * Accepts a full equation containing '=' (e.g., "a + b = b + a")
     * and checks whether L.H.S == R.H.S and identifies the property.
     */
    public PropertyCheckResult checkProperty(String fullEquation, Map<String, Double> values) {
        MathLogger.operationStarted("checkProperty", "equation=" + fullEquation);

        if (!fullEquation.contains("=")) {
            return new PropertyCheckResult("", "", Double.NaN, Double.NaN, false,
                    "Invalid Input", "Equation must contain an '=' sign to separate L.H.S and R.H.S.");
        }

        String[] parts = fullEquation.split("=");
        if (parts.length != 2) {
            return new PropertyCheckResult("", "", Double.NaN, Double.NaN, false,
                    "Invalid Input", "Equation must contain exactly one '=' operator.");
        }

        String lhsStr = parts[0].trim();
        String rhsStr = parts[1].trim();

        // 1. Evaluate both sides independently using EquationEvaluator pipeline
        double lhsValue = evaluator.solveEquation(lhsStr, values);
        double rhsValue = evaluator.solveEquation(rhsStr, values);

        boolean isEqual = Double.compare(lhsValue, rhsValue) == 0;

        // 2. Identify structural mathematical property
        String propertyName = detectPropertyType(lhsStr, rhsStr);
        
        // 3. Formulate explanation based on validity
        String reason = generateReason(propertyName, lhsValue, rhsValue, isEqual, lhsStr, rhsStr);

        PropertyCheckResult result = new PropertyCheckResult(
                lhsStr, rhsStr, lhsValue, rhsValue, isEqual, propertyName, reason
        );

        MathLogger.operationSuccess(CAT, "checkProperty complete. Equal=" + isEqual);
        return result;
    }

    private String detectPropertyType(String lhs, String rhs) {
        String cleanLhs = lhs.replaceAll("\\s+", "");
        String cleanRhs = rhs.replaceAll("\\s+", "");

        // Distributive: e.g., a*(b+c) vs a*b+a*c
        if ((cleanLhs.contains("(") || cleanRhs.contains("(")) && 
            (cleanLhs.contains("*") || cleanLhs.contains("/") || cleanRhs.contains("*") || cleanRhs.contains("/"))) {
            return "Distributive Property";
        }
        
        // Associative: check if regrouping parenthesis exists e.g. (a+b)+c vs a+(b+c)
        if (cleanLhs.contains("(") && cleanRhs.contains("(")) {
            return "Associative Property";
        }

        // Identity: operations involving 0 (addition/subtraction) or 1 (multiplication/division)
        if (cleanLhs.contains("+0") || cleanLhs.contains("0+") || cleanLhs.contains("*1") || cleanLhs.contains("1*") ||
            cleanRhs.contains("+0") || cleanRhs.contains("0+") || cleanRhs.contains("*1") || cleanRhs.contains("1*")) {
            return "Identity Property";
        }

        // Commutative: default binary order swap e.g. a + b vs b + a
        return "Commutative Property";
    }

    private String generateReason(String property, double lhsVal, double rhsVal, boolean isEqual, String lhs, String rhs) {
        if (isEqual) {
            switch (property) {
                case "Commutative Property":
                    return "Order of operands does not change the result for this operation (" + lhsVal + " == " + rhsVal + ").";
                case "Associative Property":
                    return "Grouping of operands with parentheses does not change the result (" + lhsVal + " == " + rhsVal + ").";
                case "Distributive Property":
                    return "Multiplying a sum is equal to multiplying each addend individually (" + lhsVal + " == " + rhsVal + ").";
                case "Identity Property":
                    return "Operating with the identity element (0 for +, 1 for *) preserves the original value (" + lhsVal + " == " + rhsVal + ").";
                default:
                    return "L.H.S and R.H.S evaluate to the exact same numerical value.";
            }
        } else {
            return "L.H.S evaluates to " + lhsVal + " while R.H.S evaluates to " + rhsVal + ". " +
                   "This operation is NOT valid under the " + property + " (e.g., non-commutative/non-associative operators like subtraction or division).";
        }
    }
}