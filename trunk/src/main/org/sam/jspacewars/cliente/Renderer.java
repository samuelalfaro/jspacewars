/* 
 * Renderer.java
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
package org.sam.jspacewars.cliente;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import org.sam.elementos.Modificador;
import org.sam.jogl.Instancia3D;
import org.sam.jogl.MatrixSingleton;
import org.sam.jogl.fondos.Fondo;

class Renderer implements GLEventListener {

	private final transient GLU glu = new GLU();
	private transient long tAnterior, tActual;

	private final transient Fondo fondo;
	private final transient ClientData data;
	private final transient MarcoDeIndicadores marco;
	
	Renderer( Fondo fondo, ClientData data, MarcoDeIndicadores marco) {
		this.fondo = fondo;
		this.data = data;
		this.marco = marco;
	}

	private boolean iniciado = false;
	
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
		iniciado = true;
	}

	public void display(GLAutoDrawable drawable) {
		if(!iniciado){
			reshape(drawable, 0, 0, drawable.getWidth(), drawable.getHeight());
			init(drawable);
		}
		
		tAnterior = tActual;
		tActual = System.nanoTime();
		float incT = (float) (tActual - tAnterior) / 1000000000;
		
		GL gl = drawable.getGL();
		marco.setViewportAreaInterna(gl);

		fondo.getModificador().modificar(incT);
		fondo.draw(gl);

		gl.glMatrixMode(GL.GL_MODELVIEW);
		glu.gluLookAt(0.0, 0.0, 11, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);
		MatrixSingleton.loadModelViewMatrix();

		for( Modificador modificador: data.modificadores )
			modificador.modificar(incT);

		gl.glDepthMask(true);
		for( Instancia3D elemento: data.elementos )
			elemento.draw(gl);
		
		/*
		gl.glDisable(GL.GL_TEXTURE_2D);
		gl.glBegin(GL.GL_LINE_STRIP);
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		float ratio = (30*4.0f/32) / (3.0f - 4*4.0f/32);
		float h = 3.26f;
		float w = ratio * h;
		gl.glVertex2f(-w,-h);
		gl.glVertex2f( w,-h);
		gl.glVertex2f( w, h);
		gl.glVertex2f(-w, h);
		gl.glVertex2f(-w,-h);
		gl.glEnd();
		gl.glEnable(GL.GL_TEXTURE_2D);
		//*/
		marco.update(data);
		marco.draw(gl);
		gl.glFlush();
	}

	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL gl = drawable.getGL();
		gl.glViewport(0, 0, width, height);
		marco.setBounds( 0, 0, width, height );

		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		double near = 0.5;
		double far = 240.0;
		double a1 = 35.0; // angulo en grados
		double a2 = a1/360*Math.PI; // mitad del angulo en radianes
		double d  = near/Math.sqrt((1/Math.pow(Math.sin(a2), 2))-1);

		// Formato 4/3 centrado, panorámico a la derecha en caso contrario.
		float ratio_4_3 = marco.getWidthAreaInterna(4, 3)/marco.getHeightAreaInterna(4, 3);
		float aWidth = marco.getWidthAreaInterna( width, height );
		float aHeight =marco.getHeightAreaInterna( width, height );
		
		gl.glFrustum(-ratio_4_3 * d, ((2.0 * aWidth) / aHeight - ratio_4_3) * d, -d, d, near, far);
		
		MatrixSingleton.loadProjectionMatrix();
		gl.glMatrixMode(GL.GL_MODELVIEW);
		fondo.setProporcionesPantalla( aWidth / aHeight );
	}
}
