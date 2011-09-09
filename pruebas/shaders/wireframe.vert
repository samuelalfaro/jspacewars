uniform vec2 viewport; // x = width/2, y = height/2

attribute vec3 vertice0;
attribute vec3 vertice1;
attribute vec3 vertice2;
attribute vec3 vertice3;

varying vec4  distances;

vec2 toViewPort( in vec4 p ){
    return viewport * p.xy / p.w;
}

float lengthPointToRect( in vec2 p, in vec2 p0, in vec2 p1 ){
    float m = ( p0.y - p1.y )/( p0.x - p1.x);
    return abs( m * ( p.x - p0.x ) - p.y + p0.y ) / pow( m * m + 1.0, 0.5 );
}

void main()
{
	gl_FrontColor = 
		max( normalize( gl_NormalMatrix * gl_Normal ).z, 0.0 ) *
		gl_FrontMaterial.diffuse * gl_LightSource[0].diffuse + 
		gl_FrontMaterial.ambient * gl_LightSource[0].ambient;

	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;

	vec2 vp = toViewPort( gl_Position );
    vec2 v0 = toViewPort( gl_ModelViewProjectionMatrix * vec4( vertice0.xyz, 1.0 ) );
    vec2 v1 = toViewPort( gl_ModelViewProjectionMatrix * vec4( vertice1.xyz, 1.0 ) );
    vec2 v2 = toViewPort( gl_ModelViewProjectionMatrix * vec4( vertice2.xyz, 1.0 ) );
    vec2 v3 = toViewPort( gl_ModelViewProjectionMatrix * vec4( vertice3.xyz, 1.0 ) );

    distances[0] = lengthPointToRect( vp, v0, v1 );
    distances[1] = lengthPointToRect( vp, v1, v2 );
    distances[2] = lengthPointToRect( vp, v2, v3 );
    distances[3] = lengthPointToRect( vp, v3, v0 );
}
