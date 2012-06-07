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

import javax.swing.event.EventListenerList;

import org.sam.jogl.Apariencia;
import org.sam.jogl.AtributosTransparencia;
import org.sam.jogl.gui.event.MouseEvent;
import org.sam.jogl.gui.event.MouseListener;
import org.sam.jogl.gui.event.MouseWheelEvent;
import org.sam.jogl.gui.event.MouseWheelListener;

public abstract class GLComponent extends GLRectangle{
	
	protected static final GLTextRenderer TEXT_RENDERER = new GLTextRenderer();
	
	protected static final Apariencia apariencia = new Apariencia();
	
	static{
		apariencia.setAtributosTransparencia( 
				new AtributosTransparencia( 
						AtributosTransparencia.Equation.ADD,
						AtributosTransparencia.SrcFunc.SRC_ALPHA,
						AtributosTransparencia.DstFunc.ONE_MINUS_SRC_ALPHA
				)
		);
	}
	
	protected int state;
	protected EventListenerList listenerList = new EventListenerList();
	
	protected float leftPadding;
	protected float topPadding;
	protected float rightPadding;
	protected float bottomPadding;
	
	public GLComponent(){
		state = StateConstants.STATE_DEFAULT;
	}
	
	public final void setPadding( float padding ){
		setPadding( padding, padding, padding, padding );
	}
	
	public final void setPadding( float horizontalPadding, float verticalPadding ){
		setPadding( horizontalPadding, verticalPadding, horizontalPadding, verticalPadding );
	}
	
	public void setPadding( float leftPadding, float topPadding, float rightPadding, float bottomPadding ){
		this.leftPadding = leftPadding;
		this.topPadding = topPadding;
		this.rightPadding = rightPadding;
		this.bottomPadding = bottomPadding;
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
	
	/**
	 * Adds the specified listener to the list.
	 * 
	 * @param s the listener to receive events
	 */
	public synchronized void addMouseListener( MouseListener s ){
		listenerList.add( MouseListener.class, s );
	}

	/**
	 * Removes the specified listener from the list.
	 * 
	 * @param s the listener to remove
	 */
	public synchronized void removeMouseListener( MouseListener s ){
		listenerList.remove( MouseListener.class, s );
	}

	/**
	 * Adds the specified listener to the list.
	 * 
	 * @param s the listener to receive events
	 */
	public synchronized void addMouseWheelListener( MouseWheelListener s ){
		listenerList.add( MouseWheelListener.class, s );
	}

	/**
	 * Removes the specified listener from the list.
	 * 
	 * @param s the listener to remove
	 */
	public synchronized void removeMouseWheelListener( MouseWheelListener s ){
		listenerList.remove( MouseWheelListener.class, s );
	}
	
	protected void processMouseEvent( int id, int button, float x, float y ){
		MouseListener[] listeners = listenerList.getListeners( MouseListener.class );
		if( listeners.length > 0 ){
			MouseEvent evt = new MouseEvent( this, id, button, x, y );
			switch( id ){
			case MouseEvent.MOUSE_MOVED:
				for( MouseListener listener: listeners )
					listener.mouseMoved( evt );
				break;
			case MouseEvent.MOUSE_PRESSED:
				for( MouseListener listener: listeners )
					listener.mousePressed( evt );
				break;
			case MouseEvent.MOUSE_RELEASED:
				for( MouseListener listener: listeners )
					listener.mouseReleased( evt );
				break;
			case MouseEvent.MOUSE_DRAGGED:
				for( MouseListener listener: listeners )
					listener.mouseDragged( evt );
				break;
			case MouseEvent.MOUSE_ENTERED:
				for( MouseListener listener: listeners )
					listener.mouseEntered( evt );
				break;
			case MouseEvent.MOUSE_EXITED:
				for( MouseListener listener: listeners )
					listener.mouseExited( evt );
				break;
			}
		}
	}

	protected void processMouseEvent( int id, float x, float y ){
		processMouseEvent( id, MouseEvent.NOBUTTON, x, y );
	}

	protected void processMouseWheelEvent( int change ){
		MouseWheelListener[] listeners = listenerList.getListeners( MouseWheelListener.class );
		if( listeners.length > 0 ){
			MouseWheelEvent evt = new MouseWheelEvent( this, change );
			for( MouseWheelListener listener: listeners )
				listener.mouseWheelMoved( evt );
		}
	}

}
