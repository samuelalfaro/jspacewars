uniform sampler2D textureIn;
uniform float pixelSize;

void main(void){
	float angle = radians(45.0);
	vec2 desp = vec2(cos(angle),sin(angle));
	
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
	
   	gl_FragColor = sum/19;
}


  