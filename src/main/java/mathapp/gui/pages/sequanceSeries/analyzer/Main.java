package mathapp.gui.pages.sequanceSeries.analyzer;

import java.util.Arrays;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        SequenceAnalyzer analyzer = new SequenceAnalyzer();

        runCase(analyzer, "Arithmetic",              new double[]{2, 5, 8, 11, 14});
        runCase(analyzer, "Geometric",                new double[]{3, 6, 12, 24, 48});
        runCase(analyzer, "Harmonic",                 new double[]{1, 1.0 / 2, 1.0 / 3, 1.0 / 4, 1.0 / 5});
        runCase(analyzer, "Arithmetico-Geometric",    new double[]{1, 4, 12, 32, 80});
        runCase(analyzer, "Polynomial (squares)",     new double[]{1, 4, 9, 16, 25, 36});
        runCase(analyzer, "Polynomial (cubic)",       new double[]{1, 8, 27, 64, 125, 216});
        runCase(analyzer, "Linear Recurrence (Fib)",  new double[]{1, 1, 2, 3, 5, 8, 13});
        runCase(analyzer, "Affine Recurrence",        new double[]{2, 5, 11, 23, 47});
        // 10, 11, 11.5, 11.8333..., 12.0833..., 12.2833...  ->  differences are
        // 1, 1/2, 1/3, 1/4, 1/5  (a HARMONIC series). No direct detector fits
        // the original terms themselves — only the recursive difference-chain
        // (Section 6) catches it, by re-running the full analyzer on the
        // difference series and finding Harmonic there.
        runCase(analyzer, "Difference-chain (hard)",
            new double[]{10.0, 11.0, 11.5, 11.8333333333, 12.0833333333, 12.2833333333});

        runCase(analyzer, "Genuinely random",         new double[]{4, 17, 2, 91, 6});

        System.out.println("\n============================================================");
        System.out.println(" Next-term generation demo (user picks how many next terms)");
        System.out.println("============================================================");
        demoNextTerms(analyzer, new double[]{2, 5, 11, 23, 47}, 3);
        demoNextTerms(analyzer, new double[]{1, 1, 2, 3, 5, 8, 13}, 4);

        System.out.println("\n============================================================");
        System.out.println(" int[] / long[] / float[] input support demo");
        System.out.println("============================================================");

        int[] intSeries = {2, 5, 11, 23, 47};
        DetectionResult intResult = analyzer.analyze(intSeries); // no manual conversion needed
        System.out.println("int[] input   -> " + intResult.getType()
            + "  (a_10 = " + intResult.getModel().nthTerm(10) + ")");

        long[] longSeries = {2L, 5L, 11L, 23L, 47L};
        DetectionResult longResult = analyzer.analyze(longSeries);
        System.out.println("long[] input  -> " + longResult.getType()
            + "  (a_10 = " + longResult.getModel().nthTerm(10) + ")");

        float[] floatSeries = {2f, 5f, 11f, 23f, 47f};
        DetectionResult floatResult = analyzer.analyze(floatSeries);
        System.out.println("float[] input -> " + floatResult.getType()
            + "  (a_10 = " + floatResult.getModel().nthTerm(10) + ")");
    }

    private static void runCase(SequenceAnalyzer analyzer, String label, double[] terms) {
        System.out.println("\n============================================================");
        System.out.println(" " + label + "   series = " + Arrays.toString(terms));
        System.out.println("============================================================");

        DetectionResult result = analyzer.analyze(terms);

        System.out.println("Type        : " + result.getType());
        System.out.printf ("Confidence  : %.2f%n", result.getConfidence());
        System.out.println("Note        : " + result.getNote());

        if (!result.getParameters().isEmpty()) {
            System.out.println("Parameters  :");
            for (Map.Entry<String, Double> e : result.getParameters().entrySet()) {
                System.out.printf("    %-22s = %.6g%n", e.getKey(), e.getValue());
            }
        }

        SequenceModel model = result.getModel();
        if (result.getType() == SequenceType.UNCLASSIFIED || model == null) {
            System.out.println("General term / Sum formulas are unavailable (unclassified).");
            return;
        }

        System.out.println("General term: " + model.getGeneralTermFormula());
        System.out.println("Sum formula : " + model.getSumFormula());

        int n = 10;
        System.out.printf ("a_%d          = %.6g%n", n, model.nthTerm(n));
        System.out.printf ("S_%d          = %.6g%n", n, model.sumOfN(n));
    }

    private static void demoNextTerms(SequenceAnalyzer analyzer, double[] terms, int howMany) {
        DetectionResult result = analyzer.analyze(terms);
        SequenceModel model = result.getModel();
        System.out.println("\nOriginal series : " + Arrays.toString(terms)
            + "   [" + result.getType() + "]");

        if (model == null || result.getType() == SequenceType.UNCLASSIFIED) {
            System.out.println("Cannot generate further terms — pattern unclassified.");
            return;
        }

        double[] next = model.generateNext(howMany);
        double[] full = model.getFullSeriesWithNext(howMany);
        System.out.println("Next " + howMany + " term(s)  : " + Arrays.toString(next));
        System.out.println("Full series     : " + Arrays.toString(full));
    }
}
