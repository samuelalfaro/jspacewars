package org.sam.red;

/**
 * Esta clase proporciona un objeto para que varios objetos de otras clases lo compartan y se puedan sincronizar.
 */
public class Sincronizador {
	
	/**
	 * Bloquea a la hebra llamante hasta que es notificada
	 */
	public synchronized void esperar() {
		try {
			wait();
		} catch (InterruptedException e) {
		}
	}
	
	/**
	 * Notifica a las hebras que están esperando en el método <code>esperar()</code> que continuen.
	 */
	public synchronized void notificar() {
		notifyAll();
	}
	
}
