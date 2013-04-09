cd "$(readlink -f $(dirname "$0"))"
java -cp bin:lib/ext/vecmath.jar:lib/jogl_old/gluegen-rt.jar:lib/jogl_old/joal.jar:lib/jogl_old/jogl.all.jar:lib/jogl_old/nativewindow.all.jar:lib/jogl_old/newt.all.jar:lib/ibxm-alpha54.jar:lib/jogg-0.0.7.jar:lib/jorbis-0.0.15.jar:lib/xstream-1.4.2.jar -Djava.library.path=native/linux-i586 org.sam.jspacewars.ExampleGameMenuJOGL
