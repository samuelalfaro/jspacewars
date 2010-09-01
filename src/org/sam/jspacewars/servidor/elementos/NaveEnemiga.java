/**
 * 
 */
package org.sam.jspacewars.servidor.elementos;

import org.sam.colisiones.Poligono;
import org.sam.jspacewars.servidor.tareas.Tarea;

/**
 * @author samuel
 * 
 */
public class NaveEnemiga extends Nave {

	private Tarea tarea;
	
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

	private transient long time;
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sam.elementos.Nave#iniciar()
	 */
	@Override
	public void iniciar() {
		super.inicializar();
		time = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sam.elementos.Dinamico#actua(long)
	 */
	public void actua(long nanos) {
		long startTime = time;
		time += nanos;
		if(tarea != null)
			tarea.realizar(this, startTime, time);
	}
}
