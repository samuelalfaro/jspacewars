/* 
 * Cliente.java
 * 
 * Copyright (c) 2009 Samuel Alfaro <samuelalfaro at gmail.com>. All rights reserved.
 * 
 * This file is part of jspacewars.
 * 
 * jspacewars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * jspacewars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with jspacewars.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.sam.jspacewars.cliente;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ListIterator;

import org.sam.elementos.Cache;
import org.sam.jogl.Instancia3D;

/**
 * 
 * @author Samuel Alfaro
 */
public class Cliente extends Thread {

	private static void recibir(ByteBuffer productor, Cache<Instancia3D> cache, ClientData consumidor) {
		consumidor.nBombas = productor.getInt();
		consumidor.nVidas = productor.getInt();
		consumidor.puntos = productor.getInt();

		for( int i = 0; i < consumidor.nivelesFijos.length; i++ )
			consumidor.nivelesFijos[i] = productor.getInt();

		for( int i = 0; i < consumidor.nivelesActuales.length; i++ )
			consumidor.nivelesActuales[i] = productor.getInt();

		for( int i = 0; i < consumidor.nivelesDisponibles.length; i++ )
			consumidor.nivelesDisponibles[i] = productor.getInt();

		consumidor.indicador = productor.getInt();
		consumidor.grado = productor.getInt();

		int i = 0, nElementos = productor.getInt();
		ListIterator<Instancia3D> iConsumidor = consumidor.elementos.listIterator();

		if( iConsumidor.hasNext() && i < nElementos ){
			short tipo = productor.getShort();
			short id = productor.getShort();
			i++;
			Instancia3D almacenado = iConsumidor.next();

			do{
				if( almacenado.getTipo() < tipo || (almacenado.getTipo() == tipo && almacenado.getId() < id) ){
					if( almacenado.getModificador() != null )
						consumidor.modificadores.remove(almacenado.getModificador());
					cache.cached(almacenado);
					iConsumidor.remove();
					if( !iConsumidor.hasNext() ){
						Instancia3D nuevo = cache.newObject(tipo);
						if( nuevo.getModificador() != null )
							consumidor.modificadores.add(nuevo.getModificador());
						nuevo.setId(id);
						nuevo.recibir(productor);
						consumidor.elementos.add(nuevo);
						break;
					}
					almacenado = iConsumidor.next();
				}else if( almacenado.getTipo() > tipo || (almacenado.getTipo() == tipo && almacenado.getId() > id) ){
					Instancia3D nuevo = cache.newObject(tipo);
					if( nuevo.getModificador() != null )
						consumidor.modificadores.add(nuevo.getModificador());
					nuevo.setId(id);
					nuevo.recibir(productor);
					iConsumidor.add(nuevo);
					if( i == nElementos ){
						if( almacenado.getModificador() != null )
							consumidor.modificadores.remove(almacenado.getModificador());
						cache.cached(almacenado);
						iConsumidor.remove();
						break;
					}
					tipo = productor.getShort();
					id = productor.getShort();
					i++;
				}else{
					almacenado.recibir(productor);
					if( i == nElementos || !iConsumidor.hasNext() )
						break;
					tipo = productor.getShort();
					id = productor.getShort();
					i++;
					almacenado = iConsumidor.next();
				}
			}while( true );
		}
		// Si se reciben mas elementos de los que hay, se crean.
		while( i < nElementos ){
			i++;
			Instancia3D nuevo = cache.newObject(productor.getShort());
			if( nuevo.getModificador() != null )
				consumidor.modificadores.add(nuevo.getModificador());
			nuevo.setId(productor.getShort());
			nuevo.recibir(productor);
			consumidor.elementos.add(nuevo);
		}
		// Si queda mas elementos de los que se reciben se eliminan
		while( iConsumidor.hasNext() ){
			Instancia3D viejo = iConsumidor.next();
			cache.cached(viejo);
			if( viejo.getModificador() != null )
				consumidor.modificadores.remove(viejo.getModificador());
			iConsumidor.remove();
		}
		if( consumidor.elementos.size() != nElementos )
			System.out.println("Error: " + nElementos + "\t" + consumidor.elementos.size());
	}

	private final transient ReadableByteChannel channelIn;
	private final transient WritableByteChannel channelOut;
	private final transient ByteBuffer buff;

	private final transient Cache<Instancia3D> cache;
	private final transient ClientData data;
	private final transient PantallaCliente pantalla;

	/**
	 * @param channelIn
	 * @param channelOut
	 * @param cache
	 * @param data
	 * @param pantalla
	 */
	public Cliente(ReadableByteChannel channelIn, WritableByteChannel channelOut, Cache<Instancia3D> cache,
			ClientData data, PantallaCliente pantalla) {

		this.channelIn = channelIn;
		this.channelOut = channelOut;
		this.buff = ByteBuffer.allocateDirect(8192);

		this.cache = cache;
		this.data = data;
		this.pantalla = pantalla;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {

		while( true ){
			buff.clear();
			buff.putInt(data.key_state);
			buff.flip();
			try{
				channelOut.write(buff);
			}catch( IOException e ){
				e.printStackTrace();
			}

			buff.clear();
			try{
				channelIn.read(buff);
			}catch( IOException e ){
				e.printStackTrace();
			}
			buff.flip();

			recibir(buff, cache, data);

			pantalla.update();
		}
	}
}