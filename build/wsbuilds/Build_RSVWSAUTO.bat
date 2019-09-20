For /F "tokens=1* delims==" %%A IN (config.properties) DO (
    IF "%%A"=="rootPath" set projectRootRepositoryInRunningMachine=%%B
)
set projectLocation=%projectRootRepositoryInRunningMachine%\ws\auto\RSVWSAuto\
set runnableJarFilesLocation=%projectRootRepositoryInRunningMachine%\catalog\wsautosuites\RSV\
set commonResourcesTargetLocation=%projectRootRepositoryInRunningMachine%\catalog\wsautosuites\resources\
set commonResourcesSourceLocation=%projectRootRepositoryInRunningMachine%\ws\resources\
cd %projectLocation%

call mvn clean

call mvn install -f %projectLocation%\pom.xml -DskipTests
call copy /Y %projectLocation%\target\RSVWSAuto-1.0-SNAPSHOT-jar-with-dependencies.jar  %runnableJarFilesLocation%\RSVWSAuto-1.0-SNAPSHOT-jar-with-dependencies.jar
call copy /Y %projectLocation%\testng_RSVSanity.xml  %runnableJarFilesLocation%\testng_RSVSanity.xml
call xcopy /s /Y %projectLocation%\target\dependency-jars  %runnableJarFilesLocation%\dependency-jars\*
call copy /Y %projectRootRepositoryInRunningMachine%\common\libraries\com\oracle\ojdbc7\11.2.0\ojdbc7-11.2.0.jar %runnableJarFilesLocation%\dependency-jars\ojdbc7-11.2.0.jar
call xcopy /s /Y %commonResourcesSourceLocation%\config  %commonResourcesTargetLocation%\config\*
call xcopy /s /Y %commonResourcesSourceLocation%\dataBank  %commonResourcesTargetLocation%\dataBank\*
call xcopy /s /Y %commonResourcesSourceLocation%\objectRepo  %commonResourcesTargetLocation%\objectRepo\*
if not exist "%runnableJarFilesLocation%\results\" mkdir %commonResourcesTargetLocation%\results

echo "Batch Process Completed"

pause

