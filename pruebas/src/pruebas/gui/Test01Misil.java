/* 
 * Test01Misil.java
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
package pruebas.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Test01Misil extends JPanel implements MouseListener, MouseMotionListener{
	private static final long serialVersionUID = 1L;
	
	class Animador extends Thread{
		public void run(){
			while(true){
				actualizar();
				repaint();
				try{
					Thread.sleep(40);
				}catch(InterruptedException e){
				}
			}
		}
	}
	
	static class formaMisil{
		static int[] coordX = {-20,-20,-15,15,20,15,-15,-20};
		static int[] coordY = {0,10,5,5,0,-5,-5,-10};
		static int[] pX = new int[8];
		static int[] pY = new int[8];
	}
	
	class Misil{
		double x, y, ang, vel;
		boolean parado;
		boolean objetivoAlcanzado;
		
		double ox, oy;
		double vx, vy;
		
		Misil(){
			parado = true;
			objetivoAlcanzado = false;
			ang = 0;
			vel = 50;
			setObjetivo(300,300);
			vx = vel * Math.cos(ang);
			vy = vel * Math.sin(ang);
		}

		Misil(double x, double y){
			parado = true;
			objetivoAlcanzado = false;
			this.x = x;
			this.y = y; 
			ang = 0;
			vel = 6;
			setObjetivo(300,300);
			vx = vel * Math.cos(ang);
			vy = vel * Math.sin(ang);
		}
		
		void setObjetivo(double x,double y){
			ox = x;
			oy = y; 
		}
		
		int signo(double d){
			return d<0?-1:1;
		}	

		double diferenciaAngulos(double a1, double a2){
			double r = a2 - a1;
			if(Math.abs(r)>Math.PI)
				r = signo(r)*(Math.abs(r) - 2*Math.PI);
			return r;
		}	

		void reorientar(){
			double angOb = Math.atan2( (oy -y),(ox-x));
			double dif = diferenciaAngulos(ang,angOb);
			if(Math.abs(dif) < 0.1 ){
				ang = angOb;
				return; 
			}
			double inc = 0.1*signo(dif);
			ang += inc;
		}
		
		int cont = 0;
		
		void avanzar(){
			if(Math.sqrt((oy -y)*(oy -y)+(ox-x)*(ox-x)) < 5){
				objetivoAlcanzado = true;
				return;
			}
			reorientar();
			vx = vel * Math.cos(ang);
			vy = vel * Math.sin(ang);
			
			x += vx;
			y += vy;
			if(cont++==5){
				if (vel++>50) 
					this.objetivoAlcanzado = true;
				cont = 0;
			}
		}
		
		void paint(Graphics g){
			double cosAlfa = Math.cos(ang);
			double senAlfa = Math.sin(ang);
			for(int i = 0; i < 8; i++){
				double x_aux = formaMisil.coordX[i];
				double y_aux = formaMisil.coordY[i];
				formaMisil.pX[i] = (int)(x_aux * cosAlfa - y_aux * senAlfa + x);
				formaMisil.pY[i] = (int)(x_aux * senAlfa + y_aux * cosAlfa + y);
			}
			Graphics2D g2 = (Graphics2D)g;
			Paint gp = new GradientPaint(formaMisil.pX[0],formaMisil.pY[0],Color.YELLOW,formaMisil.pX[1],formaMisil.pY[1],Color.BLACK,true);			
			g2.setPaint(gp);
			g2.fillPolygon(formaMisil.pX,formaMisil.pY,8);
			g2.setColor(Color.BLACK);
			g2.drawPolygon(formaMisil.pX,formaMisil.pY,8);
			g2.drawLine((int)x,(int)y,(int)ox,(int)oy);
		}
	}
	
	Misil misiles[];
	int xO, iO;
	
	Test01Misil(){
		xO = 300;
 		iO = 4; 
		misiles = new Misil[50];
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	private Animador getAnimador(){
		return new Animador();
	}
	
	static public void main(String args[]){
		// Se crea un frame, que va a contener nuestro panel
		JFrame frame=new JFrame();
		frame.setBounds(100, 100, 600, 600);
		frame.setTitle("Test 01: Misiles");
		frame.setDefaultCloseOperation(3);
		frame.setResizable(false);

		// Creamos el panel donde dibujaremos
		Test01Misil panel=new Test01Misil();

		// Lo a�adimos a la ventana
		frame.getContentPane().add(panel);
		
		// Mostramos la ventana
		frame.setVisible(true);
				
		Animador animador = panel.getAnimador();
		animador.start();

	}
	
	void actualizar(){
		xO += iO;
		if (xO == 500)
			iO = -iO;
		if (xO == 100)
			iO = -iO;
		
		for(int i = 0; i< 50; i++){
			if (misiles[i]!= null){
				if (misiles[i].objetivoAlcanzado)
					misiles[i]= null;
				else{
					misiles[i].setObjetivo(xO,300);
					misiles[i].avanzar();
				}
			}
		}
	}

	public void paint(Graphics g){
		g.setColor(Color.WHITE);
		g.clearRect(0,0,getWidth(),getHeight());

		g.setColor(Color.BLACK);
		for(int i = 0; i< 50; i++){
			if (misiles[i]!= null)
				misiles[i].paint(g);
		}
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
		for(int i = 0; i< 50; i++){
			if (misiles[i]== null){
				misiles[i] = new Misil(e.getX(),e.getY());
				return;
			}
		}
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}
}

