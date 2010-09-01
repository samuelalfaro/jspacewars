package org.sam.jogl;

import javax.vecmath.Point3f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

public class VectorUtils {
	
	public static void rotateAboutX(Tuple3f t, double alpha){
		rotateAboutX(t, Math.cos(alpha), Math.sin(alpha));
	}
	public static void rotateAboutX(Tuple3f t, double cosA, double sinA){
	    float ny = (float)(cosA * t.y - sinA * t.z);
	    float nz = (float)(cosA * t.z + sinA * t.y);
	    t.y = ny;
	    t.z = nz;
	}

	public static void rotateAboutY(Tuple3f t, double alpha){
		rotateAboutY(t, Math.cos(alpha), Math.sin(alpha));
	}
	public static void rotateAboutY(Tuple3f t, double cosA, double sinA){
	    float nx = (float)(cosA * t.x + sinA * t.z);
	    float nz = (float)(cosA * t.z - sinA * t.x);
	    t.x = nx;
	    t.z = nz;
	}

	public static void rotateAboutZ(Tuple3f t, double alpha){
		rotateAboutZ(t, Math.cos(alpha), Math.sin(alpha));
	}
	public static void rotateAboutZ(Tuple3f t, double cosA, double sinA){
	    float nx = (float)(cosA * t.x - sinA * t.y);
	    float ny = (float)(cosA * t.y + sinA * t.x);
	    t.x = nx;
	    t.y = ny;
	}
	
	public static float dot(Vector3f v1, Vector3f v2){
		return (v1.x*v2.x + v1.y*v2.y + v1.z*v2.z);
	}
	
	/**
	 * @return dot( cross(v1, v2), v3 )
	 */
	public static float dotcross(Vector3f v1, Vector3f v2, Vector3f v3){
		return ((v1.y*v2.z - v1.z*v2.y)*v3.x + (v2.x*v1.z - v2.z*v1.x)*v3.y + (v1.x*v2.y - v1.y*v2.x)*v3.z);
	}
	
	public static Vector2f cambioDeBase( Vector3f v, Vector3f s, Vector3f t ){
		float d = s.x * t.y - s.y * t.x;
		if( Math.abs(d) > 0.01f )
			return new Vector2f(
				(v.x * t.y - v.y * t.x)/d,
				(v.y * s.x - v.x * s.y)/d
			);
		d = s.y * t.z - s.z * t.y;
		if( Math.abs(d) > 0.01f )
			return new Vector2f(
				(v.y * t.z - v.z * t.y)/d,
				(v.z * s.y - v.y * s.z)/d
			);
	
		d = s.z * t.x - s.x * t.z;
		return new Vector2f(
			(v.z * t.x - v.x * t.z)/d,
			(v.x * s.z - v.z * s.x)/d
		);
	}
	
	public static Vector3f cambioDeBase(Vector2f v, Vector3f s, Vector3f t){
		return new Vector3f(
				v.x * s.x + v.y * t.x,
				v.x * s.y + v.y * t.y,
				v.x * s.z + v.y * t.z
		);
	}
	
	/**
	 * Reflejo coplanar</br>
	 * <PRE>
	 * Cálculo del vector perpendicular a s y coplanar al plano(v,s)
	 * cross(v, s) = normal del plano(v,s)
	 * cross(n, s) = cross( cross(v, s), s ) = vector perpendicular a la normal y a s
	 * 
	 * cc = cross( cross(v1, v2), v2)
	 * 
	 *      c = cross(v1, v2)
	 *           c.x = ( v1.y*v2.z - v1.z*v2.y )
	 *           c.y = ( v2.x*v1.z - v2.z*v1.x )
	 *           c.z = ( v1.x*v2.y - v1.y*v2.x )
	 * 
	 * cc.x = c.y*v2.z - c.z*v2.y = ( v2.x*v1.z - v2.z*v1.x )*v2.z - ( v1.x*v2.y - v1.y*v2.x )*v2.y =
	 *      = v1.z*v2.x*v2.z - v1.x*v2.z² - v1.x*v2.y² + v1.y*v2.x*v2.y =
	 *      = - v1.x*(v2.z² + v2.y²) + v1.y*v2.x*v2.y + v1.z*v2.x*v2.z  =
	 *      = v2.x*(v1.y*v2.y + v1.z*v2.z) - v1.x*(v2.y² + v2.z²)
	 *      	
	 * cc.y = v2.x*c.z - v2.z*c.x = v2.x*( v1.x*v2.y - v1.y*v2.x ) - v2.z*( v1.y*v2.z - v1.z*v2.y )
	 *      = v1.x*v2.x*v2.y - v1.y*v2.x² - v1.y*v2.z² + v1.z*v2.y+*v2.z =
	 *      = v1.x*v2.x*v2.y - v1.y*(v2.x² + v2.z²) + v1.z*v2.y+*v2.z =
	 *      = v2.y*( v1.x*v2.x + v1.z*v2.z) - v1.y*(v2.x² + v2.z²)
	 *      	
	 * cc.z = c.x*v2.y - c.y*v2.x = ( v1.y*v2.z - v1.z*v2.y )*v2.y - ( v2.x*v1.z - v2.z*v1.x )*v2.x
	 *      = v1.y*v2.y*v2.z - v1.z*v2.y² - v1.z*v2.x² + v1.x*v2.x+*v2.z =
	 *      = v1.x*v2.x*v2.z + v1.y*v2.y*v2.z - v1.z*(v2.x² + v2.y²) =
	 *      = v2.z*(v1.x*v2.x + v1.y*v2.y) - v1.z*(v2.x² + v2.y²)
	 *      	
	 * cc.x = v2.x*(v1.y*v2.y + v1.z*v2.z) - v1.x*(v2.y² + v2.z²)
	 * cc.y = v2.y*(v1.x*v2.x + v1.z*v2.z) - v1.y*(v2.x² + v2.z²)
	 * cc.z = v2.z*(v1.x*v2.x + v1.y*v2.y) - v1.z*(v2.x² + v2.y²)
	 * 
	 * t = cross( cross(v, s), s );
	 * t.x = s.x*(v.y*s.y + v.z*s.z) - v.x*(s.y² + s.z²)
	 * t.y = s.y*(v.x*s.x + v.z*s.z) - v.y*(s.x² + s.z²)
	 * t.z = s.z*(v.x*s.x + v.y*s.y) - v.z*(s.x² + s.y²)
	 * 
	 * Cambio del vector v a base de s y t
	 * 	
	 * d = s.x * t.y - s.y * t.x =
	 * = s.x *( s.y*(v.x*s.x + v.z*s.z) - v.y*(s.x*s.x + s.z*s.z) ) -
	 * 	 s.y *( s.x*(v.y*s.y + v.z*s.z) - v.x*(s.y*s.y + s.z*s.z) ) =
	 * = s.x*s.y*(v.x*s.x + v.z*s.z) - v.y*(s.x*s.x*s.x + s.x*s.z*s.z) -
	 * 	 s.x*s.y*(v.y*s.y + v.z*s.z) + v.x*(s.y*s.y*s.y + s.y*s.z*s.z) =
	 * = s.x*s.y*(v.x*s.x + v.z*s.z) - s.x*s.y*(v.y*s.y + v.z*s.z) +
	 * 	 v.x*(s.y*s.y*s.y + s.y*s.z*s.z) - v.y*(s.x*s.x*s.x + s.x*s.z*s.z) =
	 * = s.x*s.y*(v.x*s.x -v.y*s.y + v.z*s.z - v.z*s.z) +
	 * 	 v.x*(s.y*s.y*s.y + s.y*s.z*s.z) - v.y*(s.x*s.x*s.x + s.x*s.z*s.z) =
	 * = s.x*s.y*(v.x*s.x -v.y*s.y) +
	 * 	 v.x*(s.y*s.y*s.y + s.y*s.z*s.z) - v.y*(s.x*s.x*s.x + s.x*s.z*s.z) =
	 * = v.x*s.x²*s.y - v.y*s.x*s.y² +
	 * 	 v.x*(s.y³ + s.y*s.z²) - v.y*(s.x³ + s.x*s.z²) =
	 * = v.x*s.x²*s.y +  v.x*(s.y³ + s.y*s.z²) - v.y*s.x*s.y² - v.y*(s.x³ + s.x*s.z²) =
	 * = v.x*s.y*(s.x² + s.y² + s.z²) - v.y*s.x*(s.x² +s.y² + s.z²) =
	 * = (v.x*s.y - v.y*s.x) * (s.x² + s.y² + s.z²) =
	 * = (v.x*s.y - v.y*s.x) * |s|²
	 * 
	 * vb2.x = (v.x * t.y - v.y * t.x)/d =
	 * = ( v.x*( s.y*(v.x*s.x + v.z*s.z) - v.y*(s.x*s.x + s.z*s.z) ) 
	 *   - v.y*( s.x*(v.y*s.y + v.z*s.z) - v.x*(s.y*s.y + s.z*s.z) ) )/d =
	 * = ( v.x*s.y*(v.x*s.x + v.z*s.z) - v.x*v.y*(s.x*s.x + s.z*s.z)
	 *   - v.y*s.x*(v.y*s.y + v.z*s.z) + v.x*v.y*(s.y*s.y + s.z*s.z) )/d =
	 * = ( v.x*s.y*(v.x*s.x + v.z*s.z) - v.x*v.y*( s.x*s.x - s.y*s.y + s.z*s.z - s.z*s.z)
	 *   - v.y*s.x*(v.y*s.y + v.z*s.z) )/d =
	 * = ( v.x*s.y*(v.x*s.x + v.z*s.z) - v.x*v.y*( s.x² - s.y² )
	 *   - v.y*s.x*(v.y*s.y + v.z*s.z) )/d =
	 * = ( v.x*s.y*(v.x*s.x + v.z*s.z) - v.x*v.y*s.x² + v.x*v.y*s.y²
	 *   - v.y*s.x*(v.y*s.y + v.z*s.z) )/d =
	 * = ( v.x*s.y*(v.x*s.x + v.y*s.y + v.z*s.z) - v.y*s.x*(v.x*s.x + v.y*s.y + v.z*s.z) )/d =
	 * = ( v.x*s.y - v.y*s.x ) * ( v.x*s.x + v.y*s.y + v.z*s.z )/d =
	 * = ( v.x*s.y - v.y*s.x ) * ( v.x*s.x + v.y*s.y + v.z*s.z )/(v.x*s.y - v.y*s.x)* |s|² =
	 * = ( v.x*s.x + v.y*s.y + v.z*s.z )/|s|² =
	 * = dot(v,s)/|s|²
	 * 
	 * vb2.y = (v.y * s.x - v.x * s.y)/d --> -vb2y = (v.x * s.y - v.y * s.x)/d =
	 * = (v.x*s.y - v.y*s.x)/(v.x*s.y - v.y*s.x)*|s|² = 1/|s|²
	 * 	
	 * rb3.x = vb2x * s.x + vb2y * t.x = t.x/|s|² + s.x * dot(v,s)/|s|² = (t.x + s.x * dot(v,s))/|s|²
	 * rb3.y = vb2x * s.y + vb2y * t.y = t.y/|s|² + s.y * dot(v,s)/|s|² = (t.y + s.y * dot(v,s))/|s|²
	 * rb3.z = vb2x * s.z + vb2y * t.z = t.z/|s|² + s.z * dot(v,s)/|s|² = (t.z + s.z * dot(v,s))/|s|²
	 * 	
	 * rb3.x*|s|² = t.x + s.x * dot(v,s) =
	 * = s.x*(v.y*s.y + v.z*s.z) - v.x*(s.y² + s.z²) + s.x * ( v.x*s.x + v.y*s.y + v.z*s.z ) =
	 * = v.y*s.x*s.y + v.z*s.x*s.z - v.x*s.y² - v.x*s.z² + v.x*s.x² + v.y*s.x*s.y + v.z*s.x*s.z =
	 * = v.x*s.x² - v.x*s.y² - v.x*s.z² + v.y*s.x*s.y + v.y*s.x*s.y + v.z*s.x*s.z + v.z*s.x*s.z =
	 * = v.x*( s.x² - s.y² - s.z² ) + 2*v.y*s.x*s.y + 2*v.z*s.x*s.z =
	 * = v.x*(  s.x² - s.y² - s.z² ) + 2*s.x*( v.y*s.y + v.z*s.z ) =
	 * = v.x*( 2*s.x² -s.x² - s.y² - s.z² ) + 2*s.x*( v.y*s.y + v.z*s.z ) =
	 * = 2*s.x*( v.x*s.x + v.y*s.y + v.z*s.z ) - v.x*( s.x² + s.y² + s.z² ) =
	 * = 2*s.x*dot(v,s) - v.x*|s|²
	 * 	
	 * rb3.y*|s|² = t.y + s.y * dot(v,s) =
	 * = s.y*(v.x*s.x + v.z*s.z) - v.y*(s.x² + s.z²) + s.y * ( v.x*s.x + v.y*s.y + v.z*s.z ) =
	 * = v.x*s.x*s.y + v.z*s.y*s.z - v.y*s.x² - v.y*s.z² + v.x*s.x*s.y + v.y*s.y² + v.z*s.y*s.z =
	 * = v.x*s.x*s.y + v.x*s.x*s.y - v.y*s.x² + v.y*s.y² - v.y*s.z² + v.z*s.y*s.z + v.z*s.y*s.z =
	 * = 2*v.x*s.x*s.y  + v.y*( -s.x² + s.y² - s.z² ) +  2*v.z*s.y*s.z = 
	 * = v.y*( 2*s.y² -s.x² - s.y² - s.z² ) + 2*s.y*( v.x*s.x + v.z*s.z ) = 
	 * = 2*s.y*( v.x*s.x + v.y*s.y + v.z*s.z ) - v.y*( s.x² + s.y² + s.z² ) =
	 * = 2*s.y*dot(v,s) - v.y*|s|²
	 * 	
	 * rb3.z*|s|² = t.z + s.z * dot(v,s) =
	 * = s.z*(v.x*s.x + v.y*s.y) - v.z*(s.x² + s.y²) + s.z * ( v.x*s.x + v.y*s.y + v.z*s.z ) =
	 * = v.x*s.x*s.z + v.y*s.y*s.z - v.z*s.x² - v.z*s.y² + v.x*s.x*s.z + v.y*s.y*s.z + v.z*s.z² =
	 * = 2*v.x*s.x*s.z + 2*v.y*s.y*s.z  + v.z*( -s.x² - s.y²  +s.z²) =
	 * = v.z*( -s.x² - s.y² + s.z² ) + 2*s.z*( v.x*s.x + v.y*s.y ) =
	 * = v.z*( 2*s.z² -s.x² - s.y² - s.z² ) + 2*s.z*( v.x*s.x + v.y*s.y ) =
	 * = 2*s.z*( v.x*s.x + v.y*s.y + v.z*s.z ) - v.z*( s.x² + s.y² + s.z² ) =
	 * = 2*s.z*dot(v,s) - v.z*|s|²
	 * 
	 * rb3.x = s.x*2*dot(v,s)/|s|² - v.x
	 * rb3.y = s.y*2*dot(v,s)/|s|² - v.y
	 * rb3.z = s.z*2*dot(v,s)/|s|² - v.z
	 * </PRE>
	 * @param v {@code Vector3f} a reflejar.
	 * @param s {@code Vector3f} con la dirección del eje de simetría.
	 * @return el {@code Vector3f} reflejado.
	 */
	public static Vector3f reflect(Vector3f v, Vector3f s){
		float d = 2 * dot(v,s)/dot(s,s);
		return new Vector3f(
				d * s.x - v.x,
				d * s.y - v.y,
				d * s.z - v.z
		);
	}
	
	public static void orthogonalizeGramSchmidt(Vector3f n, Vector3f t){
	    // tOrtho = normalize( t - n·dot(n, t) )
		float d = dot( n, t );
		t.set(
			t.x - n.x*d,
			t.y - n.y*d,
			t.z - n.z*d
		);
		t.normalize();
	}
	
	public static float distance(Tuple3f t1, Tuple3f t2){
		float dx = t1.x-t2.x;
		float dy = t1.y-t2.y;
		float dz = t1.z-t2.z;
		return (float) Math.sqrt(dx*dx+dy*dy+dz*dz);
	}
	
	private static final Vector3f _d1 = new Vector3f();
	private static final Vector3f _d2 = new Vector3f();
	private static final Vector3f _normal = new Vector3f();

	/**
	 * @return ( p1 - p2 ) x ( p3 - p1 )
	 */
	public static Vector3f crossVectors( Point3f p1, Point3f p2, Point3f p3 ){
		_d1.set(p1);
		_d1.sub(p2);
		
		_d2.set(p3);
		_d2.sub(p1);
		
		_normal.cross( _d1, _d2 );
		
		return _normal;
	}
}
