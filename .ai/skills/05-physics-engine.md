# Physics Engine Skill

Every Physics calculation must be unit-aware.

For every formula:

1. Define physical quantities.
2. Define SI base units.
3. Convert input units.
4. Verify dimensional consistency.
5. Validate physical ranges.
6. State assumptions.
7. Calculate intermediate values.
8. Return the result with units.
9. Generate graph or simulation data when useful.
10. Explain numerical limitations.

Do not return a bare number.

Example assumptions:

- Air resistance ignored.
- Gravitational acceleration is constant.
- The system is closed.
- Temperature is measured in Kelvin.
- The conductor follows Ohm's law.