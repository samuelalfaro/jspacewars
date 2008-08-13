package org.sam.pruebas.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.TexturePaint;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.sam.util.Tipografias;
import org.sam.util.Imagen;

public class PruebaFuentes{
	
	private static class PanelFuentes  extends JComponent{ 
		private static final long serialVersionUID = 1L;
		private TexturePaint texturaFondo, texturaTexto;
		private Font fuente;
		private String texto;
		
		public PanelFuentes(Image imgFondo, Image imgTexto, Font _fuente, String _texto){
			texturaFondo = Imagen.toTexturePaint(imgFondo);
			texturaTexto = Imagen.toTexturePaint(imgTexto);
			fuente  = _fuente;
			texto   = _texto;
		}
		
		public void paintComponent(Graphics g){
			Graphics2D g2 = (Graphics2D)g;
			g2.setPaint(texturaFondo);
			g2.fillRect(0,0,getWidth(),getHeight());
			g2.setFont(fuente);			
			FontMetrics fm = g2.getFontMetrics();
			int x = (getWidth() - fm.stringWidth(texto)) / 2;
			int y = (getHeight() - fm.getHeight())/2+fm.getMaxAscent();
			g2.setPaint(texturaTexto);
			g2.drawString(texto,x,y);
		}
	}

	static public void main(String args[]){

		Font font = Tipografias.load("resources/fonts/smelo.ttf", Font.PLAIN,100);
		System.out.println("Fuente usada: "+font);
		
		PanelFuentes p = new PanelFuentes(
			Imagen.cargarImagen("resources/img/texturas/oxido.jpg"),
			Imagen.cargarImagen("resources/img/texturas/piedra.jpg"),
			font, "Sex Pistols");
		
		JFrame frame = new JFrame("Prueba Fuentes");

		frame.setSize(500,200);
		frame.setDefaultCloseOperation(3);
		
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(p,BorderLayout.CENTER);
		frame.setVisible(true);
	}
}