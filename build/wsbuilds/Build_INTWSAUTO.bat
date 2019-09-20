For /F "tokens=1* delims==" %%A IN (config.properties) DO (
    IF "%%A"=="rootPath" set projectRootRepositoryInRunningMachine=%%B
)
set projectLocation=%projectRootRepositoryInRunningMachine%\ws\auto\INTWSAuto
set runnableJarFilesLocation=%projectRootRepositoryInRunningMachine%\catalog\wsautosuites\INT
set commonResourcesSourceLocation=%projectRootRepositoryInRunningMachine%\ws\resources
cd %projectLocation%

call mvn clean
call mvn install -f %projectLocation%\pom.xml -DskipTests
call copy /Y %projectLocation%\target\INTWSAuto-1.0-SNAPSHOT-jar-with-dependencies.jar  %runnableJarFilesLocation%\INTWSAuto-1.0-SNAPSHOT-jar-with-dependencies.jar
call copy /Y %projectLocation%\testng_INTSanity.xml  %runnableJarFilesLocation%\testng_INTSanity.xml
call xcopy /s /Y %projectLocation%\target\dependency-jars  %runnableJarFilesLocation%\dependency-jars\*
call copy /Y %projectRootRepositoryInRunningMachine%\common\libraries\com\oracle\ojdbc7\11.2.0\ojdbc7-11.2.0.jar %runnableJarFilesLocation%\dependency-jars\ojdbc7-11.2.0.jar
call xcopy /s /Y %commonResourcesSourceLocation%\config  %runnableJarFilesLocation%\config\*
call xcopy /s /Y %commonResourcesSourceLocation%\dataBank  %runnableJarFilesLocation%\dataBank\*
call xcopy /s /Y %commonResourcesSourceLocation%\objectRepo  %runnableJarFilesLocation%\objectRepo\*
if not exist "%runnableJarFilesLocation%\results\" mkdir %runnableJarFilesLocation%\results

echo "Batch Process Completed"

pause

