package org.sam.jogl.gui;

public class Contador extends BarraImagenes{
	private int valor;
	
	public Contador(int nDigitos, Pixmap[] pixmaps){
		super(nDigitos, pixmaps);
	}
	
	public int getValor(){
		return this.valor;
	}
	
	public void setValor(int valor){
		if(this.valor == valor)
			return;
		
		this.valor = (valor < 0)? 0 : valor;
		int i = getNValues();
		int v = this.valor;
		while( i > 0 && v > 0 ){
			int d = v % 10;
			this.setValueAt(--i, d);
			v /= 10;
		}
		while( i > 0 )
			this.setValueAt(--i, 0);
	}
}