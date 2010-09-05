package org.sam.colisiones;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;

/**
 * Clase para testear las interseciones entre dos objetos {@code Poligono}.
 */
@SuppressWarnings("serial")
public final class Test05ColisionPoligonos extends Test00Abs{

	private static final int N_LADOS = 15;
	
	private Poligono poligono1, poligono2;
	private List<Object> elementosEvaluados;
	
	private class Animador extends Thread{
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
				poligono1.transformar(pi2*alfa, 0.5f, 0.0f, 0.0f);
				poligono1.actualizarLimiteRectangular();
				repaint();
				try{
					Thread.sleep(40);
				}catch(InterruptedException e){
				}
			}
		}
	}
	
	private Test05ColisionPoligonos(){
		super(new Dimension(500,500));
		elementosEvaluados = new ArrayList<Object>(30);
		poligono1 = crearPoligono(N_LADOS);
		poligono1.actualizarLimiteRectangular();
		
		poligono2 = null;
		new Animador().start();
	}		
	
	public void paintComponent(Graphics g){
		g.setColor(Color.WHITE);
		g.clearRect(0,0,getWidth(),getHeight());
		
		LimiteRectangular l1, l2;
		if(poligono2 == null){
			g.setColor(Color.LIGHT_GRAY);
			dibuja(g,poligono1.getLimites());
		
			g.setColor(Color.BLACK);
			dibuja(g,poligono1);
		}else{
			l1 = poligono1.getLimites();
			l2 = poligono2.getLimites();

			LimiteRectangular interseccionRectangular = l1.interseccion(l2);
			if(interseccionRectangular != null){
				g.setColor(Color.YELLOW);
				pinta(g, interseccionRectangular );
				
				g.setColor(Color.LIGHT_GRAY);
				dibuja(g,l1);
				dibuja(g,l2);
				
				boolean test1 = poligono1.hayColision(poligono2);
				boolean test2 = poligono1.hayColision(poligono2, elementosEvaluados);
				
				if(test1 != test2)
					System.err.println("Error: Tests distintos");
				
				boolean chocan = test1;

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
				
				Iterator<?> it = elementosEvaluados.iterator();
				while(it.hasNext()){
					Object obj = it.next();
					if( obj instanceof Point2D.Float){
						Point2D.Float p = (Point2D.Float)obj;
						g.setColor(it.hasNext()?Color.MAGENTA:Color.RED.darker());
						dibuja( g, p.x, p.y );
					}else if( obj instanceof Segmento){
						g.setColor(Color.RED.darker());
						dibuja( g, (Segmento)obj );
					}
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
		poligono2 = crearPoligono(N_LADOS);
		float x = xPantallaMundo(e.getX());
		float y = yPantallaMundo(e.getY());
		poligono2.transformar( 0.0f, 0.1f, x, y );
		poligono2.actualizarLimiteRectangular();
	}

	public void mouseDragged(MouseEvent e) {
		if (poligono2 == null)
			return;
		float x = xPantallaMundo(e.getX());
		float y = yPantallaMundo(e.getY());
		poligono2.transformar( 0.0f, 0.1f, x, y );
		poligono2.actualizarLimiteRectangular();
	}
	
	/**
	 * Método principal encargado de lanzar este test.
	 * @param args ignorados.
	 */
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