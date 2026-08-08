package mathapp.arithmetic;

public class ArithmeticOperations {

    public static double sum(double a, double b) {
        return a + b;
    }

    public static double subtract(double a, double b) {
        return a - b;
    }

    public static double multiply(double a, double b) {
        return a * b;
    }

    public static double divide(double a, double b) {
        if (b == 0) throw new ArithmeticException("Division by zero!");
        return a / b;
    }

    public static double remainder(double a, double b) {
        return a % b;
    }

    public static double square(double a) {
        return a * a;
    }

    public static double cube(double a) {
        return a * a * a;
    }
}

