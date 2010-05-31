uniform sampler2D textureIn;
uniform float pixelSize;
uniform vec2 center;

void main(void){

	vec2 desp = normalize( gl_TexCoord[0].st - center);
	
	vec4 sum; 

	sum  = texture2D( textureIn, gl_TexCoord[0].st - 9.0 * pixelSize * desp ) * 0.008074;
	sum += texture2D( textureIn, gl_TexCoord[0].st - 8.0 * pixelSize * desp ) * 0.013735;
	sum += texture2D( textureIn, gl_TexCoord[0].st - 7.0 * pixelSize * desp ) * 0.021948;
	sum += texture2D( textureIn, gl_TexCoord[0].st - 6.0 * pixelSize * desp ) * 0.032948;
	sum += texture2D( textureIn, gl_TexCoord[0].st - 5.0 * pixelSize * desp ) * 0.046464;
   	sum += texture2D( textureIn, gl_TexCoord[0].st - 4.0 * pixelSize * desp ) * 0.061555;
   	sum += texture2D( textureIn, gl_TexCoord[0].st - 3.0 * pixelSize * desp ) * 0.076606;
   	sum += texture2D( textureIn, gl_TexCoord[0].st - 2.0 * pixelSize * desp ) * 0.089562;
   	sum += texture2D( textureIn, gl_TexCoord[0].st       - pixelSize * desp ) * 0.098364;
   	sum += texture2D( textureIn, gl_TexCoord[0].st                          ) * 0.101487;
	sum += texture2D( textureIn, gl_TexCoord[0].st       - pixelSize * desp ) * 0.098364;
   	sum += texture2D( textureIn, gl_TexCoord[0].st + 2.0 * pixelSize * desp ) * 0.089562;
   	sum += texture2D( textureIn, gl_TexCoord[0].st + 3.0 * pixelSize * desp ) * 0.076606;
   	sum += texture2D( textureIn, gl_TexCoord[0].st + 4.0 * pixelSize * desp ) * 0.061555;
   	sum += texture2D( textureIn, gl_TexCoord[0].st + 5.0 * pixelSize * desp ) * 0.046464;
   	sum += texture2D( textureIn, gl_TexCoord[0].st + 6.0 * pixelSize * desp ) * 0.032948;
   	sum += texture2D( textureIn, gl_TexCoord[0].st + 7.0 * pixelSize * desp ) * 0.021948;
   	sum += texture2D( textureIn, gl_TexCoord[0].st + 8.0 * pixelSize * desp ) * 0.013735;
   	sum += texture2D( textureIn, gl_TexCoord[0].st + 9.0 * pixelSize * desp ) * 0.008074;
	
   	gl_FragColor = sum;
}


  