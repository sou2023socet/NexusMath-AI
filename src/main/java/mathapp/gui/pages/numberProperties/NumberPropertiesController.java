package mathapp.gui.pages.numberProperties;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import mathapp.numberproperties.NumberProperties;
import mathapp.logging.MathLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NumberPropertiesController {

    @FXML private TextField inputField;
    @FXML private ComboBox<NumberProperty> propertyCombo;
    @FXML private TextArea resultArea;
    @FXML private Button actionCheckButton;
    @FXML private Button actionClearButton;

    // ======================== Enum Definition ========================

    public enum NumberProperty {
        // 1.1 Number Systems
        CHECK_NATURAL_NUMBERS_LIST("Check Natural Numbers (List)"),
        CHECK_WHOLE_NUMBERS_LIST("Check Whole Numbers (List)"),
        CHECK_NUMBER_IS_INTEGERS_LIST("Check Number Is Integers (List)"),
        CHECK_POSITIVE_NUMBER_LIST("Check Positive Number (List)"),
        CHECK_NEGATIVE_NUMBER_LIST("Check Negative Number (List)"),
        CHECK_RATIONAL_NUMBERS_LIST("Check Rational Numbers (List)"),
        CHECK_IRRATIONAL_NUMBER_LIST("Check Irrational Number (List)"),
        CHECK_REAL_NUMBER_LIST("Check Real Number (List)"),
        COMPLEX_NUMBER_LIST("Complex Number (List)"),
        PLACE_VALUE("Place Value"),
        
//        COMPARING_NUMBER("Comparing Number"), // skip
//        ORDERING_NUMBER("Ordering Number"), // skip dsa concept
//        ABSOLUTE_VALUE("Absolute Value"),// skip
//        CLOSURE_PROPERTY("Closure property"),// skip
        
        // TODO : ADD IN EQUATION SOLVER TO CHECK LHS & RHS
//        COMMUTATIVE_PROPERTY("Commutative property"),
//        ASSOCIATIVE_PROPERTY("Associative property"),
//        DISTRIBUTIVE_PROPERTY("Distributive property"),
//        IDENTITY_ELEMENTS("Identity elements"),
//        INVERSE_ELEMENTS("Inverse elements"),
        
        CHECK_EVEN_NUMBER("Check Even Number"),
        CHECK_ODD_NUMBER("Check Odd Number"),
        CHECK_POSITIVE_NUMBER("Check Positive Number"),
        CHECK_NEGATIVE_NUMBER("Check Negative Number"),
        CHECK_PRIME_NUMBER("Check Prime Number"),
        CHECK_COMPOSITE_NUMBER("Check Composite Number"),
        CHECK_COPRIME_NUMBER("Check CoPrime Number"),
        CONSECUTIVE_NUMBER("Consecutive Number"),

        // 1.2 Basic Operations (skip covered in calulator and in equation solver)
//        ADDITION("Addition"),
//        SUBTRACTION("Subtraction"),
//        MULTIPLICATION("Multiplication"),
//        DIVISION("Division"),
//        ORDER_OF_OPERATIONS("Order of operations"),
//        BODMAS("BODMAS"),
//        PEMDAS("PEMDAS"),
//        UNARY_PLUS_MINUS("Unary plus & minus"),
//        ESTIMATION("Estimation"),
//        APPROXIMATION("Approximation"),
//        ROUNDING_ROUND_OFF("Rounding / Round Off"),
//        SIGNIFICANT_FIGURES("Significant figures"),
//        ERROR_BOUNDS("Error bounds"),
//        ABSOLUTE_ERROR("Absolute error"),
//        RELATIVE_ERROR("Relative error"),
//        PERCENTAGE_ERROR("Percentage error"),
//        NUMERICAL_EXPRESSIONS("Numerical expressions"),
//        NESTED_EXPRESSIONS("Nested expressions"),

        // 1.3 Fractions and Decimals skip (covered in calculator and equation solver
//        PROPER_FRACTIONS("Proper Fractions"),
//        IMPROPER_FRACTION("Improper Fraction"),
//        MIXED_FRACTION("Mixed Fraction"),
//        EQUIVALENT_FRACTION("Equivalent Fraction"),
//        SIMPLIFYING_FRACTION("Simplifying Fraction"),
//        COMPARING_FRACTION("Comparing Fraction"),
//        ADDING_FRACTION("Adding Fraction"),
//        SUBTRACTING_FRACTION("Subtracting Fraction"),
//        MULTIPLYING_FRACTION("Multiplying Fraction"),
//        DIVIDING_FRACTION("Dividing Fraction"),
//        DECIMAL_NOTATION("Decimal Notation"),
//        TERMINATING_DECIMAL("Terminating Decimal"),
//        RECURRING_DECIMAL("Recurring Decimal"),
//        CONVERTING_FRACTIONS_TO_DECIMALS("Converting Fractions to Decimals"),
//        CONVERTING_DECIMALS_TO_FRACTIONS("Converting Decimals to Fractions"),
//        DECIMAL_APPROXIMATION("Decimal Approximation"),
//        FRACTIONAL_EXPRESSIONS("Fractional Expressions"),

        // 1.4 Factors and Multiples
        FACTORS("Factors"),
        MULTIPLES("Multiples"),
        PRIME_FACTORISATION("Prime Factorisation"),
//        DIVISIBILITY_RULES("Divisibility Rules"),
//        HIGHEST_COMMON_DIVISOR("Highest Common Divisor"),
//        LEAST_COMMON_MULTIPLE("Least Common Multiple"),
        EUCLIDEAN_ALGORITHM("Euclidean Algorithm"),
        EXTENDED_EUCLIDEAN_ALGORITHM("Extended Euclidean algorithm"),
        REMAINDERS("Remainders"),
//        MODULAR_ARITHMETIC_FOUNDATIONS("Modular Arithmetic foundations"),
//        APPLICATION_OF_HCF("Application of HCF"),
//        APPLICATION_OF_LCM("Application of LCM"),
        FACTOR_TREES("Factor Trees"),

        // 1.5 Number Theory
//        DIVISIBILITY("Divisibility"),
//        CONGRUENCE_MODULO_N("Congruence Modulo n"),
//        MODULAR_ADDITION("Modular addition"),
//        MODULAR_SUBTRACTION("Modular Subtraction"),
//        MODULAR_MULTIPLICATION("Modular Multiplication"),
//        MODULAR_EXPONENTIATION("Modular Exponentiation"),
//        PRIME_TESTING("Prime Testing"),
//        TRIAL_DIVISION("Trial Division"),
//        SIEVE_OF_ERATOSTHENES("Sieve of Eratosthenes"),
//        SIEVE_OF_SUNDARAM("Sieve of Sundaram"),
//        FUNDAMENTAL_THEOREM_OF_ARITHMETIC("Fundamental theorem of arithmetic"),
//        EULERS_TOTIENT_FUNCTION("Euler's totient function"),
//        DIVISOR_COUNTING_FUNCTION("Divisor-counting function"),
//        SUM_OF_DIVISORS_FUNCTION("Sum-of-divisors function"),
//        PERFECT_NUMBERS("Perfect numbers"),
//        AMICABLE_NUMBERS("Amicable numbers"),
//        CHINESE_REMAINDER_THEOREM("Chinese remainder theorem"),
//        FERMATS_LITTLE_THEOREM("Fermat's little theorem"),
//        EULERS_THEOREM("Euler's theorem"),
//        WILSONS_THEOREM("Wilson's theorem"),
//        MILLER_RABIN_PRIMALITY_TESTING("Miller-Rabin primality testing"),
//        INTEGER_FACTORISATION("Integer factorisation"),
//        DIOPHANTINE_EQUATIONS("Diophantine equations"),
//        LINEAR_DIOPHANTINE_EQUATIONS("Linear Diophantine equations"),

        // 1.6 Ratio, Proportion and Variation
//        RATIO("Ratio"),
//        EQUIVALENT_RATIOS("Equivalent ratios"),
//        SIMPLIFYING_RATIOS("Simplifying ratios"),
//        COMPARING_RATIOS("Comparing ratios"),
//        PROPORTION("Proportion"),
//        DIRECT_PROPORTION("Direct proportion"),
//        INVERSE_PROPORTION("Inverse proportion"),
//        JOINT_VARIATION("Joint variation"),
//        PARTIAL_VARIATION("Partial variation"),
//        CONTINUED_PROPORTION("Continued proportion"),
//        UNITARY_METHOD("Unitary method"),
//        PARTNERSHIP_PROBLEMS("Partnership problems"),
//        MIXTURE_PROBLEMS("Mixture problems"),
//        ALLIGATION("Alligation"),
//        SCALE("Scale"),
//        MAPS("Maps"),
//        SPEED_DISTANCE_TIME("Speed, distance and time"),
//        WORK_AND_TIME("Work and time"),
//        APPLICATIONS_OF_PROPORTION("Applications of proportion"),

        // 1.7 Percentages and Financial Arithmetic
//        PERCENTAGE_CONVERSION("Percentage conversion"),
//        PERCENTAGE_INCREASE("Percentage increase"),
//        PERCENTAGE_DECREASE("Percentage decrease"),
//        SUCCESSIVE_PERCENTAGE_CHANGES("Successive percentage changes"),
//        PROFIT("Profit"),
//        LOSS("Loss"),
//        COST_PRICE("Cost price"),
//        SELLING_PRICE("Selling price"),
//        MARKED_PRICE("Marked price"),
//        DISCOUNT("Discount"),
//        MULTIPLE_DISCOUNTS("Multiple discounts"),
//        SIMPLE_INTEREST("Simple interest"),
//        COMPOUND_INTEREST("Compound interest"),
//        ANNUAL_COMPOUNDING("Annual compounding"),
//        HALF_YEARLY_COMPOUNDING("Half-yearly compounding"),
//        QUARTERLY_COMPOUNDING("Quarterly compounding"),
//        TAX("Tax"),

        // Additional List Operations
        CHECK_ANY_EVEN_IN_LIST("Check Any Even in List"),
        CHECK_ANY_ODD_IN_LIST("Check Any Odd in List"),
        COUNT_ODD_NUMBERS_IN_LIST("Count Odd Numbers in List"),
        COUNT_EVEN_NUMBERS_IN_LIST("Count Even Numbers in List"),
        FIND_FACTORS("Find Factors"),
        CHECK_PERFECT_NUMBER("Check Perfect Number"),
        FIND_CO_PRIME_FACTORS("Find CoPrimeFactors"),
        FIND_HCF("Find HCF"),
        FIND_LCM("Find LCM");

        private final String displayName;

        NumberProperty(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    // Input types for the handler
    public enum InputType {
        DOUBLE_LIST,
        INTEGER_LIST,
        SINGLE_DOUBLE,
        SINGLE_LONG,
        STRING_LIST,
        PLAIN_STRING
    }

    // ======================== JavaFX Handlers ========================

    @FXML
    public void initialize() {
        MathLogger.screenOpened("NUMBER_PROPERTIES_SCREEN");
        propertyCombo.setItems(FXCollections.observableArrayList(NumberProperty.values()));
        propertyCombo.getSelectionModel().selectFirst();
        resultArea.setEditable(false);
    }

    @FXML
    void handleCheck(ActionEvent event) {
        String inputText = inputField.getText().trim();
        NumberProperty selectedProperty = propertyCombo.getValue();

        MathLogger.operationStarted("CHECK_PROPERTY", selectedProperty + " | input=" + inputText);
        if (inputText.isEmpty()) {
            MathLogger.operationFailed("CHECK_PROPERTY", "Empty input");
            resultArea.setText(handleOutput("Please enter a number or a list of numbers (comma-separated).", true));
            return;
        }

        try {
            String result = evaluateProperty(inputText, selectedProperty);
            resultArea.setText(result);
            MathLogger.operationSuccess("CHECK_PROPERTY", selectedProperty + " -> " + result.replace("\n", " | "));
        } catch (Exception e) {
            MathLogger.operationFailed("CHECK_PROPERTY", selectedProperty + " failed", e);
            resultArea.setText(handleOutput(e.getMessage(), true));
        }
    }

    @FXML
    void handleClear(ActionEvent event) {
        inputField.clear();
        resultArea.clear();
        propertyCombo.getSelectionModel().selectFirst();
    }

    // ======================== Core Evaluation ========================

    private String evaluateProperty(String input, NumberProperty property) throws Exception {
        switch (property) {
            case CHECK_NATURAL_NUMBERS_LIST: {
                ArrayList<Integer> list = handleInput(input, InputType.INTEGER_LIST);
                boolean res = NumberProperties.isNaturalNumberList(list);
                return handleOutput("Natural Numbers", res, list);
            }
            case CHECK_WHOLE_NUMBERS_LIST: {
                ArrayList<Integer> list = handleInput(input, InputType.INTEGER_LIST);
                boolean res = NumberProperties.isWholeNumberList(list);
                return handleOutput("Whole Numbers", res, list);
            }
            case CHECK_NUMBER_IS_INTEGERS_LIST: {
                ArrayList<Double> list = handleInput(input, InputType.DOUBLE_LIST);
                boolean res = NumberProperties.isIntergerNumberList(list);
                return handleOutput("Integer Numbers", res, list);
            }
            case CHECK_POSITIVE_NUMBER_LIST: {
                ArrayList<Double> list = handleInput(input, InputType.DOUBLE_LIST);
                boolean res = NumberProperties.isPositiveNumberList(list);
                return handleOutput("Positive Numbers", res, list);
            }
            case CHECK_NEGATIVE_NUMBER_LIST: {
                ArrayList<Double> list = handleInput(input, InputType.DOUBLE_LIST);
                boolean res = NumberProperties.isNegativeNumberList(list);
                return handleOutput("Negative Numbers", res, list);
            }
            case CHECK_RATIONAL_NUMBERS_LIST: {
                ArrayList<Double> list = handleInput(input, InputType.DOUBLE_LIST);
                boolean res = NumberProperties.isRationalNumberList(list);
                return handleOutput("Rational Numbers", res, list);
            }
            case CHECK_IRRATIONAL_NUMBER_LIST: {
                ArrayList<Double> list = handleInput(input, InputType.DOUBLE_LIST);
                boolean res = NumberProperties.isIrrationalNumberList(list);
                return handleOutput("Irrational Numbers", res, list);
            }
            case CHECK_REAL_NUMBER_LIST: {
                ArrayList<Double> list = handleInput(input, InputType.DOUBLE_LIST);
                boolean res = NumberProperties.isRealNumberList(list);
                return handleOutput("Real Numbers", res, list);
            }
            case COMPLEX_NUMBER_LIST: {
                List<String> tokens = handleInput(input, InputType.STRING_LIST);
                List<double[]> numbers = new ArrayList<>();
                for (String token : tokens) {
                    numbers.add(NumberProperties.parseComplexString(token.trim()));
                }
                return handleOutput(NumberProperties.checkComplexNumbers(numbers));
            }
            case PLACE_VALUE: {
                Long number = handleInput(input, InputType.SINGLE_LONG);
                int digitCount = String.valueOf(Math.abs(number)).length();
                String digitLabel = digitCount + " - digit number";
                String wordRepresentation = NumberProperties.convertToWords(number);
                List<String> placeValues = NumberProperties.placeValue(number);
                return handleOutput(NumberProperties.outputHandler(digitLabel, wordRepresentation, placeValues));
            }
            case FACTOR_TREES: {
                Double num = handleInput(input, InputType.SINGLE_DOUBLE);
                String treeResult = NumberProperties.generateFactorTree(num);
                return handleOutput(treeResult);
            }
            case CHECK_EVEN_NUMBER: {
                Double num = handleInput(input, InputType.SINGLE_DOUBLE);
                boolean res = NumberProperties.evenNumber(num);
                return handleOutput(num + " is " + (res ? "Even" : "Not Even"), !res);
            }
            case CHECK_ODD_NUMBER: {
                Double num = handleInput(input, InputType.SINGLE_DOUBLE);
                boolean res = NumberProperties.oddNumber(num);
                return handleOutput(num + " is " + (res ? "Odd" : "Not Odd"), !res);
            }
            case CHECK_POSITIVE_NUMBER: {
                Double num = handleInput(input, InputType.SINGLE_DOUBLE);
                boolean res = NumberProperties.positive(num);
                return handleOutput(num + " is " + (res ? "Positive" : "Negative"), !res);
            }
            case CHECK_NEGATIVE_NUMBER: {
                Double num = handleInput(input, InputType.SINGLE_DOUBLE);
                boolean res = NumberProperties.negative(num);
                return handleOutput(num + " is " + (res ? "Negative" : "Not Negative (Zero or Positive)"), !res);
            }
            case CHECK_PRIME_NUMBER: {
                Long num = handleInput(input, InputType.SINGLE_LONG);
                boolean res = NumberProperties.prime(num.intValue());
                return handleOutput(num + " is " + (res ? "Prime" : "Not Prime"), !res);
            }
            case CHECK_COMPOSITE_NUMBER: {
                Double num = handleInput(input, InputType.SINGLE_DOUBLE);
                boolean res = NumberProperties.isComposite(num);
                return handleOutput(num + " is " + (res ? "Composite" : "Not Composite"), res);
            }
            case CHECK_COPRIME_NUMBER: {
                ArrayList<Double> list = handleInput(input, InputType.DOUBLE_LIST);
                if (list.size() < 2) {
                    throw new IllegalArgumentException("Please enter two numbers separated by a comma (e.g., 8, 15).");
                }
                double num1 = list.get(0);
                double num2 = list.get(1);
                boolean res = NumberProperties.isCoprime(num1, num2);
                
                String msg = (int) num1 + " and " + (int) num2 + " are " + 
                        (res ? "Co-Prime (GCD = 1)" : "Not Co-Prime (GCD > 1)");
                return handleOutput(msg, res, list);
            }
            case CONSECUTIVE_NUMBER: {
                ArrayList<Double> list = handleInput(input, InputType.DOUBLE_LIST);
                boolean isConsecutive = NumberProperties.isConsecutive(list);
                return handleOutput("Consecutive Number Sequence", isConsecutive, list);
            }

            case PRIME_FACTORISATION: {
                Double num = handleInput(input, InputType.SINGLE_DOUBLE);
                String result = NumberProperties.primeFactorisationFormatted(num);
                return handleOutput(result);
            }

            case EUCLIDEAN_ALGORITHM: {
                ArrayList<Integer> list = handleInput(input, InputType.INTEGER_LIST);
                if (list.size() < 2) {
                    throw new IllegalArgumentException("Please provide two integer numbers separated by a comma (e.g., 252, 105).");
                }
                String steps = NumberProperties.euclideanAlgorithmSteps(list.get(0), list.get(1));
                return handleOutput(steps);
            }

            case EXTENDED_EUCLIDEAN_ALGORITHM: {
                ArrayList<Integer> list = handleInput(input, InputType.INTEGER_LIST);
                if (list.size() < 2) {
                    throw new IllegalArgumentException("Please provide two integer numbers separated by a comma (e.g., 240, 46).");
                }
                String result = NumberProperties.extendedEuclideanAlgorithm(list.get(0), list.get(1));
                return handleOutput(result);
            }

            case REMAINDERS: {
                ArrayList<Integer> list = handleInput(input, InputType.INTEGER_LIST);
                if (list.size() < 2) {
                    throw new IllegalArgumentException("Please enter Dividend and Divisor separated by a comma (e.g., 29, 6).");
                }
                String result = NumberProperties.calculateRemainderDetails(list.get(0), list.get(1));
                return handleOutput(result);
            }
            case CHECK_ANY_EVEN_IN_LIST: {
                ArrayList<Double> list = handleInput(input, InputType.DOUBLE_LIST);
                boolean res = NumberProperties.anyEvenInList(list);
                return handleOutput("Contains any Even number?", !res, list);
            }
            case CHECK_ANY_ODD_IN_LIST: {
                ArrayList<Double> list = handleInput(input, InputType.DOUBLE_LIST);
                boolean res = NumberProperties.anyOddInList(list);
                return handleOutput("Contains any Odd number?", !res, list);
            }
            case COUNT_ODD_NUMBERS_IN_LIST: {
                ArrayList<Double> list = handleInput(input, InputType.DOUBLE_LIST);
                int count = NumberProperties.oddCountInList(list);
                return handleOutput("📋 List: " + list + "\n\n🔢 Odd count: " + count);
            }
            case COUNT_EVEN_NUMBERS_IN_LIST: {
                ArrayList<Double> list = handleInput(input, InputType.DOUBLE_LIST);
                int count = NumberProperties.evenCountInList(list);
                return handleOutput("📋 List: " + list + "\n\n🔢 Even count: " + count);
            }
            case FIND_FACTORS: {
                Double num = handleInput(input, InputType.SINGLE_DOUBLE);
                List<Integer> factorList = NumberProperties.factors(num, new ArrayList<>());
                String msg = "🔢 Factors of " + num.intValue() + ":\n" +
                        (factorList.isEmpty() ? "  No factors found (prime number)" : "  " + factorList);
                return handleOutput(msg);
            }
            case CHECK_PERFECT_NUMBER: {
                Double num = handleInput(input, InputType.SINGLE_DOUBLE);
                List<Integer> factorList = NumberProperties.factors(num, new ArrayList<>());
                boolean isPerfect = NumberProperties.getIsPerfectNumber(num);
                String msg = "🔢 Factors of " + num.intValue() + ":\n" +
                        (factorList.isEmpty() ? "  No factors found" : "  " + factorList) + 
                        "\nIs Perfect Number: " + (isPerfect ? "Yes" : "No");
                return handleOutput(msg);
            }
            case FIND_CO_PRIME_FACTORS: {
                Double num = handleInput(input, InputType.SINGLE_DOUBLE);
                List<Integer> factorList = NumberProperties.coPrimeFactor(num, new ArrayList<>());
                String msg = "🔢 CoPrime Factors of " + num.intValue() + ":\n" +
                        (factorList.isEmpty() ? "  No CoPrime Factors found" : "  " + factorList);
                return handleOutput(msg);
            }
            case FIND_HCF: {
                ArrayList<Double> list = handleInput(input, InputType.DOUBLE_LIST);
                int result = NumberProperties.hcf(list);
                return handleOutput("📋 List: " + list + "\n\n📐 HCF = " + result);
            }
            case FIND_LCM: {
                ArrayList<Double> list = handleInput(input, InputType.DOUBLE_LIST);
                int result = NumberProperties.lcm(list);
                return handleOutput("📋 List: " + list + "\n\n📐 LCM = " + result);
            }
            default:
                return handleOutput("Feature not implemented yet for: " + property.getDisplayName(), true);
        }
    }

    // ======================== Input Handler ========================

    @SuppressWarnings("unchecked")
    private <T> T handleInput(String input, InputType targetType) throws IllegalArgumentException {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Input cannot be empty.");
        }

        String cleaned = input.trim();

        try {
            switch (targetType) {
                case DOUBLE_LIST:
                    return (T) Arrays.stream(cleaned.split(","))
                            .map(String::trim)
                            .map(Double::parseDouble)
                            .collect(Collectors.toCollection(ArrayList::new));

                case INTEGER_LIST:
                    return (T) Arrays.stream(cleaned.split(","))
                            .map(String::trim)
                            .map(Integer::parseInt)
                            .collect(Collectors.toCollection(ArrayList::new));

                case SINGLE_DOUBLE:
                    return (T) Double.valueOf(cleaned);

                case SINGLE_LONG:
                    return (T) Long.valueOf(cleaned);

                case STRING_LIST:
                    return (T) Arrays.stream(cleaned.split(","))
                            .map(String::trim)
                            .collect(Collectors.toList());

                case PLAIN_STRING:
                    return (T) cleaned;

                default:
                    throw new IllegalArgumentException("Unsupported input type provided.");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid numerical input format. Please check your numbers.");
        }
    }

    // ======================== Output Handler ========================

    private String handleOutput(String message) {
        return handleOutput(message, false);
    }

    private String handleOutput(String message, boolean isError) {
        if (isError) {
            return "❌ Error: " + message;
        }
        return "✅ Result:\n\n" + message;
    }

    private String handleOutput(String message, boolean status, Object context) {
        String icon = status ? "✅" : "❌";
        return "📋 Input: " + context + "\n\n➡️ " + message + " " + icon;
    }

    private String handleOutput(String propertyName, boolean status, List<?> list) {
        String statusText = status 
                ? "✅ Yes, all numbers are " + propertyName 
                : "❌ No, not all numbers are " + propertyName;
        return "📋 List: " + list + "\n\n➡️ " + statusText;
    }
}