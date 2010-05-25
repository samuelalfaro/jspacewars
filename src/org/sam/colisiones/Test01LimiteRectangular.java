package org.sam.colisiones;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public final class Test01LimiteRectangular extends Test00Abs{
	
	private LimiteRectangular rectangulos[], limiteRectangular;
	private float wX1, wY1, wX2, wY2;
	private boolean puntoAsignado;
	
	private Test01LimiteRectangular(){
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
		
		limiteRectangular = new LimiteRectangular();
		puntoAsignado = false;
	}
	
    @Override
	public void paintComponent(Graphics g){
		g.setColor(Color.WHITE);
		g.clearRect(0,0,getWidth(),getHeight());
		
		boolean hayInterseccion = false;
		for(LimiteRectangular rectangulo: rectangulos){
			LimiteRectangular interseccion = limiteRectangular.interseccion(rectangulo);
			if ( interseccion!= null ){
				hayInterseccion = true;
				g.setColor(Color.MAGENTA);
				pinta(g, interseccion);
			}
			g.setColor(Color.BLACK);
			dibuja(g, rectangulo);
		}
		g.setColor( hayInterseccion ? Color.RED : Color.BLUE);
		dibuja(g, limiteRectangular);
	}

	public void mouseReleased(MouseEvent e) {
		if(!puntoAsignado){
			wX1 = wX2 = xPantallaMundo(e.getX());
			wY1 = wY2 = yPantallaMundo(e.getY());
		}else{
			wX2 = xPantallaMundo(e.getX());
			wY2 = yPantallaMundo(e.getY());
		}
		limiteRectangular.setValues(wX1,wY1,wX2,wY2);
		puntoAsignado = !puntoAsignado;
	}

	public void mouseMoved(MouseEvent e) {
		if(puntoAsignado){
			wX2 = xPantallaMundo(e.getX());
			wY2 = yPantallaMundo(e.getY());
			limiteRectangular.setValues(wX1,wY1,wX2,wY2);
			repaint();
		}
	}

	static public void main(String args[]){
		JFrame frame=new JFrame("Test 01: Interseccion Limites Rectangulares");
		frame.getContentPane().add(new Test01LimiteRectangular());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}

