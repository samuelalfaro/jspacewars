package org.sam.jspacewars.servidor;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.media.opengl.GLCanvas;
import javax.swing.JFrame;

import org.sam.colisiones.ComprobadorDeColisones;
import org.sam.elementos.Cache;
import org.sam.jspacewars.cliente.ClienteTestColisiones;
import org.sam.jspacewars.serialization.Loader;
import org.sam.jspacewars.servidor.elementos.Disparo;
import org.sam.jspacewars.servidor.elementos.Elemento;
import org.sam.jspacewars.servidor.elementos.NaveEnemiga;
import org.sam.jspacewars.servidor.elementos.NaveUsuario;
import org.sam.jspacewars.servidor.elementos.SingletonObjetivos;

/**
 * Clase para testear las colisiones entre los distintos elementos que forman parte del juego, muestra por pantalla,
 * tanto los polígonos asociados a cada elemento como sus límites.
 */
public class TestColisiones {

	private transient Cache<Elemento> cache;
	private transient NaveUsuario nave;
	
	private transient Collection<Collection<? extends Elemento>> elementos;
	
	private transient Collection<Elemento> navesProtagonistas;
	private transient Collection<Collection<Disparo>> listasDeDisparosProtagonistas;
	private transient Collection<Elemento> navesEnemigas;
	private transient Collection<Disparo> disparosEnemigos;
	
	private transient SortedSet<Elemento> elementosOrdenados;
	
	private transient ComprobadorDeColisones comprobador;
	
	private TestColisiones() throws IOException {
		cache = new Cache<Elemento>(1000);
		Loader.loadData(cache);
		initData();
	}

	private void initData() {
		elementosOrdenados = new TreeSet<Elemento>(Elemento.COMPARADOR_POSICIONES);
		comprobador = new ComprobadorDeColisones(250);

		elementos = new LinkedList<Collection<? extends Elemento>>();
		
		navesProtagonistas = new LinkedList<Elemento>();
		elementos.add(navesProtagonistas);
		
		listasDeDisparosProtagonistas = new LinkedList<Collection<Disparo>>();
		
		Collection<Disparo> disparosNave;

		nave = (NaveUsuario) cache.newObject(0x03);
		disparosNave = new LinkedList<Disparo>();
		nave.setDstDisparos(disparosNave);
		listasDeDisparosProtagonistas.add(disparosNave);
		elementos.add(disparosNave);
		
		nave.iniciar();
		nave.setPosicion(-3, 0);
		
		float ratio = (30*4.0f/32) / (3.0f - 4*4.0f/32); // ratio 4/3 sin bordes GUI
		float h = 2.9f;
		float w = ratio * h;
		
		nave.setLimites(w, h);
		navesProtagonistas.add(nave);
		
		navesEnemigas = new LinkedList<Elemento>();
		elementos.add(navesEnemigas);
		
		disparosEnemigos = new LinkedList<Disparo>();
		elementos.add(disparosEnemigos);

		NaveEnemiga naveEnemiga = (NaveEnemiga)cache.newObject(0x30);
		naveEnemiga.setPosicion(3, -0.5f);
		SingletonObjetivos.setObjetivoUsuario(naveEnemiga);
		navesEnemigas.add(naveEnemiga);
		
		naveEnemiga = (NaveEnemiga)cache.newObject(0x10);
		naveEnemiga.setPosicion(0, -2.5f);
		navesEnemigas.add(naveEnemiga);
		
		naveEnemiga = (NaveEnemiga)cache.newObject(0x12);
		naveEnemiga.setPosicion(3, 2.5f);
		navesEnemigas.add(naveEnemiga);
	}

	private void calcularAcciones(long nanos) {
		
		elementosOrdenados.clear();
		for(Collection<Disparo> listaDisparo: listasDeDisparosProtagonistas)
			for(Disparo d: listaDisparo)
				elementosOrdenados.add(d);
		comprobador.comprobarColisiones(elementosOrdenados, navesEnemigas);
		
		for( Collection<? extends Elemento> listaElementos: elementos ){
			Iterator<? extends Elemento> iElemento = listaElementos.iterator();
			while( iElemento.hasNext() ){
				Elemento e = iElemento.next();
				if( e.getX() > 10 || e.getY() < -4.0 || e.getY() > 4.0 || e.isDestruido() ){
					iElemento.remove();
					cache.cached(e);
				}else
					e.actua(nanos);
			}
		}
	}

	private void atender(final ClienteTestColisiones cliente, long nanos){
		nave.setKeyState(cliente.getKeyState());
		calcularAcciones(nanos);

		cliente.setNVidas(2);
		cliente.setNBombas(1);
		cliente.setPuntos(0);
		cliente.setNivelesFijos( nave.getNivelesFijos() );
		cliente.setNivelesActuales( nave.getNivelesActuales() );
		cliente.setNivelesDisponibles( nave.getNivelesDisponibles() );
		cliente.setIndicador(nave.getAumentadoresDeNivel());
		cliente.setGrado(nave.getGradoNave());
		
		cliente.clearList();
		for( Collection<? extends Elemento> listaElementos: elementos )
			for( Elemento elemento : listaElementos)
				cliente.add(elemento);
	}
	
	/**
	 * Método principal que lanza el test de colisiones.
	 * @param args ignorados.
	 * @throws IOException Si se produce un error en la lectura del ficharo que contine los elementos del juego.
	 */
	public static void main(String... args) throws IOException{
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final GLCanvas canvas = new GLCanvas();
		final ClienteTestColisiones cliente = new ClienteTestColisiones( canvas );

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(canvas, BorderLayout.CENTER);

		final TestColisiones test = new TestColisiones();
		
		frame.setSize(800, 600);
		frame.setVisible(true);

		Thread animador = new Thread(){
			public void run(){
				long tAnterior = System.nanoTime();
				while(true){
					long tActual = System.nanoTime();
					test.atender( cliente, tActual-tAnterior );
					tAnterior = tActual;
					canvas.display();
				}
			}
		};
		animador.start();
		canvas.requestFocus();
	}
}