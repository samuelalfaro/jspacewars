package org.sam.pruebas.red;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import org.sam.red.BufferCanal;
import org.sam.red.BufferSocket;

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