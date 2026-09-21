package mathapp.gui.pages.patterns;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SequencePatternEngineTest {

    @Test
    void testArithmeticSequenceRecognition() {
        double[] terms = {3.0, 7.0, 11.0, 15.0, 19.0};
        SequencePatternResult result = SequencePatternEngine.analyzeSequence(terms, 3);

        assertEquals("Arithmetic Sequence", result.patternType());
        assertTrue(result.isExact());
        assertEquals("4.0000n + -1.0000", result.closedFormFormula());
        assertEquals(23.0, result.predictedNextTerms().get(0), 1e-6);
    }

    @Test
    void testGeometricSequenceRecognition() {
        double[] terms = {2.0, 6.0, 18.0, 54.0};
        SequencePatternResult result = SequencePatternEngine.analyzeSequence(terms, 2);

        assertEquals("Geometric Sequence", result.patternType());
        assertTrue(result.isExact());
        assertEquals(162.0, result.predictedNextTerms().get(0), 1e-6);
    }
}