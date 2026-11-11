@echo off
setlocal
title Sinfonia do Toque - Teste sem Arduino
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0scripts\executar.ps1" -DebugKeyboard
if errorlevel 1 (
    echo.
    echo Nao foi possivel iniciar. Leia o arquivo LEIA_PRIMEIRO.md.
)
pause
