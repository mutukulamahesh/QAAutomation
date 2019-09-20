For /F "tokens=1* delims==" %%A IN (config.properties) DO (
    IF "%%A"=="rootPath" set projectRootRepositoryInRunningMachine=%%B
	)
set projectLocation=%projectRootRepositoryInRunningMachine%\ui\auto\OPERAUIGeneric

cd %projectLocation%

call mvn clean

call mvn install -f %projectLocation%\pom.xml -DskipTests

call copy /Y %projectLocation%\target\OPERAUIGeneric-0.0.1-SNAPSHOT-jar-with-dependencies.jar  %projectRootRepositoryInRunningMachine%\catalog\uiautosuites\OPERAUIGeneric\OPERAUIGeneric-0.0.1-SNAPSHOT-jar-with-dependencies.jar

call echo D | xcopy /Y /I %projectRootRepositoryInRunningMachine%\ui\testdata\metadata\objectrepo\OPERAUIGeneric  %projectRootRepositoryInRunningMachine%\catalog\testdata\metadata\objectrepo\OPERAUIGeneric /E

call echo D | xcopy /Y /I %projectRootRepositoryInRunningMachine%\ui\testdata\operauigeneric  %projectRootRepositoryInRunningMachine%\catalog\testdata\operauigeneric /E


