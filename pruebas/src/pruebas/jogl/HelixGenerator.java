package pruebas.jogl;

import javax.media.opengl.GL;

import org.sam.jogl.Apariencia;
import org.sam.jogl.Material;
import org.sam.jogl.Objeto3D;
import org.sam.jogl.OglList;

public class HelixGenerator {
	
	private HelixGenerator(){}

	private static float DEG_TO_RAD = (float)(Math.PI / 180.0);
	private static float TOW_PI  = (float)( 2 * Math.PI);
	
	public static Objeto3D generateHelix(GL gl, float r1I, float r1F, float r2I, float r2F, float l, int twists) {
		float incTheta = 15.0f * DEG_TO_RAD;
		float incPhi   = 30.0f * DEG_TO_RAD;
		
		int lid = gl.glGenLists(1);
		
		gl.glNewList(lid, GL.GL_COMPILE);
		gl.glBegin(GL.GL_QUADS);
		
		float cosU0 = 1.0f;
		float sinU0 = 0.0f;
		float cosU1 = (float)Math.cos( incTheta );
		float sinU1 = (float)Math.sin( incTheta );
		float l0 = 0, l1 = l * incTheta / TOW_PI;
		float r10 = r1I, r11 = (incTheta /(TOW_PI * twists)) * (r1F - r1I) + r1I;
		float r20 = r2I, r21 = (incTheta /(TOW_PI * twists)) * (r2F - r2I) + r2I;
		
		for (float theta = 0.0f; theta <= TOW_PI * twists; ) {
				
			float cosV0 = 1.0f;
			float sinV0 = 0.0f;
			float cosV1 = (float) Math.cos( incPhi );
			float sinV1 = (float) Math.sin( incPhi );

			float posX00 = r10 + l0;
			float posY00 = r20;
			float posX10 = r10 * cosV1 + l0;
			float posY10 = r10 * sinV1 + r20;
			
			float posX01 = r11 + l1;
			float posY01 = r21;
			float posX11 = r11 * cosV1 + l1;
			float posY11 = r11 * sinV1 + r21;
		
			for (float phi = 0.0f; phi <= TOW_PI; ) {
				
				gl.glNormal3f(  cosV0,  sinV0 * cosU0,  sinV0 * sinU0 );
				gl.glVertex3f( posX00, posY00 * cosU0, posY00 * sinU0 );
				
				gl.glNormal3f(  cosV1,  sinV1 * cosU0,  sinV1 * sinU0 );
				gl.glVertex3f( posX10, posY10 * cosU0, posY10 * sinU0 );
				
				gl.glNormal3f(  cosV1,  sinV1 * cosU1,  sinV1 * sinU1 );
				gl.glVertex3f( posX11, posY11 * cosU1, posY11 * sinU1 );
				
				gl.glNormal3f(  cosV0,  sinV0 * cosU1,  sinV0 * sinU1 );
				gl.glVertex3f( posX01, posY01 * cosU1, posY01 * sinU1 );
				
				cosV0 = cosV1;
				sinV0 = sinV1;
				posX00 = posX10;
				posY00 = posY10;
				posX01 = posX11;
				posY01 = posY11;
				
				phi += incPhi;
				cosV1 = (float) Math.cos( phi );
				sinV1 = (float) Math.sin( phi );
				posX10 = r10 * cosV1 + l0;
				posY10 = r10 * sinV1 + r20;
				posX11 = r11 * cosV1 + l1;
				posY11 = r11 * sinV1 + r21;
			}
			cosU0 = cosU1;
			sinU0 = sinU1;
			theta += incTheta;
			cosU1 = (float)Math.cos( theta );
			sinU1 = (float)Math.sin( theta );
			l0 = l1;
			float alpha = theta / TOW_PI;
			l1 = alpha * l;
			r10 = r11;
			r11 = (theta /(TOW_PI * twists)) * (r1F - r1I) + r1I;
			r20 = r21;
			r21 = (theta /(TOW_PI * twists)) * (r2F - r2I) + r2I;
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
	
	public static Objeto3D generateHelix(GL gl, float r1, float r2, float l, int twists) {
		return generateHelix(gl, r1, r1, r2, r2, l, twists);
	}
}
