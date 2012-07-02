uniform float solid_width;
uniform float translucid_width;
uniform vec4  empty_color;

varying vec4  distances;

void main(){

	float min_d = min( min( distances[0], distances[1] ), min( distances[2], distances[3] ) );

    //Condiciones 0 o 1 tranforma condicionales en multiplicaciones.	
    float c1 = min_d < solid_width ? 1.0 : 0.0;
    // translucid_width positivo -> condiciones excluyenes -> si c1 == 0 -> c2 = 1 sino c2 = 0
	float c2 = min_d > solid_width + translucid_width ? 1.0 : 0.0;
    // si c1 == 0 && c2 == 0 -> c3 = 1 sino c3 = 0    
    float c3 = ( c1 - 1.0 )*( c2 - 1.0 ); 
	//gl_FragColor = c1 * gl_Color + c2 * empty_color + c3 * mix( gl_Color, empty_color, pow( ( min_d - solid_width ) / translucid_width, 2.0 ) );
    gl_FragColor = empty_color;
}
