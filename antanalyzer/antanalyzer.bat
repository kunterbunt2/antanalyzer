ECHO OFF
rem SET JAVA_HOME=%JAVA_HOME_11%
rem set PATH=%JAVA_HOME%\bin;%PATH%
rem java -version
rem chcp 65001
pushd
set mypath=%~dp0
cd /D %mypath%
call java -cp intern/lib/*  de.bushnaq.abdalla.antanalyzer.SpringBootApplication %*
popd
pause