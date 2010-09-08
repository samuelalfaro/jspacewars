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

import org.fenggui.Container;
import org.fenggui.decorator.background.GradientBackground;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.layout.RowLayout;
import org.fenggui.layout.StaticLayout;
import org.fenggui.util.Color;
import org.fenggui.util.Spacing;

public class GameMenu extends Container {

	private static class MyListener implements IButtonPressedListener{
		final Map<String,ButtonAction> actions;
		
		MyListener(Map<String,ButtonAction> actions){
			this.actions = actions;
		}
		
		/* (non-Javadoc)
		 * @see org.fenggui.event.IButtonPressedListener#buttonPressed(org.fenggui.event.ButtonPressedEvent)
		 */
		@Override
		public void buttonPressed(ButtonPressedEvent e) {
			actions.get(((MyGameMenuButton)e.getSource()).getName() ).run();
		}
		
	}

	private final MyGameMenuButton player1, player2, server, client, options, quit;
	private final MyGameMenuButton sound, graphics, network, back;
	
	public GameMenu(Map<String,ButtonAction> actions) {
		MyListener listener = new MyListener(actions);

		this.setLayoutManager(new RowLayout(false));
		this.getAppearance().add(
				new GradientBackground(new Color(0.0f, 1.0f, 1.0f, 0.25f), new Color(0.0f, 0.0f, 1.0f, 0.5f)));
		this.getAppearance().setPadding(new Spacing(10, 10));

		player1 = MyGameMenuButton.derive( "player1", listener );

		player2 = MyGameMenuButton.derive( "player2", listener );
		actions.put("player2", new ButtonAction() {
			public void run() {
				buildTowPlayersMenu();
			}
		});
		
		server = MyGameMenuButton.derive( "server", listener );
		actions.put("server", new ButtonAction() {
			public void run() {
			}
		});
		
		client = MyGameMenuButton.derive( "client", listener );
		actions.put("server", new ButtonAction() {
			public void run() {
			}
		});

		options = MyGameMenuButton.derive( "options", listener );
		actions.put("options", new ButtonAction() {
			public void run() {
				GameMenu.this.buildOptionsMenu();
			}
		});

		sound = MyGameMenuButton.derive( "sound", listener );
		actions.put("sound", new ButtonAction() {
			public void run() {
			}
		});

		graphics = MyGameMenuButton.derive( "graphics", listener );
		actions.put("graphics", new ButtonAction() {
			public void run() {
			}
		});

		network = MyGameMenuButton.derive( "network", listener );
		actions.put("network", new ButtonAction() {
			public void run() {
			}
		});

		back = MyGameMenuButton.derive( "back", listener );
		actions.put("back", new ButtonAction() {
			public void run() {
				GameMenu.this.buildMainMenu();
			}
		});

		quit = MyGameMenuButton.derive( "quit", listener );
		actions.put("server", new ButtonAction() {
			public void run() {
				GameMenu.this.getDisplay().removeWidget(GameMenu.this);
				System.exit(0);
			}
		});
	}

	private void buildMainMenu() {
		this.removeAllWidgets();
		this.addWidget(player1);
		this.addWidget(player2);
		this.addWidget(options);
		this.addWidget(quit);
		this.pack();
		StaticLayout.center(this, this.getDisplay());
	}

	private void buildTowPlayersMenu() {
		this.removeAllWidgets();
		this.addWidget(server);
		this.addWidget(client);
		this.addWidget(back);
		this.pack();
		StaticLayout.center(this, this.getDisplay());
	}
	
	private void buildOptionsMenu() {
		this.removeAllWidgets();
		this.addWidget(graphics);
		this.addWidget(sound);
		this.addWidget(network);
		this.addWidget(back);
		this.pack();
		StaticLayout.center(this, this.getDisplay());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fenggui.Container#addedToWidgetTree()
	 */
	@Override
	public void addedToWidgetTree() {
		super.addedToWidgetTree();
		buildMainMenu();
	}
}
