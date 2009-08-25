/*
 * Created on 05-ene-2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package tips;

import java.io.*;

public class PruebaPipes {

	public static void main(String[] args) {
		final int EOF = -1;

		PipedOutputStream pipeOut = new PipedOutputStream();
		PrintStream log = new PrintStream(new BufferedOutputStream(pipeOut));
		
		PipedInputStream pipeIn= null;
		
		try{
			pipeIn = new PipedInputStream(pipeOut);
			System.err.println("Tuberia creada");
		}catch(IOException e){
			System.err.println("Error crendo pipe");
		}
		System.setOut(log);
		
		// Creamos un nuevo hilo para evitar el bloqueo cuando se llena el stream
		// de salida.
		new Thread(){
			public void run(){
				System.out.println("Escribiendo en pipe");
				for(int i=0; i< 10000; i++)
					System.out.println("Dato "+i);
				//Cerramos el canal de salida, para que pueda finalizar el programa.
				System.out.close();
			}
		}.start();

		System.err.print("Datos recibidos por la tuberia:\n\t");
		int tbuf = 2048;
		byte[] buffer = new byte[tbuf];
		int bytes;
		try{	
			while ((bytes = pipeIn.read(buffer))!= EOF){
				System.err.write(buffer,0,bytes);
			}
		}catch(IOException e){
			System.err.println("Error leyendo tuberia");
		}
	}
}
