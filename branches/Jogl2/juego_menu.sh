#current="$(readlink -f $(dirname "$0"))"
#previous="$(pwd)"
#cd "$current"
cd "$(readlink -f $(dirname "$0"))"
#java -cp lib/gluegen-rt.jar:lib/jogl.jar:lib/xstream-1.3.jar:lib/ext/vecmath.jar:lib/ibxm-alpha51.jar:lib/jogg-0.0.7.jar:lib/jorbis-0.0.15.jar:JSpaceWars.jar -Djava.library.path=native/linux-i586 -splash:splash.jpg org.sam.red.servidor.ServidorJuego
java -cp lib/ext/vecmath.jar:lib/gluegen-rt.jar:lib/jogl.jar:lib/FengGUI.jar:lib/xstream-1.3.jar:lib/ibxm-alpha51.jar:lib/jogg-0.0.7.jar:lib/jorbis-0.0.15.jar:bin -Djava.library.path=native/linux-i586 org.sam.jspacewars.ExampleGameMenuJOGL
