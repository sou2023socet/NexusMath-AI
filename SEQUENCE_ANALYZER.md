# Sequence & Series Analyzer (Java)

A rule-detection engine that takes a numeric series and figures out its
underlying pattern, then exposes nth-term / sum / next-term operations
through a single common interface.

## How to compile & run

```
mvn -DskipTests compile
java -cp target/classes mathapp.gui.pages.sequanceSeries.analyzer.Main
```

## Input types supported

`SequenceAnalyzer.analyze(...)` accepts `double[]`, `int[]`, `long[]`, and
`float[]` directly — no manual conversion needed. Internally everything is
widened to `double` (the engine's one internal numeric type), since formulas
like sums, ratios, and harmonic terms are not guaranteed to stay integers
even when the input is. `int -> double` and `float -> double` widening is
always exact; `long -> double` can lose precision only for magnitudes beyond
2^53 (~9.007e15), which is an inherent limit of `double`'s 53-bit mantissa,
not a bug in the conversion.

## What it detects (direct, in pipeline order)

1. Arithmetic (AP)               — a_n = a + (n-1)d
2. Geometric (GP)                — a_n = a * r^(n-1)
3. Harmonic (HP)                 — reciprocals form an AP
4. Arithmetico-Geometric (AGP)   — a_n = (a+(n-1)d) * r^(n-1)
                                    (detected as the repeated-root case of an
                                    order-2 homogeneous recurrence)
5. Affine recurrence             — a_n = r*a_(n-1) + c   (e.g. 2,5,11,23,47)
6. Linear recurrence             — a_n = c1*a_(n-1) + c2*a_(n-2) (Fibonacci-style)
7. Polynomial, any degree <= 5   — via repeated finite differencing +
                                    Newton's Forward Difference Formula

## The recursive fallback (Section 6 of the design)

If NONE of the direct detectors match the original series, the engine takes
the first-difference series and re-runs the ENTIRE analyzer on it,
recursively (capped at depth 4). If that resolves to a known family, the
original series is reported as DIFFERENCE_REDUCED, with the reconstruction
done via telescoping: a_n = a_1 + sum_{k=1}^{n-1} d_k.

Example baked into Main.java: 10, 11, 11.5, 11.8333..., 12.0833..., 12.2833...
None of the direct detectors fit this. Its difference series is
1, 1/2, 1/3, 1/4, 1/5 — a Harmonic sequence — so the engine reports it as a
difference-reduced Harmonic pattern.

## Package layout

```
mathapp.gui.pages.sequanceSeries.analyzer
  SequenceType.java              enum of every family
  MathUtils.java                 tolerance-safe comparisons, binomial coefficients
  SequenceModel.java             abstract Strategy: nthTerm/sumOfN/generateNext
  DetectionResult.java           type + params + confidence + model + note
  SequenceAnalyzer.java          orchestrator: chain of detectors + recursion
                                  + int[]/long[]/float[]/double[] overloads
  Main.java                      demo covering every family + input type

mathapp.gui.pages.sequanceSeries.analyzer.models
                                one concrete SequenceModel per family
  ArithmeticSequence, GeometricSequence, HarmonicSequence,
  ArithmeticoGeometricSequence, PolynomialSequence,
  LinearRecurrenceSequence, AffineRecurrenceSequence,
  DifferenceReducedSequence, UnclassifiedSequence

mathapp.gui.pages.sequanceSeries.analyzer.detectors
                                one detector per family (Chain of Responsibility)
  ArithmeticDetector, GeometricDetector, HarmonicDetector,
  ArithmeticoGeometricDetector, AffineRecurrenceDetector,
  LinearRecurrenceDetector, PolynomialDetector,
  RecurrenceCoefficientSolver (shared 2x2 solver)
```

## Design notes / known trade-offs

- All floating point comparisons go through `MathUtils.approxEquals`
  (relative tolerance 1e-6) — never `==`. If you feed in hand-typed decimals
  truncated to only a few digits (e.g. `0.333333` instead of `1.0/3`), you
  can trip this tolerance yourself; prefer exact fractions/divisions in test
  data.
- `LinearRecurrenceSequence` computes `nthTerm` by iterating the recurrence
  forward (exact for integer/rational sequences like Fibonacci) rather than
  through the irrational closed-form roots (Binet's formula), which would
  otherwise leak floating-point noise. The closed form is still shown via
  `getGeneralTermFormula()` for its mathematical value.
- Harmonic series has no elementary closed-form sum; `sumOfN` is computed
  numerically there (exact, just not a one-line formula).

## Not yet implemented (planned next iteration, per the design discussion)

- Composite / interleaved-stream detection (e.g. `1,2,5,10,3,4,15,20,...`
  being two or more woven-together sub-sequences)
- Cross-stream relationship detection (stream B = 2x stream A, etc.)
- Piecewise / block-based rule changes
- "Odd one out" anomaly detection within an otherwise clean series

These were scoped in the design conversation but intentionally left out of
this pass so the core engine could be validated first.
