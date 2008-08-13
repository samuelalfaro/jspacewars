package org.sam.red.pruebas.nuevas;

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
