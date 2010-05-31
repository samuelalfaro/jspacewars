uniform sampler2D textureIn;
uniform float pixelSize;
uniform vec2 center;

void main(void){

	vec2 desp = normalize( gl_TexCoord[0].st - center);
	
	vec4 sum; 

	sum  = texture2D( textureIn, gl_TexCoord[0].st - 9.0 * pixelSize * desp );
	sum += texture2D( textureIn, gl_TexCoord[0].st - 8.0 * pixelSize * desp );
	sum += texture2D( textureIn, gl_TexCoord[0].st - 7.0 * pixelSize * desp );
	sum += texture2D( textureIn, gl_TexCoord[0].st - 6.0 * pixelSize * desp );
	sum += texture2D( textureIn, gl_TexCoord[0].st - 5.0 * pixelSize * desp );
   	sum += texture2D( textureIn, gl_TexCoord[0].st - 4.0 * pixelSize * desp );
   	sum += texture2D( textureIn, gl_TexCoord[0].st - 3.0 * pixelSize * desp );
   	sum += texture2D( textureIn, gl_TexCoord[0].st - 2.0 * pixelSize * desp );
   	sum += texture2D( textureIn, gl_TexCoord[0].st       - pixelSize * desp );
   	sum += texture2D( textureIn, gl_TexCoord[0].st                          );
	sum += texture2D( textureIn, gl_TexCoord[0].st       - pixelSize * desp );
   	sum += texture2D( textureIn, gl_TexCoord[0].st + 2.0 * pixelSize * desp );
   	sum += texture2D( textureIn, gl_TexCoord[0].st + 3.0 * pixelSize * desp );
   	sum += texture2D( textureIn, gl_TexCoord[0].st + 4.0 * pixelSize * desp );
   	sum += texture2D( textureIn, gl_TexCoord[0].st + 5.0 * pixelSize * desp );
   	sum += texture2D( textureIn, gl_TexCoord[0].st + 6.0 * pixelSize * desp );
   	sum += texture2D( textureIn, gl_TexCoord[0].st + 7.0 * pixelSize * desp );
   	sum += texture2D( textureIn, gl_TexCoord[0].st + 8.0 * pixelSize * desp );
   	sum += texture2D( textureIn, gl_TexCoord[0].st + 9.0 * pixelSize * desp );
	
   	gl_FragColor = sum / 19.0f;
}


  