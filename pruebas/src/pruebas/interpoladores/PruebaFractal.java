/* 
 * PruebaFractal.java
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
package pruebas.interpoladores;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.sam.interpoladores.GettersFactory;
import org.sam.interpoladores.Getter;
import org.sam.interpoladores.MetodoDeInterpolacion;

public class PruebaFractal{

	@SuppressWarnings("serial")
	static class Resultados extends JPanel implements MouseListener{
	
		final int 	N_PUNTOS = 10;
		final float keys[];
		final float values[];
		
		Getter.Float<Float> interpolador;

		Resultados(){

			keys = new float[N_PUNTOS];
			values = new float[N_PUNTOS];

			rellenar(800,600);
			this.addMouseListener(this);
		}

		/**
		 * @param w ignorado.
		 */
		private void rellenar(int w, int h){
			float ik = 1.0f/(N_PUNTOS-1);
			keys[0] = 0.0f;
			values[0] = (float)((Math.random() - 0.5)*h)/2;
			for (int i = 1, len=N_PUNTOS-1; i < len; i++){
				keys[i] = keys[i-1]+ik;
				values[i] = (float)((Math.random() - 0.5)*h)/2;
			}
			keys[N_PUNTOS-1] = 1.0f;
			values[N_PUNTOS-1] = values[0];

			interpolador = GettersFactory.Float.create(keys, values, MetodoDeInterpolacion.Predefinido.CATMULL_ROM_SPLINE);
		}
		
		public void paintComponent(Graphics g){
			g.clearRect(0,0,getWidth(),getHeight());
			g.translate(0,getHeight()/2);

			g.setColor(Color.BLACK);
			g.drawLine(0,0,getWidth(),0);

			
			float valoresAnteriores = interpolador.get(0);
			float valoresActuales;
	 
			for(int x=-1,w=getWidth(); x<w; x++){
				valoresActuales = interpolador.get((float)((x*2)%w)/w);
				int scale = 4;
				for(int i= 0; i < 8; i++){
					valoresActuales += interpolador.get((float)((x*scale)%w)/w)/scale;
					scale *= 2;
				}
				g.drawLine(x,-((int)valoresAnteriores),x,-((int)valoresActuales));
				valoresAnteriores = valoresActuales;
			}
		}

		public void mouseClicked(MouseEvent e){
		}
		public void mousePressed(MouseEvent e){
		}
		public void mouseReleased(MouseEvent e){
			rellenar(getWidth(), getHeight());
			repaint();
		}
		public void mouseEntered(MouseEvent e){
		}
		public void mouseExited(MouseEvent e){
		}
	}

	static public void main(String args[]){
		JPanel	resultados;

		resultados = new Resultados();
		
		JFrame frame = new JFrame("Prueba Fractal");
		frame.setSize(800,600);
		frame.setDefaultCloseOperation(3);
		
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(resultados,BorderLayout.CENTER);
		frame.setVisible(true);
	}
}

