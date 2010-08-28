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
import org.sam.jspacewars.servidor.elementos.SingletonEnemigos;

public class ServidorJuego {
	
	private static final float ratio = (30*4.0f/32) / (3.0f - 4*4.0f/32); // ratio 4/3 sin bordes GUI
	private static final float LIMITE_VERTICAL =  2.9f;
	private static final float LIMITE_HORIZONTAL = ratio * LIMITE_VERTICAL;
	
	private static final float X_MIN = -8.0f;
	private static final float X_MAX = 10.0f;
	private static final float Y_MIN = -4.0f;
	private static final float Y_MAX =  4.0f;
	
	private static final int TAM_DATAGRAMA = 8192;
	
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
	
	public ServidorJuego(Cache<Elemento> cache) throws IOException {
		this(cache, true, 0);
	}

	public ServidorJuego(Cache<Elemento> cache, int port) throws IOException {
		this(cache, false, port);
	}

	private ServidorJuego(Cache<Elemento> cache, boolean localOnly, int port) throws IOException {

		this.cache = cache;
		
		this.navesProtagonistas = new LinkedList<Elemento>();
		this.listasDeDisparosProtagonistas = new LinkedList<Collection<Disparo>>();
		this.navesEnemigas = new LinkedList<Elemento>();
		this.disparosEnemigos = new LinkedList<Disparo>();
		
		this.elementos = new LinkedList<Collection<? extends Elemento>>();
		
		this.elementosOrdenados1_pos = new TreeSet<Elemento>(Elemento.COMPARADOR_POSICIONES);
		this.elementosOrdenados2_pos = new TreeSet<Elemento>(Elemento.COMPARADOR_POSICIONES);
		
		this.comprobador = new ComprobadorDeColisones(250);
		
		this.elementosOrdenados_id = new TreeSet<Elemento>(Elemento.COMPARADOR);
		
		initData(localOnly);
		initLocalChannel();
		if( !localOnly )
			initRemoteChannel(port);
	}

	private void initData(boolean localOnly) {

		elementos.clear();
		
		navesProtagonistas.clear();
		elementos.add(navesProtagonistas);
		
		Collection<Disparo> disparosNave;

		nave1 = (NaveUsuario) cache.newObject(0x03);
		disparosNave = new LinkedList<Disparo>();
		nave1.setDstDisparos(disparosNave);
		listasDeDisparosProtagonistas.clear();
		listasDeDisparosProtagonistas.add(disparosNave);
		elementos.add(disparosNave);
		nave1.iniciar();
		nave1.setPosicion(0, 0);
		
		nave1.setLimites( LIMITE_HORIZONTAL, LIMITE_VERTICAL );
		
		navesProtagonistas.add(nave1);

		if( !localOnly ){
			nave2 = (NaveUsuario) cache.newObject(0x01);
			disparosNave = new LinkedList<Disparo>();
			nave2.setDstDisparos(disparosNave);
			listasDeDisparosProtagonistas.add(disparosNave);
			elementos.add(disparosNave);
			nave2.iniciar();
			nave2.setPosicion(0, -1);
			nave2.setLimites( LIMITE_HORIZONTAL, LIMITE_VERTICAL );
			navesProtagonistas.add(nave2);
		}

		navesEnemigas.clear();
		elementos.add(navesEnemigas);
		disparosEnemigos.clear();
		elementos.add(disparosEnemigos);
		
		NaveEnemiga bomber = (NaveEnemiga)cache.newObject(0x30);
		bomber.setPosicion(3, -1);
		SingletonEnemigos.setObjetivo(bomber);
		navesEnemigas.add(bomber);
	}

	private transient Selector selector;

	private transient SourceChannel localChannelClientIn;

	public SourceChannel getLocalChannelClientIn() {
		return localChannelClientIn;
	}

	private transient SinkChannel localChannelClientOut;

	public SinkChannel getLocalChannelClientOut() {
		return localChannelClientOut;
	}

	private transient SourceChannel localChannelServerIn;
	private transient SinkChannel localChannelServerOut;

	private transient DatagramChannel remoteChannelInOut;

	private void initLocalChannel() throws IOException {
		selector = Selector.open();

		Pipe pipeServer = Pipe.open();
		Pipe pipeClient = Pipe.open();

		localChannelClientIn = pipeServer.source();
		localChannelClientOut = pipeClient.sink();

		localChannelServerIn = pipeClient.source();
		localChannelServerIn.configureBlocking(false);
		localChannelServerOut = pipeServer.sink();

		localChannelServerIn.register(selector, SelectionKey.OP_READ);

	}

	private void initRemoteChannel(int port) throws IOException {
		remoteChannelInOut = DatagramChannel.open();
		remoteChannelInOut.configureBlocking(false);
		remoteChannelInOut.socket().bind(new InetSocketAddress(port));

		remoteChannelInOut.register(selector, SelectionKey.OP_READ);
	}
	
	private static boolean inLimits(Elemento e){
		float x = e.getX();
		float y = e.getY();
		return  x > X_MIN && x < X_MAX && y > Y_MIN &&  y < Y_MAX;
	}

	private void calcularAcciones(long nanos) {
		
		elementosOrdenados1_pos.clear();
		for(Collection<Disparo> listaDisparo: listasDeDisparosProtagonistas)
			for(Disparo d: listaDisparo)
				elementosOrdenados1_pos.add(d);
		
		elementosOrdenados2_pos.clear();
		for( Elemento elemento: navesEnemigas )
			elementosOrdenados2_pos.add(elemento);
		
		comprobador.comprobarColisiones(elementosOrdenados1_pos, elementosOrdenados2_pos);
		
		
		for( Collection<? extends Elemento> listaElementos: elementos ){
			Iterator<? extends Elemento> iElemento = listaElementos.iterator();
			while( iElemento.hasNext() ){
				Elemento e = iElemento.next();
				if( !inLimits(e)|| e.isDestruido() ){
					iElemento.remove();
					cache.cached(e);
				}else
					e.actua(nanos);
			}
		}
	}

	private void almacenarDatosNave(ByteBuffer buff, int vidas, int bombas, int puntos, NaveUsuario nave) {
		buff.putInt(vidas);
		buff.putInt(bombas);
		buff.putInt(puntos);
		for( int n: nave.getNivelesFijos() )
			buff.putInt(n);
		for( int n: nave.getNivelesActuales() )
			buff.putInt(n);
		for( int n: nave.getNivelesDisponibles() )
			buff.putInt(n);
		buff.putInt(nave.getAumentadoresDeNivel());
		buff.putInt(nave.getGradoNave());
	}

	private void almacenarElementos(ByteBuffer buff) {
		elementosOrdenados_id.clear();
		for( Collection<? extends Elemento> listaElementos: elementos )
			for( Elemento elemento: listaElementos ){
				elementosOrdenados_id.add(elemento);
			}
		buff.putInt(elementosOrdenados_id.size());
		for( Elemento elemento: elementosOrdenados_id )
			elemento.enviar(buff);
	}

	public void atenderClientes() throws IOException {

		ByteBuffer buffIn = ByteBuffer.allocateDirect(TAM_DATAGRAMA);
		ByteBuffer buffOut = ByteBuffer.allocateDirect(TAM_DATAGRAMA);

		long tActual = System.nanoTime();
		long tAnterior = tActual;

		if( nave2 == null ){ // La nave2 no se ha inciado porque solo hay un
								// jugador
			while( true ){
				selector.select();
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				if( !selectedKeys.isEmpty() ){
					selectedKeys.clear();
					buffIn.clear();
					localChannelServerIn.read(buffIn);
					buffIn.flip();
					nave1.setKeyState(buffIn.getInt());

					tAnterior = tActual;
					tActual = System.nanoTime();
					calcularAcciones(tActual - tAnterior);

					int vidas = 2;
					int bombas = 1;
					int puntos = 0;

					buffOut.clear();
					almacenarDatosNave(buffOut, vidas, bombas, puntos, nave1);
					almacenarElementos(buffOut);
					buffOut.flip();

					localChannelServerOut.write(buffOut);
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
					localChannelServerIn.read(buffIn);
					buffIn.flip();
					nave1.setKeyState(buffIn.getInt());

					tAnterior = tActual;
					tActual = System.nanoTime();
					calcularAcciones(tActual - tAnterior);

					int vidas = 2;
					int bombas = 1;
					int puntos = 0;

					buffOut.clear();
					almacenarDatosNave(buffOut, vidas, bombas, puntos, nave1);
					almacenarElementos(buffOut);
					buffOut.flip();

					localChannelServerOut.write(buffOut);
				}else if( key.channel() == remoteChannelInOut ){
					buffIn.clear();
					SocketAddress sa = remoteChannelInOut.receive(buffIn);
					if( sa != null ){
						buffIn.flip();
						nave2.setKeyState(buffIn.getInt());

						tAnterior = tActual;
						tActual = System.nanoTime();
						calcularAcciones(tActual - tAnterior);

						int vidas = 2;
						int bombas = 1;
						int puntos = 0;

						buffOut.clear();
						almacenarDatosNave(buffOut, vidas, bombas, puntos, nave2);
						almacenarElementos(buffOut);
						buffOut.flip();

						remoteChannelInOut.send(buffOut, sa);
					}
				}
				it.remove();
			}
		}
	}
}