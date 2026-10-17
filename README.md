# NexusMath-AI Math Workbench

This project is the JavaFX desktop frontend in the NexusMath-AI suite. It owns the user interface, authentication flow, and HTTP clients; calculation engines run in the separate `auth-backend` service.

## Overview

The frontend is an independently buildable Java 21/Maven application. It sends math and authentication requests over REST and does not include local math calculation engines. It can launch without the backend, but login and math actions require a reachable backend.

## Key capabilities

- JavaFX screens for arithmetic, equations, matrices, sequences, number properties, statistics, probability, and distributions
- Authenticated REST requests to the backend for calculations
- Configurable backend URL
- JavaFX desktop interface with FXML and CSS styling

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

From this project root, build and test the frontend independently:

```bash
mvn test
```

Launch the JavaFX UI independently with:

```bash
mvn javafx:run
```

The frontend's math and login features require the separately running backend.
Its default URL is `http://localhost:8081`; override it with
`MATHAPP_API_BASE_URL` or the `mathapp.api.base-url` JVM property. For example,
in PowerShell:

```powershell
$env:MATHAPP_API_BASE_URL = "http://localhost:8081"
mvn javafx:run
```

Run `mvn test` to execute the frontend HTTP-client tests without requiring a
running backend. The packaged JavaFX installer is Windows-specific:

```bash
mvn package
```

## Main project structure

```text
mathapp/
├── src/main/java/mathapp/
│   ├── controller/        # JavaFX view controller
│   ├── gui/               # JavaFX UI, API clients, FXML, CSS and screen controllers
│   ├── logging/           # frontend logging
│   ├── MathAppLauncher.java
│   └── ...
├── src/main/resources/mathapp/gui/
│   ├── main_view.fxml
│   ├── home_view.fxml
│   ├── style.css
│   └── ...
├── src/test/java/mathapp/
│   └── gui/api/           # REST client tests using an in-process HTTP server
├── pom.xml
├── README.md
├── SEQUENCE_ANALYZER.md
└── logs/
```

## Notes

The frontend and backend are separate Maven projects and can be built and
started independently. The backend is the sole owner of calculation logic;
the frontend contains only presentation, request/response models, and REST
client code.
