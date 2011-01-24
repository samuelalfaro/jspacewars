/* 
 * SingletonObjetivos.java
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
package org.sam.jspacewars.servidor.elementos;

public final class SingletonObjetivos {
	
	private SingletonObjetivos() {
	}

	private static NaveEnemiga objetivoProtas;
	private static NaveUsuario objetivoEnemigos;

	public static Elemento getObjetivo(Nave me) {
		return me instanceof NaveUsuario ? objetivoProtas : objetivoEnemigos;
	}
	
	@Deprecated
	public static Elemento  getObjetivo(){
		return objetivoProtas;
	}
	
	@Deprecated
	public static void setObjetivoUsuario(NaveEnemiga objetivo) {
		SingletonObjetivos.objetivoProtas = objetivo;
	}
	
	@Deprecated
	public static void setObjetivoEnemigos(NaveUsuario objetivo) {
		SingletonObjetivos.objetivoEnemigos = objetivo;
	}
}
