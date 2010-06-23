package pruebas.jogl;

import javax.media.opengl.GL;
import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import org.sam.jogl.Apariencia;
import org.sam.jogl.Material;
import org.sam.jogl.Objeto3D;
import org.sam.jogl.OglList;

public class HelixGenerator {
	
	private HelixGenerator(){}

	static float DEG_TO_RAD = (float)(Math.PI / 180.0);
	static float TOW_PI  = (float)( 2 * Math.PI);
	
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
	
	private interface Generator{
		public int getMode();
		public void generate(GL gl,
				Point3f vert1, Point2f text1, Vector3f norm1, Vector4f tang1,
				Point3f vert2, Point2f text2, Vector3f norm2, Vector4f tang2,
				Point3f vert3, Point2f text3, Vector3f norm3, Vector4f tang3,
				Point3f vert4, Point2f text4, Vector3f norm4, Vector4f tang4
			);
	}
	
	private static Generator QuadsGenerator = new Generator(){
		@Override
		public int getMode() {
			return GL.GL_QUADS;
		}
		
		@Override
		public void generate(GL gl,
				Point3f vert1, Point2f text1, Vector3f norm1, Vector4f tang1,
				Point3f vert2, Point2f text2, Vector3f norm2, Vector4f tang2,
				Point3f vert3, Point2f text3, Vector3f norm3, Vector4f tang3,
				Point3f vert4, Point2f text4, Vector3f norm4, Vector4f tang4
			){
			gl.glTexCoord2f(        text1.x, text1.y);
			gl.glVertexAttrib4f( 1, tang1.x, tang1.y, tang1.z, tang1.w);
			gl.glNormal3f(          norm1.x, norm1.y, norm1.z);
			gl.glVertex3f(          vert1.x, vert1.y, vert1.z);
			
			gl.glTexCoord2f(        text2.x, text2.y);
			gl.glVertexAttrib4f( 1, tang2.x, tang2.y, tang2.z, tang2.w);
			gl.glNormal3f(          norm2.x, norm2.y, norm2.z);
			gl.glVertex3f(          vert2.x, vert2.y, vert2.z);
			
			gl.glTexCoord2f(        text3.x, text3.y);
			gl.glVertexAttrib4f( 1, tang3.x, tang3.y, tang3.z, tang3.w);
			gl.glNormal3f(          norm3.x, norm3.y, norm3.z);
			gl.glVertex3f(          vert3.x, vert3.y, vert3.z);
			
			gl.glTexCoord2f(        text4.x, text4.y);
			gl.glVertexAttrib4f( 1, tang4.x, tang4.y, tang4.z, tang4.w);
			gl.glNormal3f(          norm4.x, norm4.y, norm4.z);
			gl.glVertex3f(          vert4.x, vert4.y, vert4.z);
		}


	};
	
	private static class NTBGenerator implements Generator{
		float scale = 0.25f;
		
		@Override
		public int getMode() {
			return GL.GL_LINES;
		}
		
		@Override
		public void generate(GL gl,
				Point3f vert1, Point2f text1, Vector3f norm1, Vector4f tang1,
				Point3f vert2, Point2f text2, Vector3f norm2, Vector4f tang2,
				Point3f vert3, Point2f text3, Vector3f norm3, Vector4f tang3,
				Point3f vert4, Point2f text4, Vector3f norm4, Vector4f tang4
			){
			
			Vector3f bino1 = new Vector3f();
			bino1.cross(norm1, new Vector3f(tang1.x, tang1.y, tang1.z));
			if(tang1.w < 0.0f)
				bino1.negate();
			Vector3f bino2 = new Vector3f();
			bino2.cross(norm2, new Vector3f(tang2.x, tang2.y, tang2.z));
			if(tang2.w < 0.0f)
				bino2.negate();
			Vector3f bino3 = new Vector3f();
			bino3.cross(norm3, new Vector3f(tang3.x, tang3.y, tang3.z));
			if(tang3.w < 0.0f)
				bino3.negate();
			Vector3f bino4 = new Vector3f();
			bino4.cross(norm4, new Vector3f(tang4.x, tang4.y, tang4.z));
			if(tang4.w < 0.0f)
				bino4.negate();
			
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

			gl.glColor3f(0.0f, 0.5f, 1.0f);

			gl.glVertex3f( vert1.x, vert1.y, vert1.z );
			gl.glVertex3f( vert1.x + bino1.x * scale, vert1.y + bino1.y * scale, vert1.z + bino1.z * scale);

			gl.glVertex3f( vert2.x, vert2.y, vert2.z );
			gl.glVertex3f( vert2.x + bino2.x * scale, vert2.y + bino2.y * scale, vert2.z + bino2.z * scale);

			gl.glVertex3f( vert3.x, vert3.y, vert3.z );
			gl.glVertex3f( vert3.x + bino3.x * scale, vert3.y + bino3.y * scale, vert3.z + bino3.z * scale);

			gl.glVertex3f( vert4.x, vert4.y, vert4.z );
			gl.glVertex3f( vert4.x + bino4.x * scale, vert4.y + bino4.y * scale, vert4.z + bino4.z * scale );
		}
	}
	
	private static NTBGenerator myNTBGenerator = new NTBGenerator();
	
	private static Vector4f calculateTangent(Vector3f normal, Point3f pu0, Point2f tu0, Point3f pu1, Point2f tu1, Point3f pv0, Point2f tv0, Point3f pv1, Point2f tv1){
		
		float x1 = pu1.x - pu0.x;
		float x2 = pv1.x - pv0.x;
		float y1 = pu1.y - pu0.y;
		float y2 = pv1.y - pv0.y;
		float z1 = pu1.z - pu0.z;
		float z2 = pv1.z - pv0.z;

		float s1 = tu1.x - tu0.x;
		float s2 = tv1.x - tv0.x;
		float t1 = tu1.y - tu0.y;
		float t2 = tv1.y - tv0.y;

		float r = 1.0f / (s1 * t2 - s2 * t1);
		Vector3f sDir  = new Vector3f(
				(t2 * x1 - t1 * x2) * r,
				(t2 * y1 - t1 * y2) * r,
                (t2 * z1 - t1 * z2) * r
		);
		sDir.normalize();
		
		Vector3f tDir  = new Vector3f(
				(s1 * x2 - s2 * x1) * r,
				(s1 * y2 - s2 * y1) * r,
                (s1 * z2 - s2 * z1) * r
		);
		tDir.normalize();
		
        Vector3f t, aux;
        // Gram-Schmidt orthogonalize
        // t = (t - n * Dot(n, t)).Normalize();
        t = new Vector3f(sDir);
        aux  = new Vector3f(normal);
        aux.scale( aux.dot(t) );
        t.sub( aux );
        t.normalize();
        // Calculate handedness
        // w = (Dot(Cross(n, t), tDir) < 0.0f) ? -1.0f : 1.0f;
        aux = new Vector3f();
        aux.cross(normal, t);
        return new Vector4f(
        		t.x, t.y, t.z,
        		aux.dot(tDir) < 0.0f ? -1.0f : 1.0f
        );
	}
	
	private static OglList generate(GL gl, Generator generator, float r1I, float r1F, int steps1, float r2I, float r2F, int steps2, float l, int twists) {
		Point3f[][]  vertex   = new Point3f [steps1+1][steps2 * twists+1];
		Point2f[][]  tCoords  = new Point2f [steps1+1][steps2 * twists+1];
		Vector3f[][] normals  = new Vector3f[steps1+1][steps2 * twists+1];
		
		float offsetX  = -(l * twists)/2;
		float incTheta = TOW_PI / steps2;
		float incL     = l / steps2;
		float incU	   = 12.0f / steps2;
		float incR1    = (r1F - r1I)/(steps2 * twists);
		float incR2    = (r2F - r2I)/(steps2 * twists);
		float incPhi   = TOW_PI / steps1;
		float incV	   = 4.0f / steps1;
		
		float theta = 0.0f;
		float u = 0.0f;

		l = 0;
		float r1 = r1I;
		float r2 = r2I;

		for (int i = 0, total = steps2 * twists; i <= total; i++) {
			
			float phi  = 0.0f;
			incV = Math.abs(incV);
			float v = 0.0f;
	
			for ( int j= 0; j <= steps1; j++ ) {
			//for ( int j= 0; j < 8; j++ ) {
				
				Point3f vert  = new Point3f ( r1, 0, 0);
				rotateAboutZ(vert, phi);
				vert.x += l;
				vert.y += r2;
				rotateAboutX(vert, theta);
				vert.x += offsetX;
				vertex  [j][i] = vert;
				tCoords [j][i] = new Point2f ( u, v );
				
				phi += incPhi;
				v += incV;
				if(v > 1){
					v = 2 - v;
					incV = - incV;
				}else if(v < 0){
					v = - v;
					incV = - incV;
				}
			}
			theta += incTheta;
			u += incU;
			if(u > 1){
				u = 2 - u;
				incU = - incU;
			}else if(u < 0){
				u = - u;
				incU = - incU;
			}
			l  += incL;
			r1 += incR1;
			r2 += incR2;
		}
		
		Vector3f udir1 = new Vector3f(), udir2 = new Vector3f();
		Vector3f vdir1 = new Vector3f(), vdir2 = new Vector3f();
		
		Vector3f n1 = new Vector3f();
		Vector3f n2 = new Vector3f();
		Vector3f n3 = new Vector3f();
		Vector3f n4 = new Vector3f();
		
		for ( int i= 0; i <= steps1; i++ ) {
			for (int j = 0, total = steps2 * twists; j <= total; j++) {
				if(j > 0){
					udir1.set(vertex [i][j-1]);
					udir1.sub(vertex [i][j]);
				}
				if(j < total){
					udir2.set(vertex [i][j+1]);
					udir2.sub(vertex [i][j]);
				}
				if(j == 0){
					udir1 = new Vector3f(udir2);
					udir1.negate();
				}
				if(j == total){
					udir2 = new Vector3f(udir1);
					udir2.negate();
				}
					
				vdir1.set(vertex [i > 0 ? i-1 : steps1-1][j]);
				vdir1.sub(vertex [i][j]);
				
				vdir2.set(vertex [i < steps1 ? i+1 : 1][j]);
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
		
		Vector4f t1 = new Vector4f();
		Vector4f t2 = new Vector4f();
		Vector4f t3 = new Vector4f();
		Vector4f t4 = new Vector4f();
		
		Vector3f t = new Vector3f();
		Vector3f aux = new Vector3f();
		
		int lid = gl.glGenLists(1);
		gl.glNewList(lid, GL.GL_COMPILE);
		gl.glBegin(generator.getMode());
		
		for ( int i= 0; i < steps1; i++ ) {
			for (int j = 0, total = steps2 * twists; j < total; j++) {
				
				float uSign = tCoords[i][j].x < tCoords[i][j+1].x ? 1.0f: -1.0f;
				float vSign = tCoords[i][j].y < tCoords[i+1][j].y ? -1.0f: 1.0f;
				
				udir2.set(vertex [i][j+1]);
				udir2.sub(vertex [i][j > 0 ? j-1 : j]);
				
				t.set(udir2);
				aux.set(normals[i  ][j  ]);
				aux.scale( aux.dot(t) );
		        t.sub( aux );
		        t.normalize();
				
				t1.set(t.x, t.y, t.z, vSign);
				t1.scale(uSign);
				
				udir2.set(vertex [i+1][j+1]);
				udir2.sub(vertex [i+1][j > 0 ? j-1 : j]);
				
				t.set(udir2);
				aux.set(normals[i+1][j  ]);
				aux.scale( aux.dot(t) );
		        t.sub( aux );
		        t.normalize();
				
				t2.set(t.x, t.y, t.z, vSign);
				t2.scale(uSign);
				
				udir2.set(vertex [i+1][j+1 < total ? j+2: j+1]);
				udir2.sub(vertex [i+1][j]);
				t.set(udir2);
				aux.set(normals[i+1][j+1]);
				aux.scale( aux.dot(t) );
		        t.sub( aux );
		        t.normalize();
				
				t3.set(t.x, t.y, t.z, vSign);
				t3.scale(uSign);
				
				udir2.set(vertex [i][j+1 < total ? j+2: j+1]);
				udir2.sub(vertex [i][j]);
				
				t.set(udir2);
				aux.set(normals[i  ][j+1]);
				aux.scale( aux.dot(t) );
		        t.sub( aux );
		        t.normalize();
				t4.set(t.x, t.y, t.z, vSign);
				t4.scale(uSign);
				
				generator.generate(gl, 
						vertex[i  ][j  ], tCoords[i  ][j  ], normals[i  ][j  ], t1,
						vertex[i+1][j  ], tCoords[i+1][j  ], normals[i+1][j  ], t2,
						vertex[i+1][j+1], tCoords[i+1][j+1], normals[i+1][j+1], t3,
						vertex[i  ][j+1], tCoords[i  ][j+1], normals[i  ][j+1], t4
				);
			}
		}
		gl.glEnd();
		gl.glEndList();
		
		return new OglList(lid);
	}
	
	public static Objeto3D generateHelix(GL gl, float r1I, float r1F, int steps1, float r2I, float r2F, int steps2, float l, int twists) {
		OglList list = generate(gl, QuadsGenerator, r1I, r1F, steps1, r2I, r2F, steps2, l, twists );

		Apariencia ap = new Apariencia();
		ap.setMaterial(Material.DEFAULT);
		return new Objeto3D(list, ap);
	}
	
	public static Objeto3D generateHelix(GL gl, float r1, float r2, int twists) {
		return generateHelix(gl, r1, r1, 12, r2, r2, 24, r1*2.5f, twists);
	}
	
	public static Objeto3D generateNTB(GL gl, float r1I, float r1F, int steps1, float r2I, float r2F, int steps2, float l, int twists, float scale) {
		myNTBGenerator.scale = scale;
		OglList list = generate(gl, myNTBGenerator, r1I, r1F, steps1, r2I, r2F, steps2, l, twists );
		return new Objeto3D(list, new Apariencia());
	}
	
	public static Objeto3D generateNTB(GL gl, float r1, float r2, int twists, float scale) {
		return generateNTB(gl, r1, r1, 12, r2, r2, 24, r1*2.5f, twists, scale);
	}
}
