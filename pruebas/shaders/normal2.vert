varying vec3 pos;
varying vec3 n;
varying vec4 t;

attribute vec4 vTangent;
	
void main(){
	/*
	pos = gl_Vertex.xyz;
	n = gl_Normal;
	t = vTangent;
	/*/
	pos = (gl_ModelViewMatrix *  gl_Vertex).xyz;
	n = gl_NormalMatrix * gl_Normal;
	t = vec4( gl_NormalMatrix * vTangent.xyz, vTangent.w );
	//*/
	gl_TexCoord[0] =  gl_MultiTexCoord0;
	gl_Position = ftransform();
}