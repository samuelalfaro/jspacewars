package pruebas.jogl;

import javax.media.opengl.GL;
import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
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
	
	public static Objeto3D generateHelix(GL gl, float r1I, float r1F, int steps1, float r2I, float r2F, int steps2, float l, int twists) {
		float incTheta = TOW_PI / steps2;
		float incL     = l / steps2;
		float incU	   = 6.0f / steps2;
		float incR1    = (r1F - r1I)/(steps2 * twists);
		float incR2    = (r2F - r2I)/(steps2 * twists);
		float incPhi   = TOW_PI / steps1;
		float incV	   = 4.0f / steps1;
		
		int lid = gl.glGenLists(1);
		
		gl.glNewList(lid, GL.GL_COMPILE);
		gl.glBegin(GL.GL_QUADS);
		
		float theta = incTheta;
		float cosU0 = 1.0f, cosU1 = (float)Math.cos( theta );
		float sinU0 = 0.0f, sinU1 = (float)Math.sin( theta );
		float u0    = 0,    u1    = incU;

		float l0  = 0,    l1  = incL;
		float r10 = r1I,  r11 = r1I + incR1;
		float r20 = r2I,  r21 = r2I + incR2;

		for (int i = 0, total = steps2 * twists; i < total; i++) {
			
			float phi = incPhi;
			float cosV0 = 1.0f, cosV1 = (float) Math.cos( phi );
			float sinV0 = 0.0f, sinV1 = (float) Math.sin( phi );
			float v0    = 0,    v1    = incV;

			float posX00 = r10 + l0,  posX10 = r10 * cosV1 + l0;
			float posY00 = r20,       posY10 = r10 * sinV1 + r20;
			float posX01 = r11 + l1,  posX11 = r11 * cosV1 + l1;
			float posY01 = r21,       posY11 = r11 * sinV1 + r21;
			
			for ( int j= 0; j < steps1/1; j++ ) {
				
				Point3f  vert1 = new Point3f ( posX00, posY00 * cosU0, posY00 * sinU0 );
				Vector3f norm1 = new Vector3f(  cosV0,  sinV0 * cosU0,  sinV0 * sinU0 );
				Point2f  text1 = new Point2f ( u0, v0 );
				
				Point3f  vert2 = new Point3f ( posX10, posY10 * cosU0, posY10 * sinU0 );
				Vector3f norm2 = new Vector3f(  cosV1,  sinV1 * cosU0,  sinV1 * sinU0 );
				Point2f  text2 = new Point2f ( u0, v1 );
				
				Point3f  vert3 = new Point3f ( posX11, posY11 * cosU1, posY11 * sinU1 );
				Vector3f norm3 = new Vector3f(  cosV1,  sinV1 * cosU1,  sinV1 * sinU1 );
				Point2f  text3 = new Point2f ( u1, v1 );
				
				Point3f  vert4 = new Point3f ( posX01, posY01 * cosU1, posY01 * sinU1 );
				Vector3f norm4 = new Vector3f(  cosV0,  sinV0 * cosU1,  sinV0 * sinU1 );
				Point2f  text4 = new Point2f ( u1, v0 );
				
				/*
				Vector4f tang1 = new Vector4f( sinV0, cosV0 * cosU0, cosV0 * sinU0, v1 < v0 ? -1.0f: 1.0f );
				tang1.scale(u1 < u0 ? -1.0f: 1.0f);
				Vector4f tang2 = new Vector4f( sinV1, cosV1 * cosU0, cosV1 * sinU0, v1 < v0 ? -1.0f: 1.0f );
				tang2.scale(u1 < u0 ? -1.0f: 1.0f);
				Vector4f tang3 = new Vector4f( sinV1, cosV1 * cosU1, cosV1 * sinU1, v1 < v0 ? -1.0f: 1.0f );
				tang3.scale(u1 < u0 ? -1.0f: 1.0f);
				Vector4f tang4 = new Vector4f( sinV0, cosV0 * cosU1, cosV0 * sinU1, v1 < v0 ? -1.0f: 1.0f);
				tang4.scale(u1 < u0 ? -1.0f: 1.0f);
				/*/
				
				float x1 = vert2.x - vert1.x;
				float x2 = vert4.x - vert1.x;
				float y1 = vert2.y - vert1.y;
				float y2 = vert4.y - vert1.y;
				float z1 = vert2.z - vert1.z;
				float z2 = vert4.z - vert1.z;

				float s1 = text2.x - text1.x;
				float s2 = text4.x - text1.x;
				float t1 = text2.y - text1.y;
				float t2 = text4.y - text1.y;

				float r = 1.0f / (s1 * t2 - s2 * t1);
				//float r = 1;
				Vector3f sdir  = new Vector3f(
						(s1 * x2 - s2 * x1) * r,
						(s1 * y2 - s2 * y1) * r,
						(s1 * z2 - s2 * z1) * r
				);
				sdir.normalize();
				Vector3f tdir  = new Vector3f(
						(t2 * x1 - t1 * x2) * r,
						(t2 * y1 - t1 * y2) * r,
						(t2 * z1 - t1 * z2) * r
				);
				tdir.normalize();
				
		        Vector3f t, aux;

		        // Gram-Schmidt orthogonalize
		        // (t - n * Dot(n, t)).Normalize();
		        t = new Vector3f(tdir);
		        aux  = new Vector3f(norm1);
		        aux.scale( aux.dot(t) );
		        t.sub( aux );
		        t.normalize();

		        // Calculate handedness
		        // w = (Dot(Cross(n, sdir), tdir) < 0.0f) ? -1.0f : 1.0f;
		        aux = new Vector3f();
		        aux.cross(norm1, tdir);

		        Vector4f tang1 = new Vector4f(
		        		t.x, t.y, t.z,
		        		aux.dot(sdir) < 0.0f ? -1.0f : 1.0f
		        );

		        // Gram-Schmidt orthogonalize
		        // (t - n * Dot(n, t)).Normalize();
		        t = new Vector3f(tdir);
		        aux  = new Vector3f(norm2);
		        aux.scale( aux.dot(t) );
		        t.sub( aux );
		        t.normalize();

		        // Calculate handedness
		        // w = (Dot(Cross(n, sdir), tdir) < 0.0f) ? -1.0f : 1.0f;
		        aux = new Vector3f();
		        aux.cross(norm2, tdir);

		        Vector4f tang2 = new Vector4f(
		        		t.x, t.y, t.z,
		        		aux.dot(sdir) < 0.0f ? -1.0f : 1.0f
		        );

		        // Gram-Schmidt orthogonalize
		        // (t - n * Dot(n, t)).Normalize();
		        t = new Vector3f(tdir);
		        aux  = new Vector3f(norm3);
		        aux.scale( aux.dot(t) );
		        t.sub( aux );
		        t.normalize();

		        // Calculate handedness
		        // w = (Dot(Cross(n, sdir), tdir) < 0.0f) ? -1.0f : 1.0f;
		        aux = new Vector3f();
		        aux.cross(norm3, tdir);

		        Vector4f tang3 = new Vector4f(
		        		t.x, t.y, t.z,
		        		aux.dot(sdir) < 0.0f ? -1.0f : 1.0f
		        );

		        // Gram-Schmidt orthogonalize
		        // (t - n * Dot(n, t)).Normalize();
		        t = new Vector3f(tdir);
		        aux  = new Vector3f(norm4);
		        aux.scale( aux.dot(t) );
		        t.sub( aux );
		        t.normalize();

		        // Calculate handedness
		        // w = (Dot(Cross(n, sdir), tdir) < 0.0f) ? -1.0f : 1.0f;
		        aux = new Vector3f();
		        aux.cross(norm4, tdir);

		        Vector4f tang4 = new Vector4f(
		        		t.x, t.y, t.z,
		        		aux.dot(sdir) < 0.0f ? -1.0f : 1.0f
		        );
		       // */
				
				gl.glTexCoord2f(       text1.x, text1.y);
				gl.glNormal3f(         norm1.x, norm1.y, norm1.z);
				gl.glVertexAttrib4f(1, tang1.x, tang1.y, tang1.z, tang1.w);
				gl.glVertex3f(         vert1.x, vert1.y, vert1.z);
				
				gl.glTexCoord2f(       text2.x, text2.y);
				gl.glNormal3f(         norm2.x, norm2.y, norm2.z);
				gl.glVertexAttrib4f(1, tang2.x, tang2.y, tang2.z, tang2.w);
				gl.glVertex3f(         vert2.x, vert2.y, vert2.z);
				
				gl.glTexCoord2f(       text3.x, text3.y);
				gl.glNormal3f(         norm3.x, norm3.y, norm3.z);
				gl.glVertexAttrib4f(1, tang3.x, tang3.y, tang3.z, tang3.w);
				gl.glVertex3f(         vert3.x, vert3.y, vert3.z);
				
				gl.glTexCoord2f(       text4.x, text4.y);
				gl.glNormal3f(         norm4.x, norm4.y, norm4.z);
				gl.glVertexAttrib4f(1, tang4.x, tang4.y, tang4.z, tang4.w);
				gl.glVertex3f(         vert4.x, vert4.y, vert4.z);
				
				phi += incPhi;
				cosV0 = cosV1;    cosV1 = (float) Math.cos( phi );
				sinV0 = sinV1;    sinV1 = (float) Math.sin( phi );
				v0 = v1;          v1 += incV;
				if(v1 > 1){
					v1 = 2 - v1;
					incV = - incV;
				}
				
				posX00 = posX10;  posX10 = r10 * cosV1 + l0;
				posY00 = posY10;  posY10 = r10 * sinV1 + r20;
				posX01 = posX11;  posX11 = r11 * cosV1 + l1;
				posY01 = posY11;  posY11 = r11 * sinV1 + r21;
				
			}
			incV = - incV;
			
			theta += incTheta;
			cosU0 = cosU1;  cosU1 = (float)Math.cos( theta );
			sinU0 = sinU1;  sinU1 = (float)Math.sin( theta );
			u0 = u1;        u1 += incU;
			if(u1 > 1){
				u1 = 2 - u1;
				incU = - incU;
			}else if(u1 < 0){
				u1 = - u1;
				incU = - incU;
			}
			
			l0  = l1;       l1  += incL;
			r10 = r11;      r11 += incR1;
			r20 = r21;      r21 += incR2;
		}
		gl.glEnd();
		gl.glEndList();
		
		Material material = new Material();
		material.setDiffuse( 0.4f, 0.2f, 0.8f, 1.0f );
		material.setSpecular( 1.0f, 1.0f, 1.0f, 1.0f );
		material.setShininess(128.0f);
		
		Apariencia apHelix = new Apariencia();
		apHelix.setMaterial(material);
		
		return new Objeto3D(new OglList(lid), apHelix);
	}
	
	public static Objeto3D generateHelix(GL gl, float r1, float r2, int twists) {
		return generateHelix(gl, r1, r1, 12, r2, r2, 24, r1*2.5f, twists);
	}
}
