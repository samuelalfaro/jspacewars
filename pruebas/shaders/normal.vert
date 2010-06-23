varying vec3 lightVec;
varying vec3 eyeVec;

attribute vec4 vTangent;
	
void main(){
	//*
	gl_TexCoord[0] =  gl_MultiTexCoord0;

	// Building the matrix Eye Space -> Tangent Space
	vec3 n = normalize(gl_NormalMatrix * gl_Normal);
	vec3 t = normalize(gl_NormalMatrix * vTangent.xyz);
	vec3 b = normalize(gl_NormalMatrix * cross(gl_Normal, vTangent.xyz) * sign(vTangent.w));
	
	vec3 vertexPosition = vec3(gl_ModelViewMatrix *  gl_Vertex);
	//vec3 lightDir = normalize( (gl_ModelViewMatrix * gl_LightSource[0].position).xyz - vertexPosition);
	vec3 lightDir = normalize( gl_LightSource[0].position.xyz - vertexPosition);
		
	// transform light and half angle vectors by tangent basis
	
	lightVec.x = dot (lightDir, t);
	lightVec.y = dot (lightDir, b);
	lightVec.z = dot (lightDir, n);
	
	eyeVec.x = dot (vertexPosition, t);
	eyeVec.y = dot (vertexPosition, b);
	eyeVec.z = dot (vertexPosition, n);
	
	/*/
	lightVec = vTangent.xyz;
	eyeVec   = sign(vTangent.w) * cross(gl_Normal, vTangent.xyz);
	//*/
	gl_Position = ftransform();
}