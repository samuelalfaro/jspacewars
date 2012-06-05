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

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.vecmath.Point2f;

import org.sam.jogl.Dibujable;

public abstract class GLComponent extends GLRectangle{
	
	private int state;
	
	javax.swing.event.EventListenerList list;
	
	public GLComponent(){
		state = StateConstants.STATE_DEFAULT;
	}
	
	public final void setEnabled( boolean enabled ){
		if( !enabled )
			this.state = StateConstants.STATE_DISABLED;
		else
			this.state &= ~StateConstants.STATE_DISABLED;
	}
	
	public final boolean isEnabled(){
		return ( state & StateConstants.STATE_DISABLED ) == 0;
	}
	
	public final void setFocused( boolean focused ){
		if( focused )
			this.state |= StateConstants.STATE_FOCUSED;
		else
			this.state &= ~StateConstants.STATE_FOCUSED;
	}
	
	public final boolean isFocused(){
		return ( state & StateConstants.STATE_FOCUSED ) != 0;
	}
	
	public final void setHovered( boolean hovered ){
		if( hovered )
			this.state |= StateConstants.STATE_HOVERED;
		else
			this.state &= ~StateConstants.STATE_HOVERED;
	}
	
	public final boolean isHovered(){
		return ( state & StateConstants.STATE_HOVERED ) != 0;
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
	
	protected void processMouseEvent( MouseEvent e ){

	}

	protected void processMouseMotionEvent( MouseEvent e ){

	}

	protected void processMouseWheelEvent( MouseWheelEvent e ){

	}
}
