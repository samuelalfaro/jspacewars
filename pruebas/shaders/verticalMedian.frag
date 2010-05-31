uniform sampler2D textureIn;
uniform float pixelSize;

void main(void){

	vec4 sum; 

	sum  = texture2D(textureIn, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t - 9.0*pixelSize));
	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t - 8.0*pixelSize));
	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t - 7.0*pixelSize));
	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t - 6.0*pixelSize));
	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t - 5.0*pixelSize));
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t - 4.0*pixelSize));
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t - 3.0*pixelSize));
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t - 2.0*pixelSize));
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t - pixelSize    ));
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t                ));
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t + pixelSize    ));
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t + 2.0*pixelSize));
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t + 3.0*pixelSize));
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t + 4.0*pixelSize));
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t + 5.0*pixelSize));
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t + 6.0*pixelSize));
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t + 7.0*pixelSize));
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t + 8.0*pixelSize));
   	sum += texture2D(textureIn, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t + 9.0*pixelSize));
	
   	gl_FragColor = sum/19.0;
}


  