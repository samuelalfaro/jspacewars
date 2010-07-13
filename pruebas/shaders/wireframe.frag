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
	float min_d = min( min( d.x, d.y ), min( d.z, d.w ) );
	
	// Compare fragment closest distance to edge with
	// desired line width.
	//
	gl_FragColor = mix( gl_Color, line_color, smoothstep( line_width, line_fadeOut, min_d ) );
}
