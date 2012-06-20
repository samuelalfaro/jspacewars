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

import org.sam.jogl.gui.event.MouseAdapter;
import org.sam.jogl.gui.event.MouseEvent;

/**
 * 
 */
public class GLButton extends GLLabel{
	
	private class ButtonListener extends MouseAdapter{

		public void mouseEntered( MouseEvent e ){
			System.err.println( isHovered() +" " +getText() + " mouseEntered " + e.getX() + " " + e.getY() );
			if( !isEnabled() )
				return;
			setHovered( true );
		}

		public void mouseExited( MouseEvent e ){
			System.err.println( isHovered() +" " +getText() + " mouseExited " + e.getX() + " " + e.getY() );
			if( !isEnabled() )
				return;
			setHovered( false );
			setPressed( false );
		}

		public void mousePressed( MouseEvent e ){
			System.err.println(  getText() + " mousePressed " );
			if( !isEnabled() )
				return;
			if( e.getButton() == MouseEvent.BUTTON1 ){
			//	setHovered( true );
				setPressed( true );
			}
		}

		public void mouseReleased( MouseEvent e ){
			System.err.println(  getText() + " mouseReleased " );
			if( !isEnabled() )
				return;
			if( isPressed() && e.getButton() == MouseEvent.BUTTON1 ){
				setPressed( false );
				//fireActionPerformed( actionCommand );
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

	protected void changeState( int oldState, int newState ){
		if( !isEnabled() ){
			// desactivado
			this.setBackground( UIManager.getBackground( "Button.background.disabled" ) );
			this.setBorder( UIManager.getBorder( "Button.border.disabled" ) );
			this.setTextRendererProperties( UIManager.getTextRendererProperties( "Button.properties.disabled" ) );
		}else if( isPressed() ){
			// pulsado recibe el foco
			this.setBackground( UIManager.getBackground( "Button.background.pressed" ) );
			this.setBorder( UIManager.getBorder( "Button.border.pressed" ) );
			this.setTextRendererProperties( UIManager.getTextRendererProperties( "Button.properties.pressed" ) );
		}else if( isFocused() ){
			if( isHovered() ){
				// ratón encima foco
				this.setBackground( UIManager.getBackground( "Button.background.hovered" ) );
				this.setBorder( UIManager.getBorder( "Button.border.hovered" ) );
				this.setTextRendererProperties( UIManager.getTextRendererProperties( "Button.properties.hovered" ) );
			}else{
				// default foco
				this.setBackground( UIManager.getBackground( "Button.background.focused" ) );
				this.setBorder( UIManager.getBorder( "Button.border.focused" ) );
				this.setTextRendererProperties( UIManager.getTextRendererProperties( "Button.properties.focused" ) );
			}
		}else if( isHovered() ){
			// ratón encima
			this.setBackground( UIManager.getBackground( "Button.background.hovered" ) );
			this.setBorder( UIManager.getBorder( "Button.border.hovered" ) );
			this.setTextRendererProperties( UIManager.getTextRendererProperties( "Button.properties.hovered" ) );
		}else{
			// default
			this.setBackground( UIManager.getBackground( "Button.background.default" ) );
			this.setBorder( UIManager.getBorder( "Button.border.default" ) );
			this.setTextRendererProperties( UIManager.getTextRendererProperties( "Button.properties.default" ) );
		}
	}
	
	public final void setPressed( boolean pressed ){
		setStateBit( pressed, StateConstants.STATE_PRESSED );
	}
	
	public final boolean isPressed(){
		return ( state & StateConstants.STATE_PRESSED ) != 0;
	}
	
	protected void init(){
		if( initialized )
			return;
		initialized = true;
		
		this.setBackground( UIManager.getBackground( "Button.background.default" ) );
		this.setBorder( UIManager.getBorder( "Button.border.default" ) );
		this.setTextRendererProperties( UIManager.getTextRendererProperties( "Button.properties.default" ) );
	}
}
