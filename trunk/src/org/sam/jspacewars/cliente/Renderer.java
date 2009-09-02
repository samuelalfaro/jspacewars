/* 
 * Renderer.java
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
package org.sam.jspacewars.cliente;

import java.util.Collection;
import java.util.List;

import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;

import org.sam.elementos.Modificador;
import org.sam.jogl.Instancia3D;
import org.sam.jogl.ObjetosOrientables;
import org.sam.jogl.fondos.Fondo;

/**
 * 
 * @author Samuel Alfaro
 */
class Renderer implements GLEventListener {

	private final transient GLU glu = new GLU();
	private transient long tAnterior, tActual;

	private final transient Fondo fondo;
	private final transient List<Instancia3D> elementos;
	private final transient Collection<Modificador> modificadores;

	Renderer(Fondo fondo, List<Instancia3D> elementos, Collection<Modificador> modificadores) {
		this.fondo = fondo;
		this.elementos = elementos;
		this.modificadores = modificadores;
	}

	public void init(GLAutoDrawable drawable) {
		GL gl = drawable.getGL();

		gl.glShadeModel(GL.GL_SMOOTH);

		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LESS);

		gl.glEnable(GL.GL_LIGHT0);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, new float[] { 0.5f, 1.0f, 1.0f, 0.0f }, 0);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, new float[] { 0.4f, 0.6f, 0.6f, 1.0f }, 0);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR, new float[] { 0.0f, 0.0f, 0.0f, 1.0f }, 0);

		gl.glEnable(GL.GL_LIGHT1);
		gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, new float[] { -0.5f, -1.0f, 1.0f, 0.0f }, 0);
		gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, new float[] { 0.6f, 0.4f, 0.4f, 1.0f }, 0);
		gl.glLightfv(GL.GL_LIGHT1, GL.GL_SPECULAR, new float[] { 0.0f, 0.0f, 0.0f, 1.0f }, 0);

		gl.glEnable(GL.GL_LIGHT2);
		gl.glLightfv(GL.GL_LIGHT2, GL.GL_POSITION, new float[] { 0.0f, 0.0f, 1.0f, 0.0f }, 0);
		gl.glLightfv(GL.GL_LIGHT2, GL.GL_DIFFUSE, new float[] { 0.0f, 0.0f, 0.0f, 1.0f }, 0);
		gl.glLightfv(GL.GL_LIGHT2, GL.GL_SPECULAR, new float[] { 0.95f, 0.95f, 0.95f, 1.0f }, 0);

		gl.glEnable(GL.GL_LIGHTING);
		gl.glEnable(GL.GL_CULL_FACE);

		tActual = System.nanoTime();
	}

	public void display(GLAutoDrawable drawable) {

		tAnterior = tActual;
		tActual = System.nanoTime();
		float incT = (float) (tActual - tAnterior) / 1000000000;

		GL gl = drawable.getGL();

		fondo.getModificador().modificar(incT);
		fondo.draw(gl);

		gl.glMatrixMode(GL.GL_MODELVIEW);
		glu.gluLookAt(0.0, 0.0, 11, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);
		ObjetosOrientables.loadModelViewMatrix();

		for( Modificador modificador: modificadores )
			modificador.modificar(incT);

		gl.glDepthMask(true);
		for( Instancia3D elemento: elementos )
			elemento.draw(gl);

		gl.glFlush();
	}

	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
		GL gl = drawable.getGL();
		fondo.setProporcionesPantalla((float) w / h);
		gl.glViewport(0, 0, w, h);
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		double near = 0.5;
		double far = 240.0;
		double a1 = 35.0; // angulo en grados
		double a2 = a1 / 360 * Math.PI; // mitad del angulo en radianes
		double d = near / Math.sqrt((1 / Math.pow(Math.sin(a2), 2)) - 1);
		// gl.glFrustum(-d,((2.0*w)/h -1.0)*d, -d, d, near, far);
		double anchovisible = 4.5 / 3.0;
		gl.glFrustum(-anchovisible * d, ((2.0 * w) / h - anchovisible) * d, -d, d, near, far);
		ObjetosOrientables.loadProjectionMatrix();
		gl.glMatrixMode(GL.GL_MODELVIEW);
	}
}
