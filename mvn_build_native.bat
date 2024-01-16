REM
REM compile and package the project
REM

java -version
chcp 65001
call "C:\Program Files\Microsoft Visual Studio\2022\Community\VC\Auxiliary\Build\vcvars64.bat"
call mvn clean -Pnative -X -DskipTests native:compile
pause