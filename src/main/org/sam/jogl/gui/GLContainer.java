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

import javax.media.opengl.GL2;

import org.sam.jogl.gui.event.ChangeEvent;
import org.sam.jogl.gui.event.ChangeListener;
import org.sam.jogl.gui.event.MouseEvent;
import org.sam.jogl.gui.event.MouseListener;

/**
 * 
 */
public class GLContainer extends GLComponent{

	private static final MouseListener ContainerMouseListener = new MouseListener(){

		@Override
		public void mouseMoved( MouseEvent e ){
			GLContainer source = (GLContainer)e.source;
			for( GLComponent c: source.childs() )
				if( c.isVisible() && c.isEnabled() ){
					if( c.contains( e.x, e.y ) ){
						if( !c.isHovered() )
							c.processMouseEvent( MouseEvent.MOUSE_ENTERED, e.x, e.y );
						else
							c.processMouseEvent( MouseEvent.MOUSE_MOVED, e.x, e.y );
					}else
						if( c.isHovered() )
							c.processMouseEvent( MouseEvent.MOUSE_EXITED, e.x, e.y );
				}
		}

		@Override
		public void mouseDragged( MouseEvent e ){
			GLContainer source = (GLContainer)e.source;
			for( GLComponent c: source.childs() )
				if( c.isVisible() && c.isEnabled() ){
					if( c.contains( e.x, e.y ) ){
						if( !c.isHovered() )
							c.processMouseEvent( MouseEvent.MOUSE_ENTERED, e.button, e.x, e.y );
						else
							c.processMouseEvent( MouseEvent.MOUSE_DRAGGED, e.button, e.x, e.y );
					}else
						if( c.isHovered() )
							c.processMouseEvent( MouseEvent.MOUSE_EXITED, e.button, e.x, e.y );
				}
		}

		@Override
		public void mousePressed( MouseEvent e ){
			GLContainer source = (GLContainer)e.source;
			for( GLComponent c: source.childs() )
				if( c.isVisible() && c.isEnabled() && c.contains( e.x, e.y ) )
					c.processMouseEvent( MouseEvent.MOUSE_PRESSED, e.button, e.x, e.y );
		}

		@Override
		public void mouseReleased( MouseEvent e ){
			GLContainer source = (GLContainer)e.source;
			for( GLComponent c: source.childs() )
				if( c.isVisible() && c.isEnabled() && c.contains( e.x, e.y ) )
					c.processMouseEvent( MouseEvent.MOUSE_RELEASED, e.button, e.x, e.y );
		}

		@Override
		public void mouseEntered( MouseEvent e ){
			GLContainer source = (GLContainer)e.source;
			source.setHovered( true );
			for( GLComponent c: source.childs() )
				if( c.isVisible() && c.isEnabled() && !c.isHovered() && c.contains( e.x, e.y ) )
					c.processMouseEvent( MouseEvent.MOUSE_ENTERED, e.x, e.y );
		}

		@Override
		public void mouseExited( MouseEvent e ){
			GLContainer source = (GLContainer)e.source;
			source.setHovered( false );
			for( GLComponent c: source.childs() )
				if(	c.isVisible() && c.isEnabled() && c.isHovered() )
					c.processMouseEvent( MouseEvent.MOUSE_EXITED, e.x, e.y );
		}
	};
	
	private static final ChangeListener ContainerChangeListener = new ChangeListener(){
		@Override
		public void stateChanged( ChangeEvent e ){
			GLContainer source = (GLContainer)e.source;
			if( !source.isEnabled() ){
				// desactivado
				source.setBackground( UIManager.getBackground( "Button.background.disabled" ) );
				source.setBorder( UIManager.getBorder( "Button.border.disabled" ) );
			}else{
				// default
				source.setBackground( UIManager.getBackground( "Button.background.default" ) );
				source.setBorder( UIManager.getBorder( "Button.border.default" ) );
			}
		}
	};
	
	private List<GLComponent> components;

	public GLContainer(){
		components = new ArrayList<GLComponent>();
		addMouseListener( ContainerMouseListener );
		//addChangeListener( ContainerChangeListener );
	}
	
	/* (non-Javadoc)
	 * @see org.sam.elementos.Initializable#init()
	 */
	@Override
	public void init(){
		this.setBackground( UIManager.getBackground( "Container.background.default" ) );
		this.setBorder( UIManager.getBorder( "Container.border.default" ) );
	}

	public void add( GLComponent component ){
		component.setParent( this );
		component.setPosition( x1 + component.x1, y1 + component.y1 );
		components.add( component );
	}

	public void remove( GLComponent component ){
		if( components.remove( component ) )
			component.setParent( null );
	}

	public void removeAll(){
		for( GLComponent c: components )
			c.setParent( null );
		components.clear();
	}

	public List<GLComponent> childs(){
		return components;
	}

	/*
	 * (non-Javadoc)
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

	/*
	 * (non-Javadoc)
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
	 * @see org.sam.jogl.gui.GLComponent#drawComponent(javax.media.opengl.GL2)
	 */
	@Override
	public void drawComponent( GL2 gl ){
		for( GLComponent c: components )
			c.draw( gl );
	}

}
