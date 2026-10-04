# NexusMath-AI Math Workbench

This project is the main desktop mathematics application in the NexusMath-AI suite. It is built as a JavaFX-based scientific computing workbench that combines calculation tools, algebra utilities, numerical methods, equation solving, and statistics in one interface.

## Overview

The application is organized as a modular Java project centered around a GUI-driven workspace. It targets educational and analytical math workflows with a polished desktop UI and a set of reusable engine packages for mathematical computations.

## Key capabilities

- Arithmetic and calculator-style workflows
- Algebraic operations and matrix utilities
- Equation and expression solving
- Number property analysis
- Numerical computation tools
- Statistics and data-oriented calculations
- JavaFX desktop interface with FXML and CSS styling
- Modular project organization by domain

## Technology stack

- Java 21
- Maven
- JavaFX 21.0.2
- JUnit 5
- FXML + CSS UI design

## Main application entry points

- `mathapp.gui.MainGUIApp` — primary JavaFX app launcher
- `mathapp.MathAppLauncher` — alternate launcher class

## Run the project

From the project root:

```bash
mvn clean javafx:run
```

To build the app package:

```bash
mvn clean package
```

## Main project structure

```text
mathapp/
├── src/main/java/mathapp/
│   ├── algebra/           # matrix and symbolic algebra logic
│   ├── apps/              # app-level workflows and screens
│   ├── arithmetic/        # arithmetic utilities and calculators
│   ├── controller/        # controller logic and navigation helpers
│   ├── equations/         # equation solving components
│   ├── gui/               # JavaFX UI, FXML, CSS and screen controllers
│   ├── logging/           # logging support
│   ├── numberproperties/  # number analysis features
│   ├── numerical/         # numerical analysis tools
│   ├── stats/            # statistics modules
│   ├── MathAppLauncher.java
│   └── ...
├── src/main/resources/mathapp/gui/
│   ├── main_view.fxml
│   ├── home_view.fxml
│   ├── style.css
│   └── ...
├── src/test/java/mathapp/
│   ├── gui/
│   └── stats/
├── pom.xml
├── README.md
├── SEQUENCE_ANALYZER.md
└── logs/
```

## Domain areas represented in the codebase

- Algebra: matrix operations and engine logic
- Arithmetic: core numeric computation routines
- Equations: solving and manipulation workflows
- Numerical: approximation and numerical analysis
- Number properties: digit, prime, divisor, and classification helpers
- Statistics: statistical calculators and analysis modules
- GUI: screen routing and JavaFX views

## Notes

This application is the main NexusMath-AI user-facing platform. The sibling authentication service in the `auth-backend` module provides secure login and account management services for the broader product.
