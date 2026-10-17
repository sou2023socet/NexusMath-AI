package mathapp.gui.pages.numberProperties;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import mathapp.gui.api.MathServiceClient;
import mathapp.gui.auth.AuthContext;
import mathapp.logging.MathLogger;

public class NumberPropertiesController {

    @FXML private TextField inputField;
    @FXML private ComboBox<NumberProperty> propertyCombo;
    @FXML private TextArea resultArea;
    @FXML private Button actionCheckButton;
    @FXML private Button actionClearButton;
    private final MathServiceClient mathServiceClient = new MathServiceClient();

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
        String inputText = inputField.getText() == null ? "" : inputField.getText().trim();
        NumberProperty selectedProperty = propertyCombo.getValue();

        if (selectedProperty == null) {
            resultArea.setText("❌ Error: Please select a number property.");
            return;
        }

        MathLogger.operationStarted("CHECK_PROPERTY", selectedProperty + " | input=" + inputText);
        if (inputText.isEmpty()) {
            MathLogger.operationFailed("CHECK_PROPERTY", "Empty input");
            resultArea.setText("❌ Error: Please enter a number or a list of numbers (comma-separated).");
            return;
        }

        try {
            String result = mathServiceClient.evaluateNumberProperty(
                    selectedProperty.name(), inputText, AuthContext.getState().getToken());
            resultArea.setText(result);
            MathLogger.operationSuccess("CHECK_PROPERTY", selectedProperty + " completed remotely");
        } catch (Exception e) {
            MathLogger.operationFailed("CHECK_PROPERTY", selectedProperty + " failed", e);
            resultArea.setText("❌ Error: " + e.getMessage());
        }
    }

    @FXML
    void handleClear(ActionEvent event) {
        inputField.clear();
        resultArea.clear();
        propertyCombo.getSelectionModel().selectFirst();
    }

}
