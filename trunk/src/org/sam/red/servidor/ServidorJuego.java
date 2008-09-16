package org.sam.red.servidor;

import java.awt.BorderLayout;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Pipe;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.JFrame;

import org.sam.elementos.Canion;
import org.sam.elementos.CanionInterpolado;
import org.sam.elementos.CanionLineal;
import org.sam.elementos.Disparo;
import org.sam.elementos.Elemento;
import org.sam.elementos.LanzaMisiles;
import org.sam.elementos.Nave;
import org.sam.elementos.NaveUsuario;
import org.sam.gui.Marco;
import org.sam.interpoladores.XStreamConventers;
import org.sam.red.Colisionable;
import org.sam.red.cliente.VisorCliente3D;
import org.sam.util.Cache;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class ServidorJuego{
	
	private static final int TAM_DATAGRAMA = 8192;
	public static final int PORT = 1111;
	public static final int ANCHO = 600;
	public static final int ALTO = 400;
	
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
	private SortedSet <Disparo> disparosOrdenados;
	@SuppressWarnings("unused")
	private ComprobadorDeColisones comprobador;

	private VisorCliente3D cliente;
	
	
	private void actua(long milis, ByteBuffer buff){
		
		for(Nave nave: naves)
			nave.actua(milis);

		for(Collection<Disparo> listaDisparo: listasDeDisparos){
			Iterator <Disparo> iDisparo = listaDisparo.iterator();
			while(iDisparo.hasNext()){
				Disparo d = iDisparo.next();
				//if (d.getX() > ANCHO || d.getY() < 0 || d.getY() > ALTO || d.isDestruido()){
				if (d.getX() > 10 || d.getY() < -4.0 || d.getY() > 4.0 ){
					iDisparo.remove();
					cache.cached(d);
				}else
					d.actua(milis);
			}
		}
		
//		iListaDisparos = listasDeDisparos.iterator();
//		while(iListaDisparos.hasNext())
//			comprobador.comprobarColisiones(naves,iListaDisparos.next());
		
		disparosOrdenados.clear();
		for(Collection<Disparo> listaDisparos: listasDeDisparos)
			for(Disparo disparo: listaDisparos){
				//if (!d.isDestruido())
					disparosOrdenados.add(disparo);
			}

		buff.clear();
		
		buff.putInt(naves.size());
		for(Nave nave: naves)
			nave.enviar(buff);

		buff.putInt(disparosOrdenados.size());
		for(Disparo disparo: disparosOrdenados)
			disparo.enviar(buff);
		
		buff.flip();
	}
	
	private void iniciar() throws Exception{
		
		Pipe pipeServer;
		Pipe pipeClient;
	
		pipeServer = Pipe.open();
		pipeClient = Pipe.open();
		cliente = new VisorCliente3D( pipeServer.source(), pipeClient.sink() );

		JFrame frame = new JFrame("Servidor Juego");
		frame.setSize(ANCHO,ALTO);
		//frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setContentPane(Marco.getPanel());
		frame.getContentPane().add(cliente.getPantalla(),BorderLayout.CENTER);
		frame.setVisible(true);
			
		cliente.start();

		cache = new Cache<Elemento>(500);
		Canion.setCache(cache);
		
		XStream xstream = new XStream(new DomDriver());
		XStreamConventers.registerConverters(xstream);
		
		xstream.alias("NaveUsuario", NaveUsuario.class);
		xstream.alias("Canion", Canion.class);
		xstream.alias("CanionLineal", CanionLineal.class);
		xstream.alias("CanionInterpolado", CanionInterpolado.class);
		xstream.alias("LanzaMisiles", LanzaMisiles.class);
		
		try {
			cache.addPrototipo((NaveUsuario)xstream.fromXML(new FileReader("Nave.xml")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			ObjectInputStream in;
			in = xstream.createObjectInputStream(new FileReader("disparos.xml"));
			try {
				while(true){
					cache.addPrototipo((Elemento)in.readObject());
				}
			} catch (java.io.EOFException eof) {
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		naves = new LinkedList<Nave>();
		listasDeDisparos = new LinkedList<Collection <Disparo>>();
		
		Collection <Disparo> disparosNave;
		
		NaveUsuario nave1 = (NaveUsuario)cache.newObject(0x100);
		disparosNave = new LinkedList<Disparo>();
		nave1.setDstDisparos(disparosNave);
		listasDeDisparos.add(disparosNave);
		nave1.iniciar();
		nave1.setPosicion(0, 0);
		nave1.setLimitesPantalla(4.5f, 3);
		naves.add(nave1);
		
		NaveUsuario nave2 = (NaveUsuario)cache.newObject(0x100);
		disparosNave = new LinkedList<Disparo>();
		nave2.setDstDisparos(disparosNave);
		listasDeDisparos.add(disparosNave);
		nave2.iniciar();
		nave2.setPosicion(3,-3);
		nave2.setLimitesPantalla(4.5f, 3);
		naves.add(nave2);
		
		
		DatagramChannel canalServer = DatagramChannel.open();
		canalServer.configureBlocking(false);
		canalServer.socket().bind(new InetSocketAddress(PORT));

		Pipe.SourceChannel pipeIn = pipeClient.source();
		pipeIn.configureBlocking(false);
		Pipe.SinkChannel pipeOut = pipeServer.sink();

		Selector selector = Selector.open();
		canalServer.register(selector, SelectionKey.OP_READ);
		pipeIn.register(selector, SelectionKey.OP_READ);

		ByteBuffer buffIn = ByteBuffer.allocateDirect(TAM_DATAGRAMA);
		ByteBuffer buffOut = ByteBuffer.allocateDirect(TAM_DATAGRAMA);
		
		disparosOrdenados = new TreeSet<Disparo>(Elemento.COMPARADOR);
		comprobador = new ComprobadorDeColisones(250);

		long tActual = System.currentTimeMillis();
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
					tActual = System.currentTimeMillis();
					actua( tActual - tAnterior, buffOut );

					pipeOut.write(buffOut);
				}
				else if( key.channel() == canalServer){
					buffIn.clear();
					SocketAddress sa = canalServer.receive(buffIn);
					if (sa != null) {
						buffIn.flip();
						nave2.setKeyState(buffIn.getInt());
						
						tAnterior = tActual;
						tActual = System.currentTimeMillis();
						actua( tActual - tAnterior, buffOut );
						canalServer.send(buffOut,sa);
					}
				}
				it.remove();
			}
		}
	}
	
	static public void main(String args[]){
		ServidorJuego me = new ServidorJuego();
		try{
			me.iniciar();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}