package org.sam.gui;

import java.awt.*;

@SuppressWarnings("serial")
public class Contador extends FloatComponent {
	
	private final Image img;
	private final transient int  imgW, imgH, digW;
	private int nDigitos;
	private int valor;
	
	public Contador(double x, double y, double ancho, double alto, Image img) {
		super(x, y, ancho, alto);
		this.img = img;
		imgW = img.getWidth(null);
		imgH = img.getHeight(null);
		digW = imgW/10;
	}
	
	public void setNDigitos(int nDigitos){
		this.nDigitos = (nDigitos < 1)? 1 : nDigitos;
	}
	
	public int getValor(){
		return this.valor;
	}
	
	public void setValor(int valor){
		this.valor = (valor < 0)? 0 : valor;
	}
	
	public void paintComponent(Graphics g){
		int ancho = getWidth()/nDigitos;
		int alto  = getHeight();
		
		int aux = valor;
		int digito = aux % 10;
		int cont = nDigitos;
		while( cont-- > 0 ){
			g.drawImage(img, cont * ancho, 0, (cont + 1) * ancho, alto, digito * digW, 0, (digito + 1) * digW, imgH, null);
			aux /= 10;
			digito = aux % 10;
		}
	}
}
