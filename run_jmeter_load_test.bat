@echo off
REM ============================================
REM JMeter Load Test Runner for Java 17+
REM ============================================

echo.
echo ========================================
echo HR Application Load Test
echo ========================================
echo.

REM Check if JMeter is installed
set JMETER_HOME=C:\apache-jmeter-5.6.3
if not exist "%JMETER_HOME%\bin\jmeter.bat" (
    echo ERROR: JMeter not found at %JMETER_HOME%
    echo Please download and extract Apache JMeter 5.6.3 from:
    echo https://jmeter.apache.org/download_jmeter.cgi
    echo.
    echo After extraction, rename the folder to: apache-jmeter-5.6.3
    echo And place it at: C:\
    echo.
    pause
    exit /b 1
)

REM Set Java options for Java 17+ compatibility
set JVM_ARGS=--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.desktop/javax.swing=ALL-UNNAMED --add-opens java.desktop/java.awt=ALL-UNNAMED

REM Increase JMeter heap size
set HEAP=-Xms2g -Xmx4g

echo Configuration:
echo - JMeter Home: %JMETER_HOME%
echo - Users: 200
echo - Ramp-up: 60 seconds
echo - Server: 13.205.138.59:8080
echo.

echo Running Load Test...
echo.
echo IMPORTANT: Make sure your application is running at http://13.205.138.59:8080
echo.

REM Delete old results file if exists
if exist "C:\Users\Kartik\Documents\GIthub\Human_Resource_Application\results.jtl" (
    echo Deleting old results file...
    del "C:\Users\Kartik\Documents\GIthub\Human_Resource_Application\results.jtl"
)

REM Delete old report folder if exists
if exist "C:\Users\Kartik\Documents\GIthub\Human_Resource_Application\html-report" (
    echo Deleting old report folder...
    rmdir /s /q "C:\Users\Kartik\Documents\GIthub\Human_Resource_Application\html-report"
)

echo.
pause

REM Navigate to JMeter folder
cd /d "%JMETER_HOME%\bin"

REM Run JMeter in Non-GUI mode with report generation
REM Usage: jmeter -n -t testplan.jmx -l results.jtl -e -o report-folder
jmeter -n -t "C:\Users\Kartik\Documents\GIthub\Human_Resource_Application\HR_API_LoadTest.jmx" -l "C:\Users\Kartik\Documents\GIthub\Human_Resource_Application\results.jtl" -e -o "C:\Users\Kartik\Documents\GIthub\Human_Resource_Application\html-report"

echo.
echo ========================================
echo Test Completed!
echo ========================================
echo.

echo Results saved to:
echo - Raw data: C:\Users\Kartik\Documents\GIthub\Human_Resource_Application\results.jtl
echo - HTML Report: C:\Users\Kartik\Documents\GIthub\Human_Resource_Application\html-report\index.html
echo.

echo To view detailed HTML report, open:
echo C:\Users\Kartik\Documents\GIthub\Human_Resource_Application\html-report\index.html
echo.

pause