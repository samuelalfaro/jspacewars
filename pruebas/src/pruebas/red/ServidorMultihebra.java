/* 
 * ServidorMultihebra.java
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

import org.sam.util.Sincronizador;

public class ServidorMultihebra{

	public static void main(String args[]){
		
		final Sincronizador sincronizador = new Sincronizador();
		
		Thread cliente = new Thread(){
		
			public void run(){
				BufferCanal bc = new BufferBloqueante(sincronizador,1);
				ByteBuffer buff = bc.getByteBuffer();
				
				while(true){

					buff.rewind();
					int len = (int)(Math.random()*(8192/4-1));
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
			}
		};
		
		BufferCanal bc = new BufferBloqueante(sincronizador,0);
		ByteBuffer buff = bc.getByteBuffer();
			
		System.out.println("Servidor esperando conexiones");
		
		cliente.start();
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
	}
}
