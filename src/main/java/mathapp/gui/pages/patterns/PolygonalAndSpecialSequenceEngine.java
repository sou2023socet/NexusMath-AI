package mathapp.gui.pages.patterns;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Specialized mathematical engine for generating figurative/polygonal numbers,
 * Catalan sequences, and combinatorial Pascal row structures.
 */
public class PolygonalAndSpecialSequenceEngine {

    /**
     * Generates $n$ terms of an $s$-gonal (polygonal) number sequence.
     * Formula: $P_s(n) = \frac{(s - 2)n^2 - (s - 4)n}{2}$
     *
     * @param sides Number of polygon sides $s \ge 3$ (e.g., 3=Triangular, 4=Square, 5=Pentagonal)
     * @param count Number of sequence terms to generate ($n \ge 1$)
     */
    public static List<BigInteger> generatePolygonalNumbers(int sides, int count) {
        if (sides < 3) {
            throw new IllegalArgumentException("Polygonal numbers require at least 3 sides.");
        }
        if (count < 1) {
            throw new IllegalArgumentException("Term count must be at least 1.");
        }

        List<BigInteger> sequence = new ArrayList<>();
        BigInteger s = BigInteger.valueOf(sides);
        BigInteger sMinus2 = s.subtract(BigInteger.valueOf(2));
        BigInteger sMinus4 = s.subtract(BigInteger.valueOf(4));
        BigInteger two = BigInteger.valueOf(2);

        for (long n = 1; n <= count; n++) {
            BigInteger nBig = BigInteger.valueOf(n);
            BigInteger term1 = sMinus2.multiply(nBig.pow(2));
            BigInteger term2 = sMinus4.multiply(nBig);
            BigInteger pN = term1.subtract(term2).divide(two);
            sequence.add(pN);
        }

        return sequence;
    }

    /**
     * Generates $n$ terms of the Catalan Number sequence.
     * Formula: $C_n = \frac{1}{n+1} \binom{2n}{n} = \frac{(2n)!}{(n+1)!\,n!}$
     */
    public static List<BigInteger> generateCatalanNumbers(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Term count must be at least 1.");
        }

        List<BigInteger> catalanList = new ArrayList<>();
        for (int n = 0; n < count; n++) {
            catalanList.add(calculateCatalanNumber(n));
        }
        return catalanList;
    }

    private static BigInteger calculateCatalanNumber(int n) {
        BigInteger num = factorial(2 * n);
        BigInteger den = factorial(n + 1).multiply(factorial(n));
        return num.divide(den);
    }

    /**
     * Generates $r$ rows of Pascal's Triangle.
     */
    public static List<List<BigInteger>> generatePascalsTriangle(int rows) {
        if (rows < 1) {
            throw new IllegalArgumentException("Row count must be at least 1.");
        }

        List<List<BigInteger>> triangle = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            List<BigInteger> row = new ArrayList<>();
            for (int j = 0; j <= i; j++) {
                if (j == 0 || j == i) {
                    row.add(BigInteger.ONE);
                } else {
                    BigInteger val = triangle.get(i - 1).get(j - 1).add(triangle.get(i - 1).get(j));
                    row.add(val);
                }
            }
            triangle.add(row);
        }
        return triangle;
    }

    /**
     * Generates $n$ terms of the standard Fibonacci sequence.
     */
    public static List<BigInteger> generateFibonacciSequence(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Term count must be at least 1.");
        }

        List<BigInteger> fib = new ArrayList<>();
        fib.add(BigInteger.ZERO);
        if (count == 1) return fib;

        fib.add(BigInteger.ONE);
        for (int i = 2; i < count; i++) {
            fib.add(fib.get(i - 1).add(fib.get(i - 2)));
        }
        return fib;
    }

    private static BigInteger factorial(int n) {
        BigInteger result = BigInteger.ONE;
        for (int i = 2; i <= n; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return result;
    }
}