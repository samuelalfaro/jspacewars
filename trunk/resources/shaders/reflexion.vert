varying vec3 pos;
varying vec3 vNormal;

void main()
{
	pos = (gl_ModelViewMatrix * gl_Vertex).xyz;
	vNormal =  gl_NormalMatrix * gl_Normal;
   	gl_TexCoord[0] = gl_MultiTexCoord0;
   	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}
