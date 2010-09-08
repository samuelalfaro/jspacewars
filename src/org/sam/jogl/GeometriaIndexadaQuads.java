package org.sam.jogl;

import javax.media.opengl.GL;

/**
 * Implementación de una {@code Geometria} diseñada para almacenar cuadriláteros indexados.
 */
public class GeometriaIndexadaQuads extends GeometriaIndexada {

	/**
	 * Constructor que genera una {@code GeometriaIndexadaQuads} con los parámetros correspondientes.
	 * @param nVertex  {@link GeometriaAbs#nVertex Número de vértices} de la {@code Geometria} creada.
	 * @param att_mask {@link GeometriaAbs#att_mask Máscara de atributos} de la {@code Geometria} creada.
	 */
	public GeometriaIndexadaQuads(int nVertex, int att_mask) {
		super(nVertex, att_mask);
		// TODO Auto-generated constructor stub
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void draw(GL gl) {
		// TODO Auto-generated method stub
	}
}
