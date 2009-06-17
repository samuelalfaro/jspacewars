/*
 * Created on 05-ene-2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.sam.tips;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

public class PruebaPipes2 {

	static class Lector extends Thread{
		PipedInputStream pipeIn;
		boolean solo;
		
		public Lector(){
			setName("Lector");
			solo = true;
			start();
		}
		
		public synchronized void run(){
			try{
				while(solo)
					wait();
				
				System.err.println("Lector: Comenzando a leer datos");
				int tbuf = 2048;
				byte[] buffer = new byte[tbuf];
				
				while(true){
					try{	
						final int bytes = pipeIn.read(buffer);
						if(bytes == -1)
							break;
						System.err.write(buffer,0,bytes);
					}catch(IOException e){
						System.err.println("Lector: Error leyendo pipe");
					}
				}
			}catch(InterruptedException e){
				System.err.println("Lector: Error tiempo de espera agotado");
			}
		}
		
		public synchronized boolean conectarTubos( PipedOutputStream pipeOut){
			try {
				pipeIn = new PipedInputStream(pipeOut);
			} catch (IOException connectionFailed) {
				System.err.println("Lector: conexion fallida");
				return false;
			}
			System.err.println("Lector: conexion realizada");
			solo = false;
			notifyAll();
			return true;
		}
	}

	static class Escritor extends Thread{
		
		final PipedOutputStream pipeOut;
		
		public Escritor(){
			pipeOut = new PipedOutputStream();
			PrintStream log = new PrintStream(pipeOut);
			System.setOut(log);
			start();
		}
		
		public void run(){
			try{
				Lector lector = (Lector)buscarThread("Lector");
				if( !lector.conectarTubos(pipeOut)){
					System.err.println("Escritor: Error conexion no efectuada");
					return;
				}
			}catch(NullPointerException e){
				System.err.println("Escritor: Error no ecuentra hilo lector");
				return;
			}catch(ClassCastException e){
				System.err.println("Escritor: Error no ecuentra hilo lector");
				return;
			}

			System.out.println("Escritor: Comenzando a escribir");
			for(int cont = 1; cont <= 20; cont++)
				System.out.println("Escritor: \tmensaje "+cont);
			System.out.println("Escritor: Cerrando conexion");
			
			try{
				pipeOut.close();
			}catch(IOException e){
			}
		}
	}

	public static Thread buscarThread(String objetivo) {
		final int MARGEN_DE_SEGURIDAD = 10;
		
		ThreadGroup rootGroup = Thread.currentThread().getThreadGroup();
		while (rootGroup.getParent() != null) {
			rootGroup = rootGroup.getParent();
		}
		Thread threadList[] = new Thread [rootGroup.activeGroupCount() + MARGEN_DE_SEGURIDAD];
		int count = rootGroup.enumerate(threadList);
		Thread aThread;
		for (int i=0; i<count; i++) {
			aThread = threadList[i];
			if ((aThread != null) && (aThread.getName().equals(objetivo)))
				return aThread;
		}
		return null;
	}
	
	public static void main(String[] args) {
		
		Lector   lector   = new Lector();
		Escritor escritor = new Escritor();

		try{
			lector.join();
			escritor.join();
		}catch(InterruptedException e){
		}
	}
}
