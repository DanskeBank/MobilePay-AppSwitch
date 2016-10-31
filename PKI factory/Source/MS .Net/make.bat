REM Make sure msbuild.exe is in your PATH
REM Output is in .\DanskeBank.PKIFactory.Library\bin\Release

msbuild DanskeBank.PKIFactory.sln /t:Build /p:Configuration=Release

pause
