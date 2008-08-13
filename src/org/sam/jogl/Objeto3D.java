package org.sam.jogl;

import javax.media.opengl.GL;

public class Objeto3D  implements Dibujable {
	private Forma3D  forma3D;
	private Apariencia apariencia;

	public Objeto3D() {
	}
	
	public Objeto3D(Forma3D forma3D, Apariencia apariencia) {
		this.forma3D = forma3D;
		this.apariencia = apariencia;
	}
	public Forma3D getForma3D() {
		return forma3D;
	}
	public void setForma3D(Forma3D forma3D) {
		this.forma3D = forma3D;
	}
	public Apariencia getApariencia() {
		return apariencia;
	}
	public void setApariencia(Apariencia apariencia) {
		this.apariencia = apariencia;
	}
	
	/**
	 * 
	 */
	public void draw(GL gl) {
		if( apariencia != null )
			apariencia.activar(gl);
		forma3D.draw(gl);
//		apariencia.desactivar(gl);
	}
}
