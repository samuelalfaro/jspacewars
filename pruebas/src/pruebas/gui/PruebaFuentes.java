/* 
 * PruebaFuentes.java
 * 
 * Copyright (c) 2008-2010
 * Samuel Alfaro Jiménez <samuelalfaro at gmail.com>.
 * All rights reserved.
 * 
 * This file is part of jSpaceWars.
 * 
 * jSpaceWars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * jSpaceWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with jSpaceWars. If not, see <http://www.gnu.org/licenses/>.
 */
package pruebas.gui;

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
