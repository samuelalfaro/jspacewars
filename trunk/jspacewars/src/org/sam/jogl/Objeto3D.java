package org.sam.jogl;

import javax.media.opengl.GL;

public class Objeto3D extends  ObjetoAbstracto{
	private Forma3D  forma3D;

	public Objeto3D() {
	}
	
	public Objeto3D(Forma3D forma3D, Apariencia apariencia) {
		super(apariencia);
		this.forma3D = forma3D;
	}
	
	protected Objeto3D(Objeto3D me) {
		super(me);
		this.forma3D = me.forma3D;
	}
	
	public Forma3D getForma3D() {
		return forma3D;
	}
	
	public void setForma3D(Forma3D forma3D) {
		this.forma3D = forma3D;
	}
	
	/* (non-Javadoc)
	 * @see org.sam.jogl.Dibujable#draw(javax.media.opengl.GL)
	 */
	public void draw(GL gl) {
		super.draw(gl);
		if(forma3D != null)
			forma3D.draw(gl);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Objeto3D clone(){
		return new Objeto3D(this);
	}
}
