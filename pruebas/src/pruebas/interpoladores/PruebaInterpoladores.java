/* 
 * PruebaInterpoladores.java
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

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.sam.interpoladores.*;

public class PruebaInterpoladores{

	@SuppressWarnings("serial")
	static class Resultados extends JPanel implements MouseListener{
		final int 	N_PUNTOS = 20;

		Object[] keysParams = new Object[]{1.0, 2.0};

		final double keys[];
		final double dvalues[];
		final double pMedios[];

		Getter.Double<Double> i1, i2, i3;
		Getter.Double<Double> i4, i5;
		Getter.Double<Double> i6, i7;

		Resultados(){
			keys = new double[N_PUNTOS];
			dvalues = new double[N_PUNTOS];
			pMedios = new double[N_PUNTOS-1];

			rellenar();
			this.addMouseListener(this);
		}

		private void rellenar(){
			keys[0] = 0.5/N_PUNTOS;
			dvalues[0]=(Math.random() - 0.5);

			for (int i = 1; i < N_PUNTOS; i++){
				keys[i] = keys[i-1]+ Math.max(0.05, Math.random());
				dvalues[i]=(Math.random() - 0.5)/2;
				pMedios[i-1] = 0.8 * Math.random() +0.1;
			}
			double scaleKeys = (N_PUNTOS-1)/(N_PUNTOS*(keys[N_PUNTOS-1]-keys[0]));
			for (int i = 1; i < N_PUNTOS; i++){
				keys[i]-=keys[0];
				keys[i]*=scaleKeys;
				keys[i]+=keys[0];
			}

			i1 = GettersFactory.Double.create(keys,dvalues,MetodoDeInterpolacion.Predefinido.ESCALON);
			i2 = GettersFactory.Double.create(keys,dvalues,MetodoDeInterpolacion.Predefinido.LINEAL);
			i3 = GettersFactory.Double.create(keys,dvalues,MetodoDeInterpolacion.Predefinido.COCIENTE_POLINOMICO_PUNTO_MEDIO, pMedios);
			i4 = GettersFactory.Double.create(keys,dvalues,MetodoDeInterpolacion.Predefinido.EXPONENCIAL_PUNTO_MEDIO, pMedios);
			i5 = GettersFactory.Double.create(keys,dvalues,MetodoDeInterpolacion.Predefinido.COSENOIDAL);
			i6 = GettersFactory.Double.create(keys,dvalues,MetodoDeInterpolacion.Predefinido.CARDINAL_SPLINE, 0.5f);
			i7 = GettersFactory.Double.create(keys,dvalues,MetodoDeInterpolacion.Predefinido.FINITE_DIFFERENCE_SPLINE);
		}

		@Override
		public void paintComponent(Graphics g){
			g.clearRect(0,0,getWidth(),getHeight());
			g.translate(0,getHeight()/2);

			g.setColor(Color.BLACK);
			g.drawLine(0,0,getWidth(),0);

			for(int i=0; i < N_PUNTOS; i++)
				g.fillRect((int)(keys[i]*getWidth()-2),-(int)(dvalues[i]*getHeight()+2),5,5);

			int v10 = (int)(i1.get(0)* getHeight()), v11;
			int v20 = (int)(i2.get(0)* getHeight()), v21;
			int v30 = (int)(i3.get(0)* getHeight()), v31;
			int v40 = (int)(i4.get(0)* getHeight()), v41;
			int v50 = (int)(i5.get(0)* getHeight()), v51;
			int v60 = (int)(i4.get(0)* getHeight()), v61;
			int v70 = (int)(i5.get(0)* getHeight()), v71;

			for(int x=0; x < getWidth(); x++){
				double key = (double)x/getWidth();
				
				v11 = (int)( i1.get(key)* getHeight() );
				v21 = (int)( i2.get(key)* getHeight() );
				v31 = (int)( i3.get(key)* getHeight() );
				v41 = (int)( i4.get(key)* getHeight() );
				v51 = (int)( i5.get(key)* getHeight() );
				v61 = (int)( i6.get(key)* getHeight() );
				v71 = (int)( i7.get(key)* getHeight() );

				g.setColor(Color.BLUE);  // Por Pasos
				g.drawLine(x,-v10,x,-v11);
				g.setColor(Color.GREEN); // Lineal
				g.drawLine(x,-v20,x,-v21);
				g.setColor(Color.RED); // COCIENTE_POLINOMICO_PUNTO_MEDIO
				g.drawLine(x,-v30,x,-v31);
				g.setColor(Color.MAGENTA.darker().darker()); // EXPONENCIAL_PUNTO_MEDIO
				g.drawLine(x,-v40,x,-v41);
				g.setColor(Color.YELLOW.darker().darker()); // COSENOIDAL
				g.drawLine(x,-v50,x,-v51);
				g.setColor(Color.ORANGE.darker().darker()); // CARDINAL_SPLINE
				g.drawLine(x,-v60,x,-v61);
				g.setColor(Color.PINK.darker().darker()); // FINITE_DIFFERENCE_SPLINE
				g.drawLine(x,-v70,x,-v71);

				v10 = v11;
				v20 = v21;
				v30 = v31;
				v40 = v41;
				v50 = v51;
				v60 = v61;
				v70 = v71;
			}
		}

		public void mouseClicked(MouseEvent e){
		}
		public void mousePressed(MouseEvent e){
		}
		public void mouseReleased(MouseEvent e){
			rellenar();
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

		JFrame frame = new JFrame("Prueba interpoladores");
		frame.setSize(800,600);
		frame.setDefaultCloseOperation(3);

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(resultados,BorderLayout.CENTER);
		frame.setVisible(true);
	}
}

