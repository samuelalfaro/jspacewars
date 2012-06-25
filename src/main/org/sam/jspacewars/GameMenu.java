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
			actions.get( ( (GLButton)e.getSource() ).getActionName() ).run();
		}
	}

	

	
	private final GLButton player1, player2, server, client, options, quit;
	private final GLButton sound, graphics, network, back;
	
	public GameMenu( Map<String, ButtonAction> actions ){
		
		Locale locale = Locale.getDefault(); 
		ResourceBundle bundle = ResourceBundle.getBundle("org.sam.gui.translations.messages", locale);
		ResourceBundle basic = ResourceBundle.getBundle("com.sun.swing.internal.plaf.basic.resources.basic", locale);
		
		MyListener listener = new MyListener( actions );
		ButtonAction action;

		player1 = new GLButton( "player1", listener, bundle );

		player2 = new GLButton( "player2", listener, bundle );
		action = new ButtonAction( "player2" ){
			public void run(){
				buildTowPlayersMenu();
			}
		};
		actions.put( action.getName(), action );

		server = new GLButton( "server", listener, bundle );
		client = new GLButton( "client", listener, bundle );

		action = new ButtonAction( "options" ){
			public void run(){
				GameMenu.this.buildOptionsMenu();
			}
		};
		options = new GLButton( action.getName(), listener, bundle );
		actions.put( action.getName(), action );

		action = new ButtonAction( "sound" ){
			public void run(){
			}
		};
		sound = new GLButton( action.getName(), listener, bundle );
		actions.put( action.getName(), action );

		action = new ButtonAction( "graphics" ){
			public void run(){
			}
		};
		graphics = new GLButton( action.getName(), listener, bundle );
		actions.put( action.getName(), action );

		action = new ButtonAction( "network" ){
			public void run(){
			}
		};
		network = new GLButton( action.getName(), listener, bundle );
		actions.put( action.getName(), action );

		action = new ButtonAction( "back" ){
			public void run(){
				GameMenu.this.buildMainMenu();
			}
		};
		back = new GLButton( action.getName(), listener, bundle );
		actions.put( action.getName(), action );

		action = new ButtonAction( "quit" ){
			public void run(){
				//GameMenu.this.getDisplay().removeWidget( GameMenu.this );
				System.exit( 0 );
			}
		};
		quit = new GLButton( action.getName(), listener, bundle );
		actions.put( action.getName(), action );
	}

	private void buildMainMenu() {
		this.removeAll();
		this.add(player1);
		this.add(player2);
		this.add(options);
		this.add(quit);
	}

	private void buildTowPlayersMenu() {
		this.removeAll();
		this.add(server);
		this.add(client);
		this.add(back);
	}
	
	private void buildOptionsMenu() {
		this.removeAll();
		this.add(graphics);
		this.add(sound);
		this.add(network);
		this.add(back);
	}
}
