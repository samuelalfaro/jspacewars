/* 
 * PrototipoCacheable.java
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
 * Interface que propociona los métodos a las clases que lo implementan, para que los objetos
 * de dichas clases, puedan almacenarse y recuperarse de una {@code Cache}.
 * @param <T> Tipo genérico de los objetos devueltos por el método {@link Prototipo#clone()}.
 */
public interface PrototipoCacheable<T> extends Prototipo<T>, Reseteable{
	
	/**
	 * Devuelve un valor de un código hash para este objeto.
	 * 
	 * @return El valor del código hash solicitado.
	 */
	@Override
	public int hashCode();
}
