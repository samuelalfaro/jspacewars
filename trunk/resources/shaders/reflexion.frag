const vec3 Xunitvec = vec3 (1.0, 0.0, 0.0);
const vec3 Yunitvec = vec3 (0.0, 1.0, 0.0);

uniform sampler2D difuseMap;
uniform sampler2D reflexionMap;
uniform sampler2D reflexionEnv;

varying vec3 pos;
varying vec3 vNormal;

void Light(
	in    gl_LightSourceParameters  lightSource,
	in    vec3 eye,
	in    vec3 pos,
	in    vec3 normal,
	inout vec3 ambient,
	inout vec3 diffuse,
	inout vec3 specular)
{
	ambient  += lightSource.ambient.rgb;
	float nDotL = max( 0.0, dot(normal, normalize(lightSource.position.xyz)));
	diffuse += lightSource.diffuse.rgb * nDotL;
	//float nDotHV =  max( 0.0, dot(normal, lightSource.halfVector.xyz) );
	float nDotHV =  max( 0.0, dot( reflect( normalize(-lightSource.position.xyz), normal ) , eye ) );
	specular += lightSource.specular.rgb * pow( nDotHV, gl_FrontMaterial.shininess );
}

void main(){
	vec3 amb = vec3(0.0);
	vec3 diff = vec3(0.0);
	vec3 spec = vec3(0.0);

	vec3 eye = normalize(-pos);
	vec3 normal = normalize(vNormal);

	Light( gl_LightSource[0], eye, pos, normal, amb, diff, spec);
	Light( gl_LightSource[1], eye, pos, normal, amb, diff, spec);
	Light( gl_LightSource[2], eye, pos, normal, amb, diff, spec);

	// Compute reflection vector
	vec3 reflectDir = -reflect( eye, normal );

	// Compute altitude and azimuth angles
	vec2 index;

	index.t = dot( reflectDir, Yunitvec );
	reflectDir.y = 0.0;
	index.s =  sign(-reflectDir.z) * dot(normalize(reflectDir), Xunitvec);

    	// Translate index values into proper range
	index *= 0.5;
	index += 0.5;

	//*
	gl_FragColor = 
		vec4(
			min(
				max(
					gl_FrontLightModelProduct.sceneColor.rgb +
					amb * gl_FrontMaterial.ambient.rgb +
					diff * texture2D( difuseMap, gl_TexCoord[0].st ).rgb +
					(
						spec * gl_FrontMaterial.specular.rgb
						+ 2.0 * ( texture2D( reflexionEnv, index ).rgb - 0.5 )
			 		) * texture2D( reflexionMap, gl_TexCoord[0].st ).x,
				 	0.0 
				),
				1.0 
			),
			1.0 
		);
	/*/
	//gl_FragColor = vec4( diff, 1.0);
	gl_FragColor = vec4(texture2D( reflexionEnv, index ).rgb, 1.0);
	//gl_FragColor = vec4(  min( max( 2.0 * ( texture2D( reflexionEnv, index ).rgb - 0.5 ) * texture2D( reflexionMap, gl_TexCoord[0].st ).x, 0.0 ), 1.0), 1.0);
	//*/
}
