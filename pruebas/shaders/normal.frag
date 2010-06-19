varying vec3 lightVec;
varying vec3 eyeVec;

uniform sampler2D normalMap;

void main(){
	//*
	vec3 lVec = normalize(lightVec);
	vec3 vVec = normalize(eyeVec);

	vec3 normal = normalize( 2.0 * texture2D(normalMap, gl_TexCoord[0].st).xyz - 1.0 );

	vec4 vAmbient = gl_LightSource[0].ambient * gl_FrontMaterial.ambient;

	float diffuse = max( dot(lVec, normal), 0.0 );
	
	vec4 vDiffuse = gl_LightSource[0].diffuse * gl_FrontMaterial.diffuse * diffuse;	

	float specular = diffuse > 0.0 ? pow( max( 0.0, dot( reflect( lVec, normal ) , vVec ) ), gl_FrontMaterial.shininess ) : 0.0;
	
	vec4 vSpecular = gl_LightSource[0].specular * gl_FrontMaterial.specular * specular;	
	
	gl_FragColor = clamp(vAmbient + vDiffuse + vSpecular, 0.0, 1.0);
	
	/*/
	float r = lightVec.x;
	float b = eyeVec.y;
	
	gl_FragColor = vec4( r, 0.0, b, 1.0 );
	//*/
}
