# API and Controller Skill

Controllers must:

1. Authenticate and authorise the request.
2. Validate the request DTO.
3. Delegate to one application service.
4. Return a typed response DTO.
5. Use correct HTTP status codes.
6. Never contain mathematical or algorithmic logic.
7. Never expose stack traces.
8. Never expose database entities directly.
9. Include a request or correlation ID.
10. Return consistent error responses.

Suggested response structure:

- requestId
- domain
- operation
- status
- inputs
- assumptions
- steps
- result
- warnings
- visualisation
- execution metadata