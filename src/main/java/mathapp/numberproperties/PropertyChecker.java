package mathapp.numberproperties;


import java.util.HashMap;
import java.util.Map;

public class PropertyChecker {
    public static void main(String[] args) {
        NumberPropertyChecker checker = new NumberPropertyChecker();

        Map<String, Double> variables = new HashMap<>();
        variables.put("a", 5.0);
        variables.put("b", 10.0);
        variables.put("c", 2.0);

        // Test 1: Commutative Property (Addition - Valid)
        PropertyCheckResult res1 = checker.checkProperty("a + b = b + a", variables);
        System.out.println(res1);

        // Test 2: Commutative Property (Subtraction - Invalid)
        PropertyCheckResult res2 = checker.checkProperty("a - b = b - a", variables);
        System.out.println(res2);

        // Test 3: Distributive Property (Valid)
        PropertyCheckResult res3 = checker.checkProperty("a * (b + c) = a * b + a * c", variables);
        System.out.println(res3);
    }
}