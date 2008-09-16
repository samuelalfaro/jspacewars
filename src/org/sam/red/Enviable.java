package org.sam.red;

import java.nio.ByteBuffer;

/**
 * Este interface indica que un objeto se puede enviar a traves de un ByteBuffer.
 */
public interface Enviable {
	
	/**
	 * Este método se encarga de enviar sus datos por un ByteBuffer.
	 * 
	 * @param buff ByteBuffer por el que se envian los datos.
	 */
	public void enviar(ByteBuffer buff);

}
