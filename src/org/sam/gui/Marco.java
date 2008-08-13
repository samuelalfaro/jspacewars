package org.sam.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.sam.util.Imagen;

/**
 * @author Samuel
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class Marco{
	
	@SuppressWarnings("serial")
	private static class Arriba extends JComponent{
		private final Image img;
		private Dimension dim;
		
		public Arriba(Image _img){
			img  = _img;
			dim = new Dimension();
			setLayout(new FloatLayout());
			
			Image numeros = Imagen.cargarImagen("resources/img/numeros.png");
			// vidas
			add(new Contador(17.0/256,2.0/128,3.0/128,3.0/128,3,1,numeros));
			// bombas
			add(new Contador(35.0/256,2.0/128,3.0/128,3.0/128,2,1,numeros));
			// puntos
			add(new Contador(7.0/256,7.0/128,17.0/128,3.0/128,14765,6,numeros));
			
			Image[] leds = new Image[4];
			leds[0]= Imagen.cargarImagen("resources/img/leds/bt00.png");
			leds[1]= Imagen.cargarImagen("resources/img/leds/bt01.png");
			leds[2]= Imagen.cargarImagen("resources/img/leds/bt02.png");
			leds[3]= Imagen.cargarImagen("resources/img/leds/bt03.png");
			
			Image[] indicadores = new Image[6];
			indicadores[0]= Imagen.cargarImagen("resources/img/indicadores/mr00.png");
			indicadores[1]= Imagen.cargarImagen("resources/img/indicadores/mr01.png");
			indicadores[2]= Imagen.cargarImagen("resources/img/indicadores/mr02.png");
			indicadores[3]= Imagen.cargarImagen("resources/img/indicadores/mr03.png");
			indicadores[4]= Imagen.cargarImagen("resources/img/indicadores/mr04.png");
			indicadores[5]= Imagen.cargarImagen("resources/img/indicadores/mr05.png");
			
			// velocidad
			int lvelocidad [] = {3,2,1,1,0};
			int ivelocidad [] = {2,5};
			add(new BarraLeds(24.0/128,15.0/256,14.0/128,3.0/256,1.0/21,0.2,lvelocidad,leds));
			add(new BarraLeds(24.0/128,19.0/256,14.0/128,2.0/128,1.0/21,0.1,ivelocidad,indicadores));
			// diparos numero
			int lnDisparos [] = {3,2,1,0,0};
			int inDisparos [] = {3,2};
			add(new BarraLeds(42.0/128,15.0/256,14.0/128,3.0/256,1.0/21,0.2,lnDisparos,leds));
			add(new BarraLeds(42.0/128,19.0/256,14.0/128,2.0/128,1.0/21,0.1,inDisparos,indicadores));
			// disparos potencia
			int lpDisparos [] = {2,1,1,0,0};
			int ipDisparos [] = {2,1};
			add(new BarraLeds(60.0/128,15.0/256,14.0/128,3.0/256,1.0/21,0.2,lpDisparos,leds));
			add(new BarraLeds(60.0/128,19.0/256,14.0/128,2.0/128,1.0/21,0.1,ipDisparos,indicadores));
			// misiles numero
			int lnMisiles [] = {2,1,0,0,0};
			int inMisiles [] = {2,1};
			add(new BarraLeds(78.0/128,15.0/256,14.0/128,3.0/256,1.0/21,0.2,lnMisiles,leds));
			add(new BarraLeds(78.0/128,19.0/256,14.0/128,2.0/128,1.0/21,0.1,inMisiles,indicadores));
			// misiles potencia
			int lpMisiles [] = {1,0,0,0,0};
			int ipMisiles [] = {1,0};
			add(new BarraLeds(96.0/128,15.0/256,14.0/128,3.0/256,1.0/21,0.2,lpMisiles,leds));
			add(new BarraLeds(96.0/128,19.0/256,14.0/128,2.0/128,1.0/21,0.1,ipMisiles,indicadores));
			// upgrade
			int lupgrade [] = {3,1,1};
			int iupgrade [] = {1};
			add(new BarraLeds(114.0/128,15.0/256,10.0/128,3.0/256,1.0/13,0.2,lupgrade,leds));
			add(new BarraLeds(114.0/128,19.0/256,10.0/128,2.0/128,1.0/21,0.1,iupgrade,indicadores));
		}
		
		public Dimension getMinimumSize(){
			int ancho = getParent().getWidth();
			int alto  = (ancho * 3)>>5;
			dim.setSize(ancho,alto);
			this.setSize(dim);
			return dim;
		}
		
		public Dimension getPreferredSize(){
			return getMinimumSize();
		}
		
		public void paintComponent(Graphics g){
			int ancho = (int)dim.getWidth();
			int alto  = (int)dim.getHeight();
			g.drawImage(img,0,0,ancho,alto,null);
		}
	}
	
	@SuppressWarnings("serial")
	private static class Lateral extends JComponent{
		private final Image img;
		private final int imgW; 
		private Dimension dim;
		
		public Lateral(Image _img){
			img  = _img;
			imgW = _img.getWidth(null);
			dim = new Dimension();
		}
		
		public Dimension getMinimumSize(){
			int ancho = getParent().getWidth();
			int alto  = getParent().getHeight() - (ancho >> 3);
			ancho = ancho >> 5;
			dim.setSize(ancho,alto);
			return dim;
		}
		
		public Dimension getPreferredSize(){
			return getMinimumSize();
		}
		
		public void paint(Graphics g){
			int ancho = (int)dim.getWidth();
			int alto  = (int)dim.getHeight();
			g.drawImage(img,0,0,ancho,alto,0,0,imgW,alto*imgW/ancho,null);
		}
	}
	
	@SuppressWarnings("serial")
	private static class Abajo extends JComponent{
		private final Image img;
		private Dimension dim;
		
		public Abajo(Image _img){
			img  = _img;
			dim = new Dimension();
		}
		
		public Dimension getMinimumSize(){
			int ancho = getParent().getWidth();
			int alto  = ancho >>5;
			dim.setSize(ancho,alto);
			return dim;
		}
		
		public Dimension getPreferredSize(){
			return getMinimumSize();
		}
		
		public void paint(Graphics g){
			int ancho = (int)dim.getWidth();
			int alto  = (int)dim.getHeight();
			g.drawImage(img,0,0,ancho,alto,null);
		}
	}
	
	public static JPanel getPanel(){
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new Arriba(Imagen.cargarImagen("resources/img/interface/if01.png")),BorderLayout.NORTH);
		panel.add(new Lateral(Imagen.cargarImagen("resources/img/interface/if02.png")),BorderLayout.WEST);
		panel.add(new Lateral(Imagen.cargarImagen("resources/img/interface/if03.png")),BorderLayout.EAST);
		panel.add(new Abajo(Imagen.cargarImagen("resources/img/interface/if04.png")),BorderLayout.SOUTH);
		System.gc();
		return panel;
	}
}
