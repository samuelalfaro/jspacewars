/*
 * Created on 26-dic-2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.sam.gui;

import java.awt.*;

/**
 * @author Samuel
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class BarraLeds extends FloatComponent {
	private static final long serialVersionUID = 1L;
	
	private final Image[] imgs;
	private final int[] valores;
	private double bordeH, bordeV;
	
	public BarraLeds(double x, double y, double ancho, double alto,double _bordeH, double _bordeV, int _valores[], Image _imgs[]) throws IllegalArgumentException{
		super(x, y, ancho, alto);
		try{
			if(_valores.length <1)
				throw new IllegalArgumentException();
			valores = _valores;
		}catch(NullPointerException e){
			throw new IllegalArgumentException();
		}
		bordeH = (_bordeH < 0 || _bordeH > (1.0/(_valores.length +1)))? 0.0 : _bordeH;	
		bordeV = (_bordeV < 0 || _bordeV > .5) ? 0.0 : _bordeV;
		imgs = _imgs;
	}
	
	public BarraLeds(double x, double y, double ancho, double alto, int _valores[], Image _imgs[]) throws IllegalArgumentException{
		this(x, y, ancho, alto, 0.0, 0.0, _valores, _imgs);
	}

	public int[] getValores(){
		return valores;
	}
	
	public void paintComponent(Graphics g){
		int nLeds = valores.length;
		int ancho = getWidth();
		int anchoBorde = (int)(bordeH*ancho);
		int anchoLed = (ancho - (int)(ancho*bordeH*(nLeds+1)))/nLeds;
		int alto  = getHeight();
		int altoBorde = (int)(bordeV*alto);
		int altoLed = alto - (int)(alto*2*bordeV);
		
		int cont = 0;
		int x = anchoBorde;
		int dx = anchoBorde + anchoLed;
		while(cont < nLeds){
			try{
				g.drawImage(imgs[valores[cont]],x,altoBorde,anchoLed,altoLed,null);
			}catch(ArrayIndexOutOfBoundsException e){
			}
			cont++;
			x+=dx;
		}
	}
}
