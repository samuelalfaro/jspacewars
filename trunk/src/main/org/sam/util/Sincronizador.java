/* 
 * Sincronizador.java
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
package org.sam.util;

/**
 * Clase que proporciona un objeto para que varios objetos de otras clases lo compartan y se puedan sincronizar.
 */
public class Sincronizador {
	
	/**
	 * Bloquea a la hebra llamante hasta que es notificada.
	 */
	public void esperar(){
		synchronized( this ){
			try{
				wait();
			}catch( InterruptedException e ){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Bloquea a la hebra llamante hasta que es notificada.
	 * 
	 * @param milis máximo tiempo de espera en milisegundos.
	 */
	public void esperar( long milis ){
		synchronized( this ){
			try{
				wait( milis );
			}catch( InterruptedException e ){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Notifica a las hebras bloquadas por los métodos {@linkplain #esperar()} o {@linkplain #esperar(long)} que continuen.
	 */
	public void notificar(){
		synchronized( this ){
			notifyAll();
		}
	}
}
