/* 
 * Prototipo.java
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
 * Interface que define que los objetos de las clases que lo implementan, pueden clonarse,
 * generando nuevas instancias del tipo genérico {@code T}.
 * @param <T> Tipo genérico de los objetos devueltos por el método {@link #clone()}.
 */
public interface Prototipo<T> extends Cloneable{
	/**
	 * Método que devuelve una nueva instancia de {@code T}, clonada a partir del {@code Prototipo<T>} que lo invoca.
	 * @return la nueva instancia clonada.
	 */
	public T clone();
}
