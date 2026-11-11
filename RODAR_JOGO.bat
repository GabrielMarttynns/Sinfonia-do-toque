@echo off
setlocal
title Sinfonia do Toque - Iniciar jogo
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0scripts\executar.ps1"
if errorlevel 1 (
    echo.
    echo Nao foi possivel iniciar. Leia o arquivo LEIA_PRIMEIRO.md.
)
pause
