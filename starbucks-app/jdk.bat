
@echo off
if %1==11 (
   set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-11.0.14.9-hotspot
) 
if %1==8 (
   set JAVA_HOME=C:\Program Files\Java\jre1.8.0_311
)
echo setting PATH
set Path=%JAVA_HOME%\bin;%Path%
java -version