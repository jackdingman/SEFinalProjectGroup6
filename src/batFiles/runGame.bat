@echo off
REM ─────────────────────────────────────────────────────────────
REM 1) Compute where your project root is (two levels up)
set "BASE_DIR=%~dp0..\.."

REM 2) Where IntelliJ puts your compiled .class files
set "CLASSES_DIR=%BASE_DIR%\out\production\SoftwareEngineering_FinalProject"

REM 3) Quick sanity check
if not exist "%CLASSES_DIR%\game\app\Game.class" (
  echo ERROR: Cannot find Game.class in:
  echo    %CLASSES_DIR%\game\app\Game.class
  pause
  exit /b 1
)

REM 4) Jump into the classes folder
pushd "%CLASSES_DIR%"

REM 5) Launch your game, pulling in ALL jars under lib\ (for your JDBC driver)
java -cp .;%BASE_DIR%\lib\* game.app.Game

REM 6) Keep the window open to read any output
pause

REM 7) Return to where we started
popd
