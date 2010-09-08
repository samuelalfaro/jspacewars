/* 
 * Servidor.java
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

import java.net.SocketException;
import java.nio.ByteBuffer;


public class Servidor{

	private final static int PORT = 13;
	
	public static void main(String args[]){
	
		try{
			BufferCanal bc = new BufferSocket(PORT);
			ByteBuffer buff = bc.getByteBuffer();
			
			System.out.println("Servidor esperando conexiones");

			while(true){
				bc.recibir();
				int len = buff.getInt();
					
				buff.rewind();
				System.out.println("enviando "+len+" datos");
				buff.putInt(len);
				for(int i=0; i < len; i++){
					buff.putFloat(i);
				}
				bc.enviar();
			}
		}catch (SocketException e) {
			e.printStackTrace();
		}
	}
}
