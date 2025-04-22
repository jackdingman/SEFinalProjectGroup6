@echo off
REM ─────────────────────────────────────────────────────────────
REM 1) Base folders (two levels up → project root)
set "BASE_DIR=%~dp0\..\.."
set "CLASSES_DIR=%BASE_DIR%\out\production\SoftwareEngineering_FinalProject"
set "LIB_GLOB=%BASE_DIR%\lib\*"

REM 2) Sanity‑check for your ServerGUI.class
if not exist "%CLASSES_DIR%\game\multiplayer\ServerGUI.class" (
  echo ERROR: Cannot find ServerGUI.class in %CLASSES_DIR%
  pause
  exit /b 1
)

REM 3) cd into the classes folder
pushd "%CLASSES_DIR%"

REM 4) Launch ServerGUI, pulling in ALL jars from lib\ (including ocsf.jar)
start "Chat Server" cmd /k ^
  "java -cp .;%LIB_GLOB% game.multiplayer.ServerGUI"

REM 5) Back to wherever we started
popd
