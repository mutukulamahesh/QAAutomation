For /F "tokens=1* delims==" %%A IN (config.properties) DO (
    IF "%%A"=="rootPath" set projectRootRepositoryInRunningMachine=%%B
)	
set projectLocation=%projectRootRepositoryInRunningMachine%\ui\auto\EVMUIAuto

cd %projectLocation%

call mvn clean

call mvn install -f %projectLocation%\pom.xml -DskipTests

call copy /Y %projectLocation%\target\EVMUIAuto-0.0.1-SNAPSHOT-jar-with-dependencies.jar  %projectRootRepositoryInRunningMachine%\catalog\uiautosuites\EVMUIAuto\EVMUIAuto-0.0.1-SNAPSHOT-jar-with-dependencies.jar

call echo D | xcopy /Y /I %projectRootRepositoryInRunningMachine%\ui\testdata\metadata\objectrepo\evm %projectRootRepositoryInRunningMachine%\catalog\testdata\metadata\objectrepo\evm /E

call echo D | xcopy /Y /I %projectRootRepositoryInRunningMachine%\ui\testdata\evm  %projectRootRepositoryInRunningMachine%\catalog\testdata\evm /E


 