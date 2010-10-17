uniform sampler2D textureIn;
uniform float pixelSize;

void main(void){

	vec2 inc = vec2( 0.0, pixelSize );
	vec2 st  = vec2( gl_TexCoord[0].s, gl_TexCoord[0].t - 9.0*pixelSize );
	
	vec4 sum;

	sum  = texture2D( textureIn, st ); st += inc;
	sum += texture2D( textureIn, st ); st += inc;
	sum += texture2D( textureIn, st ); st += inc;
	sum += texture2D( textureIn, st ); st += inc;
	sum += texture2D( textureIn, st ); st += inc;
   	sum += texture2D( textureIn, st ); st += inc;
   	sum += texture2D( textureIn, st ); st += inc;
   	sum += texture2D( textureIn, st ); st += inc;
   	sum += texture2D( textureIn, st ); st += inc;
   	sum += texture2D( textureIn, st ); st += inc;
	sum += texture2D( textureIn, st ); st += inc;
   	sum += texture2D( textureIn, st ); st += inc;
   	sum += texture2D( textureIn, st ); st += inc;
   	sum += texture2D( textureIn, st ); st += inc;
   	sum += texture2D( textureIn, st ); st += inc;
   	sum += texture2D( textureIn, st ); st += inc;
   	sum += texture2D( textureIn, st ); st += inc;
   	sum += texture2D( textureIn, st ); st += inc;
   	sum += texture2D( textureIn, st );
	
   	gl_FragColor = sum / 19.0;
}


  