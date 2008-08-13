package org.sam.red;

import java.nio.ByteBuffer;

/**
 * Esta clase se encarga de bloquear a las hebras que llaman a sus métodos enviar/recibir simulando una comunicación
 * de tal forma que una hebra quedará bloqueada hasta que otra hebra no haya realizado un envio. 
 */
public class BufferBloqueante extends BufferCanal {
	
	private static ByteBuffer canal = null;
	private static boolean hayDatos = false;
	private static int emisor = -1;

	private int me;
	private Sincronizador sincronizador;
	
	public BufferBloqueante(Sincronizador sincronizador, int id){
		if (canal == null){
			canal = ByteBuffer.wrap(new byte[TAM_DATAGRAMA]); 
		}
		buff = canal;
		this.sincronizador = sincronizador;
		me = id;
	}
	
	/**
	 * <p>Este método no envia nada, simplemente, notifica a otra hebra que estará bloqueada en el método recibir que
	 * puede leer datos.</p>
	 * <p>Como la comunicacion es entre dos hebras, si la hebra que ha llamado a este método, ejecuta 
	 * seguidamente recibir, se quedará bloqueda, hasta que otra hebra llame al método enviar.</p>
	 */
	public void enviar() {
		while(hayDatos)
			sincronizador.esperar();
		hayDatos = true;
		emisor = me;
		buff.rewind();
		sincronizador.notificar();
	}
	
	/**
	 * Este método no recibe nada, simplemente bloquea a la hebra llamante hasta que otra hebra llame al método enviar
	 */
	public void recibir() {
		while(!hayDatos || emisor == me)
			sincronizador.esperar();
		hayDatos = false;
		sincronizador.notificar();
	}
}
