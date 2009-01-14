/*
struct gl_LightSourceParameters {
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
};
*/

const float R0 = 0.3;
varying vec3 difuse;
varying vec3 specular;
varying float nDotHV;
varying float fresnel;

void main()
{
    difuse   = vec3(0.0);
    specular = vec3(0.0);
	
    gl_TexCoord[0] = gl_MultiTexCoord0;
    vec3 pos = (gl_ModelViewMatrix * gl_Vertex).xyz;
    vec3 eye = - normalize(pos);
    vec3 normal = normalize(gl_NormalMatrix * gl_Normal);
    gl_TexCoord[1].xyz = reflect(-eye,normal);
    
    float nDotL = dot( normal, gl_LightSource[0].position.xyz);
	if( nDotL > 0.0){
		difuse = gl_LightSource[0].diffuse.rgb * nDotL;
		nDotHV =  (gl_LightSource[0].position.w == 0.0) ?
			dot(gl_LightSource[0].halfVector.xyz, normal): 
			dot(normalize(gl_LightSource[0].position.xyz - pos),normal); 
		specular = gl_LightSource[0].specular.rgb;
	}
   	//fresnel = clamp( R0 + (1.0-R0) * pow(1.0-dot(normalize(gl_LightSource[0].position.xyz), normal), 5.0) , 0.0, 1.0);
   	fresnel = 1.0 - (R0 + (1.0-R0) * pow(1.0-abs(nDotL), 3.0));
   	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}
