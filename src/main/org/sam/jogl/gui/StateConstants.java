/* 
 * StateConstants.java
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
package org.sam.jogl.gui;

/**
 * 
 */
public interface StateConstants{

	public static final int STATE_DISABLED = 0x10;
	
	public static final int STATE_DEFAULT  = 0x00;
	public static final int STATE_FOCUSED  = 0x01;
	public static final int STATE_HOVERED  = 0x02;
	public static final int STATE_PRESSED  = 0x04;
	
}
