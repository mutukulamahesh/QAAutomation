For /F "tokens=1* delims==" %%A IN (config.properties) DO (
    IF "%%A"=="rootPath" set projectRootRepositoryInRunningMachine=%%B
)
set projectLocation=%projectRootRepositoryInRunningMachine%\ui\generic\OperaAFWUILib

cd %projectLocation%

call mvn install -f %projectLocation%\pom.xml -DskipTests


