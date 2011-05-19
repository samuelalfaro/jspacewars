//=========================================================================
// Copyright (C) Tommy Hinks						  
// tommy[dot]hinks[at]gmail[dot]com  												  
//
// Contributors: 
//             1) Tommy Hinks
//=========================================================================

uniform vec4 viewport;

varying vec4  distances;
varying float perspective;

attribute vec3 vertice0;
attribute vec3 vertice1;
attribute vec3 vertice2;
attribute vec3 vertice3;

void main()
{	// Light direction is always (0.0, 0.0, 1.0), i.e. looking out of the camera.
	// This means that diffuse shading depends only on the z-coordinate of the transformed
	// normal in eye-space.
	//
	// vec3 normal = normalize(gl_NormalMatrix*gl_Normal);

	// Compute diffuse and ambient only, no specular.
	// Vertices of the same triangle have the same normal, 
	// hence lighting is done per triangle. Lighting and material 
	// properties are passed through built-in uniforms.
	//
	gl_FrontColor = 
		max( normalize( gl_NormalMatrix * gl_Normal ).z, 0.0 ) *
		gl_FrontMaterial.diffuse * gl_LightSource[0].diffuse + 
		gl_FrontMaterial.ambient * gl_LightSource[0].ambient;

	// Stored in the (multi-)texture coordinates for each vertex are the 
	// three vertex positions of the triangle that the vertex belongs to.
	// Project the triangle vertices into viewport (screen) space.
	//

	vec4 vertex = gl_ModelViewProjectionMatrix * vec4( vertice0.xyz, 1.0 );
	vec2 vp0 = vec2(
		viewport.x + 0.5 * viewport.z * ( vertex.x / vertex.w + 1.0 ),
		viewport.y + 0.5 * viewport.w * ( vertex.y / vertex.w + 1.0 )
	);
	
	vertex = gl_ModelViewProjectionMatrix * vec4( vertice1.xyz, 1.0 );
	vec2 vp1 = vec2(
		viewport.x + 0.5 * viewport.z * ( vertex.x / vertex.w + 1.0 ),
		viewport.y + 0.5 * viewport.w * ( vertex.y / vertex.w + 1.0 )
	);

	vertex = gl_ModelViewProjectionMatrix * vec4( vertice2.xyz, 1.0 );
	vec2 vp2 = vec2(
		viewport.x + 0.5 * viewport.z * ( vertex.x / vertex.w + 1.0 ),
		viewport.y + 0.5 * viewport.w * ( vertex.y / vertex.w + 1.0 )
	);

	vertex = gl_ModelViewProjectionMatrix * vec4( vertice3.xyz, 1.0 );
	vec2 vp3 = vec2(
		viewport.x + 0.5 * viewport.z * ( vertex.x / vertex.w + 1.0 ),
		viewport.y + 0.5 * viewport.w * ( vertex.y / vertex.w + 1.0 )
	);


	// Project current vertex into viewport (screen) space.
	gl_Position = gl_ModelViewProjectionMatrix*gl_Vertex;

	vec2 vp = vec2(
		viewport.x + 0.5 * viewport.z * ( gl_Position.x / gl_Position.w + 1.0),
		viewport.y + 0.5 * viewport.w * ( gl_Position.y / gl_Position.w + 1.0)
	);

	// Compute the distance in viewport (screen) space to each of the triangle
	// edges. These distances are pre-multiplied by the perspective component
	// of the vertex to avoid errors with perspective correction. The inverse
	// perspective component is interpolated and used in the fragment shader.
	//
	
   vec2 d1, d0;
	d0 = vp0 - vp; d1 = vp1 - vp0; float l1 = length(d1);
	distances.x = abs( d0.y * d1.x - d0.x * d1.y )/l1;

	d0 = vp1 - vp; d1 = vp2 - vp1; float l2 = length(d1);
	distances.y = abs( d0.y * d1.x - d0.x * d1.y )/l2;

	d0 = vp2 - vp; d1 = vp3 - vp2; float l3 = length(d1);
	distances.z = abs( d0.y * d1.x - d0.x * d1.y )/l3;

	d0 = vp3 - vp; d1 = vp0 - vp3; float l4 = length(d1);
	distances.w = abs( d0.y * d1.x - d0.x * d1.y )/l4;

	float m = max( max( distances[0], distances[1] ), max( distances[2], distances[3] ) );


	if( l1 == 0.0 )
		distances.x = m;
	if( l2 == 0.0 )
		distances.y = m;
	if( l3 == 0.0 )
		distances.z = m;
	if( l4 == 0.0 )
		distances.w = m;

	distances *= gl_Position.w;	// Perspective pre-multiplication.
	perspective = 1.0/gl_Position.w;	// Varying post-multiplication for fragment shader.
}
