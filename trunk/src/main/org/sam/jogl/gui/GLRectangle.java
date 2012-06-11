/* 
 * GLComponent.java
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
package org.sam.jogl.gui;

import javax.vecmath.Point2f;

import org.sam.jogl.Dibujable;

public abstract class GLRectangle implements Dibujable{
	
	 /** Coordenada X menor de una de las esquinas del rectángulo. */
	 protected float x1;
	 /** Coordenada Y menor de una de las esquinas del rectángulo. */
	 protected float y1;
	 /** Coordenada X de la esquina opuesta. */
	 protected float x2;
	 /** Coordenada Y de la esquina opuesta. */
	 protected float y2;
	
	public GLRectangle(){
	}
	
	public final void setBounds( float w, float h ){
		setCorners( 0, 0, w, h );
	}
	
	public void setBounds( float x, float y, float w, float h ){
		setCorners( x, y, x + w, y + h );
	}
	
	public void setPosition( float x, float y ){
		setCorners( x, y, x2 + x - x1, y2 + y - y1 );
	}
	
	public final void setCorners( float x1, float y1, float x2, float y2 ){
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	public final float getWidth(){
		return x2 - x1;
	}
	
	public final float getHeight(){
		return y2 - y1;
	}
	
	public final boolean contains( Point2f p ){
		return contains( p.x, p.y );
	}
	
	public final boolean contains( float x, float y ){
		return ( x1 <= x && x <= x2 && y1 <= y && y <= y2 );
	}
}
