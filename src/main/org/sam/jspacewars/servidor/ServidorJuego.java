/* 
 * ServidorJuego.java
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
package org.sam.jspacewars.servidor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Pipe;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.Pipe.SinkChannel;
import java.nio.channels.Pipe.SourceChannel;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.sam.colisiones.ComprobadorDeColisones;
import org.sam.elementos.Cache;
import org.sam.jspacewars.servidor.elementos.Disparo;
import org.sam.jspacewars.servidor.elementos.Elemento;
import org.sam.jspacewars.servidor.elementos.NaveEnemiga;
import org.sam.jspacewars.servidor.elementos.NaveUsuario;
import org.sam.jspacewars.servidor.elementos.SingletonObjetivos;

/**
 * <p>Clase encargada de atender a los clientes.
 * <ul>
 * <li>Recibe las órdenes del cliente correspondiente.</li>
 * <li>Calcula las acciones realizadas por los distintos elementos.</li>
 * <li>Envía los resultados al cliente correspondiente.</li>
 * </ul></p><p>
 * Las peticiones se atienden de forma <b>asíncrona</b>, calculando las acciones
 * producidas durante ese intervalo, mediante incrementos discretos de tiempo.</p>
 * <p>Calcular las acciones de forma <b>discreta</b>, permite:<ul>
 * <li>Enviar la misma respuesta a varios clientes a la vez, cuando sus peticiones
 * coincidan.</li>
 * <li>Que elementos, cuyo comportamiento es <b>díficil integrar en función del tiempo</b>,
 * puesto que <b>dependen de más variables</b>, como los misiles, se comporten siempre
 * igual independientemente de los <i>FPS</i>.</li>
 * <li>Mejorar la eficacia de la detección de colisiones, sin necesidad de modificar
 * el algoritmo, simplemente aumentando la frecuencia de los cálculos.</li> 
 * </ul></p>
 */
public class ServidorJuego {
	
	private static final float ratio = ( 30 * 4.0f / 32 ) / ( 3.0f - 4 * 4.0f / 32 ); // ratio 4/3 sin bordes GUI
	private static final float LIMITE_VERTICAL =  2.9f;
	private static final float LIMITE_HORIZONTAL = ratio * LIMITE_VERTICAL;
	
	private static final float X_MIN = -8.0f;
	private static final float X_MAX = 10.0f;
	private static final float Y_MIN = -4.0f;
	private static final float Y_MAX =  4.0f;
	
	private static final int TAM_DATAGRAMA = 8192;
	
	/**
	 * Caché empleada tanto: para obtener las instancias de los nuevos elementos,
	 * que aparecen durante el juego, como para almacenar, para su posterior uso, las
	 * instancias de los elementos, que desaparecen.
	 */
	private final transient Cache<Elemento> cache;

	private final transient Collection<Elemento> navesProtagonistas;
	private final transient Collection<Collection<Disparo>> listasDeDisparosProtagonistas;
	private final transient Collection<Elemento> navesEnemigas;
	private final transient Collection<Disparo> disparosEnemigos;
	
	private final transient SortedSet<Elemento> elementosOrdenados1_pos;
	private final transient SortedSet<Elemento> elementosOrdenados2_pos;
	
	private final transient ComprobadorDeColisones comprobador;
	
	private final transient Collection<Collection<? extends Elemento>> elementos;
	private final transient SortedSet<Elemento> elementosOrdenados_id;
	
	private transient NaveUsuario nave1, nave2;
	
	/**
	 * Constructor que crea un servidor para atender a un único cliente local,
	 * y le asigna la caché necesaria.
	 * @param cache Caché asignada.
	 * @throws IOException Si se produce algún error al establecer
	 * los canales de comunicación.
	 */
	public ServidorJuego( Cache<Elemento> cache ) throws IOException{
		this( cache, true, 0 );
	}

	/**
	 * Constructor que crea un servidor, con su caché necesaria,
	 * preparado para atender tanto al cliente local, como al cliente de red.
	 * @param cache Caché asignada.
	 * @param port  Puerto por donde acepta las peticiones del cliente de red.
	 * @throws IOException Si se produce algún error al establecer
	 * los canales de comunicación.
	 */
	public ServidorJuego( Cache<Elemento> cache, int port ) throws IOException{
		this( cache, false, port );
	}

	private ServidorJuego( Cache<Elemento> cache, boolean localOnly, int port ) throws IOException{

		this.cache = cache;

		this.navesProtagonistas = new LinkedList<Elemento>();
		this.listasDeDisparosProtagonistas = new LinkedList<Collection<Disparo>>();
		this.navesEnemigas = new LinkedList<Elemento>();
		this.disparosEnemigos = new LinkedList<Disparo>();

		this.elementos = new LinkedList<Collection<? extends Elemento>>();

		this.elementosOrdenados1_pos = new TreeSet<Elemento>( Elemento.COMPARADOR_POSICIONES );
		this.elementosOrdenados2_pos = new TreeSet<Elemento>( Elemento.COMPARADOR_POSICIONES );

		this.comprobador = new ComprobadorDeColisones( 250 );

		this.elementosOrdenados_id = new TreeSet<Elemento>( Elemento.COMPARADOR );

		initData( localOnly );
		initLocalChannel();
		if( !localOnly )
			initRemoteChannel( port );
	}

	private void initData( boolean localOnly ){

		elementos.clear();

		navesProtagonistas.clear();
		elementos.add( navesProtagonistas );

		Collection<Disparo> disparosNave;

		nave1 = (NaveUsuario)cache.newObject( 0x03 );
		disparosNave = new LinkedList<Disparo>();
		nave1.setDstDisparos( disparosNave );
		listasDeDisparosProtagonistas.clear();
		listasDeDisparosProtagonistas.add( disparosNave );
		elementos.add( disparosNave );
		nave1.init();
		nave1.setPosicion( 0, 0 );

		nave1.setLimites( LIMITE_HORIZONTAL, LIMITE_VERTICAL );

		navesProtagonistas.add( nave1 );

		if( !localOnly ){
			nave2 = (NaveUsuario)cache.newObject( 0x01 );
			disparosNave = new LinkedList<Disparo>();
			nave2.setDstDisparos( disparosNave );
			listasDeDisparosProtagonistas.add( disparosNave );
			elementos.add( disparosNave );
			nave2.init();
			nave2.setPosicion( 0, -1 );
			nave2.setLimites( LIMITE_HORIZONTAL, LIMITE_VERTICAL );
			navesProtagonistas.add( nave2 );
		}

		navesEnemigas.clear();
		elementos.add( navesEnemigas );
		disparosEnemigos.clear();
		elementos.add( disparosEnemigos );

		NaveEnemiga naveEnemiga = (NaveEnemiga)cache.newObject( 0x10 );
		naveEnemiga.setPosicion( 0, -2.5f );
		navesEnemigas.add( naveEnemiga );

		naveEnemiga = (NaveEnemiga)cache.newObject( 0x12 );
		naveEnemiga.setPosicion( 3, 2.5f );
		navesEnemigas.add( naveEnemiga );

		naveEnemiga = (NaveEnemiga)cache.newObject( 0x30 );
		naveEnemiga.setPosicion( 3, -0.5f );
		navesEnemigas.add( naveEnemiga );

		SingletonObjetivos.setObjetivoUsuario( naveEnemiga );
	}

	private transient Selector selector;

	private transient SourceChannel localChannelClientIn;

	/**
	 * Método que devuelve el canal de entrada para un cliente local.
	 * @return El canal de entrada solicitado.
	 */
	public SourceChannel getLocalChannelClientIn() {
		return localChannelClientIn;
	}

	private transient SinkChannel localChannelClientOut;

	/**
	 * Método que devuelve el canal de salida para un cliente local.
	 * @return El canal de salida solicitado.
	 */
	public SinkChannel getLocalChannelClientOut() {
		return localChannelClientOut;
	}

	private transient SourceChannel localChannelServerIn;
	private transient SinkChannel localChannelServerOut;

	private transient DatagramChannel remoteChannelInOut;

	private void initLocalChannel() throws IOException{
		selector = Selector.open();

		Pipe pipeServer = Pipe.open();
		Pipe pipeClient = Pipe.open();

		localChannelClientIn = pipeServer.source();
		localChannelClientOut = pipeClient.sink();

		localChannelServerIn = pipeClient.source();
		localChannelServerIn.configureBlocking( false );
		localChannelServerOut = pipeServer.sink();

		localChannelServerIn.register( selector, SelectionKey.OP_READ );

	}

	private void initRemoteChannel( int port ) throws IOException{
		remoteChannelInOut = DatagramChannel.open();
		remoteChannelInOut.configureBlocking( false );
		remoteChannelInOut.socket().bind( new InetSocketAddress( port ) );

		remoteChannelInOut.register( selector, SelectionKey.OP_READ );
	}

	private static boolean inLimits( Elemento e ){
		float x = e.getX();
		float y = e.getY();
		return x > X_MIN && x < X_MAX && y > Y_MIN && y < Y_MAX;
	}

	private static final int TIME_STEP = 2000000; // 500 ciclos por segundo en nanos
	
	private void calcularAcciones(){

		elementosOrdenados1_pos.clear();
		for( Collection<Disparo> listaDisparo: listasDeDisparosProtagonistas )
			for( Disparo d: listaDisparo )
				elementosOrdenados1_pos.add( d );

		elementosOrdenados2_pos.clear();
		for( Elemento elemento: navesEnemigas )
			elementosOrdenados2_pos.add( elemento );

		comprobador.comprobarColisiones( elementosOrdenados1_pos, elementosOrdenados2_pos );

		for( Collection<? extends Elemento> listaElementos: elementos ){
			Iterator<? extends Elemento> iElemento = listaElementos.iterator();
			while( iElemento.hasNext() ){
				Elemento e = iElemento.next();
				if( !inLimits( e ) || e.isDestruido() ){
					iElemento.remove();
					cache.cached( e );
				}else
					e.actua( TIME_STEP );
			}
		}
	}
	
	private long calcularAcciones( long nanos ){
		int steps = (int)( ( nanos + TIME_STEP / 2 ) / TIME_STEP );
		for( int i = 0; i < steps; i++ )
			calcularAcciones();
		if( steps > 0 ){
			elementosOrdenados_id.clear();
			for( Collection<? extends Elemento> listaElementos: elementos )
				for( Elemento elemento: listaElementos ){
					elementosOrdenados_id.add( elemento );
				}
		}
		return nanos - TIME_STEP * steps;
	}

	private void almacenarDatosNave( ByteBuffer buff, int vidas, int bombas, int puntos, NaveUsuario nave ){
		buff.putInt( vidas );
		buff.putInt( bombas );
		buff.putInt( puntos );
		for( int n: nave.getNivelesFijos() )
			buff.putInt( n );
		for( int n: nave.getNivelesActuales() )
			buff.putInt( n );
		for( int n: nave.getNivelesDisponibles() )
			buff.putInt( n );
		buff.putInt( nave.getAumentadoresDeNivel() );
		buff.putInt( nave.getGradoNave() );
	}

	private void almacenarElementos( ByteBuffer buff ){
		buff.putInt( elementosOrdenados_id.size() );
		for( Elemento elemento: elementosOrdenados_id )
			elemento.enviar( buff );
	}

	/**
	 * Método encargado de atender a los clientes de la forma descirta en
	 * la documentación de la clase.
	 * @throws IOException Si se produce un error de comunicación.
	 */
	public void atenderClientes() throws IOException{

		ByteBuffer buffIn = ByteBuffer.allocateDirect( TAM_DATAGRAMA );
		ByteBuffer buffOut = ByteBuffer.allocateDirect( TAM_DATAGRAMA );

		long tActual = System.nanoTime();
		long tAnterior = tActual;

		if( nave2 == null ){ // La nave2 no se ha inciado porque solo hay un jugador
			while( true ){
				selector.select();
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				if( !selectedKeys.isEmpty() ){
					selectedKeys.clear();
					buffIn.clear();
					localChannelServerIn.read( buffIn );
					buffIn.flip();
					nave1.setKeyState( buffIn.getInt() );

					tActual = System.nanoTime();
					long timeOffset = calcularAcciones( tActual - tAnterior );
					tAnterior = tActual - timeOffset;

					int vidas = 2;
					int bombas = 1;
					int puntos = 0;

					buffOut.clear();
					almacenarDatosNave( buffOut, vidas, bombas, puntos, nave1 );
					almacenarElementos( buffOut );
					buffOut.flip();

					localChannelServerOut.write( buffOut );
				}
			}
		}
		while( true ){
			selector.select();
			Iterator<SelectionKey> it = selector.selectedKeys().iterator();
			while( it.hasNext() ){
				SelectionKey key = it.next();
				if( key.channel() == localChannelServerIn ){
					buffIn.clear();
					localChannelServerIn.read( buffIn );
					buffIn.flip();
					nave1.setKeyState( buffIn.getInt() );

					tActual = System.nanoTime();
					long timeOffset = calcularAcciones( tActual - tAnterior );
					tAnterior = tActual - timeOffset;

					int vidas = 2;
					int bombas = 1;
					int puntos = 0;

					buffOut.clear();
					almacenarDatosNave( buffOut, vidas, bombas, puntos, nave1 );
					almacenarElementos( buffOut );
					buffOut.flip();

					localChannelServerOut.write( buffOut );
				}else if( key.channel() == remoteChannelInOut ){
					buffIn.clear();
					SocketAddress sa = remoteChannelInOut.receive( buffIn );
					if( sa != null ){
						buffIn.flip();
						nave2.setKeyState( buffIn.getInt() );

						tActual = System.nanoTime();
						long timeOffset = calcularAcciones( tActual - tAnterior );
						tAnterior = tActual - timeOffset;

						int vidas = 2;
						int bombas = 1;
						int puntos = 0;

						buffOut.clear();
						almacenarDatosNave( buffOut, vidas, bombas, puntos, nave2 );
						almacenarElementos( buffOut );
						buffOut.flip();

						remoteChannelInOut.send( buffOut, sa );
					}
				}
				it.remove();
			}
		}
	}
}
