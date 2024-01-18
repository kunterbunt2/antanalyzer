REM
REM compile and package the project without running the tests
REM

SET JAVA_HOME=%JAVA_HOME_11%
set PATH=%JAVA_HOME%\bin;%PATH%
java -version
chcp 65001
rem call mvn clean verify spring-boot:repackage -DskipTests -X
call mvn clean package -DskipTests
pause