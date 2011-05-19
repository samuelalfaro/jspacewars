//=========================================================================
// Copyright (C) Tommy Hinks						  
// tommy[dot]hinks[at]gmail[dot]com  												  
//
// Contributors: 
//             1) Tommy Hinks
//=========================================================================

uniform float solid_width;
uniform float translucid_width;

uniform vec4  empty_color;

varying vec4  distances;
varying float perspective;

void main(){
	// Find smallest distance to triangle edge.
	// Post-multiply distances to avoid perspective correction from interpolation.
	//
	vec4  d = distances * perspective;
	float min_d = min( min( d[0], d[1] ), min( d[2], d[3] ) );
	
	// Compare fragment closest distance to edge with
	// desired line width.
	// gl_FragColor = mix( empty_color, gl_Color, smoothstep( line_width, line_fadeOut, min_d ) );
	// gl_FragColor = mix( empty_color, gl_Color, min( max( line_width - min_d, 0 ), 1 ) );
	min_d = mod( min_d, 3.0 * (solid_width + translucid_width) );
	/*
	if( min_d < solid_width )
		gl_FragColor = gl_Color;
	else if( min_d > solid_width + translucid_width )
		gl_FragColor = empty_color;
	else
		gl_FragColor = mix( gl_Color, empty_color, pow( ( min_d - solid_width ) / translucid_width, 0.5 ) );
	*/
	float c1 = min_d < solid_width ? 1.0 : 0.0;
	float c2 = ( 1.0 - c1 )*( min_d > solid_width + translucid_width ? 1.0 : 0.0 );
	gl_FragColor = c1 * gl_Color + c2 * empty_color + ( 1.0 - c1 - c2 ) * mix( gl_Color, empty_color, pow( ( min_d - solid_width ) / translucid_width, 0.5 ) );
	//gl_FragColor = mix( vec4( 1.0, 0.0, 1.0, 1.0 ), vec4( 0.5, 0.5, 0.5, 1.0 ), smoothstep( line_width, line_fadeOut, min_d ) );
}
