package org.sam.colisiones;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public final class Test05ColisionPoligonos extends Test00Abs{

	static final int N_LADOS = 15;
	
	Poligono poligono1, poligono2;
	
	class Animador extends Thread{
		public void run(){
			float alfa = 0.0f;
			float incAlfa = 0.001f;
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
				poligono1.rotar(pi2*alfa);
				poligono1.actualizarLimiteRectangular();
				repaint();
				try{
					Thread.sleep(40);
				}catch(InterruptedException e){
				}
			}
		}
	}
	
	Test05ColisionPoligonos(){
		super(new Dimension(500,500));
		
		poligono1 = new Poligono(N_LADOS);
		poligono1.escalar(0.25f);
		poligono1.trasladar(0.25f,0.25f);
		poligono1.actualizarLimiteRectangular();
		
		poligono2 = null;
		new Animador().start();
	}		
	
	int segmentosInterseccion[] = new int[2];
	
	public void paintComponent(Graphics g){
		g.setColor(Color.WHITE);
		g.clearRect(0,0,getWidth(),getHeight());
		
		LimiteRectangular l1, l2;
		if(poligono2 == null){
			g.setColor(Color.LIGHT_GRAY);
			dibuja(g,poligono1.getLimiteRectangular());
		
			g.setColor(Color.BLACK);
			dibuja(g,poligono1);
		}else{
			l1 = poligono1.getLimiteRectangular();
			l2 = poligono2.getLimiteRectangular();

			LimiteRectangular interseccionRectangular = l1.interseccion(l2);
			if(interseccionRectangular != null){
				g.setColor(Color.YELLOW);
				pinta(g, interseccionRectangular );
				
				g.setColor(Color.LIGHT_GRAY);
				dibuja(g,l1);
				dibuja(g,l2);
				
				boolean chocan = poligono1.hayIntersecion(poligono2, segmentosInterseccion);

				Color segmentosInterioresColor = chocan ? Color.ORANGE.darker(): Color.BLUE;
				Color segmentosExterioresColor = chocan ? Color.MAGENTA.darker(): Color.GREEN.darker();

				g.setColor(segmentosExterioresColor);
				dibujaSegmentosExteriores(g,poligono1, interseccionRectangular);
				g.setColor(segmentosInterioresColor);
				dibujaSegmentosInteriores(g,poligono1, interseccionRectangular);
				
				g.setColor(segmentosExterioresColor);
				dibujaSegmentosExteriores(g,poligono2, interseccionRectangular);
				g.setColor(segmentosInterioresColor);
				dibujaSegmentosInteriores(g,poligono2, interseccionRectangular);

				
				if(chocan){
					g.setColor(Color.RED.darker());
					dibuja(g,poligono1.getSegmento(segmentosInterseccion[0]));
					dibuja(g,poligono2.getSegmento(segmentosInterseccion[1]));
				}
			}else{
				g.setColor(Color.LIGHT_GRAY);
				dibuja(g,l1);
				dibuja(g,l2);
				g.setColor(Color.BLACK);
				dibuja(g,poligono1);
				dibuja(g,poligono2);
			}
		}
	}
	
	public void mouseClicked(MouseEvent e) {
		poligono2 = new Poligono(N_LADOS);
		poligono2.escalar(0.25f);
		float x = xPantallaMundo(e.getX());
		float y = yPantallaMundo(e.getY());
		poligono2.trasladar(x,y);
		poligono2.actualizarLimiteRectangular();
	}

	public void mouseDragged(MouseEvent e) {
		if (poligono2 == null)
			return;
		float x = xPantallaMundo(e.getX());
		float y = yPantallaMundo(e.getY());
		poligono2.trasladar(x,y);
		poligono2.actualizarLimiteRectangular();
	}
	
	static public void main(String args[]){
		JFrame frame=new JFrame("Test 05: Colision de Poligonos");
		frame.getContentPane().add(new Test05ColisionPoligonos());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}