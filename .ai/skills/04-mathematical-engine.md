# Mathematics Engine Skill

Mathematical operations must be deterministic, testable and traceable.

Every operation must define:

- Mathematical category
- Operation name
- Input variables
- Domain restrictions
- Formula
- Symbolic or numerical mode
- Precision and tolerance
- Intermediate steps
- Final result
- Error conditions
- Known test cases

Use exact representation where possible:

- Integers for integer operations
- Rational values for fractions
- BigDecimal for controlled decimal calculations
- Complex value objects for complex numbers
- Matrix value objects for matrices
- Vector value objects for vectors

Do not use double precision without documenting the expected error.

Examples of domain errors:

- DivisionByZeroException
- InvalidRadicandException
- DomainRestrictionException
- MatrixSingularityException
- ConvergenceFailureException
- UnboundVariableException
- InvalidDimensionException