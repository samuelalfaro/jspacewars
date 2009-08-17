/*
 * FengGUI - Java GUIs in OpenGL (http://www.fenggui.org)
 * 
 * Copyright (c) 2005-2009 FengGUI Project
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details:
 * http://www.gnu.org/copyleft/lesser.html#TOC3
 * 
 */
package org.sam.gui;

import java.io.IOException;

import org.fenggui.Container;
import org.fenggui.Display;
import org.fenggui.binding.render.Binding;
import org.fenggui.binding.render.Pixmap;
//import org.fenggui.composite.MessageWindow;
import org.fenggui.decorator.background.GradientBackground;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.layout.RowLayout;
import org.fenggui.layout.StaticLayout;
import org.fenggui.util.Color;
import org.fenggui.util.Spacing;

public class GameMenu extends  Container{

	private final MyGameMenuButton play, credits, options, quit;
	private final MyGameMenuButton sound, graphics, network, back;
	
	public GameMenu( final Display display ) {
		
		this.setLayoutManager( new RowLayout(false) );
		this.getAppearance().add(
				new GradientBackground( new Color(0.0f, 1.0f, 1.0f, 0.25f), new Color(0.0f, 0.0f, 1.0f, 0.5f) ) );
		this.getAppearance().setPadding( new Spacing(10, 10) );
		
		Pixmap defaultState = null;
		Pixmap hoverState = null;
		Pixmap pressedState = null;
		
		try{
			defaultState = new Pixmap(Binding.getInstance().getTexture("resources/img/botones/boton0.jpg"));
			hoverState = new Pixmap(Binding.getInstance().getTexture("resources/img/botones/boton1.jpg"));
			pressedState = new Pixmap(Binding.getInstance().getTexture("resources/img/botones/boton2.jpg"));
		}catch( IOException ignorada ){
		}

		play = new MyGameMenuButton("Play", defaultState, hoverState, pressedState);
		play.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(ButtonPressedEvent e) {
//				MessageWindow mw = new MessageWindow("Nothing to play. Just a demo.");
//				mw.pack();
//				display.addWidget(mw);
//				StaticLayout.center(mw, display);
			}
		});

		options = new MyGameMenuButton("Options", defaultState, hoverState, pressedState);
		options.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(ButtonPressedEvent e) {
				buildOptionsMenu( display );
			}
		});

		sound = new MyGameMenuButton("Sound", defaultState, hoverState, pressedState);

		graphics = new MyGameMenuButton("Graphics ...", defaultState, hoverState, pressedState);
		network = new MyGameMenuButton("Network", defaultState, hoverState, pressedState);

		back = new MyGameMenuButton("Back", defaultState, hoverState, pressedState);
		back.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(ButtonPressedEvent e) {
				buildMainMenu( display );
			}
		});

		credits = new MyGameMenuButton("Credits", defaultState, hoverState, pressedState);
		credits.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(ButtonPressedEvent e) {
//				MessageWindow mw = new MessageWindow("We dont take credit for FengGUI :)");
//				mw.pack();
//				display.addWidget(mw);
//				StaticLayout.center(mw, display);
			}
		});

		quit = new MyGameMenuButton("Quit", defaultState, hoverState, pressedState);
		quit.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(ButtonPressedEvent e) {
				display.removeWidget( GameMenu.this );
			}
		});

		buildMainMenu( display );
	}

	private void buildMainMenu( final Display display ) {
		this.removeAllWidgets();
		this.addWidget(play);
		this.addWidget(options);
		this.addWidget(credits);
		this.addWidget(quit);
		this.pack();
		StaticLayout.center( this, display );
	}

	private void buildOptionsMenu( final Display display ) {
		this.removeAllWidgets();
		this.addWidget(graphics);
		this.addWidget(sound);
		this.addWidget(network);
		this.addWidget(back);
		this.pack();
		StaticLayout.center( this, display );
	}
}
