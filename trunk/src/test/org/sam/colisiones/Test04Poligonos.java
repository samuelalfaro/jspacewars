/* 
 * Test04Poligonos.java
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
package org.sam.colisiones;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;

/**
 * Clase para testear si un {@code Poligono} contiene distintos puntos.
 */
@SuppressWarnings( "serial" )
public final class Test04Poligonos extends Test00Abs{

	
	private static final int N_LADOS = 20;
	private Poligono poligono;

	private Test04Poligonos(){
		super( new Dimension( 500, 500 ) );
		this.poligono = crearPoligono( N_LADOS );
		new AnimadorPoligono( this, poligono ).start();
	}

	private static final transient Color ColorOut = Color.LIGHT_GRAY;
	private static final transient Color ColorIn1 = Color.GREEN.darker();
	private static final transient Color ColorIn2 = Color.RED;
	
	public void paintComponent( Graphics g ){
		g.setColor( Color.WHITE );
		g.clearRect( 0, 0, getWidth(), getHeight() );

		LimiteRectangular rectangulo = poligono.getLimites();
		for( float x = -0.45f; x < 0.5f; x += 0.05f )
			for( float y = -0.45f; y < 0.5f; y += 0.05f ){
				if( !rectangulo.contiene( x, y ) ){
					g.setColor( ColorOut );
					dibuja( g, x, y, 3 );
				}else if( !poligono.contiene( x, y ) ){
					g.setColor( ColorIn1 );
					dibuja( g, x, y, 3 );
				}else{
					g.setColor( ColorIn2 );
					dibuja( g, x, y, 5 );
				}
			}

		g.setColor( Color.LIGHT_GRAY );
		dibuja( g, rectangulo );

		g.setColor( Color.BLACK );
		dibuja( g, poligono );
	}

	/**
	 * Método principal encargado de lanzar este test.
	 * @param args ignorados.
	 */
	static public void main( String args[] ){
		JFrame frame = new JFrame( "Test 04: Poligono de " + N_LADOS + " lados" );
		frame.getContentPane().add( new Test04Poligonos() );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.pack();
		frame.setResizable( false );
		frame.setLocationRelativeTo( null );
		frame.setVisible( true );
	}
}
