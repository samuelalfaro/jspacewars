package pruebas.jogl.generators;

import javax.media.opengl.GL;
import javax.vecmath.Point3f;
import javax.vecmath.TexCoord2f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import org.sam.jogl.Apariencia;
import org.sam.jogl.Generator;
import org.sam.jogl.Material;
import org.sam.jogl.Objeto3D;
import org.sam.jogl.OglList;
import org.sam.jogl.VectorUtils;

public class SphereGenerator {
	
	public  static final int ONLY_VERTICES          = 0x00;
	public  static final int GENERATE_NORMALS       = 0x01;
	public  static final int GENERATE_TEXT_COORDS   = 0x02;
	public  static final int WIREFRAME              = 0x05;
	private static final int WIREFRAME_MASK         = 0x04;
	public  static final int GENERATE_TANGENTS      = 0x0B;
	private static final int GENERATE_TANGENTS_MASK = 0x08;
	public  static final int GENERATE_NTB           = 0x1B;
	private static final int GENERATE_NTB_MASK      = 0x10;

	private SphereGenerator(){}

	private static final float TOW_PI  = (float)( 2 * Math.PI );
	
	private static void generateVertices(Point3f[][] vertices, float r, int steps1, int steps2 ) {
		
		float incTheta = TOW_PI / steps2;
		float incPhi   = (float)( Math.PI ) /steps1;
		
		float theta = 0.0f;

		for (int i = 0; i < vertices[0].length; i++) {
			
			float phi  = 0.0f;
			for ( int j= 0; j < vertices.length; j++ ) {
				
				Point3f vert  = new Point3f ( r, 0, 0);
				VectorUtils.rotateAboutZ(vert, phi);
				VectorUtils.rotateAboutX(vert, theta);
				vertices  [j][i] = vert;
				
				phi += incPhi;
			}
			theta += incTheta;
		}
	}
	
	private static void generateNormals(Vector3f[][] normals, int steps1, int steps2 ) {
		
		float incTheta = TOW_PI / steps2;
		float incPhi   = (float)( Math.PI ) /steps1;
		
		float theta = 0.0f;

		for (int i = 0; i < normals[0].length; i++) {
			
			float phi  = 0.0f;
			for ( int j= 0; j < normals.length; j++ ) {
				
				Vector3f normal  = new Vector3f ( 1.0f, 0, 0);
				VectorUtils.rotateAboutZ(normal, phi);
				VectorUtils.rotateAboutX(normal, theta);
				normals  [j][i] = normal;
				
				phi += incPhi;
			}
			theta += incTheta;
		}
	}
	
	private static void generateTextCoords(TexCoord2f[][] tCoords, float scaleU, float scaleV) {
		float incU = scaleU / (tCoords[0].length - 1);
		float incV = scaleV / (tCoords.length - 1);
		
		float v = 0.0f;
		for( int i= 0; i < tCoords.length; i++ ) {
			float u = 0.0f;
			incU = Math.abs(incU) * Math.signum( scaleU );
			for( int j = tCoords[0].length-1; j >= 0; j--) {
				tCoords [i][j] = new TexCoord2f ( u, v );
				//System.out.println(u + " " + v);
				u += incU;
				if(u > 1.0f){
					u = 2.0f - u;
					incU = - incU;
				}else if(u < 0.0f){
					u = - u;
					incU = - incU;
				}
			}
			v += incV;
			if(v > 1.0f){
				v = 2.0f - v;
				incV = - incV;
			}else if(v < 0.0f){
				v = - v;
				incV = - incV;
			}
		}
	}

	private static void generateTangents(Point3f[][] vertices, Vector3f[][][] tangents){
		
		for ( int i= 0; i < tangents[0].length; i++ )
			for (int j = 0; j < tangents[0][0].length; j++){
				tangents[0][i][j] = new Vector3f();
				tangents[1][i][j] = new Vector3f();
			}
		final Vector3f tangent = new Vector3f();
		final Vector3f bitangent = new Vector3f();
		
		for (int i = 0; i < vertices.length-1; i++) {
			for (int j = 0; j < vertices[0].length -1; j++) {
				int j_1 = j+1 < vertices[0].length ? j+1: 0;
				
				final Point3f p00 = vertices[i  ][j  ]; 
				final Point3f p10 = vertices[i+1][j  ]; 
				final Point3f p11 = vertices[i+1][j_1];
				final Point3f p01 = vertices[i  ][j_1];

				tangent.set(
						p11.x + p01.x - p10.x - p00.x,
						p11.y + p01.y - p10.y - p00.y,
						p11.z + p01.z - p10.z - p00.z
				);
				
				bitangent.set(
						p11.x + p10.x - p01.x - p00.x,
						p11.y + p10.y - p01.y - p00.y,
			            p11.z + p10.z - p01.z - p00.z
				);
				
				tangents[0][i  ][j  ].add(tangent);
				tangents[0][i  ][j_1].add(tangent);
				tangents[0][i+1][j_1].add(tangent);
				tangents[0][i+1][j  ].add(tangent);
				
				tangents[1][i  ][j  ].add(bitangent);
				tangents[1][i  ][j_1].add(bitangent);
				tangents[1][i+1][j_1].add(bitangent);
				tangents[1][i+1][j  ].add(bitangent);
			}
		}
	}
	
	private static void calculateTangent(Vector3f normal, Vector2f s, Vector2f t, Vector3f uVec, Vector3f vVec, Vector4f t4f){
		Vector3f sDir = new Vector3f(
				(t.y * uVec.x - t.x * vVec.x),
				(t.y * uVec.y - t.x * vVec.y),
	            (t.y * uVec.z - t.x * vVec.z)
		);
		sDir.normalize();
		Vector3f tDir = new Vector3f(
				(s.y * uVec.x + s.x * vVec.x),
				(s.y * uVec.y + s.x * vVec.y),
	            (s.y * uVec.z + s.x * vVec.z)
		);
		t4f.set( sDir.x, sDir.y, sDir.z, VectorUtils.dotcross( normal, sDir, tDir ) < 0.0f ? -1.0f : 1.0f );
	}
	
	// Tangente alineada a las aristas para test
	@SuppressWarnings("unused")
	private static void calculateTangent(Vector3f normal, Point3f pu0, Point3f pu1, float uSign, float vSign, Vector4f t4f){
		Vector3f t = new Vector3f(pu1);
		t.sub(pu0);
		VectorUtils.orthogonalizeGramSchmidt( normal, t );
		t4f.set( t.x, t.y, t.z, vSign );
        t4f.scale( uSign );
	}
	
	private static OglList generate(GL gl, int flags, Generator generator, float r, int steps1, int steps2) {
		
		Point3f[][] vertices = new Point3f [steps1 + 1][steps2];
		generateVertices(vertices, r, steps1, steps2 );
		
		Vector3f[][] normals = null;
		if ( (flags & GENERATE_NORMALS) != 0 ){
			normals  = new Vector3f[steps1+1][steps2];
			generateNormals(normals, steps1, steps2 );
		}
		
		TexCoord2f[][] tCoords = null;
		if ( (flags & GENERATE_TEXT_COORDS) != 0 ){
			tCoords  = new TexCoord2f [steps1+1][steps2+1];
			generateTextCoords( tCoords, 12.0f, 4.0f );
		}
		
		Vector3f n00 = null; Vector4f t00 = null; TexCoord2f c00 = null; Point3f p00;
		Vector3f n10 = null; Vector4f t10 = null; TexCoord2f c10 = null; Point3f p10;
		Vector3f n11 = null; Vector4f t11 = null; TexCoord2f c11 = null; Point3f p11;
		Vector3f n01 = null; Vector4f t01 = null; TexCoord2f c01 = null; Point3f p01;

		Vector3f[][][] tb = null;
		if ((flags & GENERATE_TANGENTS_MASK) != 0) {
			t00 = new Vector4f();
			t10 = new Vector4f();
			t11 = new Vector4f();
			t01 = new Vector4f();
			
			tb  = new Vector3f[2][steps1+1][steps2];
			generateTangents( vertices, tb );

			for (int i = 0; i < normals.length; i++) {
				for (int j = 0; j < normals[0].length; j++) {
					VectorUtils.orthogonalizeGramSchmidt(normals[i][j],tb[0][i][j]);
					VectorUtils.orthogonalizeGramSchmidt(normals[i][j],tb[1][i][j]);
				}
			}
		}
		
		OglList oglList = new OglList(gl);
		
		gl.glBegin((flags & GENERATE_NTB_MASK) != 0 ? GL.GL_LINES : GL.GL_QUADS );
		
		final Vector2f s = new Vector2f();
		final Vector2f t = new Vector2f();
		
		for (int i = 0; i < steps1; i++) {
			
			for (int j = 0, total = steps2; j < total; j++) {
				int j_1 = j+1 < steps2 ? j+1: 0;
				p00 = vertices[i  ][j  ];
				p10 = vertices[i+1][j  ]; 
				p11 = vertices[i+1][j_1];
				p01 = vertices[i  ][j_1];
				if ((flags & GENERATE_NORMALS) != 0) {
					n00 = normals[i  ][j  ]; 
					n10 = normals[i+1][j  ];
					n11 = normals[i+1][j_1];
					n01 = normals[i  ][j_1];
				}
				if ((flags & GENERATE_TEXT_COORDS) != 0) {
					c00 = tCoords[i  ][j  ]; 
					c10 = tCoords[i+1][j  ];
					c11 = tCoords[i+1][j+1];
					c01 = tCoords[i  ][j+1];
				}
				if ((flags & GENERATE_TANGENTS_MASK) != 0) {
					/*
					float uSign = c00.x < c01.x ?  1.0f: -1.0f;
					float vSign = c00.y < c10.y ? -1.0f:  1.0f;
					int j0 = j > 0 ? j-1 : j;
					int j2 = j+1 < total ? j+2: j+1;
					
					calculateTangent( n00, vertices[i  ][j0], p01, uSign, vSign, t00);
					calculateTangent( n10, vertices[i+1][j0], p11, uSign, vSign, t10);
					calculateTangent( n11, p10, vertices[i+1][j2], uSign, vSign, t11);
					calculateTangent( n01, p00, vertices[i  ][j2], uSign, vSign, t01);
					/*/
					float s1 = c11.x + c01.x - c10.x - c00.x;
					float s2 = c11.x + c10.x - c01.x - c00.x;
					float t1 = c11.y + c01.y - c10.y - c00.y;
					float t2 = c11.y + c10.y - c01.y - c00.y;
					float scale = 1.0f / (s1 * t2 - s2 * t1);
					
					s.set(s1, s2); s.scale(scale);
					t.set(t1, t2); t.scale(scale);
					
					calculateTangent(n00, s, t, tb[0][i  ][j  ], tb[1][i  ][j  ], t00);
					calculateTangent(n10, s, t, tb[0][i+1][j  ], tb[1][i+1][j  ], t10);
					calculateTangent(n11, s, t, tb[0][i+1][j_1], tb[1][i+1][j_1], t11);
					calculateTangent(n01, s, t, tb[0][i  ][j_1], tb[1][i  ][j_1], t01);
					//*/
				}
				generator.generate(gl,
						p00, p10, p11, p01,
						c00, c10, c11, c01,
						n00, n10, n11, n01,
						t00, t10, t11, t01
				);
			}
		}
		gl.glEnd();
		OglList.endList(gl);
		
		return oglList;
	}
	
	public static Objeto3D generate(GL gl, int flags, float r, int steps1, int steps2) {

		Generator generator;
		if( (flags & GENERATE_TANGENTS_MASK) != 0 )
			generator = Generator.VerticesTangents;
		else if( (flags & WIREFRAME_MASK) != 0 )
			generator = Generator.VerticesWireFrame;
		else if( (flags & GENERATE_NORMALS) != 0  && (flags & GENERATE_TEXT_COORDS) != 0 )
			generator = Generator.VerticesNormalsTexCoords;
		else if( (flags & GENERATE_TEXT_COORDS) != 0 )
			generator = Generator.VerticesTexCoords;
		else if( (flags & GENERATE_NORMALS) != 0 )
			generator = Generator.VerticesNormals;
		else
			generator = Generator.Vertices;
		
		OglList list = generate(gl, flags, generator, r, steps1, steps2 );

		Apariencia ap = new Apariencia();
		ap.setMaterial(Material.DEFAULT);
		return new Objeto3D(list, ap);
	}
	
	public static Objeto3D generate(GL gl, int flags, float r) {
		return generate(gl, flags, r, 12, 36);
	}
	
	public static Objeto3D generate(GL gl, float r) {
		return generate(gl, GENERATE_NORMALS, r, 12, 36);
	}
	
	public static Objeto3D generateNTB(GL gl, float r, int steps1, int steps2, float scale) {
		OglList list = generate(
				gl, GENERATE_NTB, new Generator.NTBGenerator(scale),
				r, steps1, steps2
		);
		return new Objeto3D(list, new Apariencia());
	}
	
	public static Objeto3D generateNTB(GL gl, float r, float scale) {
		return generateNTB( gl, r, 12, 36, scale );
	}
}