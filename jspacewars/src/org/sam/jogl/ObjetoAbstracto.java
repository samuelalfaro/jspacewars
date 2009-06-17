package org.sam.jogl;

import javax.media.opengl.GL;


public abstract class ObjetoAbstracto implements Nodo {
	private transient Nodo parent;
	private Apariencia apariencia;

	protected ObjetoAbstracto() {
	}
	
	protected ObjetoAbstracto(Apariencia apariencia) {
		this.apariencia = apariencia;
	}

	protected ObjetoAbstracto(ObjetoAbstracto me) {
		this.apariencia = me.apariencia;
	}
	
	public final Apariencia getApariencia() {
		return apariencia;
	}
	
	public final void setApariencia(Apariencia apariencia) {
		this.apariencia = apariencia;
	}
	
	/* (non-Javadoc)
	 * @see org.sam.jogl.Dibujable#draw(javax.media.opengl.GL)
	 */
	public void draw(GL gl) {
		if( apariencia != null )
			apariencia.usar(gl);
	}
	
	/* (non-Javadoc)
	 * @see org.sam.jogl.Nodo#setParent(org.sam.jogl.Nodo)
	 */
	public final void setParent(Nodo parent) {
		this.parent = parent;
	}
	
	/* (non-Javadoc)
	 * @see org.sam.jogl.Nodo#getParent()
	 */
	public final Nodo getParent() {
		return parent;
	}
	
	private transient final Nodo[] nodos = new Nodo[]{}; 
	public final Nodo[] getChilds(){
		return nodos;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public abstract ObjetoAbstracto clone();
}
