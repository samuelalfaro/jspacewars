/*
 * Created on 03-ene-2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.sam.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;

/**
 * @author Samuel
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public final class EtiquetaAnimada extends JComponent implements MouseListener{
	private static final long serialVersionUID = 1L;

	private int mouseOut;
	private int mouseIn;
	private int mouseClick;

	private Color[] colores;
	private String texto;
	private int frame;
	private int state;
	
	public static Color[] generarColores(Color colOut, Color colIn, Color colClick, int frames){
		Color _colores[] = new Color[frames + 3];
		_colores[0] = colOut;
		
		int col1 = colOut.getRGB();
		int col2 = colIn.getRGB();
		for(int i = 1; i <= frames; i++){
			int a = (i*0xFF)/(frames+1);
			int invA = 0xFF - a;
			
			int alfa = ((col1>>24 & 0xFF)*invA + (col2>>24 & 0xFF)*a);
			int r = ((col1>>16 & 0xFF)*invA + (col2>>16 & 0xFF)*a);
			int g = ((col1>>8 & 0xFF)*invA + (col2>>8 & 0xFF)*a);
			int b = ((col1 & 0xFF)*invA + (col2 & 0xFF)*a);
	
			_colores[i] = new Color(r>>8,g>>8,b>>8,alfa>>8); 
		}
		_colores[frames+1] = colIn;
		_colores[frames+2] = colClick;
		System.gc();
		return _colores;
	}
	
	public EtiquetaAnimada(String _texto, Color[] _colores){
		super();
		//addMouseListener(this);
		mouseOut = 0;
		mouseIn  = _colores.length -2;
		mouseClick  = _colores.length -1;
		setOpaque(false);
		
		frame = state = mouseOut;
		colores = _colores;
		texto = _texto;
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
		frame = state = mouseIn;
		repaint();
	}

	public void mouseExited(MouseEvent e) {
		frame = mouseOut;
		state --;
		repaint();
	}

	public void mousePressed(MouseEvent e) {
		frame = state = mouseClick;
		repaint();
	}

	public void mouseReleased(MouseEvent e) {
		if (frame == mouseClick){
			frame = state = mouseIn;
			repaint();
		}
	}

	public void redibujar(){
		if( state != frame && state>0 ){
			repaint();
			state --;
		}
	}

	public void paintComponent(Graphics g){
		try{
			g.setColor(colores[state]);
			FontMetrics fm = g.getFontMetrics();
			int x = (getWidth() - fm.stringWidth(texto)) / 2;
			int y = (getHeight() - fm.getHeight())/2+fm.getMaxAscent();
			g.drawString(texto,x,y);
		}catch(NullPointerException e){
			System.out.println(state);
		}
	}
	
	public Dimension getMinimumSize(){
		return new Dimension(136,63);
	}

	public Dimension getPreferredSize(){
		return getMinimumSize();
	}
}
