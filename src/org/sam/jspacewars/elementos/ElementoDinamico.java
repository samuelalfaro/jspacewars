package org.sam.jspacewars.elementos;

import org.sam.elementos.Dinamico;

public abstract class ElementoDinamico extends Elemento implements Dinamico {

	public ElementoDinamico(short tipo) {
		super(tipo);
	}

	protected ElementoDinamico(ElementoDinamico prototipo) {
		super(prototipo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sam.elementos.Elemento#clone()
	 */
	public abstract ElementoDinamico clone();
}