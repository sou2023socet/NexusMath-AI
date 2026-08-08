package mathapp.apps;

import mathapp.arithmetic.ArithmeticOperations;
import mathapp.arithmetic.ExponentFunctions;

import java.util.Scanner;

/**
 * CalculatorApp provides basic calculator support (sum/subtract/multiply/divide/power).
 *
 * Note: The JavaFX menu already navigates to the calculator screen (multi-equation solver UI)
 * in {@code mathapp.gui.MultiEquationController}. This class adds CLI support and also
 * enables future wiring from UI.
 */
public class CalculatorApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("--- Mathapp Calculator (CLI) ---");
        System.out.println("Examples:");
        System.out.println("  add 2 3");
        System.out.println("  sub 10 4");
        System.out.println("  mul 6 7");
        System.out.println("  div 10 2");
        System.out.println("  pow 2 8");
        System.out.println("Type 'exit' to quit.");

        while (true) {
            System.out.print("calc> ");
            String line = scanner.nextLine();
            if (line == null) continue;
            line = line.trim();
            if (line.equalsIgnoreCase("exit")) {
                System.out.println("Bye!");
                return;
            }
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+");
            if (parts.length != 3) {
                System.out.println("❌ Invalid format. Expected: <op> <a> <b>");
                continue;
            }

            String op = parts[0].toLowerCase();
            double a;
            double b;
            try {
                a = Double.parseDouble(parts[1]);
                b = Double.parseDouble(parts[2]);
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid numbers.");
                continue;
            }

            try {
                double result;
                switch (op) {
                    case "add":
                        result = ArithmeticOperations.sum(a, b);
                        break;
                    case "sub":
                    case "subtract":
                        result = ArithmeticOperations.subtract(a, b);
                        break;
                    case "mul":
                    case "multiply":
                        result = ArithmeticOperations.multiply(a, b);
                        break;
                    case "div":
                    case "divide":
                        result = ArithmeticOperations.divide(a, b);
                        break;
                    case "pow":
                    case "power":
                        result = ExponentFunctions.power(a, b);
                        break;
                    default:
                        System.out.println("❌ Unknown operation: " + op);
                        continue;
                }
                System.out.println("✅ " + op + "(" + a + ", " + b + ") = " + result);
            } catch (Exception e) {
                System.out.println("❌ Calculation error: " + e.getMessage());
            }
        }
    }
}

