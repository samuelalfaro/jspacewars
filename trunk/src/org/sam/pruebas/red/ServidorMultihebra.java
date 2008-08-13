package org.sam.pruebas.red;

import java.nio.ByteBuffer;

import org.sam.red.BufferBloqueante;
import org.sam.red.BufferCanal;
import org.sam.red.Sincronizador;

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