@echo off
setlocal

cd /d "%~dp0"
if errorlevel 1 (
  echo Could not open the backend project directory.
  exit /b 1
)

powershell -NoProfile -Command "$listener = Get-NetTCPConnection -State Listen -LocalPort 8081 -ErrorAction SilentlyContinue | Select-Object -First 1; if (-not $listener) { exit 0 }; $process = Get-CimInstance Win32_Process -Filter ('ProcessId = ' + $listener.OwningProcess) -ErrorAction Stop; if ($process.CommandLine -match 'com\.mathapp\.auth\.AuthBackendApplication') { Write-Output ('Backend is already running on port 8081 (PID ' + $process.ProcessId + '). Reusing the existing instance.'); exit 2 }; Write-Output ('Port 8081 is occupied by ' + $process.Name + ' (PID ' + $process.ProcessId + '). Stop it or change the backend port.'); exit 3"
set "PORT_CHECK_RESULT=%ERRORLEVEL%"
if "%PORT_CHECK_RESULT%"=="2" (
  exit /b 0
)
if not "%PORT_CHECK_RESULT%"=="0" (
  echo Could not start the backend because port 8081 is unavailable or could not be checked.
  exit /b 1
)

where java >nul 2>nul
if errorlevel 1 (
  echo Java was not found on PATH. Install JDK 17 or newer.
  exit /b 1
)

where mvn >nul 2>nul
if errorlevel 1 (
  echo Maven was not found on PATH.
  exit /b 1
)

echo Updating remote dependency metadata, cleaning, and installing the backend...
call mvn -U clean install
if errorlevel 1 (
  echo Backend clean/install failed. The server was not started.
  exit /b 1
)

if not defined GEMINI_API_KEY (
  if not defined AI_CHAT_ENABLED set "AI_CHAT_ENABLED=false"
  echo GEMINI_API_KEY is not set. AI chat will remain disabled unless explicitly enabled.
) else (
  if not defined AI_CHAT_ENABLED set "AI_CHAT_ENABLED=true"
)

set "AI_PROVIDER=gemini"
set "GEMINI_MODEL=gemini-3.8-flash"

echo Starting the Spring Boot backend with the dev profile...
call mvn -U spring-boot:run -Dspring-boot.run.profiles=dev
set "SERVER_EXIT_CODE=%ERRORLEVEL%"

if not "%SERVER_EXIT_CODE%"=="0" (
  echo Backend server exited with code %SERVER_EXIT_CODE%.
)
exit /b %SERVER_EXIT_CODE%
