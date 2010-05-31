uniform sampler2D textureIn; 
uniform float pixelSize;
 
void main(void){

	vec4 sum;

	sum  = texture2D(textureIn, vec2(gl_TexCoord[0].s - 9.0*pixelSize, gl_TexCoord[0].t)) * 0.008074;
	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s - 8.0*pixelSize, gl_TexCoord[0].t)) * 0.013735;
	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s - 7.0*pixelSize, gl_TexCoord[0].t)) * 0.021948;
	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s - 6.0*pixelSize, gl_TexCoord[0].t)) * 0.032948;
	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s - 5.0*pixelSize, gl_TexCoord[0].t)) * 0.046464;
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s - 4.0*pixelSize, gl_TexCoord[0].t)) * 0.061555;
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s - 3.0*pixelSize, gl_TexCoord[0].t)) * 0.076606;
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s - 2.0*pixelSize, gl_TexCoord[0].t)) * 0.089562;
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s - pixelSize,     gl_TexCoord[0].t)) * 0.098364;
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s,                 gl_TexCoord[0].t)) * 0.101487;
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s + pixelSize,     gl_TexCoord[0].t)) * 0.098364;
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s + 2.0*pixelSize, gl_TexCoord[0].t)) * 0.089562;
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s + 3.0*pixelSize, gl_TexCoord[0].t)) * 0.076606;
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s + 4.0*pixelSize, gl_TexCoord[0].t)) * 0.061555;
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s + 5.0*pixelSize, gl_TexCoord[0].t)) * 0.046464;
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s + 6.0*pixelSize, gl_TexCoord[0].t)) * 0.032948;
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s + 7.0*pixelSize, gl_TexCoord[0].t)) * 0.021948;
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s + 8.0*pixelSize, gl_TexCoord[0].t)) * 0.013735;
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s + 9.0*pixelSize, gl_TexCoord[0].t)) * 0.008074;
 
   	gl_FragColor = sum;
}




  