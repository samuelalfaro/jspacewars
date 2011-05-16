/* 
 * Colisionable.java
 * 
 * Copyright (c) 2008-2010
 * Samuel Alfaro Jiménez <samuelalfaro at gmail dot com>.
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
package org.sam.colisiones;

/**
 * Interface que proporciona los métodos para tratar las colisones entre objetos.
 */
public interface Colisionable{
	
	/**
	 * @return Los {@code Limites} del objeto solicitados.
	 */
	public Limites getLimites();
	
	/**
	 * Este método evalúa si hay una colisión, entre el objeto que lo invoca y otro objeto {@code Colisionable}.
	 * 
	 * @param otro El otro objeto a evaluar.
	 * 
	 * @return <ul><li>{@code true}, cuando hay una colisión. <li>{@code false}, en caso contrario.</ul>
	 */
	public boolean hayColision(Colisionable otro);
	
	/**
	 * Este método trata la colisión, entre el objeto que lo invoca y otro objeto  {@code Colisionable}.
	 * 
	 * @param otro El otro objeto con el que se produce la colisión.
	 */
	public void colisionar(Colisionable otro);
}