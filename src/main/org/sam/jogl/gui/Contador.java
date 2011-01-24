/* 
 * Contador.java
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
