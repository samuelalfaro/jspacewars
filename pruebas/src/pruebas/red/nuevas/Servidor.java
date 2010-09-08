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
package pruebas.red.nuevas;

public class Servidor {

	@SuppressWarnings("unused")
	private static class HiloServidor extends Thread{

		public HiloServidor(int conexion){
			establecer(conexion);
		}
		
		private void establecer(int conexion){
		}
		
		private void recibirDatos(){
		}
		
		private void enviarDatos(){
			// mientras datos bloqueados esperar
		}
		
		private void atenderCliente(){
			recibirDatos();
			enviarDatos();
		}
		
		public void run(){
			while(true){
				atenderCliente();
			}
		}
	}

	@SuppressWarnings("unused")
	private static class HiloMotor extends Thread{

		public HiloMotor(){
		}
		
		private void actualizarDatos(){
		}

		private void moverJuego(){
			//Bloquear datos
			actualizarDatos();
			//Desbloquar datos
		}
		
		public void run(){
			try {
				long sleep, currentTime, oldTime = System.currentTimeMillis();
				while(true){
					moverJuego();
					currentTime = System.currentTimeMillis();
					if( (sleep = 36 - (currentTime - oldTime)) > 0)
						Thread.sleep(sleep);
					oldTime = currentTime;
				}
			}catch (InterruptedException e){
				e.printStackTrace();
			}
		}
	}

	public Servidor() {
		super();
	}

	public static void main(String[] args) {
	}
}
