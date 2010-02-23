/* 
 * MyGlCanvas.java
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
package org.sam.jogl;

import java.awt.GraphicsDevice;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.*;

/**
 * @author Samuel Alfaro
 * 
 */
@SuppressWarnings("serial")
public class MyGlCanvas extends GLCanvas {

	final List<GLEventListener> listeners;

	public MyGlCanvas() {
		this(null);
	}

	public MyGlCanvas(GLCapabilities capabilities) {
		this(capabilities, null, null, null);
	}

	public MyGlCanvas(GLCapabilities capabilities, GLCapabilitiesChooser chooser,
			GLContext shareWith, GraphicsDevice device) {
		super(capabilities, chooser, shareWith, device);
		listeners = new ArrayList<GLEventListener>(5);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seejavax.media.opengl.GLCanvas#addGLEventListener(javax.media.opengl.
	 * GLEventListener)
	 */
	@Override
	public void addGLEventListener(GLEventListener listener) {
		listeners.add(listener);
		super.addGLEventListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.media.opengl.GLCanvas#removeGLEventListener(javax.media.opengl.
	 * GLEventListener)
	 */
	@Override
	public void removeGLEventListener(GLEventListener listener) {
		listeners.remove(listener);
		super.removeGLEventListener(listener);
	}

	public void removeAllGLEventListeners() {
		for( GLEventListener listener: listeners )
			super.removeGLEventListener(listener);
		listeners.clear();
	}
}