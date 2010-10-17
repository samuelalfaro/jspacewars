uniform sampler2D textureIn;
uniform float pixelSize;

void main(void){

	float angle = radians(45.0);
	
	vec2 inc = vec2(cos(angle),sin(angle)) * pixelSize;
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


  