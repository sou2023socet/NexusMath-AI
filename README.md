# NexusMath-AI Auth Backend

This backend provides the authentication and account-management layer for the NexusMath-AI ecosystem. It exposes a secure REST API for user registration, login, JWT-based session handling, and password reset workflows used by the main MathApp client.

## Overview

The project is a Spring Boot 3 service built for Java 17. It handles user identity, password hashing, JWT issuance, email-based password resets, and protected API access for the math application.

## Key capabilities

- User registration and login
- BCrypt password hashing
- JWT generation and validation
- Stateless Spring Security configuration
- Password reset flow with time-limited tokens
- Email delivery for reset instructions
- Structured logging for API calls and operations

## Technology stack

- Java 17
- Spring Boot 3.3.0
- Spring Web MVC
- Spring Security
- Spring Data JPA
- Hibernate
- MySQL
- JWT (jjwt)
- Thymeleaf
- JavaMail

## API endpoints

Base path: `/api/auth`

- `POST /api/auth/register` — create a new user account
- `POST /api/auth/login` — authenticate and receive a JWT
- `POST /api/auth/forgot-password` — request a password reset email
- `POST /api/auth/reset-password` — complete password reset using the token
- `POST /api/auth/logout` — client-side logout (token discarded by frontend)

Public endpoints are allowed during authentication flows and password reset pages, while all other requests require JWT validation.

## Math API

The JavaFX client is maintained in the separate `mathapp` project. Authenticated
math requests are handled here so calculation code is not executed by the UI.
The API currently supports:

- `POST /api/math/arithmetic`
- `POST /api/math/equations/solve`
- `POST /api/math/sequences/evaluate` and `/api/math/sequences/analyze`
- `POST /api/math/number-properties/evaluate`
- `POST /api/math/matrices/evaluate`
- `POST /api/math/statistics/regression`, `/statistics/correlation`,
  `/statistics/venn2`, `/statistics/bayes`, and `/statistics/contingency`
- `POST /api/math/distributions/evaluate`
- `POST /api/math/combinatorics`

Math endpoints require the same JWT issued by `/api/auth/login` or
`/api/auth/register`.

## Run and test independently

This project is an independent Spring Boot service. It can be built and tested
without the JavaFX project:

```bash
mvn verify
```

Starting the service requires the configured MySQL database and application
configuration. From this project root, run:

```bash
mvn spring-boot:run
```

The JavaFX frontend is a separate project and is not needed to start this
service. Conversely, the frontend can launch on its own, but its authenticated
math actions require this service to be reachable.

## Security model

The service uses Spring Security with a stateless session strategy and a custom JWT filter. Requests such as registration, login, forgot-password, reset-password, and static assets are permitted without an active session. Other routes require a valid token.

## Configuration

The active profile is configured in `application.yml` and the development database settings are in `application-dev.yml`.

Important values:

- app server port: `8081`
- database: `jdbc:mysql://localhost:3306/mathapp_auth`
- profile: `dev`
- frontend URL: `http://localhost:8081` (used for password reset links)

## Running the service

From the project root:

```bash
mvn clean spring-boot:run
```

The development profile creates the `mathapp_auth` database when it is missing,
provided the configured MySQL user has permission to create databases. You can
also create or verify it explicitly with the PowerShell script:

```powershell
.\scripts\create-database.ps1 -Username root
```

The script prompts for the MySQL password. Override `-Server`, `-Port`,
`-Username`, or `-Database` when your local MySQL setup differs. The MySQL
client tools must be available on `PATH`.

For a packaged build:

```bash
mvn clean package
java -jar target/auth-backend-1.0-SNAPSHOT.jar
```

## Project structure

```text
auth-backend/
├── src/main/java/com/mathapp/auth/
│   ├── api/             # controllers and request models
│   ├── config/          # security and application configuration
│   ├── logging/         # logging helpers and request tracking
│   ├── model/           # JPA entity models (User, token objects)
│   ├── repo/            # repository interfaces
│   ├── security/        # JWT filter and token service
│   ├── service/         # auth and email services
│   └── AuthBackendApplication.java
├── src/main/resources/
│   ├── application.yml
│   ├── application-dev.yml
│   ├── application-prod.yml
│   ├── application-test.yml
│   └── templates/       # password reset pages
├── pom.xml
├── README.md
└── logs/
```

## Notes

This project is designed to work alongside the JavaFX desktop app in the
separate `mathapp` repository. The backend owns calculation engines and APIs;
the desktop client consumes those APIs using generated JWT tokens. Neither
project imports or builds the other.
