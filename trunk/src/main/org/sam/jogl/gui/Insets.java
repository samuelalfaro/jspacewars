/* 
 * Insets.java
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
public class Insets{
	
	public final float left;
	public final float top;
	public final float right;
	public final float bottom;
	
	public Insets(){
		this( 0.0f, 0.0f, 0.0f, 0.0f );
	}
	
	public Insets( float insets ){
		this( insets, insets, insets, insets );
	}
	
	public Insets( float horizontal, float vertical ){
		this( horizontal, vertical, horizontal, vertical );
	}
	
	public Insets( float left, float top, float right, float bottom ){
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}
	
}
