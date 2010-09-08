/* 
 * ModificableBoolean.java
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
package org.sam.util;

/**
 * Clase que encapsula un dato primitivo de tipo {@code boolean}.</br>
 * La finalidad de esta clase es poder compartir un dato booleano entre distintos objetos.
 * Pudiendo consultarlo y modificarlo, ya que el valor encapsulado por calse {@link Boolean}
 * es {@code final} y no se puede cambiar.
 */
public class ModificableBoolean {
	
	private boolean value;
	
	/**
	 * Constructor que genera un {@code ModificableBoolean}. El valor inicial por defecto es {@code false}.
	 */
	public ModificableBoolean(){
		this(false);
	}
	
	/**
	 * Constructor que genera un {@code ModificableBoolean} inicializado
	 * con el valor que se pasa como parámetro.
	 * @param value {@code boolean} que contiene el valor inicial.
	 */
	public ModificableBoolean(boolean value){
		this.value = value;
	}
	
	/**
	 * @return <ul>
	 * <li>{@code true} si el valor encapuslado es {@code true}.</li>
	 * <li>{@code false} en caso contrario</li>
	 * </ul>
	 */
	public boolean isTrue(){
		return value;
	}
	
	/**
	 * @return <ul>
	 * <li>{@code true} si el valor encapuslado es {@code false}.</li>
	 * <li>{@code false} en caso contrario</li>
	 * </ul>
	 */
	public boolean isFalse(){
		return !value;
	}
	
	/**
	 * Método que asigna {@code true} al valor encapsulado.
	 */
	public void setTrue(){
		this.value = true;
	}
	
	/**
	 * Método que asigna {@code false} al valor encapsulado.
	 */
	public void setFalse(){
		this.value = false;
	}
}
