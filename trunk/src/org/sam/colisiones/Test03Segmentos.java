package org.sam.colisiones;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;



public class Test03Segmentos extends JPanel implements MouseListener, MouseMotionListener{
	private static final long serialVersionUID = 1L;
	
	double s1X1, s1Y1, s1X2, s1Y2;
	double s2X1, s2Y1, s2X2, s2Y2;
	double s3X1, s3Y1, s3X2, s3Y2;
	double s4X1, s4Y1, s4X2, s4Y2;
	
	double sX1, sY1, sX2, sY2;
	
	boolean puntoAsignado;
	
	Test03Segmentos(){
		// segmento horizontal
		s1X1 = -0.25; s1Y1= 0.0; s1X2= 0.25; s1Y2= 0.0;
		// segmento vetical
		s2X1 = 0.0; s2Y1= -0.25; s2X2= 0.0; s2Y2 = 0.25;
		// segmento diagonal
		s3X1 = -0.25; s3Y1= -0.25; s3X2 = 0.25;  s3Y2 = 0.25; 
		// segmento contradiagonal1
		s4X1 = -0.25; s4Y1= 0.25; s4X2 = 0.25;  s4Y2 = -0.25;
		
		sX1= sY1= sX2= sY2= 0.0;
		
		puntoAsignado = false;
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	static public void main(String args[]){
		// Se crea un frame, que va a contener nuestro panel
		JFrame frame=new JFrame();
		frame.setBounds(100, 100, 600, 600);
		frame.setTitle("Test 03: Interseccion Segmentos");
		frame.setDefaultCloseOperation(3);
		frame.setResizable(false);

		// Creamos el panel donde dibujaremos
		Test03Segmentos panel=new Test03Segmentos();

		// Lo añadimos a la ventana
		frame.getContentPane().add(panel);
		
		// Mostramos la ventana
		frame.setVisible(true);
		
		// Vamos al bucle principal del programa contenido en nuestro panel
		panel.run();
	}
	
	public void run(){
		puntoAsignado = false;
	}

	void dibujaSegmento(Graphics g, double wX1, double wY1,double wX2,double wY2){
		int pX1 = xMundoPantalla(wX1);
		int pY1 = yMundoPantalla(wY1);
		int pX2 = xMundoPantalla(wX2);
		int pY2 = yMundoPantalla(wY2);

		g.fillRect(pX1-2,pY1-2,5,5);
		g.fillRect(pX2-2,pY2-2,5,5);
		g.drawLine(pX1,pY1,pX2,pY2);
	}
	
	public void paint(Graphics g){
		g.setColor(Color.WHITE);
		g.clearRect(0,0,getWidth(),getHeight());
		
		boolean hayInterseccion = false;
		
		if (Segmento.hayInterseccion(sX1,sY1,sX2,sY2,s1X1,s1Y1,s1X2,s1Y2)){
			hayInterseccion = true;
			g.setColor(Color.GREEN);
		}else
			g.setColor(Color.BLACK);
		dibujaSegmento(g,s1X1,s1Y1,s1X2,s1Y2);
		
		if (Segmento.hayInterseccion(sX1,sY1,sX2,sY2,s2X1,s2Y1,s2X2,s2Y2)){
			hayInterseccion = true;
			g.setColor(Color.GREEN);
		}else
			g.setColor(Color.BLACK);
		dibujaSegmento(g,s2X1,s2Y1,s2X2,s2Y2);
		
		if (Segmento.hayInterseccion(sX1,sY1,sX2,sY2,s3X1,s3Y1,s3X2,s3Y2)){
			hayInterseccion = true;
			g.setColor(Color.GREEN);
		}else
			g.setColor(Color.BLACK);
		dibujaSegmento(g,s3X1,s3Y1,s3X2,s3Y2);
		
		if (Segmento.hayInterseccion(sX1,sY1,sX2,sY2,s4X1,s4Y1,s4X2,s4Y2)){
			hayInterseccion = true;
			g.setColor(Color.GREEN);
		}else
			g.setColor(Color.BLACK);
		dibujaSegmento(g,s4X1,s4Y1,s4X2,s4Y2);
		
		if (hayInterseccion)
			g.setColor(Color.RED);
		else
			g.setColor(Color.BLUE);
		dibujaSegmento(g,sX1,sY1,sX2,sY2);
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
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
	
	public void mouseReleased(MouseEvent e) {
		if(!puntoAsignado){
			sX1 = sX2 = xPantallaMundo(e.getX());
			sY1 = sY2 = yPantallaMundo(e.getY());
		}else{
			sX2 = xPantallaMundo(e.getX());
			sY2 = yPantallaMundo(e.getY());
		}
		puntoAsignado = !puntoAsignado;
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
		if(puntoAsignado){
			sX2 = xPantallaMundo(e.getX());
			sY2 = yPantallaMundo(e.getY());
			repaint();
		}
	}
}

