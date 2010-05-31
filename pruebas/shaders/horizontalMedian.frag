uniform sampler2D textureIn; 
uniform float pixelSize;
 
void main(void){

	vec4 sum;

	sum  = texture2D(textureIn, vec2(gl_TexCoord[0].s - 9.0*pixelSize, gl_TexCoord[0].t));
	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s - 8.0*pixelSize, gl_TexCoord[0].t));
	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s - 7.0*pixelSize, gl_TexCoord[0].t));
	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s - 6.0*pixelSize, gl_TexCoord[0].t));
	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s - 5.0*pixelSize, gl_TexCoord[0].t));
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s - 4.0*pixelSize, gl_TexCoord[0].t));
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s - 3.0*pixelSize, gl_TexCoord[0].t));
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s - 2.0*pixelSize, gl_TexCoord[0].t));
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s - pixelSize,     gl_TexCoord[0].t));
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s,                 gl_TexCoord[0].t));
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s + pixelSize,     gl_TexCoord[0].t));
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s + 2.0*pixelSize, gl_TexCoord[0].t));
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s + 3.0*pixelSize, gl_TexCoord[0].t));
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s + 4.0*pixelSize, gl_TexCoord[0].t));
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s + 5.0*pixelSize, gl_TexCoord[0].t));
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s + 6.0*pixelSize, gl_TexCoord[0].t));
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s + 7.0*pixelSize, gl_TexCoord[0].t));
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s + 8.0*pixelSize, gl_TexCoord[0].t));
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s + 9.0*pixelSize, gl_TexCoord[0].t));
 
   	gl_FragColor = sum/19.0;
}




  