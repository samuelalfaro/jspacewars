//=========================================================================
// Copyright (C) Tommy Hinks						  
// tommy[dot]hinks[at]gmail[dot]com  												  
//
// Contributors: 
//             1) Tommy Hinks
//=========================================================================

uniform float line_width;
uniform float line_exp;
uniform vec4 line_color;

varying vec4  distances;
varying float perspective;

void main(){

	// Find smallest distance to triangle edge.
	// Post-multiply distances to avoid perspective correction from interpolation.
	//
	float min_d = min( min( distances.x, distances.y ), min( distances.z, distances.w ) )* perspective;
	
	// Compare fragment closest distance to edge with
	// desired line width.
	//
	gl_FragColor = mix( line_color, gl_Color, smoothstep( line_width, line_width + 1.0, min_d ) );
}
