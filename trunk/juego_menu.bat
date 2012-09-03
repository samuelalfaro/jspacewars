@echo off
cd %~dp0
set CLASSPATH=%CLASSPATH%;lib/ext/vecmath.jar
set CLASSPATH=%CLASSPATH%;lib/gluegen-rt.jar
set CLASSPATH=%CLASSPATH%;lib/jogl-all.jar
set CLASSPATH=%CLASSPATH%;lib/joal.jar
set CLASSPATH=%CLASSPATH%;lib/ibxm-a61.jar
set CLASSPATH=%CLASSPATH%;lib/jogg-0.0.7.jar
set CLASSPATH=%CLASSPATH%;lib/jorbis-0.0.15.jar
set CLASSPATH=%CLASSPATH%;lib/xstream-1.4.2.jar
set CLASSPATH=%CLASSPATH%;bin 
@echo on
java org.sam.jspacewars.ExampleGameMenuJOGL
pause