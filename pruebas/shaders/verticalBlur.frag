uniform sampler2D textureIn;
uniform float pixelSize;

void main(void){

	vec4 sum; 

	sum  = texture2D(textureIn, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t - 9.0*pixelSize)) * 0.008074;
	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t - 8.0*pixelSize)) * 0.013735;
	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t - 7.0*pixelSize)) * 0.021948;
	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t - 6.0*pixelSize)) * 0.032948;
	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t - 5.0*pixelSize)) * 0.046464;
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t - 4.0*pixelSize)) * 0.061555;
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t - 3.0*pixelSize)) * 0.076606;
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t - 2.0*pixelSize)) * 0.089562;
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t - pixelSize    )) * 0.098364;
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t                )) * 0.101487;
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t + pixelSize    )) * 0.098364;
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t + 2.0*pixelSize)) * 0.089562;
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t + 3.0*pixelSize)) * 0.076606;
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t + 4.0*pixelSize)) * 0.061555;
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t + 5.0*pixelSize)) * 0.046464;
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t + 6.0*pixelSize)) * 0.032948;
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t + 7.0*pixelSize)) * 0.021948;
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t + 8.0*pixelSize)) * 0.013735;
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t + 9.0*pixelSize)) * 0.008074;
	
   	gl_FragColor = sum;
}


  