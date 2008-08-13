/*
 * Created on 26-dic-2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.sam.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;

/**
 * @author Samuel
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class FloatComponent extends JComponent{
	private static final long serialVersionUID = 1L;
	
	private double x, y, ancho, alto;
	
	public FloatComponent(double x, double y, double ancho, double alto){
		this.x = x;
		this.y = y;
		this.ancho = ancho;
		this.alto = alto;
		this.setOpaque(true);
	}

	public Dimension getPreferredSize(){
		int anchoC = getParent().getWidth();
		setLocation((int)(x*anchoC),(int)(y*anchoC));			
		Dimension dim = super.getPreferredSize();
		dim.setSize(ancho*anchoC,alto*anchoC);
		setSize(dim);
		return dim;
	}
	
	public void paintComponent(Graphics g){
		g.setColor(Color.YELLOW);
		g.drawRect(0,0,getWidth(),getHeight());
	}
}
