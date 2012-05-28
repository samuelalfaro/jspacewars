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

import java.util.Map;

import org.sam.jogl.gui.ButtonAction;
import org.sam.jogl.gui.GLContainer;

public class GameMenu extends GLContainer {

//	private static class MyListener implements IButtonPressedListener{
//		final Map<String, ButtonAction> actions;
//
//		MyListener( Map<String, ButtonAction> actions ){
//			this.actions = actions;
//		}
//
//		/* (non-Javadoc)
//		 * @see org.fenggui.event.IButtonPressedListener#buttonPressed(java.lang.Object, org.fenggui.event.ButtonPressedEvent)
//		 */
//		@Override
//		public void buttonPressed( Object source, ButtonPressedEvent e ){
//			System.out.println( ((MyGameMenuButton)source ).getName());
//			System.out.println( actions.get( ( (MyGameMenuButton)source ).getName() ) );
//			actions.get( ( (MyGameMenuButton)source ).getName() ).run();
//		}
//	}

//	private final MyGameMenuButton player1, player2, server, client, options, quit;
//	private final MyGameMenuButton sound, graphics, network, back;
	private MyGameMenuButton player1, player2, server, client, options, quit;
	private MyGameMenuButton sound, graphics, network, back;
	
	public GameMenu( Map<String, ButtonAction> actions ){
//		MyListener listener = new MyListener(actions);
//
//		this.setLayoutManager(new RowLayout(false));
//		this.getAppearance().add(
//				new GradientBackground(new Color(0.0f, 1.0f, 1.0f, 0.25f), new Color(0.0f, 0.0f, 1.0f, 0.5f)));
//		this.getAppearance().setPadding(new Spacing(10, 10));
//
//		player1 = MyGameMenuButton.derive( "player1", listener );
//
//		player2 = MyGameMenuButton.derive( "player2", listener );
//		actions.put("player2", new ButtonAction() {
//			public void run() {
//				buildTowPlayersMenu();
//			}
//		});
//		
//		server = MyGameMenuButton.derive( "server", listener );
//		client = MyGameMenuButton.derive( "client", listener );
//
//		options = MyGameMenuButton.derive( "options", listener );
//		actions.put("options", new ButtonAction() {
//			public void run() {
//				GameMenu.this.buildOptionsMenu();
//			}
//		});
//
//		sound = MyGameMenuButton.derive( "sound", listener );
//		actions.put("sound", new ButtonAction() {
//			public void run() {
//			}
//		});
//
//		graphics = MyGameMenuButton.derive( "graphics", listener );
//		actions.put("graphics", new ButtonAction() {
//			public void run() {
//			}
//		});
//
//		network = MyGameMenuButton.derive( "network", listener );
//		actions.put("network", new ButtonAction() {
//			public void run() {
//			}
//		});
//
//		back = MyGameMenuButton.derive( "back", listener );
//		actions.put("back", new ButtonAction() {
//			public void run() {
//				GameMenu.this.buildMainMenu();
//			}
//		});
//
//		quit = MyGameMenuButton.derive( "quit", listener );
//		actions.put("quit", new ButtonAction() {
//			public void run() {
//				GameMenu.this.getDisplay().removeWidget(GameMenu.this);
//				System.exit(0);
//			}
//		});
	}

	private void buildMainMenu() {
		this.removeAll();
		this.add(player1);
		this.add(player2);
		this.add(options);
		this.add(quit);
//		this.pack();
//		StaticLayout.center(this, this.getDisplay());
	}

	private void buildTowPlayersMenu() {
		this.removeAll();
		this.add(server);
		this.add(client);
		this.add(back);
//		this.pack();
//		StaticLayout.center(this, this.getDisplay());
	}
	
	private void buildOptionsMenu() {
		this.removeAll();
		this.add(graphics);
		this.add(sound);
		this.add(network);
		this.add(back);
//		this.pack();
//		StaticLayout.center(this, this.getDisplay());
	}

//	/** {@inheritDoc} */
//	@Override
//	public void addedToWidgetTree() {
//		super.addedToWidgetTree();
//		buildMainMenu();
//	}
}
