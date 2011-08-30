#current="$(readlink -f $(dirname "$0"))"
#previous="$(pwd)"
#cd "$current"
cd "$(readlink -f $(dirname "$0"))"
#java -cp lib/gluegen-rt.jar:lib/lib/jogl.jar:lib/lib/xstream-1.3.jar:lib/ext/vecmath.jar:lib/ibxm-alpha51.jar:lib/jogg-0.0.7.jar:lib/jorbis-0.0.15.jar:JSpaceWars.jar -Djava.library.path=native/linux-i586 -splash:splash.jpg org.sam.red.servidor.ServidorJuego
java -cp lib/ext/vecmath.jar:lib/Filters.jar:lib/gluegen-rt.jar:lib/ibxm-alpha54.jar:lib/jars.txt:lib/joal.jar:lib/jogg-0.0.7.jar:lib/jogl.all.jar:lib/jorbis-0.0.15.jar:lib/nativewindow.all.jar:lib/newt.all.jar:lib/xstream-1.3.jar:../FengGUI/bin:bin -Djava.library.path=native/linux-i586 org.sam.jspacewars.ExampleGameMenuJOGL
