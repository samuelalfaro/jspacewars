/* 
 * GameMenu.java
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
package org.sam.jspacewars;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.sam.jogl.gui.ButtonAction;
import org.sam.jogl.gui.GLButton;
import org.sam.jogl.gui.GLComponent;
import org.sam.jogl.gui.GLContainer;
import org.sam.jogl.gui.event.ActionEvent;
import org.sam.jogl.gui.event.ActionListener;

public class GameMenu extends GLContainer {

	private static class MyListener implements ActionListener{
		
		final Map<String, ButtonAction> actions;

		MyListener( Map<String, ButtonAction> actions ){
			this.actions = actions;
		}

		/* (non-Javadoc)
		 * @see org.sam.jogl.gui.event.ActionListener#actionPerformed(org.sam.jogl.gui.event.ActionEvent)
		 */
		@Override
		public void actionPerformed( ActionEvent e ){
			actions.get( ( (GLButton)e.source ).getActionName() ).run();
		}
	}

	public GameMenu( Map<String, ButtonAction> actions ){
		
		Locale locale = Locale.getDefault(); 
		ResourceBundle bundle = null; //ResourceBundle.getBundle("org.sam.gui.translations.messages", locale);
		ResourceBundle basic = ResourceBundle.getBundle("com.sun.swing.internal.plaf.basic.resources.basic", locale);
		
		MyListener listener = new MyListener( actions );
		
		final GLComponent[] mainMenu = new GLComponent[] {
				new GLButton( "player1", bundle, listener ),
				new GLButton( "player2", bundle, listener ),
				new GLButton( "options", bundle, listener ),
				new GLButton( "quit", bundle, listener )
		};
		
		final GLComponent[] towPlayersMenu = new GLComponent[] {
				new GLButton( "server", bundle, listener ),
				new GLButton( "client", bundle, listener ),
				new GLButton( "back", bundle, listener ),
		};
		
		final GLComponent[] optionsMenu = new GLComponent[] {
				new GLButton( "sound", bundle, listener ),
				new GLButton( "graphics", bundle, listener ),
				new GLButton( "network", bundle, listener ),
				new GLButton( "back", bundle, listener ),
		};
		
		ButtonAction action;

		action = new ButtonAction( "player2" ){
			public void run(){
				GameMenu.this.buildMenu( towPlayersMenu, 300, 50, 20 );
			}
		};
		actions.put( action.getName(), action );
		
		action = new ButtonAction( "options" ){
			public void run(){
				GameMenu.this.buildMenu( optionsMenu, 300, 50, 20 );
			}
		};
		actions.put( action.getName(), action );
		
		action = new ButtonAction( "quit" ){
			public void run(){
				System.exit( 0 );
			}
		};
		actions.put( action.getName(), action );
		
		action = new ButtonAction( "back" ){
			public void run(){
				GameMenu.this.buildMenu( mainMenu, 300, 50, 20 );
			}
		};
		actions.put( action.getName(), action );
		
		action = new ButtonAction( "sound" ){
			public void run(){
			}
		};
		actions.put( action.getName(), action );

		action = new ButtonAction( "graphics" ){
			public void run(){
			}
		};
		actions.put( action.getName(), action );

		action = new ButtonAction( "network" ){
			public void run(){
			}
		};
		actions.put( action.getName(), action );
		
		buildMenu( mainMenu, 300, 50, 20 );
	}

	void buildMenu( GLComponent[] components, float buttonsWidth, float buttonsHeight, float padding ){
		if( components == null || components.length == 0 )
			return;
		this.removeAll();
		this.setBounds( 2 * padding + buttonsWidth, padding + components.length * ( padding + buttonsHeight ) );
		float posY = padding;
		for( GLComponent c: components ){
			c.setBounds( padding, posY, buttonsWidth, buttonsHeight );
			posY += padding + buttonsHeight;
			this.add( c );
		}
	}
}
