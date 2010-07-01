package pruebas.jogl;

import javax.media.opengl.GL;
import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import org.sam.jogl.Apariencia;
import org.sam.jogl.Material;
import org.sam.jogl.Objeto3D;
import org.sam.jogl.OglList;

public class HelixGenerator {
	
	public  static final int ONLY_VERTICES          = 0x0;
	public  static final int GENERATE_NORMALS       = 0x1;
	public  static final int GENERATE_TEXT_COORDS   = 0x2;
	private static final int GENERATE_TANGENTS_MASK = 0x4;
	public  static final int GENERATE_TANGENTS      = 0x7;
	
	private HelixGenerator(){}

	static void rotateAboutX(Tuple3f t, double alpha){
		rotateAboutX(t, Math.cos(alpha), Math.sin(alpha));
	}
	static void rotateAboutX(Tuple3f t, double cosA, double sinA){
	    float ny = (float)(cosA * t.y - sinA * t.z);
	    float nz = (float)(cosA * t.z + sinA * t.y);
	    t.y = ny;
	    t.z = nz;
	}

	static void rotateAboutY(Tuple3f t, double alpha){
		rotateAboutY(t, Math.cos(alpha), Math.sin(alpha));
	}
	static void rotateAboutY(Tuple3f t, double cosA, double sinA){
	    float nx = (float)(cosA * t.x + sinA * t.z);
	    float nz = (float)(cosA * t.z - sinA * t.x);
	    t.x = nx;
	    t.z = nz;
	}

	static void rotateAboutZ(Tuple3f t, double alpha){
		rotateAboutZ(t, Math.cos(alpha), Math.sin(alpha));
	}
	static void rotateAboutZ(Tuple3f t, double cosA, double sinA){
	    float nx = (float)(cosA * t.x - sinA * t.y);
	    float ny = (float)(cosA * t.y + sinA * t.x);
	    t.x = nx;
	    t.y = ny;
	}
	
	private static float dot(Vector3f v1, Vector3f v2){
		return (v1.x*v2.x + v1.y*v2.y + v1.z*v2.z);
	}
	
	private static Vector3f cross(Vector3f v1, Vector3f v2){
		return new Vector3f(
			v1.y*v2.z - v1.z*v2.y,
			v2.x*v1.z - v2.z*v1.x,
			v1.x*v2.y - v1.y*v2.x
		);
	}
	
	private static Vector3f cross(Vector3f v1, Vector4f v2){
		return new Vector3f(
        	v1.y*v2.z - v1.z*v2.y,
        	v2.x*v1.z - v2.z*v1.x,
        	v1.x*v2.y - v1.y*v2.x
        );
	}
	
	static Vector2f cambioDeBase(Vector3f v, Vector3f s, Vector3f t){
//		float d = s.x * t.y - s.y * t.x;
//		if( Math.abs(d) > 0.01f )
//			return new Vector2f(
//				(v.x * t.y - v.y * t.x)/d,
//				(v.y * s.x - v.x * s.y)/d
//			);
//		d = s.y * t.z - s.z * t.y;
//		if( Math.abs(d) > 0.01f )
//			return new Vector2f(
//				(v.y * t.z - v.z * t.y)/d,
//				(v.z * s.y - v.y * s.z)/d
//			);
//
//		d = s.z * t.x - s.x * t.z;
//		return new Vector2f(
//			(v.z * t.x - v.x * t.z)/d,
//			(v.x * s.z - v.z * s.x)/d
//		);
		float d = dot(s,s); // = |s|²
		return new Vector2f(
			dot(v,s)/d,
			-1.0f/d
		);
	}
	
	static Vector3f cambioDeBase(Vector2f v, Vector3f s, Vector3f t){
		return new Vector3f(
				v.x * s.x + v.y * t.x,
				v.x * s.y + v.y * t.y,
				v.x * s.z + v.y * t.z
		);
	}
	
	static Vector3f reflect(Vector3f v, Vector3f s, Vector3f t){
		System.out.format( "( % 8.5f, % 8.5f, % 8.5f ) ( % 8.5f, % 8.5f, % 8.5f ) --> % 8.5f\n", 
				s.x, s.y, s.z, t.x, t.y, t.z, dot(s, t) );
		Vector2f v2f = cambioDeBase(v, s, t);
		v2f.y = -v2f.y;
		Vector3f reflect = cambioDeBase(v2f, s, t );
		System.out.format( "\t( % 8.5f, % 8.5f, % 8.5f ) => [% 8.5f, % 8.5f ] --> [% 8.5f, % 8.5f ] => ( % 8.5f, % 8.5f, % 8.5f )\n",
				v.x, v.y, v.z, v2f.x, v2f.y, v2f.x, -v2f.y, reflect.x, reflect.y, reflect.z );
		System.out.format(" dotN = % 8.5f vDotS-rDotS = % 8.5f\n", 
				dot(cross(v,s),reflect),  dot(v,s) - dot(reflect,s) );
		return cambioDeBase(v2f, s, t );
	}

	/**
	 * Reflejo coplanar
	 * <PRE>
	 * cross( cross(v1, v2), v2)
	 * c.x = ( v1.y*v2.z - v1.z*v2.y )
	 * c.y = ( v2.x*v1.z - v2.z*v1.x )
	 * c.z = ( v1.x*v2.y - v1.y*v2.x )
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
	 * Calculo del vector perpendicular a s y coplanar al plano(v,s)
	 * cross(v, s) = normal del plano(v,s)
	 * cross(n, s) = cross( cross(v, s), s ) = vector perpendicular a la normal y a s
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
	 */
	static final Vector3f reflect(Vector3f v, Vector3f s){
		float d = 2 * dot(v,s)/dot(s,s);
		return new Vector3f(
				d * s.x - v.x,
				d * s.y - v.y,
				d * s.z - v.z
		);
	}
	
	private static Vector3f orthogonalizeGramSchmidt(Vector3f n, Vector3f t){
	    // tOrtho = normalize( t - n·dot(n, t) )
		Vector3f tOrtho = new Vector3f(t);
		Vector3f aux = new Vector3f(n);
		aux.scale( dot( aux, t ) );
		tOrtho.sub( aux );
		tOrtho.normalize();
		return tOrtho;
	}
	
	private interface Generator{
		public int getMode();
		public void generate(GL gl,
				Vector3f norm1, Vector4f tang1, Point2f text1, Point3f vert1,
				Vector3f norm2, Vector4f tang2, Point2f text2, Point3f vert2,
				Vector3f norm3, Vector4f tang3, Point2f text3, Point3f vert3,
				Vector3f norm4, Vector4f tang4, Point2f text4, Point3f vert4
			);
	}
	
	private static final Generator QuadsGenerator = new Generator(){
		@Override
		public int getMode() {
			return GL.GL_QUADS;
		}
		
		@Override
		public void generate(GL gl,
				Vector3f norm1, Vector4f tang1, Point2f text1, Point3f vert1,
				Vector3f norm2, Vector4f tang2, Point2f text2, Point3f vert2,
				Vector3f norm3, Vector4f tang3, Point2f text3, Point3f vert3,
				Vector3f norm4, Vector4f tang4, Point2f text4, Point3f vert4
			){

			gl.glVertex3f( vert1.x, vert1.y, vert1.z );
			gl.glVertex3f( vert2.x, vert2.y, vert2.z );
			gl.glVertex3f( vert3.x, vert3.y, vert3.z );
			gl.glVertex3f( vert4.x, vert4.y, vert4.z );
		}
	};
	
	private static final Generator QuadsNormalsGenerator = new Generator(){
		@Override
		public int getMode() {
			return GL.GL_QUADS;
		}
		
		@Override
		public void generate(GL gl,
				Vector3f norm1, Vector4f tang1, Point2f text1, Point3f vert1,
				Vector3f norm2, Vector4f tang2, Point2f text2, Point3f vert2,
				Vector3f norm3, Vector4f tang3, Point2f text3, Point3f vert3,
				Vector3f norm4, Vector4f tang4, Point2f text4, Point3f vert4
			){
			gl.glNormal3f( norm1.x, norm1.y, norm1.z );
			gl.glVertex3f( vert1.x, vert1.y, vert1.z );
			
			gl.glNormal3f( norm2.x, norm2.y, norm2.z );
			gl.glVertex3f( vert2.x, vert2.y, vert2.z );
			
			gl.glNormal3f( norm3.x, norm3.y, norm3.z );
			gl.glVertex3f( vert3.x, vert3.y, vert3.z );
			
			gl.glNormal3f( norm4.x, norm4.y, norm4.z );
			gl.glVertex3f( vert4.x, vert4.y, vert4.z );
		}
	};
	
	private static final Generator QuadsTexCoordsGenerator = new Generator(){
		@Override
		public int getMode() {
			return GL.GL_QUADS;
		}
		
		@Override
		public void generate(GL gl,
				Vector3f norm1, Vector4f tang1, Point2f text1, Point3f vert1,
				Vector3f norm2, Vector4f tang2, Point2f text2, Point3f vert2,
				Vector3f norm3, Vector4f tang3, Point2f text3, Point3f vert3,
				Vector3f norm4, Vector4f tang4, Point2f text4, Point3f vert4
			){
			gl.glTexCoord2f( text1.x, text1.y );
			gl.glVertex3f(   vert1.x, vert1.y, vert1.z );
			
			gl.glTexCoord2f( text2.x, text2.y );
			gl.glVertex3f(   vert2.x, vert2.y, vert2.z );
			
			gl.glTexCoord2f( text3.x, text3.y );
			gl.glVertex3f(   vert3.x, vert3.y, vert3.z );
			
			gl.glTexCoord2f( text4.x, text4.y );
			gl.glVertex3f(   vert4.x, vert4.y, vert4.z );
		}
	};
	
	private static final Generator QuadsNormalsTexCoordsGenerator = new Generator(){
		@Override
		public int getMode() {
			return GL.GL_QUADS;
		}
		
		@Override
		public void generate(GL gl,
				Vector3f norm1, Vector4f tang1, Point2f text1, Point3f vert1,
				Vector3f norm2, Vector4f tang2, Point2f text2, Point3f vert2,
				Vector3f norm3, Vector4f tang3, Point2f text3, Point3f vert3,
				Vector3f norm4, Vector4f tang4, Point2f text4, Point3f vert4
			){
			gl.glNormal3f(   norm1.x, norm1.y, norm1.z );
			gl.glTexCoord2f( text1.x, text1.y );
			gl.glVertex3f(   vert1.x, vert1.y, vert1.z );
			
			gl.glNormal3f(   norm2.x, norm2.y, norm2.z );
			gl.glTexCoord2f( text2.x, text2.y );
			gl.glVertex3f(   vert2.x, vert2.y, vert2.z );
			
			gl.glNormal3f(   norm3.x, norm3.y, norm3.z );
			gl.glTexCoord2f( text3.x, text3.y );
			gl.glVertex3f(   vert3.x, vert3.y, vert3.z );
			
			gl.glNormal3f(   norm4.x, norm4.y, norm4.z );
			gl.glTexCoord2f( text4.x, text4.y );
			gl.glVertex3f(   vert4.x, vert4.y, vert4.z );
		}
	};
	
	private static final Generator QuadsTangentsGenerator = new Generator(){
		@Override
		public int getMode() {
			return GL.GL_QUADS;
		}
		
		@Override
		public void generate(GL gl,
				Vector3f norm1, Vector4f tang1, Point2f text1, Point3f vert1,
				Vector3f norm2, Vector4f tang2, Point2f text2, Point3f vert2,
				Vector3f norm3, Vector4f tang3, Point2f text3, Point3f vert3,
				Vector3f norm4, Vector4f tang4, Point2f text4, Point3f vert4
			){
			gl.glNormal3f(          norm1.x, norm1.y, norm1.z );
			gl.glVertexAttrib4f( 1, tang1.x, tang1.y, tang1.z, tang1.w );
			gl.glTexCoord2f(        text1.x, text1.y );
			gl.glVertex3f(          vert1.x, vert1.y, vert1.z );
			
			gl.glNormal3f(          norm2.x, norm2.y, norm2.z );
			gl.glVertexAttrib4f( 1, tang2.x, tang2.y, tang2.z, tang2.w );
			gl.glTexCoord2f(        text2.x, text2.y );
			gl.glVertex3f(          vert2.x, vert2.y, vert2.z );
			
			gl.glNormal3f(          norm3.x, norm3.y, norm3.z );
			gl.glVertexAttrib4f( 1, tang3.x, tang3.y, tang3.z, tang3.w );
			gl.glTexCoord2f(        text3.x, text3.y );
			gl.glVertex3f(          vert3.x, vert3.y, vert3.z );
			
			gl.glNormal3f(          norm4.x, norm4.y, norm4.z );
			gl.glVertexAttrib4f( 1, tang4.x, tang4.y, tang4.z, tang4.w );
			gl.glTexCoord2f(        text4.x, text4.y );
			gl.glVertex3f(          vert4.x, vert4.y, vert4.z );
		}
	};
	
	private static class NTBGenerator implements Generator{
		float scale = 1.0f;
		
		@Override
		public int getMode() {
			return GL.GL_LINES;
		}
		
		@Override
		public void generate(GL gl,
				Vector3f norm1, Vector4f tang1, Point2f text1, Point3f vert1,
				Vector3f norm2, Vector4f tang2, Point2f text2, Point3f vert2,
				Vector3f norm3, Vector4f tang3, Point2f text3, Point3f vert3,
				Vector3f norm4, Vector4f tang4, Point2f text4, Point3f vert4
			){
			
			gl.glColor3f(0.5f, 1.0f, 0.0f);
			
			gl.glVertex3f( vert1.x, vert1.y, vert1.z );
			gl.glVertex3f( vert1.x + norm1.x * scale, vert1.y + norm1.y * scale, vert1.z + norm1.z * scale );

			gl.glVertex3f( vert2.x, vert2.y, vert2.z );
			gl.glVertex3f( vert2.x + norm2.x * scale, vert2.y + norm2.y * scale, vert2.z + norm2.z * scale );

			gl.glVertex3f( vert3.x, vert3.y, vert3.z );
			gl.glVertex3f( vert3.x + norm3.x * scale, vert3.y + norm3.y * scale, vert3.z + norm3.z * scale );

			gl.glVertex3f( vert4.x, vert4.y, vert4.z );
			gl.glVertex3f( vert4.x + norm4.x * scale, vert4.y + norm4.y * scale, vert4.z + norm4.z * scale );

			gl.glColor3f(1.0f, 0.0f, 0.5f);

			gl.glVertex3f( vert1.x, vert1.y, vert1.z );
			gl.glVertex3f( vert1.x + tang1.x * scale, vert1.y + tang1.y * scale, vert1.z + tang1.z * scale );

			gl.glVertex3f( vert2.x, vert2.y, vert2.z );
			gl.glVertex3f( vert2.x + tang2.x * scale, vert2.y + tang2.y * scale, vert2.z + tang2.z * scale );

			gl.glVertex3f( vert3.x, vert3.y, vert3.z );
			gl.glVertex3f( vert3.x + tang3.x * scale, vert3.y + tang3.y * scale, vert3.z + tang3.z * scale );

			gl.glVertex3f( vert4.x, vert4.y, vert4.z );
			gl.glVertex3f( vert4.x + tang4.x * scale, vert4.y + tang4.y * scale, vert4.z + tang4.z * scale );

			Vector3f bitang;
			gl.glColor3f(0.0f, 0.5f, 1.0f);
			
			gl.glVertex3f( vert1.x, vert1.y, vert1.z );
			bitang = cross( norm1, tang1 );
			if(tang1.w < 0.0f)
				bitang.negate();
			gl.glVertex3f( vert1.x + bitang.x * scale, vert1.y + bitang.y * scale, vert1.z + bitang.z * scale);

			gl.glVertex3f( vert2.x, vert2.y, vert2.z );
			bitang = cross( norm2, tang2 );
			if(tang2.w < 0.0f)
				bitang.negate();
			gl.glVertex3f( vert2.x + bitang.x * scale, vert2.y + bitang.y * scale, vert2.z + bitang.z * scale);

			gl.glVertex3f( vert3.x, vert3.y, vert3.z );
			bitang = cross( norm3, tang3 );
			if(tang3.w < 0.0f)
				bitang.negate();
			gl.glVertex3f( vert3.x + bitang.x * scale, vert3.y + bitang.y * scale, vert3.z + bitang.z * scale);

			gl.glVertex3f( vert4.x, vert4.y, vert4.z );
			bitang = cross( norm4, tang4 );
			if(tang4.w < 0.0f)
				bitang.negate();
			gl.glVertex3f( vert4.x + bitang.x * scale, vert4.y + bitang.y * scale, vert4.z + bitang.z * scale );
		}
	}
	
	private static final NTBGenerator MyNTBGenerator = new NTBGenerator();
	
	private static final float TOW_PI  = (float)( 2 * Math.PI);
	
	private static void generateVertices(Point3f[][] vertices, float r1I, float r1F, int steps1, float r2I, float r2F, int steps2, float l, int twists) {
		
		float offsetX  = -(l * twists)/2;
		float incTheta = TOW_PI / steps2;
		float incL     = l / steps2;
		float incR1    = (r1F - r1I)/(steps2 * twists);
		float incR2    = (r2F - r2I)/(steps2 * twists);
		float incPhi   = TOW_PI / steps1;
		
		float theta = 0.0f;

		l = 0;
		float r1 = r1I;
		float r2 = r2I;
		
		for (int i = 0; i < vertices[0].length; i++) {
			
			float phi  = 0.0f;
			for ( int j= 0; j < vertices.length; j++ ) {
				
				Point3f vert  = new Point3f ( r1, 0, 0);
				rotateAboutZ(vert, phi);
				vert.x += l;
				vert.y += r2;
				rotateAboutX(vert, theta);
				vert.x += offsetX;
				vertices  [j][i] = vert;
				
				phi += incPhi;
			}
			theta += incTheta;
			r1 += incR1;
			r2 += incR2;
			l  += incL*r1/r1I;
		}
	}
	
	private static void generateNormals(Point3f[][] vertex, Vector3f[][] normals){
		
		Vector3f udir1 = new Vector3f(), udir2 = new Vector3f();
		Vector3f vdir1 = new Vector3f(), vdir2 = new Vector3f();
		
		Vector3f n1 = new Vector3f();
		Vector3f n2 = new Vector3f();
		Vector3f n3 = new Vector3f();
		Vector3f n4 = new Vector3f();
		
		for ( int i= 0; i < vertex.length; i++ ) {
			for (int j = 0; j < vertex[0].length; j++) {
				if(j > 0){
					udir1.set(vertex [i][j-1]);
					udir1.sub(vertex [i][j]);
				}
				if( j < vertex[0].length -1 ){
					udir2.set(vertex [i][j+1]);
					udir2.sub(vertex [i][j]);
				}
				if(j == 0){
					udir1 = new Vector3f(udir2);
					udir1.negate();
				}
				if( j == vertex[0].length -1 ){
					udir2 = new Vector3f(udir1);
					udir2.negate();
				}
					
				vdir1.set(vertex [i > 0 ? i-1 : vertex.length -1 ][j]);
				vdir1.sub(vertex [i][j]);
				
				vdir2.set(vertex [i < vertex.length-1 ? i+1 : 0][j]);
				vdir2.sub(vertex [i][j]);
				
				n1.cross(vdir1, udir1);
				n2.cross(udir1, vdir2);
				n3.cross(vdir2, udir2);
				n4.cross(udir2, vdir1);
				
				Vector3f normal = new Vector3f ();
				normal.add(n1);
				normal.add(n2);
				normal.add(n3);
				normal.add(n4);
				normal.normalize();
				
				normals [i][j] = normal;
			}
		}
	}
	/*
	private static void generateTextCoords(Point2f[][] tCoords, float scaleU, float scaleV) {
		float incU = scaleU / (tCoords[0].length - 1);
		float incV = scaleV / (tCoords.length - 1);
		
		float v = 0.0f;
		for( int i= 0; i < tCoords.length; i++ ) {
			incU = Math.abs(incU) * Math.signum( scaleU );
			float u = 0.0f;
			for( int j = 0; j < tCoords[0].length; j++) {
				tCoords [i][j] = new Point2f ( u, v );
				u += incU;
				if(u > 1){
					u = 2 - u;
					incU = - incU;
				}else if(u < 0){
					u = - u;
					incU = - incU;
				}
			}
			v += incV;
			if(v > 1){
				v = 2 - v;
				incV = - incV;
			}else if(v < 0){
				v = - v;
				incV = - incV;
			}
		}
	}
	/*/
	private static void generateTextCoords(Point2f[][] tCoords, float scaleU, float scaleV) {
		float incU = scaleU / (tCoords[0].length - 1);
		float incV = scaleV / (tCoords.length - 1);
		
		float u = 0.0f;
		incU = Math.abs(incU) * Math.signum( scaleU );
		for( int i = 0; i < tCoords[0].length; i++) {
			float v = 0.05f * i;
			for( int j= 0; j < tCoords.length/2; j++ ) {
				tCoords [j][i] = new Point2f ( u, v );
				v += incV;
			}
			tCoords [tCoords.length/2][i] = new Point2f ( u, v );
			for( int j= 0; j < tCoords.length/2; j++ ) {
				tCoords [ (tCoords.length-1) -j][i] = new Point2f ( tCoords [j][i] );
			}
			
			u += incU;
			if(u > 1){
				u = 2 - u;
				incU = - incU;
			}else if(u < 0){
				u = - u;
				incU = - incU;
			}
		}
	}
	//*/

	private static void generateTangents(Point3f[][] vertices, Vector3f[][] tangents){
		
		for ( int i= 0; i < tangents.length; i++ )
			for (int j = 0; j < tangents[0].length; j++)
				tangents[i][j] = new Vector3f();
		
		final Vector3f tangent = new Vector3f();
		
		for (int i = 0; i < vertices.length; i++) {
			int i_1 = i+1 < vertices.length ? i+1: 0;
			for (int j = 0; j < vertices[0].length -1; j++) {
				
				final Point3f p00 = vertices[i  ][j  ]; 
				final Point3f p10 = vertices[i_1][j  ]; 
				final Point3f p11 = vertices[i_1][j+1];
				final Point3f p01 = vertices[i  ][j+1];

				float x1 = p11.x + p01.x - p10.x - p00.x;
				float x2 = p11.x + p10.x - p01.x - p00.x;
				float y1 = p11.y + p01.y - p10.y - p00.y;
				float y2 = p11.y + p10.y - p01.y - p00.y;
				float z1 = p11.z + p01.z - p10.z - p00.z;
				float z2 = p11.z + p10.z - p01.z - p00.z;
			
				float s1 = 1.0f; //Math.abs(c11.x + c01.x - c10.x - c00.x);
				float s2 = 0.0f; //Math.abs(c11.x + c10.x - c01.x - c00.x);
				float t1 = 1.0f; //Math.abs(c11.y + c01.y - c10.y - c00.y);
				float t2 = 0.0f; //Math.abs(c11.y + c10.y - c01.y - c00.y);
			
				tangent.set(
						(t2 * x1 - t1 * x2),
						(t2 * y1 - t1 * y2),
			            (t2 * z1 - t1 * z2)
				);
				//sDir.normalize();
				tangents[i  ][j  ].add(tangent);
				tangents[i  ][j+1].add(tangent);
				tangents[i_1][j+1].add(tangent);
				tangents[i_1][j  ].add(tangent);
			}
		}
	}
	
	
	private static void generateTangentDirs(Point3f[][] vertices, Point2f[][] tCoords, Vector3f[][] sDirs){
		
		for ( int i= 0; i < sDirs.length; i++ )
			for (int j = 0; j < sDirs[0].length; j++)
				sDirs[i][j] = new Vector3f();
		
		final Vector3f sDir = new Vector3f();
		
		for (int i = 0; i < vertices.length; i++) {
			int i_1 = i+1 < vertices.length ? i+1: 0;
			for (int j = 0; j < vertices[0].length -1; j++) {
				
				final Point3f p00 = vertices[i  ][j  ]; 
				final Point3f p10 = vertices[i_1][j  ]; 
				final Point3f p11 = vertices[i_1][j+1];
				final Point3f p01 = vertices[i  ][j+1];

				final Point2f c00 = tCoords[i  ][j  ]; 
				final Point2f c10 = tCoords[i+1][j  ];
				final Point2f c11 = tCoords[i+1][j+1];
				final Point2f c01 = tCoords[i  ][j+1];
				
				float x1 = p11.x + p01.x - p10.x - p00.x;
				float x2 = p11.x + p10.x - p01.x - p00.x;
				float y1 = p11.y + p01.y - p10.y - p00.y;
				float y2 = p11.y + p10.y - p01.y - p00.y;
				float z1 = p11.z + p01.z - p10.z - p00.z;
				float z2 = p11.z + p10.z - p01.z - p00.z;
			
				float s1 = Math.abs(c11.x + c01.x - c10.x - c00.x);
				float s2 = Math.abs(c11.x + c10.x - c01.x - c00.x);
				float t1 = Math.abs(c11.y + c01.y - c10.y - c00.y);
				float t2 = Math.abs(c11.y + c10.y - c01.y - c00.y);
			
				float r = 1.0f / (s1 * t2 - s2 * t1);
				sDir.set(
						(t2 * x1 - t1 * x2) * r,
						(t2 * y1 - t1 * y2) * r,
			            (t2 * z1 - t1 * z2) * r
				);
				//sDir.normalize();
				sDirs[i  ][j  ].add(sDir);
				sDirs[i  ][j+1].add(sDir);
				sDirs[i_1][j+1].add(sDir);
				sDirs[i_1][j  ].add(sDir);
			}
		}
	}
	/*
	private static void calculateTangent(Vector3f normal, Vector3f tangent, Vector3f arista1, Vector3f arista2, Vector3f sDir, Vector3f tDir, Vector4f t4f){
		sDir = orthogonalizeGramSchmidt( normal, sDir );
		Vector3f tang = orthogonalizeGramSchmidt(normal, tangent);
				
		float d0 = dot( tang, sDir );

		Vector3f tRa1 =  reflect( tang, orthogonalizeGramSchmidt(normal, arista1) );
		float d1 = dot( tRa1, sDir );
		Vector3f tRa2 =  reflect( tang, orthogonalizeGramSchmidt(normal, arista2) );
		float d2 = dot( tRa2, sDir );
		Vector3f tRa3 =  reflect( tRa1, orthogonalizeGramSchmidt(normal, arista2) );
		float d3 = dot( tRa3, sDir );

		if( d1 >= d0 && d1 >= d2 && d1 >= d3 ){
			tang = tRa1;
		}else if( d2 >= d0 && d2 >= d1 && d2 >= d3 ){
			tang = tRa2;
		}else if( d3 >= d0 && d3 >= d1 && d3 >= d2 ){
			tang = tRa3;
		}

	    t4f.set( tang.x, tang.y, tang.z, dot( cross( normal, tang ), tDir ) < 0.0f ? -1.0f : 1.0f );
	}
	/*/
	private static void calculateTangent(Vector3f normal, Vector3f tangent, Vector3f aristaH, float uSign, float vSign, Vector4f t4f){

		Vector3f tang = orthogonalizeGramSchmidt(normal, tangent);
		Vector3f tRa1 =  orthogonalizeGramSchmidt( normal, reflect( tang, aristaH) );

		if( vSign > 0 ){
			tang = tRa1;
		}
		if( uSign < 0 ){
			tang.negate();
		}
	    //t4f.set( tang.x, tang.y, tang.z, dot( cross( normal, tang ), tDir ) < 0.0f ? -1.0f : 1.0f );
		t4f.set( tang.x, tang.y, tang.z, 1.0f );
	}
	//*/
	
	// Tangente alineada a las aristas para test
	@SuppressWarnings("unused")
	private static void calculateTangent(Vector3f normal, Point3f pu0, Point3f pu1, float uSign, float vSign, Vector4f t4f){
		Vector3f udir = new Vector3f(pu1);
		udir.sub(pu0);
		Vector3f t = orthogonalizeGramSchmidt( normal, udir );
		t4f.set( t.x, t.y, t.z, vSign );
        t4f.scale( uSign );
	}
	
	private static OglList generate(GL gl, int flags, Generator generator, float r1I, float r1F, int steps1, float r2I, float r2F, int steps2, float l, int twists) {
		
		Point3f[][] vertices = new Point3f [steps1][steps2 * twists+1];
		generateVertices(vertices, r1I, r1F, steps1, r2I, r2F, steps2, l, twists );
		
		Vector3f[][] normals = null;
		if ( (flags & GENERATE_NORMALS) != 0 ){
			normals  = new Vector3f[steps1][steps2 * twists+1];
			generateNormals(vertices, normals);
		}
		
		Point2f[][] tCoords = null;
		if ( (flags & GENERATE_TEXT_COORDS) != 0 ){
			tCoords  = new Point2f [steps1+1][steps2 * twists+1];
			generateTextCoords( tCoords, 6.0f*twists, 2.0f );
		}
		
		Vector3f n00 = null; Vector4f t00 = null; Point2f c00 = null; Point3f p00;
		Vector3f n10 = null; Vector4f t10 = null; Point2f c10 = null; Point3f p10;
		Vector3f n11 = null; Vector4f t11 = null; Point2f c11 = null; Point3f p11;
		Vector3f n01 = null; Vector4f t01 = null; Point2f c01 = null; Point3f p01;

		Vector3f[][] sDirs = null;
		if ((flags & GENERATE_TANGENTS_MASK) != 0) {
			t00 = new Vector4f();
			t10 = new Vector4f();
			t11 = new Vector4f();
			t01 = new Vector4f();
			
			sDirs  = new Vector3f[steps1][steps2 * twists+1];
			generateTangentDirs( vertices, tCoords, sDirs );
		}
		
		int lid = gl.glGenLists(1);
		gl.glNewList(lid, GL.GL_COMPILE);
		gl.glBegin(generator.getMode());
		
		
		final Vector3f aristaH = new Vector3f();

		final Vector3f aristav1 = new Vector3f();
		final Vector3f aristav2 = new Vector3f();
		
		final Vector3f sDir = new Vector3f();
		final Vector3f tDir = new Vector3f();
		
		for (int i = 0; i < steps1; i++) {
			int i_1 = i+1 < steps1 ? i+1: 0;
			for (int j = 0, total = steps2 * twists; j < total; j++) {
				p00 = vertices[i  ][j  ];
				p10 = vertices[i_1][j  ]; 
				p11 = vertices[i_1][j+1];
				p01 = vertices[i  ][j+1];
				if ((flags & GENERATE_NORMALS) != 0) {
					n00 = normals[i  ][j  ]; 
					n10 = normals[i_1][j  ];
					n11 = normals[i_1][j+1];
					n01 = normals[i  ][j+1];
				}
				if ((flags & GENERATE_TEXT_COORDS) != 0) {
					c00 = tCoords[i  ][j  ]; 
					c10 = tCoords[i+1][j  ];
					c11 = tCoords[i+1][j+1];
					c01 = tCoords[i  ][j+1];
				}
				if ((flags & GENERATE_TANGENTS_MASK) != 0) {
					
					float uSign = c00.x < c01.x ?  1.0f: -1.0f;
					float vSign = c00.y < c10.y ? -1.0f:  1.0f;
					int j0 = j > 0 ? j-1 : j;
					int j2 = j+1 < total ? j+2: j+1;
					/*

					
					calculateTangent( n00, vertices[i  ][j0], p01, uSign, vSign, t00);
					calculateTangent( n10, vertices[i+1][j0], p11, uSign, vSign, t10);
					calculateTangent( n11, p10, vertices[i+1][j2], uSign, vSign, t11);
					calculateTangent( n01, p00, vertices[i  ][j2], uSign, vSign, t01);
					/*/
					float x1 = p11.x + p01.x - p10.x - p00.x;
					float x2 = p11.x + p10.x - p01.x - p00.x;
					float y1 = p11.y + p01.y - p10.y - p00.y;
					float y2 = p11.y + p10.y - p01.y - p00.y;
					float z1 = p11.z + p01.z - p10.z - p00.z;
					float z2 = p11.z + p10.z - p01.z - p00.z;
				
					float s1 = c11.x + c01.x - c10.x - c00.x;
					float s2 = c11.x + c10.x - c01.x - c00.x;
					float t1 = c11.y + c01.y - c10.y - c00.y;
					float t2 = c11.y + c10.y - c01.y - c00.y;
				
					float r = 1.0f / (s1 * t2 - s2 * t1);
					
					sDir.set(
							(t2 * x1 - t1 * x2) * r,
							(t2 * y1 - t1 * y2) * r,
				            (t2 * z1 - t1 * z2) * r
					);
					sDir.normalize();
					
					tDir.set(
							(s1 * x2 - s2 * x1) * r,
							(s1 * y2 - s2 * y1) * r,
				            (s1 * z2 - s2 * z1) * r
					);
					
					aristav1.set(p10); aristav1.sub(p00);
					aristav2.set(p11); aristav2.sub(p01);
					
//					calculateTangent( n00, sDirs[i  ][j  ], aristah1, aristav1, sDir, tDir, t00 );
//					calculateTangent( n10, sDirs[i_1][j  ], aristah2, aristav1, sDir, tDir, t10 );
//					calculateTangent( n11, sDirs[i_1][j+1], aristah2, aristav2, sDir, tDir, t11 );
//					calculateTangent( n01, sDirs[i  ][j+1], aristah1, aristav2, sDir, tDir, t01 );
					
					aristaH.set(p01); aristaH.sub(vertices[i  ][j0]);
					calculateTangent( n00, sDirs[i  ][j  ], aristaH, uSign, vSign, t00 );
					aristaH.set(p11); aristaH.sub(vertices[i_1][j0]);
					calculateTangent( n10, sDirs[i_1][j  ], aristaH, uSign, vSign, t10 );
					aristaH.set(vertices[i_1][j2]); aristaH.sub(p10);
					calculateTangent( n11, sDirs[i_1][j+1], aristaH, uSign, vSign, t11 );
					aristaH.set(vertices[i  ][j2]); aristaH.sub(p00);
					calculateTangent( n01, sDirs[i  ][j+1], aristaH, uSign, vSign, t01 );
					//*/
				}
				generator.generate(gl, 
						n00, t00, c00, p00,
						n10, t10, c10, p10,
						n11, t11, c11, p11,
						n01, t01, c01, p01
				);
			}
		}
		gl.glEnd();
		gl.glEndList();
		
		return new OglList(lid);
	}
	
	public static Objeto3D generate(GL gl, int flags, float r1I, float r1F, int steps1, float r2I, float r2F, int steps2, float l, int twists) {

		Generator generator;
		if( (flags & GENERATE_TANGENTS_MASK) != 0 )
			generator = QuadsTangentsGenerator;
		else if( (flags & GENERATE_NORMALS) != 0  && (flags & GENERATE_TEXT_COORDS) != 0 )
			generator = QuadsNormalsTexCoordsGenerator;
		else if( (flags & GENERATE_TEXT_COORDS) != 0 )
			generator = QuadsTexCoordsGenerator;
		else if( (flags & GENERATE_NORMALS) != 0 )
			generator = QuadsNormalsGenerator;
		else
			generator = QuadsGenerator;
			
		OglList list = generate(gl, flags, generator, r1I, r1F, steps1, r2I, r2F, steps2, l, twists );

		Apariencia ap = new Apariencia();
		ap.setMaterial(Material.DEFAULT);
		return new Objeto3D(list, ap);
	}
	
	public static Objeto3D generate(GL gl, int flags, float r1, float r2, int twists) {
		return generate(gl, flags, r1, r1, 12, r2, r2, 36, r1*2.5f, twists);
	}
	
	public static Objeto3D generate(GL gl, float r1, float r2, int twists) {
		return generate(gl, GENERATE_NORMALS, r1, r1, 12, r2, r2, 36, r1*2.5f, twists);
	}
	
	public static Objeto3D generateNTB(GL gl, float r1I, float r1F, int steps1, float r2I, float r2F, int steps2, float l, int twists, float scale) {
		MyNTBGenerator.scale = scale;
		OglList list = generate(
				gl, GENERATE_TANGENTS,
				MyNTBGenerator, r1I, r1F, steps1, r2I, r2F, steps2, l, twists
		);
		return new Objeto3D(list, new Apariencia());
	}
	
	public static Objeto3D generateNTB(GL gl, float r1, float r2, int twists, float scale) {
		return generateNTB(gl, r1, r1, 12, r2, r2, 36, r1*2.5f, twists, scale);
	}
}