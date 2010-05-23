package org.sam.colisiones;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class PantallaTest extends JPanel implements MouseListener, MouseMotionListener{
	
	PantallaTest(Dimension size){
		this.setMinimumSize(size);
		this.setPreferredSize(size);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}
	
	protected final int xMundoPantalla(float c){
		return (int)((c+0.5f)*getWidth());	
	}

	protected final float xPantallaMundo(int c){
		return ((float)c)/getWidth() - 0.5f;
	}

	protected final int yMundoPantalla(float c){
		return -(int)((c-0.5f)*getHeight());
	}

	protected final float yPantallaMundo(int c){
		return ((float)-c)/getHeight() + 0.5f;
	}
	
	protected final void dibuja(Graphics g, LimiteRectangular rectangulo){
		int pX1 = xMundoPantalla(rectangulo.x1);
		int pY1 = yMundoPantalla(rectangulo.y1);
		int pX2 = xMundoPantalla(rectangulo.x2);
		int pY2 = yMundoPantalla(rectangulo.y2);

		g.fillRect(pX1-2,pY1-2,5,5);
		g.fillRect(pX2-2,pY2-2,5,5);
		g.drawRect(pX1,pY2,pX2-pX1,pY1-pY2);
	}
	
	protected final void pinta(Graphics g, LimiteRectangular rectangulo){
		int pX1 = xMundoPantalla(rectangulo.x1);
		int pY1 = yMundoPantalla(rectangulo.y1);
		int pX2 = xMundoPantalla(rectangulo.x2);
		int pY2 = yMundoPantalla(rectangulo.y2);

		g.fillRect(pX1,pY2,pX2-pX1,pY1-pY2);
	}
	
	protected final void dibuja(Graphics g, Segmento segmento){
		int pX1 = xMundoPantalla(segmento.x1);
		int pY1 = yMundoPantalla(segmento.y1);
		int pX2 = xMundoPantalla(segmento.x2);
		int pY2 = yMundoPantalla(segmento.y2);

		g.fillRect(pX1-2,pY1-2,5,5);
		g.fillRect(pX2-2,pY2-2,5,5);
		g.drawLine(pX1,pY1,pX2,pY2);
	}

	protected final void dibuja(Graphics g, Poligono poligono){
		for (int loop = 0; loop < poligono.getNLados(); loop++)
			dibuja(g, poligono.getSegmento(loop) );
	}
	
	public void mouseClicked(MouseEvent e) {
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
	}

	public void mouseMoved(MouseEvent e) {
	}
}

