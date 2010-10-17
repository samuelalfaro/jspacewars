uniform sampler2D textureIn; 
uniform float pixelSize;
 
void main(void){

	vec2 inc = vec2( pixelSize, 0.0 );
	vec2 st  = vec2( gl_TexCoord[0].s - 9.0 * pixelSize , gl_TexCoord[0].t );
	
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




  