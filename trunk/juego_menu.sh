cd "$(readlink -f $(dirname "$0"))"
java -cp bin:lib/ext/vecmath.jar:lib/gluegen-rt.jar:lib/jogl-all.jar:lib/joal.jar:lib/ibxm-a61.jar:lib/jogg-0.0.7.jar:lib/jorbis-0.0.15.jar:lib/xstream-1.4.2.jar org.sam.jspacewars.ExampleGameMenuJOGL
