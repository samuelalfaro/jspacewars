/* 
 * Prueba020_FondoCieloEstrellado.java
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
package pruebas.jogl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import org.sam.jogl.Apariencia;
import org.sam.jogl.AtributosTextura;
import org.sam.jogl.AtributosTransparencia;
import org.sam.jogl.MatrixSingleton;
import org.sam.jogl.Objeto3D;
import org.sam.jogl.Textura;
import org.sam.jogl.particulas.Emisor;
import org.sam.jogl.particulas.FactoriaDeParticulas;
import org.sam.jogl.particulas.Particulas;
import org.sam.util.Imagen;

import pruebas.jogl.generators.HelixGenerator;

import com.jogamp.opengl.util.Animator;

public class Prueba020_FondoCieloEstrellado{

	static class Renderer implements GLEventListener{

		private GLU glu;
		private OrbitBehavior orbitBehavior;

		private Apariencia apFondo;
		private transient Particulas estrellas[] = new Particulas[20];

		private Objeto3D helix;

		private transient float proporcionesFondo, proporcionesPantalla;
		private transient long tAnterior, tActual;

		Renderer(){}

		public void init( GLAutoDrawable drawable ){
			GL2 gl = drawable.getGL().getGL2();
			glu = new GLU();
			orbitBehavior = new OrbitBehavior();
			orbitBehavior.setEyePos( 0.0f, 0.0f, -22.0f );
			orbitBehavior.setTargetPos( 0.0f, 0.0f, 0.0f );
			orbitBehavior.addMouseListeners( (GLCanvas)drawable );

			gl.glClearColor( 0.0f, 0.25f, 0.25f, 0.0f );

			BufferedImage img = Imagen.cargarToBufferedImage( "resources/texturas/cielo512.jpg" );
			proporcionesFondo = img.getHeight( null ) / img.getWidth( null );
			apFondo = new Apariencia();

			apFondo.setTextura( new Textura( gl, Textura.Format.RGB, img, true ) );
			apFondo.setAtributosTextura( new AtributosTextura() );
			apFondo.getAtributosTextura().setMode( AtributosTextura.Mode.REPLACE );

			Apariencia apEstrellas = new Apariencia();
			apEstrellas.setTextura( new Textura( gl, Textura.Format.ALPHA, Imagen
					.cargarToBufferedImage( "resources/texturas/spark.jpg" ), true ) );
			apEstrellas.setAtributosTransparencia( new AtributosTransparencia( AtributosTransparencia.Equation.ADD,
					AtributosTransparencia.SrcFunc.SRC_ALPHA, AtributosTransparencia.DstFunc.ONE_MINUS_SRC_ALPHA ) );

			Matrix4f tEmisor = new Matrix4f();
			tEmisor.setIdentity();
			tEmisor.rotZ( (float)Math.PI );
			tEmisor.setTranslation( new Vector3f( 2.05f, 0.5f, 0.0f ) );
			Emisor emisor = new Emisor.Cache(  new Emisor.Transformador( new Emisor.Lineal( 1.0f, 0.0f ), tEmisor ), 1024 );

			FactoriaDeParticulas.setOptimizedFor2D( true );
			for( int i = 0, len = estrellas.length; i < len; i++ ){
				estrellas[i] = FactoriaDeParticulas
						.createParticulas( (int)( Math.pow( 8 * ( len - i ) / len, 2 ) + 1 ) );
				estrellas[i].setEmisor( emisor );
				estrellas[i].setEmision( Particulas.Emision.CONTINUA );
				estrellas[i].setRangoDeEmision( 1.0f );
				// estrellas[i].setRadio(4.0f + (12.0f * (i+1) )/len);
				estrellas[i].setRadio( 0.004f + ( 0.012f * ( i + 1 ) ) / len );
				float vel = 0.1f * i + 0.05f;
				float tVida = 2.05f / ( vel * 0.95f );
				estrellas[i].setTiempoVida( tVida );
				estrellas[i].setVelocidad( vel, vel * 0.05f, false );
				// estrellas[i].setVelocidad(vel);
				// estrellas[i].setVelocidadGiro(360.0f,90.0f,true);
				estrellas[i].setGiroInicial( 0, 180, true );
				estrellas[i].setColor( 
						0.65f + ( 0.35f * ( i + 1 ) ) / len,
						0.35f + ( 0.65f * ( i + 1 ) ) / len,
						0.85f + ( 0.15f * ( i + 1 ) ) / len,
						1.0f
				);
				// estrellas[i].setPertubacionColor(0.25f, false, true);
				estrellas[i].setApariencia( apEstrellas );
				estrellas[i].reset();
				estrellas[i].getModificador().modificar( tVida );
			}
			FactoriaDeParticulas.setOptimizedFor2D( false );

			helix = HelixGenerator.generate( gl, 1.2f, 3.0f, 6 );

			gl.glEnable( GL.GL_DEPTH_TEST );
			gl.glDepthFunc( GL.GL_LESS );

			gl.glEnable( GLLightingFunc.GL_LIGHT0 );
			gl.glLightfv( GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_POSITION,
					new float[] { 0.0f, 0.0f, 10.0f, 1.0f }, 0 );
			gl.glLightfv( GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_DIFFUSE,
					new float[] { 0.9f, 1.0f, 1.0f, 1.0f }, 0 );
			gl.glLightfv( GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_SPECULAR,
					new float[] { 1.0f, 1.0f, 0.9f, 1.0f }, 0 );
			// gl.glEnable(GL.GL_CULL_FACE);
			tActual = System.nanoTime();
		}

		public void display( GLAutoDrawable drawable ){
			tAnterior = tActual;
			tActual = System.nanoTime();
			float incT = ( tActual - tAnterior ) / 1000000000.0f;

			GL2 gl = drawable.getGL().getGL2();

			gl.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );
			gl.glLoadIdentity();
			MatrixSingleton.loadModelViewMatrix();

			gl.glMatrixMode( GLMatrixFunc.GL_PROJECTION );
			gl.glPushMatrix();
			gl.glLoadIdentity();
			gl.glOrtho( 0.0, proporcionesPantalla, 0.0, 1.0, 0, 1 );
			MatrixSingleton.loadProjectionMatrix();

			apFondo.usar( gl );
			gl.glClear( GL.GL_DEPTH_BUFFER_BIT );

			float s1 = 0.75f;
			float s2 = proporcionesPantalla * proporcionesFondo + s1;

			gl.glDepthMask( false );
			gl.glBegin( GL2.GL_QUADS );
			gl.glTexCoord2f( s1, 0 );
			gl.glVertex3f( 0, 0, 0 );
			gl.glTexCoord2f( s2, 0 );
			gl.glVertex3f( proporcionesPantalla, 0, 0 );
			gl.glTexCoord2f( s2, 1 );
			gl.glVertex3f( proporcionesPantalla, 1, 0 );
			gl.glTexCoord2f( s1, 1 );
			gl.glVertex3f( 0, 1, 0 );
			gl.glEnd();
			gl.glDepthMask( true );

			for( Particulas p: estrellas ){
				p.getModificador().modificar( incT );
				p.draw( gl );
			}

			gl.glMatrixMode( GLMatrixFunc.GL_PROJECTION );
			gl.glPopMatrix();

			gl.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );

			orbitBehavior.setLookAt( glu );

			helix.draw( gl );

			//gl.glFlush();
		}

		public void reshape( GLAutoDrawable drawable, int x, int y, int w, int h ){
			GL2 gl = drawable.getGL().getGL2();
			gl.glViewport( 0, 0, w, h );
			proporcionesPantalla = (float)w / h;
			gl.glMatrixMode( GLMatrixFunc.GL_PROJECTION );
			gl.glLoadIdentity();
			double near = 0.01;
			double far = 100.0;
			double a1 = 45.0; // angulo en grados
			double a2 = a1 / 360 * Math.PI; // mitad del angulo en radianes
			double d = near / Math.sqrt( ( 1 / Math.pow( Math.sin( a2 ), 2 ) ) - 1 );

			// formato panoramico Horizontal/Vertical centrado
			double ratio = (double)w / h;
			if( ratio < 1 )
				gl.glFrustum( -d, d, -d / ratio, d / ratio, near, far );
			else
				gl.glFrustum( -d * ratio, d * ratio, -d, d, near, far );

			gl.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );
		}

		/* (non-Javadoc)
		 * @see javax.media.opengl.GLEventListener#dispose(javax.media.opengl.GLAutoDrawable)
		 */
		@Override
		public void dispose( GLAutoDrawable drawable ){
		}
	}

	public static void main( String[] args ){

		JFrame frame = new JFrame( "Prueba Fondo Estrellas" );
		frame.getContentPane().setBackground( Color.BLACK );
		frame.getContentPane().setPreferredSize( new Dimension( 640, 480 ) );
		frame.pack();
		frame.setLocationRelativeTo( null ); // center
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

		GLCanvas canvas = new GLCanvas( new GLCapabilities( GLProfile.get( GLProfile.GL2 ) ) );
		canvas.addGLEventListener( new Renderer() );

		frame.getContentPane().add( canvas );

		Animator animator = new Animator();
		// animator.setRunAsFastAsPossible(true);
		animator.add( canvas );

		frame.setVisible( true );

		animator.add( canvas );
		canvas.requestFocusInWindow();
		animator.start();
	}
}
