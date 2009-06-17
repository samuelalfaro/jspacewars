package org.sam.red.servidor;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.JFrame;

import org.sam.elementos.*;
import org.sam.gui.Visor3D;
import org.sam.red.Colisionable;
import org.sam.util.Cache;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class ServidorJuego{
	
	private static final int TAM_DATAGRAMA = 8192;
	public static final int PORT = 1111;
	
	public static final int ANCHO = 640;	// TODO Borrar es por comodidad
	public static final int ALTO = 480;		// TODO Borrar es por comodidad
	
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
		
		public void comprobarColisiones(Collection<? extends Colisionable> unConjuntoA, Collection<? extends Colisionable> unConjuntoB){
			if(unConjuntoA.size() == 0 || unConjuntoB.size() == 0)
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
				if( conjuntoA.size()>0 && ( conjuntoB.size()==0 || compararInicios(conjuntoA.peek(),conjuntoB.peek() ) < 0 ) ){
					Colisionable actual = conjuntoA.poll();
					// Se eliminan los elementos activos que han salido cuando llega el nuevo
					while( activosA.size()>0 && compararInicioFin(activosA.peek(),actual)>0)
						activosA.poll();
					while( activosB.size()>0 && compararInicioFin(activosB.peek(),actual)>0)
						activosB.poll();
					activosA.offer(actual);
					Iterator<Colisionable> i = activosB.iterator();
					while(i.hasNext()){
						Colisionable otro = i.next();
						if (actual.hayColision(otro)){
							actual.colisionar(otro);
						}
					}
				}else{
					Colisionable actual = conjuntoB.poll();
					// Se eliminan los elementos activos que han salido cuando llega el nuevo
					while( activosA.size() >0 && compararInicioFin(activosA.peek(),actual)>0 )
						activosA.poll();
					while( activosB.size() >0 && compararInicioFin(activosB.peek(),actual)>0 )
						activosB.poll();
					activosB.offer(actual);
					Iterator<Colisionable> i = activosA.iterator();
					while(i.hasNext()){
						Colisionable otro = i.next();
						if (actual.hayColision(otro)){
							actual.colisionar(otro);
						}
					}
				}
			}while( conjuntoA.size() > 0 || conjuntoB.size() > 0 );
		}
	}
	
	private Cache<Elemento> cache;
	private Collection <Nave> naves;
	private Collection <Collection <Disparo>> listasDeDisparos;
	private SortedSet <Elemento> elementosOrdenados;
	@SuppressWarnings("unused")
	private ComprobadorDeColisones comprobador;
	private NaveUsuario nave1, nave2;
	
	private ServidorJuego() throws IOException{
		loadData();
		initChannels();
	}
	
	private static void loadToCache(Cache<Elemento> cache, ObjectInputStream in) throws IOException{
		try{
			while( true )
				cache.addPrototipo((Elemento) in.readObject());
		}catch( ClassNotFoundException e ){
			e.printStackTrace();
		}catch( EOFException eof ){
		}
	}
	
	private void loadData(){
		
		cache = new Cache<Elemento>(500);
		Canion.setCache(cache);
		
		elementosOrdenados = new TreeSet<Elemento>(Elemento.COMPARADOR);
		comprobador = new ComprobadorDeColisones(250);
		
		XStream xStream = new XStream(new DomDriver());
		ElementosConverters.register(xStream);
		
		try{
			loadToCache(cache, xStream.createObjectInputStream(new FileReader("naves.xml")));
			loadToCache(cache, xStream.createObjectInputStream(new FileReader("disparos.xml")));
		}catch( FileNotFoundException e ){
			e.printStackTrace();
		}catch( IOException e ){
			e.printStackTrace();
		}
		
		naves = new LinkedList<Nave>();
		listasDeDisparos = new LinkedList<Collection <Disparo>>();
		
		Collection <Disparo> disparosNave;
		
		nave1 = (NaveUsuario)cache.newObject(0x00);
		nave1.setId((short)1);
		disparosNave = new LinkedList<Disparo>();
		nave1.setDstDisparos(disparosNave);
		listasDeDisparos.add(disparosNave);
		nave1.iniciar();
		nave1.setPosicion(0, 0);
		nave1.setLimitesPantalla(4.5f, 3);
		naves.add(nave1);
		
		nave2 = (NaveUsuario)cache.newObject(0x01);
		nave2.setId((short)2);
		disparosNave = new LinkedList<Disparo>();
		nave2.setDstDisparos(disparosNave);
		listasDeDisparos.add(disparosNave);
		nave2.iniciar();
		nave2.setPosicion(0,-1);
		nave2.setLimitesPantalla(4.5f, 3);
		naves.add(nave2);
		
		NaveEnemiga bomber = (NaveEnemiga)cache.newObject(0x08);
		bomber.setPosicion(4,-2);
		SingletonEnemigos.setObjetivo(bomber);
		naves.add(bomber);
	}
	
	Pipe pipeServer;
	Pipe pipeClient;
	
	Selector selector;
	
	Pipe.SourceChannel pipeIn;
	Pipe.SinkChannel pipeOut;
	
	DatagramChannel canalServer;
	
	private void initChannels() throws IOException{
		pipeServer = Pipe.open();
		pipeClient = Pipe.open();
		
		selector = Selector.open();
		
		pipeIn = pipeClient.source();
		pipeIn.configureBlocking(false);
		pipeOut = pipeServer.sink();
		
		pipeIn.register(selector, SelectionKey.OP_READ);
		
		canalServer = DatagramChannel.open();
		canalServer.configureBlocking(false);
		canalServer.socket().bind(new InetSocketAddress(PORT));

		canalServer.register(selector, SelectionKey.OP_READ);
	}
	
	private void calcularAcciones(long milis){
			
			for(Collection<Disparo> listaDisparo: listasDeDisparos){
				Iterator <Disparo> iDisparo = listaDisparo.iterator();
				while(iDisparo.hasNext()){
					Disparo d = iDisparo.next();
					//if (d.getX() > ANCHO || d.getY() < 0 || d.getY() > ALTO || d.isDestruido()){
					if (d.getX() > 10 || d.getY() < -4.0 || d.getY() > 4.0  || d.finalizado()){
						iDisparo.remove();
						cache.cached(d);
					}else
						d.actua(milis);
				}
			}
			
			for(Nave nave: naves)
				nave.actua(milis);
			
	//		iListaDisparos = listasDeDisparos.iterator();
	//		while(iListaDisparos.hasNext())
	//			comprobador.comprobarColisiones(naves,iListaDisparos.next());
		}

	private void almacenarElementos( ByteBuffer buff ){
		buff.clear();
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
		
		buff.flip();
	}

	private void atenderClientes() throws IOException{
		
		ByteBuffer buffIn = ByteBuffer.allocateDirect(TAM_DATAGRAMA);
		ByteBuffer buffOut = ByteBuffer.allocateDirect(TAM_DATAGRAMA);
		
		long tActual = System.nanoTime();
		long tAnterior = tActual;
		
		while (true) {
			selector.select();
			Iterator<SelectionKey> it = selector.selectedKeys().iterator();
			while(it.hasNext()){
				SelectionKey key = it.next();
				if(key.channel() == pipeIn){
					buffIn.clear();
					pipeIn.read(buffIn);
					buffIn.flip();
					nave1.setKeyState(buffIn.getInt());
					
					tAnterior = tActual;
					tActual = System.nanoTime();
					calcularAcciones( tActual - tAnterior );
					almacenarElementos(buffOut);

					pipeOut.write(buffOut);
				}
				else if( key.channel() == canalServer){
					buffIn.clear();
					SocketAddress sa = canalServer.receive(buffIn);
					if (sa != null) {
						buffIn.flip();
						nave2.setKeyState(buffIn.getInt());
						
						tAnterior = tActual;
						tActual = System.nanoTime();
						calcularAcciones( tActual - tAnterior );
						
						almacenarElementos(buffOut);
						canalServer.send(buffOut,sa);
					}
				}
				it.remove();
			}
		}
	}
	
	static public void main(String args[]){
		try{
			ServidorJuego me = new ServidorJuego();
			Visor3D visor = new Visor3D( me.pipeServer.source(), me.pipeClient.sink() );
			
			JFrame frame = new JFrame("Servidor Juego");
			frame.setSize(ServidorJuego.ANCHO, ServidorJuego.ALTO);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			frame.setContentPane(visor.getPanel());
			frame.setVisible(true);
			visor.start();

			me.atenderClientes();

		}catch( IOException e ){
			e.printStackTrace();
		}
	}
}