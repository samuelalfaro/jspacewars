package org.sam.jspacewars.servidor;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.channels.Pipe.SinkChannel;
import java.nio.channels.Pipe.SourceChannel;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

import org.sam.elementos.*;
import org.sam.jspacewars.elementos.*;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class ServidorJuego{
	
	private static final int TAM_DATAGRAMA = 8192;
	
	private static class ComprobadorDeColisones{
		
		private final Queue<Colisionable> colaConjuntoA;
		private final Queue<Colisionable> colaConjuntoB;
		
		private final Queue<Colisionable> colaActivosA;
		private final Queue<Colisionable> colaActivosB;
		
		ComprobadorDeColisones(int tamColas){
			colaConjuntoA = new ArrayBlockingQueue<Colisionable>(tamColas);
			colaConjuntoB = new ArrayBlockingQueue<Colisionable>(tamColas);
			colaActivosA  = new ArrayBlockingQueue<Colisionable>(tamColas);
			colaActivosB  = new ArrayBlockingQueue<Colisionable>(tamColas);
		}
		
		private int compararInicios(Colisionable c1, Colisionable c2){
			return 0;
			//return ((Elemento)c1).posX - ((Elemento)c2).posX;
		}
		
		private int compararInicioFin(Colisionable c1, Colisionable c2){
			return 0;
			//return ((Elemento)c1).posX - (((Elemento)c2).posX + 10);
		}
		
		public void comprobarColisiones(
				Collection<? extends Colisionable> unConjuntoA,
				Collection<? extends Colisionable> unConjuntoB
		) {
			if( unConjuntoA.size() == 0 || unConjuntoB.size() == 0 )
				return;

			Queue<Colisionable> conjuntoA = colaConjuntoA;
			conjuntoA.clear();
			conjuntoA.addAll(unConjuntoA);

			Queue<Colisionable> conjuntoB = colaConjuntoB;
			conjuntoB.clear();
			conjuntoB.addAll(unConjuntoB);

			Queue<Colisionable> activosA = colaActivosA;
			activosA.clear();
			Queue<Colisionable> activosB = colaActivosB;
			activosB.clear();

			do{
				if( conjuntoA.size() > 0 && (conjuntoB.size() == 0 || compararInicios(conjuntoA.peek(), conjuntoB.peek()) < 0) ){
					Colisionable actual = conjuntoA.poll();
					// Se eliminan los elementos activos que han salido cuando llega el nuevo
					while( activosA.size() > 0 && compararInicioFin(activosA.peek(), actual) > 0 )
						activosA.poll();
					while( activosB.size() > 0 && compararInicioFin(activosB.peek(), actual) > 0 )
						activosB.poll();
					activosA.offer(actual);
					for( Colisionable otro: activosB )
						if( actual.hayColision(otro) )
							actual.colisionar(otro);
				}else{
					Colisionable actual = conjuntoB.poll();
					// Se eliminan los elementos activos que han salido cuando llega el nuevo
					while( activosA.size() > 0 && compararInicioFin(activosA.peek(), actual) > 0 )
						activosA.poll();
					while( activosB.size() > 0 && compararInicioFin(activosB.peek(), actual) > 0 )
						activosB.poll();
					activosB.offer(actual);
					for( Colisionable otro: activosA )
						if( actual.hayColision(otro) )
							actual.colisionar(otro);
				}
			}while( conjuntoA.size() > 0 || conjuntoB.size() > 0 );
		}
	}
	
	public ServidorJuego() throws IOException {
		this(true, 0);
	}

	public ServidorJuego(int port) throws IOException {
		this(false, port);
	}
	
	private ServidorJuego(boolean localOnly, int port) throws IOException{
		loadData();
		initData(localOnly);
		initLocalChannel();
		if(!localOnly)
			initRemoteChannel(port);
	}
	
	private transient Cache<Elemento> cache;
	
	private void loadData() throws FileNotFoundException, IOException{
		
		cache = new Cache<Elemento>(1000);
		Canion.setCache(cache);
		
		XStream xStream = new XStream(new DomDriver());
		ElementosConverters.register(xStream);
		
		loadToCache(cache, xStream.createObjectInputStream(new FileReader("naves.xml")));
		loadToCache(cache, xStream.createObjectInputStream(new FileReader("disparos.xml")));
	}
	
	private static void loadToCache(Cache<Elemento> cache, ObjectInputStream in) throws IOException{
		try{
			while( true )
				cache.addPrototipo((Elemento) in.readObject());
		}catch( ClassNotFoundException e ){
			e.printStackTrace();
		}catch( EOFException eof ){
			in.close();
		}
	}
	
	private transient NaveUsuario nave1, nave2;
	private transient Collection <Nave> naves;
	private transient Collection <Collection <Disparo>> listasDeDisparos;
	
	private transient SortedSet <Elemento> elementosOrdenados;
	@SuppressWarnings("unused")
	private transient ComprobadorDeColisones comprobador;
	
	private void initData(boolean localOnly){
		
		elementosOrdenados = new TreeSet<Elemento>(Elemento.COMPARADOR);
		comprobador = new ComprobadorDeColisones(250);
		
		naves = new LinkedList<Nave>();
		listasDeDisparos = new LinkedList<Collection <Disparo>>();
		
		Collection <Disparo> disparosNave;
		
		nave1 = (NaveUsuario)cache.newObject(0x02);
		nave1.setId((short)1);
		disparosNave = new LinkedList<Disparo>();
		nave1.setDstDisparos(disparosNave);
		listasDeDisparos.add(disparosNave);
		nave1.iniciar();
		nave1.setPosicion(0, 0);
		nave1.setLimites(4.5f, 3);
		naves.add(nave1);
		
		if( !localOnly ){
			nave2 = (NaveUsuario)cache.newObject(0x01);
			nave2.setId((short)2);
			disparosNave = new LinkedList<Disparo>();
			nave2.setDstDisparos(disparosNave);
			listasDeDisparos.add(disparosNave);
			nave2.iniciar();
			nave2.setPosicion(0,-1);
			nave2.setLimites(4.5f, 3);
			naves.add(nave2);
		}
		
		NaveEnemiga bomber = (NaveEnemiga)cache.newObject(0x08);
		bomber.setPosicion(4,-2);
		SingletonEnemigos.setObjetivo(bomber);
		naves.add(bomber);
	}
	
	private transient Selector selector;
	
	private transient SourceChannel localChannelClientIn;
	
	public SourceChannel getLocalChannelClientIn() {
		return localChannelClientIn;
	}
	
	private transient SinkChannel	localChannelClientOut;
	
	public SinkChannel getLocalChannelClientOut() {
		return localChannelClientOut;
	}
	
	private transient SourceChannel localChannelServerIn;
	private transient SinkChannel 	localChannelServerOut;
	
	private transient DatagramChannel remoteChannelInOut;
	
	private void initLocalChannel() throws IOException{
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
	
	private void initRemoteChannel(int port) throws IOException{
		remoteChannelInOut = DatagramChannel.open();
		remoteChannelInOut.configureBlocking(false);
		remoteChannelInOut.socket().bind(new InetSocketAddress(port));

		remoteChannelInOut.register(selector, SelectionKey.OP_READ);
	}
	
	private void calcularAcciones(long nanos){
		for(Collection<Disparo> listaDisparo: listasDeDisparos){
			Iterator <Disparo> iDisparo = listaDisparo.iterator();
			while(iDisparo.hasNext()){
				Disparo d = iDisparo.next();
				if (d.getX() > 10 || d.getY() < -4.0 || d.getY() > 4.0  || d.finalizado()){
					iDisparo.remove();
					cache.cached(d);
				}else
					d.actua(nanos);
			}
		}
		for(Nave nave: naves)
			nave.actua(nanos);
//		iListaDisparos = listasDeDisparos.iterator();
//		while(iListaDisparos.hasNext())
//			comprobador.comprobarColisiones(naves,iListaDisparos.next());
	}

	private void almacenarDatosNave( ByteBuffer buff, int vidas, int bombas, int puntos, NaveUsuario nave ){
		buff.putInt(vidas);
		buff.putInt(bombas);
		buff.putInt(puntos);
		for(int n:nave.getNivelesFijos())
			buff.putInt(n);
		for(int n:nave.getNivelesActuales())
			buff.putInt(n);
		for(int n:nave.getNivelesDisponibles())
			buff.putInt(n);
		buff.putInt(nave.getAumentadoresDeNivel());
		buff.putInt(nave.getGradoNave());
	}
	
	private void almacenarElementos( ByteBuffer buff ){
		elementosOrdenados.clear();
		for(Elemento elemento: naves){
			//if (!d.isDestruido())
				elementosOrdenados.add(elemento);
		}
		for(Collection<Disparo> listaDisparos: listasDeDisparos)
			for(Elemento elemento: listaDisparos){
				//if (!d.isDestruido())
					elementosOrdenados.add(elemento);
			}
		buff.putInt(elementosOrdenados.size());
		for(Elemento elemento: elementosOrdenados)
			elemento.enviar(buff);
	}

	public void atenderClientes() throws IOException{
		
		ByteBuffer buffIn = ByteBuffer.allocateDirect(TAM_DATAGRAMA);
		ByteBuffer buffOut = ByteBuffer.allocateDirect(TAM_DATAGRAMA);
		
		long tActual = System.nanoTime();
		long tAnterior = tActual;
		
		if(nave2 == null){ // La nave2 no se ha inciado porque solo hay un jugador
			while (true) {
				selector.select();
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				if(!selectedKeys.isEmpty()){
					selectedKeys.clear();
					buffIn.clear();
					localChannelServerIn.read(buffIn);
					buffIn.flip();
					nave1.setKeyState(buffIn.getInt());

					tAnterior = tActual;
					tActual = System.nanoTime();
					calcularAcciones( tActual - tAnterior );
					
					
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
		while (true) {
			selector.select();
			Iterator<SelectionKey> it = selector.selectedKeys().iterator();
			while(it.hasNext()){
				SelectionKey key = it.next();
				if(key.channel() == localChannelServerIn){
					buffIn.clear();
					localChannelServerIn.read(buffIn);
					buffIn.flip();
					nave1.setKeyState(buffIn.getInt());

					tAnterior = tActual;
					tActual = System.nanoTime();
					calcularAcciones( tActual - tAnterior );
					
					int vidas = 2;
					int bombas = 1;
					int puntos = 0;
					
					buffOut.clear();
					almacenarDatosNave(buffOut, vidas, bombas, puntos, nave1);
					almacenarElementos(buffOut);
					buffOut.flip();

					localChannelServerOut.write(buffOut);
				}
				else if( key.channel() == remoteChannelInOut){
					buffIn.clear();
					SocketAddress sa = remoteChannelInOut.receive(buffIn);
					if (sa != null) {
						buffIn.flip();
						nave2.setKeyState(buffIn.getInt());

						tAnterior = tActual;
						tActual = System.nanoTime();
						calcularAcciones( tActual - tAnterior );

						int vidas = 2;
						int bombas = 1;
						int puntos = 0;
						
						buffOut.clear();
						almacenarDatosNave(buffOut, vidas, bombas, puntos, nave2);
						almacenarElementos(buffOut);
						buffOut.flip();
						
						remoteChannelInOut.send(buffOut,sa);
					}
				}
				it.remove();
			}
		}
	}
}