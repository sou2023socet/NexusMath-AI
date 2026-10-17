package com.mathapp.auth.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NumberPropertyServiceTest {

    private final NumberPropertyService service = new NumberPropertyService();

    @Test
    void evaluatesPrimeNumbers() {
        assertTrue(service.evaluate("CHECK_PRIME_NUMBER", "7").contains("7 is Prime"));
    }

    @Test
    void evaluatesNumberLists() {
        assertTrue(service.evaluate("CHECK_WHOLE_NUMBERS_LIST", "0, 2, 4")
                .contains("Yes, all numbers are Whole Numbers"));
    }

    @Test
    void rejectsUnsupportedNumberProperties() {
        assertThrows(IllegalArgumentException.class, () -> service.evaluate("UNKNOWN", "2"));
    }

    @Test
    void detectsEvenAndOddNumbersInLists() {
        assertTrue(service.evaluate("CHECK_ANY_EVEN_IN_LIST", "1, 4, 7").contains("Contains any Even number? ✅"));
        assertTrue(service.evaluate("CHECK_ANY_ODD_IN_LIST", "2, 4, 7").contains("Contains any Odd number? ✅"));
    }

    @Test
    void evaluatesFactorAndMultipleOperations() {
        assertTrue(service.evaluate("FACTORS", "6").contains("[1, 2, 3, 6]"));
        assertTrue(service.evaluate("MULTIPLES", "3, 4").contains("[3, 6, 9, 12]"));
    }

    @Test
    void rejectsNullInputsWithoutNullPointerException() {
        assertThrows(IllegalArgumentException.class, () -> service.evaluate(null, "2"));
        assertThrows(IllegalArgumentException.class, () -> service.evaluate("CHECK_PRIME_NUMBER", null));
    }
}
