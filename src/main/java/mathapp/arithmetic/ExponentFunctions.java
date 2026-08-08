package mathapp.arithmetic;

public class ExponentFunctions {

    // power implementation mirrors the original number_property logic.
    // Note: This is kept as-is structurally; it can be improved later.
    public static double power(double a, double b) {
        double power = 1;
        boolean isNegative = (b < 0);
        boolean isDecimal = (b % 1 != 0);

        if (!isNegative && !isDecimal) {
            while (b != 0) {
                power = ArithmeticOperations.multiply(power, a);
                b = b - 1;
            }
            return power;
        } else if (isNegative && !isDecimal) {
            while (b < 0) {
                power = ArithmeticOperations.multiply(power, a);
                b = b + 1;
            }
            return ArithmeticOperations.divide(1, power);
        } else if (!isNegative && isDecimal) {
            int integerPart = (int) b;
            double fractionalPart = b - integerPart;

            while (integerPart != 0) {
                power = ArithmeticOperations.multiply(power, a);
                integerPart--;
            }
            power = ArithmeticOperations.multiply(power, decimalPower(a, fractionalPart));
            return power;
        } else if (isNegative && isDecimal) {
            b = -b;
            int integerPart = (int) b;
            double fractionalPart = b - integerPart;

            while (integerPart != 0) {
                power = ArithmeticOperations.multiply(power, a);
                integerPart--;
            }

            power = ArithmeticOperations.multiply(power, decimalPower(a, fractionalPart));
            return ArithmeticOperations.divide(1, power);
        } else {
            return power;
        }
    }

    private static double exp(double x) {
        double sum = 1.0;
        double term = 1.0;

        for (int i = 1; i <= 20; i++) {
            term = term * x / i;
            sum += term;
        }

        return sum;
    }

    private static double ln(double x) {
        if (x <= 0) {
            return 0; // undefined
        }

        double y = (x - 1) / (x + 1);
        double yPower = y;
        double sum = 0;

        for (int i = 1; i <= 39; i += 2) {
            sum += yPower / i;
            yPower *= y * y;
        }

        return 2 * sum;
    }

    private static double decimalPower(double a, double b) {
        return exp(b * ln(a));
    }
}

