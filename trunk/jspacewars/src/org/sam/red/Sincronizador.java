package org.sam.red;

/**
 * Esta clase proporciona un objeto para que varios objetos de otras clases lo compartan y se puedan sincronizar.
 */
public class Sincronizador {
	
	/**
	 * Bloquea a la hebra llamante hasta que es notificada
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
	 * Bloquea a la hebra llamante hasta que es notificada
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
	 * Notifica a las hebras que están esperando en el método <code>esperar()</code> que continuen.
	 */
	public void notificar() {
		synchronized(this){
			notifyAll();
		}
	}
	
}
