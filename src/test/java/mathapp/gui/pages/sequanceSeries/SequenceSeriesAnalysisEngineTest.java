package mathapp.gui.pages.sequanceSeries;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SequenceSeriesAnalysisEngineTest {

    private static final List<Double> SQUARES_PLUS_ONE = List.of(2.0, 5.0, 10.0, 17.0);

    @Test
    void buildsFiniteDifferenceTable() {
        assertEquals(List.of(
                List.of(2.0, 5.0, 10.0, 17.0),
                List.of(3.0, 5.0, 7.0),
                List.of(2.0, 2.0),
                List.of(0.0)), SequenceAnalysisEngine.differenceTable(SQUARES_PLUS_ONE));
    }

    @Test
    void derivesAndEvaluatesNthTermFromFiniteDifferences() {
        assertEquals("a_n = 2 + 3 * C(n - 1, 1) + 2 * C(n - 1, 2)",
                SequenceAnalysisEngine.formatNthTermFormula(SQUARES_PLUS_ONE));
        assertEquals(26.0, SequenceAnalysisEngine.termAt(SQUARES_PLUS_ONE, 5), 1e-9);
    }

    @Test
    void derivesAndEvaluatesPartialSeriesSum() {
        assertEquals("S_n = 2 * C(n, 1) + 3 * C(n, 2) + 2 * C(n, 3)",
                SeriesAnalysisEngine.formatPartialSumFormula(SQUARES_PLUS_ONE));
        assertEquals(60.0, SeriesAnalysisEngine.sumThrough(SQUARES_PLUS_ONE, 5), 1e-9);
    }

        @Test
        void usesGeometricFormulaForGeometricSequences() {
                List<Double> terms = List.of(3.0, 6.0, 12.0);

                assertEquals(48.0, SequenceAnalysisEngine.termAt(terms, 5), 1e-9);
                assertEquals(93.0, SeriesAnalysisEngine.sumThrough(terms, 5), 1e-9);
                assertTrue(SequenceAnalysisEngine.formatNthTermFormula(terms).contains("2^(n - 1)"));
        }

        @Test
        void usesRecurrenceForFibonacciLikeSequences() {
                List<Double> terms = List.of(1.0, 1.0, 2.0, 3.0);

                assertEquals(8.0, SequenceAnalysisEngine.termAt(terms, 6), 1e-9);
                assertEquals(20.0, SeriesAnalysisEngine.sumThrough(terms, 6), 1e-9);
                assertTrue(SequenceAnalysisEngine.formatNthTermFormula(terms).contains("a_(n - 1) + a_(n - 2)"));
        }

        @Test
        void controllerRoutesNthTermAndSeriesOperations() {
                PatternsAndSequencesController controller = new PatternsAndSequencesController();

                assertTrue(controller.evaluateProperty(
                                PatternsAndSequencesController.Domain2Property.FIND_NTH_TERM,
                                "2, 5, 10, 17", 5).contains("a_5 = 26.0"));
                assertTrue(controller.evaluateProperty(
                                PatternsAndSequencesController.Domain2Property.SUM_SERIES_TO_N,
                                "2, 5, 10, 17", 5).contains("S_5 = 60.0"));
        }

                    @Test
                    void controllerUsesSuppliedAnalyzerForAnalysisAndPredictions() {
                        PatternsAndSequencesController controller = new PatternsAndSequencesController();

                        String analysis = controller.evaluateProperty(
                                PatternsAndSequencesController.Domain2Property.ANALYZE_SEQUENCE_PATTERN,
                                "2, 5, 11, 23, 47");
                        String predictions = controller.evaluateProperty(
                                PatternsAndSequencesController.Domain2Property.PREDICT_NEXT_TERMS,
                                "2, 5, 11, 23, 47");

                        assertTrue(analysis.contains("AFFINE_RECURRENCE"));
                        assertTrue(analysis.contains("General term:"));
                        assertTrue(predictions.contains("[95.0, 191.0, 383.0]"));
                    }

    @Test
    void rejectsInvalidTermsAndTermIndexes() {
        assertThrows(IllegalArgumentException.class,
                () -> SequenceAnalysisEngine.differenceTable(List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> SequenceAnalysisEngine.termAt(SQUARES_PLUS_ONE, 0));
        assertThrows(IllegalArgumentException.class,
                () -> SeriesAnalysisEngine.sumThrough(SQUARES_PLUS_ONE, 0));
    }
}