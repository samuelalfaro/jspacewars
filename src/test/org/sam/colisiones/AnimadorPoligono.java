/* 
 * AnimadorPoligono.java
 * 
 * Copyright (c) 2011 Samuel Alfaro Jiménez <samuelalfaro at gmail dot com>.
 * All rights reserved.
 * 
 * This file is part of jspacewars.
 * 
 * jspacewars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * jspacewars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with jspacewars.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sam.colisiones;

import java.awt.Component;

/**
 * Clase encargada de animar un polígono e invocar al repintado del componente donde será mostrado.
 */
class AnimadorPoligono extends Thread{

	private static final float PI2 = (float)( Math.PI * 2 );
	
	private final Component component;
	private final Poligono poligono;
	
	/**
	 * Constructor del animador.
	 * 
	 * @param component donde se dibujael poligónono.
	 * @param poligono dibujado.
	 */
	AnimadorPoligono( Component component, Poligono poligono ){
		this.component = component;
		this.poligono  = poligono;
	}

	public void run(){
		
		float alfa = 0.0f;
		float incAlfa = 0.0025f;
		
		while( true ){
			alfa += incAlfa;
			if( alfa > 1 ){
				alfa = 1;
				incAlfa = -incAlfa;
			}else if( alfa < 0 ){
				alfa = 0;
				incAlfa = -incAlfa;
			}
			poligono.transformar( PI2 * alfa, 0.5f - alfa, 0.0f, 0.0f );
			poligono.actualizarLimiteRectangular();
			component.repaint();
			try{
				Thread.sleep( 40 );
			}catch( InterruptedException e ){
			}
		}
	}
}