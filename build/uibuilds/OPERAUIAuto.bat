For /F "tokens=1* delims==" %%A IN (config.properties) DO (
    IF "%%A"=="rootPath" set projectRootRepositoryInRunningMachine=%%B
)
set projectLocation=%projectRootRepositoryInRunningMachine%\ui\auto\OPERAUIAuto

cd %projectLocation%

call mvn clean

call mvn install -f %projectLocation%\pom.xml -DskipTests

call copy /Y %projectLocation%\target\OPERAUIAuto-0.0.1-SNAPSHOT-jar-with-dependencies.jar  %projectRootRepositoryInRunningMachine%\catalog\uiautosuites\OPERAUIAuto\OPERAUIAuto-0.0.1-SNAPSHOT-jar-with-dependencies.jar
call echo D | xcopy /Y /I %projectRootRepositoryInRunningMachine%\ui\testdata  %projectRootRepositoryInRunningMachine%\catalog\testdata /E


