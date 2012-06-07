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

import org.sam.jogl.gui.event.MouseAdapter;
import org.sam.jogl.gui.event.MouseEvent;

/**
 * 
 */
public class GLButton extends GLLabel{
	
	private class ButtonListener extends MouseAdapter{

		public void mouseEntered( MouseEvent e ){
			if( !isEnabled() )
				return;
			if( !isHovered() )
				setHovered( true );
		}

		public void mouseExited( MouseEvent e ){
			if( !isEnabled() )
				return;
			setHovered( false );
			setPressed( false );
		}

		public void mousePressed( MouseEvent e ){
			if( !isEnabled() )
				return;
			if( e.getButton() == MouseEvent.BUTTON1 ){
				setPressed( true );
			}
		}

		public void mouseReleased( MouseEvent e ){
			if( !isEnabled() )
				return;
			if( isPressed() && e.getButton() == MouseEvent.BUTTON1 ){
				setHovered( true );
				setFocused( true );
				setPressed( false );
				//fireActionPerformed( actionCommand );
			}
		}

		public void mouseDragged( MouseEvent e ){
			if( !isEnabled() )
				return;
			if( contains( e.getX(), e.getY() ) ){
				setHovered( true );
				setPressed( e.getButton() == MouseEvent.BUTTON1  );
			}else{
				setHovered( false );
				setPressed( false );
			}
		}
	}
	
	public GLButton( String text ){
		super( text );
		addMouseListener( new ButtonListener() );
	}

	public GLButton(){
		this("");
	}
	
	public final void setPressed( boolean pressed ){
		if( pressed )
			this.state |= StateConstants.STATE_PRESSED;
		else
			this.state &= ~StateConstants.STATE_PRESSED;
	}
	
	public final boolean isPressed(){
		return ( state & StateConstants.STATE_PRESSED ) != 0;
	}
	
	/* (non-Javadoc)
	 * @see org.sam.jogl.Dibujable#draw(javax.media.opengl.GL2)
	 */
	@Override
	public void draw( GL2 gl ){
		if( !isEnabled() ){
			draw( gl, 0.25f, 0.25f, 0.25f, 0.5f, 0.5f, 0.5f ); 	// desactivado
			TEXT_RENDERER.setColor( 0.5f, 0.5f, 0.5f,  1.0f );
		}else if( isPressed() ){
			draw( gl, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f ); // pulsado recibe el foco
			TEXT_RENDERER.setColor( 0.5f, 0.5f, 0.0f,  1.0f );
		}else if( isFocused() ){
			if( isHovered() ){
				draw( gl, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f ); // ratón encima foco
				TEXT_RENDERER.setColor( 0.5f, 0.5f, 0.0f,  1.0f );
			}else{
				draw( gl, 0.5f, 0.5f, 0.5f, 1.0f, 1.0f, 0.0f ); // default foco
				TEXT_RENDERER.setColor( 0.0f, 0.0f, 0.0f,  1.0f );
			}
		}else if( isHovered() ){
			draw( gl, 0.0f, 1.0f, 0.0f, 0.75f, 0.75f, 0.75f ); // ratón encima
			TEXT_RENDERER.setColor( 0.0f, 0.0f, 0.0f,  1.0f );
		}else{
			draw( gl, 0.5f, 0.5f, 0.5f, 0.75f, 0.75f, 0.75f ); // default
			TEXT_RENDERER.setColor( 0.0f, 0.0f, 0.0f,  1.0f );
		}
		TEXT_RENDERER.setHorizontalAlignment( horizontalAlignment );
		TEXT_RENDERER.setVerticalAlignment( verticalAlignment );
		TEXT_RENDERER.glPrint( gl, x1, (y1 + y2)/ 2, text );
	}

}
