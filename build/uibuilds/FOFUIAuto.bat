For /F "tokens=1* delims==" %%A IN (config.properties) DO (
    IF "%%A"=="rootPath" set projectRootRepositoryInRunningMachine=%%B
	)
set projectLocation=%projectRootRepositoryInRunningMachine%\ui\auto\FOFUIAuto

cd %projectLocation%

call mvn clean

call mvn install -f %projectLocation%\pom.xml -DskipTests

call copy /Y %projectLocation%\target\FOFUIAuto-0.0.1-SNAPSHOT-jar-with-dependencies.jar  %projectRootRepositoryInRunningMachine%\catalog\uiautosuites\FOFUIAuto\FOFUIAuto-0.0.1-SNAPSHOT-jar-with-dependencies.jar

call echo D | xcopy /Y /I %projectRootRepositoryInRunningMachine%\ui\testdata\metadata\objectrepo\fof  %projectRootRepositoryInRunningMachine%\catalog\testdata\metadata\objectrepo\fof /E

call echo D | xcopy /Y /I %projectRootRepositoryInRunningMachine%\ui\testdata\fof  %projectRootRepositoryInRunningMachine%\catalog\testdata\fof /E


