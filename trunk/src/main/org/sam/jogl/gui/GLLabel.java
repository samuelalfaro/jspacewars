/* 
 * GLButton.java
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

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

/**
 * 
 */
public class GLLabel extends GLComponent{
	
	protected void draw( GL2 gl, float rf, float gf, float bf, float rb, float gb, float bb ){
		gl.glBegin(GL2.GL_QUADS);
			gl.glColor3f  ( rf, gf, bf );
			gl.glVertex2f ( x1, y1 );
			gl.glVertex2f ( x2, y1 );
			gl.glVertex2f ( x2, y2 );
			gl.glVertex2f ( x1, y2 );
		gl.glEnd();
		gl.glBegin( GL.GL_LINE_STRIP );
			gl.glColor3f  ( rb, gb, bb );
			gl.glVertex2f ( x1, y1 );
			gl.glVertex2f ( x2, y1 );
			gl.glVertex2f ( x2, y2 );
			gl.glVertex2f ( x1, y2 );
			gl.glVertex2f ( x1, y1 );
		gl.glEnd();
	}

	/* (non-Javadoc)
	 * @see org.sam.jogl.Dibujable#draw(javax.media.opengl.GL2)
	 */
	@Override
	public void draw( GL2 gl ){
		if( !isEnabled() ){
			draw( gl, 0.25f, 0.25f, 0.25f, 0.5f, 0.5f, 0.5f ); 	// desactivado
		}
		draw( gl, 0.5f, 0.5f, 0.5f, 0.75f, 0.75f, 0.75f ); // default
	}

}
