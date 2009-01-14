uniform sampler2D textura;
varying vec4 color;
const vec3 blanco = vec3(1);

void main (void){
	float a = color.a * texture2D( textura, gl_TexCoord[0].st).a;
	gl_FragColor.rgb = mix(blanco,color.rgb, a);
	gl_FragColor.a = a;
}