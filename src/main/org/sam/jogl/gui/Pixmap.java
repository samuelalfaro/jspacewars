/* 
 * Pixmap.java
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
/**
 * 
 */
package org.sam.jogl.gui;

import javax.media.opengl.GL2;

public class Pixmap{
	/**
	 * Esquinas de las coordenadas de textura.
	 */
	public float u1, v1, u2, v2;

	public Pixmap(){
		this( 0, 0, 1, 1 );
	}

	public Pixmap( float x, float y, float w, float h ){
		u1 = x;
		v1 = y;
		u2 = x + w;
		v2 = y + h;
	}

	public void draw( GL2 gl, float x1, float y1, float x2, float y2 ){
		gl.glBegin( GL2.GL_QUADS );
			gl.glTexCoord2f( u1, v1 );
			gl.glVertex2f  ( x1, y1 );
			gl.glTexCoord2f( u2, v1 );
			gl.glVertex2f  ( x2, y1 );
			gl.glTexCoord2f( u2, v2 );
			gl.glVertex2f  ( x2, y2 );
			gl.glTexCoord2f( u1, v2 );
			gl.glVertex2f  ( x1, y2 );
		gl.glEnd();
	}
}
