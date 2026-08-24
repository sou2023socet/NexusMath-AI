# Agentic Workflow Skill

The agent must not directly invent mathematical results.

Workflow:

1. Read the user's request.
2. Select a domain.
3. Identify the operation.
4. Extract parameters.
5. Ask for missing required values.
6. Validate the input.
7. Build a typed task graph.
8. Execute only registered backend operations.
9. Stream progress.
10. Preserve intermediate results.
11. Stop downstream tasks when a required task fails.
12. Return the final answer with trace and warnings.
13. Save the workflow history.

The agent may plan and orchestrate.
The domain engine must calculate.