@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation...
@if "%DEBUG%" == "" @echo off
@setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%

@set WRAPPER_JAR="%APP_HOME%\.mvn\wrapper\maven-wrapper.jar"
@set WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

@if not exist %WRAPPER_JAR% (
  echo Couldn't find %WRAPPER_JAR%
  pause
  exit /B 1
)

@set JAVA_EXE=java.exe
@if defined JAVA_HOME set JAVA_EXE="%JAVA_HOME%\bin\java.exe"

"%JAVA_EXE%" %MAVEN_OPTS% -classpath %WRAPPER_JAR% "-Dmaven.multiModuleProjectDirectory=%APP_HOME%" %WRAPPER_LAUNCHER% %*