@echo off
setlocal enabledelayedexpansion

:loop
cls
nvidia-smi
timeout /t 1 /nobreak >nul

rem Check for user input
set "input="
set /p "input=Press 'q' to quit: " <nul
if /i "!input!"=="q" goto end

goto loop

:end
echo Exiting...
