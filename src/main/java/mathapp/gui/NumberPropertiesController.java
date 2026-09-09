package mathapp.gui;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import mathapp.numberproperties.NumberProperties;
import mathapp.logging.MathLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NumberPropertiesController {

    @FXML private TextField inputField;
    @FXML private ComboBox<String> propertyCombo;
    @FXML private TextArea resultArea;
    @FXML private Button actionCheckButton;
    @FXML private Button actionClearButton;

    @FXML
    public void initialize() {
        MathLogger.screenOpened("NUMBER_PROPERTIES_SCREEN");
        // Populate the property selection dropdown
        propertyCombo.setItems(FXCollections.observableArrayList(
                // 1.1 Number Systems
                "Check Natural Numbers (List)", // done
                "Check Whole Numbers (List)", // done
                "Check Number Is Integers (List)", // done
                "Check Positive Number (List)", // done
                "Check Negative Number", // done
                "Check Rational Numbers (List)", // done
                "Check Irrational Number (List)",// done
                "Check Real Number (List)",// done
                "Complex Number (List)",// done
                "Number Sets & Classification",// add logic
                "Number Line",// add logic
                "Place Value",// add logic
                "Comparing Number",// add logic
                "Ordering Number",// das sorting algortihm will be add
                "Absolute Value",
                "Numerical Distance",// add logic
                "Properties of Numbers",// add logic
                "Closure property",// add logic
                "Commutative property",// add logic
                "Associative property",// add logic
                "Distributive property",// add logic
                "Identity elements",// add logic
                "Inverse elements",// add logic
                "Check Even Number",
                "Check Odd Number",
                "Check Prime Number",
                "Check Composite Number",// add logic
                "Check CoPrime Number",// add logic
                "Consecutive Number",// add logic
                
                // 1.2 Basic Oprations
                // Basic Oprations (Calculator is also covered) 
                "Addition",
                "Subtraction",
                "Multiplication",
                "Division",
                "Order of operations",
                "BODMAS",// need to check for add
                "PEMDAS",// need to check for add
                "Unary plus & minus",
                "Estimation",// add logic not requred but need to be add
                "Approximation",// add logic 
                "Rounding / Round Off",// add logic
                "Significant figures",// add logic
                "Error bounds",// add logic (+- value that should so error) (∣true value−approximation∣≤E)
                "Absolute error",// add logic (Absolute error = |True Value - Approximate Value)
                "Relative error",// add logic (
                "Percentage error",// add logic
                "Numerical expressions",// add logic
                "Nested expressions",// add logic
                
                // 1.3 Fraction and decimals
                "Proper Fractions",
                "Improper Fraction",
                "Mixed Fraction",
                "Equivalent Fraction",
                "Simplifying Fraction",
                "Comparing Frcation",
                "Adding Fraction",
                "Subtracting Fraction",
                "Multiplying Fraction",
                "Dividing Fraction",
                "Decimal Notaction",
                "Terminating Decimal",
                "Recurring Decimal",
                "Converting Fractions to Decimals",
                "Converting Decimals to Fractions",
                "Decimal Approximation",
                "Fractional Expressions",
                
                // 1.4 Factors and multiples
                "Factors",
                "Multiples",
                "Prime Factorisation",
                "Divisibility Rules",
                "Highest Common Divisor",
                "Least Common Multiple",
                "Euclidean Algorithm",
                "Extended Euclidean algorithm",
                "Remainders",
                "Modular Arithmetic foundations",
                "Application of HCF",
                "Application of LCM",
                "Factor Trees",
                
                // 1.5 Number Theory
                "Divisibility",
                "Congruence Modulo n",
                "Modular addition",
                "Modular Subtraction",
                "Modular Multiplication",
                "Modular Exponentiation",
                "Prime Testing",
                "Trial Divison",
                "Sieve of Eratosthenes",
                "Sieve of Sundaram",
                "Fundamental theorem of arithmetic",
                "Euler’s totient function",
                "Divisor-counting function",
                "Sum-of-divisors function",
                "Perfect numbers",
                "Amicable numbers",
                "Chinese remainder theorem",
                "Fermat’s little theorem",
                "Euler’s theorem",
                "Wilson’s theorem",
                "Miller–Rabin primality testing",
                "Integer factorisation",
                "Diophantine equations",
                "Linear Diophantine equations",
                
                // 1.6 Ratio, proportion and variation 
                "Ratio",
                "Equivalent ratios",
                "Simplifying ratios",
                "Comparing ratios",
                "Proportion",
                "Direct proportion",
                "Inverse proportion",
                "Joint variation",
                "Partial variation",
                "Continued proportion",
                "Unitary method",
                "Partnership problems",
                "Mixture problems",
                "Alligation",
                "Scale",
                "Maps",
                "Speed, distance and time",
                "Work and time",
                "Applications of proportion",
                
                // 1.7 Percentages and financial arithmetic 
                
                
                
                
                "Check Any Even in List",
                "Check Any Odd in List",
                "Count Odd Numbers in List",
                "Count Even Numbers in List",
                "Find Factors",
                "Check Perfect Number",
                "Find CoPrimeFactors",
                "Find HCF",
                "Find LCM"
                // add 
//                "Real Number",
//                "Interger",
//                "Negative Number",
//                "Positive Number"
        ));
        propertyCombo.getSelectionModel().selectFirst();
        resultArea.setEditable(false);
    }

    @FXML
    void handleCheck(ActionEvent event) {
        String inputText = inputField.getText().trim();
        String selectedProperty = propertyCombo.getValue();

        MathLogger.operationStarted("CHECK_PROPERTY", selectedProperty + " | input=" + inputText);
        if (inputText.isEmpty()) {
            MathLogger.operationFailed("CHECK_PROPERTY", "Empty input");
            resultArea.setText("❌ Please enter a number or a list of numbers (comma-separated).");
            return;
        }

        try {
            String result = evaluateProperty(inputText, selectedProperty);
            resultArea.setText(result);
            MathLogger.operationSuccess("CHECK_PROPERTY", selectedProperty + " -> " + result.replace("\n", " | "));
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

    private String evaluateProperty(String input, String property) {
        switch (property) {
            case "Check Natural Numbers (List)":
                return checkNaturalList(input);
            case "Check Whole Numbers (List)":
                return checkWholeList(input);
            case "Check Number Is Integers (List)":
            	return checkIntegerList(input);
            case "Check Positive Number (List)":
            	return checkPositiveList(input);
            case "Check Negative Number (List)":
            	return checkNegativeList(input);
            case "Check Rational Numbers (List)":
                return checkRationalList(input);
            case "Check Irrational Number (List)":
            	return checkIrrationalList(input);
            case "Check Real Number (List)":
            	return checkReallList(input);
            case "CHeck Complex Number (List)":
            	return checkComplexNumberList(input);
            	
            	
            	
            	
            	
            	
            	
            	
            	
            	
            	
            case "Check Even Number":
                return checkSingleEven(input);
            case "Check Odd Number":
                return checkSingleOdd(input);
            case "Check Positive Number":
                return checkSinglePositive(input);
            case "Check Negative Number":
                return checkSingleNegative(input);
            case "Check Prime Number":
                return checkSinglePrime(input);
            case "Check Any Even in List":
                return checkAnyEven(input);
            case "Check Any Odd in List":
                return checkAnyOdd(input);
            case "Count Odd Numbers in List":
                return countOdd(input);
            case "Count Even Numbers in List":
                return countEven(input);
            case "Find Factors":
                return findFactors(input);
            case "Check Perfect Number":
            	return isPefectNumber(input);
            case "Find CoPrimeFactors":
            	return findCoPrimeFactors(input);
            case "Find HCF":
                return findHcf(input);
            case "Find LCM":
                return findLcm(input);
            default:
                return "❌ Unknown property selected.";
        }
    }

    // ======================== Single Number Checks ========================

    private String checkSingleEven(String input) {
        double num = Double.parseDouble(input);
        boolean result = NumberProperties.evenNumber(num);
        return formatSingleResult(num + " is " + (result ? "✅ Even" : "❌ Not Even"));
    }

    private String checkSingleOdd(String input) {
        double num = Double.parseDouble(input);
        boolean result = NumberProperties.oddNumber(num);
        return formatSingleResult(num + " is " + (result ? "✅ Odd" : "❌ Not Odd"));
    }

    private String checkSinglePositive(String input) {
        double num = Double.parseDouble(input);
        boolean result = NumberProperties.positive(num);
        return formatSingleResult(num + " is " + (result ? "✅ Positive" : "❌ Negative"));
    }

    private String checkSingleNegative(String input) {
        double num = Double.parseDouble(input);
        boolean result = NumberProperties.negative(num);
        return formatSingleResult(num + " is " + (result ? "✅ Negative" : "❌ Not Negative (Zero or Positive)"));
    }

    private String checkSinglePrime(String input) {
        int num = Integer.parseInt(input);
        boolean result = NumberProperties.prime(num);
        return formatSingleResult(num + " is " + (result ? "✅ Prime" : "❌ Not Prime"));
    }

    // ======================== List Checks ========================

    private String checkNaturalList(String input) {
        ArrayList<Integer> list = parseIntegerList(input);
        boolean result = NumberProperties.isNaturalNumberList(list);
        return formatListResult("Natural Numbers", result, list);
    }

    private String checkWholeList(String input) {
        ArrayList<Integer> list = parseIntegerList(input);
        boolean result = NumberProperties.isWholeNumberList(list);
        return formatListResult("Whole Numbers", result, list);
    }
    
    private String checkIntegerList(String input) {
    	ArrayList<Double> list = parseDoubleList(input);
        boolean result = NumberProperties.isIntergerNumberList(list);
        return formatListResult("Integer Numbers", result, list);
    }
    
    private String checkPositiveList(String input) {
    	ArrayList<Double> list = parseDoubleList(input);
        boolean result = NumberProperties.isPositiveNumberList(list);
        return formatListResult("Postive Numbers", result, list);
    }
    
    private String checkNegativeList(String input) {
    	ArrayList<Double> list = parseDoubleList(input);
        boolean result = NumberProperties.isNegativeNumberList(list);
        return formatListResult("Negative Numbers", result, list);
    }

    private String checkRationalList(String input) {
        ArrayList<Double> list = parseDoubleList(input);
        boolean result = NumberProperties.isRationalNumberList(list);
        return formatListResult("Rational Numbers", result, list);
    }
    
    private String checkIrrationalList(String input) {
        ArrayList<Double> list = parseDoubleList(input);
        boolean result = NumberProperties.isIrrationalNumberList(list);
        return formatListResult("Irrational Numbers", result, list);
    }
    
    private String checkReallList(String input) {
        ArrayList<Double> list = parseDoubleList(input);
        boolean result = NumberProperties.isRealNumberList(list);
        return formatListResult("Real Numbers", result, list);
    }
    
    private String checkComplexNumberList(String input) {
        ArrayList<Double> list = parseDoubleList(input);
        boolean result = NumberProperties.(list);
        return formatListResult("Complex Numbers", result, list);
    }

    private String checkAnyEven(String input) {
        ArrayList<Double> list = parseDoubleList(input);
        boolean result = NumberProperties.anyEvenInList(list);
        return "📋 List: " + list + "\n\n➡️ Contains any Even number? " + (result ? "✅ Yes" : "❌ No");
    }

    private String checkAnyOdd(String input) {
        ArrayList<Double> list = parseDoubleList(input);
        boolean result = NumberProperties.anyOddInList(list);
        return "📋 List: " + list + "\n\n➡️ Contains any Odd number? " + (result ? "✅ Yes" : "❌ No");
    }

    private String countOdd(String input) {
        ArrayList<Double> list = parseDoubleList(input);
        int count = NumberProperties.oddCountInList(list);
        return "📋 List: " + list + "\n\n🔢 Odd count: " + count;
    }

    private String countEven(String input) {
        ArrayList<Double> list = parseDoubleList(input);
        int count = NumberProperties.evenCountInList(list);
        return "📋 List: " + list + "\n\n🔢 Even count: " + count;
    }

    private String findFactors(String input) {
        double num = Double.parseDouble(input);
        List<Integer> factorList = new ArrayList<>();
        factorList = NumberProperties.factors(num, factorList);
        return "🔢 Factors of " + (int) num + ":\n" +
                (factorList.isEmpty() ? "  No factors found (prime number)" : "  " + factorList);
    }
    
    private String isPefectNumber(String input) {
        double num = Double.parseDouble(input);
        List<Integer> factorList = new ArrayList<>();
        factorList = NumberProperties.factors(num, factorList);
        boolean isPerfectNumber = NumberProperties.getIsPerfectNumber();
        return "🔢 Factors of " + (int) num + ":\n" +
                (factorList.isEmpty() ? "  No factors found (prime number)" : "  " + factorList + "\nIs This Perfect Number : " + (isPerfectNumber ? "yes" : "No"));
    }
    
    private String findCoPrimeFactors(String input) {
        double num = Double.parseDouble(input);
        List<Integer> factorList = new ArrayList<>();
        factorList = NumberProperties.coPrimeFactor(num, factorList);
        return "🔢 CoPrime Factors of " + (int) num + ":\n" +
                (factorList.isEmpty() ? "  No CoPrime Factors found (prime number)" : "  " + factorList);
    }

    private String findHcf(String input) {
        ArrayList<Double> list = parseDoubleList(input);
        int result = NumberProperties.hcf(list);
        return "📋 List: " + list + "\n\n📐 HCF = " + result;
    }

    private String findLcm(String input) {
        ArrayList<Double> list = parseDoubleList(input);
        int result = NumberProperties.lcm(list);
        return "📋 List: " + list + "\n\n📐 LCM = " + result;
    }

    // ======================== Parsing Helpers ========================

    private ArrayList<Integer> parseIntegerList(String input) {
        return Arrays.stream(input.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private ArrayList<Double> parseDoubleList(String input) {
        return Arrays.stream(input.split(","))
                .map(String::trim)
                .map(Double::parseDouble)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // ======================== Formatting Helpers ========================

    private String formatSingleResult(String message) {
        return "✅ Result:\n\n" + message;
    }

    private String formatListResult(String propertyName, boolean result, List<?> list) {
        String status = result ? "✅ Yes, all numbers are " + propertyName : "❌ No, not all numbers are " + propertyName;
        return "📋 List: " + list + "\n\n➡️ " + status;
    }
    
    private String checkIrrationalNumber(String input) {
        double num = Double.parseDouble(input);
        boolean result = NumberProperties.irrational(num);
        return formatSingleResult(
            num + " is " + (result ? "✅ Irrational" : "❌Not Irrational")
        );
    }

}

