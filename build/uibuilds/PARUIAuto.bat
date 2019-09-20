For /F "tokens=1* delims==" %%A IN (config.properties) DO (
    IF "%%A"=="rootPath" set projectRootRepositoryInRunningMachine=%%B
	)
set projectLocation=%projectRootRepositoryInRunningMachine%\ui\auto\PARUIAuto

cd %projectLocation%

call mvn clean

call mvn install -f %projectLocation%\pom.xml -DskipTests

call copy /Y %projectLocation%\target\PARUIAuto-0.0.1-SNAPSHOT-jar-with-dependencies.jar  %projectRootRepositoryInRunningMachine%\catalog\uiautosuites\PARUIAuto\PARUIAuto-0.0.1-SNAPSHOT-jar-with-dependencies.jar

call echo D | xcopy /Y /I %projectRootRepositoryInRunningMachine%\ui\testdata\metadata\objectrepo\rates %projectRootRepositoryInRunningMachine%\catalog\testdata\metadata\objectrepo\rates /E

call echo D | xcopy /Y /I %projectRootRepositoryInRunningMachine%\ui\testdata\rates  %projectRootRepositoryInRunningMachine%\catalog\testdata\rates /E


