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

import java.util.ResourceBundle;

import org.sam.jogl.gui.GLTextRenderer.HorizontalAlignment;
import org.sam.jogl.gui.GLTextRenderer.VerticalAlignment;
import org.sam.jogl.gui.event.ActionEvent;
import org.sam.jogl.gui.event.ActionListener;
import org.sam.jogl.gui.event.ChangeEvent;
import org.sam.jogl.gui.event.ChangeListener;
import org.sam.jogl.gui.event.MouseAdapter;
import org.sam.jogl.gui.event.MouseEvent;
import org.sam.jogl.gui.event.MouseListener;

public class GLButton extends GLLabel{
	
	private static final MouseListener ButtonMouseListener = new MouseAdapter(){

		public void mouseEntered( MouseEvent e ){
			((GLButton)e.getSource()).setHovered( true );
		}

		public void mouseExited( MouseEvent e ){
			GLButton source = (GLButton)e.getSource();
			source.setHovered( false );
			source.setPressed( false );
		}

		public void mousePressed( MouseEvent e ){
			if( e.getButton() == MouseEvent.BUTTON1 )
				((GLButton)e.getSource()).setPressed( true );
		}

		public void mouseReleased( MouseEvent e ){
			GLButton source = (GLButton)e.getSource();
			if( source.isPressed() && e.getButton() == MouseEvent.BUTTON1 ){
				source.setPressed( false );
				source.fireActionPerformed( source.getActionName() );
			}
		}
	};
	
	private static final ChangeListener ButtonChangeListener = new ChangeListener(){

		@Override
		public void stateChanged( ChangeEvent e ){
			GLButton source = (GLButton)e.getSource();
			if( !source.isEnabled() ){
				// desactivado
				source.setBackground( UIManager.getBackground( "Button.background.disabled" ) );
				source.setBorder( UIManager.getBorder( "Button.border.disabled" ) );
				source.setTextRendererProperties( UIManager.getTextRendererProperties( "Button.properties.disabled" ) );
			}else if( source.isPressed() ){
				// pulsado recibe el foco
				source.setBackground( UIManager.getBackground( "Button.background.pressed" ) );
				source.setBorder( UIManager.getBorder( "Button.border.pressed" ) );
				source.setTextRendererProperties( UIManager.getTextRendererProperties( "Button.properties.pressed" ) );
			}else if( source.isFocused() ){
				if( source.isHovered() ){
					// ratón encima foco
					source.setBackground( UIManager.getBackground( "Button.background.hovered" ) );
					source.setBorder( UIManager.getBorder( "Button.border.hovered" ) );
					source.setTextRendererProperties( UIManager.getTextRendererProperties( "Button.properties.hovered" ) );
				}else{
					// default foco
					source.setBackground( UIManager.getBackground( "Button.background.focused" ) );
					source.setBorder( UIManager.getBorder( "Button.border.focused" ) );
					source.setTextRendererProperties( UIManager.getTextRendererProperties( "Button.properties.focused" ) );
				}
			}else if( source.isHovered() ){
				// ratón encima
				source.setBackground( UIManager.getBackground( "Button.background.hovered" ) );
				source.setBorder( UIManager.getBorder( "Button.border.hovered" ) );
				source.setTextRendererProperties( UIManager.getTextRendererProperties( "Button.properties.hovered" ) );
			}else{
				// default
				source.setBackground( UIManager.getBackground( "Button.background.default" ) );
				source.setBorder( UIManager.getBorder( "Button.border.default" ) );
				source.setTextRendererProperties( UIManager.getTextRendererProperties( "Button.properties.default" ) );
			}
		}
	};
	
	private String actionName;
	
	public GLButton( String actionName, ResourceBundle bundle, ActionListener al ){
		super( bundle != null ? bundle.getString( actionName ) : actionName, HorizontalAlignment.CENTER, VerticalAlignment.CENTER );
		this.actionName = actionName;
		addMouseListener( ButtonMouseListener );
		addChangeListener( ButtonChangeListener );
		if( al != null )
			this.addActionListener( al );
	}
	
	public GLButton( String actionName, ActionListener al ){
		this( actionName, null, al );
	}
	
	public GLButton( String actionName, ResourceBundle bundle ){
		this( actionName, bundle, null );
	}
	
	public GLButton( String actionName ){
		this( actionName, null, null );
	}
	
	public GLButton( ButtonAction action, ResourceBundle bundle ){
		this( action.getName(), bundle, action );
	}
	
	public GLButton( ButtonAction action ){
		this( action.getName(), null, action );
	}

	protected void initComponent(){
		this.setBackground( UIManager.getBackground( "Button.background.default" ) );
		this.setBorder( UIManager.getBorder( "Button.border.default" ) );
		this.setTextRendererProperties( UIManager.getTextRendererProperties( "Button.properties.default" ) );
	}
	
	public final void setPressed( boolean pressed ){
		setStateBit( pressed, StateConstants.STATE_PRESSED );
	}
	
	public final boolean isPressed(){
		return ( state & StateConstants.STATE_PRESSED ) != 0;
	}
	
	 public String getActionName(){
		 if( actionName == null )
			 return this.getText();
		 return actionName;
	 }
	
	/**
	 * Adds the specified listener to the list.
	 * 
	 * @param s the listener to receive events
	 */
	public synchronized void addActionListener( ActionListener s ){
		listenerList.add( ActionListener.class, s );
	}

	/**
	 * Removes the specified listener from the list.
	 * 
	 * @param s the listener to remove
	 */
	public synchronized void removeActionListener( ActionListener s ){
		listenerList.remove( ActionListener.class, s );
	}
	
	/**
	 * Fires an action event with the specified command
	 * to all action listeners registered with this component.
	 * 
	 * @param command the action command for the event
	 */
	protected void fireActionPerformed( String command ){
		ActionListener[] listeners = listenerList.getListeners( ActionListener.class );
		if( listeners.length > 0 ){
			ActionEvent evt = new ActionEvent( this, command );
			for( ActionListener listener: listeners )
				listener.actionPerformed( evt );
		}
	}

}
