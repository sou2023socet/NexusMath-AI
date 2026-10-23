@echo off
setlocal

set "ROOT=%~dp0"
set "BACKEND=%ROOT%..\auth-backend"
set "WEB=%ROOT%nexusmath-web"

where java >nul 2>nul
if errorlevel 1 (
  echo Java was not found on PATH. Install Java 17 or newer.
  exit /b 1
)

where mvn >nul 2>nul
if errorlevel 1 (
  echo Maven was not found on PATH.
  exit /b 1
)

where node >nul 2>nul
if errorlevel 1 (
  echo Node.js was not found on PATH. Install Node.js 20 or newer.
  exit /b 1
)

where npm >nul 2>nul
if errorlevel 1 (
  echo npm was not found on PATH.
  exit /b 1
)

if not exist "%BACKEND%\pom.xml" (
  echo Backend project was not found: "%BACKEND%"
  exit /b 1
)

if not exist "%WEB%\package-lock.json" (
  echo Angular project or package-lock.json was not found: "%WEB%"
  exit /b 1
)

echo Cleaning and installing Spring backend dependencies...
pushd "%BACKEND%"
call mvn clean install
if errorlevel 1 (
  popd
  echo Backend dependency installation failed.
  exit /b 1
)
popd

echo Installing Angular dependencies from package-lock.json...
pushd "%WEB%"
call npm ci
if errorlevel 1 (
  popd
  echo Angular dependency installation failed.
  exit /b 1
)
popd

if not defined GEMINI_API_KEY (
  echo GEMINI_API_KEY is not set. Backend will start with AI chat disabled.
  set "AI_CHAT_ENABLED=false"
) else (
  if not defined AI_CHAT_ENABLED set "AI_CHAT_ENABLED=true"
)
set "AI_PROVIDER=gemini"
set "GEMINI_MODEL=gemini-3.8-flash"

echo Starting Spring backend and Angular web client in separate windows...
start "NexusMath AI Backend" /D "%BACKEND%" cmd /k "mvn spring-boot:run"
start "NexusMath Angular" /D "%WEB%" cmd /k "npm start"

echo Started. Gemini is a hosted API model; no model files are installed locally.
echo Set GEMINI_API_KEY in the environment before running this file to enable AI chat.
endlocal
