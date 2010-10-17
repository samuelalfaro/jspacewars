uniform sampler2D textureIn;
uniform float pixelSize;

void main(void){

	float angle = radians(90.0);
	
	vec2 inc = vec2(cos(angle),sin(angle)) * pixelSize;
	vec2 st  = gl_TexCoord[0].st - 9.0 * inc;
	
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


  