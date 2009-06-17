package org.sam.pruebas.red;

import java.net.SocketException;
import java.nio.ByteBuffer;

import org.sam.red.BufferCanal;
import org.sam.red.BufferSocket;

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