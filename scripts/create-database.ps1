param(
    [string]$Server = "localhost",
    [ValidateRange(1, 65535)]
    [int]$Port = 3306,
    [string]$Username = "root",
    [ValidatePattern('^[A-Za-z0-9_]+$')]
    [string]$Database = "mathapp_auth"
)

$mysql = Get-Command mysql -CommandType Application -ErrorAction SilentlyContinue
if (-not $mysql) {
    throw "MySQL client 'mysql' was not found on PATH. Install MySQL client tools and retry."
}

$arguments = @(
    "--host=$Server"
    "--port=$Port"
    "--user=$Username"
    "--password"
    "--execute=CREATE DATABASE IF NOT EXISTS $Database CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
)

& $mysql.Source @arguments
if ($LASTEXITCODE -ne 0) {
    throw "MySQL failed to create or verify database '$Database'. Check the server, credentials, and database privileges."
}

Write-Output "Database '$Database' is ready."
