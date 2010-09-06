package org.sam.jogl;

import javax.media.opengl.GL;

/**
 * Clase base sobre la que crear, clases descendientes de
 * un {@code Nodo} con {@code Apariencia}.
 */
public abstract class Objeto3DAbs extends Hoja {

	private Apariencia apariencia;

	/**
	 * Construtor por defecto que crea un {@code Objeto3DAbs}.
	 */
	protected Objeto3DAbs() {
	}
	
	/**
	 * Construtor que crea un {@code Objeto3DAbs} y le
	 * asigna {@code Apariencia} indicada.
	 * @param apariencia  La {@code Apariencia} asignada.
	 */
	protected Objeto3DAbs(Apariencia apariencia) {
		this.apariencia = apariencia;
	}

	/**
	 * Construtor que crea un {@code Objeto3DAbs} copiando los
	 * datos de otro {@code Objeto3DAbs} que sirve como prototipo.
	 * @param me {@code Objeto3DAbs} prototipo.
	 */
	protected Objeto3DAbs(Objeto3DAbs me) {
		this.apariencia = me.apariencia;
	}
	
	/**
	 * <i>Getter</i> que devuelve la {@code Apariencia} de este {@code Objeto3DAbs}.
	 * @return La {@code Apariencia} solicitada.
	 */
	public final Apariencia getApariencia() {
		return apariencia;
	}

	/**
	 * <i>Setter</i> que asigna una {@code Apariencia} a este {@code Objeto3DAbs}.
	 * @param apariencia La {@code Apariencia} asignada.
	 */
	public final void setApariencia(Apariencia apariencia) {
		this.apariencia = apariencia;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void draw(GL gl) {
		if( apariencia != null )
			apariencia.usar(gl);
	}
}
