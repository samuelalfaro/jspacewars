/**
 * 
 */
package org.sam.jspacewars.servidor.elementos;

import org.sam.colisiones.Poligono;

/**
 * @author samuel
 * 
 */
public class NaveEnemiga extends Nave {

	/**
	 * @param tipo
	 */
	public NaveEnemiga(short code, Poligono forma) {
		super(code, forma);
		dstDisparos = null;
	}

	/**
	 * @param prototipo
	 */
	protected NaveEnemiga(NaveEnemiga prototipo) {
		super(prototipo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sam.elementos.Nave#clone()
	 */
	@Override
	public NaveEnemiga clone() {
		return new NaveEnemiga(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sam.elementos.Nave#iniciar()
	 */
	@Override
	public void iniciar() {
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sam.elementos.Dinamico#actua(long)
	 */
	public void actua(long nanos) {
		// TODO Auto-generated method stub
	}
}
