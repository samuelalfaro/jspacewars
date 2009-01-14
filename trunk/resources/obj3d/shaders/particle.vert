#define TAM 32
#define maxIndex 31
#define fmaxIndex 31.0

// Parametros del objeto
uniform vec3 fuerza;
uniform bool escalaInterpolada;
uniform vec2 escalas[TAM];
uniform bool colorInterpolado;
uniform vec4 colores[TAM];

// Atributos por vertice
attribute vec3  velocidad;
attribute float giro;
attribute float vida;

varying vec4 color;

vec4 getPosicion(float t){
	return vec4( ((fuerza/2.0)*t + velocidad)*t + gl_Vertex.xyz , 1.0 );
}
//*
void main(){
	if (vida > 0.0 && vida < 1.0){
		float fIndex = vida*fmaxIndex;
		int index = int( floor(fIndex) );
		
		vec2 scale = escalaInterpolada ? mix(escalas[index],escalas[index+1],fract(fIndex)) : escalas[0] ;
		//scale *= gl_MultiTexCoord0.st;

		vec3 vRight = normalize(vec3(gl_ModelViewMatrix[0][0], gl_ModelViewMatrix[1][0], gl_ModelViewMatrix[2][0]));
		vec3 vUp    = normalize(vec3(gl_ModelViewMatrix[0][1], gl_ModelViewMatrix[1][1], gl_ModelViewMatrix[2][1]));
		
		vec3 dirVel = normalize((fuerza/2.0)*vida + velocidad);
		
		vec4 posicion = getPosicion(vida);
		vec4 p1 = gl_ModelViewProjectionMatrix * posicion; 
		vec4 p2 = gl_ModelViewProjectionMatrix * getPosicion(vida + 0.001); 
		
		posicion.xyz += gl_MultiTexCoord0.s * scale.x *dirVel;
		
		vec2 dir = p2.xy/p2.z - p1.xy/p1.z;
		
		float alfa = atan(dir.y, dir.x); 
		float cosAlfa = cos(alfa);
		float senAlfa = sin(alfa);
		dir = mat2(cosAlfa, senAlfa, -senAlfa, cosAlfa) *  gl_MultiTexCoord0.st * scale.y;
		
		posicion.xyz += dir.x * vRight + dir.y * vUp;
		gl_Position = gl_ModelViewProjectionMatrix * posicion;
		
		if( giro != 0.0){
			alfa = giro*vida;
			cosAlfa = cos(alfa);
			senAlfa = sin(alfa);
			gl_TexCoord[0].st = mat2(cosAlfa, senAlfa, -senAlfa, cosAlfa) * gl_MultiTexCoord0.st;
		}else
			gl_TexCoord[0] = gl_MultiTexCoord0;
		gl_TexCoord[0].st += 0.5;
		
		color = colorInterpolado ? mix(colores[index],colores[index+1],fract(fIndex)) : colores[0] ; 
	}
}
/*/
void main(){
	if (vida > 0.0 && vida < 1.0){
		float fIndex = vida*fmaxIndex;
		int index = int( floor(fIndex) );
		
		vec2 scale = escalaInterpolada ? mix(escalas[index],escalas[index+1],fract(fIndex)) : escalas[0] ;
		scale *= gl_MultiTexCoord0.st;

		vec3 vRight = normalize(vec3(gl_ModelViewMatrix[0][0], gl_ModelViewMatrix[1][0], gl_ModelViewMatrix[2][0]));
 		vec3 vUp    = normalize(vec3(gl_ModelViewMatrix[0][1], gl_ModelViewMatrix[1][1], gl_ModelViewMatrix[2][1]));

		vec4 posicion = getPosicion(vida);
		posicion.xyz += scale.x * vRight + scale.y * vUp;
		gl_Position = gl_ModelViewProjectionMatrix * posicion;
		
		gl_TexCoord[0] = gl_MultiTexCoord0;
		gl_TexCoord[0].st += 0.5;
		
		color = colorInterpolado ? mix(colores[index],colores[index+1],fract(fIndex)) : colores[0] ;
	}
}
//*/