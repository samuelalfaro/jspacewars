/* 
 * GLEventListenerBackgroundRenderer.java
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

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

import org.sam.jogl.fondos.Fondo;

/**
 * Implementación de un {@code GLEventListener} que renderiza 
 */
class GLEventListenerBackgroundRenderer implements GLEventListener {

	private final transient Fondo fondo;
	private transient long tAnterior, tActual;

	GLEventListenerBackgroundRenderer(Fondo fondo) {
		this.fondo = fondo;
		tActual = System.nanoTime();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.media.opengl.GLEventListener#init(javax.media.opengl.GLAutoDrawable
	 * )
	 */
	public void init(GLAutoDrawable drawable) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable
	 * )
	 */
	public void display(GLAutoDrawable drawable) {
		tAnterior = tActual;
		tActual = System.nanoTime();
		float incT = (float) (tActual - tAnterior) / 1000000000;
		GL2 gl = drawable.getGL().getGL2();

		fondo.getModificador().modificar(incT);
		fondo.draw(gl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.media.opengl.GLEventListener#reshape(javax.media.opengl.GLAutoDrawable
	 * , int, int, int, int)
	 */
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl = drawable.getGL().getGL2();
		fondo.setProporcionesPantalla((float) width / height);
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();
		double near = 0.5;
		double far = 240.0;
		double a1 = 35.0; // angulo en grados
		double a2 = a1 / 360 * Math.PI; // mitad del angulo en radianes
		double d = near / Math.sqrt((1 / Math.pow(Math.sin(a2), 2)) - 1);
		// gl.glFrustum(-d,((2.0*w)/h -1.0)*d, -d, d, near, far);
		double anchovisible = 4.5 / 3.0;
		gl.glFrustum(-anchovisible * d, ((2.0 * width) / height - anchovisible) * d, -d, d, near, far);
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
	}

	/* (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#dispose(javax.media.opengl.GLAutoDrawable)
	 */
	@Override
	public void dispose( GLAutoDrawable drawable ){
	}
}
