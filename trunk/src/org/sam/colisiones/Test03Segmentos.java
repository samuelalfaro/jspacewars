package org.sam.colisiones;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public final class Test03Segmentos extends PantallaTest{
	
	private Segmento s1;
	private Segmento s2;
	private Segmento s3;
	private Segmento s4;
	
	private float sX1, sY1, sX2, sY2;
	private Segmento segmento;
	
	private boolean puntoAsignado;
	
	private Test03Segmentos(){
		super(new Dimension(500,500));
		
		s1 = new Segmento( -0.25f,  0.0f,  0.25f,  0.0f);  // segmento horizontal
		s2 = new Segmento(   0.0f, -0.25f, 0.0f,   0.25f); // segmento vetical
		s3 = new Segmento( -0.25f, -0.25f, 0.25f,  0.25f); // segmento diagonal
		s4 = new Segmento( -0.25f,  0.25f, 0.25f, -0.25f); // segmento contradiagonal1
		
		sX1= sY1= sX2= sY2= 0.0f;
		segmento = new Segmento( sX1, sY1, sX2, sY2 );
		
		puntoAsignado = false;
	}
	
	public void paintComponent(Graphics g){
		g.setColor(Color.WHITE);
		g.clearRect(0,0,getWidth(),getHeight());
		
		boolean hayInterseccion = false;
		
		if (segmento.hayInterseccion(s1)){
			hayInterseccion = true;
			g.setColor(Color.GREEN);
		}else
			g.setColor(Color.BLACK);
		dibuja(g,s1);
		
		if (segmento.hayInterseccion(s2)){
			hayInterseccion = true;
			g.setColor(Color.GREEN);
		}else
			g.setColor(Color.BLACK);
		dibuja(g,s2);
		
		if (segmento.hayInterseccion(s3)){
			hayInterseccion = true;
			g.setColor(Color.GREEN);
		}else
			g.setColor(Color.BLACK);
		dibuja(g,s3);
		
		if (segmento.hayInterseccion(s4)){
			hayInterseccion = true;
			g.setColor(Color.GREEN);
		}else
			g.setColor(Color.BLACK);
		dibuja(g,s4);
		
		if (hayInterseccion)
			g.setColor(Color.RED);
		else
			g.setColor(Color.BLUE);
		dibuja(g,segmento);
	}

	public void mouseReleased(MouseEvent e) {
		if(!puntoAsignado){
			sX1 = sX2 = xPantallaMundo(e.getX());
			sY1 = sY2 = yPantallaMundo(e.getY());
		}else{
			sX2 = xPantallaMundo(e.getX());
			sY2 = yPantallaMundo(e.getY());
		}
		segmento.setValues( sX1, sY1, sX2, sY2 );
		puntoAsignado = !puntoAsignado;
	}

	public void mouseMoved(MouseEvent e) {
		if(puntoAsignado){
			sX2 = xPantallaMundo(e.getX());
			sY2 = yPantallaMundo(e.getY());
			repaint();
		}
		segmento.setValues( sX1, sY1, sX2, sY2 );
	}
	
	static public void main(String args[]){
		JFrame frame=new JFrame("Test 03: Interseccion Segmentos");
		frame.getContentPane().add(new Test03Segmentos());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}

