package mathapp.numberproperties;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class NumberProperties {

    // =========================================================
    // 1. BASIC NUMBER CLASSIFICATIONS & PREDICATES
    // =========================================================

    public static boolean positive(double number) {
        return number >= 0;
    }

    public static boolean negative(double number) {
        return number < 0;
    }

    public static boolean integer(double number) {
        return Double.isFinite(number) && number == Math.floor(number);
    }

    public static boolean oddNumber(double number) {
        return integer(number) && ((long) number) % 2 != 0;
    }

    public static boolean evenNumber(double number) {
        return integer(number) && ((long) number) % 2 == 0;
    }

    public static boolean prime(int number) {
        if (number < 2) return false;
        if (number % 2 == 0) return number == 2;
        for (int i = 3; (long) i * i <= number; i += 2) {
            if (number % i == 0) return false;
        }
        return true;
    }

    public static boolean isCoprime(int a, int b) {
        return findHcf(a, b) == 1;
    }

    public static boolean irrational(double number) {
        if (Double.isNaN(number) || Double.isInfinite(number) || number < 0) {
            return false;
        }
        double sqrt = Math.sqrt(number);
        return sqrt != Math.floor(sqrt);
    }

    public static double absoluteNumber(double number) {
        return Math.abs(number);
    }

    public static boolean isArmstrongNumber(double number) {
        if (number < 0 || !integer(number)) return false;

        long originalNumber = (long) number;
        long temp = originalNumber;
        int digits = String.valueOf(originalNumber).length();
        long sum = 0;

        while (temp > 0) {
            long remainder = temp % 10;
            sum += Math.pow(remainder, digits);
            temp /= 10;
        }
        return sum == originalNumber;
    }

    public static boolean isPerfectNumber(double number) {
        if (!integer(number) || number <= 1) return false;

        int n = (int) number;
        int sum = 1;
        for (int i = 2; i <= n / 2; i++) {
            if (n % i == 0) {
                sum += i;
            }
        }
        return sum == n;
    }
    
    /**
     * Checks if a number is composite.
     * A composite number is a positive integer greater than 1 that is not prime.
     */
    public static boolean isComposite(double number) {
        if (!integer(number) || number <= 3) {
            return false; // Numbers <= 3 (including negative numbers, 0, 1, 2, 3) are not composite
        }
        return !prime((int) number);
    }
    
    /**
     * Checks if a number is a perfect number.
     * A perfect number is a positive integer equal to the sum of its proper positive divisors (excluding itself).
     */
    public static boolean getIsPerfectNumber(double number) {
        return isPerfectNumber(number);
    }

    /**
     * Returns a list of numbers from 1 up to number - 1 that are co-prime with the given number.
     * Two numbers are co-prime if their Greatest Common Divisor (GCD/HCF) is 1.
     */
    public static List<Integer> coPrimeFactor(double number, List<Integer> coPrimeList) {
        if (coPrimeList == null) {
            coPrimeList = new ArrayList<>();
        }
        
        if (!integer(number) || number <= 1) {
            return coPrimeList;
        }

        int n = (int) number;
        for (int i = 1; i < n; i++) {
            if (isCoprime(n, i)) {
                coPrimeList.add(i);
            }
        }
        return coPrimeList;
    }
    
    /**
     * Checks if two numbers are co-prime (coprime).
     * Two integers are coprime if their highest common divisor (HCF/GCD) is 1.
     */
    public static boolean isCoprime(double a, double b) {
        if (!integer(a) || !integer(b)) {
            return false;
        }
        int num1 = (int) Math.abs(a);
        int num2 = (int) Math.abs(b);
        if (num1 == 0 || num2 == 0) return false;
        
        return findHcf(num1, num2) == 1;
    }

    // =========================================================
    // 2. LIST PREDICATES & STATISTICS
    // =========================================================

    public static boolean isNaturalNumberList(List<Integer> numberSeries) {
        if (numberSeries == null || numberSeries.isEmpty()) return false;
        for (int num : numberSeries) {
            if (num <= 0) return false;
        }
        return true;
    }

    public static boolean isWholeNumberList(List<Integer> numberSeries) {
        if (numberSeries == null || numberSeries.isEmpty()) return false;
        for (int number : numberSeries) {
            if (number < 0) return false;
        }
        return true;
    }

    public static boolean isIntergerNumberList(List<Double> numberSeries) {
        if (numberSeries == null || numberSeries.isEmpty()) return false;
        for (double number : numberSeries) {
            if (!integer(number)) return false;
        }
        return true;
    }

    public static boolean isPositiveNumberList(List<Double> numberSeries) {
        if (numberSeries == null || numberSeries.isEmpty()) return false;
        for (double number : numberSeries) {
            if (!positive(number)) return false;
        }
        return true;
    }

    public static boolean isNegativeNumberList(List<Double> numberSeries) {
        if (numberSeries == null || numberSeries.isEmpty()) return false;
        for (double number : numberSeries) {
            if (!negative(number)) return false;
        }
        return true;
    }

    public static boolean isRationalNumberList(List<Double> numberSeries) {
        return numberSeries != null && !numberSeries.isEmpty();
    }

    public static boolean isIrrationalNumberList(List<Double> numberSeries) {
        if (numberSeries == null || numberSeries.isEmpty()) return false;
        for (double number : numberSeries) {
            if (!irrational(number)) return false;
        }
        return true;
    }

    public static boolean isRealNumberList(List<Double> numberSeries) {
        return numberSeries != null && !numberSeries.isEmpty();
    }

    public static boolean anyEvenInList(List<Double> numberSeries) {
        if (numberSeries == null || numberSeries.isEmpty()) return false;
        for (double number : numberSeries) {
            if (evenNumber(number)) return true;
        }
        return false;
    }

    public static boolean anyOddInList(List<Double> numberSeries) {
        if (numberSeries == null || numberSeries.isEmpty()) return false;
        for (double number : numberSeries) {
            if (oddNumber(number)) return true;
        }
        return false;
    }

    public static int oddCountInList(List<Double> numberSeries) {
        if (numberSeries == null || numberSeries.isEmpty()) return 0;
        int count = 0;
        for (double number : numberSeries) {
            if (oddNumber(number)) count++;
        }
        return count;
    }

    public static int evenCountInList(List<Double> numberSeries) {
        if (numberSeries == null || numberSeries.isEmpty()) return 0;
        int count = 0;
        for (double number : numberSeries) {
            if (evenNumber(number)) count++;
        }
        return count;
    }

    // =========================================================
    // 3. FACTORS, MULTIPLES, HCF & LCM
    // =========================================================

    public static List<Integer> factors(double numberFactor, List<Integer> numberFactorList) {
        if (numberFactorList == null) {
            numberFactorList = new ArrayList<>();
        }
        int n = (int) numberFactor;
        for (int i = 1; i < n; i++) {
            if (n % i == 0) {
                numberFactorList.add(i);
            }
        }
        return numberFactorList;
    }

    public static List<Integer> primeFactors(double numberFactor) {
        List<Integer> factors = new ArrayList<>();
        int n = (int) numberFactor;
        if (n < 2) return factors;

        for (int i = 2; i * i <= n; i++) {
            while (n % i == 0) {
                factors.add(i);
                n /= i;
            }
        }
        if (n > 1) {
            factors.add(n);
        }
        return factors;
    }

    private static int findHcf(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return Math.abs(a);
    }

    public static int hcf(List<Double> numberList) {
        if (numberList == null || numberList.isEmpty()) return 0;
        int result = numberList.get(0).intValue();
        for (int i = 1; i < numberList.size(); i++) {
            result = findHcf(result, numberList.get(i).intValue());
            if (result == 1) return 1;
        }
        return result;
    }

    public static int lcm(List<Double> numberList) {
        if (numberList == null || numberList.isEmpty()) return 0;
        int result = numberList.get(0).intValue();
        for (int i = 1; i < numberList.size(); i++) {
            int nextNum = numberList.get(i).intValue();
            if (nextNum == 0 || result == 0) return 0;
            result = (result * nextNum) / findHcf(result, nextNum);
        }
        return result;
    }

    // =========================================================
    // 4. COMPLEX NUMBERS
    // =========================================================

    public static boolean isComplex(double realPart, double imaginaryPart) {
        return Double.isFinite(realPart)
                && Double.isFinite(imaginaryPart)
                && imaginaryPart != 0.0;
    }

    public static String checkComplexNumbers(List<double[]> numbers) {
        if (numbers == null || numbers.isEmpty()) {
            return "No numbers found.";
        }

        StringBuilder result = new StringBuilder();

        for (double[] number : numbers) {
            if (number == null || number.length < 2 || !isComplex(number[0], number[1])) {
                if (result.length() > 0) {
                    result.append(", ");
                }
                if (number == null || number.length < 2) {
                    result.append("Invalid");
                } else {
                    result.append(formatComplex(number[0], number[1]));
                }
            }
        }

        return result.length() == 0
                ? "All numbers are complex."
                : result.toString();
    }

    public static String formatComplex(double realPart, double imaginaryPart) {
        if (imaginaryPart == 0) {
            return String.valueOf(realPart);
        }
        if (realPart == 0) {
            return imaginaryPart + "i";
        }
        return realPart
                + (imaginaryPart > 0 ? " + " : " - ")
                + Math.abs(imaginaryPart)
                + "i";
    }

    public static double[] parseComplexString(String expr) {
        if (expr == null || expr.trim().isEmpty()) {
            return null;
        }

        expr = expr.replaceAll("\\s+", "");

        try {
            double totalReal = 0.0;
            double totalImag = 0.0;
            String[] terms = expr.split("(?=[+-])");

            for (String term : terms) {
                if (term.isEmpty()) continue;

                if (term.contains("i")) {
                    double[] parsedTerm = evaluateITerm(term);
                    totalReal += parsedTerm[0];
                    totalImag += parsedTerm[1];
                } else {
                    totalReal += evaluateValue(term);
                }
            }
            return new double[] { totalReal, totalImag };
        } catch (Exception e) {
            return null;
        }
    }

    private static double[] evaluateITerm(String term) {
        if (term.contains("i^")) {
            String[] parts = term.split("i\\^");
            String coeffStr = parts[0];
            double coeff = 1.0;

            if (coeffStr.equals("-")) coeff = -1.0;
            else if (!coeffStr.isEmpty() && !coeffStr.equals("+")) coeff = evaluateValue(coeffStr);

            int power = Integer.parseInt(parts[1]);
            int mod = Math.floorMod(power, 4);

            switch (mod) {
                case 0: return new double[] { coeff, 0.0 };
                case 1: return new double[] { 0.0, coeff };
                case 2: return new double[] { -coeff, 0.0 };
                case 3: return new double[] { 0.0, -coeff };
            }
        }

        String coeffStr = term.replace("i", "");
        double coeff = 1.0;
        if (coeffStr.equals("-")) coeff = -1.0;
        else if (!coeffStr.isEmpty() && !coeffStr.equals("+")) coeff = evaluateValue(coeffStr);

        return new double[] { 0.0, coeff };
    }

    private static double evaluateValue(String val) {
        if (val.contains("^")) {
            String[] baseExp = val.split("\\^");
            return Math.pow(Double.parseDouble(baseExp[0]), Double.parseDouble(baseExp[1]));
        }
        return Double.parseDouble(val);
    }

    // =========================================================
    // 5. PLACE VALUE & WORDS
    // =========================================================

    public static List<String> placeValue(long number) {
        List<String> result = new ArrayList<>();
        boolean isNegative = number < 0;
        long absNumber = Math.abs(number);

        String numStr = String.valueOf(absNumber);
        int length = numStr.length();

        for (int i = 0; i < length; i++) {
            int digit = Character.getNumericValue(numStr.charAt(i));
            if (digit == 0) continue;

            long placeMultiplier = (long) Math.pow(10, length - 1 - i);
            long value = digit * placeMultiplier;

            result.add(isNegative ? "-" + value : String.valueOf(value));
        }
        return result;
    }

    public static String placeValue(double number) {
        if (Double.isNaN(number) || Double.isInfinite(number)) {
            return "❌ Invalid number";
        }

        String text = BigDecimal.valueOf(number).toPlainString();
        boolean negative = text.startsWith("-");
        if (negative) {
            text = text.substring(1);
        }

        String[] parts = text.split("\\.", -1);
        String whole = parts[0];
        String decimal = parts.length > 1 ? parts[1] : "";

        StringBuilder result = new StringBuilder();
        if (negative) {
            result.append("Sign: Negative (-)\n\n");
        }

        String[] wholePlaces = {
            "Ones", "Tens", "Hundreds", "Thousands", "Ten-Thousands", "Hundred-Thousands",
            "Millions", "Ten-Millions", "Hundred-Millions", "Billions", "Ten-Billions", 
            "Hundred-Billions", "Trillions"
        };

        for (int i = 0; i < whole.length(); i++) {
            int digit = whole.charAt(i) - '0';
            int position = whole.length() - 1 - i;
            String place = position < wholePlaces.length ? wholePlaces[position] : "10^" + position;

            if (result.length() > 0) result.append("\n");
            result.append(digit).append(" → ").append(place);
        }

        String[] decimalPlaces = {
            "Tenths", "Hundredths", "Thousandths", "Ten-Thousandths", 
            "Hundred-Thousandths", "Millionths", "Ten-Millionths", "Hundred-Millionths"
        };

        if (!decimal.isEmpty()) {
            result.append("\n\nDecimal places:");
            for (int i = 0; i < decimal.length(); i++) {
                int digit = decimal.charAt(i) - '0';
                String place = i < decimalPlaces.length ? decimalPlaces[i] : "10^-" + (i + 1);
                result.append("\n").append(digit).append(" → ").append(place);
            }
        }

        return result.toString();
    }

    public static String convertToWords(long number) {
        if (number == 0) return "Zero";
        if (number < 0) return "Negative " + convertToWords(Math.abs(number));

        String[] units = {
            "", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
            "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen",
            "seventeen", "eighteen", "nineteen"
        };
        String[] tens = {
            "", "", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"
        };

        StringBuilder words = new StringBuilder();

        if ((number / 1000000000) > 0) {
            words.append(convertToWords(number / 1000000000)).append(" billion ");
            number %= 1000000000;
        }
        if ((number / 1000000) > 0) {
            words.append(convertToWords(number / 1000000)).append(" million ");
            number %= 1000000;
        }
        if ((number / 1000) > 0) {
            words.append(convertToWords(number / 1000)).append(" thousand ");
            number %= 1000;
        }
        if ((number / 100) > 0) {
            words.append(convertToWords(number / 100)).append(" hundred ");
            number %= 100;
        }
        if (number > 0) {
            if (words.length() > 0) words.append("and ");
            if (number < 20) {
                words.append(units[(int) number]);
            } else {
                words.append(tens[(int) (number / 10)]);
                if ((number % 10) > 0) {
                    words.append("-").append(units[(int) (number % 10)]);
                }
            }
        }

        String result = words.toString().trim();
        return Character.toUpperCase(result.charAt(0)) + result.substring(1);
    }

    public static String outputHandler(String digitLabel, String words, List<String> placeValues) {
        StringBuilder sb = new StringBuilder();
        sb.append(digitLabel).append("\n").append(words).append("\n");

        if (placeValues == null || placeValues.isEmpty()) {
            sb.append("0");
        } else {
            sb.append(String.join(" + ", placeValues));
        }

        return sb.toString();
    }

    // =========================================================
    // 6. ORDERING, COMPARISON & DISTANCE
    // =========================================================

    public static int compareNumbers(double first, double second) {
        if (!Double.isFinite(first) || !Double.isFinite(second)) {
            throw new IllegalArgumentException("Only finite real numbers can be compared.");
        }
        return Double.compare(first, second);
    }

    public static ArrayList<Double> orderAscending(List<Double> numbers) {
        ArrayList<Double> result = new ArrayList<>();
        if (numbers == null) return result;

        for (Double number : numbers) {
            if (number == null || !Double.isFinite(number)) {
                throw new IllegalArgumentException("Ordering requires finite real numbers.");
            }
            result.add(number);
        }
        result.sort(Double::compare);
        return result;
    }

    public static ArrayList<Double> orderDescending(List<Double> numbers) {
        ArrayList<Double> result = new ArrayList<>();
        if (numbers == null) return result;

        for (Double number : numbers) {
            if (number == null || !Double.isFinite(number)) {
                throw new IllegalArgumentException("Ordering requires finite real numbers.");
            }
            result.add(number);
        }
        result.sort((a, b) -> Double.compare(b, a));
        return result;
    }

    public static double numericalDistance(double first, double second) {
        if (!Double.isFinite(first) || !Double.isFinite(second)) {
            throw new IllegalArgumentException("Distance requires finite real numbers.");
        }
        return Math.abs(first - second);
    }

    public static String numberLine(double number) {
        if (!Double.isFinite(number)) {
            return "❌ Not a real number";
        }
        if (number < 0) return "⬅ Left of 0";
        if (number > 0) return "Right of 0 ➡";
        return "📍 At 0";
    }

    // =========================================================
    // 7. ALGEBRAIC PROPERTIES
    // =========================================================

    public static List<String> classifyNumber(double number) {
        List<String> result = new ArrayList<>();
        if (!Double.isFinite(number)) return result;

        result.add("Complex");
        result.add("Real");
        result.add("Rational");

        if (number == Math.floor(number)) {
            result.add("Integer");
            if (number >= 0) result.add("Whole");
            if (number > 0) result.add("Natural");
        }
        return result;
    }

    public static List<String> propertiesOfNumber(double number) {
        List<String> result = new ArrayList<>();
        if (!Double.isFinite(number)) return result;

        result.add("Real");
        result.add("Rational");

        if (integer(number)) {
            result.add("Integer");
            if (number >= 0) result.add("Whole");
            if (number > 0) result.add("Natural");
            if (number != 0) result.add("Signed Integer");
        }

        if (number == 0) result.add("Zero");
        if (number > 0) result.add("Positive");
        if (number < 0) result.add("Negative");

        if (number != 0 && integer(number)) {
            result.add(evenNumber(number) ? "Even" : "Odd");
        }

        return result;
    }

    public static boolean closureProperty(List<Double> numbers, String operation) {
        if (numbers == null || numbers.isEmpty() || operation == null) return false;

        for (Double a : numbers) {
            if (a == null || !Double.isFinite(a)) return false;

            for (Double b : numbers) {
                if (b == null || !Double.isFinite(b)) return false;

                double result;
                switch (operation.trim()) {
                    case "+": result = a + b; break;
                    case "-": result = a - b; break;
                    case "*": result = a * b; break;
                    case "/":
                        if (b == 0) return false;
                        result = a / b;
                        break;
                    default:
                        throw new IllegalArgumentException("Supported operations: +, -, *, /");
                }

                boolean found = false;
                for (Double value : numbers) {
                    if (value != null && Double.compare(value, result) == 0) {
                        found = true;
                        break;
                    }
                }
                if (!found) return false;
            }
        }
        return true;
    }

    public static boolean commutativeProperty(List<Double> numbers, String operation) {
        if (numbers == null || numbers.isEmpty() || operation == null) return false;
        if (!operation.equals("+") && !operation.equals("*")) return false;

        for (Double a : numbers) {
            if (a == null || !Double.isFinite(a)) return false;
            for (Double b : numbers) {
                if (b == null || !Double.isFinite(b)) return false;

                double first = operation.equals("+") ? a + b : a * b;
                double second = operation.equals("+") ? b + a : b * a;

                if (Double.compare(first, second) != 0) return false;
            }
        }
        return true;
    }

    public static boolean associativeProperty(List<Double> numbers, String operation) {
        if (numbers == null || numbers.size() < 3 || operation == null) return false;
        if (!operation.equals("+") && !operation.equals("*")) return false;

        for (Double a : numbers) {
            if (a == null || !Double.isFinite(a)) return false;
            for (Double b : numbers) {
                if (b == null || !Double.isFinite(b)) return false;
                for (Double c : numbers) {
                    if (c == null || !Double.isFinite(c)) return false;

                    double left = operation.equals("+") ? (a + b) + c : (a * b) * c;
                    double right = operation.equals("+") ? a + (b + c) : a * (b * c);

                    double tolerance = 1e-12 * Math.max(1.0, Math.max(Math.abs(left), Math.abs(right)));
                    if (Math.abs(left - right) > tolerance) return false;
                }
            }
        }
        return true;
    }

    public static boolean distributiveProperty(List<Double> numbers, String operation) {
        if (numbers == null || numbers.size() < 3 || operation == null) return false;
        if (!operation.equals("+") && !operation.equals("-")) return false;

        for (Double a : numbers) {
            if (a == null || !Double.isFinite(a)) return false;
            for (Double b : numbers) {
                if (b == null || !Double.isFinite(b)) return false;
                for (Double c : numbers) {
                    if (c == null || !Double.isFinite(c)) return false;

                    double left = operation.equals("+") ? a * (b + c) : a * (b - c);
                    double right = operation.equals("+") ? (a * b) + (a * c) : (a * b) - (a * c);

                    double tolerance = 1e-12 * Math.max(1.0, Math.max(Math.abs(left), Math.abs(right)));
                    if (Math.abs(left - right) > tolerance) return false;
                }
            }
        }
        return true;
    }

    public static boolean identityElement(List<Double> numbers, String operation) {
        if (numbers == null || numbers.isEmpty() || operation == null) return false;

        double identity;
        if (operation.equals("+")) identity = 0.0;
        else if (operation.equals("*")) identity = 1.0;
        else return false;

        boolean identityExists = false;
        for (Double value : numbers) {
            if (value != null && Double.compare(value, identity) == 0) {
                identityExists = true;
                break;
            }
        }
        if (!identityExists) return false;

        for (Double value : numbers) {
            if (value == null || !Double.isFinite(value)) return false;

            double result = operation.equals("+") ? value + identity : value * identity;
            if (Double.compare(result, value) != 0) return false;
        }
        return true;
    }

    public static boolean inverseElement(List<Double> numbers, String operation) {
        if (numbers == null || numbers.isEmpty() || operation == null) return false;
        if (!operation.equals("+") && !operation.equals("*")) return false;

        double identity = operation.equals("+") ? 0.0 : 1.0;

        for (Double a : numbers) {
            if (a == null || !Double.isFinite(a)) return false;

            boolean inverseExists = false;
            for (Double b : numbers) {
                if (b == null || !Double.isFinite(b)) continue;

                double result = operation.equals("+") ? a + b : a * b;
                if (Math.abs(result - identity) < 1e-12) {
                    inverseExists = true;
                    break;
                }
            }
            if (!inverseExists) return false;
        }
        return true;
    }
    
    // =========================================================
    // 8. CONSECUTIVE NUMBERS, PRIME FACTORISATION & EUCLIDEAN ALGORITHMS
    // =========================================================

    /**
     * Checks if a list of numbers forms a consecutive sequence.
     */
    public static boolean isConsecutive(List<Double> numberSeries) {
        if (numberSeries == null || numberSeries.size() < 2) return false;

        // Verify all numbers are integers
        for (double num : numberSeries) {
            if (!integer(num)) return false;
        }

        List<Double> sorted = orderAscending(numberSeries);
        for (int i = 0; i < sorted.size() - 1; i++) {
            if (sorted.get(i + 1) - sorted.get(i) != 1.0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Generates the prime factorisation expression with powers (e.g., "12 = 2^2 × 3").
     */
    public static String primeFactorisationFormatted(double number) {
        if (!integer(number) || number < 2) {
            return "Prime factorisation is defined for integers greater than 1.";
        }

        int n = (int) number;
        int original = n;
        List<String> factors = new ArrayList<>();

        for (int i = 2; i * i <= n; i++) {
            int count = 0;
            while (n % i == 0) {
                count++;
                n /= i;
            }
            if (count > 0) {
                factors.add(count == 1 ? String.valueOf(i) : i + "^" + count);
            }
        }
        if (n > 1) {
            factors.add(String.valueOf(n));
        }

        return original + " = " + String.join(" × ", factors);
    }

    /**
     * Performs the Euclidean Algorithm step-by-step to find the GCD/HCF of two integers.
     */
    public static String euclideanAlgorithmSteps(long a, long b) {
        a = Math.abs(a);
        b = Math.abs(b);
        if (a < b) {
            long temp = a;
            a = b;
            b = temp;
        }

        StringBuilder steps = new StringBuilder();
        long stepNum = 1;
        long originalA = a;
        long originalB = b;

        while (b != 0) {
            long quotient = a / b;
            long remainder = a % b;
            steps.append("Step ").append(stepNum++).append(": ")
                 .append(a).append(" = (").append(b).append(" × ").append(quotient)
                 .append(") + ").append(remainder).append("\n");
            a = b;
            b = remainder;
        }

        steps.append("\nGCD(").append(originalA).append(", ").append(originalB).append(") = ").append(a);
        return steps.toString();
    }

    /**
     * Performs the Extended Euclidean Algorithm to find GCD(a, b) and Bezout coefficients (x, y)
     * such that a*x + b*y = GCD(a, b).
     */
    public static String extendedEuclideanAlgorithm(long a, long b) {
        long oldR = a, r = b;
        long oldS = 1, s = 0;
        long oldT = 0, t = 1;

        StringBuilder steps = new StringBuilder();
        steps.append("Finding Coefficients x, y for equation: ").append(a).append("x + ").append(b).append("y = GCD(").append(a).append(", ").append(b).append(")\n\n");

        while (r != 0) {
            long quotient = oldR / r;

            long tempR = oldR - quotient * r;
            oldR = r;
            r = tempR;

            long tempS = oldS - quotient * s;
            oldS = s;
            s = tempS;

            long tempT = oldT - quotient * t;
            oldT = t;
            t = tempT;
        }

        steps.append("GCD = ").append(oldR).append("\n");
        steps.append("Bézout Coefficient x = ").append(oldS).append("\n");
        steps.append("Bézout Coefficient y = ").append(oldT).append("\n\n");
        steps.append("Verification:\n(").append(a).append(" × ").append(oldS).append(") + (")
             .append(b).append(" × ").append(oldT).append(") = ")
             .append((a * oldS) + (b * oldT));

        return steps.toString();
    }

    /**
     * Calculates Quotient, Remainder, and Modular Congruence details for a dividend and divisor.
     */
    public static String calculateRemainderDetails(long dividend, long divisor) {
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero is undefined.");
        }

        long quotient = dividend / divisor;
        long remainder = dividend % divisor;
        
        // Handling negative modular arithmetic
        long modRemainder = (remainder < 0) ? remainder + Math.abs(divisor) : remainder;

        return "Dividend: " + dividend + "\n" +
               "Divisor: " + divisor + "\n" +
               "Quotient: " + quotient + "\n" +
               "Remainder: " + remainder + "\n" +
               "Modular Remainder (" + dividend + " mod " + divisor + "): " + modRemainder;
    }
    
    // =========================================================
    // 9. FACTOR TREE GENERATOR
    // =========================================================

    /**
     * Generates an ASCII visual representation of a Factor Tree for a given number.
     */
    public static String generateFactorTree(double number) {
        if (!integer(number) || number < 2) {
            return "Factor trees are defined for positive integers greater than 1.";
        }

        int n = (int) number;
        StringBuilder tree = new StringBuilder();
        tree.append("Factor Tree for ").append(n).append(":\n\n");
        buildFactorTreeText(n, "", true, tree);
        return tree.toString();
    }

    private static void buildFactorTreeText(int n, String indent, boolean isLast, StringBuilder sb) {
        sb.append(indent);
        if (!indent.isEmpty()) {
            sb.append(isLast ? "└── " : "├── ");
        }
        sb.append(n);

        if (prime(n)) {
            sb.append(" (Prime)\n");
            return;
        }
        sb.append("\n");

        // Find the smallest prime factor to split the tree
        int factor1 = 2;
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) {
                factor1 = i;
                break;
            }
        }
        int factor2 = n / factor1;

        String childIndent = indent + (indent.isEmpty() ? "" : (isLast ? "    " : "│   "));
        buildFactorTreeText(factor1, childIndent, false, sb);
        buildFactorTreeText(factor2, childIndent, true, sb);
    }
}