package org.sam.colisiones;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Random;

import javax.swing.JPanel;

/**
 * Clase abstracta que contiene los métodos comunes a los distintos tipos de test derivados.
 */
@SuppressWarnings("serial")
public abstract class Test00Abs extends JPanel implements MouseListener, MouseMotionListener{
	
	/**
	 * Crea un polígono aleatorio, simple, concavo, irregular de {@code n} lados.
	 * @param nLados Número de lados del polígono.
	 * @return El polígono creado.
	 * @see <a href="http://es.wikipedia.org/wiki/Polígono">Polígono [Wikipedia]</a>
	 */
	protected final static Poligono crearPoligono(int nLados){
		if(nLados < 3)
			throw new IllegalArgumentException();
		
		Random r=new Random();
		
		float coordX[] = new float[nLados];
		float coordY[] = new float[nLados];

		float oX = 0.0f;
		float oY = 0.0f;
		
		double iAng = Math.PI*2 / nLados;
		double ang = iAng * (r.nextDouble() - 0.5);
		int loop = 0;
		while(loop < nLados){

			double dist = r.nextDouble();
		
			double val  = Math.cos(ang)*dist;
			coordX[loop]= (float)val; 
			oX += val;
			
			val  = Math.sin(ang)*dist;
			coordY[loop]= (float)val;
			oY += val;
			
			loop++;
			ang = iAng * (loop + r.nextDouble() - 0.5 );
		}
		oX /= nLados;
		oY /= nLados;
		
		for(loop = 0; loop< nLados; loop++){
			coordX[loop] -= oX;
			coordY[loop] -= oY;
		}
		return new Poligono(coordX, coordY).clone();
	}
	
	Test00Abs(Dimension size){
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
	
	protected final void dibuja(Graphics g, float pX, float pY){
		dibuja(g,  xMundoPantalla(pX),  yMundoPantalla(pY));
	}
	
	protected final void dibuja(Graphics g, int pX, int pY){
		g.fillRect( pX-1, pY-1, 3, 3 );
	}
	
	protected final void dibuja(Graphics g, LimiteRectangular rectangulo){
		int pX1 = xMundoPantalla(rectangulo.xII);
		int pY1 = yMundoPantalla(rectangulo.yII);
		int pX2 = xMundoPantalla(rectangulo.xSD);
		int pY2 = yMundoPantalla(rectangulo.ySD);

		g.drawRect(pX1,pY2,pX2-pX1,pY1-pY2);
		dibuja(g, pX1, pY1);
		dibuja(g, pX2, pY2);
	}
	
	protected final void pinta(Graphics g, LimiteRectangular rectangulo){
		int pX1 = xMundoPantalla(rectangulo.xII);
		int pY1 = yMundoPantalla(rectangulo.yII);
		int pX2 = xMundoPantalla(rectangulo.xSD);
		int pY2 = yMundoPantalla(rectangulo.ySD);

		g.fillRect(pX1,pY2,pX2-pX1,pY1-pY2);
	}
	
	protected final void dibuja(Graphics g, Segmento segmento){
		int pX1 = xMundoPantalla(segmento.x1);
		int pY1 = yMundoPantalla(segmento.y1);
		int pX2 = xMundoPantalla(segmento.x2);
		int pY2 = yMundoPantalla(segmento.y2);

		g.drawLine(pX1,pY1,pX2,pY2);
		dibuja(g, pX1, pY1);
		dibuja(g, pX2, pY2);
	}

	protected final void dibuja(Graphics g, Poligono poligono){
		for (int loop = 0; loop < poligono.getNLados(); loop++)
			dibuja(g, poligono.getSegmento(loop) );
	}
	
	protected final void dibujaSegmentosInteriores(Graphics g, Poligono poligono, LimiteRectangular rectangulo){
		for (int loop = 0; loop < poligono.getNLados(); loop++){
			Segmento segmento = poligono.getSegmento(loop);
			if( segmento.hayInterseccion(rectangulo) )
				dibuja(g, poligono.getSegmento(loop) );
		}
	}
	
	protected final void dibujaSegmentosExteriores(Graphics g, Poligono poligono, LimiteRectangular rectangulo){
		for (int loop = 0; loop < poligono.getNLados(); loop++){
			Segmento segmento = poligono.getSegmento(loop);
			if( !segmento.hayInterseccion(rectangulo) )
				dibuja(g, poligono.getSegmento(loop) );
		}
	}
	
    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    @Override
	public void mouseClicked(MouseEvent e) {
	}

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    @Override
	public void mouseEntered(MouseEvent e) {
	}

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    @Override
	public void mouseExited(MouseEvent e) {
	}

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
	public void mousePressed(MouseEvent e) {
	}

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
	public void mouseReleased(MouseEvent e) {
	}

    /* (non-Javadoc)
     * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
     */
    @Override
	public void mouseDragged(MouseEvent e) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
    @Override
	public void mouseMoved(MouseEvent e) {
	}
}

