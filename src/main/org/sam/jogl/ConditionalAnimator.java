/* 
 * ConditionalAnimator.java
 * 
 * Copyright (c) 2012 Samuel Alfaro Jiménez <samuelalfaro at gmail dot com>.
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
package org.sam.jogl;

import javax.media.opengl.GLAutoDrawable;

import org.sam.util.ModificableBoolean;

/**
 * Clase que se encarga de animar un elemento {@code GLAutoDrawable} mientras se cumple una
 * determinada condición, que puede ser modicada en otra parte.
 */
public class ConditionalAnimator extends Thread{
	
	/** Elemento animado por este objeto.*/
	private final GLAutoDrawable drawable;
	/** Condición de salida del bucle.*/
	private final ModificableBoolean condition;
	/** Tiempo nanosegundos para dibujar cada frame. */
	private final long frame_time;

	/**
	 * Constructor que crea y asigna los valores de una hebra encargada de animar un determinado
	 * elemento {@code GLAutoDrawable}.
	 * 
	 * @param drawable Elemento animado.
	 * @param condition Condición de salida del bucle.
	 * @param fps Fotogramas por segundo esperados.
	 */
	public ConditionalAnimator( GLAutoDrawable drawable, ModificableBoolean condition, int fps ){
		this.drawable = drawable;
		this.condition = condition;
		this.frame_time = 1000000000 / fps;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run(){
		long tActual = System.nanoTime(), tAnterior;
		do{
			tAnterior = tActual;
			drawable.display();
			tActual = System.nanoTime();
			long tRender = tActual - tAnterior;
			if( tRender < frame_time ){
				long sleepNanos = frame_time - tRender;
				try{
					Thread.sleep( sleepNanos / 1000000, (int)( sleepNanos % 1000000 ) );
				}catch( InterruptedException ignorada ){
				}
			}
		}while( condition.isTrue() );
	}
}
