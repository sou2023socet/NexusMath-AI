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

This project is designed to work alongside the JavaFX desktop app in the sibling `mathapp` module. The auth backend manages secure identity and the math app consumes the generated JWT tokens for protected access.
