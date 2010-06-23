package org.sam.jogl;

import javax.media.opengl.GL;

public class Material {
	
	private transient static Material anterior;
	
	private final float ambient[];
	private final float diffuse[];
	private final float emission[];
	private final float shininess[];
	private final float specular[];
	
	public static final Material DEFAULT = new Material() {
		public void setAmbient(float r, float g, float b, float a) {
			throw new UnsupportedOperationException("DEFAULT can't be changed");
		}

		public void setDiffuse(float r, float g, float b, float a) {
			throw new UnsupportedOperationException("DEFAULT can't be changed");
		}

		public void setEmission(float r, float g, float b, float a) {
			throw new UnsupportedOperationException("DEFAULT can't be changed");
		}

		public void setSpecular(float r, float g, float b, float a) {
			throw new UnsupportedOperationException("DEFAULT can't be changed");
		}
		
		public void setShininess(float shininess) {
			throw new UnsupportedOperationException("DEFAULT can't be changed");
		}
	};
	
	public Material(){
		ambient   = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
		diffuse   = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
		emission  = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
		shininess = new float[]{64.0f};
		specular  = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
	}
	
	private static float[] rgbaToArray(float r, float g, float b, float a, float[] dst){
		if ((r < 0.0f  || r > 1.0f) ||
			(g < 0.0f  || g > 1.0f) ||
			(b < 0.0f  || b > 1.0f) ||
			(a < 0.0f  || a > 1.0f))
			throw new IllegalArgumentException();
		if(dst  == null)
			dst = new float[]{r,g,b,a};
		else{
			dst[0] = r;
			dst[1] = g;
			dst[2] = b;
			dst[3] = a;
		}
		return dst;
	}
	
	public void setAmbient(float r, float g, float b, float a) {
		rgbaToArray(r, g, b, a, this.ambient);
	}

	public void setDiffuse(float r, float g, float b, float a) {
		rgbaToArray(r, g, b, a, this.diffuse);
	}

	public void setEmission(float r, float g, float b, float a) {
		rgbaToArray(r, g, b, a, this.emission);
	}

	public void setShininess(float shininess) {
		if(shininess < 0.0f || shininess > 128.0f)
			throw new IllegalArgumentException(); 
		this.shininess[0] = shininess;
	}
	
	public void setSpecular(float r, float g, float b, float a) {
		rgbaToArray(r, g, b, a, this.specular);
	}
	
	public void activar(GL gl){
		if( anterior == this )
			return;
		if( anterior == null )
			gl.glEnable(GL.GL_LIGHTING);

		gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, ambient, 0);
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, diffuse, 0);
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, emission, 0);
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, shininess, 0);
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, specular, 0);

		anterior = this;
	}
	
	public static  void desactivar(GL gl){
		if( anterior == null )
			return;
		gl.glDisable(GL.GL_LIGHTING);
		anterior = null;
	}
}
