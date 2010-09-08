/* 
 * BufferBloqueante.java
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
package pruebas.red;

import java.nio.ByteBuffer;

import org.sam.util.Sincronizador;

/**
 * Esta clase se encarga de bloquear a las hebras que llaman a sus m�todos enviar/recibir simulando una comunicaci�n
 * de tal forma que una hebra quedar� bloqueada hasta que otra hebra no haya realizado un envio. 
 */
public class BufferBloqueante extends BufferCanal {

    private static ByteBuffer canal = null;
    private static boolean hayDatos = false;
    private static int emisor = -1;
    private int me;
    private Sincronizador sincronizador;

    public BufferBloqueante(Sincronizador sincronizador, int id) {
        if (canal == null) {
            canal = ByteBuffer.wrap(new byte[TAM_DATAGRAMA]);
        }
        buff = canal;
        this.sincronizador = sincronizador;
        me = id;
    }

    /**
     * <p>Este m�todo no envia nada, simplemente, notifica a otra hebra que estar� bloqueada en el m�todo recibir que
     * puede leer datos.</p>
     * <p>Como la comunicacion es entre dos hebras, si la hebra que ha llamado a este m�todo, ejecuta 
     * seguidamente recibir, se quedar� bloqueda, hasta que otra hebra llame al m�todo enviar.</p>
     */
    public void enviar() {
        while (hayDatos) {
            sincronizador.esperar();
        }
        hayDatos = true;
        emisor = me;
        buff.rewind();
        sincronizador.notificar();
    }

    /**
     * Este m�todo no recibe nada, simplemente bloquea a la hebra llamante hasta que otra hebra llame al m�todo enviar
     */
    public void recibir() {
        while (!hayDatos || emisor == me) {
            sincronizador.esperar();
        }
        hayDatos = false;
        sincronizador.notificar();
    }
}
