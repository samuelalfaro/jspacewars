/* 
 * GLEventListenerDisplayGUI.java
 * 
 * Copyright (c) 2009 Samuel Alfaro <samuelalfaro at gmail.com>. All rights reserved.
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
package org.sam.jspacewars;

import javax.media.opengl.*;

import org.fenggui.Display;
import org.fenggui.binding.render.jogl.*;

/**
 *
 * @author Samuel Alfaro
 */
class GLEventListenerDisplayGUI implements GLEventListener {

	private transient Display display;
	private final transient GameMenu gameMenu;

	GLEventListenerDisplayGUI(GameMenu gameMenu){
		this.gameMenu = gameMenu;
	}

	/* (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#init(javax.media.opengl.GLAutoDrawable)
	 */
	public void init(GLAutoDrawable drawable) {
		display = new Display(  new JOGLBinding((GLCanvas)drawable, drawable.getGL(), new JOGLOpenGL(drawable.getGL())) );
		//			try{
		//				FengGUI.setTheme( new XMLTheme("data/themes/QtCurve/QtCurve.xml") );
		//			}catch( IOException ignorada ){
		//			}catch( IXMLStreamableException ignorada ){
		//			}
		new EventBinding((GLCanvas)drawable, display);

		display.addWidget( gameMenu );
	}

	/* (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable)
	 */
	public void display(GLAutoDrawable drawable) {
		display.display();
	}

	/* (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#reshape(javax.media.opengl.GLAutoDrawable, int, int, int, int)
	 */
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
	}

	/* (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#displayChanged(javax.media.opengl.GLAutoDrawable, boolean, boolean)
	 */
	@Override
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
	}
}