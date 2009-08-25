package org.sam.colisiones;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;



public class Test02LimiteRectangular extends JPanel implements MouseListener, MouseMotionListener{
	private static final long serialVersionUID = 1L;
	
	LimiteRectangular limiteRectangular;
	double wX1, wY1, wX2, wY2;
	boolean puntoAsignado;
	
	Test02LimiteRectangular(){
		limiteRectangular = new LimiteRectangular(0.5,0.5);
		puntoAsignado = false;
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	static public void main(String args[]){
		// Se crea un frame, que va a contener nuestro panel
		JFrame frame=new JFrame();
		frame.setBounds(100, 100, 600, 600);
		frame.setTitle("Test 02: Interseccion Segmento Limite Rectangular");
		frame.setDefaultCloseOperation(3);
		frame.setResizable(false);

		// Creamos el panel donde dibujaremos
		Test02LimiteRectangular panel=new Test02LimiteRectangular();

		// Lo añadimos a la ventana
		frame.getContentPane().add(panel);
		
		// Mostramos la ventana
		frame.setVisible(true);
		
		// Vamos al bucle principal del programa contenido en nuestro panel
		panel.run();
	}
	
	public void run(){
		limiteRectangular = new LimiteRectangular(0.5,0.5);
		puntoAsignado = false;
	}

	public void paint(Graphics g){
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
			wX1 = wX2 = xPantallaMundo(e.getX());
			wY1 = wY2 = yPantallaMundo(e.getY());
		}else{
			wX2 = xPantallaMundo(e.getX());
			wY2 = yPantallaMundo(e.getY());
		}
		puntoAsignado = !puntoAsignado;
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
		if(puntoAsignado){
			wX2 = xPantallaMundo(e.getX());
			wY2 = yPantallaMundo(e.getY());
			repaint();
		}
	}
}

