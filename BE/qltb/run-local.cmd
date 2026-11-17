@echo off
REM Spring Boot 3.x yeu cau JDK 17+. Maven wrapper mac dinh dang dung JDK 8 tren may dev.
set "JAVA_HOME=C:\Program Files\Java\jdk-21"
if not exist "%JAVA_HOME%\bin\java.exe" set "JAVA_HOME=C:\Program Files\Java\jdk-17"
if not exist "%JAVA_HOME%\bin\java.exe" (
  echo Khong tim thay JDK 17/21. Cai dat JDK hoac dat bien JAVA_HOME.
  exit /b 1
)
set "PATH=%JAVA_HOME%\bin;%PATH%"
echo Using JAVA_HOME=%JAVA_HOME%
"%JAVA_HOME%\bin\java.exe" -version
call "%~dp0mvnw.cmd" %*