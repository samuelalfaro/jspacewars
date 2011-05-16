/* 
 * Test03Segmentos.java
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
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

/**
 * Clase para testear las interseciones objetos {@code Segmento}.
 */
@SuppressWarnings("serial")
public final class Test03Segmentos extends Test00Abs{
	
	private Segmento[] segmentos;

	private Segmento segmento;
	
	private boolean puntoAsignado;
	
	private static void generaSegmentos(Segmento segmentos[], float x, float y, float r1, float r2, int offset){
		for(int i= 0; i < 12; i++){
			double a = (2*i + 1) * Math.PI*2 / 24;
			float cosA = (float)Math.cos(a);
			float sinA = (float)Math.sin(a);
			segmentos[i + offset] = new Segmento( r1 * cosA + x,  r1 * sinA + y , r2 * cosA + x,  r2 * sinA + y); 
		}
		for(int i = 12; i < 36; i++){
			int p = (i - 12) / 2;
			double a1 = (10*p + 7) * Math.PI*2 / 120;
			float cos1A = (float)Math.cos(a1);
			float sin1A = (float)Math.sin(a1);
			double a2 = (10*p + 13) * Math.PI*2 / 120;
			float cos2A = (float)Math.cos(a2);
			float sin2A = (float)Math.sin(a2);
			float r = (i % 2)*(r2-3*r1) + 2*r1;
			segmentos[i + offset] = new Segmento( r * cos1A + x,  r * sin1A + y, r * cos2A + x,  r * sin2A + y ); 
		}
	}
	
	private Test03Segmentos(){
		super(new Dimension(500,500));
		
		segmentos = new Segmento[148];
		segmentos[0] = new Segmento( 0.05f, 0.0f , 0.45f, 0.0f  );
		segmentos[1] = new Segmento(-0.05f, 0.0f ,-0.45f, 0.0f  );
		segmentos[2] = new Segmento( 0.0f , 0.05f, 0.0f , 0.45f );
		segmentos[3] = new Segmento( 0.0f ,-0.05f, 0.0f ,-0.45f );
		generaSegmentos(segmentos, 0.25f, 0.25f, 0.05f, 0.2f,   4 );
		generaSegmentos(segmentos, 0.25f,-0.25f, 0.05f, 0.2f,  40 );
		generaSegmentos(segmentos,-0.25f, 0.25f, 0.05f, 0.2f,  76 );
		generaSegmentos(segmentos,-0.25f,-0.25f, 0.05f, 0.2f, 112 );

		
		segmento = new Segmento();
		
		puntoAsignado = false;
	}
	
	public void paintComponent(Graphics g){
		g.setColor(Color.WHITE);
		g.clearRect(0,0,getWidth(),getHeight());
		
		boolean hayInterseccion = false;
		
		for(Segmento s: segmentos){
			if (segmento.hayInterseccion(s)){
				hayInterseccion = true;
				g.setColor(Color.MAGENTA);
			}else
				g.setColor(Color.BLACK);
			dibuja(g,s);
		}
		g.setColor(hayInterseccion ? Color.RED: Color.BLUE);
		dibuja(g,segmento);
	}

	public void mouseReleased(MouseEvent e) {
		if(!puntoAsignado){
			float x = xPantallaMundo(e.getX());
			float y = yPantallaMundo(e.getY());
			segmento.setPoints( x, y, x, y );
		}else{
			segmento.setPoint2( xPantallaMundo(e.getX()), yPantallaMundo(e.getY()) );
		}
		puntoAsignado = !puntoAsignado;
	}

	public void mouseMoved(MouseEvent e) {
		if(puntoAsignado){
			segmento.setPoint2( xPantallaMundo(e.getX()), yPantallaMundo(e.getY()) );
			repaint();
		}
	}
	
	/**
	 * Método principal encargado de lanzar este test.
	 * @param args ignorados.
	 */
	static public void main(String args[]){
		JFrame frame=new JFrame("Test 03: Interseccion Segmentos");
		frame.getContentPane().add(new Test03Segmentos());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}

