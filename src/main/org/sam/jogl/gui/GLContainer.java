/* 
 * GLContainer.java
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

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.vecmath.Point2f;

/**
 * 
 */
public class GLContainer extends GLComponent{

	List<GLComponent> components;
	
	public GLContainer(){
		components = new ArrayList<GLComponent>();
	}
	
	public void add( GLComponent component){
		component.setPosition( x1 + component.x1, y1 + component.y1 );
		components.add( component );
	}
	
	public void remove( GLComponent component){
		components.remove( component );
	}
	
	public void removeAll(){
		components.clear();
	}
	
	public List<GLComponent> childs(){
		return components;
	}
	
	/* (non-Javadoc)
	 * @see org.sam.jogl.gui.GLComponent#setBounds(float, float, float, float)
	 */
	@Override
	public void setBounds( float x, float y, float w, float h ){
		float offX = x - x1;
		float offY = y - y1;
		setCorners( x, y, x + w, y + h );
		for( GLComponent c: components )
			c.setPosition( c.x1 + offX, c.y1 + offY );
	}
	
	/* (non-Javadoc)
	 * @see org.sam.jogl.gui.GLComponent#setPosition(float, float)
	 */
	@Override
	public void setPosition( float x, float y ){
		float offX = x - x1;
		float offY = y - y1;
		super.setPosition( x, y );
		for( GLComponent c: components )
			c.setPosition( c.x1 + offX, c.y1 + offY );
	}

	/* (non-Javadoc)
	 * @see org.sam.jogl.Dibujable#draw(javax.media.opengl.GL2)
	 */
	@Override
	public void draw( GL2 gl ){
		gl.glBegin( GL.GL_LINE_STRIP );
			gl.glColor3f( 1, 1, 1 );
			gl.glVertex2f( x1, y1 );
			gl.glVertex2f( x2, y1 );
			gl.glVertex2f( x2, y2 );
			gl.glVertex2f( x1, y2 );
			gl.glVertex2f( x1, y1 );
		gl.glEnd();
		gl.glBegin( GL.GL_LINE_STRIP );
			gl.glColor3f( 0.5f, 0.5f, 0.5f );
			gl.glVertex2f( x1 - 5, y1 - 5 );
			gl.glVertex2f( x2 + 5, y1 - 5 );
			gl.glVertex2f( x2 + 5, y2 + 5 );
			gl.glVertex2f( x1 - 5, y2 + 5 );
			gl.glVertex2f( x1 - 5, y1 - 5 );
		gl.glEnd();
		for( GLComponent c: components )
			c.draw( gl );
	}
	
}
