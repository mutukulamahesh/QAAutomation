For /F "tokens=1* delims==" %%A IN (config.properties) DO (
    IF "%%A"=="rootPath" set projectRootRepositoryInRunningMachine=%%B
	)
set projectLocation=%projectRootRepositoryInRunningMachine%\ui\auto\RSVUIAuto

cd %projectLocation%

call mvn clean

call mvn install -f %projectLocation%\pom.xml -DskipTests

call copy /Y %projectLocation%\target\RSVUIAuto-0.0.1-SNAPSHOT-jar-with-dependencies.jar  %projectRootRepositoryInRunningMachine%\catalog\uiautosuites\RSVUIAuto\RSVUIAuto-0.0.1-SNAPSHOT-jar-with-dependencies.jar

call echo D | xcopy /Y /I %projectRootRepositoryInRunningMachine%\ui\testdata\metadata\objectrepo\rsv  %projectRootRepositoryInRunningMachine%\catalog\testdata\metadata\objectrepo\rsv /E

call echo D | xcopy /Y /I %projectRootRepositoryInRunningMachine%\ui\testdata\rsv  %projectRootRepositoryInRunningMachine%\catalog\testdata\rsv /E


