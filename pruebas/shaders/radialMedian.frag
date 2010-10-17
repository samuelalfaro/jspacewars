uniform sampler2D textureIn;
uniform float pixelSize;
uniform vec2 center;

void main(void){

	vec2 inc = normalize( gl_TexCoord[0].st - center) * pixelSize;
	vec2 st  = gl_TexCoord[0].st - 9.0 * inc;
	
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


  