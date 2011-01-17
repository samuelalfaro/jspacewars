//=========================================================================
// Copyright (C) Tommy Hinks						  
// tommy[dot]hinks[at]gmail[dot]com  												  
//
// Contributors: 
//             1) Tommy Hinks
//=========================================================================

uniform float line_width;
uniform float line_fadeOut;
uniform vec4  line_color;

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
	//
	//gl_FragColor = mix( gl_Color, line_color, smoothstep( line_width, line_fadeOut, min_d ) );
	//gl_FragColor = mix( line_color, gl_Color,  smoothstep( line_width, line_fadeOut, min_d ) );
	gl_FragColor = mix( vec4( 1.0, 0.0, 1.0, 1.0 ), vec4( 0.5, 0.5, 0.5, 1.0 ), smoothstep( line_width, line_fadeOut, min_d ) );
}
