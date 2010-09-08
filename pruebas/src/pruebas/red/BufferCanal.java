/* 
 * BufferCanal.java
 * 
 * Copyright (c) 2008-2010
 * Samuel Alfaro Jiménez <samuelalfaro at gmail.com>.
 * All rights reserved.
 * 
 * This file is part of jSpaceWars.
 * 
 * jSpaceWars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * jSpaceWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with jSpaceWars. If not, see <http://www.gnu.org/licenses/>.
 */
package pruebas.red;

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
