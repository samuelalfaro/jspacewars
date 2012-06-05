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

import javax.media.opengl.GL2;

/**
 * 
 */
public class GLButton extends GLLabel{
	
	/* (non-Javadoc)
	 * @see org.sam.jogl.Dibujable#draw(javax.media.opengl.GL2)
	 */
	@Override
	public void draw( GL2 gl ){
		if( !isEnabled() ){
			draw( gl, 0.25f, 0.25f, 0.25f, 0.5f, 0.5f, 0.5f ); 	// desactivado
		}else if( isPressed() ){
			draw( gl, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f ); // pulsado recibe el foco
		}else if( isFocused() ){
			if( isHovered() )
				draw( gl, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f ); // ratón encima foco
			else
				draw( gl, 0.5f, 0.5f, 0.5f, 1.0f, 1.0f, 0.0f ); // default foco
		}else if( isHovered() ){
			draw( gl, 0.0f, 1.0f, 0.0f, 0.75f, 0.75f, 0.75f ); // ratón encima
		}else
			draw( gl, 0.5f, 0.5f, 0.5f, 0.75f, 0.75f, 0.75f ); // default
	}

}
