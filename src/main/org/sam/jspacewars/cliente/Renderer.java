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

import java.awt.geom.Rectangle2D;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
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
	private final transient float ratio_4_3;
	
	Renderer( Fondo fondo, ClientData data, MarcoDeIndicadores marco ){
		this.fondo = fondo;
		this.data = data;
		this.marco = marco;
		
		Rectangle2D.Float a43 = new Rectangle2D.Float();
		marco.calcularAreaInterna( a43, 4, 3 );
		ratio_4_3 = a43.width / a43.height;
	}

	private boolean iniciado = false;
	
	public void init( GLAutoDrawable drawable ){
		GL2 gl = drawable.getGL().getGL2();

		gl.glShadeModel( GLLightingFunc.GL_SMOOTH );

		gl.glEnable( GL.GL_DEPTH_TEST );
		gl.glDepthFunc( GL.GL_LESS );

		gl.glEnable( GLLightingFunc.GL_LIGHT0 );
		gl.glLightfv( GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_POSITION, new float[] { 0.5f, 1.0f, 1.0f, 0.0f }, 0 );
		gl.glLightfv( GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_DIFFUSE,  new float[] { 0.4f, 0.6f, 0.6f, 1.0f }, 0 );
		gl.glLightfv( GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_SPECULAR, new float[] { 0.0f, 0.0f, 0.0f, 1.0f }, 0 );

		gl.glEnable( GLLightingFunc.GL_LIGHT1 );
		gl.glLightfv( GLLightingFunc.GL_LIGHT1, GLLightingFunc.GL_POSITION, new float[] { -0.5f, -1.0f, 1.0f, 0.0f }, 0 );
		gl.glLightfv( GLLightingFunc.GL_LIGHT1, GLLightingFunc.GL_DIFFUSE,  new float[] { 0.6f, 0.4f, 0.4f, 1.0f }, 0 );
		gl.glLightfv( GLLightingFunc.GL_LIGHT1, GLLightingFunc.GL_SPECULAR, new float[] { 0.0f, 0.0f, 0.0f, 1.0f }, 0 );

		gl.glEnable( GLLightingFunc.GL_LIGHT2 );
		gl.glLightfv( GLLightingFunc.GL_LIGHT2, GLLightingFunc.GL_POSITION, new float[] { 0.0f, 0.0f, 1.0f, 0.0f }, 0 );
		gl.glLightfv( GLLightingFunc.GL_LIGHT2, GLLightingFunc.GL_DIFFUSE,  new float[] { 0.0f, 0.0f, 0.0f, 1.0f }, 0 );
		gl.glLightfv( GLLightingFunc.GL_LIGHT2, GLLightingFunc.GL_SPECULAR, new float[] { 0.95f, 0.95f, 0.95f, 1.0f }, 0 );

		gl.glEnable( GLLightingFunc.GL_LIGHTING );
		gl.glEnable( GL.GL_CULL_FACE );

		tActual = System.nanoTime();
		iniciado = true;
	}

	public void display( GLAutoDrawable drawable ){
		if( !iniciado ){
			reshape( drawable, 0, 0, drawable.getWidth(), drawable.getHeight() );
			init( drawable );
		}

		tAnterior = tActual;
		tActual = System.nanoTime();
		float incT = (float)( tActual - tAnterior ) / 1000000000;
		
		fondo.getModificador().modificar( incT );
		for( Modificador modificador: data.modificadores )
			modificador.modificar( incT );
		marco.update( data );
		
		GL2 gl = drawable.getGL().getGL2();

		fondo.draw( gl );
		
		gl.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );
		glu.gluLookAt( 0.0, 0.0, 11, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0 );
		MatrixSingleton.loadModelViewMatrix();
		
		gl.glDepthMask( true );
		for( Instancia3D elemento: data.elementos )
			elemento.draw( gl );
		marco.draw( gl );
		
		gl.glFlush();
	}

	private final transient Rectangle2D.Float aWH = new Rectangle2D.Float();
	
	public void reshape( GLAutoDrawable drawable, int x, int y, int width, int height ){
		
		marco.setBounds( 0, 0, width, height );
		marco.calcularAreaInterna( aWH, width, height );
		fondo.setBounds( aWH.x, aWH.y, aWH.width, aWH.height );
		
		GL2 gl = drawable.getGL().getGL2();
		gl.glMatrixMode( GLMatrixFunc.GL_PROJECTION );
		gl.glLoadIdentity();
		// Formato 4/3 centrado, panorámico a la derecha en caso contrario.
		double near = 0.5;
		double far = 240.0;
		double a1 = 35.0; // angulo en grados
		double a2 = a1 / 360 * Math.PI; // mitad del angulo en radianes
		double d = near / Math.sqrt( ( 1 / Math.pow( Math.sin( a2 ), 2 ) ) - 1 );
		gl.glFrustum( -ratio_4_3 * d, ( ( 2.0 * aWH.width ) / aWH.height - ratio_4_3 ) * d, -d, d, near, far );
		
		MatrixSingleton.loadProjectionMatrix();

		gl.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );
	}

	/* (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#dispose(javax.media.opengl.GLAutoDrawable)
	 */
	@Override
	public void dispose( GLAutoDrawable drawable ){
	}
}
