/* 
 * Test02Misil.java
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

import pruebas.util.FastMath;

@SuppressWarnings({ "serial", "deprecation" })
public class Test02Misil extends JPanel implements MouseListener, MouseMotionListener{
	
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
		float x, y, vel;
		float ang;
		boolean objetivoAlcanzado;
		
		float ox, oy;
		float vx, vy;
		
		Misil(float x, float y){
			objetivoAlcanzado = false;
			this.x = x;
			this.y = y; 
			ang = 0;
			vel = 10;
			setObjetivo(300,300);
			vx = vel * FastMath.cos240(ang);
			vy = vel * FastMath.sin240(ang);
		}
		
		void setObjetivo(float x,float y){
			ox = x;
			oy = y; 
		}
		
		static final float iAng = 3;
		
		int c = 0;
		
		void reorientar(){
			// radio * sen(a/2) = v/2 ---> radio = v/2·sen(a/2) --> diametro = v/sen(a/2)
			float diam = vel/FastMath.sin240(iAng/2);
			float dist =  FastMath.len( oy -y, ox-x);
			
			float angOb = FastMath.atan( oy -y, ox-x );

			float dif = Math.abs(angOb - ang);
			float absDif =  (dif > 120) ? 240 - dif: dif;
			
			if( absDif < iAng ){
				if( dist < vel){
					objetivoAlcanzado = true;
					return;
				}
				ang = angOb;
				vel+=2;
				return; 
			}
			
			if( (dist > diam) || (dist/vel > absDif/iAng) ){
				if ( (ang > angOb) ^ (dif > 120) ) {
					ang -= iAng;
					if(ang < 0)
						ang += 240;
				}else{
					ang += iAng;
					if(ang  > 240)
						ang -= 240;
				}
				return;
			}

			if (vel > 10 && ++c == 5){
				c = 0;
				vel--;
			}
			if( absDif + iAng > 120 ){
				ang = angOb > 120 ? angOb - 120: angOb + 120;
				return; 
			}
			if ( (ang > angOb) ^ (dif > 120) ) {
				ang += iAng;
				if(ang > 240)
					ang -= 240;
			}else{
				ang -= iAng;
				if(ang  < 0)
					ang += 240;
			}
		}
		
		int cont = 0;
		void avanzar(){
			reorientar();
			vx = vel * FastMath.cos240(ang);
			vy = vel * FastMath.sin240(ang);
			
			x += vx;
			y += vy;
		}
		
		void paint(Graphics g){
			double cosAlfa = FastMath.cos240(ang);
			double senAlfa = FastMath.sin240(ang);
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
	
	Test02Misil(){
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
		frame.setTitle("Test 02: Misiles");
		frame.setDefaultCloseOperation(3);
		frame.setResizable(false);

		// Creamos el panel donde dibujaremos
		Test02Misil panel=new Test02Misil();

		// Lo añadimos a la ventana
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
				if (misiles[i].objetivoAlcanzado){
					misiles[i]= null;
					continue;
				}
				misiles[i].setObjetivo(xO,300);
				misiles[i].avanzar();
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

