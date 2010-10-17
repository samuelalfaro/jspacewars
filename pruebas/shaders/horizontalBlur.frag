uniform sampler2D textureIn; 
uniform float pixelSize;
 
void main(void){

	vec2 inc = vec2( pixelSize, 0.0 );
	vec2 st  = vec2( gl_TexCoord[0].s - 9.0 * pixelSize , gl_TexCoord[0].t );
	
	vec4 sum;

	sum  = texture2D( textureIn, st ) * 0.008074; st += inc;
	sum += texture2D( textureIn, st ) * 0.013735; st += inc;
	sum += texture2D( textureIn, st ) * 0.021948; st += inc;
	sum += texture2D( textureIn, st ) * 0.032948; st += inc;
	sum += texture2D( textureIn, st ) * 0.046464; st += inc;
   	sum += texture2D( textureIn, st ) * 0.061555; st += inc;
   	sum += texture2D( textureIn, st ) * 0.076606; st += inc;
   	sum += texture2D( textureIn, st ) * 0.089562; st += inc;
   	sum += texture2D( textureIn, st ) * 0.098364; st += inc;
   	sum += texture2D( textureIn, st ) * 0.101487; st += inc;
	sum += texture2D( textureIn, st ) * 0.098364; st += inc;
   	sum += texture2D( textureIn, st ) * 0.089562; st += inc;
   	sum += texture2D( textureIn, st ) * 0.076606; st += inc;
   	sum += texture2D( textureIn, st ) * 0.061555; st += inc;
   	sum += texture2D( textureIn, st ) * 0.046464; st += inc;
   	sum += texture2D( textureIn, st ) * 0.032948; st += inc;
   	sum += texture2D( textureIn, st ) * 0.021948; st += inc;
   	sum += texture2D( textureIn, st ) * 0.013735; st += inc;
   	sum += texture2D( textureIn, st ) * 0.008074;
	
   	gl_FragColor = sum;
}




  