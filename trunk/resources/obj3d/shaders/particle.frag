uniform sampler2D textura;

varying vec4 color;

float ZPosFromDepthBufferValue(float depthBufferValue){
	//const float fxn = gl_DepthRange.far * gl_DepthRange.near;
	//const float fmn = gl_DepthRange.far - gl_DepthRange.near;
	//return  fxn / ( gl_DepthRange.far - fmn * depthBufferValue );
	return gl_DepthRange.diff / gl_DepthRange.far * depthBufferValue ;
}

void main (void){
	gl_FragColor.rgb = color.rgb;
	//gl_FragColor.a   = color.a * texture2D( textura, gl_TexCoord[0].st ).a ;
	float a  = texture2D( textura, gl_TexCoord[0].st ).a ;
	//gl_FragColor.a  = color.a * max(0.0f, (min(1.0,gl_FragCoord.w) - ZPosFromDepthBufferValue(gl_FragDepth)));
	gl_FragColor.a  = texture2D( textura, gl_TexCoord[0].st ).a * ZPosFromDepthBufferValue(gl_FragCoord.z);
}