/* 
 * Modificador.java
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
package org.sam.elementos;

/**
 * Interface que proporciona el método, a las clases que lo implementen, para modificar
 * un determinado objeto {@link Modificable}. Esta objeto {@code Modificable} será el
 * responsable de instanciar y proporcionar su {@code Modificador} correspondiente.
 */
public interface Modificador {
	/**
	 * Método que se se encarga de modificar un objeto {@code Modificable} durante
	 * un periodo de tiempo determinado.
	 * 
	 * @param nanos periodo de tiempo en nanosegundos durante el cual se realiza la modificiación.
	 * @return <ul>
	 * <li>{@code true},  si se puede seguir modificando el objeto asociado.</li>
	 * <li>{@code false}, en caso contrario.</li>
	 * </ul>
	 */
	public boolean modificar( long nanos );
}
