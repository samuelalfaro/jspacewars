package org.sam.jogl;

import javax.media.opengl.GL;

/**
 * Clase que almacena en una lista memoria de video, la información de la
 * geometría que se genera mediante llamadas contenidas entre
 * {@code gl.glBegin(Primitive)} y {@code gl.glEnd()}.
 */
public class OglList implements Geometria {
	
	private final transient int idList;
	
	/**
	 * Constructor que crea un {@code OglList}, almacenando su indentificador
	 * y activando el contexto, para comenzar a almacenar la geometría 
	 * en memoria de video.<br/>
	 * <u>Nota:</u> Es inpresdible invocar {@link #endList(GL)}, tras llamar a
	 * este constructor, para indicar cuando se ha finalizado de almacenar la
	 * lista.
	 * @param gl Contexto gráfico en el que se realiza a acción.
	 */
	public OglList(GL gl){
		this.idList = gl.glGenLists(1);
		gl.glNewList(this.idList, GL.GL_COMPILE);
	}
	
	/**
	 * Método que indica se ha finalizado de almacenar la lista.
	 * @param gl Contexto gráfico en el que se realiza a acción.
	 */
	public static void endList(GL gl) {
		gl.glEndList();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void draw(GL gl) {
		gl.glCallList(idList);
	}
}
