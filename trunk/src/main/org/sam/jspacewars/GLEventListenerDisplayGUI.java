/* 
 * GLEventListenerDisplayGUI.java
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

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

/**
 * 
 * @author Samuel Alfaro
 */
class GLEventListenerDisplayGUI implements GLEventListener{

	private final transient GameMenu gameMenu;
	private transient boolean menu_isVisible;

	GLEventListenerDisplayGUI( Map<String, ButtonAction> actions ){
		this.gameMenu = new GameMenu( actions );
		menu_isVisible = false;
	}

	/* (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#init(javax.media.opengl.GLAutoDrawable)
	 */
	public void init( GLAutoDrawable drawable ){
	}

	/* (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable)
	 */
	public void display( GLAutoDrawable drawable ){
	}

	/* (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#reshape(javax.media.opengl.GLAutoDrawable, int, int, int, int)
	 */
	public void reshape( GLAutoDrawable drawable, int x, int y, int width, int height ){
	}

	/*
	 * (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#dispose(javax.media.opengl.GLAutoDrawable)
	 */
	@Override
	public void dispose( GLAutoDrawable drawable ){
	}

	public void showMenu(){
		if( !menu_isVisible ){
			menu_isVisible = true;
		}
	}

	public void hideMenu(){
		if( menu_isVisible ){
			menu_isVisible = false;
			//display.removeWidget(gameMenu);
		}
	}

}
