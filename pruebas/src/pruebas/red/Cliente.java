/* 
 * Cliente.java
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
import java.net.UnknownHostException;
import java.nio.ByteBuffer;


public class Cliente{

	private final static int PORT = 13;
	private final static int TAM_DATAGRAMA = 8192;
	
	public static void main(String args[]){
		
		System.out.println("Cliente");
		try{
			BufferCanal bc = new BufferSocket("LocalHost",PORT);
			ByteBuffer buff = bc.getByteBuffer();
			
			while(true){
				buff.rewind();
				int len = (int)(Math.random()*(TAM_DATAGRAMA/4-1));
				//int len = (int)(Math.random()*20);
				System.out.println("solicitando "+len+" datos");
				buff.putInt(len);
				bc.enviar();
				
				bc.recibir();
				len = buff.getInt();
				System.out.println("recibiendo "+len+" datos");
				for(int i=0; i < len; i++){
					System.out.println(buff.getFloat());
				}
			}
		}catch (SocketException e) {
			e.printStackTrace();
		}catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
}
