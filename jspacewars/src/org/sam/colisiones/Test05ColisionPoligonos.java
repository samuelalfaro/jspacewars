package org.sam.colisiones;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;



public class Test05ColisionPoligonos extends JPanel implements MouseListener, MouseMotionListener{
	private static final long serialVersionUID = 1L;

	static final int N_LADOS = 15;
	Poligono poligono1, poligono2;
	
	class Animador extends Thread{
		public void run(){
			double alfa = 0.0;
			double incAlfa = 0.005;
			double pi2 = Math.PI * 2;
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
		poligono1 = new Poligono(N_LADOS);
		poligono1.escalar(0.25);
		poligono1.trasladar(0.25,0.25);
		poligono1.actualizarLimiteRectangular();
		
		poligono2 = null;
		
		addMouseListener(this);
		addMouseMotionListener(this);
	}		

	private Animador getAnimador(){
		return new Animador();
	}
	
	static public void main(String args[]){
		// Se crea un frame, que va a contener nuestro panel
		JFrame frame=new JFrame();
		frame.setBounds(100, 100, 600, 600);
		frame.setTitle("Test 05: Colision de Poligonos");
		frame.setDefaultCloseOperation(3);
		frame.setResizable(false);

		// Creamos el panel donde dibujaremos
		Test05ColisionPoligonos panel=new Test05ColisionPoligonos();

		// Lo añadimos a la ventana
		frame.getContentPane().add(panel);
		
		// Mostramos la ventana
		frame.setVisible(true);
		Animador animador = panel.getAnimador();
		animador.start();
		
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
	
	void dibuja(Graphics g, LimiteRectangular rectangulo){
		int pX1 = xMundoPantalla(rectangulo.x1);
		int pY1 = yMundoPantalla(rectangulo.y1);
		int pX2 = xMundoPantalla(rectangulo.x2);
		int pY2 = yMundoPantalla(rectangulo.y2);

		g.fillRect(pX1-2,pY1-2,5,5);
		g.fillRect(pX2-2,pY2-2,5,5);
		g.drawRect(pX1,pY2,pX2-pX1,pY1-pY2);
	}
	
	void pinta(Graphics g, LimiteRectangular rectangulo){
		int pX1 = xMundoPantalla(rectangulo.x1);
		int pY1 = yMundoPantalla(rectangulo.y1);
		int pX2 = xMundoPantalla(rectangulo.x2);
		int pY2 = yMundoPantalla(rectangulo.y2);

		g.fillRect(pX1,pY2,pX2-pX1,pY1-pY2);
	}
		
	void dibuja(Graphics g, Segmento segmento){
		int pX1 = xMundoPantalla(segmento.x1);
		int pY1 = yMundoPantalla(segmento.y1);
		int pX2 = xMundoPantalla(segmento.x2);
		int pY2 = yMundoPantalla(segmento.y2);

		g.fillRect(pX1-2,pY1-2,5,5);
		g.fillRect(pX2-2,pY2-2,5,5);
		g.drawLine(pX1,pY1,pX2,pY2);
	}

	void dibuja(Graphics g, Poligono poligono){
		for (int loop = 0; loop< N_LADOS; loop++){
			dibuja(g, poligono.getSegmento(loop) );
		}
	}
	
	public void paint(Graphics g){
		g.setColor(Color.WHITE);
		g.clearRect(0,0,getWidth(),getHeight());
		
		LimiteRectangular l1, l2;
		if(poligono2 != null){
			l1 = poligono1.getLimiteRectangular();
			l2 = poligono2.getLimiteRectangular();

			if(l1.hayInterseccion(l2)){
				g.setColor(Color.YELLOW);
				pinta(g,l1.interseccion(l2));
			}
			
			g.setColor(Color.LIGHT_GRAY);
			dibuja(g,l1);
			dibuja(g,l2);
			
			if (poligono1.hayIntersecion(poligono2))
				g.setColor(Color.RED);
			else
				g.setColor(Color.BLACK);
			dibuja(g,poligono1);
			dibuja(g,poligono2);
		}else{
			g.setColor(Color.LIGHT_GRAY);
			dibuja(g,poligono1.getLimiteRectangular());
		
			g.setColor(Color.BLACK);
			dibuja(g,poligono1);
		}
	}
	
	public void mouseClicked(MouseEvent e) {
		poligono2 = new Poligono(N_LADOS);
		poligono2.escalar(0.25);
		double x = xPantallaMundo(e.getX());
		double y = yPantallaMundo(e.getY());
		poligono2.trasladar(x,y);
		poligono2.actualizarLimiteRectangular();
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
		if (poligono2 == null) return;
		double x = xPantallaMundo(e.getX());
		double y = yPantallaMundo(e.getY());
		poligono2.trasladar(x,y);
		poligono2.actualizarLimiteRectangular();
	}

	public void mouseMoved(MouseEvent e) {
	}
}