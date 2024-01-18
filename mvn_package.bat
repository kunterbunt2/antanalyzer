REM
REM compile and package the project
REM

SET JAVA_HOME=%JAVA_HOME_11%
set PATH=%JAVA_HOME%\bin;%PATH%
java -version
chcp 65001
call mvn clean package
pause