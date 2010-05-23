package org.sam.colisiones;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public final class Test02LimiteRectangular extends PantallaTest{
	
	private LimiteRectangular limiteRectangular;
	private float wX1, wY1, wX2, wY2;
	private boolean puntoAsignado;
	
	private Test02LimiteRectangular(){
		super(new Dimension(500,500));
		limiteRectangular = new LimiteRectangular(0.5f,0.5f);
		puntoAsignado = false;
	}
	
	public void paintComponent(Graphics g){
		g.setColor(Color.WHITE);
		g.clearRect(0,0,getWidth(),getHeight());
		g.setColor(Color.BLACK);
		g.drawRect(getWidth()/4,getHeight()/4,getWidth()/2,getHeight()/2);
		
		int pX1 = xMundoPantalla(wX1);
		int pY1 = yMundoPantalla(wY1);
		int pX2 = xMundoPantalla(wX2);
		int pY2 = yMundoPantalla(wY2);
			
		if (limiteRectangular.hayInterseccion(wX1,wY1,wX2,wY2))
			g.setColor(Color.RED);
		else
			g.setColor(Color.BLUE);
			
		g.fillRect(pX1-2,pY1-2,5,5);
		g.fillRect(pX2-2,pY2-2,5,5);
		g.drawLine(pX1,pY1,pX2,pY2);
	}

	public void mouseReleased(MouseEvent e) {
		if(!puntoAsignado){
			wX1 = wX2 = xPantallaMundo(e.getX());
			wY1 = wY2 = yPantallaMundo(e.getY());
		}else{
			wX2 = xPantallaMundo(e.getX());
			wY2 = yPantallaMundo(e.getY());
		}
		puntoAsignado = !puntoAsignado;
	}

	public void mouseMoved(MouseEvent e) {
		if(puntoAsignado){
			wX2 = xPantallaMundo(e.getX());
			wY2 = yPantallaMundo(e.getY());
			repaint();
		}
	}
	
	static public void main(String args[]){
		JFrame frame=new JFrame("Test 02: Interseccion Segmento Limite Rectangular");
		frame.getContentPane().add(new Test02LimiteRectangular());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}

