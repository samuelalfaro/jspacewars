uniform sampler2D textureIn;
uniform float pixelSize;
uniform float corte;
uniform float bloom;
 
void main(void){
	vec4 texel  = texture2D(textureIn, gl_TexCoord[0].st);
	float luminance = texel.r * 0.299 + texel.g * 0.587 + texel.b * 0.114;
  	gl_FragColor = vec4( luminance >= corte ? pow( texel.rgb, vec3(1.0/bloom) ) : vec3(0.0), texel.a );
}


  