package org.sam.colisiones;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;



public class Test01LimiteRectangular extends JPanel implements MouseListener, MouseMotionListener{
	private static final long serialVersionUID = 1L;
	
	LimiteRectangular limiteRectangular, otroLimiteRectangular;
	double wX1, wY1, wX2, wY2;
	boolean puntoAsignado;
	
	Test01LimiteRectangular(){
		limiteRectangular = new LimiteRectangular(0.5,0.5);
		otroLimiteRectangular = new LimiteRectangular();
		puntoAsignado = false;
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	static public void main(String args[]){
		// Se crea un frame, que va a contener nuestro panel
		JFrame frame=new JFrame();
		frame.setBounds(100, 100, 600, 600);
		frame.setTitle("Test 01: Interseccion Limites Rectangulares");
		frame.setDefaultCloseOperation(3);
		frame.setResizable(false);

		// Creamos el panel donde dibujaremos
		Test01LimiteRectangular panel=new Test01LimiteRectangular();

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
		otroLimiteRectangular.setValues(wX1,wY1,wX2,wY2,false);
		puntoAsignado = !puntoAsignado;
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
		if(puntoAsignado){
			wX2 = xPantallaMundo(e.getX());
			wY2 = yPantallaMundo(e.getY());
			otroLimiteRectangular.setValues(wX1,wY1,wX2,wY2,false);
			repaint();
		}
	}
}

