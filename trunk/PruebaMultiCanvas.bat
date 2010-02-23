@echo off
cd %~dp0
set CLASSPATH=%CLASSPATH%;
set CLASSPATH=%CLASSPATH%;lib/ext/vecmath.jar
set CLASSPATH=%CLASSPATH%;lib/gluegen-rt.jar
set CLASSPATH=%CLASSPATH%;lib/jogl.jar
set CLASSPATH=%CLASSPATH%;lib/FengGUI.jar
set CLASSPATH=%CLASSPATH%;lib/ibxm-alpha51.jar
set CLASSPATH=%CLASSPATH%;lib/jogg-0.0.7.jar
set CLASSPATH=%CLASSPATH%;lib/jorbis-0.0.15.jar
set CLASSPATH=%CLASSPATH%;lib/xstream-1.3.jar
rem set CLASSPATH=%CLASSPATH%;JSpaceWars.jar
set CLASSPATH=%CLASSPATH%;.
set CLASSPATH=%CLASSPATH%;bin
set CLASSPATH=%CLASSPATH%;../pruebas/bin
@echo on
java -Djava.library.path=./native/windows -Dsun.java2d.noddraw=true -Djogl.GLContext.optimize pruebas.jogl.MultipleCanvasExample
pause