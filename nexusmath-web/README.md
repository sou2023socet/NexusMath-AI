# NexusMath AI Web

Angular 18 standalone web client for the existing NexusMath-AI Spring API. The JavaFX client and backend remain separate applications and are not replaced by this workspace.

## Prerequisites

- Node.js 20+ and npm
- Java 17+ and Maven for the backend
- A configured, reachable backend and its configured MySQL database for authentication and calculations

## Run locally

Start the backend from `../../auth-backend` using the repository's configured development profile and database setup. Then start the Angular client:

```powershell
npm ci
npm start
```

The web client is served at `http://localhost:4200`. `npm start` uses [proxy.conf.js](./proxy.conf.js) to forward `/api` requests to the backend. Its default target is `http://localhost:8081`, matching the Spring development server. To use another backend URL, set `NEXUSMATH_API_URL` in the environment where the Angular server runs, then restart it:

```powershell
$env:NEXUSMATH_API_URL = "http://localhost:8081"
npm start
```

The variable must be an absolute HTTP(S) URL reachable from the Angular dev-server process; do not include credentials. If the web app runs in a container, `localhost` refers to that container, so use the backend's reachable service/host address instead. Production hosting must configure its web-server reverse proxy for `/api` to the backend; the Angular dev-server environment variable does not configure production infrastructure.

## Build and tests

```powershell
npm run build
npm test -- --watch=false --browsers=ChromeHeadless
```

The production build uses the Angular CLI bundle budgets in [angular.json](./angular.json). The Angular unit tests use Karma/Jasmine and `HttpTestingController`; they do not require a live backend.

## Implemented routes

| Route | Purpose | Access |
| --- | --- | --- |
| `/dashboard` | Entry page and truthful summary of current backend capabilities | Public |
| `/login` | Login, registration, and forgot-password request | Public |
| `/math` | Authenticated calculator and math/statistics API workbench | Authenticated |
| `/theory` | Descriptive overview of topics supported by existing math services | Authenticated |
| `/guide` | Client usage and session behavior | Authenticated |
| `/about` | Application architecture summary | Authenticated |
| `/agents` | Explicit notice that AI-agent services are not yet implemented | Authenticated |

## Authentication and API behavior

- Login and registration use the existing `/api/auth` contracts (`email`, `username`, and `password`).
- The JWT is held in application memory only; it is not written to local storage or cookies. A browser refresh requires a new login.
- A functional route guard gates math and related workbench routes. An HTTP interceptor attaches the bearer token to `/api/` requests.
- Sign out calls the stateless backend logout endpoint, then clears the in-memory session.
- The client does not store calculation history. Reset-password email links continue to use the existing backend reset page.
- The math API remains the source of truth for calculation, validation, and error messages. Client-side checks provide early feedback but do not replace backend validation.

## Scope and known gaps

See [PROJECT_COMPLETION.md](./PROJECT_COMPLETION.md) for the feature-parity checklist, test evidence, design-access blocker, and deferred work. The Angular UI uses the colors and typography from the existing JavaFX stylesheet as an interim shared baseline. A Figma design URL is available, but the MCP request returned no selected layer and subsequent metadata access was rate-limited; the UI has not been compared against the design.
