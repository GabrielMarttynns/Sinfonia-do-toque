param([switch]$DebugKeyboard)

$ErrorActionPreference = 'Stop'
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
$root = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$version = '8.14.6'
$gradleHome = Join-Path $env:LOCALAPPDATA "SinfoniaDoToque\gradle-$version"
$gradleBat = Join-Path $gradleHome 'bin\gradle.bat'
try {
    if (-not (Test-Path $gradleBat)) {
        $installParent = Split-Path -Parent $gradleHome
        New-Item -ItemType Directory -Path $installParent -Force | Out-Null
        $zipPath = Join-Path $installParent "gradle-$version-bin.zip"
        $url = "https://services.gradle.org/distributions/gradle-$version-bin.zip"
        Write-Host "Baixando Gradle oficial $version (primeira execucao)..."
        Invoke-WebRequest -Uri $url -OutFile $zipPath -UseBasicParsing

        $hashUrl = "$url.sha256"
        $expected = ((Invoke-WebRequest -Uri $hashUrl -UseBasicParsing).Content -as [string]).Trim().Split(' ')[0].ToLowerInvariant()
        $actual = (Get-FileHash -Path $zipPath -Algorithm SHA256).Hash.ToLowerInvariant()
        if ($actual -ne $expected) { throw 'Falha na verificacao SHA256 do Gradle baixado.' }
        Write-Host 'Extraindo Gradle...'
        Expand-Archive -Path $zipPath -DestinationPath $installParent -Force
        Remove-Item $zipPath -Force
    }
    $argsGradle = @('-p', $root, ':lwjgl3:run', '--console=plain')
    if ($DebugKeyboard) { $argsGradle += '-PdebugKeyboard=true' }
    Write-Host 'Iniciando Sinfonia do Toque...'
    & $gradleBat @argsGradle
    exit $LASTEXITCODE
} catch {
    Write-Host "ERRO: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host 'Alternativa: abra a pasta no IntelliJ e execute a tarefa Gradle lwjgl3 > run.'
    exit 1
}
