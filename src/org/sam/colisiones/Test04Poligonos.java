package org.sam.colisiones;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public final class Test04Poligonos extends Test00Abs{
	private static final int N_LADOS = 20;
	private Poligono poligono;
	
	private class Animador extends Thread{
		public void run(){
			float alfa = 0.0f;
			float incAlfa = 0.0025f;
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
				poligono.transformar(pi2*alfa, 0.5f-alfa, 0.0f, 0.0f);
				poligono.actualizarLimiteRectangular();
				repaint();
				try{
					Thread.sleep(40);
				}catch(InterruptedException e){
				}
			}
		}
	}
	
	private Test04Poligonos(){
		super(new Dimension(500,500));
		this.poligono = crearPoligono(N_LADOS);
		new Animador().start();
	}

	public void paintComponent(Graphics g){
		g.setColor(Color.WHITE);
		g.clearRect(0,0,getWidth(),getHeight());
		
		LimiteRectangular rectangulo = poligono.getLimiteRectangular();
		for(float x = -0.45f; x < 0.5f; x += 0.05f)
			for(float y = -0.45f; y < 0.5f; y += 0.05f){
				g.setColor( rectangulo.contiene(x, y) ? poligono.contiene(x, y) ? Color.RED: Color.BLUE: Color.BLACK );
				dibuja(g,x,y);
			}
				
		g.setColor(Color.LIGHT_GRAY);
		dibuja(g,rectangulo);
		
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

