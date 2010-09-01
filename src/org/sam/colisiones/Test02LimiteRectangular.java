package org.sam.colisiones;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

/**
 * Clase para testear las interseciones entre un {@code Segmento} distintos objetos {@code LimiteRectangular}.
 */
@SuppressWarnings("serial")
public final class Test02LimiteRectangular extends Test00Abs{
	
	private LimiteRectangular[] rectangulos;
	private Segmento segmento;
	private boolean puntoAsignado;
	
	private Test02LimiteRectangular(){
		super(new Dimension(500,500));
		rectangulos = new LimiteRectangular[13];
		for(int i= 0; i < 9; i++){
			rectangulos[i] =  new LimiteRectangular(0.15f,0.15f);
			float despX = (i % 3)*0.2f -0.2f;
			float despY = (i / 3)*0.2f -0.2f;
			rectangulos[i].trasladar(despX, despY);
			
		}
		rectangulos[ 9] =  new LimiteRectangular( -.275f,-.425f, .275f,-.375f );
		rectangulos[10] =  new LimiteRectangular( -.425f,-.275f,-.375f, .275f );
		rectangulos[11] =  new LimiteRectangular(  .375f,-.275f, .425f, .275f );
		rectangulos[12] =  new LimiteRectangular( -.275f, .375f, .275f, .425f );
		
		segmento = new Segmento();
		puntoAsignado = false;
	}
	
	public void paintComponent(Graphics g){
		g.setColor(Color.WHITE);
		g.clearRect(0,0,getWidth(),getHeight());
		
		boolean hayInterseccion = false;
		
		for(LimiteRectangular rectangulo: rectangulos){
			if(rectangulo.hayInterseccion(segmento)){
				hayInterseccion = true;
				g.setColor(Color.MAGENTA);
				pinta(g, rectangulo);
			}
			g.setColor(Color.BLACK);
			dibuja(g, rectangulo);
		}
		g.setColor( hayInterseccion ? Color.RED : Color.BLACK);
		dibuja( g, segmento );
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
		JFrame frame=new JFrame("Test 02: Interseccion Segmento Limites Rectangulares");
		frame.getContentPane().add(new Test02LimiteRectangular());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}

