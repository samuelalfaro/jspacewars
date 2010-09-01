package org.sam.util;

/**
 * Clase que proporciona un objeto para que varios objetos de otras clases lo compartan y se puedan sincronizar.
 */
public class Sincronizador {
	
	/**
	 * Bloquea a la hebra llamante hasta que es notificada.
	 */
	public void esperar() {
		synchronized(this){
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Bloquea a la hebra llamante hasta que es notificada.
	 * 
	 * @param milis máximo tiempo de espera en milisegundos.
	 */
	public void esperar(long milis) {
		synchronized(this){
			try {
				wait(milis);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Notifica a las hebras bloquadas por los métodos {@linkplain #esperar()} o {@linkplain #esperar(long)} que continuen.
	 */
	public void notificar() {
		synchronized(this){
			notifyAll();
		}
	}
}
