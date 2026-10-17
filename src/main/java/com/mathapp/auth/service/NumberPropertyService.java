package com.mathapp.auth.service;

import mathapp.numberproperties.NumberProperties;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class NumberPropertyService {

    public String evaluate(String operation, String input) {
        if (operation == null || input == null || input.isBlank()) {
            throw new IllegalArgumentException("A number property and input are required.");
        }
        String value = input.trim();
        return switch (operation) {
            case "CHECK_NATURAL_NUMBERS_LIST" ->
                    listResult("Natural Numbers", NumberProperties.isNaturalNumberList(parseIntegers(value)), parseIntegers(value));
            case "CHECK_WHOLE_NUMBERS_LIST" ->
                    listResult("Whole Numbers", NumberProperties.isWholeNumberList(parseIntegers(value)), parseIntegers(value));
            case "CHECK_NUMBER_IS_INTEGERS_LIST" ->
                    listResult("Integer Numbers", NumberProperties.isIntergerNumberList(parseDoubles(value)), parseDoubles(value));
            case "CHECK_POSITIVE_NUMBER_LIST" ->
                    listResult("Positive Numbers", NumberProperties.isPositiveNumberList(parseDoubles(value)), parseDoubles(value));
            case "CHECK_NEGATIVE_NUMBER_LIST" ->
                    listResult("Negative Numbers", NumberProperties.isNegativeNumberList(parseDoubles(value)), parseDoubles(value));
            case "CHECK_RATIONAL_NUMBERS_LIST" ->
                    listResult("Rational Numbers", NumberProperties.isRationalNumberList(parseDoubles(value)), parseDoubles(value));
            case "CHECK_IRRATIONAL_NUMBER_LIST" ->
                    listResult("Irrational Numbers", NumberProperties.isIrrationalNumberList(parseDoubles(value)), parseDoubles(value));
            case "CHECK_REAL_NUMBER_LIST" ->
                    listResult("Real Numbers", NumberProperties.isRealNumberList(parseDoubles(value)), parseDoubles(value));
            case "COMPLEX_NUMBER_LIST" -> complexNumbers(value);
            case "PLACE_VALUE" -> placeValue(parseLong(value));
            case "FACTOR_TREES" -> result(NumberProperties.generateFactorTree(parseDouble(value)));
            case "CHECK_EVEN_NUMBER" -> predicate(parseDouble(value), NumberProperties.evenNumber(parseDouble(value)), "Even", "Not Even", false);
            case "CHECK_ODD_NUMBER" -> predicate(parseDouble(value), NumberProperties.oddNumber(parseDouble(value)), "Odd", "Not Odd", false);
            case "CHECK_POSITIVE_NUMBER" -> predicate(parseDouble(value), NumberProperties.positive(parseDouble(value)), "Positive", "Negative", false);
            case "CHECK_NEGATIVE_NUMBER" -> predicate(parseDouble(value), NumberProperties.negative(parseDouble(value)), "Negative", "Not Negative (Zero or Positive)", false);
            case "CHECK_PRIME_NUMBER" -> {
                long number = parseLong(value);
                boolean prime = NumberProperties.prime((int) number);
                yield errorOrResult(number + " is " + (prime ? "Prime" : "Not Prime"), !prime);
            }
            case "CHECK_COMPOSITE_NUMBER" -> {
                double number = parseDouble(value);
                yield predicate(number, NumberProperties.isComposite(number), "Composite", "Not Composite", true);
            }
            case "CHECK_COPRIME_NUMBER" -> coprime(value);
            case "CONSECUTIVE_NUMBER" -> {
                List<Double> numbers = parseDoubles(value);
                yield contextualResult("Consecutive Number Sequence",
                        NumberProperties.isConsecutive(numbers), numbers);
            }
            case "PRIME_FACTORISATION" -> result(NumberProperties.primeFactorisationFormatted(parseDouble(value)));
            case "EUCLIDEAN_ALGORITHM" -> {
                List<Integer> numbers = firstTwoIntegers(value);
                yield result(NumberProperties.euclideanAlgorithmSteps(numbers.get(0), numbers.get(1)));
            }
            case "EXTENDED_EUCLIDEAN_ALGORITHM" -> {
                List<Integer> numbers = firstTwoIntegers(value);
                yield result(NumberProperties.extendedEuclideanAlgorithm(numbers.get(0), numbers.get(1)));
            }
            case "REMAINDERS" -> {
                List<Integer> numbers = firstTwoIntegers(value);
                yield result(NumberProperties.calculateRemainderDetails(numbers.get(0), numbers.get(1)));
            }
            case "CHECK_ANY_EVEN_IN_LIST" -> {
                List<Double> numbers = parseDoubles(value);
                yield contextualResult("Contains any Even number?", NumberProperties.anyEvenInList(numbers), numbers);
            }
            case "CHECK_ANY_ODD_IN_LIST" -> {
                List<Double> numbers = parseDoubles(value);
                yield contextualResult("Contains any Odd number?", NumberProperties.anyOddInList(numbers), numbers);
            }
            case "COUNT_ODD_NUMBERS_IN_LIST" -> {
                List<Double> numbers = parseDoubles(value);
                yield result("📋 List: " + numbers + "\n\n🔢 Odd count: " + NumberProperties.oddCountInList(numbers));
            }
            case "COUNT_EVEN_NUMBERS_IN_LIST" -> {
                List<Double> numbers = parseDoubles(value);
                yield result("📋 List: " + numbers + "\n\n🔢 Even count: " + NumberProperties.evenCountInList(numbers));
            }
            case "FIND_FACTORS" -> factors(parseDouble(value));
            case "CHECK_PERFECT_NUMBER" -> perfectNumber(parseDouble(value));
            case "FIND_CO_PRIME_FACTORS" -> coPrimeFactors(parseDouble(value));
            case "FIND_HCF" -> result("📋 List: " + parseDoubles(value) + "\n\n📐 HCF = " + NumberProperties.hcf(parseDoubles(value)));
            case "FIND_LCM" -> result("📋 List: " + parseDoubles(value) + "\n\n📐 LCM = " + NumberProperties.lcm(parseDoubles(value)));
            case "FACTORS" -> factors(parseDouble(value));
            case "MULTIPLES" -> multiples(value);
            default -> throw new IllegalArgumentException("Unsupported number property operation.");
        };
    }

    private String complexNumbers(String input) {
        List<double[]> numbers = Arrays.stream(input.split(","))
                .map(String::trim)
                .map(NumberProperties::parseComplexString)
                .toList();
        return result(NumberProperties.checkComplexNumbers(numbers));
    }

    private String placeValue(long number) {
        int digitCount = String.valueOf(Math.abs(number)).length();
        String digitLabel = digitCount + " - digit number";
        String words = NumberProperties.convertToWords(number);
        List<String> places = NumberProperties.placeValue(number);
        return result(NumberProperties.outputHandler(digitLabel, words, places));
    }

    private String coprime(String input) {
        List<Double> numbers = parseDoubles(input);
        requireTwo(numbers);
        double first = numbers.get(0);
        double second = numbers.get(1);
        boolean coprime = NumberProperties.isCoprime(first, second);
        String message = (int) first + " and " + (int) second + " are "
                + (coprime ? "Co-Prime (GCD = 1)" : "Not Co-Prime (GCD > 1)");
        return contextualResult(message, coprime, numbers);
    }

    private String factors(double number) {
        if (!Double.isFinite(number) || number < 1 || number > Integer.MAX_VALUE
                || number != Math.rint(number)) {
            throw new IllegalArgumentException("Factors require a positive whole number.");
        }
        int integer = (int) number;
        List<Integer> factors = NumberProperties.factors(number, new ArrayList<>());
        factors.add(integer);
        String message = "🔢 Factors of " + integer + ":\n"
                + (factors.isEmpty() ? "  No factors found (prime number)" : "  " + factors);
        return result(message);
    }

    private String multiples(String input) {
        List<Integer> values = parseIntegers(input);
        if (values.isEmpty() || values.size() > 2) {
            throw new IllegalArgumentException("Enter a number and an optional count for its multiples.");
        }
        int number = values.get(0);
        int count = values.size() == 2 ? values.get(1) : 10;
        if (count < 1 || count > 100) {
            throw new IllegalArgumentException("Multiple count must be between 1 and 100.");
        }
        List<Long> multiples = java.util.stream.LongStream.rangeClosed(1, count)
                .map(index -> (long) number * index)
                .boxed()
                .toList();
        return result("First " + count + " multiples of " + number + ":\n" + multiples);
    }

    private String perfectNumber(double number) {
        List<Integer> factors = NumberProperties.factors(number, new ArrayList<>());
        boolean perfect = NumberProperties.getIsPerfectNumber(number);
        String message = "🔢 Factors of " + (int) number + ":\n"
                + (factors.isEmpty() ? "  No factors found" : "  " + factors)
                + "\nIs Perfect Number: " + (perfect ? "Yes" : "No");
        return result(message);
    }

    private String coPrimeFactors(double number) {
        List<Integer> factors = NumberProperties.coPrimeFactor(number, new ArrayList<>());
        String message = "🔢 CoPrime Factors of " + (int) number + ":\n"
                + (factors.isEmpty() ? "  No CoPrime Factors found" : "  " + factors);
        return result(message);
    }

    private String predicate(double value, boolean matches, String positive, String negative, boolean errorWhenMatch) {
        String message = value + " is " + (matches ? positive : negative);
        return errorOrResult(message, matches == errorWhenMatch);
    }

    private String listResult(String property, boolean matches, List<?> numbers) {
        String message = matches
                ? "✅ Yes, all numbers are " + property
                : "❌ No, not all numbers are " + property;
        return "📋 List: " + numbers + "\n\n➡️ " + message;
    }

    private String contextualResult(String message, boolean matches, Object input) {
        return "📋 Input: " + input + "\n\n➡️ " + message + (matches ? " ✅" : " ❌");
    }

    private String errorOrResult(String message, boolean isError) {
        return isError ? "❌ Error: " + message : result(message);
    }

    private String result(String message) {
        return "✅ Result:\n\n" + message;
    }

    private List<Double> parseDoubles(String input) {
        return Arrays.stream(input.split(","))
                .map(String::trim)
                .map(Double::parseDouble)
                .toList();
    }

    private List<Integer> parseIntegers(String input) {
        return Arrays.stream(input.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .toList();
    }

    private List<Integer> firstTwoIntegers(String input) {
        List<Integer> values = parseIntegers(input);
        requireTwo(values);
        return values;
    }

    private double parseDouble(String input) {
        return Double.parseDouble(input);
    }

    private long parseLong(String input) {
        return Long.parseLong(input);
    }

    private void requireTwo(List<?> values) {
        if (values.size() < 2) {
            throw new IllegalArgumentException("Please enter at least two comma-separated values.");
        }
    }
}
