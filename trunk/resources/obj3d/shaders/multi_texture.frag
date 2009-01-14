uniform sampler2D difuseMap;
uniform sampler2D reflexionMap;
uniform samplerCube reflexionEnv;

varying vec3 difuse;
varying vec3 specular;
varying float nDotHV;
varying float fresnel;

void main(){
	vec3 color = texture2D(difuseMap,gl_TexCoord[0].st).rgb*difuse;
	float shininess = texture2D(reflexionMap,gl_TexCoord[0].st).a;
	//vec3 reflection = textureCube(reflexionEnv,gl_TexCoord[1].xyz).rgb*fresnel*shininess;
	vec3 reflection = textureCube(reflexionEnv,gl_TexCoord[1].xyz).rgb;
	//gl_FragColor = vec4(color+reflection+(specular*((shininess<0.05)?shininess:pow(nDotHV,shininess*64.0))),1.0);
	gl_FragColor = vec4(mix(color,reflection,shininess*fresnel)+specular*pow(nDotHV,64.0),1.0);
}