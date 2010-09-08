/* 
 * KeysState.java
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

public interface KeysState {
	public static final int SUBE    = (1 << 0);
	public static final int BAJA    = (1 << 1);
	public static final int ACELERA = (1 << 2);
	public static final int FRENA   = (1 << 3);
	public static final int DISPARO = (1 << 4);
	public static final int BOMBA   = (1 << 5);
	public static final int UPGRADE = (1 << 6);
}
