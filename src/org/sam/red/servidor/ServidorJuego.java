package org.sam.red.servidor;

import java.awt.BorderLayout;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.JFrame;

import org.sam.red.BufferBloqueante;
import org.sam.red.BufferCanal;
import org.sam.red.BufferSocket;
import org.sam.red.Colisionable;
import org.sam.red.Sincronizador;
import org.sam.red.cliente.VisorCliente;
import org.sam.util.Cache;

public class ServidorJuego{
	
	public static final int PORT = 1111;
	public static final int ANCHO = 400;
	public static final int ALTO = 400;
	
	private class ComprobadorDeColisones{
		
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
			return ((Elemento)c1).posX - ((Elemento)c2).posX;
		}
		
		private int compararInicioFin(Colisionable c1, Colisionable c2){
			return ((Elemento)c1).posX - (((Elemento)c2).posX + 10);
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
			}while( conjuntoA.size()>0 || conjuntoB.size()>0);
		}
	}
	
	private class HebraServidora extends Thread{
		BufferCanal bc;
		ByteBuffer buff;
		Nave nave;
		Collection <Nave> naves;
		Collection <Collection <Disparo>> listasDeDisparos;
		SortedSet <Disparo> disparos;
		ComprobadorDeColisones comprobador;

		public HebraServidora(BufferCanal bc, Nave nave, Collection <Nave> naves, Collection <Collection <Disparo>> listasDeDisparos){
			this.bc = bc;
			buff = bc.getByteBuffer();
			this.nave = nave;
			this.naves = naves;
			this.listasDeDisparos = listasDeDisparos;
			this.disparos = new TreeSet<Disparo>(new ComparadorDeElementos());
			this.comprobador = new ComprobadorDeColisones(250);
		}
		
		private void actua(){
			Iterator <Nave> iNave;
			Iterator <Collection <Disparo>> iListaDisparos;
			Iterator <Disparo> iDisparo;
			
			nave.setKeyState(buff.getInt());
			
			iNave = naves.iterator();
			while(iNave.hasNext())
				iNave.next().actua();

			iListaDisparos = listasDeDisparos.iterator();
			while(iListaDisparos.hasNext()){
				iDisparo = iListaDisparos.next().iterator();
				while(iDisparo.hasNext()){
					Disparo d = iDisparo.next();
					if (d.getX() > ANCHO || d.getY() < 0 || d.getY() > ALTO || d.isDestruido()){
						iDisparo.remove();
						cache.cached(d);
					}else
						d.actua();
				}
			}
			
			iListaDisparos = listasDeDisparos.iterator();
			while(iListaDisparos.hasNext())
				comprobador.comprobarColisiones(naves,iListaDisparos.next());
			
			
			disparos.clear();
			
			iListaDisparos = listasDeDisparos.iterator();
			while(iListaDisparos.hasNext()){
				iDisparo = iListaDisparos.next().iterator();
				while(iDisparo.hasNext()){
					Disparo d = iDisparo.next();
					if (!d.isDestruido())
						disparos.add(d);
				}
			}
			
			buff.rewind();
			
			buff.putInt(naves.size());
			iNave = naves.iterator();
			while(iNave.hasNext())
				iNave.next().enviar(buff);

			buff.putInt(disparos.size());
			iDisparo = disparos.iterator();
			while(iDisparo.hasNext())
				iDisparo.next().enviar(buff);

			bc.enviar();

		}
		
		public void run(){
			while(true){
				bc.recibir();
				actua();
			}
		}
	}
	
	private Cache<Elemento> cache;
	private HebraServidora server1, server2;
	private VisorCliente cliente;
	
	private void iniciar(){
		
		cache = new Cache<Elemento>(100);
		Nave.setCache(cache);
		cache.addPrototipo(new Nave(0x10));
		cache.addPrototipo(new Disparo(1));
		cache.addPrototipo(new Disparo(2));
		cache.addPrototipo(new Disparo(3));
		
		Collection <Nave> naves = new LinkedList<Nave>();
		Collection <Collection<Disparo>> listasDeDisparos = new LinkedList<Collection <Disparo>>();
		
		Nave nave;
		Collection <Disparo> disparos;
		
		nave = (Nave)cache.newObject(0x10);
		nave.setPosicion(ANCHO/2, ALTO/4);
		nave.setVelodidad(5);
		disparos = new LinkedList<Disparo>();
		nave.setPropiedadesDisparos(disparos,130);
		listasDeDisparos.add(disparos);
		naves.add(nave);
		
		Sincronizador sincronizador = new Sincronizador();
		server1 = new HebraServidora(new BufferBloqueante(sincronizador,0), nave, naves, listasDeDisparos);
		//TODO mirar
		//cliente =new VisorCliente(new BufferBloqueante(sincronizador,1));
		
		disparos = new LinkedList<Disparo>();
		nave = (Nave)cache.newObject(0x10);
		nave.setPosicion(ANCHO/2, 3*ALTO/4);
		nave.setVelodidad(5);
		nave.setPropiedadesDisparos(disparos,130);
		listasDeDisparos.add(disparos);
		naves.add(nave);
		try{
			server2 = new HebraServidora( new BufferSocket(PORT), nave, naves, listasDeDisparos);
		}catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	static public void main(String args[]){
		ServidorJuego me = new ServidorJuego();
		me.iniciar();
		
		JFrame frame = new JFrame("Servidor Juego");
		frame.setSize(ANCHO,ALTO);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(me.cliente.getPantalla(),BorderLayout.CENTER);
		frame.setVisible(true);
		
		me.server1.start();
		me.server2.start();
		me.cliente.start();
	}
}