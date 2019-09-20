For /F "tokens=1* delims==" %%A IN (config.properties) DO (
    IF "%%A"=="rootPath" set projectRootRepositoryInRunningMachine=%%B
)
set projectLocation=%projectRootRepositoryInRunningMachine%\ws\auto\MDMWSAuto
cd %projectLocation%

call mvn clean

call mvn install -f %projectLocation%\pom.xml -DskipTests

echo "Batch Process Completed"

pause