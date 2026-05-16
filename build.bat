@echo off
setlocal EnableExtensions EnableDelayedExpansion
cd /d "%~dp0"

set "APP_NAME=Rontl-Java-App"
set "PROJECT_DIR=%CD%"
set "SRC_DIR=src"
set "LIB_DIR=lib"
set "BUILD_DIR=build"
set "CLASSES_DIR=%BUILD_DIR%\classes"
set "DIST_DIR=dist"
set "MANIFEST=manifest.txt"
set "INFO_FILE=%SRC_DIR%\Core\Info.java"
set "JAR_FILE=%PROJECT_DIR%\%DIST_DIR%\%APP_NAME%.jar"
set "RAW_JAR_FILE=%PROJECT_DIR%\%BUILD_DIR%\%APP_NAME%-raw.jar"
set "SOURCES_FILE=%BUILD_DIR%\sources.txt"
set "PROGUARD_VERSION=7.9.1"
set "TOOLS_DIR=tools"
set "PROGUARD_URL=https://github.com/Guardsquare/proguard/releases/download/v%PROGUARD_VERSION%/proguard-%PROGUARD_VERSION%.zip"
set "PROGUARD_ZIP=%PROJECT_DIR%\%TOOLS_DIR%\proguard-%PROGUARD_VERSION%.zip"
set "PROGUARD_DIR=%PROJECT_DIR%\%TOOLS_DIR%\proguard-%PROGUARD_VERSION%"
set "PROGUARD_CMD=%PROGUARD_DIR%\bin\proguard.bat"
set "PROGUARD_CONFIG=%PROJECT_DIR%\%BUILD_DIR%\proguard.pro"
set "OBFUSCATION_DICTIONARY=%PROJECT_DIR%\%BUILD_DIR%\obfuscation-dictionary.txt"
set "OBFUSCATION_MAP=%PROJECT_DIR%\%DIST_DIR%\obfuscation-map.txt"
set "JAVAC=javac"
set "JAR=jar"
set "TARGET=target"
set "FIN_APP_DIR=..\Rontl-Java-App"

if exist "%FIN_APP_DIR%" rmdir /s /q "%FIN_APP_DIR%"
echo old build deleted "%FIN_APP_DIR%"

where "%JAVAC%" >nul 2>nul
if errorlevel 1 (
    if defined JAVA_HOME if exist "%JAVA_HOME%\bin\javac.exe" (
        set "JAVAC=%JAVA_HOME%\bin\javac.exe"
        set "JAR=%JAVA_HOME%\bin\jar.exe"
    )
)

where "%JAVAC%" >nul 2>nul
if errorlevel 1 (
    for /d %%D in ("%ProgramFiles%\Java\jdk*") do (
        if exist "%%~fD\bin\javac.exe" (
            set "JAVAC=%%~fD\bin\javac.exe"
            set "JAR=%%~fD\bin\jar.exe"
        )
    )
)

if not exist "%JAVAC%" (
    where "%JAVAC%" >nul 2>nul
    if errorlevel 1 (
        echo Error: javac not found. Install JDK and add it to PATH.
        exit /b 1
    )
)

where "%JAR%" >nul 2>nul
if errorlevel 1 (
    if defined JAVA_HOME if exist "%JAVA_HOME%\bin\jar.exe" (
        set "JAR=%JAVA_HOME%\bin\jar.exe"
    )
)

where "%JAR%" >nul 2>nul
if errorlevel 1 (
    for /d %%D in ("%ProgramFiles%\Java\jdk*") do (
        if exist "%%~fD\bin\jar.exe" (
            set "JAR=%%~fD\bin\jar.exe"
        )
    )
)

if not exist "%JAR%" (
    where "%JAR%" >nul 2>nul
    if errorlevel 1 (
        echo Error: jar not found. Install JDK and add it to PATH.
        exit /b 1
    )
)

if not exist "%SRC_DIR%" (
    echo Error: source folder "%SRC_DIR%" not found.
    exit /b 1
)

if not exist "%MANIFEST%" (
    echo Error: manifest file "%MANIFEST%" not found.
    exit /b 1
)

if not exist "%INFO_FILE%" (
    echo Error: info file "%INFO_FILE%" not found.
    exit /b 1
)

if exist "%BUILD_DIR%" rmdir /s /q "%BUILD_DIR%"
if not exist "%BUILD_DIR%" mkdir "%BUILD_DIR%"
if not exist "%CLASSES_DIR%" mkdir "%CLASSES_DIR%"
if not exist "%DIST_DIR%" mkdir "%DIST_DIR%"

echo Updating build number...
powershell -NoProfile -ExecutionPolicy Bypass -Command "$path=$env:INFO_FILE; $text=[IO.File]::ReadAllText($path); $pattern='(public\s+static\s+\w+\s+Build\s*=\s*)(\d+)(\s*;)'; if ($text -notmatch $pattern) { Write-Error 'Build variable not found in Info.java'; exit 1 }; $newText=[regex]::Replace($text,$pattern,{ param($m) $m.Groups[1].Value + ([int]$m.Groups[2].Value + 1) + $m.Groups[3].Value },1); $utf8=New-Object System.Text.UTF8Encoding($false); [IO.File]::WriteAllText($path,$newText,$utf8); Write-Host ('Build number updated to ' + ([regex]::Match($newText,$pattern).Groups[2].Value))"
if errorlevel 1 (
    echo Build failed while updating Info.java.
    exit /b 1
)

dir /s /b "%SRC_DIR%\*.java" > "%SOURCES_FILE%"
for %%A in ("%SOURCES_FILE%") do if %%~zA==0 (
    echo Error: no Java source files found in "%SRC_DIR%".
    exit /b 1
)

set "CP="
if exist "%LIB_DIR%\*.jar" (
    for %%J in ("%LIB_DIR%\*.jar") do (
        if defined CP (
            set "CP=!CP!;%%~fJ"
        ) else (
            set "CP=%%~fJ"
        )
    )
)

echo Compiling sources...
if defined CP (
    "%JAVAC%" -g:none -encoding UTF-8 -cp "!CP!" -d "%CLASSES_DIR%" @"%SOURCES_FILE%"
) else (
    "%JAVAC%" -g:none -encoding UTF-8 -d "%CLASSES_DIR%" @"%SOURCES_FILE%"
)

if errorlevel 1 (
    echo Build failed during compilation.
    exit /b 1
)

if exist "%SRC_DIR%\Assets" (
    echo Copying assets...
    robocopy "%SRC_DIR%\Assets" "%CLASSES_DIR%\Assets" /E >nul
    if errorlevel 8 (
        echo Build failed while copying assets.
        exit /b 1
    )
)

if exist "%LIB_DIR%\*.jar" (
    echo Adding libraries into jar...
    pushd "%CLASSES_DIR%"
    for %%J in ("..\..\%LIB_DIR%\*.jar") do (
        "%JAR%" xf "%%~fJ"
        if errorlevel 1 (
            popd
            echo Build failed while adding library %%~nxJ.
            exit /b 1
        )
    )
    popd

    if exist "%CLASSES_DIR%\META-INF\MANIFEST.MF" del /q "%CLASSES_DIR%\META-INF\MANIFEST.MF"
    if exist "%CLASSES_DIR%\META-INF\*.SF" del /q "%CLASSES_DIR%\META-INF\*.SF"
    if exist "%CLASSES_DIR%\META-INF\*.RSA" del /q "%CLASSES_DIR%\META-INF\*.RSA"
    if exist "%CLASSES_DIR%\META-INF\*.DSA" del /q "%CLASSES_DIR%\META-INF\*.DSA"
)

echo Creating temporary jar...
"%JAR%" cfm "%RAW_JAR_FILE%" "%MANIFEST%" -C "%CLASSES_DIR%" .

if errorlevel 1 (
    echo Build failed while creating temporary jar.
    exit /b 1
)

if not exist "%PROGUARD_CMD%" (
    echo Downloading ProGuard %PROGUARD_VERSION%...
    if not exist "%TOOLS_DIR%" mkdir "%TOOLS_DIR%"
    powershell -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference='Stop'; Invoke-WebRequest -Uri $env:PROGUARD_URL -OutFile $env:PROGUARD_ZIP; Expand-Archive -LiteralPath $env:PROGUARD_ZIP -DestinationPath $env:TOOLS_DIR -Force"
    if errorlevel 1 (
        echo Build failed while downloading ProGuard.
        exit /b 1
    )
)

if not exist "%PROGUARD_CMD%" (
    echo Error: ProGuard was not found at "%PROGUARD_CMD%".
    exit /b 1
)

echo Preparing obfuscation...
powershell -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference='Stop'; $chars='abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'; $rnd=[Random]::new(); $names=New-Object System.Collections.Generic.HashSet[string]; while ($names.Count -lt 600) { $len=$rnd.Next(8,15); $name=-join (1..$len | ForEach-Object { $chars[$rnd.Next($chars.Length)] }); [void]$names.Add($name) }; [IO.File]::WriteAllLines($env:OBFUSCATION_DICTIONARY, $names, [Text.UTF8Encoding]::new($false)); $cfg=@('-injars ''' + $env:RAW_JAR_FILE + '''','-outjars ''' + $env:JAR_FILE + '''','-libraryjars ''<java.home>/jmods/java.base.jmod''','-libraryjars ''<java.home>/jmods/java.desktop.jmod''','-dontshrink','-dontoptimize','-ignorewarnings','-dontwarn','-dontnote','-allowaccessmodification','-overloadaggressively','-useuniqueclassmembernames','-repackageclasses ''''','-obfuscationdictionary ''' + $env:OBFUSCATION_DICTIONARY + '''','-classobfuscationdictionary ''' + $env:OBFUSCATION_DICTIONARY + '''','-packageobfuscationdictionary ''' + $env:OBFUSCATION_DICTIONARY + '''','-printmapping ''' + $env:OBFUSCATION_MAP + '''','-renamesourcefileattribute SourceFile','-keepattributes Exceptions,InnerClasses,EnclosingMethod,Signature','-adaptresourcefilenames **.properties,**.xml,META-INF/MANIFEST.MF','-adaptresourcefilecontents **.properties,**.xml,META-INF/MANIFEST.MF','-keep class javazoom.** { *; }','-keep interface javazoom.** { *; }','-keep public class ProgramStart {','    public static void main(java.lang.String[]);','}'); [IO.File]::WriteAllLines($env:PROGUARD_CONFIG, $cfg, [Text.UTF8Encoding]::new($false))"
if errorlevel 1 (
    echo Build failed while preparing obfuscation.
    exit /b 1
)

echo Obfuscating jar...
call "%PROGUARD_CMD%" @"%PROGUARD_CONFIG%"

if errorlevel 1 (
    echo Build failed during obfuscation.
    exit /b 1
)

echo Done: %JAR_FILE%
echo Run with: java -jar "%JAR_FILE%"
echo Obfuscation map: %OBFUSCATION_MAP%

if exist "%BUILD_DIR%" rmdir /s /q "%BUILD_DIR%"
if exist "%TARGET%" rmdir /s /q "%TARGET%"
echo Cache deleted: "%BUILD_DIR%", "%TARGET%"

move %DIST_DIR% %FIN_APP_DIR%
echo JAR "%JAR_FILE%" moved to "%FIN_APP_DIR%"
pause
