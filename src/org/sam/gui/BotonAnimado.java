package org.sam.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;

import org.sam.util.Imagen;

public final class BotonAnimado extends JComponent implements MouseListener{
	private static final long serialVersionUID = 1L;
	
	private int mouseOut;
	private int mouseIn;
	private int mouseClick;

	private Image[] imgs;
	private int frame;
	private int state;
	
	public static Image[] generarImagenes(Image imgOut, Image imgIn, Image imgClick, int frames){
		Image _imgs[] = new Image[frames + 3];
		_imgs[0] = imgOut;
		
		int ancho = imgOut.getWidth(null);
		int alto  = imgOut.getHeight(null); 
		int[] img1 = Imagen.toPixels(imgOut);
		int[] img2 = Imagen.toPixels(imgIn, ancho, alto);
		
		for(int i = 1; i <= frames; i++){
			int alfa = (i*0xFF)/(frames+1);
			_imgs[i] = Imagen.fusionar(img1,img2,ancho,alto,Imagen.MODO_SUP,alfa);
		}
		_imgs[frames+1] = imgIn;
		_imgs[frames+2] = imgClick;
		System.gc();
		return _imgs;
	}

	public BotonAnimado(Image[] _imgs){
		super();
		//addMouseListener(this);
		mouseOut = 0;
		mouseIn  = _imgs.length -2;
		mouseClick  = _imgs.length -1;
		
		frame = state = mouseOut;
		imgs = _imgs;
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
			g.drawImage(imgs[state],0,0,this.getWidth(),this.getHeight(),null);
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
