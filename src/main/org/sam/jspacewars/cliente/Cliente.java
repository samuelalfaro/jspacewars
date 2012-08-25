/* 
 * Cliente.java
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
package org.sam.jspacewars.cliente;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ListIterator;

import javax.media.opengl.awt.GLCanvas;

import org.sam.elementos.Cache;
import org.sam.jogl.Instancia3D;
import org.sam.jspacewars.DataGame;

public class Cliente extends Thread {

	/**
	 * Método estático que interpreta desde un {@code ByteBuffer} los datos recibidos del
	 * {@link org.sam.jspacewars.servidor.ServidorJuego  ServidorJuego} y los almacena en un objecto {@code ClientData}
	 * para mostrarlo en pantalla a través de {@link org.sam.jspacewars.cliente.Renderer Renderer}.
	 * 
	 * @param productor {@code ByteBuffer} desde donde se leen los datos recibidos.
	 * @param consumidor {@code Object} donde se almancenan los datos leidos.
	 * @param cache {@code Cache} desde donde se recuperarán los nuevos elementos necesarios,
	 * o se almacenarán temporarlmente los elementos desechados.
	 */
	private static void interpretar( ByteBuffer productor, ClientData consumidor, Cache<Instancia3D> cache ){
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
				if( almacenado.getType() < tipo || ( almacenado.getType() == tipo && almacenado.getId() < id ) ){
					if( almacenado.getModificador() != null )
						consumidor.modificadores.remove( almacenado.getModificador() );
					cache.cached( almacenado );
					iConsumidor.remove();
					if( !iConsumidor.hasNext() ){
						Instancia3D nuevo = cache.newObject( tipo );
						if( nuevo.getModificador() != null )
							consumidor.modificadores.add( nuevo.getModificador() );
						nuevo.setId( id );
						nuevo.recibir( productor );
						consumidor.elementos.add( nuevo );
						break;
					}
					almacenado = iConsumidor.next();
				}else if( almacenado.getType() > tipo || ( almacenado.getType() == tipo && almacenado.getId() > id ) ){
					Instancia3D nuevo = cache.newObject( tipo );
					if( nuevo.getModificador() != null )
						consumidor.modificadores.add( nuevo.getModificador() );
					nuevo.setId( id );
					nuevo.recibir( productor );
					iConsumidor.add( nuevo );
					if( i == nElementos ){
						if( almacenado.getModificador() != null )
							consumidor.modificadores.remove( almacenado.getModificador() );
						cache.cached( almacenado );
						iConsumidor.remove();
						break;
					}
					tipo = productor.getShort();
					id = productor.getShort();
					i++;
				}else{
					almacenado.recibir( productor );
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
			Instancia3D nuevo = cache.newObject( productor.getShort() );
			if( nuevo.getModificador() != null )
				consumidor.modificadores.add( nuevo.getModificador() );
			nuevo.setId( productor.getShort() );
			nuevo.recibir( productor );
			consumidor.elementos.add( nuevo );
		}
		// Si queda mas elementos de los que se reciben se eliminan
		while( iConsumidor.hasNext() ){
			Instancia3D viejo = iConsumidor.next();
			cache.cached( viejo );
			if( viejo.getModificador() != null )
				consumidor.modificadores.remove( viejo.getModificador() );
			iConsumidor.remove();
		}
		if( consumidor.elementos.size() != nElementos )
			System.err.println(
				"Error recibiendo datos\n" +
				"\t[" + Cliente.class.getName() +"]\n" +
				"\tSe esperaban " + nElementos + " elementos y se han recibido " + consumidor.elementos.size() + "." );
	}

	private transient ReadableByteChannel channelIn;
	private transient WritableByteChannel channelOut;
	private final transient ByteBuffer buff;

	private final transient Cache<Instancia3D> cache;
	private final transient ClientData data;
	private final transient GLCanvas canvas;

	/**
	 * @param dataGame
	 * @param canvas
	 */
	public Cliente( DataGame dataGame, GLCanvas canvas ){

		this.channelIn = null;
		this.channelOut = null;
		this.buff = ByteBuffer.allocateDirect( 8192 );

		this.cache = dataGame.getCache();
		this.data = new ClientData();
		this.canvas = canvas;

		this.canvas.addGLEventListener( new Renderer( dataGame.getFondo(), data, dataGame.getGui() ) );
		this.canvas.addKeyListener( new GameKeyListener( data ) );
	}
	
	/**
	 * @param channelIn valor del channelIn asignado.
	 */
	public void setChannelIn( ReadableByteChannel channelIn ){
		this.channelIn = channelIn;
	}

	/**
	 * @param channelOut valor del channelOut asignado.
	 */
	public void setChannelOut( WritableByteChannel channelOut ){
		this.channelOut = channelOut;
	}

	/** {@inheritDoc} */
	@Override
	public void run(){
		//		System.out.println("Iniciando cliente");
		while( true ){
			//			System.out.println("Enviando");
			buff.clear();
			buff.putInt( data.key_state );
			buff.flip();

			try{
				channelOut.write( buff );
			}catch( AsynchronousCloseException e ){
				e.printStackTrace();
			}catch( ClosedChannelException e ){
				e.printStackTrace();
			}catch( IOException e ){
				e.printStackTrace();
			}
			//			System.out.println("Leyendo");
			buff.clear();
			try{
				channelIn.read( buff );
			}catch( AsynchronousCloseException e ){
				e.printStackTrace();
			}catch( ClosedChannelException e ){
				e.printStackTrace();
			}catch( IOException e ){
				e.printStackTrace();
			}
			buff.flip();
			
			interpretar( buff, data, cache );

			canvas.display();
		}
	}
}
