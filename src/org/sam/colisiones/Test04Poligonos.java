package org.sam.colisiones;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Test04Poligonos extends PantallaTest{
	static final int N_LADOS = 20;
	Poligono poligono;
	
	class Animador extends Thread{
		public void run(){
			float alfa = 0.0f;
			float incAlfa = 0.005f;
			float pi2 = (float)(Math.PI * 2);
			while(true){
				alfa += incAlfa;
				if(alfa >1){
					alfa = 1;
					incAlfa = -incAlfa;
				}else if(alfa < 0){
					alfa = 0;
					incAlfa = -incAlfa;
				}
				//poligono.transformar(0.0,0.0,1.0,pi2*incAlfa);
				poligono.rotar(pi2*alfa);
				poligono.escalar(0.5f-alfa);
				poligono.actualizarLimiteRectangular();
				repaint();
				try{
					Thread.sleep(40);
				}catch(InterruptedException e){
				}
			}
		}
	}
	
	Test04Poligonos(){
		super(new Dimension(500,500));
		poligono = new Poligono(N_LADOS);
		new Animador().start();
	}

	public void paintComponent(Graphics g){
		g.setColor(Color.WHITE);
		g.clearRect(0,0,getWidth(),getHeight());
		
		g.setColor(Color.LIGHT_GRAY);
		dibuja(g,poligono.getLimiteRectangular());
		
		g.setColor(Color.BLACK);
		dibuja(g,poligono);
	}
	
	static public void main(String args[]){
		JFrame frame=new JFrame("Test 04: Poligono de "+N_LADOS+" lados");
		frame.getContentPane().add(new Test04Poligonos());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}

