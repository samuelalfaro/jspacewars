package org.sam.jogl;

import javax.media.opengl.GL;

/**
 * Clase que representa un {@code Nodo} final {@code Dibujable},
 * compuesto por una {@code Geometria} y una {@code Apariencia}.
 */
public class Objeto3D extends Objeto3DAbs{
	private Geometria  geometria;

	/**
	 * Construtor por defecto que crea un {@code Objeto3D}.
	 */
	public Objeto3D() {
	}
	
	/**
	 * Construtor que crea un {@code Objeto3D} con la
	 * {@code forma3D} y la {@code Apariencia} indicadas.
	 * 
	 * @param geometria La {@code Geometria} asignada.
	 * @param apariencia  La {@code Apariencia} asignada.
	 */
	public Objeto3D(Geometria geometria, Apariencia apariencia) {
		super(apariencia);
		this.geometria = geometria;
	}
	
	/**
	 * Construtor que crea un {@code Objeto3D} copiando los
	 * datos de otro {@code Objeto3D} que sirve como prototipo.
	 * @param me {@code Objeto3D} prototipo.
	 */
	protected Objeto3D(Objeto3D me) {
		super(me);
		this.geometria = me.geometria;
	}
	
	/**
	 * <i>Getter</i> que devuelve la {@code Geometria} de este {@code Objeto3D}.
	 * @return La {@code Geometria} solicitada.
	 */
	public Geometria getGeometria() {
		return geometria;
	}
	
	/**
	 * <i>Setter</i> que asigna una {@code Geometria} a este {@code Objeto3D}.
	 * @param geometria La {@code Geometria} asignada.
	 */
	public void setGeometria(Geometria geometria) {
		this.geometria = geometria;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void draw(GL gl) {
		super.draw(gl);
		if(geometria != null)
			geometria.draw(gl);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Objeto3D clone(){
		return new Objeto3D(this);
	}
}
