package org.sam.jogl;

import javax.media.opengl.GL;

/**
 * Interface indica que los objeto, de las clases que lo implementan,
 * pueden ser dibujados, y proporciona el método para realizar dicha acción.
 */
public interface Dibujable {
	
	/**
	 * Método encargado de dibujar el objeto correspondiente.
	 * 
	 * @param gl Contexto gráfico en el que se realiza a acción.
	 */
	public void draw(GL gl);
}
