/* 
 * TestColisiones.java
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

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JFrame;

import org.sam.colisiones.ComprobadorDeColisones;
import org.sam.elementos.Cache;
import org.sam.jspacewars.cliente.PantallaTestColisiones;
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
public class TestColisiones implements Runnable{

	private final Cache<Elemento> cache;
	private final Collection<Collection<? extends Elemento>> elementos;

	private final Collection<Elemento> navesProtagonistas;
	private final Collection<Disparo>  disparosProtagonistas;
	private final Collection<Elemento> navesEnemigas;
	private final Collection<Disparo>  disparosEnemigos;
	
	private final SortedSet<Elemento> elementosOrdenados;
	private final ComprobadorDeColisones comprobador;
	
	private NaveUsuario nave;
	private final PantallaTestColisiones pantalla;
	
	private TestColisiones( PantallaTestColisiones pantalla ) throws IOException{
		
		cache = new Cache<Elemento>(1000);
		Loader.loadData(cache);
		
		elementos = new LinkedList<Collection<? extends Elemento>>();
		
		navesProtagonistas = new LinkedList<Elemento>();
		elementos.add(navesProtagonistas);
		
		disparosProtagonistas = new LinkedList<Disparo>();
		elementos.add(disparosProtagonistas);

		
		navesEnemigas = new LinkedList<Elemento>();
		elementos.add(navesEnemigas);
		
		disparosEnemigos = new LinkedList<Disparo>();
		elementos.add(disparosEnemigos);
		
		elementosOrdenados = new TreeSet<Elemento>(Elemento.COMPARADOR_POSICIONES);
		comprobador = new ComprobadorDeColisones(250);
		
		this.pantalla = pantalla;
		
		initData();
	}

	@SuppressWarnings( "deprecation" )
	private void initData() {
		
		nave = (NaveUsuario) cache.newObject(0x03);
		nave.setDstDisparos( disparosProtagonistas );
		nave.iniciar();
		nave.setPosicion( -3, 0 );
		float ratio = ( 30 * 4.0f / 32 ) / ( 3.0f - 4 * 4.0f / 32 ); // ratio 4/3 sin bordes GUI
		float h = 2.9f;
		float w = ratio * h;
		nave.setLimites(w, h);
		navesProtagonistas.add(nave);
		
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

	private void calcularAcciones( long nanos ){

		elementosOrdenados.clear();
		for( Disparo d: disparosProtagonistas )
			elementosOrdenados.add( d );
		comprobador.comprobarColisiones( elementosOrdenados, navesEnemigas );

		for( Collection<? extends Elemento> listaElementos: elementos ){
			Iterator<? extends Elemento> iElemento = listaElementos.iterator();
			while( iElemento.hasNext() ){
				Elemento e = iElemento.next();
				if( e.getX() > 10 || e.getY() < -4.0 || e.getY() > 4.0 || e.isDestruido() ){
					iElemento.remove();
					cache.cached( e );
				}else
					e.actua( nanos );
			}
		}
	}

	public void run(){
		
		long tAnterior = System.nanoTime();
		
		while(true){
			long tActual = System.nanoTime();

			nave.setKeyState( pantalla.getKeyState() );
			calcularAcciones( tActual - tAnterior );

			pantalla.setNVidas( 2 );
			pantalla.setNBombas( 1 );
			pantalla.setPuntos( 0 );
			pantalla.setNivelesFijos( nave.getNivelesFijos() );
			pantalla.setNivelesActuales( nave.getNivelesActuales() );
			pantalla.setNivelesDisponibles( nave.getNivelesDisponibles() );
			pantalla.setIndicador( nave.getAumentadoresDeNivel() );
			pantalla.setGrado( nave.getGradoNave() );

			pantalla.clearList();
			for( Collection<? extends Elemento> listaElementos: elementos )
				for( Elemento elemento: listaElementos )
					pantalla.add( elemento );
			tAnterior = tActual;

			pantalla.display();
		}
	}
	
	/**
	 * Método principal que lanza el test de colisiones.
	 * @param args ignorados.
	 * @throws IOException Si se produce un error en la lectura del fichero que contine los elementos del juego.
	 */
	public static void main(String... args) throws IOException{
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

		final PantallaTestColisiones pantalla = new PantallaTestColisiones();

		frame.getContentPane().setLayout( new BorderLayout() );
		frame.getContentPane().add( pantalla, BorderLayout.CENTER );

		final TestColisiones test = new TestColisiones( pantalla );

		frame.setSize( 800, 600 );
		frame.setVisible( true );

		Thread animador = new Thread( test );
		animador.start();
		pantalla.requestFocus();
	}
}
