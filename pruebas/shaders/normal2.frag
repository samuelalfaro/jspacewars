varying vec3 pos;
varying vec3 n;
varying vec4 t;

uniform sampler2D normalMap;

void Light(
	in    gl_LightSourceParameters  lightSource,
	in    mat3 tbnBasis,
	in    vec3 pos,
	in    vec3 eyeVec,
	in    vec3 normal,
	inout vec3 ambient,
	inout vec3 diffuse,
	inout vec3 specular)
{
	ambient  += lightSource.ambient.rgb;
	//Direccional lightSource.position.w = 0.0
	vec3 lightVec = tbnBasis * normalize( lightSource.position.xyz - lightSource.position.w * pos );
	// Solo se calcula la distancia en las luces puntuales o en los focos en otro caso sera 0
	float d = lightSource.position.w * distance( lightSource.position.xyz, pos );
	
	// lightSource.constantAttenuation por defecto es 1, por tanto en la luces direccionales la attenuation sera 1
	float attenuation = 1.0 / 
		( d * ( d * lightSource.quadraticAttenuation + lightSource.linearAttenuation ) + lightSource.constantAttenuation );
	
	float nDotL = dot(normal, lightVec);
	float cut   = step( 0.0, nDotL );
	
	//Foco si 0 <= lightSource.spotCutoff <= 90.0 por defecto spotCutoff = 180
	float esFoco = 1.0 - step( 90.0, lightSource.spotCutoff );
	
	float spotDot = esFoco * dot( -lightVec, tbnBasis * lightSource.spotDirection );

	attenuation *= mix( 1.0, pow( spotDot * step( lightSource.spotCosCutoff, spotDot ), lightSource.spotExponent ), esFoco);

	diffuse  += max( 0.0, nDotL ) * lightSource.diffuse.rgb * attenuation;
	
	float rDotEV = dot( reflect( -lightVec, normal), eyeVec );
	specular +=  cut * lightSource.specular.rgb * pow( max( 0.0, rDotEV ), gl_FrontMaterial.shininess ) * attenuation;
}

void main(){
	
	//*
	vec3 nBasis = normalize(n);
	// Gram-Schmidt orthogonalize
	vec3 tBasis = normalize( t.xyz - nBasis * dot(nBasis,t.xyz) );
	vec3 bBasis = cross(nBasis, tBasis) * sign(t.w);
	
	mat3 tbnBasis = mat3(
		tBasis.x, bBasis.x, nBasis.x,
		tBasis.y, bBasis.y, nBasis.y,
		tBasis.z, bBasis.z, nBasis.z
	);
	
	vec3 eye = tbnBasis * normalize(-pos);
	vec3 normal = normalize( 2.0 * texture2D(normalMap, gl_TexCoord[0].st).xyz - 1.0 );
	/*/
	mat3 tbnBasis = mat3(
		1.0, 0.0, 0.0,
		0.0, 1.0, 0.0,
		0.0, 0.0, 1.0
	);
	vec3 normal = normalize(n);
	vec3 eye = tbnBasis * normalize(-pos);
	//*/
	
	vec3 amb = vec3(0.0);
	vec3 diff = vec3(0.0);
	vec3 spec = vec3(0.0);

	Light( gl_LightSource[0], tbnBasis, pos, eye, normal, amb, diff, spec);
	Light( gl_LightSource[1], tbnBasis, pos, eye, normal, amb, diff, spec);
	Light( gl_LightSource[2], tbnBasis, pos, eye, normal, amb, diff, spec);

	gl_FragColor = vec4( clamp(
		amb  * gl_FrontMaterial.ambient.rgb +
		diff * gl_FrontMaterial.diffuse.rgb + 
		spec * gl_FrontMaterial.specular.rgb, 0.0, 1.0), 1.0);
}
