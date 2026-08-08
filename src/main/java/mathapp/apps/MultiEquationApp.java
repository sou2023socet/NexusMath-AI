package mathapp.apps;

import mathapp.equations.EquationGenerator;
import mathapp.equations.MultipleEquationHandler;
import mathapp.equations.api.EquationSolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MultiEquationApp {

    public static void main(String[] args) {
        EquationSolver coreSolver = new EquationSolver();
        EquationGenerator generator = new EquationGenerator(coreSolver);
        MultipleEquationHandler multiHandler = new MultipleEquationHandler(generator, coreSolver);
        Scanner scanner = new Scanner(System.in);

        displayUserGuide();

        while (true) {
            System.out.println("\n================================================");
            System.out.println("MAIN MENU:");
            System.out.println("1. Run Interactive Equation System Solver");
            System.out.println("2. Run Built-In System Test Case (Speed/Distance/Rate)");
            System.out.println("3. Show Help / User Guide");
            System.out.println("4. Exit");
            System.out.print("Choose an option (1-4): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    runInteractiveSolver(scanner, multiHandler);
                    break;
                case "2":
                    runSystemTestCase(multiHandler);
                    break;
                case "3":
                    displayUserGuide();
                    break;
                case "4":
                    System.out.println("Thank you for using the App! Goodbye.");
                    scanner.close();
                    return;
                default:
                    System.out.println("❌ Invalid choice. Please enter a number between 1 and 4.");
            }
        }
    }

    private static void runInteractiveSolver(Scanner scanner, MultipleEquationHandler multiHandler) {
        System.out.println("\n--- INTERACTIVE EQUATION SOLVER ---");
        
        int totalEquations = 0;
        while (true) {
            System.out.print("How many equations are in your system? ");
            try {
                totalEquations = Integer.parseInt(scanner.nextLine().trim());
                if (totalEquations <= 0) {
                    System.out.println("❌ Please enter a number greater than 0.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid entry. Please enter a valid whole number integer.");
            }
        }

        List<String> userEquations = new ArrayList<>();
        System.out.println("\n--- STEP 1: ENTER EQUATIONS ---");
        System.out.println("ℹ️ Formulas can be written in any direction (e.g., 'a+b=c' or 'c=a+b').");
        System.out.println("ℹ️ Variables are CASE-SENSITIVE ('Speed' and 'speed' are different).");

        for (int i = 1; i <= totalEquations; i++) {
            System.out.print("Enter Equation #" + i + ": ");
            String eqInput = scanner.nextLine().trim();
            
            if (!eqInput.contains("=")) {
                System.out.println("❌ Invalid structure. Must contain an '=' sign. Re-enter this equation.");
                i--; 
                continue;
            }
            if (eqInput.contains("?")) {
                System.out.println("❌ Do not put '?' inside your formula. Set the target in Step 2.");
                i--;
                continue;
            }
            userEquations.add(eqInput);
        }

        System.out.println("\n--- STEP 2: ENTER VARIABLES ---");
        System.out.println("Assign numerical values and specify the target unknown variable.");
        System.out.println("Format Style: a=12 Speed=20 time_1=3 target=?");
        System.out.print("Variables Input string: ");
        String userInputs = scanner.nextLine().trim();

        if (userInputs.isEmpty() || !userInputs.contains("=?")) {
            System.out.println("❌ Input validation error. Remember to explicitly declare your goal target via 'variable=?'");
            return;
        }

        System.out.println("\n--- Processing Interactive System of Equations ---");
        try {
            Map<String, Double> finalState = multiHandler.solveSystem(userEquations, userInputs);
            System.out.println("\n✅ Final Result Processing Output Map:");
            System.out.println(finalState);
        } catch (Exception e) {
            System.out.println("❌ A processing logic breakdown occurred. Check formula formatting spaces.");
        }
    }

    public static void runSystemTestCase(MultipleEquationHandler multiHandler) {
        System.out.println("\n--- Running Built-In System Test Case ---");
        
        List<String> systemOfEquations = new ArrayList<>();
        // Proving that 'any side' equations work perfectly by putting variables on the left or right
        systemOfEquations.add("speed = distance / time");
        systemOfEquations.add("rate * hours = distance");

        String userInputs = "rate=20 hours=3 time=2 speed=?";

        System.out.println("Testing Formulas:");
        for (String eq : systemOfEquations) {
            System.out.println("  • " + eq);
        }
        System.out.println("Testing Inputs: " + userInputs);
        System.out.println("Processing...");

        Map<String, Double> finalState = multiHandler.solveSystem(systemOfEquations, userInputs);

        System.out.println("\nFinal Global Map Returned to Front-End:");
        System.out.println(finalState);
        System.out.println("------------------------------------------");
    }

    private static void displayUserGuide() {
        System.out.println("\n==========================================================================");
        System.out.println("      WELCOME TO THE MULTI-EQUATION MATHEMATICAL SOLVER");
        System.out.println("==========================================================================");
        System.out.println("🎯 WHAT THIS APP DOES:");
        System.out.println("   This app links multiple algebraic formulas together to find a missing number.");
        System.out.println("   If you want to find 'a', but 'a' needs 'b', the engine will automatically");
        System.out.println("   solve for 'b' in another equation first, and then solve for 'a'.");
        System.out.println("\n📖 STEP-BY-STEP USER GUIDE:");
        System.out.println("   [STEP 1: Enter Equations]");
        System.out.println("   Provide formulas linking variables. You can put the target on ANY SIDE.");
        System.out.println("   Variables are CASE-SENSITIVE and can use numbers/underscores (e.g., 'time_1').");
        System.out.println("   Correct Input Example 1 : a + b = c");
        System.out.println("   Correct Input Example 2 : c = a + b");
        System.out.println("   Correct Input Example 3 : Speed = distance / time");
        System.out.println();
        System.out.println("   [STEP 2: Enter Variables & Target]");
        System.out.println("   Tell the app the numbers you already know, separated by standard spaces.");
        System.out.println("   Finally, tell the app what to solve for by setting it to '?'.");
        System.out.println("   Correct Input Example 1 : c=10 d=2 a=?");
        System.out.println("   Correct Input Example 2 : rate=20 hours=3 time=2 Speed=?");
        System.out.println("==========================================================================");
    }
}