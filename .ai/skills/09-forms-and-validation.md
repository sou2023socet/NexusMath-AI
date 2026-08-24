# Form and Validation Skill

Every form must define:

- Field name
- Label
- Type
- Required status
- Minimum value
- Maximum value
- Unit
- Format
- Error message
- Default value
- Help text
- Accessibility label

Validation must exist at two levels:

1. Frontend validation for immediate user feedback.
2. Backend validation as the final authority.

Never trust frontend validation alone.

Examples:

- Matrix rows must be greater than zero.
- Matrix columns must be greater than zero.
- Denominator cannot be zero.
- Probability must be between 0 and 1.
- Temperature in Kelvin cannot be negative.
- Graph vertices must have valid identifiers.
- A sorted-array requirement must be checked before binary search.