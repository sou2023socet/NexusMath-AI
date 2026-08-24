# Coding Standards Skill

Backend:

- Use Java 21.
- Use clear class and method names.
- Prefer immutable DTOs and value objects.
- Use records where suitable.
- Avoid static global state.
- Avoid business logic in controllers.
- Avoid magic numbers.
- Use explicit domain exceptions.
- Validate all external input.
- Use BigDecimal where precision is important.
- Document numerical tolerance decisions.
- Do not silently catch exceptions.
- Do not return null when a meaningful result or error is possible.

Frontend:

- Use Angular standalone components.
- Prefer OnPush change detection.
- Use typed reactive forms.
- Keep components focused.
- Put HTTP calls in services.
- Put state management in dedicated stores or services.
- Do not duplicate validation rules in multiple components.
- Use accessible labels and keyboard navigation.
- Keep visualisation logic separate from business calculations.

General:

- Use small commits.
- Add tests with every new feature.
- Do not generate placeholder code disguised as complete code.
- Do not invent API endpoints that are not approved.