package org.sam.colisiones;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;



public class Test04Poligonos extends JPanel{
	private static final long serialVersionUID = 1L;
	
	static final int N_LADOS = 20;
	Poligono poligono;
	
	class Animador extends Thread{
		public void run(){
			double alfa = 0.0;
			double incAlfa = 0.005;
			double pi2 = Math.PI * 2;
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
				poligono.escalar(0.5-alfa);
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
		poligono = new Poligono(N_LADOS);
	}

	private Animador getAnimador(){
		return new Animador();
	}
	
	static public void main(String args[]){
		// Se crea un frame, que va a contener nuestro panel
		JFrame frame=new JFrame();
		frame.setBounds(100, 100, 600, 600);
		frame.setTitle("Test 04: Poligono de "+N_LADOS+" lados");
		frame.setDefaultCloseOperation(3);
		frame.setResizable(false);

		// Creamos el panel donde dibujaremos
		Test04Poligonos panel=new Test04Poligonos();

		// Lo añadimos a la ventana
		frame.getContentPane().add(panel);
		
		// Mostramos la ventana
		frame.setVisible(true);
		Animador animador = panel.getAnimador();
		animador.start();
		
	}

	int xMundoPantalla(double c){
		return (int)((c+0.5)*getWidth());	
	}

	double xPantallaMundo(int c){
		return ((double)c)/getWidth() - 0.5;
	}

	int yMundoPantalla(double c){
		return -(int)((c-0.5)*getHeight());
	}

	double yPantallaMundo(int c){
		return ((double)-c)/getHeight() + 0.5;
	}
	
	void dibuja(Graphics g, LimiteRectangular rectangulo){
		int pX1 = xMundoPantalla(rectangulo.x1);
		int pY1 = yMundoPantalla(rectangulo.y1);
		int pX2 = xMundoPantalla(rectangulo.x2);
		int pY2 = yMundoPantalla(rectangulo.y2);

		g.fillRect(pX1-2,pY1-2,5,5);
		g.fillRect(pX2-2,pY2-2,5,5);
		g.drawRect(pX1,pY2,pX2-pX1,pY1-pY2);
	}
	
	void dibuja(Graphics g, Segmento segmento){
		int pX1 = xMundoPantalla(segmento.x1);
		int pY1 = yMundoPantalla(segmento.y1);
		int pX2 = xMundoPantalla(segmento.x2);
		int pY2 = yMundoPantalla(segmento.y2);

		g.fillRect(pX1-2,pY1-2,5,5);
		g.fillRect(pX2-2,pY2-2,5,5);
		g.drawLine(pX1,pY1,pX2,pY2);
	}

	void dibuja(Graphics g, Poligono poligono){
		for (int loop = 0; loop< N_LADOS; loop++){
			dibuja(g, poligono.getSegmento(loop) );
		}
	}
	
	public void paint(Graphics g){
		g.setColor(Color.WHITE);
		g.clearRect(0,0,getWidth(),getHeight());
		
		g.setColor(Color.LIGHT_GRAY);
		dibuja(g,poligono.getLimiteRectangular());
		
		g.setColor(Color.BLACK);
		dibuja(g,poligono);
	}
}

