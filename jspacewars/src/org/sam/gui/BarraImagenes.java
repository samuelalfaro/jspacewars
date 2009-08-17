package org.sam.gui;

import java.awt.*;

@SuppressWarnings("serial")
public class BarraImagenes extends FloatComponent {
	
	private final Image[] imgs;
	private final int[] valores;
	private double bordeH, bordeV;
	
	public BarraImagenes(double x, double y, double ancho, double alto, double _bordeH, double _bordeV, int nValores, Image _imgs[]) throws IllegalArgumentException{
		super(x, y, ancho, alto);
		if( nValores < 1 )
			throw new IllegalArgumentException();
		valores = new int[nValores];

		bordeH = (_bordeH < 0 || _bordeH > (1.0/(nValores +1)))? 0.0 : _bordeH;	
		bordeV = (_bordeV < 0 || _bordeV > .5) ? 0.0 : _bordeV;
		imgs = _imgs;
	}
	
	public BarraImagenes(double x, double y, double ancho, double alto, int nValores, Image _imgs[]) throws IllegalArgumentException{
		this(x, y, ancho, alto, 0.0, 0.0, nValores, _imgs);
	}

	public int getNValues(){
		return valores.length;
	}

	public int getValueAt(int index){
		return valores[index];
	}
	
	public void setValueAt(int index, int value){
		valores[index] = value;
	}
	
	public void paintComponent(Graphics g){
		int nImgs = valores.length;
		int ancho = getWidth();
		int anchoBorde = (int)(bordeH * ancho);
		int anchoImg = ( ancho - (int)(ancho * bordeH * (nImgs + 1)) ) / nImgs;
		int alto = getHeight();
		int altoBorde = (int) (bordeV * alto);
		int altoImg = alto - (int) (alto * 2 * bordeV);

		int cont = 0;
		int x = anchoBorde;
		int dx = anchoBorde + anchoImg;
		while( cont < nImgs ){
			try{
				g.drawImage(imgs[valores[cont]], x, altoBorde, anchoImg, altoImg, null);
			}catch( ArrayIndexOutOfBoundsException e ){
				System.err.println(e);
			}
			cont++;
			x += dx;
		}
	}
}
