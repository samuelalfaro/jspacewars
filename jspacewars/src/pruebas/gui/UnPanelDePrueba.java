/*
 * Created on 04-ene-2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package pruebas.gui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import org.sam.gui.BotonAnimado;
import org.sam.gui.EtiquetaAnimada;

public class UnPanelDePrueba extends JPanel implements MouseListener {
	private static final long serialVersionUID = 1L;

	BotonAnimado boton;
	EtiquetaAnimada etiqueta;
	boolean seleccionado;
	
	public UnPanelDePrueba(LayoutManager layout, BotonAnimado _boton, EtiquetaAnimada _etiqueta) {
		super(layout);
		boton = _boton;
		etiqueta = _etiqueta;
		
		add(boton);
		add(etiqueta);
		addMouseListener(this);
		boton.addMouseListener(this);
		etiqueta.addMouseListener(this);
		setOpaque(false);
	}

	public void mouseClicked(MouseEvent e) {
		boton.mouseClicked(e);
		etiqueta.mouseClicked(e);
	}

	public void mouseEntered(MouseEvent e) {
		seleccionado = true;
		boton.mouseEntered(e);
		etiqueta.mouseEntered(e);
	}

	public void mouseExited(MouseEvent e) {
		seleccionado = false;
		boton.mouseExited(e);
		etiqueta.mouseExited(e);
	}

	public void mousePressed(MouseEvent e) {
		boton.mousePressed(e);
		etiqueta.mousePressed(e);
	}

	public void mouseReleased(MouseEvent e) {
		boton.mouseReleased(e);
		etiqueta.mouseReleased(e);
	}
	
	public void redibujar(){
		boton.redibujar();
		etiqueta.redibujar();
	}
	
	static final Color c1 = new Color(0xFF,0xFF,0x00,0x80);
	static final Color c2 = new Color(0xFF,0xFF,0x00,0x40);
	
	public void paintComponent(Graphics g){
		if(seleccionado){
			Graphics2D g2= (Graphics2D)g;
			int ancho = getWidth();
			int alto = getHeight();
			g2.setPaint(new GradientPaint(0,0,c1,0,alto/2,c2,true));
			g2.fillRect(0,0,ancho,alto);
		}
	}
}
