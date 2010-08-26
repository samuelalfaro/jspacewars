package org.sam.jspacewars.servidor;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.media.opengl.GLCanvas;
import javax.swing.JFrame;

import org.sam.elementos.Cache;
import org.sam.jspacewars.cliente.ClienteTestColisiones;
import org.sam.jspacewars.elementos.Disparo;
import org.sam.jspacewars.elementos.Elemento;
import org.sam.jspacewars.elementos.NaveEnemiga;
import org.sam.jspacewars.elementos.NaveUsuario;
import org.sam.jspacewars.elementos.SingletonEnemigos;
import org.sam.jspacewars.serialization.Loader;

public class TestColisiones {

	private transient Cache<Elemento> cache;
	private transient NaveUsuario nave;
	private transient Collection<Elemento> naves;
	private transient Collection<Collection<Disparo>> listasDeDisparos;

	private transient ComprobadorDeColisones comprobador;
	
	public TestColisiones() throws IOException {
		cache = new Cache<Elemento>(1000);
		Loader.loadData(cache);
		initData();
	}

	private void initData() {
		comprobador = new ComprobadorDeColisones(250);

		naves = new LinkedList<Elemento>();
		listasDeDisparos = new LinkedList<Collection<Disparo>>();

		Collection<Disparo> disparosNave;

		nave = (NaveUsuario) cache.newObject(0x03);
		disparosNave = new LinkedList<Disparo>();
		nave.setDstDisparos(disparosNave);
		listasDeDisparos.add(disparosNave);
		nave.iniciar();
		nave.setPosicion(0, 0);
		
		float ratio = (30*4.0f/32) / (3.0f - 4*4.0f/32); // ratio 4/3 sin bordes GUI
		float h = 2.9f;
		float w = ratio * h;
		
		nave.setLimites(w, h);
		naves.add(nave);

		NaveEnemiga bomber = (NaveEnemiga)cache.newObject(0x30);
		bomber.setPosicion(3, -1);
		SingletonEnemigos.setObjetivo(bomber);
		naves.add(bomber);
	}

	private void calcularAcciones(long nanos) {
		for( Collection<Disparo> listaDisparo: listasDeDisparos ){
			Iterator<Disparo> iDisparo = listaDisparo.iterator();
			while( iDisparo.hasNext() ){
				Disparo d = iDisparo.next();
				if( d.getX() > 10 || d.getY() < -4.0 || d.getY() > 4.0 || d.isDestruido() ){
					iDisparo.remove();
					cache.cached(d);
				}else
					d.actua(nanos);
			}
		}
		for( Elemento nave: naves )
			nave.actua(nanos);
		// iListaDisparos = listasDeDisparos.iterator();
		// while(iListaDisparos.hasNext())
		// comprobador.comprobarColisiones(naves,iListaDisparos.next());
	}

	public void atender(final ClienteTestColisiones cliente, long nanos){
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
		for( Elemento elemento: naves )
			cliente.add(elemento);

		for( Collection<Disparo> listaDisparos: listasDeDisparos )
			for( Elemento elemento: listaDisparos )
				cliente.add(elemento);
	}
	
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
	}
}