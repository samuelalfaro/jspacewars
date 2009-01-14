/* struct gl_LightSourceParameters {
	vec4 ambient; // Acli
	vec4 diffuse; // Dcli
	vec4 specular; // Scli
	vec4 position; // Ppli
	vec4 halfVector; // Derived: Hi
	vec3 spotDirection; // Sdli
	float spotExponent; // Srli
	float spotCutoff; // Crli
	// (range: [0.0,90.0], 180.0)
	float spotCosCutoff; // Derived: cos(Crli)
	// (range: [1.0,0.0],-1.0)
	float constantAttenuation; // K0
	float linearAttenuation; // K1
	float quadraticAttenuation;// K2
}; */

//uniform gl_LightSourceParameters gl_LightSource[gl_MaxLights];
uniform sampler2D textura;

varying vec3 pos, vNormal;

void Light(
	in    int  index,
	in    vec3 eye,
	in    vec3 pos,
	in    vec3 normal,
	inout vec3 ambient,
	inout vec3 diffuse,
	inout vec3 specular)
{
	//ambient  += gl_LightSource[index].ambient.rgb;
	if (gl_LightSource[index].position.w == 0.0){
		//Direccional
		float nDotL = dot(normal, normalize(gl_LightSource[index].position.xyz));
		if( nDotL > 0.0){
			diffuse += gl_LightSource[index].diffuse.rgb * nDotL;
			float nDotHV = dot(normal, gl_LightSource[index].halfVector.xyz);
			if( nDotHV > 0.0) 
				specular += gl_LightSource[index].specular.rgb * pow(nDotHV, gl_FrontMaterial.shininess);  
		}
	}else{
		vec3 L = normalize(gl_LightSource[index].position.xyz - pos); 
		
		if (gl_LightSource[index].spotCutoff == 180.0){
			//Puntual
			float nDotL = dot(normal, L);
			if (nDotL > 0.0){
				float d = distance(gl_LightSource[index].position.xyz,pos);
				float attenuation = 
					( gl_LightSource[index].constantAttenuation
					+ (gl_LightSource[index].linearAttenuation
					+ gl_LightSource[index].quadraticAttenuation * d) *d );
				diffuse  += gl_LightSource[index].diffuse.rgb * nDotL / attenuation;
		
				float nDotHV = dot( -reflect(L,normal) , eye );
				if( nDotHV > 0.0) 
					specular += gl_LightSource[index].specular.rgb * pow(nDotHV, gl_FrontMaterial.shininess) / attenuation;  
			}
		}else{
			// Foco
			float spotDot = dot(-L, normalize(gl_LightSource[index].spotDirection));
			if (spotDot >= gl_LightSource[index].spotCosCutoff){
				float nDotL = dot(normal, L);
				if (nDotL > 0.0){
					float d = distance(gl_LightSource[index].position.xyz,pos);
					float attenuation = pow(spotDot, gl_LightSource[index].spotExponent) /
						( gl_LightSource[index].constantAttenuation
						+ (gl_LightSource[index].linearAttenuation
						+ gl_LightSource[index].quadraticAttenuation * d) *d );
					diffuse  += gl_LightSource[index].diffuse.rgb * nDotL * attenuation;
		
					float nDotHV = dot( -reflect(L,normal), eye );
					if( nDotHV > 0.0) 
						specular += gl_LightSource[index].specular.rgb * pow(nDotHV, gl_FrontMaterial.shininess) * attenuation;  
				}
			}	
		}
	}
}

void main(){
	vec3 amb = vec3(0.0);
	vec3 diff = vec3(0.0);
	vec3 spec = vec3(0.0);

	vec3 eye = normalize(-pos);
	vec3 normal = normalize(vNormal);
	Light(0,eye, pos, normal, amb, diff, spec);
	Light(1, eye, pos, normal, amb, diff, spec);
	Light(2, eye, pos, normal, amb, diff, spec);

	vec3 color = 
	gl_FrontLightModelProduct.sceneColor.rgb +
		amb * gl_FrontMaterial.ambient.rgb +
		diff * texture2D(textura,gl_TexCoord[0].st).rgb;
	spec *= gl_FrontMaterial.specular.rgb;

	gl_FragColor = vec4(color + spec ,1.0);
	//gl_FragColor = vec4(texture2D(textura,gl_TexCoord[0].st).rgb ,1.0);
}