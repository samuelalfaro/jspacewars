package org.sam.colisiones;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public final class Test01LimiteRectangular extends PantallaTest{
	
	private LimiteRectangular limiteRectangular, otroLimiteRectangular;
	private float wX1, wY1, wX2, wY2;
	private boolean puntoAsignado;
	
	Test01LimiteRectangular(){
		super(new Dimension(500,500));
		limiteRectangular = new LimiteRectangular(0.5f,0.5f);
		otroLimiteRectangular = new LimiteRectangular();
		puntoAsignado = false;
	}
	
    @Override
	public void paintComponent(Graphics g){
		g.setColor(Color.WHITE);
		g.clearRect(0,0,getWidth(),getHeight());
		
		int pX1, pY1;
		int pX2, pY2;
			
		if (limiteRectangular.hayInterseccion(otroLimiteRectangular)){
			LimiteRectangular interseccion = limiteRectangular.interseccion(otroLimiteRectangular);
			pX1 = xMundoPantalla(interseccion.x1);
			pY1 = yMundoPantalla(interseccion.y2);
			pX2 = xMundoPantalla(interseccion.x2);
			pY2 = yMundoPantalla(interseccion.y1);
			g.setColor(Color.MAGENTA);
			g.fillRect(pX1,pY1,pX2-pX1,pY2-pY1);
			g.setColor(Color.RED);
		}
		else
			g.setColor(Color.BLUE);

		pX1 = xMundoPantalla(wX1);
		pY1 = yMundoPantalla(wY1);
		pX2 = xMundoPantalla(wX2);
		pY2 = yMundoPantalla(wY2);
		
		g.fillRect(pX1-2,pY1-2,5,5);
		g.fillRect(pX2-2,pY2-2,5,5);
		g.drawRect(Math.min(pX1,pX2),Math.min(pY1,pY2),Math.abs(pX2-pX1),Math.abs(pY2-pY1));
		
		g.setColor(Color.BLACK);
		g.drawRect(getWidth()/4,getHeight()/4,getWidth()/2,getHeight()/2);
	}

	public void mouseReleased(MouseEvent e) {
		if(!puntoAsignado){
			wX1 = wX2 = xPantallaMundo(e.getX());
			wY1 = wY2 = yPantallaMundo(e.getY());
		}else{
			wX2 = xPantallaMundo(e.getX());
			wY2 = yPantallaMundo(e.getY());
		}
		otroLimiteRectangular.setValues(wX1,wY1,wX2,wY2,false);
		puntoAsignado = !puntoAsignado;
	}

	public void mouseMoved(MouseEvent e) {
		if(puntoAsignado){
			wX2 = xPantallaMundo(e.getX());
			wY2 = yPantallaMundo(e.getY());
			otroLimiteRectangular.setValues(wX1,wY1,wX2,wY2,false);
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

