/*
 * Created on 03-ene-2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.sam.pruebas.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.sam.gui.BotonAnimado;
import org.sam.gui.EtiquetaAnimada;
import org.sam.util.Imagen;

public class PruebaBotones {
	
	public static void main(String arg[]){
		Image img = Imagen.cargarImagen("resources/img/botones/neon.jpg");
		
		int ancho = img.getWidth(null);
		int alto = img.getHeight(null);
		
		Image imgs[] = BotonAnimado.generarImagenes(
			Imagen.getRecorte(img, 0, 0, ancho, alto/3),
			Imagen.getRecorte(img, 0, 2*alto/3, ancho, alto/3),
			Imagen.getRecorte(img, 0, alto/3, ancho, alto/3),
			20);

		Color colores[] = EtiquetaAnimada.generarColores(
			Color.DARK_GRAY, Color.YELLOW, Color.RED, 20);
		
		JPanel p = new JPanel(){
			private static final long serialVersionUID = 1L;
			public void paintComponent(Graphics g){
				Graphics2D g2= (Graphics2D)g;
				int ancho = getWidth();
				int alto = getHeight();
				g2.setPaint(new GradientPaint(0,0,Color.DARK_GRAY,0,alto/2,Color.GRAY,true));
				g2.fillRect(0,0,ancho,alto);
			}
		};
		p.setOpaque(false);
		p.setLayout(new GridLayout(10,1,10,10));
		
		final UnPanelDePrueba paneles[] = new UnPanelDePrueba[10];
		
		GridLayout lpanel = new GridLayout(1,2,5,5);
		for(int i = 0; i< 10; i++){
			paneles[i] = new UnPanelDePrueba(
				lpanel,
				new BotonAnimado(imgs),
				new EtiquetaAnimada("Etiqueta ["+i+"]",colores));
			p.add(paneles[i]);
		}
		
		Thread animador = new Thread(){
			public void run(){
				while(true){
					for(int i= 0,t = paneles.length; i<t;i++)
						paneles[i].redibujar();
					try{
						Thread.sleep(20);
					}catch(InterruptedException e){
					}
				}
			}
		};
		
		JFrame frame = new JFrame("Prueba Botones");

		frame.setSize(810,600);
		frame.setDefaultCloseOperation(3);
			
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(p,BorderLayout.WEST);
		frame.setVisible(true);
		
		animador.start();
	}
}
