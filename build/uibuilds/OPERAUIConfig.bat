For /F "tokens=1* delims==" %%A IN (config.properties) DO (
    IF "%%A"=="rootPath" set projectRootRepositoryInRunningMachine=%%B
)
set projectLocation=%projectRootRepositoryInRunningMachine%\ui\auto\OperaUIConfig

cd %projectLocation%

call mvn clean

call mvn install -f %projectLocation%\pom.xml -DskipTests

call copy /Y %projectLocation%\target\OperaUIConfig-0.0.1-SNAPSHOT-jar-with-dependencies.jar  %projectRootRepositoryInRunningMachine%\catalog\uiautosuites\OperaUIConfig\OperaUIConfig-0.0.1-SNAPSHOT-jar-with-dependencies.jar

call echo D | xcopy /Y /I %projectRootRepositoryInRunningMachine%\ui\testdata\metadata\objectrepo\config %projectRootRepositoryInRunningMachine%\catalog\testdata\metadata\objectrepo\config /E

call echo D | xcopy /Y /I %projectRootRepositoryInRunningMachine%\ui\testdata\config  %projectRootRepositoryInRunningMachine%\catalog\testdata\config /E


