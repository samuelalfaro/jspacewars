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
public class Contador extends FloatComponent {
	private static final long serialVersionUID = 1L;

	private int valor;
	private int nDigitos;
	private Image img;
	private int  imgW, imgH, digW;

	public Contador(double x, double y, double ancho, double alto) {
		this(x, y, ancho, alto,0,1,null);
	}
	
	public Contador(double x, double y, double ancho, double alto,int _valor, int _nDigitos, Image _img) {
		super(x, y, ancho, alto);
		setValor( _valor); 
		setNDigitos( _nDigitos);
		setImage( _img);
	}
	
	public void setValor(int _valor){
		valor = (_valor < 0)? 0 : _valor;
	}
	
	public void setNDigitos(int _nDigitos){
		nDigitos = (_nDigitos < 1)? 1 : _nDigitos;
	}
	
	public void setImage(Image _img){
		img = _img;
		if (_img != null){
			imgW = _img.getWidth(null);
			imgH = _img.getHeight(null);
			digW = imgW/10;
		}
	}
	
	public void paintComponent(Graphics g){
		int ancho = getWidth()/nDigitos;
		int alto  = getHeight();
		
		int aux = valor;
		int digito = aux % 10;
		int cont = nDigitos;
		while(cont-- > 0){
			g.drawImage(img,cont*ancho,0,(cont+1)*ancho,alto,digito*digW,0,(digito+1)*digW,imgH,null);
			aux /= 10;
			digito = aux %10;
		}
	}
}
