varying vec3 lightVec;
varying vec3 eyeVec;

attribute vec4 vTangent;
	
void main(){
	//*
	gl_TexCoord[0] =  gl_MultiTexCoord0;

	// Building the matrix Eye Space -> Tangent Space
	vec3 n = gl_NormalMatrix * gl_Normal;
	vec3 t = gl_NormalMatrix * vTangent.xyz;
	vec3 b = cross(n, t) * sign(vTangent.w);
	
	mat3 tbn = mat3(
		t.x, b.x, n.x,
		t.y, b.y, n.y,
		t.z, b.z, n.z
	);
	
	vec3 vertexPosition = (gl_ModelViewMatrix *  gl_Vertex).xyz;
	// transform light and half angle vectors by tangent basis
	// lightVec = tbn * ( (gl_ModelViewMatrix * gl_LightSource[0].position).xyz - vertexPosition );
	lightVec = tbn * ( gl_LightSource[0].position.xyz - vertexPosition );
	eyeVec   = tbn * vertexPosition;
	
	/*/
	lightVec = vTangent.xyz;
	eyeVec   = sign(vTangent.w) * cross(gl_Normal, vTangent.xyz);
	//*/
	gl_Position = ftransform();
}