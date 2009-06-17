package org.sam.red;

import java.nio.ByteBuffer;

/**
 * Clase que generaliza la comunicacion mediante ByteBuffers
 */
public abstract class BufferCanal {
    
    protected final static int TAM_DATAGRAMA = 8192;
	
	protected ByteBuffer buff;
	
	/**
	 * Devuelve el ByteBuffer del que encapsula la comunicacion
	 * 
	 * @return ByteBuffer encapsulado;
	 */
	public final ByteBuffer getByteBuffer(){
		return buff;
	}
	
	/**
	 * Este método enviará el ByteBuffer por el canal correspondiente.
	 */
	public abstract void enviar();
	
	/**
	 * Este método recibirá del ByteBuffer por el canal correspondiente.
	 */
	public abstract void recibir();
}
