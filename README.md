# NexusMath Angular workspace

This workspace contains the NexusMath-AI Angular 18 web client in
[`nexusmath-web/`](./nexusmath-web/). The frontend uses the existing Spring
Boot backend in [`../auth-backend/`](../auth-backend/README.md); it does not
replace the separate JavaFX client.

## Prerequisites

- Node.js 20 or newer and npm
- Java 17 or newer and Maven to run the backend
- MySQL configured for the backend

## Start the applications

For a one-command Windows setup, open PowerShell in this directory, optionally
set the Gemini API key for the backend, and run the launcher:

```powershell
$env:GEMINI_API_KEY = "your-key"
.\start-nexusmath-ai.bat
```

The launcher installs backend and frontend dependencies, then opens Spring Boot
and Angular in separate terminal windows. Gemini is a hosted API model; the
launcher does not download model files. Without `GEMINI_API_KEY`, it starts AI
chat disabled. Keep provider keys in backend environment variables; never put
them in Angular source or frontend environment files.

To run only the backend, use [`start-backend.bat`](../auth-backend/start-backend.bat).
To start the frontend manually, use PowerShell in `nexusmath-web/`:

```powershell
npm ci
npm start
```

The Angular development server is available at `http://localhost:4200` and
proxies `/api` requests to `http://localhost:8081`. Set `NEXUSMATH_API_URL` if
the backend is at another HTTP(S) address; see the
[`nexusmath-web` README](./nexusmath-web/README.md).

## Features and routes

| Route | Feature | Access |
| --- | --- | --- |
| `/dashboard` | Application overview | Public |
| `/login` | Sign-in, registration, and password reset request | Public |
| `/math` | Calculator and mathematics/statistics API | Authenticated |
| `/theory` | Mathematics topic overview | Authenticated |
| `/guide` | Usage guidance | Authenticated |
| `/about` | Architecture overview | Authenticated |
| `/agents` | Mathematics, Physics, and DSA chatbots | Authenticated |

AI chat availability follows the active backend Spring profile. The backend's
`/api/ai-chat/config` endpoint reports only whether chat is enabled; the Angular
client disables chat controls when the backend turns it off. Development
enables AI chat by default; test, production, and Docker profiles default to
disabled. Configure availability with `AI_CHAT_ENABLED` on the backend.

Chat context is held temporarily in component memory in Angular and volatile
runtime memory on the backend. It is not written to browser storage or the
database. Exiting clears the active chat; backend restarts clear all sessions.
See the [backend README](../auth-backend/README.md) for provider and session
configuration.

## Build and test

Run these commands from `nexusmath-web/`:

```powershell
npm run build
npm test -- --watch=false --browsers=ChromeHeadless
```

The client uses Angular standalone components, lazy-loaded routes, and an HTTP
interceptor to attach the existing bearer token to API requests. The auth token
uses tab-scoped `sessionStorage`; chatbot messages are not stored there.
