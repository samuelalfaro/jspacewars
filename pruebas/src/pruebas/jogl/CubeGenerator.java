package pruebas.jogl;

import javax.media.opengl.GL;
import javax.vecmath.Vector3f;

import org.sam.jogl.Apariencia;
import org.sam.jogl.Material;
import org.sam.jogl.Objeto3D;
import org.sam.jogl.OglList;

public class CubeGenerator {
	
	private CubeGenerator(){}

	static float DEG_TO_RAD = (float)(Math.PI / 180.0);
	static float TOW_PI  = (float)( 2 * Math.PI);
	
	private static void generarQuadPlanoX(GL gl,
			float u0, float u1, 
			float v0, float v1,
			float y0, float y1, 
			float z0, float z1, 
			float x){
		
		float n = x < 0 ? -1: 1;
		float t = (u0 < u1 ? 1: -1);
		
        // Calculate handedness
        // w = (Dot(Cross(n, tdir), sdir) < 0.0f) ? -1.0f : 1.0f;
		Vector3f aux = new Vector3f();
        aux.cross(new Vector3f(n,  0,  0), new Vector3f(0,  0, t));
   		float h = aux.dot(new Vector3f(0, (v0 < v1 ? 1: -1), 0)) < 0.0f ? -1.0f : 1.0f;

		gl.glTexCoord2f(        u0, v0);
		gl.glVertexAttrib4f( 1,  0,  0,  t, h);
		gl.glNormal3f(           n,  0,  0);
		gl.glVertex3f(           x, y0, z0);

		gl.glTexCoord2f(        u1, v0);
		gl.glVertexAttrib4f( 1,  0,  0,  t, h);
		gl.glNormal3f(           n,  0,  0);
		gl.glVertex3f(           x, y0, z1);
		
		gl.glTexCoord2f(        u1, v1);
		gl.glVertexAttrib4f( 1,  0,  0,  t, h);
		gl.glNormal3f(           n,  0,  0);
		gl.glVertex3f(           x, y1, z1);
		
		gl.glTexCoord2f(        u0, v1);
		gl.glVertexAttrib4f( 1,  0,  0,  t, h);
		gl.glNormal3f(           n,  0,  0);
		gl.glVertex3f(           x, y1, z0);
	}
	
	private static void generarQuadPlanoY(GL gl,
			float u0, float u1, 
			float v0, float v1,
			float x0, float x1, 
			float z0, float z1, 
			float y){
		
		float n = y < 0 ? -1: 1;
		float t = (u0 < u1 ? 1: -1);
		
        // Calculate handedness
        // w = (Dot(Cross(n, tdir), sdir) < 0.0f) ? -1.0f : 1.0f;
		Vector3f aux = new Vector3f();
        aux.cross(new Vector3f(0,  n,  0), new Vector3f( t, 0,  0 ));
   		float h = aux.dot(new Vector3f(0, 0, (v0 < v1 ? 1: -1))) < 0.0f ? -1.0f : 1.0f;
		
		gl.glTexCoord2f(        u0, v0);
		gl.glVertexAttrib4f( 1,  t,  0,  0, h);
		gl.glNormal3f(           0,  n,  0);
		gl.glVertex3f(          x0,  y, z0);

		gl.glTexCoord2f(        u1, v0);
		gl.glVertexAttrib4f( 1,  t,  0,  0, h);
		gl.glNormal3f(           0,  n,  0);
		gl.glVertex3f(          x1,  y, z0);
		
		gl.glTexCoord2f(        u1, v1);
		gl.glVertexAttrib4f( 1,  t,  0,  0, h);
		gl.glNormal3f(           0,  n,  0);
		gl.glVertex3f(          x1,  y, z1);
		
		gl.glTexCoord2f(        u0, v1);
		gl.glVertexAttrib4f( 1,  t,  0,  0, h);
		gl.glNormal3f(           0,  n,  0);
		gl.glVertex3f(          x0,  y, z1);
	}
	
	private static void generarQuadPlanoZ(GL gl,
			float u0, float u1, 
			float v0, float v1,
			float x0, float x1, 
			float y0, float y1, 
			float z){
		
		float n = z < 0 ? -1: 1;
		float t = (u0 < u1 ? 1: -1);
		
        // Calculate handedness
        // w = (Dot(Cross(n, tdir), sdir) < 0.0f) ? -1.0f : 1.0f;
		Vector3f aux = new Vector3f();
        aux.cross(new Vector3f(0,  0,  n), new Vector3f( t, 0,  0 ));
   		float h = aux.dot(new Vector3f(0, (v0 < v1 ? 1: -1), 0)) < 0.0f ? -1.0f : 1.0f;
		
		gl.glTexCoord2f(        u0, v0);
		gl.glVertexAttrib4f( 1,  t,  0,  0, h);
		gl.glNormal3f(           0,  0,  n);
		gl.glVertex3f(          x0, y0,  z);
			
		gl.glTexCoord2f(        u1, v0);
		gl.glVertexAttrib4f( 1,  t,  0,  0, h);
		gl.glNormal3f(           0,  0,  n);
		gl.glVertex3f(          x1, y0,  z);
		
		gl.glTexCoord2f(        u1, v1);
		gl.glVertexAttrib4f( 1,  t,  0,  0, h);
		gl.glNormal3f(           0,  0,  n);
		gl.glVertex3f(          x1, y1,  z);
		
		gl.glTexCoord2f(        u0, v1);
		gl.glVertexAttrib4f( 1,  t,  0,  0, h);
		gl.glNormal3f(           0,  0,  n);
		gl.glVertex3f(          x0, y1,  z);
	}
	
	public static Objeto3D generateCube(GL gl, float lado) {
		float l = lado / 2;
		
		int lid = gl.glGenLists(1);
		
		gl.glNewList(lid, GL.GL_COMPILE);
		gl.glBegin(GL.GL_QUADS);
		generarQuadPlanoZ( gl, 
				 0,  1,  0, 1,
				-l,  0, -l, 0,
				 l );
		generarQuadPlanoZ( gl,
				 1,  0,  0, 1,
				 0,  l, -l, 0,
				 l );
		generarQuadPlanoZ( gl,
				 0,  1,  1, 0,
				-l,  0,  0, l,
				 l );
		generarQuadPlanoZ( gl,
				 1,  0,  1, 0,
				 0,  l,  0, l,
				 l );
		
		generarQuadPlanoY( gl, 
				 0,  1,  0, 1,
				-l,  0, -l, 0,
				 l );
		generarQuadPlanoY( gl,
				 1,  0,  0, 1,
				 0,  l, -l, 0,
				 l );
		generarQuadPlanoY( gl,
				 0,  1,  1, 0,
				-l,  0,  0, l,
				 l );
		generarQuadPlanoY( gl,
				 1,  0,  1, 0,
				 0,  l,  0, l,
				 l );
		
		generarQuadPlanoZ( gl, 
				 0,  1,  0, 1,
				-l,  0, -l, 0,
				-l );
		generarQuadPlanoZ( gl,
				 0,  1,  1, 0,
				-l,  0,  0, l,
				-l );
		generarQuadPlanoZ( gl,
				 1,  0,  0, 1,
				 0,  l, -l, 0,
				-l );
		generarQuadPlanoZ( gl,
				 1,  0,  1, 0,
				 0,  l,  0, l,
				-l );
		
		generarQuadPlanoY( gl, 
				 0,  1,  0, 1,
				-l,  0, -l, 0,
				-l );
		generarQuadPlanoY( gl,
				 1,  0,  0, 1,
				 0,  l, -l, 0,
				-l );
		generarQuadPlanoY( gl,
				 0,  1,  1, 0,
				-l,  0,  0, l,
				-l );
		generarQuadPlanoY( gl,
				 1,  0,  1, 0,
				 0,  l,  0, l,
				-l );
		
		generarQuadPlanoX( gl, 
				 0,  1,  0, 1,
				-l,  0, -l, 0,
				 l );
		generarQuadPlanoX( gl,
				 1,  0,  0, 1,
				-l,  0,  0, l,
				 l );
		generarQuadPlanoX( gl,
				 0,  1,  1, 0,
				 0,  l, -l, 0,
				 l );
		generarQuadPlanoX( gl,
				 1,  0,  1, 0,
				 0,  l,  0, l,
				 l );
		
		generarQuadPlanoX( gl, 
				 0,  1,  0, 1,
				-l,  0, -l, 0,
				-l );
		generarQuadPlanoX( gl,
				 1,  0,  0, 1,
				-l,  0,  0, l,
				-l );
		generarQuadPlanoX( gl,
				 0,  1,  1, 0,
				 0,  l, -l, 0,
				-l );
		generarQuadPlanoX( gl,
				 1,  0,  1, 0,
				 0,  l,  0, l,
				-l );
		gl.glEnd();
		gl.glEndList();
		
		Apariencia ap = new Apariencia();
		ap.setMaterial(Material.DEFAULT);
		return new Objeto3D(new OglList(lid), ap);
	}
}
