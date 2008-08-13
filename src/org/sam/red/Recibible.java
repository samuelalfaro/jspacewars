package org.sam.red;

import java.nio.ByteBuffer;

/**
 * Este interface indica que un objeto se puede recibir a traves de un ByteBuffer.
 */
public interface Recibible {
	
	/**
	 * Este método se encarga de cargar sus datos de un ByteBuffer.
	 * 
	 * @param buff ByteBuffer por el que se reciben los datos.
	 */
	public void recibir(ByteBuffer buff);

}
