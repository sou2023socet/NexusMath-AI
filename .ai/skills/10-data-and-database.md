# Data and Database Skill

Separate these models:

- User
- TheoryNode
- Formula
- TopicPrerequisite
- CalculationHistory
- CalculationStep
- PhysicsQuantity
- DsaExecution
- Exercise
- LearningProgress
- AgentWorkflow

Use DTOs between the API and database layers.

Store:

- Input snapshot
- Output snapshot
- Formula or algorithm version
- Assumptions
- Units
- Precision
- Warnings
- Step trace
- Timestamp
- User ID
- Domain
- Operation

Use MongoDB for flexible theory content and calculation traces.
Use MySQL only when strong relational reporting or transactional
institutional data becomes necessary.

Never store passwords in plain text.
Never store secrets in source code.
Never use uncontrolled database queries from user input.