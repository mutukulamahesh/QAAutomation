For /F "tokens=1* delims==" %%A IN (config.properties) DO (
    IF "%%A"=="rootPath" set projectRootRepositoryInRunningMachine=%%B
)
set projectLocation=%projectRootRepositoryInRunningMachine%\ui\auto\CRMUIAuto

cd %projectLocation%

call mvn clean

call mvn install -f %projectLocation%\pom.xml -DskipTests

call copy /Y %projectLocation%\target\CRMUIAuto-0.0.1-SNAPSHOT-jar-with-dependencies.jar  %projectRootRepositoryInRunningMachine%\catalog\uiautosuites\CRMUIAuto\CRMUIAuto-0.0.1-SNAPSHOT-jar-with-dependencies.jar

call echo D | xcopy /Y /I %projectRootRepositoryInRunningMachine%\ui\testdata\metadata\objectrepo\crm %projectRootRepositoryInRunningMachine%\catalog\testdata\metadata\objectrepo\crm /E

call echo D | xcopy /Y /I %projectRootRepositoryInRunningMachine%\ui\testdata\crm  %projectRootRepositoryInRunningMachine%\catalog\testdata\crm /E