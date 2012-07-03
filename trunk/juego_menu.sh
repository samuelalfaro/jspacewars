cd "$(readlink -f $(dirname "$0"))"
java -cp bin:lib/ext/vecmath.jar:lib/gluegen-rt.jar:lib/ibxm-alpha54.jar:lib/joal.jar:lib/jogg-0.0.7.jar:lib/jogl.all.jar:lib/jorbis-0.0.15.jar:lib/nativewindow.all.jar:lib/newt.all.jar:lib/xstream-1.4.2.jar -Djava.library.path=native/linux-i586 org.sam.jspacewars.ExampleGameMenuJOGL
