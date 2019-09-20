For /F "tokens=1* delims==" %%A IN (config.properties) DO (
    IF "%%A"=="rootPath" set projectRootRepositoryInRunningMachine=%%B
)
set projectLocation=%projectRootRepositoryInRunningMachine%\build\uibuilds
cd %projectLocation%
call OPERAFWUILib.bat

set projectLocation=%projectRootRepositoryInRunningMachine%\build\uibuilds
cd %projectLocation%
call OPERAUIConfig.bat

set projectLocation=%projectRootRepositoryInRunningMachine%\build\uibuilds
cd %projectLocation%
call OPERAUIGeneric.bat

set projectLocation=%projectRootRepositoryInRunningMachine%\build\uibuilds
cd %projectLocation%
call CRMUIAuto.bat

set projectLocation=%projectRootRepositoryInRunningMachine%\build\uibuilds
cd %projectLocation%
call RSVUIAuto.bat

set projectLocation=%projectRootRepositoryInRunningMachine%\build\uibuilds
cd %projectLocation%
call PARUIAuto.bat

set projectLocation=%projectRootRepositoryInRunningMachine%\build\uibuilds
cd %projectLocation%
call FOFUIAuto.bat

set projectLocation=%projectRootRepositoryInRunningMachine%\build\uibuilds
cd %projectLocation%
call EVMUIAuto.bat

set projectLocation=%projectRootRepositoryInRunningMachine%\build\uibuilds
cd %projectLocation%
call OPERAUIAuto.bat