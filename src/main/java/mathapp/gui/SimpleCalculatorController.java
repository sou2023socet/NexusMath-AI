package mathapp.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import mathapp.arithmetic.ArithmeticOperations;
import mathapp.arithmetic.ExponentFunctions;
import mathapp.logging.MathLogger;

public class SimpleCalculatorController {

    @FXML
    private TextField displayField;

    private double firstNumber = 0;
    private String operator = "";
    private boolean startNewNumber = true;

    @FXML
    public void initialize() {
        displayField.setText("0");
        MathLogger.screenOpened("SIMPLE_CALCULATOR");
    }

    // ==========================
    // Number Buttons
    // ==========================

    @FXML
    private void handleNumber(ActionEvent event) {

        String value = ((Button) event.getSource()).getText();

        if (startNewNumber) {
            displayField.setText(value);
            startNewNumber = false;
        } else {

            if (displayField.getText().equals("0")) {
                displayField.setText(value);
            } else {
                displayField.appendText(value);
            }

        }

    }

    // ==========================
    // Decimal Button
    // ==========================

    @FXML
    private void handleDecimal(ActionEvent event) {

        if (startNewNumber) {
            displayField.setText("0.");
            startNewNumber = false;
            return;
        }

        if (!displayField.getText().contains(".")) {
            displayField.appendText(".");
        }

    }

    // ==========================
    // Operator Buttons
    // ==========================

    @FXML
    private void handleOperator(ActionEvent event) {

        try {

            firstNumber = Double.parseDouble(displayField.getText());

            operator = ((Button) event.getSource()).getText();

            startNewNumber = true;

        } catch (Exception ex) {

            displayField.setText("Error");

            startNewNumber = true;

        }

    }

    // ==========================
    // Equals
    // ==========================

@FXML
    private void handleEquals(ActionEvent event) {

        MathLogger.operationStarted("CALCULATE", firstNumber + " " + operator);
        try {

            double secondNumber = Double.parseDouble(displayField.getText());

            double result;

            switch (operator) {

                case "+":
                    result = ArithmeticOperations.sum(firstNumber, secondNumber);
                    break;

                case "-":
                    result = ArithmeticOperations.subtract(firstNumber, secondNumber);
                    break;

                case "*":
                    result = ArithmeticOperations.multiply(firstNumber, secondNumber);
                    break;

                case "/":
                    result = ArithmeticOperations.divide(firstNumber, secondNumber);
                    break;

                case "%":
                    result = ArithmeticOperations.remainder(firstNumber, secondNumber);
                    break;

                case "^":
                    result = ExponentFunctions.power(firstNumber, secondNumber);
                    break;

                default:
                    result = secondNumber;
                    break;

            }

            displayResult(result);
            MathLogger.operationSuccess("CALCULATE", firstNumber + " " + operator + " " + secondNumber + " = " + result);

            operator = "";

            startNewNumber = true;

        } catch (ArithmeticException ex) {

            MathLogger.operationFailed("CALCULATE", "Arithmetic error: " + ex.getMessage(), ex);
            displayField.setText("Cannot divide by zero");

            startNewNumber = true;

        } catch (Exception ex) {

            MathLogger.operationFailed("CALCULATE", "Calculation error", ex);
            displayField.setText("Error");

            startNewNumber = true;

        }

    }

    // ==========================
    // Clear
    // ==========================

    @FXML
    private void handleClear(ActionEvent event) {

        firstNumber = 0;

        operator = "";

        startNewNumber = true;

        displayField.setText("0");

    }

    // ==========================
    // Backspace
    // ==========================

    @FXML
    private void handleBackspace(ActionEvent event) {

        if (startNewNumber) {
            return;
        }

        String text = displayField.getText();

        if (text.length() > 1) {

            displayField.setText(text.substring(0, text.length() - 1));

        } else {

            displayField.setText("0");

            startNewNumber = true;

        }

    }

    // ==========================
    // Square
    // ==========================

    @FXML
    private void handleSquare(ActionEvent event) {

        try {

            double value = Double.parseDouble(displayField.getText());

            double result = ArithmeticOperations.square(value);

            displayResult(result);

            startNewNumber = true;

        } catch (Exception ex) {

            displayField.setText("Error");

        }

    }

    // ==========================
    // Percent
    // ==========================

    @FXML
    private void handlePercent(ActionEvent event) {

        try {

            firstNumber = Double.parseDouble(displayField.getText());

            operator = "%";

            startNewNumber = true;

        } catch (Exception ex) {

            displayField.setText("Error");

        }

    }

    // ==========================
    // Power
    // ==========================

    @FXML
    private void handlePower(ActionEvent event) {

        try {

            firstNumber = Double.parseDouble(displayField.getText());

            operator = "^";

            startNewNumber = true;

        } catch (Exception ex) {

            displayField.setText("Error");

        }

    }

    // ==========================
    // Display Formatting
    // ==========================

    private void displayResult(double value) {

        if (value == (long) value) {

            displayField.setText(String.valueOf((long) value));

        } else {

            displayField.setText(String.valueOf(value));

        }

    }

}