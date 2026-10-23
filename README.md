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

## Department AI chat

The authenticated `/api/ai-chat/sessions` API provides separate Mathematics,
Physics, and DSA chat sessions. The default provider is Gemini with the
`gemini-3.8-flash` model. Configure the secret only in the backend environment
as `GEMINI_API_KEY`; optionally set `AI_PROVIDER`, `GEMINI_MODEL`, or
`AI_TIMEOUT` (for example, `20s`). Chat availability is controlled independently
in `application-dev.yml`, `application-test.yml`, `application-prod.yml`, and
`application-docker.yml` using `ai.chat.enabled` or the `AI_CHAT_ENABLED`
environment override. The provider key must be set in the backend process
environment; do not put it in a configuration file. The chat config endpoint
reports chat as disabled when the provider key is missing. Test, production,
and Docker profiles default to disabled;
set `AI_CHAT_ENABLED=true` to enable AI chat in those profiles. The frontend reads
the non-secret `/api/ai-chat/config` flag and disables its chat controls when
backend AI chat is off. No provider secret or chat context is included in Angular.

The development profile enables chat by default. Test, production, and Docker
profiles default to disabled. To disable chat in development, set
`AI_CHAT_ENABLED=false`; to enable it in another profile, set
`AI_CHAT_ENABLED=true`.

Sessions and their messages exist only in backend runtime memory. They are
bound to the authenticated user and department, expire after 30 minutes of
inactivity, allow at most 10 user messages and 10 responses, and are cleared
automatically after expiry or when the user exits the chat. The backend sends
its server-maintained context with each provider request and disables provider
storage where supported. Sessions and messages are not written to the database
or logs. A backend restart clears all active sessions; users are informed of
this in the chat UI.

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
- CORS origins are defined by the active profile's `cors.allowed-origins` property. The
  development profile allows `localhost` ports `3000`, `4200`, `4201`, `4202`,
  `5173`, `8080`, and `8081`. Add new local web-client origins to
  `application-dev.yml`; they do not need to be repeated on controllers.
- The Docker profile reads allowed origins from `CORS_ALLOWED_ORIGINS`, defaulting
  to `http://localhost:4200`. Set multiple origins as a comma-separated list.

## Running the service

From the project root:

```bash
mvn clean spring-boot:run
```

On Windows, run `start-backend.bat` from this project or double-click it. If
this backend is already listening on port `8081`, the script reuses that
instance rather than starting a duplicate. If another process owns the port,
the script reports its PID and exits without stopping it. Otherwise, the script
checks for Java and Maven, forces Maven to refresh remote dependency metadata,
runs `mvn clean install`, then starts the server with the `dev` profile.
Maven's `-U` refreshes dependency metadata and snapshots; it does not change
versions declared in `pom.xml`.

To enable Gemini AI chat, set `GEMINI_API_KEY` in the terminal before starting
the script. Without a key, the script disables AI chat unless `AI_CHAT_ENABLED`
was explicitly set. The configured Gemini model is `gemini-3.8-flash`.

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
