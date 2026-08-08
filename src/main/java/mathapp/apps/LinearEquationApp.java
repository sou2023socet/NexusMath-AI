package mathapp.apps;

import mathapp.equations.EquationGenerator;
import mathapp.equations.api.EquationSolver;

import java.util.Map;

public class LinearEquationApp {
    public static void main(String[] args) {
        EquationSolver coreSolver = new EquationSolver();
        EquationGenerator generator = new EquationGenerator(coreSolver);

        System.out.println("--- Test Case 1: Standard Variables ---");
        String inputData1 = "a=10 b=20 c=90 d=?";
        String formula1 = "a+b+d=c";
        Map<String, Double> results1 = generator.processAndSolve(inputData1, formula1);
        System.out.println("Final Output Map: " + results1);

        System.out.println("\n--- Test Case 2: Formula Base Planning (Speed/Distance/Time) ---");
        String inputData2 = "speed=50 time=2 distance=?";
        String formula2 = "speed = distance / time";

        Map<String, Double> results2 = generator.processAndSolve(inputData2, formula2);
        System.out.println("Final Output Map: " + results2);
    }
}

